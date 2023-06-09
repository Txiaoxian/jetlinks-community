package org.jetlinks.community.openapi.manager.service.request;

import lombok.Getter;
import lombok.Setter;
import org.jetlinks.community.openapi.manager.entity.OpenApiClientEntity;

@Getter
@Setter
public class BindUserOpenApiClientRequest extends OpenApiClientEntity {

    private String password;

}
