package com.controlfinance.modules.user.application.mapper;

import com.controlfinance.modules.user.application.dto.UserDto;
import com.controlfinance.modules.user.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
  UserDto toDto(User user);
}
