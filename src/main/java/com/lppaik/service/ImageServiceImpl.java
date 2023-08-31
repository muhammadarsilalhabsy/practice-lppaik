package com.lppaik.service;

import com.lppaik.entity.Image;
import com.lppaik.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

  private final ImageRepository repository;

  @Autowired
  public ImageServiceImpl(ImageRepository repository) {
    this.repository = repository;
  }


  @Override
  public String saveImageToDb(MultipartFile file) throws IOException {

    repository.save(Image.builder()
            .name(Utils.nameConversion(file))
            .data(Utils.compressImage(file.getBytes()))
            .type(file.getContentType())
            .build());

    return Utils.nameConversion(file);
  }

  @Override
  public byte[] getImageFromDb(String name) {
    Optional<Image> image = repository.findByName(name);

    return Utils.decompressImage(image.get().getData());
  }
}
