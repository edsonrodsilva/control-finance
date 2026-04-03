package com.controlfinance.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Jws<Claims> jws = jwtService.parseAccess(token);
        String userId = jws.getBody().getSubject();
        String role = String.valueOf(jws.getBody().get("role"));
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        log.debug("invalid_jwt path={} msg={}", request.getRequestURI(), e.getMessage());
        String path = request.getRequestURI();
        if (isPublicRoute(path)) {
          log.debug("invalid_jwt_ignored_on_public_route path={}", path);
        } else {
          sendUnauthorizedError(response, request);
          return;
        }
      }
    }

    chain.doFilter(request, response);
  }

  private boolean isPublicRoute(String path) {
    return path.startsWith("/api/v1/auth/") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.equals("/actuator/health");
  }

  private void sendUnauthorizedError(HttpServletResponse response, HttpServletRequest request) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("timestamp", Instant.now().toString());
    errorBody.put("status", 401);
    errorBody.put("error", "Unauthorized");
    errorBody.put("code", "UNAUTHORIZED");
    errorBody.put("message", "Invalid or expired token");
    errorBody.put("path", request.getRequestURI());

    response.getWriter().write(objectMapper.writeValueAsString(errorBody));
  }
}
