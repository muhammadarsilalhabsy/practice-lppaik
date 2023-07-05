package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.response.TokenResponse;
import com.lppaik.repository.UserRepository;
import com.lppaik.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

  @Autowired
  private UserRepository repository;

  @Autowired
  private Utils utilsService;

  @Override
  @Transactional
  public TokenResponse login(LoginUserRequest request){
    utilsService.validate(request);

    User user = repository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This username maybe not exist"));

    if(BCrypt.checkpw(request.getPassword(), user.getPassword())){

      // sukses login
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(System.currentTimeMillis() + (36L * 1_00_000 * 24 * 30));
      repository.save(user);

      return TokenResponse.builder()
              .token(user.getToken())
              .expiredAt(user.getTokenExpiredAt())
              .build();
    }else{
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You send wrong password");
    }
  }

  @Override
  @Transactional
  public void logout(User user){
    user.setTokenExpiredAt(null);
    user.setToken(null);

    repository.save(user);
  }
}
