package com.ironhack.MemeBank.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.Random;
import java.io.IOException;

public class SecretKey {
    private static Random rand = new Random((new Date()).getTime());

    public static SecretKeySpec getKeyFromPasswordWithSalt(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory     = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec       spec        = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKeySpec originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return originalKey;
    }

    public static SecretKeySpec getKeyFromPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory     = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec       spec        = new PBEKeySpec(password.toCharArray());
        SecretKeySpec originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return originalKey;
    }


}
