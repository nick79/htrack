package com.nik.htrack.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nik.htrack.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final int ACCESS_TOKEN_VALIDITY = 10 * 60 * 1000;
    public static final int REFRESH_TOKEN_VALIDITY = 30 * 60 * 1000;
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
        throws AuthenticationException {
        String email = request.getParameter("email");
        log.debug("Email is: {}", email);
        String password = request.getParameter("password");
        log.debug("Password is: {}", password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                                                                                                          password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain chain,
        final Authentication authentication) throws IOException, ServletException {

        User user = (User) authentication.getPrincipal();
        Algorithm algorithm = SecurityUtil.createAlgorithm();

        String access_token = createAccessToken(request, user, algorithm);

        String refresh_token = createRefreshToken(request, user, algorithm);

        Map<String, String> tokens = prepareResponse(response, access_token, refresh_token);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    private String createAccessToken(final HttpServletRequest request, final User user, final Algorithm algorithm) {
        return JWT
            .create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles",
                       user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm);
    }

    private String createRefreshToken(final HttpServletRequest request, final User user, final Algorithm algorithm) {
        return JWT
            .create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
            .withIssuer(request.getRequestURL().toString())
            .sign(algorithm);
    }

    private Map<String, String> prepareResponse(
        final HttpServletResponse response, final String access_token, final String refresh_token) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return tokens;
    }
}
