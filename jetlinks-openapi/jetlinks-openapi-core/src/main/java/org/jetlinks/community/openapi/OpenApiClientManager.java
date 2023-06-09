package org.jetlinks.community.openapi;

import reactor.core.publisher.Mono;

public interface OpenApiClientManager {

    Mono<OpenApiClient> getClient(String clientId);

}
