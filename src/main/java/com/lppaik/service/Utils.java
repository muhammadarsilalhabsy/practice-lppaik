package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.response.UserResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class Utils {

  private final Validator validator;

  @Autowired
  public Utils(Validator validator) {
    this.validator = validator;
  }

  public void validate(Object request){
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0){
      throw new ConstraintViolationException(constraintViolations);
    }
  }

  public UserResponse userToUserResponse(User user){
    String jurusan = Objects.nonNull(user.getJurusan()) ? user.getJurusan().getName() : null;
    String role = Objects.nonNull(user.getRole()) ? user.getRole().name() : null;
    String gender = Objects.nonNull(user.getGender()) ? user.getGender().name() : null;

    return UserResponse.builder()
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .email(user.getEmail())
            .name(user.getName())
            .jurusan(jurusan)
            .gender(gender)
            .role(role)
            .build();
  }

}
