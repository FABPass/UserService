package com.example.userservice.mfa;

import com.example.userservice.exception.ApiRequestException;
import com.example.userservice.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Mfa getMfaById(Long id) {
        Optional<Mfa> mfaById = mfaRepository.findMfaById(id);
        if(!mfaById.isPresent()) throw new ApiRequestException("There is no mfa with id: "+ id, HttpStatus.BAD_REQUEST);

        return mfaById.get();
    }


    public Mfa updateMfa(Mfa mfa) {
        Optional<Mfa> mf = mfaRepository.findMfaById(mfa.getId())
                .map(m -> {
                    m.setType(mfa.getType());
                    m.setSecret(mfa.getSecret());
                    m.setEnabled(mfa.isEnabled());
                    return mfaRepository.save(m);
                });
        if(mf.isPresent()) return mf.get();
        else throw new ApiRequestException("There is no mfa with id: "+ mfa.getId(), HttpStatus.BAD_REQUEST);
    }
}
