package com.yuewie.apievent.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.entity.LienEventAdresse;
import com.yuewie.apievent.entity.OutboxEvent;
import com.yuewie.apievent.helper.KafkaPayloadHelper;
import com.yuewie.apievent.helper.MessageHelper;
import com.yuewie.apievent.mapper.EventMapper;
import com.yuewie.apievent.mapper.LienEventAdresseMapper;
import com.yuewie.apievent.repository.*;
import com.yuewie.apievent.repository.impl.EventSpecifications;
import com.yuewie.apievent.service.EventService;
import com.yuewie.apievent.aop.log.Loggable;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@Loggable
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventJooqRepository eventJooqRepository;
    private final EventJpqlRepository eventJpqlRepository;
    private final EventSqlNativeRepository eventSqlNativeRepository;
    private final EventQueryDSLRepository   eventQueryDSLRepository;
    private final EventCriteriaApiRepository eventCriteriaApiRepository;
    private final KafkaPayloadHelper kafkaPayloadHelper;
    private final OutboxEventRepository outboxEventRepository;
    private final LienEventAdresseMapper lienEventAdresseMapper;
    private final AdresseRepository adresseRepository;
    private final LienEventAdresseRepository lienEventAdresseRepository;
    private final MessageHelper messageHelper;

    @Value("${app.kafka.topic.eventCreated}")
    private String eventCreatedTopic;

    @Value("${app.kafka.topic.eventCreatedImpaired}")
    private String eventCreatedImpairTopic;

    @Autowired
    public EventServiceImpl(EventMapper eventMapper, EventRepository eventRepository, EventJooqRepository eventJooqRepository,
                            EventJpqlRepository eventJpqlRepository, EventSqlNativeRepository eventSqlNativeRepository,
                            EventQueryDSLRepository eventQueryDSLRepository, EventCriteriaApiRepository eventCriteriaApiRepository,
                            KafkaPayloadHelper kafkaPayloadHelper, OutboxEventRepository outboxEventRepository, LienEventAdresseMapper lienEventAdresseMapper,
                            AdresseRepository adresseRepository, LienEventAdresseRepository lienEventAdresseRepository, MessageHelper messageHelper) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.eventJooqRepository = eventJooqRepository;
        this.eventJpqlRepository = eventJpqlRepository;
        this.eventSqlNativeRepository = eventSqlNativeRepository;
        this.eventQueryDSLRepository = eventQueryDSLRepository;
        this.eventCriteriaApiRepository = eventCriteriaApiRepository;
        this.kafkaPayloadHelper = kafkaPayloadHelper;
        this.outboxEventRepository = outboxEventRepository;
        this.lienEventAdresseMapper = lienEventAdresseMapper;
        this.adresseRepository = adresseRepository;
        this.lienEventAdresseRepository = lienEventAdresseRepository;
        this.messageHelper = messageHelper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> findAllEvent() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingJpql(EventSearchCriteria eventSearchCriteria) {

        return eventJpqlRepository.findAllJpql(eventSearchCriteria).stream().map(eventMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingCriteria(EventSearchCriteria eventSearchCriteria) {
        return eventCriteriaApiRepository.findAllCriteaApi(eventSearchCriteria).stream().map(eventMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingSpecification(EventSearchCriteria eventSearchCriteria) {
        Specification<Event> spec = EventSpecifications.creerSpecification(eventSearchCriteria);
        Sort sort = Sort.by(Sort.Direction.fromString(eventSearchCriteria.getOrderDirection().toString()), eventSearchCriteria.getOrderBy().toString());
        Pageable pageable = PageRequest.of(eventSearchCriteria.getPageNumber(), eventSearchCriteria.getPageSize(), sort);
        Page<EventDto> page = eventRepository
                .findAll(spec, pageable)
                .map(eventMapper::toDto);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingNativeSql(EventSearchCriteria eventSearchCriteria) {
        return eventSqlNativeRepository.findAllNativeSQL(eventSearchCriteria).stream().map(eventMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingQueryDSL(EventSearchCriteria eventSearchCriteria) {
        return eventQueryDSLRepository.findAllQueryDsl(eventSearchCriteria).stream().map(eventMapper::toDto).toList();
    }

    @Override
    public EventDto createEvent(EventCreateDto eventDto) {
        Objects.requireNonNull(eventDto, messageHelper.get("event.dto.null"));
        Event event = eventMapper.toEntity(eventDto);
        Event createdEvent = eventRepository.save(event);
        return eventMapper.toDto(createdEvent);
    }

    /**
     * Update à l'ancienne un événement existant. ON remplace tout le contenu de l'événement par le contenu du DTO.
     *RIsque de perdre des données si le DTO ne contient pas tous les champs.
     * @param eventId
     * @param eventDto
     * @return eventDto
     */
    @Override
    public EventDto updateEvent(Long eventId, EventUpdateDto eventDto) {
        Objects.requireNonNull(eventDto, messageHelper.get("event.dto.null"));
        Event eventExisted = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.not.found", eventId)));
        Event updated = eventMapper.toEntity(eventDto);
        updated.setId(eventId); // Assure qu'on veut faire une mise à jour de l'événement existant
        updated.setLiens(eventExisted.getLiens()); // Conserve les adresses existantes
        return eventMapper.toDto(eventRepository.save(updated));
    }

    /**
     * UPDATE MODERNE
     * Patch un événement existant. ON ne met à jour que les champs non-nuls du DTO.
     * @param eventId
     * @param eventPatchDto
     * @return eventDto
     */
    @Override
    public EventDto patchEvent(Long eventId, EventPatchDto eventPatchDto) {
        Objects.requireNonNull(eventPatchDto, messageHelper.get("event.update.dto.null"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.not.found", eventId)));

        eventMapper.updateEntityFromDto(eventPatchDto, event); // MapStruct applique uniquement les champs non-nuls

        return eventMapper.toDto(eventRepository.save(event));
    }


    @Override
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(messageHelper.get("event.not.found", eventId));
        }
        eventRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public EventDto getEvent(Long id) {
        return eventRepository.findById(id).map(eventMapper::toDto).orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.not.found", id)));
    }

    @Override
    public EventDto createEventWithEnvoieKafka(EventCreateDto eventDto) {
        EventDto createdEventDto;
        createdEventDto = createEvent(eventDto);
        EventCreatedKafkaPayload createdEventKafkaPayload = eventMapper.toKafkaPayload(createdEventDto);
        String payloadJson = kafkaPayloadHelper.toJson(createdEventKafkaPayload);

        // 3. Prepare and save the Outbox event (ex: topic = "event.created", key = createdEventDto.id)
        OutboxEvent outboxEvent = new OutboxEvent(
                null, // id auto-généré
                eventCreatedTopic, // topic Kafka (ou autre)
                String.valueOf(createdEventDto.getId()), // key
                payloadJson,
                false, // sent = false
                LocalDateTime.now(),
                null
        );
        outboxEventRepository.save(outboxEvent);
        outboxEvent = new OutboxEvent(
                null, // id auto-généré
                eventCreatedImpairTopic, // topic Kafka (ou autre)
                String.valueOf(createdEventDto.getId()), // key
                payloadJson,
                false, // sent = false
                LocalDateTime.now(),
                null
        );
        outboxEventRepository.save(outboxEvent);
        return createdEventDto;
    }

    @Override
    public EventDto addAdresse(Long eventId, LienEventAdresseRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.not.found", eventId)));
        LienEventAdresse lien = lienEventAdresseMapper.toEntity(dto);
        Adresse adresseEntrante = lien.getAdresse();
        if (adresseEntrante.getId() == null) {

            // Recherche par contenu (Structure fine)
            Optional<Adresse> existante = adresseRepository.findByNumeroAndRueAndCodePostalAndVilleAndPays(
                    adresseEntrante.getNumero(),
                    adresseEntrante.getRue(),
                    adresseEntrante.getCodePostal(),
                    adresseEntrante.getVille(),
                    adresseEntrante.getPays()
            );
            lien.setAdresse(existante.orElse(adresseEntrante));
        }
        else {
            Adresse adresseExistante = adresseRepository.findById(adresseEntrante.getId())
                    .orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.adresse.not.found", adresseEntrante.getId())));

            lien.setAdresse(adresseExistante);
        }
        event.addLien(lien);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public void removeAdresse(Long eventId, Long adresseId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(messageHelper.get("event.not.found", eventId)));
        Adresse adresseProxy = adresseRepository.getReferenceById(adresseId);
        event.removeLien(adresseProxy);
        eventRepository.save(event);
    }

    @Override
    public Set<LienEventAdresseDto> getAdresseByEventId(Long eventId) {
        if(!eventRepository.existsById(eventId)){
            throw new EntityNotFoundException(messageHelper.get("event.not.found", eventId));
        }
        return lienEventAdresseRepository.findByEventId(eventId).stream().map(lienEventAdresseMapper::toDto).collect(Collectors.toSet());
    }


}
