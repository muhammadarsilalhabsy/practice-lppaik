package com.lppaik.basic;

import com.lppaik.entity.*;
import com.lppaik.repository.BTQControlBookRepository;
import com.lppaik.repository.BTQDetailsRepository;
import com.lppaik.repository.JurusanRepository;
import com.lppaik.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterUserRequestTest {


  private final UserRepository userRepository;
  private final JurusanRepository jurusanRepository;
  private final BTQControlBookRepository controlBookRepository;
  private final BTQDetailsRepository detailsRepository;

  @Autowired
  public RegisterUserRequestTest(UserRepository userRepository,
                                 JurusanRepository jurusanRepository,
                                 BTQControlBookRepository controlBookRepository,
                                 BTQDetailsRepository detailsRepository) {
    this.userRepository = userRepository;
    this.jurusanRepository = jurusanRepository;
    this.controlBookRepository = controlBookRepository;
    this.detailsRepository = detailsRepository;
  }

  @Test
  void testAddUser() {
    Jurusan jurusan = jurusanRepository.findById("J0003").orElse(null);

    BTQControlBook book = new BTQControlBook();
    book.setStatus(true);
    book.setId("21916062");
    controlBookRepository.save(book);

    BTQDetails details = new BTQDetails();
    details.setId("xx01");
    details.setActivity("Makan");
    details.setDay("Kamis");
    details.setBooks(book);
    detailsRepository.save(details);

    BTQDetails detail1 = new BTQDetails();
    detail1.setId("xx01");
    detail1.setActivity("Minum");
    detail1.setDay("Jumat");
    detail1.setBooks(book);
    detailsRepository.save(detail1);

    User user = new User();
    user.setUsername("21916062");
    user.setName("jamal");
    user.setEmail("jamal@gmail.com");
    user.setPassword("rahasia");
    user.setRole(Role.MAHASISWA);
    user.setJurusan(jurusan);
    userRepository.save(user);


  }
}
