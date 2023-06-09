package org.jetlinks.community.openapi.manager.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.community.openapi.manager.entity.OpenApiClientEntity;
import org.jetlinks.community.openapi.manager.service.LocalOpenApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/open-api")
@Resource(id = "open-api", name = "openApi客户端")
@Tag(name = "OpenAPI客户端管理")
public class OpenApiClientController implements ReactiveServiceCrudController<OpenApiClientEntity, String> {

    @Autowired
    public LocalOpenApiClientService openApiClientService;

    @Override
    public ReactiveCrudService<OpenApiClientEntity, String> getService() {
        return openApiClientService;
    }

}
