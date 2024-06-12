package com.example.demo.service.Impl;

import com.example.demo.models.ModelA;
import com.example.demo.models.ModelB;
import com.example.demo.service.IAesService;
import org.springframework.stereotype.Service;

@Service
public class AesService implements IAesService {
    @Override
    public void Decrypt(ModelA body) {

    }

    @Override
    public ModelB Encrypt() {
        return null;
    }
}
