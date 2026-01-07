package com.yuewie.apievent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuewie.apievent.dto.AdresseRequestDto;
import com.yuewie.apievent.dto.EventCreateDto;
import com.yuewie.apievent.dto.LienEventAdresseRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Classe de tests d'intégration pour EventController utilisant Testcontainers.
 */
@Testcontainers // Active l'intégration de JUnit 5 avec Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Charge le contexte Spring Boot complet
@AutoConfigureMockMvc // Configure automatiquement MockMvc pour les tests
class EventControllerIntegrationTest {

    // Crée et gère un conteneur PostgreSQL pour la durée des tests de cette classe.
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc; // Utilitaire pour effectuer des requêtes HTTP vers le controller

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets Java en JSON

    private static Clock fixedClock;

    /**
     * Remplace dynamiquement les propriétés de la source de données (datasource)
     * pour que l'application se connecte à la base de données du conteneur Testcontainers
     * au lieu de celle configurée dans application.yml.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void setupClock() {
        Instant now = Instant.now();
        fixedClock = Clock.fixed(now, ZoneId.systemDefault());
    }

    @Test
    @DisplayName("POST /api/v1/events - Devrait créer un nouvel événement avec succès")
    void shouldCreateEvent_whenPostEvent() throws Exception {
        // Given: Un DTO pour créer un événement
        AdresseRequestDto adresseDto = AdresseRequestDto.builder()
                .numero("10")
                .rue("Rue de la Paix")
                .codePostal("75002")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto lienEventAdresseRequestDto = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresseDto)
                .build();

        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Lancement de Produit Tech");
        eventCreateDto.setDescription("Présentation du nouveau produit phare.");
        // Utilisation du format attendu par le DTO
        eventCreateDto.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(18));
        eventCreateDto.setEnd(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(22));
        eventCreateDto.setAdresses(Set.of(lienEventAdresseRequestDto));

        // When & Then: On exécute la requête POST et on vérifie les résultats
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isCreated()) // On attend un statut 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists()) // L'ID doit être généré
                .andExpect(jsonPath("$.name").value("Lancement de Produit Tech"))
                .andExpect(jsonPath("$.adressePrincipale.ville").value("Paris"));
    }

    @Test
    @DisplayName("GET /api/v1/events/search/querydsl - Devrait trouver un événement par son nom")
    void shouldFindEvent_whenSearchWithQueryDsl() throws Exception {
        // --- Étape 1: Créer un événement pour s'assurer qu'il y a des données à rechercher ---
        // Given: Un DTO pour créer l'événement de test
        AdresseRequestDto adresseDto = AdresseRequestDto.builder()
                .numero("5")
                .rue("Avenue Anatole France")
                .codePostal("75007")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto lienEventAdresseRequestDto = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresseDto)
                .build();

        EventCreateDto eventToCreate = new EventCreateDto();
        eventToCreate.setName("Concert sous la Tour Eiffel");
        eventToCreate.setDescription("Un concert en plein air inoubliable.");
        eventToCreate.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(18));
        eventToCreate.setEnd(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(22));
        eventToCreate.setAdresses(Set.of(lienEventAdresseRequestDto));

        // On le crée via l'API
        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventToCreate)));


        // --- Étape 2: Rechercher cet événement via l'endpoint QueryDSL ---
        // When & Then: On exécute la recherche et on vérifie les résultats
        mockMvc.perform(get("/api/v1/events/search/querydsl")
                        .param("name", "Concert sous la Tour Eiffel") // Le critère de recherche
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // On attend un statut 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray()) // Le résultat doit être un tableau
                .andExpect(jsonPath("$[0].name").value("Concert sous la Tour Eiffel")); // On vérifie que le bon événement a été trouvé
    }

    @Test
    @DisplayName("POST /api/v1/events/{id}/adresses - Devrait ajouter une adresse à un événement existant")
    void shouldAddAdresseToEvent() throws Exception {
        // --- Étape 1: Créer un événement ---
        AdresseRequestDto adresseInitiale = AdresseRequestDto.builder()
                .numero("10")
                .rue("Avenue des Champs-Élysées")
                .codePostal("75008")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto lienInitial = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresseInitiale)
                .build();

        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Soirée de Gala");
        eventCreateDto.setDescription("Une soirée élégante.");
        eventCreateDto.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(18));
        eventCreateDto.setEnd(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(22));
        eventCreateDto.setAdresses(Set.of(lienInitial));

        String createResponse = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extraire l'ID de l'événement créé
        Long eventId = objectMapper.readTree(createResponse).get("id").asLong();

        // --- Étape 2: Ajouter une nouvelle adresse à cet événement ---
        AdresseRequestDto nouvelleAdresse = AdresseRequestDto.builder()
                .numero("20")
                .rue("Rue de Rivoli")
                .codePostal("75004")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto nouveauLien = LienEventAdresseRequestDto.builder()
                .principal(false)
                .adresse(nouvelleAdresse)
                .build();

        // When & Then: Ajouter l'adresse
        mockMvc.perform(post("/api/v1/events/{id}/adresses", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nouveauLien)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Soirée de Gala"))
                .andExpect(jsonPath("$.adresses").isArray())
                .andExpect(jsonPath("$.adresses.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /api/v1/events/{id}/adresses/{adresseId} - Devrait retirer une adresse d'un événement")
    void shouldRemoveAdresseFromEvent() throws Exception {
        // --- Étape 1: Créer un événement avec 2 adresses ---
        AdresseRequestDto adresse1 = AdresseRequestDto.builder()
                .numero("15")
                .rue("Boulevard Saint-Germain")
                .codePostal("75005")
                .ville("Paris")
                .pays("France")
                .build();

        AdresseRequestDto adresse2 = AdresseRequestDto.builder()
                .numero("30")
                .rue("Rue du Faubourg Saint-Honoré")
                .codePostal("75008")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto lien1 = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresse1)
                .build();

        LienEventAdresseRequestDto lien2 = LienEventAdresseRequestDto.builder()
                .principal(false)
                .adresse(adresse2)
                .build();

        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Conférence Tech");
        eventCreateDto.setDescription("Conférence sur les nouvelles technologies.");
        eventCreateDto.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(18));
        eventCreateDto.setEnd(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.NANOS).plusHours(22));
        eventCreateDto.setAdresses(Set.of(lien1, lien2));

        String createResponse = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extraire l'ID de l'événement et l'ID de la première adresse
        Long eventId = objectMapper.readTree(createResponse).get("id").asLong();
        // Récupérer l'ID d'une adresse (peu importe laquelle car on utilise un Set)
        Long adresseIdToRemove = objectMapper.readTree(createResponse)
                .get("adresses").elements().next().get("adresse").get("id").asLong();

        // --- Étape 2: Retirer une adresse ---
        mockMvc.perform(delete("/api/v1/events/{id}/adresses/{adresseId}", eventId, adresseIdToRemove))
                .andExpect(status().isNoContent());

        // --- Étape 3: Vérifier que l'événement n'a plus qu'une seule adresse ---
        mockMvc.perform(get("/api/v1/events/{id}", eventId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.adresses").isArray())
                .andExpect(jsonPath("$.adresses.length()").value(1)); // Plus qu'une seule adresse
    }
}
