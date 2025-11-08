package com.portal.service;

import com.portal.entity.General;
import com.portal.repo.GeneralRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralService {

    private final GeneralRepository generalRepository;

    public GeneralService(GeneralRepository generalRepository) {
        this.generalRepository = generalRepository;
    }

    public List<General> getAllGenerals() {
        return generalRepository.findAll();
    }

    public General createGeneral(General general) {
        return generalRepository.save(general);
    }

    public General getGeneralById(Long id) {
        return generalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + id));
    }
}