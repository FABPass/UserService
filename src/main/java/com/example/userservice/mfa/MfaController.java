package com.example.userservice.mfa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "mfa")
public class MfaController {

    private final MfaService mfaService;

    @Autowired
    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping(path = "/all")
    public List<Mfa> getMfas(){
        return mfaService.getMfas();
    }

    @GetMapping()
    public Mfa getMfaById(@RequestParam(value = "id") Long id){
        return mfaService.getMfaById(id);
    }

    @PutMapping()
    public Mfa updateMfa(@RequestBody Mfa mfa){
        return mfaService.updateMfa(mfa);
    }
}
