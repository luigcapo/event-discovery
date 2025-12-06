package com.yuewie.apievent.mapper;

import com.yuewie.apievent.dto.AdresseDto;
import com.yuewie.apievent.dto.AdresseRequestDto;
import com.yuewie.apievent.dto.LienEventAdresseDto;
import com.yuewie.apievent.dto.LienEventAdresseRequestDto;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.LienEventAdresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface LienEventAdresseMapper {
    LienEventAdresseDto toDto(LienEventAdresse lienEventAdresse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "isPrincipal", source = "principal")
    LienEventAdresse toEntity(LienEventAdresseRequestDto lienEventAdresseRequestDto);
}
