package com.yuewie.apievent.mapper;


import com.yuewie.apievent.dto.AdresseDto;
import com.yuewie.apievent.dto.AdresseRequestDto;
import com.yuewie.apievent.entity.Adresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdresseMapper {
    AdresseDto toDto(Adresse adresse);

    @Mapping(target = "id", ignore = true)
    Adresse toEntity(AdresseRequestDto adresseRequestDto);
}
