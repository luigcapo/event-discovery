package com.yuewie.apievent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuewie.apievent.dto.AdresseDto;
import com.yuewie.apievent.dto.EventDto;
import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

    private AdresseDto adresseDto;

    @BeforeEach
    void setUp() {

        adresseDto = AdresseDto.builder()
                .id(1L)
                .intituleAdresse("3 rue du marglier")
                .codePostal("75000")
                .ville("Paris")
                .pays("France")
                .build();

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setName("Concert");
        eventDto.setDescription("A live concert");
        eventDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
        eventDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
        eventDto.setAdresses(Set.of(adresseDto));


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
            eventDto.setId(null);
            EventDto savedEventDto = new EventDto();
            savedEventDto.setId(1L); // Simule l'ID généré par la base de données
            savedEventDto.setName(eventDto.getName());
            savedEventDto.setDescription(eventDto.getDescription());
            savedEventDto.setStart(LocalDateTime.of(2025, 12, 1, 20, 0));
            savedEventDto.setEnd(LocalDateTime.of(2025, 12, 2, 20, 0));
            savedEventDto.setAdresses(Set.of(adresseDto));
            // Given
            when(eventService.createEvent(eventDto)).thenReturn(savedEventDto);

            // When & Then
            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(savedEventDto.getId()))
                    .andExpect(jsonPath("$.name").value(eventDto.getName()));

            verify(eventService).createEvent(eventDto);
        }

        @Test
        @DisplayName("Devrait retourner 400 Bad Request lors de lma création pour des données invalides")
        void shouldReturnBadRequest_whenCreateEventWithInvalidData() throws Exception {
            // Given
            eventDto.setName(""); // Invalid name

            // When & Then
            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
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
            mockMvc.perform(get("/api/v1/events/search")
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
                            .param("codePostal", "75000")
                            .param("startDate", "2025-12-01")
                            .param("startTime", "20:00")
                            .param("endDate", "2025-12-02")
                            .param("endTime", "22:00")
                            .param("orderBy", "id")
                            .param("orderDirection", "ASC")
                            .param("pageNumber", "0")
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
                            .queryParam("ville", "Paris")
                            .queryParam("codePostal", "75000")
                            .queryParam("startDate", "2025-12-01")
                            .queryParam("startTime", "20:00")
                            .queryParam("endDate", "2025-12-02")
                            .queryParam("endTime", "22:00")
                            .queryParam("orderBy", "id")
                            .queryParam("orderDirection", "ASC")
                            .queryParam("pageNumber", "0")
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
}
