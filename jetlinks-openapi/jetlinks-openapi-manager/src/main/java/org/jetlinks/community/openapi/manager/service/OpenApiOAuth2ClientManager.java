package org.jetlinks.community.openapi.manager.service;

import lombok.AllArgsConstructor;
import org.hswebframework.web.oauth2.server.OAuth2Client;
import org.hswebframework.web.oauth2.server.OAuth2ClientManager;
import org.jetlinks.community.openapi.manager.entity.OpenApiClientEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class OpenApiOAuth2ClientManager implements OAuth2ClientManager {

    private final LocalOpenApiClientService clientService;


    @Override
    public Mono<OAuth2Client> getClient(String clientId) {

        return clientService
            .findById(clientId)
            .filter(OpenApiClientEntity::clientIsEnableOAuth2)
            .map(OpenApiClientEntity::toOAuth2Client);
    }
}
