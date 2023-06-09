package org.jetlinks.community.openapi.manager.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.authorization.annotation.SaveAction;
import org.jetlinks.community.auth.entity.UserDetail;
import org.jetlinks.community.openapi.manager.entity.Application;
import org.jetlinks.community.openapi.manager.service.ApplicationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@RestController
@AllArgsConstructor
@Authorize(ignore = true)
@Slf4j
@RequestMapping("/application")
@Resource(id = "application", name = "应用管理")
@Tag(name = "应用管理")
public class ApplicationController {

    public final ApplicationService applicationService;


    @PostMapping("/_query")
    public Mono<PagerResult<Application>> queryPage(QueryParamEntity query) {
        return applicationService.queryPage(query);
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "获取应用管理详情信息")
    public Mono<Application> getApplication(@PathVariable String applicationId) {
        return applicationService
            .findById(applicationId)
            .flatMap(result -> applicationService
                .convertApplication(result)
                .map(application -> Tuples.of(result, application)))
            .map(Tuple2::getT2);
    }

    @GetMapping("/operations")
    @Operation(summary = "获取应用管理详情信息")
    public Mono<Application> getOperations() {
        return null;
    }

    @PostMapping
    @SaveAction
    @Operation(
        summary = "新增单个数据,并返回新增后的数据."
    )
    public Mono<Application> add(@RequestBody Mono<Application> payload) {
        return applicationService.saveApplication(payload);
    }
}
