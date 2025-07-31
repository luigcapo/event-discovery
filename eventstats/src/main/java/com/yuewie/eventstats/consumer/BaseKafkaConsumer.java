package com.yuewie.eventstats.consumer;

import com.yuewie.eventstats.service.technique.kafka.DuplicateMessageChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;

import java.util.Objects;

@Slf4j
public abstract class BaseKafkaConsumer<T> {

    private static final String ID_BUSINESS = "businessIdStats";
    private static final String ID_DELIVERY = "deliveryId";

    private final DuplicateMessageChecker duplicateChecker;

    public BaseKafkaConsumer(DuplicateMessageChecker duplicateChecker) {
        this.duplicateChecker = duplicateChecker;
    }


    /**
     * Méthode template qui orchestre le traitement.
     * Elle est 'final' pour que les sous-classes ne puissent pas modifier l'ordre des étapes.
     */
    protected final void executeProcessingFlow(T payload, String topic, int partition, long offset, Acknowledgment ack) {
        try {
            String deliveryId = buildDeliveryId(topic, partition, offset);
            String businessId = buildBusinessId(payload);
            // Étape 1 : Vérification pré-traitement (logique commune)
            if (!shouldProcessMessage(payload, deliveryId, businessId)) {
                log.warn("Message déjà traité ou invalide, il sera ignoré (offset: {}).", offset);
                ack.acknowledge(); // On acquitte pour ne pas le retraiter
                return;
            }

            // Étape 2 : Traitement métier (logique spécifique)
            handlePayload(payload, partition, offset);

            // Étape 3 : Finalisation et acquittement (logique commune)
            finalizeAndAcknowledge(payload, ack, deliveryId, businessId);

        } catch (Exception e) {
            log.error("Erreur critique durant le traitement du message (offset: {}). Le message ne sera pas acquitté.", offset, e);
            // On n'acquitte PAS en cas d'erreur pour permettre une nouvelle tentative
            // ou l'envoi vers une Dead Letter Topic (DLT).
        }
    }

    /**
     * Orchestre la vérification de l'idempotence à deux niveaux : transport puis métier.
     * <p>
     * Cette méthode agit comme un filtre :
     * <ol>
     * <li><b>Niveau 1 (Obligatoire) :</b> Elle vérifie d'abord l'idempotence de transport pour s'assurer
     * que le message n'a pas déjà été livré par Kafka.</li>
     * <li><b>Niveau 2 (Optionnel) :</b> Si la livraison est nouvelle, elle vérifie l'idempotence métier
     * pour s'assurer que l'action business elle-même n'a pas déjà été traitée.</li>
     * </ol>
     * La logique métier est optionnelle et ne s'active que si la méthode {@code buildBusinessId} retourne une clé non-nulle.
     *
     * @param payload L'objet de données (DTO) du message.
     * @param deliveryId L'id de transport construit à partir du topic, partition et offset.
     * @param businessId L'id fonctionnel créé en fonction des cas dans le but de recherché une indempotence métier. Peut etre {@code null} si l'idempotence métier n'est pas applicable.
     * @return {@code true} si le message est unique et doit être traité, {@code false} sinon.
     */
    protected boolean shouldProcessMessage(T payload, String deliveryId, @Nullable String businessId) {
        // --- Niveau 1 : Vérification de l'Idempotence de Transport (Obligatoire) ---
        if (duplicateChecker.isAlreadyProcessed(ID_DELIVERY, deliveryId)) {
            log.warn("Doublon de transport détecté. Le message a déjà été livré (clé: {}). Message ignoré.", deliveryId);
            return false;
        }

        // --- Niveau 2 : Vérification de l'Idempotence Métier (Optionnel) ---
        if (businessId != null) { // On ne fait la vérification que si une clé métier est fournie.
            if (duplicateChecker.isAlreadyProcessed(ID_BUSINESS, businessId)) {
                log.warn("Doublon métier détecté. L'événement a déjà été traité (clé: {}). Message ignoré.", businessId);
                return false;
            }
        }
        return true;
    }

    /**
     * Finalise le traitement en marquant le message comme traité et en acquittant.
     * Logique commune.
     */
    protected void finalizeAndAcknowledge(T payload, Acknowledgment ack, String deliveryId, @Nullable String businessId) {
        // Ici, la logique pour insérer l'ID du message dans Redis/SQL pour la détection de doublons
        duplicateChecker.ajouterAuSet(ID_DELIVERY, deliveryId);
        if(!Objects.isNull(businessId)){
            duplicateChecker.ajouterAuSet(ID_BUSINESS, businessId);
        }
        log.debug("Message finalisé et acquitté.");
        ack.acknowledge();
    }

    /**
     * Le CŒUR du traitement. Cette méthode est abstraite et DOIT être implémentée
     * par chaque consumer concret.
     */
    protected abstract void handlePayload(T payload, int partition, long offset);

    /**
     * Construit un identifiant unique pour une livraison de message spécifique (idempotence de transport).
     * <p>
     * Cette clé est utilisée pour se prémunir contre les re-livraisons techniques de Kafka (ex: après un redémarrage
     * du consommateur) en identifiant un message par sa position exacte dans le log Kafka.
     *
     * @param topic La partition du topic où le message a été reçu.
     * @param partition La partition où le message a été reçu.
     * @param offset L'offset du message dans la partition.
     * @return Une chaîne de caractères unique représentant la livraison, par exemple "mon-topic-0-12345".
     */
    protected final String buildDeliveryId(String topic, int partition, long offset) {
        return String.format("%s-%d-%d", topic, partition, offset);
    }

    /**
     * Construit un identifiant unique pour un événement métier (idempotence métier).
     * <p>
     * Cette clé est utilisée pour se prémunir contre le traitement de doublons logiques (ex: deux événements
     * envoyés pour la même action métier). L'implémentation est spécifique au traitement et se base
     * typiquement sur un ou plusieurs champs du payload.
     *
     * @param payload L'objet de données (DTO) contenant les informations de l'événement.
     * @return Une chaîne de caractères unique représentant l'action métier, ou {@code null} si l'idempotence
     * métier n'est pas applicable pour ce traitement.
     */
    protected @Nullable String buildBusinessId(T payload) {
        return null;
    }


}
