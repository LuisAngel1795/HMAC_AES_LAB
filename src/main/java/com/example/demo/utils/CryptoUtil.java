package com.example.demo.utils;

import com.example.demo.models.Llaves;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import static com.example.demo.constants.Llavesconstants.SECRET_KEY;

@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String ENCRYPTION_KEY = "YourEncryptionKey";

    public Llaves getLlaves(String idAcceso) {
        return null;
    }

    /*public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM));
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }*/

    public static <T> T decrypt(T requestVo, Llaves accesos) {
        String accesoSimetrico = accesos.getAccesoSimetrico();
        String codigoAutentificacionHash = accesos.getCodigoAutentificacionHash();
        DecryptKeys decryptKeys = new DecryptKeys(accesoSimetrico, codigoAutentificacionHash);

        try {
            loopVOPropertiesDecrypt(requestVo, decryptKeys);
            return requestVo;
        } catch (Exception var6) {
            throw new DecryptException(var6.getMessage());
        }
    }

    private static <T> void loopVOPropertiesDecrypt(T vo, DecryptKeys decryptKeys) throws Exception {
        if (null != vo) {
            Class<Object> clase = vo.getClass();
            Class<Object> parent = vo.getClass().getSuperclass();
            loopDecrypt(vo, clase, decryptKeys);
            loopDecrypt(vo, parent, decryptKeys);
        }
    }

    private static <T> void loopDecrypt(T vo, Class<Object> clase, DecryptKeys decryptKeys) throws Exception {
        Field[] campos = clase.getDeclaredFields();
        Field[] var4 = campos;
        int var5 = campos.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            if (field.isAnnotationPresent(CryptoInstance.class)) {
                loopVOPropertiesDecrypt(getInstance(field.getName(), vo, clase), decryptKeys);
            }

            if (field.isAnnotationPresent(Crypto.class)) {
                Crypto annotation = (Crypto)field.getAnnotation(Crypto.class);
                String algorithm = annotation.algorithm();
                if (Iterable.class.isAssignableFrom(field.getType())) {
                    List<?> campoIterable = (List)getInstance(field.getName(), vo, clase);
                    if (campoIterable != null && campoIterable.size() != 0) {
                        Iterator var18 = campoIterable.iterator();

                        while(var18.hasNext()) {
                            Object item = var18.next();
                            loopVOPropertiesDecrypt(item, decryptKeys);
                        }
                    }
                } else {
                    String nfii = field.getName();
                    String metodoDeclarado = createGetMethod(nfii);
                    String setMetodo = createSetMethod(nfii);
                    Method mtGet = clase.getDeclaredMethod(metodoDeclarado);
                    String datoCifrado = (String)mtGet.invoke(vo);
                    if (null != datoCifrado) {
                        String datoClaro = DecryptAESLib.decryptAES(decryptKeys.getAccesoSimetrico(), decryptKeys.getCodigoAutentificacionHash(), datoCifrado);
                        Method mtSet = clase.getDeclaredMethod(setMetodo, String.class);
                        mtSet.invoke(vo, datoClaro);
                    }
                }
            }
        }

    }
}
