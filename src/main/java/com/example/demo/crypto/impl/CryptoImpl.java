package com.example.demo.crypto.impl;

import com.example.demo.crypto.Crypto;
import com.example.demo.models.Llaves;
import com.example.demo.utils.CryptoUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.List;

import static com.example.demo.constants.Llavesconstants.ID_ACCESO_HEADER;


@Aspect
@Component
public class CryptoImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoImpl.class);

    @Autowired
    private CryptoUtil cryptoUtil;


    @Around(value = "@annotation(com.example.demo.crypto.Crypto)")
    public Object ValidatorCripto(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Crypto anotacion = method.getAnnotation(Crypto.class);
        HttpHeaders headers = (HttpHeaders) args[0];
        Boolean body = anotacion.DecryptBody();
        Object result = joinPoint.proceed();
        Boolean encriptaResponse = anotacion.EncryptResponse();
        if (Boolean.TRUE.equals(body)) {
            if (Boolean.TRUE.equals(encriptaResponse)) {
                Llaves llaves = getLlaves(headers);
                encrypt(llaves, result);
            }
        }
        return result;
    }

    private void before(ProceedingJoinPoint joinPoint) throws Exception{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        HttpHeaders headers = (HttpHeaders) args[0];
        Crypto anotacion = method.getAnnotation(Crypto.class);
        Boolean desencriptaBody = anotacion.DecryptBody();
        Object body = args[1];
        if (Boolean.TRUE.equals(desencriptaBody)) {
            Llaves llaves = getLlaves(headers);
            decrypt(llaves, body);
        }

    }

    private Llaves getLlaves(HttpHeaders headers) throws Exception {
        Llaves llaves = null;
        validarIdAcceso(headers);
//        try {
            llaves = cryptoUtil.getLlaves(headers.getFirst("x-id-acceso"));

            if (ObjectUtils.isEmpty(llaves)) {
                LOGGER.error("IdAcceso no encontrado o expirado");
//                throw new (List.of("IdAcceso no encontrado o expirado"), EExceptionInfo.E401B);
            }
//        }
        /*catch (ApiException exception) {
            throw new ApiException(List.of("IdAcceso no encontrado o expirado"), EExceptionInfo.E401B);
        }*/
        return llaves;
    }

    private void decrypt(Llaves llaves, Object body) throws Exception{
        Llaves llavesEncypt = new Llaves();

        llavesEncypt.setAccesoSimetrico(llaves.getAccesoSimetrico());
        llavesEncypt.setCodigoAutentificacionHash(llaves.getCodigoAutentificacionHash());
        try {
            cryptoUtil.decrypt(body, llavesEncypt);
        }catch (Exception e) {
            throw new Exception("error al desencriptar la data");
        }
    }

    private void encrypt(Llaves llaves, Object body){
        Llaves llavesEncypt = new Llaves();
        llavesEncypt.setAccesoSimetrico(llaves.getAccesoSimetrico());
        llavesEncypt.setCodigoAutentificacionHash(llaves.getCodigoAutentificacionHash());
        cryptoUtil.encrypt(body, llavesEncypt);
    }

    private void validarIdAcceso(HttpHeaders headers) throws Exception {
        if (ObjectUtils.isEmpty(headers.getFirst(ID_ACCESO_HEADER))) {
            throw new Exception("error, no existe el idAcceso");
        }
    }
}
