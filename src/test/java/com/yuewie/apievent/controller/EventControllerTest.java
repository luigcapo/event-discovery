package com.yuewie.apievent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.service.EventService;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventDto eventDto;

    private EventCreateDto eventCreateDto;

    private AdresseDto adresseDto;

    private AdresseRequestDto adresseRequestDto;

    private LienEventAdresseDto lienEventAdresseDto;

    private LienEventAdresseRequestDto lienEventAdresseRequestDto;

    private static Clock fixedClock;

    @BeforeAll
    static void setupClock() {
        Instant now = Instant.now();
        fixedClock = Clock.fixed(now, ZoneId.systemDefault());
    }

    @BeforeEach
    void setUp() {
        adresseDto = AdresseDto.builder()
                .id(1L)
                .numero("3")
                .rue("rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        lienEventAdresseDto = LienEventAdresseDto.builder()
                .principal(true)
                .adresse(adresseDto)
                .build();

        adresseRequestDto = AdresseRequestDto.builder()
                .numero("3")
                .rue("rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        lienEventAdresseRequestDto = LienEventAdresseRequestDto.builder()
                .principal(true)
                .adresse(adresseRequestDto)
                .build();

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setName("Concert");
        eventDto.setDescription("A live concert");
        eventDto.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        eventDto.setEnd(LocalDateTime.now(fixedClock).plusDays(2).truncatedTo(ChronoUnit.SECONDS));
        eventDto.setAdresses(Set.of(lienEventAdresseDto));

        eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Concert");
        eventCreateDto.setDescription("A live concert");
        eventCreateDto.setStart(LocalDateTime.now(fixedClock).plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        eventCreateDto.setEnd(LocalDateTime.now(fixedClock).plusDays(2).truncatedTo(ChronoUnit.SECONDS));
        eventCreateDto.setAdresses(Set.of(lienEventAdresseRequestDto));

    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/{id}")
    class GetEventById {

        @Test
        @DisplayName("Devrait retourner un événement par son ID")
        void shouldReturnEvent_whenGetEventById() throws Exception {
            // Given
            Long eventId = 1L;
            when(eventService.getEvent(eventId)).thenReturn(eventDto);

        // When & Then
        mockMvc.perform(get("/api/v1/events/{id}", eventId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventDto.getId()))
                .andExpect(jsonPath("$.name").value(eventDto.getName()))
                .andExpect(jsonPath("$.description").value(eventDto.getDescription()));
        verify(eventService).getEvent(eventId);
    }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request pour un ID invalide")
        void shouldReturnBadRequest_whenGetEventWithInvalidId() throws Exception {
            // Given
            Long invalidId = -1L;

            // When & Then
            mockMvc.perform(get("/api/v1/events/{id}", invalidId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }
    }

    @Nested
    @DisplayName("Tests for POST /api/v1/events")
    class CreateEvent {

        @Test
        @DisplayName("Devrait créer un nouvel événement")
        void shouldCreateEvent_whenCreateEvent() throws Exception {
            EventDto savedEventDto = new EventDto();
            savedEventDto.setId(1L); // Simule l'ID généré par la base de données
            savedEventDto.setName(eventCreateDto.getName());
            savedEventDto.setDescription(eventCreateDto.getDescription());
            savedEventDto.setStart(eventCreateDto.getStart());
            savedEventDto.setEnd(eventCreateDto.getEnd());
            savedEventDto.setAdresses(Set.of(lienEventAdresseDto));
            savedEventDto.setAdressePrincipale(lienEventAdresseDto.getAdresse());
            // Given
            when(eventService.createEvent(any(EventCreateDto.class))).thenReturn(savedEventDto);

            // When & Then
            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventCreateDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(savedEventDto.getId()))
                    .andExpect(jsonPath("$.name").value(savedEventDto.getName()));

            verify(eventService).createEvent(eventCreateDto);
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request lors de lma création pour des données invalides")
        void shouldReturnBadRequest_whenCreateEventWithInvalidData() throws Exception {
            // Given
            eventCreateDto.setName(""); // Invalid name

            // When & Then
            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventCreateDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }
    }

    @Nested
    @DisplayName("Tests for DELETE /api/v1/events/{id}")
    class DeleteEvent {

        @Test
        @DisplayName("Devrait supprimer un événement par son ID")
        void shouldDeleteEvent_whenDeleteEvent() throws Exception {
            // Given
            Long eventId = 1L;
            doNothing().when(eventService).deleteEvent(eventId);

            // When & Then
            mockMvc.perform(delete("/api/v1/events/{id}", eventId))
                    .andExpect(status().isNoContent());

            verify(eventService).deleteEvent(eventId);
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request pour un ID invalide")
        void shouldReturnBadRequest_whenDeleteEventWithInvalidId() throws Exception {
            // Given
            Long invalidId = -1L;

            // When & Then
            mockMvc.perform(delete("/api/v1/events/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search")
    class GetAllEvents {

        @Test
        @DisplayName("Devrait retourner tous les événements")
        void shouldReturnAllEvents_whenGetEvents() throws Exception {
            // Given
            when(eventService.findAllEvent()).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/all")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()))
                    .andExpect(jsonPath("$[0].description").value(eventDto.getDescription()));

            verify(eventService).findAllEvent();
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search/spec")
    class SearchEventsBySpec {

        @Test
        @DisplayName("Devrait rechercher des événements avec des spécifications")
        void shouldReturnMatchingEvents_whenSearchEventsBySpec() throws Exception {
            // Given

            when(eventService.searchEventUsingSpecification(any())).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/spec")
                            .param("name", "Concert")
                            .param("ville", "Paris")
                            .queryParam("numero","3")
                            .queryParam("rue","rue du marglier")
                            .param("codePostal", "75000")
                            .param("startDate", "2025-12-01")
                            .param("startTime", "20:00")
                            .param("endDate", "2025-12-02")
                            .param("endTime", "22:00")
                            .param("orderBy", "id")
                            .param("orderDirection", "ASC")
                            .param("pageNumber", "1")
                            .param("pageSize", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()));
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search/jpql")
    class SearchEventsByJpql {

        @Test
        @DisplayName("Devrait rechercher des événements avec JPQL")
        void shouldReturnMatchingEvents_whenSearchEventsByJpql() throws Exception {
            // Given
            when(eventService.searchEventUsingJpql(any())).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/jpql")
                            .queryParam("name", "Concert")
                            .queryParam("numero","3")
                            .queryParam("rue","rue du marglier")
                            .queryParam("ville", "Paris")
                            .queryParam("codePostal", "75000")
                            .queryParam("startDate", "2025-12-01")
                            .queryParam("startTime", "20:00")
                            .queryParam("endDate", "2025-12-02")
                            .queryParam("endTime", "22:00")
                            .queryParam("orderBy", "id")
                            .queryParam("orderDirection", "ASC")
                            .queryParam("pageNumber", "1")
                            .queryParam("pageSize", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()));
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search/criteria")
    class SearchEventsByCriteriaApi {

        @Test
        @DisplayName("Devrait rechercher des événements avec l'API Criteria")
        void shouldReturnMatchingEvents_whenSearchEventsByCriteriaApi() throws Exception {
            // Given
            when(eventService.searchEventUsingCriteria(any())).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/criteria")
                            .queryParam("name", "Concert")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()));

        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search/native")
    class SearchEventsByNativeSql {

        @Test
        @DisplayName("Devrait rechercher des événements avec SQL natif")
        void shouldReturnMatchingEvents_whenSearchEventsByNativeSql() throws Exception {
            // Given
            when(eventService.searchEventUsingNativeSql(any())).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/native")
                            .queryParam("name", "Concert")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()));
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/v1/events/search/querydsl")
    class SearchEventsByQueryDSL {

        @Test
        @DisplayName("Devrait rechercher des événements avec QueryDSL")
        void shouldReturnMatchingEvents_whenSearchEventsByQueryDSL() throws Exception {
            // Given
            when(eventService.searchEventUsingQueryDSL(any())).thenReturn(List.of(eventDto));

            // When & Then
            mockMvc.perform(get("/api/v1/events/search/querydsl")
                            .queryParam("name", "Concert")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(eventDto.getId()))
                    .andExpect(jsonPath("$[0].name").value(eventDto.getName()));
        }
    }

    @Nested
    @DisplayName("Tests for Address Management")
    class AddressManagement {

        @Test
        @DisplayName("Devrait ajouter une adresse à un événement")
        void shouldAddAdresseToEvent() throws Exception {
            // Given
            Long eventId = 1L;
            AdresseRequestDto newAdresse = AdresseRequestDto.builder()
                    .numero("5")
                    .rue("rue de Lyon")
                    .codePostal("69000")
                    .ville("Lyon")
                    .pays("France")
                    .build();

            LienEventAdresseRequestDto lienRequest = LienEventAdresseRequestDto.builder()
                    .principal(false)
                    .adresse(newAdresse)
                    .build();

            when(eventService.addAdresse(eventId, lienRequest)).thenReturn(eventDto);

            // When & Then
            mockMvc.perform(post("/api/v1/events/{id}/adresses", eventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lienRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(eventDto.getId()))
                    .andExpect(jsonPath("$.name").value(eventDto.getName()));

            verify(eventService).addAdresse(eventId, lienRequest);
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request pour un ID d'événement invalide lors de l'ajout d'adresse")
        void shouldReturnBadRequest_whenAddAdresseWithInvalidEventId() throws Exception {
            // Given
            Long invalidId = -1L;
            LienEventAdresseRequestDto lienRequest = LienEventAdresseRequestDto.builder()
                    .principal(false)
                    .adresse(adresseRequestDto)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/events/{id}/adresses", invalidId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lienRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }

        @Test
        @DisplayName("Devrait retirer une adresse d'un événement")
        void shouldRemoveAdresseFromEvent() throws Exception {
            // Given
            Long eventId = 1L;
            Long adresseId = 1L;
            doNothing().when(eventService).removeAdresse(eventId, adresseId);

            // When & Then
            mockMvc.perform(delete("/api/v1/events/{id}/adresses/{adresseId}", eventId, adresseId))
                    .andExpect(status().isNoContent());

            verify(eventService).removeAdresse(eventId, adresseId);
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request pour un ID invalide lors de la suppression d'adresse")
        void shouldReturnBadRequest_whenRemoveAdresseWithInvalidIds() throws Exception {
            // Given
            Long invalidEventId = -1L;
            Long adresseId = 1L;

            // When & Then
            mockMvc.perform(delete("/api/v1/events/{id}/adresses/{adresseId}", invalidEventId, adresseId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request pour un ID d'adresse invalide lors de la suppression")
        void shouldReturnBadRequest_whenRemoveAdresseWithInvalidAdresseId() throws Exception {
            // Given
            Long eventId = 1L;
            Long invalidAdresseId = -1L;

            // When & Then
            mockMvc.perform(delete("/api/v1/events/{id}/adresses/{adresseId}", eventId, invalidAdresseId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                    .andExpect(jsonPath("$.message").value("Validation échouée"));
        }
    }
}
