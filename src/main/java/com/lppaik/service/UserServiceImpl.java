package com.lppaik.service;

import com.lppaik.entity.*;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.SearchUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.model.response.UserActivityResponse;
import com.lppaik.model.response.UserResponse;
import com.lppaik.repository.BTQControlBookRepository;
import com.lppaik.repository.BTQDetailsRepository;
import com.lppaik.repository.UserRepository;
import com.lppaik.security.BCrypt;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final Utils utils;
  private final JurusanServiceImpl jurusanService;
  private final BTQDetailsRepository detailsRepository;

  private final BTQControlBookRepository bookRepository;

  private final ImageServiceImpl imageService;

  public UserServiceImpl(UserRepository userRepository, Utils utils, JurusanServiceImpl jurusanService, BTQDetailsRepository detailsRepository, BTQControlBookRepository bookRepository, ImageServiceImpl imageService) {
    this.userRepository = userRepository;
    this.utils = utils;
    this.jurusanService = jurusanService;
    this.detailsRepository = detailsRepository;
    this.bookRepository = bookRepository;
    this.imageService = imageService;
  }

  @Override
  @Transactional
  public void register(RegisterUserRequest request) {
    utils.validate(request);

    if(userRepository.existsById(request.getUsername())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already registered!");
    }

    User user = new User();
    user.setRole(Role.MAHASISWA);
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setUsername(request.getUsername());
    user.setJurusan(jurusanService.getJurusanById(request.getJurusanId()));

    if(Objects.nonNull(request.getGender())){
      user.setGender(Gender.valueOf(request.getGender()));
    }
    userRepository.save(user);

    BTQControlBook book = new BTQControlBook();
    book.setId(request.getUsername());
    book.setStatus(false);

    bookRepository.save(book);

  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getCurrentUser(User user) {
    return utils.userToUserResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> searchUser(User user, SearchUserRequest request) {

    if (user.getRole() != Role.TUTOR
            && user.getRole() != Role.ADMIN
            && user.getRole() != Role.DOSEN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Specification<User> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(request.getIdentity())){
        predicates.add(builder.or(
                builder.equal(root.get("username"), request.getIdentity()),
                builder.like(root.get("name"), "%" + request.getIdentity() + "%")
                )
        );
      }

      if (Objects.nonNull(request.getJurusan())){
        Join<User, Jurusan> jurusan = root.join("jurusan");
        predicates.add(builder.equal(jurusan.get("name"), request.getJurusan()));
      }

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<User> users = userRepository.findAll(specification, pageable);
    List<UserResponse> userResponses = users.getContent().stream()
            .map(person -> utils.userToUserResponse(person))
            .collect(Collectors.toList());

    return new PageImpl<>(userResponses, pageable, users.getTotalElements());
  }

  @Override
  @Transactional
  public UserResponse updateCurrentUser(User user, UpdateUserRequest request) {

    if(Objects.nonNull(request.getName())){
      user.setName(request.getName());
    }

    if(Objects.nonNull(request.getPassword())){
      user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    }

    if(Objects.nonNull(request.getEmail())){
      user.setEmail(request.getEmail());
    }

    if(Objects.nonNull(request.getGender())){
      user.setGender(Gender.valueOf(request.getGender()));
    }

    return utils.userToUserResponse(user);
  }

  @Override
  public List<UserActivityResponse> getUserActivities(User user) {

    Set<Activity> activities = user.getActivities();

    return activities.stream()
            .map(activity -> UserActivityResponse.builder()
                    .image(activity.getImage())
                    .title(activity.getTitle())
                    .build())
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<BTQResponse> getBTQDetails(User user) {

    BTQControlBook book = bookRepository.findFirstByUserAndId(user, user.getUsername())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    List<BTQDetails> details = detailsRepository.findAllByBook(book);

    return details.stream()
            .map(detail -> utils.detailToBTQResponse(detail))
            .collect(Collectors.toList());
  }

  @Override
  public void updateUserAvatar(User user, MultipartFile file) throws IOException {
    user.setAvatar(imageService.saveImageToDb(file));
    userRepository.save(user);
  }
}
