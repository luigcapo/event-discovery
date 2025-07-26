package com.yuewie.apievent.controller;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.exception.ApiError;
import com.yuewie.apievent.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Validated
@Tag(name="Event API", description = "Endpoints pour la gestion des évents")
public class EventController {

    private final EventService eventService;


    /** GET /api/events/{id} */
    @Operation(summary = "Récupérer un événement par son ID",
            description = "Récupère un événement spécifique en fonction de son ID. " +
                    "Si l'événement n'existe pas, une erreur 404 sera renvoyée avec un message d'erreur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event trouvé", content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "404", description = "Event non trouvé ou ID invalide", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(
            @Parameter(description = "ID de l'event a recuperer", required = true, example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable @Min(1) Long id) {
        EventDto dto = eventService.getEvent(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    @Operation(summary = "Récupérer tous les événements",
            description = "Récupère une liste de tous les événements disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements récupérée avec succès"),
    })
    public ResponseEntity<List<EventDto>> getEvents() {
        List<EventDto> events = eventService.findAllEvent();
        return ResponseEntity.ok(events);      // 200
    }

    @GetMapping("/search/spec")
    @Operation(summary = "Rechercher des événements avec des spécifications",
            description = "Recherche des événements en fonction des critères fournis via des spécifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements correspondant aux critères récupérée"),
    })
    public ResponseEntity<List<EventDto>> searchEventsBySpec(
            @Parameter(description = "Critères de recherche pour les événements", required = true)
            @Valid @ModelAttribute EventSearchCriteria eventSearchCriteria) {
        List<EventDto> events = eventService.searchEventUsingSpecification(eventSearchCriteria);
        return ResponseEntity.ok(events);      // 200
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Rechercher des événements avec JPQL",
            description = "Recherche des événements en utilisant des requêtes JPQL basées sur les critères fournis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements correspondant aux critères récupérée"),
    })
    public ResponseEntity<List<EventDto>> searchEventsByJpql(
            @Parameter(description = "Critères de recherche pour les événements", required = true)
            @Valid @ModelAttribute EventSearchCriteria eventSearchCriteria) {
        List<EventDto> events = eventService.searchEventUsingJpql(eventSearchCriteria);
        return ResponseEntity.ok(events);      // 200
    }

    @GetMapping("/search/criteria")
    @Operation(summary = "Rechercher des événements avec l'API Criteria",
            description = "Recherche des événements en utilisant l'API Criteria basée sur les critères fournis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements correspondant aux critères récupérée"),
    })
    public ResponseEntity<List<EventDto>> searchEventsByCriteriaApi(
            @Parameter(description = "Critères de recherche pour les événements", required = true)
            @Valid @ModelAttribute EventSearchCriteria eventSearchCriteria) {
        List<EventDto> events = eventService.searchEventUsingCriteria(eventSearchCriteria);
        return ResponseEntity.ok(events);      // 200
    }

    @GetMapping("/search/native")
    @Operation(summary = "Rechercher des événements avec SQL natif",
            description = "Recherche des événements en utilisant des requêtes SQL natives basées sur les critères fournis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements correspondant aux critères récupérée"),
    })
    public ResponseEntity<List<EventDto>> searchEventsByNativeSql(
            @Parameter(description = "Critères de recherche pour les événements", required = true)
            @Valid @ModelAttribute EventSearchCriteria eventSearchCriteria) {
        List<EventDto> events = eventService.searchEventUsingNativeSql(eventSearchCriteria);
        return ResponseEntity.ok(events);      // 200
    }

    @GetMapping("/search/querydsl")
    @Operation(summary = "Rechercher des événements avec QueryDSL",
            description = "Recherche des événements en utilisant QueryDSL basé sur les critères fournis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste d'événements correspondant aux critères récupérée"),
    })
    public ResponseEntity<List<EventDto>> searchEventsByQueryDSL(
            @Parameter(description = "Critères de recherche pour les événements", required = true)
            @Valid @ModelAttribute EventSearchCriteria eventSearchCriteria) {
        List<EventDto> events = eventService.searchEventUsingQueryDSL(eventSearchCriteria);
        return ResponseEntity.ok(events);      // 200
    }

    /* ---------- CREATE ---------- */

    @PostMapping("/kafka")
    @Operation(summary = "Créer un nouvel événement et l'envoyer à kafka",
            description = "Crée un nouvel événement et l'envoyer à kafka en fonction des données fournies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Événement créé avec succès", content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies pour la création de l'événement", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<EventDto> createEventAndSendToKafka(
            @Parameter(description = "Données de l'événement à créer", required = true)
            @RequestBody @Valid EventCreateDto dto) {
        EventDto created = eventService.createEventWithEnvoieKafka(dto);
        URI location = URI.create("/api/events/" + created.getId());
        return ResponseEntity
                .created(location).body(created);
    }


    @PostMapping
    @Operation(summary = "Créer un nouvel événement",
            description = "Crée un nouvel événement en fonction des données fournies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Événement créé avec succès", content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies pour la création de l'événement", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<EventDto> createEvent(
            @Parameter(description = "Données de l'événement à créer", required = true)
            @RequestBody @Valid EventCreateDto dto) {
        EventDto created = eventService.createEvent(dto);
        URI location = URI.create("/api/events/" + created.getId());
        return ResponseEntity
                .created(location).body(created);
    }

    /* ---------- UPDATE ---------- */

    /** PUT /api/events/{id} */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un événement existant",
            description = "Met à jour un événement existant en fonction de l'ID et des données fournies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Événement mis à jour avec succès", content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé pour l'ID fourni", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies pour la mise à jour", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<EventDto> updateEvent(
            @Parameter(description = "ID de l'événement à mettre à jour", required = true, example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @PathVariable @Min(1) Long id,
            @Parameter(description = "Données mises à jour de l'événement", required = true)
            @RequestBody @Valid EventUpdateDto dto) {
        EventDto updated = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    /** PATCH /api/events/{id} */
    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour un événement existant de manière partielle",
            description = "Met à jour un événement existant en fonction de l'ID et des données fournies. " +
                    "Seuls les champs non nuls du DTO seront mis à jour.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Événement mis à jour avec succès", content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé pour l'ID fourni", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies pour la mise à jour", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<EventDto> patchEvent(
            @Parameter(description = "ID de l'événement à mettre à jour", required = true, example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @PathVariable @Min(1) Long id,
            @Parameter(description = "Données mises à jour de l'événement", required = true)
            @RequestBody @Valid EventPatchDto dto) {
        EventDto updated = eventService.patchEvent(id, dto);
        return ResponseEntity.ok(updated);
    }



    /* ---------- DELETE ---------- */

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un événement par son ID",
            description = "Supprime un événement spécifique en fonction de son ID. " +
                    "Si l'événement n'existe pas, une erreur 404 sera renvoyée avec un message d'erreur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Événement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé pour l'ID fourni", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)     // 204
    public void deleteEvent(
            @Parameter(description = "ID de l'événement à supprimer", required = true, example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @PathVariable @Min(1) Long id) {
        eventService.deleteEvent(id);
    }
}
