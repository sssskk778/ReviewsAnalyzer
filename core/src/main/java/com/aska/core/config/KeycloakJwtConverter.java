package com.aska.core.config;


import com.aska.core.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String username = source.getClaimAsString("preferred_username");
        if(username == null || username.isBlank()){
            throw new InvalidBearerTokenException("У токена нет username");
        }
        String email = username.trim().toLowerCase(Locale.ROOT);

        try{
            userService.ensureExists(email, source.getClaimAsString("name"));
        }catch (DataIntegrityViolationException ex){
            log.debug("Пользователь {} уже создан параллельным запросом", email);
        }
        return new JwtAuthenticationToken(source,extractRoles(source), email);
    }

    private List<SimpleGrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
