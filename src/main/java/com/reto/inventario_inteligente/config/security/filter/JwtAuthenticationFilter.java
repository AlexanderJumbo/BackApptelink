package com.reto.inventario_inteligente.config.security.filter;

import com.reto.inventario_inteligente.entity.security.JwtToken;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.repository.security.JwtTokenRepository;
import com.reto.inventario_inteligente.service.security.JwtService;
import com.reto.inventario_inteligente.service.security.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = jwtService.extractJwtFromRequest(request);
        if(/*jwt == null || */!StringUtils.hasText(jwt)){
            filterChain.doFilter(request, response);
            return;
        }

        Optional<JwtToken> token = jwtTokenRepository.findByToken(jwt);
        boolean isValid = validateToken(token);

        if(!isValid){
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUsername(jwt);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("Username not found. Username: " + username));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, null, user.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
    private boolean validateToken(Optional<JwtToken> tokenToCheck) {

        if(!tokenToCheck.isPresent()){
            System.out.println("El token no existe o nunca fue generado en nuestro sistema");
            return false;
        }

        JwtToken token = tokenToCheck.get();
        Date now = new Date(System.currentTimeMillis());
        boolean isValid = token.isValid() && token.getExpiration().after(now);

        if(!isValid){
            System.out.println("Token inválido");
            updateTokenStatus(token);
        }
        return isValid;
    }

    private void updateTokenStatus(JwtToken token) {
        token.setValid(false);
        jwtTokenRepository.save(token);
    }
}
