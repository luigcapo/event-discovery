package com.yuewie.apievent.service;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.dto.constraint.EventFieldForOrderBy;
import com.yuewie.apievent.dto.constraint.OrderDirection;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.entity.LienEventAdresse;
import com.yuewie.apievent.mapper.EventMapper;
import com.yuewie.apievent.mapper.LienEventAdresseMapper;
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

    @Mock
    private AdresseRepository adresseRepository;

    @Mock
    private LienEventAdresseMapper lienEventAdresseMapper;

    @Mock
    private LienEventAdresseRepository lienEventAdresseRepository;

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
                .numero("3")
                .rue("rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseDto lienEventAdresseDto = LienEventAdresseDto.builder()
                .principal(true)
                .adresse(adresseDto)
                .build();

        AdresseRequestDto adresseRequestDto = AdresseRequestDto.builder()
                .numero("3")
                .rue("rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        LienEventAdresseRequestDto lienEventAdresseRequestDto = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresseRequestDto)
                .build();

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setName("Concert");
        eventDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        eventDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        eventDto.setDescription("Concert");
        eventDto.setAdresses(Set.of(lienEventAdresseDto));

        eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Concert");
        eventCreateDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        eventCreateDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        eventCreateDto.setDescription("Concert");
        eventCreateDto.setAdresses(Set.of(lienEventAdresseRequestDto));

        Adresse adresse = new Adresse();
        adresse.setId(1L);
        adresse.setNumero("3");
        adresse.setRue("rue du marglier");
        adresse.setCodePostal("75000");
        adresse.setVille("Paris");
        adresse.setPays("France");

        event = new Event();
        event.setId(1L);
        event.setName("Concert");
        event.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        event.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        event.setDescription("Concert");

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
        searchCriteria.setNumero("3");
        searchCriteria.setRue("rue du marglier");
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

    @Nested
    @DisplayName("Tests de gestion des adresses")
    class AddressManagement {

        @Test
        @DisplayName("Devrait ajouter une adresse à un événement")
        void shouldAddAdresseToEvent() {
            // Given
            Long eventId = 1L;
            AdresseRequestDto newAdresseRequest = AdresseRequestDto.builder()
                    .numero("5")
                    .rue("rue de Lyon")
                    .codePostal("69000")
                    .ville("Lyon")
                    .pays("France")
                    .build();

            LienEventAdresseRequestDto lienRequest = LienEventAdresseRequestDto.builder()
                    .principal(false)
                    .adresse(newAdresseRequest)
                    .build();

            Adresse newAdresse = new Adresse();
            newAdresse.setNumero("5");
            newAdresse.setRue("rue de Lyon");

            LienEventAdresse lien = new LienEventAdresse();
            lien.setAdresse(newAdresse);
            lien.setPrincipal(false);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(lienEventAdresseMapper.toEntity(lienRequest)).thenReturn(lien);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            // When
            EventDto result = eventService.addAdresse(eventId, lienRequest);

            // Then
            assertThat(result).isNotNull();
            verify(eventRepository).findById(eventId);
            verify(lienEventAdresseMapper).toEntity(lienRequest);
            verify(eventRepository).save(event);
            verify(eventMapper).toDto(event);
        }

        @Test
        @DisplayName("Devrait lever une exception si l'événement n'existe pas lors de l'ajout d'adresse")
        void shouldThrowException_whenEventNotFoundForAddAdresse() {
            // Given
            Long eventId = 999L;
            AdresseRequestDto adresseRequestDto = AdresseRequestDto.builder()
                    .numero("123")
                    .rue("Test")
                    .codePostal("12345")
                    .ville("Ville")
                    .pays("Pays")
                    .build();

            LienEventAdresseRequestDto lienRequest = LienEventAdresseRequestDto.builder()
                    .principal(false)
                    .adresse(adresseRequestDto)
                    .build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> eventService.addAdresse(eventId, lienRequest))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(eventRepository).findById(eventId);
            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait retirer une adresse d'un événement")
        void shouldRemoveAdresseFromEvent() {
            // Given
            Long eventId = 1L;
            Long adresseId = 1L;
            Adresse adresseProxy = new Adresse();
            adresseProxy.setId(adresseId);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(adresseRepository.getReferenceById(adresseId)).thenReturn(adresseProxy);
            when(eventRepository.save(event)).thenReturn(event);

            // When
            eventService.removeAdresse(eventId, adresseId);

            // Then
            verify(eventRepository).findById(eventId);
            verify(adresseRepository).getReferenceById(adresseId);
            verify(eventRepository).save(event);
        }

        @Test
        @DisplayName("Devrait lever une exception si l'événement n'existe pas lors de la suppression d'adresse")
        void shouldThrowException_whenEventNotFoundForRemoveAdresse() {
            // Given
            Long eventId = 999L;
            Long adresseId = 1L;

            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> eventService.removeAdresse(eventId, adresseId))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(eventRepository).findById(eventId);
            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait récupérer toutes les adresses d'un événement")
        void shouldGetAllAdressesOfEvent() {
            // Given
            Long eventId = 1L;
            LienEventAdresseDto lienDto = LienEventAdresseDto.builder()
                    .principal(true)
                    .adresse(AdresseDto.builder()
                            .id(1L)
                            .numero("3")
                            .rue("rue du marglier")
                            .codePostal("75000")
                            .ville("Paris")
                            .pays("France")
                            .build())
                    .build();

            LienEventAdresse lien = new LienEventAdresse();
            lien.setPrincipal(true);

            when(eventRepository.existsById(eventId)).thenReturn(true);
            when(lienEventAdresseRepository.findByEventId(eventId)).thenReturn(List.of(lien));
            when(lienEventAdresseMapper.toDto(lien)).thenReturn(lienDto);

            // When
            Set<LienEventAdresseDto> result = eventService.getAdresseByEventId(eventId);

            // Then
            assertThat(result).isNotNull().hasSize(1);
            verify(eventRepository).existsById(eventId);
            verify(lienEventAdresseRepository).findByEventId(eventId);
            verify(lienEventAdresseMapper).toDto(lien);
        }

        @Test
        @DisplayName("Devrait lever une exception si l'événement n'existe pas lors de la récupération des adresses")
        void shouldThrowException_whenEventNotFoundForGetAdresses() {
            // Given
            Long eventId = 999L;

            when(eventRepository.existsById(eventId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> eventService.getAdresseByEventId(eventId))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(eventRepository).existsById(eventId);
        }
    }
}
