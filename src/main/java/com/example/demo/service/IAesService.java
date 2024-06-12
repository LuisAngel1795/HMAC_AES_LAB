package com.example.demo.service;


import com.example.demo.models.ModelA;
import com.example.demo.models.ModelB;

public interface IAesService {
    void Decrypt(ModelA body);
    ModelB Encrypt();
}
