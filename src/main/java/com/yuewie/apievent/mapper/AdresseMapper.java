package com.yuewie.apievent.mapper;


import com.yuewie.apievent.dto.AdresseDto;
import com.yuewie.apievent.entity.Adresse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdresseMapper {
    AdresseDto toDto(Adresse adresse);
    Adresse toEntity(AdresseDto adresseDto);
}
