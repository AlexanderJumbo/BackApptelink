package com.reto.inventario_inteligente.config.security.authorization;

import com.reto.inventario_inteligente.entity.security.Operation;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.repository.security.OperationRepository;
import com.reto.inventario_inteligente.service.security.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private UserService userService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
        HttpServletRequest request = requestContext.getRequest();
        String url = extractUrl(request);
        String httpMethod = request.getMethod();

        boolean isPublic = isPublic(url, httpMethod);
        //Si el endpoint es público no se necesitará jwt
        if (isPublic){
            return new AuthorizationDecision(true);
        }

        boolean isGranted = isGranted(url, httpMethod, authentication.get());
        return new AuthorizationDecision(isGranted);
    }

    private boolean isGranted(String url, String httpMethod, Authentication authentication) {

        if(authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)){
            throw new AuthenticationCredentialsNotFoundException("User not logged in");
        }

        List<Operation> operations = obtainOperations(authentication);
        System.out.println("OPERATIONS: " + operations);
        boolean isGranted = operations.stream().anyMatch(getOperationPredicate(url, httpMethod));
        System.out.println("IS GRANTED " + isGranted);
        return isGranted;
    }
    //Obtener las operaciones de acuerdo al username
    private List<Operation> obtainOperations(Authentication authentication) {

        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        String username = (String) authToken.getPrincipal();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user not found. User: " + username));
        return user.getRole().getPermissions().stream()
                .map(grantedPermission -> grantedPermission.getOperation())
                .collect(Collectors.toList());
    }

    private boolean isPublic(String url, String httpMethod) {

        List<Operation> publicEndpoints = operationRepository.findPublicEndpoints();

        boolean isPublic = publicEndpoints.stream().anyMatch(getOperationPredicate(url, httpMethod));
        System.out.println("IS PUBLIC " + isPublic);
        return isPublic;
    }

    private Predicate<Operation> getOperationPredicate(String url, String httpMethod) {
        return operation -> {
            String basePath = operation.getModule().getBasePath();

            Pattern pattern = Pattern.compile(basePath.concat(operation.getPath()));
            Matcher matcher = pattern.matcher(url);

            return matcher.matches() && operation.getHttpMethod().equals(httpMethod);
        };
    }

    private String extractUrl(HttpServletRequest request) {

        String contextPath = request.getContextPath();
        String url = request.getRequestURI();
        url = url.replace(contextPath, "");

        System.out.println("URL: " + url);
        return url;
    }
}
