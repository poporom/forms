package com.poisk.core.service;

import com.poisk.core.model.Image;
import com.poisk.core.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ImageService {

    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public Image findOne(Integer id) {
        return imageRepository.getById(id);
    }

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public Image findByUrl(String url) {
        return imageRepository.findByUrl(url);
    }
}
