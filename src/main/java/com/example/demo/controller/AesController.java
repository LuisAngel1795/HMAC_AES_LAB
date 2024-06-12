package com.example.demo.controller;

import com.example.demo.crypto.Crypto;
import com.example.demo.models.ModelA;
import com.example.demo.models.ModelB;
import com.example.demo.service.Impl.AesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AesController {

    @Autowired
    private AesService service;





    @PostMapping("/testDecrypt")
    @Crypto(DecryptBody=true)
    @ResponseStatus(HttpStatus.OK)
    public void testDecrypt(@RequestHeader HttpHeaders headers,
    @RequestBody ModelA body){
        service.Decrypt(body);
    }


    @GetMapping("/testEncrypt")
    @Crypto(EncryptResponse = true)
    @ResponseStatus(HttpStatus.OK)
    public ModelB testEncript(@RequestHeader HttpHeaders headers){
        return service.Encrypt();
    }
}
