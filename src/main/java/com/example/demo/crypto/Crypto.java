package com.example.demo.crypto;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Crypto {
boolean DecryptBody() default false;
    boolean EncryptResponse() default false;
}
