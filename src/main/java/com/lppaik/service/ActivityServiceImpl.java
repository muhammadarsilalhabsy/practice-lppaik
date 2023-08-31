package com.lppaik.service;

import com.lppaik.entity.Activity;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.request.*;
import com.lppaik.model.response.ActivityResponse;
import com.lppaik.repository.ActivityRepository;
import com.lppaik.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

  private final ActivityRepository activityRepository;
  private final Utils utils;

  private final ImageServiceImpl imageService;

  private final UserRepository userRepository;

  @Autowired
  public ActivityServiceImpl(ActivityRepository activityRepository, Utils utils, ImageServiceImpl imageService, UserRepository userRepository) {
    this.activityRepository = activityRepository;
    this.imageService = imageService;
    this.userRepository = userRepository;
    this.utils = utils;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ActivityResponse> searchActivity(SearchActivityRequest request) {

    Specification<Activity> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(request.getTitle())){
        predicates.add(builder.like(root.get("title"), "%"+ request.getTitle() +"%"));
      }

      // cek waktu dan lain sebagainya .... (TAMBAHKAN NANTI)

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<ActivityResponse> activityResponses = activities.getContent().stream()
            .map(activity -> utils.activityToActivityResponse(activity))
            .collect(Collectors.toList());

    return new PageImpl<>(activityResponses, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional
  public void create(User user, CreateActivityRequest request) throws IOException {

    utils.validate(request);

    if(user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Activity activity = new Activity();
    activity.setId(UUID.randomUUID().toString());
    activity.setTitle(request.getTitle());
    activity.setImage(imageService.saveImageToDb(request.getImage()));
    activity.setLink(request.getLink());
    activity.setLocation(request.getLocation());
    activity.setDescription(request.getDescription());

    activity.setTime(LocalTime.parse(request.getTime()));
    activity.setColor(request.getColor());
    activity.setDate(LocalDate.parse(request.getDate()));

    activityRepository.save(activity);
  }


  @Override
  @Transactional
  public void addActivityToMahasiswa(User user, AddActivityToUserRequest request) {

    utils.validate(request);

    if(user.getRole() != Role.KETUA && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    User mahasiswa = userRepository.findById(request.getToUserId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mahasiswa with id " + request.getToUserId() + " is not found!"));

    Activity currentActivity = activityRepository.findById(request.getActivityId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found!"));

    mahasiswa.getActivities().add(currentActivity);

    userRepository.save(mahasiswa);

  }

  @Override
  @Transactional
  public void update(User user, UpdateActivityRequest request) throws IOException {

    utils.validate(request);

    if(user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Activity currentActivity = activityRepository.findById(request.getActivityId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found!"));

    if(Objects.nonNull(request.getDescription())){
      currentActivity.setDescription(request.getDescription());
    }

    if(Objects.nonNull(request.getTitle())){
      currentActivity.setTitle(request.getTitle());
    }

    if(Objects.nonNull(request.getImage())){
      currentActivity.setImage(imageService.saveImageToDb(request.getImage()));
    }

    if(Objects.nonNull(request.getLink())){
      currentActivity.setLink(request.getLink());
    }

    if(Objects.nonNull(request.getLocation())){
      currentActivity.setLocation(request.getLocation());
    }

    if(Objects.nonNull(request.getColor())){
      currentActivity.setColor(request.getColor());
    }

    if(Objects.nonNull(request.getTime())){
      currentActivity.setTime(LocalTime.parse(request.getTime()));
    }

    if(Objects.nonNull(request.getDate())){
      currentActivity.setDate(LocalDate.parse(request.getDate()));
    }
    activityRepository.save(currentActivity);

  }

  @Override
  @Transactional
  public void delete(User user, String activityId) {

    if(user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Activity currentActivity = activityRepository.findById(activityId)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found!"));

    currentActivity.getUsers().forEach(u -> u.getActivities().remove(currentActivity));
    userRepository.saveAll(currentActivity.getUsers());


    activityRepository.delete(currentActivity);
  }
}
