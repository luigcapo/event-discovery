package com.yuewie.apievent.service;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.dto.constraint.EventFieldForOrderBy;
import com.yuewie.apievent.dto.constraint.OrderDirection;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.mapper.EventMapper;
import com.yuewie.apievent.repository.*;
import com.yuewie.apievent.service.impl.EventServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventJpqlRepository eventJpqlRepository;

    @Mock
    private EventCriteriaApiRepository eventCriteriaApiRepository;

    @Mock
    private EventSqlNativeRepository eventSqlNativeRepository;

    @Mock
    private EventQueryDSLRepository eventQueryDSLRepository;


    @InjectMocks
    private EventServiceImpl eventService;

    private EventDto eventDto;
    private EventCreateDto eventCreateDto;
    private Event event;
    private EventSearchCriteria searchCriteria;


    @BeforeEach
    void setUp() {
        AdresseDto adresseDto = AdresseDto.builder()
                .id(1L)
                .intituleAdresse("3 rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        AdresseRequestDto adresseRequestDto = AdresseRequestDto.builder()
                .intituleAdresse("3 rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setName("Concert");
        eventDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        eventDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        eventDto.setDescription("Concert");
        eventDto.setAdresses(Set.of(adresseDto));

        eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Concert");
        eventCreateDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        eventCreateDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        eventCreateDto.setDescription("Concert");
        eventCreateDto.setAdresses(Set.of(adresseRequestDto));

        Adresse adresse = new Adresse();
        adresse.setId(1L);
        adresse.setIntituleAdresse("3 rue du marglier");
        adresse.setCodePostal("75000");
        adresse.setVille("Paris");
        adresse.setPays("France");

        event = new Event();
        event.setId(1L);
        event.setName("Concert");
        event.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        event.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        event.setDescription("Concert");
        event.setAdresses(Set.of(adresse));

        lenient().when(eventMapper.toEntity(eventCreateDto)).thenReturn(event);
        lenient().when(eventMapper.toDto(event)).thenReturn(eventDto);

        searchCriteria = new EventSearchCriteria();
        searchCriteria.setPageNumber(0);
        searchCriteria.setPageSize(10);
        searchCriteria.setOrderBy(EventFieldForOrderBy.name);
        searchCriteria.setOrderDirection(OrderDirection.ASC);
        searchCriteria.setName("Concert");
        searchCriteria.setVille("Paris");
        searchCriteria.setCodePostal("75000");
        searchCriteria.setIntituleAdresse("3 rue du marglier");
        searchCriteria.setStartDate("2025-12-01");
        searchCriteria.setStartTime("20:00");
        searchCriteria.setEndDate("2025-12-02");
        searchCriteria.setEndTime("20:00");

    }

    @Test
    @DisplayName("Devrait ajouter un événement et le retourner")
    void shouldReturnEventDto_whenCreatingEvent() {

        //Given
        when(eventRepository.save(event)).thenReturn(event);


        // When
        EventDto createdEvent = eventService.createEvent(eventCreateDto);

        // Then
        assertThat(createdEvent)
                .isNotNull()
                .isEqualTo(eventDto);
        verify(eventMapper).toEntity(eventCreateDto);
        verify(eventRepository).save(event);
        verify(eventMapper).toDto(event);
    }

    @Nested
    @DisplayName("Tests de la recherche d'un événement")
    class FindEventById {

        @Test
        @Order(1)
        @DisplayName("Devrait retourner un événement si l'ID existe")
        void shouldReturnEvent_whenFindingById() {
            // Given
            final Long EVENT_ID = 1L;

            when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            // When
            EventDto result = eventService.getEvent(EVENT_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(EVENT_ID);
            verify(eventRepository).findById(EVENT_ID);
            verify(eventMapper).toDto(event);
        }

        @Test
        @Order(2)
        @DisplayName("Devrait lever une exception DocumentNotFoundException si l'ID n'existe pas")
        void shouldThrowEntityNotFoundException_whenFindingByIdAndIdDoesNotExist() {
            // Given
            final Long EVENT_ID = 1L;

            when(eventRepository.findById(EVENT_ID))
                    .thenThrow(new EntityNotFoundException("Event not found with ID: " + EVENT_ID));

            // When & Then
            assertThatThrownBy(() -> eventService.getEvent(EVENT_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Event");

            // verifie aue la methode a ete appele au moins 1 fois
            verify(eventRepository).findById(EVENT_ID);
            // verifie aue la methode a ete appele exactement 1 fois
            verify(eventRepository, times(1)).findById(EVENT_ID);
        }
    }

        @Test
        @DisplayName("Should return events using JPQL")
        void shouldReturnEventsUsingJpql() {
            // Given
            when(eventJpqlRepository.findAllJpql(searchCriteria)).thenReturn(List.of(event));

            // When
            List<EventDto> result = eventService.searchEventUsingJpql(searchCriteria);

            // Then
            assertThat(result).isNotNull().hasSize(1);
            verify(eventJpqlRepository).findAllJpql(searchCriteria);
            verify(eventMapper).toDto(event);
        }

        @Test
        @DisplayName("Should return events using Criteria API")
        void shouldReturnEventsUsingCriteria() {
            // Given
            when(eventCriteriaApiRepository.findAllCriteaApi(searchCriteria)).thenReturn(List.of(event));

            // When
            List<EventDto> result = eventService.searchEventUsingCriteria(searchCriteria);

            // Then
            assertThat(result).isNotNull().hasSize(1);
            verify(eventCriteriaApiRepository).findAllCriteaApi(searchCriteria);
            verify(eventMapper).toDto(event);
        }

        @Test
        @DisplayName("Should return events using Native SQL")
        void shouldReturnEventsUsingNativeSql() {
            // Given
            when(eventSqlNativeRepository.findAllNativeSQL(searchCriteria)).thenReturn(List.of(event));

            // When
            List<EventDto> result = eventService.searchEventUsingNativeSql(searchCriteria);

            // Then
            assertThat(result).isNotNull().hasSize(1);
            verify(eventSqlNativeRepository).findAllNativeSQL(searchCriteria);
            verify(eventMapper).toDto(event);
        }

        @Test
        @DisplayName("Should return events using QueryDSL")
        void shouldReturnEventsUsingQueryDSL() {
            // Given
            when(eventQueryDSLRepository.findAllQueryDsl(searchCriteria)).thenReturn(List.of(event));

            // When
            List<EventDto> result = eventService.searchEventUsingQueryDSL(searchCriteria);

            // Then
            assertThat(result).isNotNull().hasSize(1);
            verify(eventQueryDSLRepository).findAllQueryDsl(searchCriteria);
            verify(eventMapper).toDto(event);
        }
}
