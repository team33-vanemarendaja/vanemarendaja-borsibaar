package com.borsibaar.mapper;

import com.borsibaar.dto.MeResponseDto;
import com.borsibaar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "needsOnboarding", expression = "java(user.getOrganizationId() == null)")
    MeResponseDto toMeResponse(User user);}