package com.yuewie.apievent.service.impl;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.mapper.EventMapper;
import com.yuewie.apievent.repository.*;
import com.yuewie.apievent.repository.impl.EventSpecifications;
import com.yuewie.apievent.service.EventService;
import com.yuewie.apievent.aop.log.Loggable;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    public EventServiceImpl(EventMapper eventMapper, EventRepository eventRepository,EventJooqRepository eventJooqRepository,
                            EventJpqlRepository eventJpqlRepository, EventSqlNativeRepository eventSqlNativeRepository,
                            EventQueryDSLRepository eventQueryDSLRepository, EventCriteriaApiRepository eventCriteriaApiRepository) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.eventJooqRepository = eventJooqRepository;
        this.eventJpqlRepository = eventJpqlRepository;
        this.eventSqlNativeRepository = eventSqlNativeRepository;
        this.eventQueryDSLRepository = eventQueryDSLRepository;
        this.eventCriteriaApiRepository = eventCriteriaApiRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> findAllEvent() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingJpql(EventSearchCriteria eventSearchCriteria) {

        return eventJpqlRepository.findAllJpql(eventSearchCriteria).stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingCriteria(EventSearchCriteria eventSearchCriteria) {
        return eventCriteriaApiRepository.findAllCriteaApi(eventSearchCriteria).stream().map(eventMapper::toDto).collect(Collectors.toList());
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
        return eventSqlNativeRepository.findAllNativeSQL(eventSearchCriteria).stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEventUsingQueryDSL(EventSearchCriteria eventSearchCriteria) {
        return eventQueryDSLRepository.findAllQueryDsl(eventSearchCriteria).stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventDto createEvent(EventCreateDto eventDto) {
        Objects.requireNonNull(eventDto, "eventDto cannot be null");
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
        Objects.requireNonNull(eventDto, "eventDto cannot be null");
        Event eventExisted = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));
        Event updated = eventMapper.toEntity(eventDto);
        updated.setId(eventId); // Assure qu'on veut faire une mise à jour de l'événement existant
        updated.setAdresses(eventExisted.getAdresses()); // Conserve les adresses existantes
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
        Objects.requireNonNull(eventPatchDto, "eventUpdateDto cannot be null");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));

        eventMapper.updateEntityFromDto(eventPatchDto, event); // MapStruct applique uniquement les champs non-nuls

        return eventMapper.toDto(eventRepository.save(event));
    }


    @Override
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event " + eventId + " not found");
        }
        eventRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public EventDto getEvent(Long id) {
        return eventRepository.findById(id).map(eventMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + id));
    }
}
