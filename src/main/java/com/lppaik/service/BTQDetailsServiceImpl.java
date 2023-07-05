package com.lppaik.service;

import com.lppaik.entity.BTQControlBook;
import com.lppaik.entity.BTQDetails;
import com.lppaik.entity.User;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.repository.BTQControlBookRepository;
import com.lppaik.repository.BTQDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class BTQDetailsServiceImpl implements BTQDetailsService {

  private final BTQControlBookRepository bookRepository;

  private final BTQDetailsRepository detailsRepository;

  private final Utils utils;


  @Autowired
  public BTQDetailsServiceImpl(BTQControlBookRepository bookRepository, BTQDetailsRepository detailsRepository, Utils utils) {
    this.bookRepository = bookRepository;
    this.detailsRepository = detailsRepository;
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
  public BTQResponse create(User user, CreateBTQDetailsRequest request) {

    utils.validate(request);

    BTQControlBook book = bookRepository.findFirstByUserAndId(user, request.getBookId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    BTQDetails detail = new BTQDetails();
    detail.setBook(book);
    detail.setDay(request.getDay());
    detail.setActivity(request.getActivity());

    detailsRepository.save(detail);

    return utils.detailToBTQResponse(detail);
  }
}
