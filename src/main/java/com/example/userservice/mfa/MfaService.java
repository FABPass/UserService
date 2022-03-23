package com.example.userservice.mfa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MfaService {

    private final MfaRepository mfaRepository;

    @Autowired
    public MfaService(MfaRepository mfaRepository) {
        this.mfaRepository = mfaRepository;
    }

    public List<Mfa> getMfas(){
        return mfaRepository.findAll();
    }
}
