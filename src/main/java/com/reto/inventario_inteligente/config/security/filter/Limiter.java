package com.reto.inventario_inteligente.config.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.inventario_inteligente.service.security.UserServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Limiter implements Filter {
    @Autowired
    private UserServiceImpl userService;
    private static final int MAX_REQUEST = 10; // Límite de solicitudes por minuto
    private static final long TIME_WINDOW = 60 * 1000; // 1 minuto en milisegundos
    private Map<String, RequestInfo> requestCount = new ConcurrentHashMap<>();

    // Clase que guarda la información de las solicitudes
    private static class RequestInfo {
        long windowStart;
        AtomicInteger requestCount;
        RequestInfo(long windowStart) {
            this.windowStart = windowStart;
            this.requestCount = new AtomicInteger(0);
        }
    }
    private boolean isLoginEndpoint(HttpServletRequest request) {
        System.out.println("REQUEST URI: " + request.getRequestURI());
        return "/api/auth/authenticate".equals(request.getRequestURI());
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String clientIP = ((HttpServletRequest) servletRequest).getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // Obtener información de solicitudes previas del cliente
        RequestInfo requestInfo = requestCount.get(clientIP);

        if (requestInfo == null || currentTime - requestInfo.windowStart > TIME_WINDOW) {
            // Si no hay registros o la ventana de tiempo ha expirado, reinicia
            requestInfo = new RequestInfo(currentTime);
            requestCount.put(clientIP, requestInfo);
        }

        // Verifica el número de solicitudes
        int currentCount = requestInfo.requestCount.incrementAndGet();

            if (isLoginEndpoint(httpRequest)) {
                if (currentCount > MAX_REQUEST) {
                    // Si se excede el límite, responde con el código 429
                    ((HttpServletResponse) servletResponse).setStatus(429);
                    servletResponse.getWriter().write("Demasiados intentos de inicio de sesión, tu cuenta ha sido bloqueada");
                        String username = null;
                        String password = null;

                        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
                            BufferedReader reader = servletRequest.getReader();
                            StringBuilder body = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                body.append(line);
                            }
                            String requestBody = body.toString();

                            // Extraer las credenciales de la solicitud JSON {"username": "usuario", "password": "contraseña"}
                            ObjectMapper objectMapper = new ObjectMapper();
                            //Map<String, String> jsonBody = objectMapper.readValue(requestBody, Map.class);
                            Map<String, String> jsonBody = objectMapper.readValue(requestBody, new TypeReference<Map<String, String>>() {});
                            username = jsonBody.get("username");
                            password = jsonBody.get("password");
                        }
                        if (username != null) {
                            userService.blockAccount(username);
                            System.out.println("CUENTA A BLOQUEAR: " + username);
                        }
                    return;
                }
            }
        // Pasar la solicitud al siguiente filtro en la cadena
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
