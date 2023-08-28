package com.lppaik.service;

import com.lppaik.entity.Activity;
import com.lppaik.entity.BTQDetails;
import com.lppaik.entity.User;
import com.lppaik.model.response.ActivityResponse;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.model.response.UserResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

  public ActivityResponse activityToActivityResponse(Activity activity){
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd LLLL yyy");
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_TIME;

    String date = activity.getDate().format(dateFormatter);
    String time = activity.getTime().format(timeFormatter);
    String day = activity.getDate().format(dayFormatter);


    return ActivityResponse.builder()
            .id(activity.getId())
            .link(activity.getLink())
            .time(time)
            .date(date)
            .day(day)
            .color(activity.getColor())
            .title(activity.getTitle())
            .image(activity.getImage())
            .location(activity.getLocation())
            .description(activity.getDescription())
            .build();
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

  public BTQResponse detailToBTQResponse(BTQDetails detail){
    return BTQResponse.builder()
            .id(detail.getId())
            .day(detail.getDay())
            .activity(detail.getActivity())
            .tutor(detail.getTutors().getName())
            .build();
  }
}
