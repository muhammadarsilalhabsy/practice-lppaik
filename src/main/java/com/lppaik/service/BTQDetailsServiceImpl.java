package com.lppaik.service;

import com.lppaik.entity.BTQControlBook;
import com.lppaik.entity.BTQDetails;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.UpdateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.repository.BTQControlBookRepository;
import com.lppaik.repository.BTQDetailsRepository;
import com.lppaik.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BTQDetailsServiceImpl implements BTQDetailsService {

  private final BTQControlBookRepository bookRepository;

  private final BTQDetailsRepository detailsRepository;

  private final UserRepository userRepository;

  private final Utils utils;


  @Autowired
  public BTQDetailsServiceImpl(BTQControlBookRepository bookRepository, BTQDetailsRepository detailsRepository, UserRepository userRepository, Utils utils) {
    this.bookRepository = bookRepository;
    this.detailsRepository = detailsRepository;
    this.userRepository = userRepository;
    this.utils = utils;
  }

  @Override
  @Transactional(readOnly = true)
  public List<BTQResponse> getDetails(User user, String bookId) {

    BTQControlBook book = bookRepository.findFirstByUserAndId(user, bookId)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    List<BTQDetails> details = detailsRepository.findAllByBook(book);

    return details.stream()
            .map(detail -> utils.detailToBTQResponse(detail))
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void createForMahasiswa(User user, CreateBTQDetailsRequest request, String forMahasiswaId) {

    utils.validate(request);

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    // check mahasiswa tujuan
    User mahasiswa = userRepository.findById(forMahasiswaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mahasiswa with id " + forMahasiswaId + " is not found!"));

    // check buku kontrolnya
    BTQControlBook book = bookRepository.findFirstByUserAndId(mahasiswa, request.getBookId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    BTQDetails detail = new BTQDetails();

    detail.setBook(book);
    detail.setDay(request.getDay());
    detail.setTutors(user);
    detail.setActivity(request.getActivity());

    detailsRepository.save(detail);
//    utils.detailToBTQResponse(detail);
  }

  @Override
  @Transactional
  public void delete(User user, Long detailId) {

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    BTQDetails detail = detailsRepository.findById(detailId)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ detail not found!"));

    detailsRepository.delete(detail);
  }

  @Override
  @Transactional
  public BTQResponse update(User user, UpdateBTQDetailsRequest request) {

    utils.validate(request);

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    BTQDetails detail = detailsRepository.findById(request.getDetailId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ detail not found!"));

    if(Objects.nonNull(request.getActivity())){
      detail.setActivity(request.getActivity());
    }

    if(Objects.nonNull(request.getDay())){
      detail.setDay(request.getDay());
    }

    detail.setTutors(user);

    detailsRepository.save(detail);

    return utils.detailToBTQResponse(detail);
  }
}
