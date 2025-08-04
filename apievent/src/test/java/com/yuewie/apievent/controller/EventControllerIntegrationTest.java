package com.yuewie.apievent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuewie.apievent.dto.AdresseRequestDto;
import com.yuewie.apievent.dto.EventCreateDto;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("POST /api/v1/events - Devrait créer un nouvel événement avec succès")
    void shouldCreateEvent_whenPostEvent() throws Exception {
        // Given: Un DTO pour créer un événement
        AdresseRequestDto adresseDto = AdresseRequestDto.builder()
                .intituleAdresse("10 Rue de la Paix")
                .codePostal("75002")
                .ville("Paris")
                .pays("France")
                .build();

        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setName("Lancement de Produit Tech");
        eventCreateDto.setDescription("Présentation du nouveau produit phare.");
        // Utilisation du format attendu par le DTO
        eventCreateDto.setStart(LocalDateTime.parse("15-10-2025 18:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        eventCreateDto.setEnd(LocalDateTime.parse("15-10-2025 22:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        eventCreateDto.setAdresses(Set.of(adresseDto));

        // When & Then: On exécute la requête POST et on vérifie les résultats
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isCreated()) // On attend un statut 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists()) // L'ID doit être généré
                .andExpect(jsonPath("$.name").value("Lancement de Produit Tech"))
                .andExpect(jsonPath("$.adresses[0].ville").value("Paris"));
    }

    @Test
    @DisplayName("GET /api/v1/events/search/querydsl - Devrait trouver un événement par son nom")
    void shouldFindEvent_whenSearchWithQueryDsl() throws Exception {
        // --- Étape 1: Créer un événement pour s'assurer qu'il y a des données à rechercher ---
        // Given: Un DTO pour créer l'événement de test
        AdresseRequestDto adresseDto = AdresseRequestDto.builder()
                .intituleAdresse("Champs de Mars")
                .codePostal("75007")
                .ville("Paris")
                .pays("France")
                .build();

        EventCreateDto eventToCreate = new EventCreateDto();
        eventToCreate.setName("Concert sous la Tour Eiffel");
        eventToCreate.setDescription("Un concert en plein air inoubliable.");
        eventToCreate.setStart(LocalDateTime.parse("20-08-2025 20:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        eventToCreate.setEnd(LocalDateTime.parse("20-08-2025 23:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        eventToCreate.setAdresses(Set.of(adresseDto));

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
}
