package com.nik.htrack.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;

public class SecurityUtil {
    @Value("${security.algorithm.secret}")
    private static String secret;

    public static Algorithm createAlgorithm() {
        return Algorithm.HMAC256(secret.getBytes());
    }
}
