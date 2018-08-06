package com.thoughtworks.training.gateway.security;

import com.netflix.zuul.context.RequestContext;
import com.thoughtworks.training.gateway.client.UserClient;
import com.thoughtworks.training.gateway.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            getTokenFromRequest(request).ifPresent(token -> {
                User user = userClient.verifyToken(token);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList()
                        )
                );
                RequestContext.getCurrentContext().addZuulRequestHeader(
                        HttpHeaders.AUTHORIZATION, String.format("%s:%s", user.getId(), user.getName())
                );
            });
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, String.format("authentication failed: %s", e.getMessage()));
            return;
        }
        chain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorization) && authorization.startsWith(Constants.BEARER_TOKEN_PREFIX)) {
            return Optional.of(authorization.substring(Constants.BEARER_TOKEN_PREFIX.length()));
        }
        return Optional.empty();
    }

}
