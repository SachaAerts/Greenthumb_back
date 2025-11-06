package com.GreenThumb.api.user.domain.service;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private static final Argon2 argon2 = Argon2Factory.create();

    public static String hash(String plainPassword) {
        char[] passwordChars = plainPassword.toCharArray();

        try {
            return argon2.hash(2, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    public static boolean verify(String hash, String password) {
        char[] passwordChars = password.toCharArray();
        try {
            return argon2.verify(hash, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}
