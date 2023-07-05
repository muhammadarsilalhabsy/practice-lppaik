package com.lppaik.service;

import com.lppaik.entity.*;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.SearchUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.UserResponse;
import com.lppaik.repository.BTQControlBookRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final Utils utils;
  private final JurusanServiceImpl jurusanService;

  private final BTQControlBookRepository bookRepository;

  public UserServiceImpl(UserRepository userRepository, Utils utils, JurusanServiceImpl jurusanService, BTQControlBookRepository bookRepository) {
    this.userRepository = userRepository;
    this.utils = utils;
    this.jurusanService = jurusanService;
    this.bookRepository = bookRepository;
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
  public Page<UserResponse> searchUser(SearchUserRequest request) {


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

    if(Objects.nonNull(request.getAvatar())){
      user.setAvatar(request.getAvatar());
    }

    if(Objects.nonNull(request.getGender())){
      user.setGender(Gender.valueOf(request.getGender()));
    }

    return utils.userToUserResponse(user);
  }


}
