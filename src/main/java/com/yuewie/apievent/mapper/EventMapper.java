package com.yuewie.apievent.mapper;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.entity.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LienEventAdresseMapper.class})
public interface EventMapper {
    @Mapping(target = "adresses", source = "liens")
    @Mapping(target = "adressePrincipale", ignore = true)
    EventDto toDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "liens", source = "adresses")
    Event toEntity(EventCreateDto eventCreateDto);

    @Mapping(target = "liens", ignore = true)
    @Mapping(target = "id", ignore = true)
    Event toEntity(EventUpdateDto eventUpdateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "liens", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(EventPatchDto dto, @MappingTarget Event entity);

    EventCreatedKafkaPayload toKafkaPayload(EventDto eventDto);

    @AfterMapping
    default void setEventInLiens(@MappingTarget Event event) {
        if (event.getLiens() != null) {
            event.getLiens().forEach(lien -> lien.setEvent(event));
        }
    }

    @AfterMapping
    default void getAdressePrincipale(@MappingTarget EventDto dto) {
        if (dto.getAdresses() == null || dto.getAdresses().isEmpty()) {
            return;
        }

        // On cherche dans la liste des DTOs
        AdresseDto principale = dto.getAdresses().stream()
                .filter(LienEventAdresseDto::isPrincipal) // On vérifie le booléen du DTO
                .findFirst()
                .map(LienEventAdresseDto::getAdresse)     // On récupère l'AdresseDto DÉJÀ convertie !
                .orElseGet(() -> dto.getAdresses().stream().findFirst().get().getAdresse());

        dto.setAdressePrincipale(principale);
    }

}
