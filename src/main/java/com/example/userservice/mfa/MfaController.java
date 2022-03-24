package com.example.userservice.mfa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "mfa")
public class MfaController {

    private final MfaService mfaService;

    @Autowired
    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping(path = "/mfas")
    public List<Mfa> getMfas(){
        return mfaService.getMfas();
    }

    @GetMapping(path = "/id")
    public Mfa getMfaById(@RequestParam(value = "id") Long id){
        return mfaService.getMfaById(id);
    }

    @PutMapping(path = "/update")
    public Mfa updateMfa(@RequestBody Mfa mfa){
        return mfaService.updateMfa(mfa);
    }
}
