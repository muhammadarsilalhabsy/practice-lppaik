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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");

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

  public static byte[] compressImage(byte[] data) {
    Deflater deflater = new Deflater();

    deflater.setLevel(Deflater.BEST_COMPRESSION);
    deflater.setInput(data);
    deflater.finish();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] temp = new byte[4 * 1024];

    while (!deflater.finished()) {
      int size = deflater.deflate(temp);
      outputStream.write(temp, 0, size);
    }

    try {
      outputStream.close();
    } catch (Exception ignore) {
    }

    return outputStream.toByteArray();

  }

  public static byte[] decompressImage(byte[] data) {
    Inflater inflater = new Inflater();
    inflater.setInput(data);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] temp = new byte[4 * 1024];

    try {
      while (!inflater.finished()) {
        int count = inflater.inflate(temp);
        outputStream.write(temp, 0, count);
      }
      outputStream.close();
    } catch (Exception ignore) {
    }
    return outputStream.toByteArray();
  }

  public static String nameConversion(MultipartFile file){
    LocalDate date = LocalDate.now();
    LocalTime time = LocalTime.now();

    String tanggal = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String jam = time.format(DateTimeFormatter.ofPattern("HH-mm"));

    return tanggal + "-" + jam + "-" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s","-");
  }
}
