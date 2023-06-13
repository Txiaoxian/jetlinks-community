package org.jetlinks.community.openapi.manager.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.ReactiveAuthenticationManager;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.authorization.annotation.SaveAction;
import org.jetlinks.community.auth.entity.UserDetail;
import org.jetlinks.community.openapi.manager.entity.Application;
import org.jetlinks.community.openapi.manager.entity.ApplicationEntity;
import org.jetlinks.community.openapi.manager.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
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


    private ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/_query")
    @QueryAction
    @Operation(summary = "获取应用管理分页信息")
    public Mono<PagerResult<Application>> queryPage(QueryParamEntity query) {

        return applicationService.queryPage(query);
    }

    @GetMapping("/{applicationId}")
    @QueryAction
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
    public Mono<Authentication> getOperations() {

        return authenticationManager.getByUserId("522fe1987753af5dde07a10f26acf1d5");
    }

    @PostMapping
    @SaveAction
    @Operation(
        summary = "新增单个数据,并返回新增后的数据."
    )
    public Mono<Application> add(@RequestBody Mono<Application> payload) {
        return applicationService.saveApplication(payload);
    }

    @PutMapping("/{applicationId}")
    @SaveAction
    @Operation(summary = "根据id编辑应用管理信息")
    public Mono<Integer> updateApplication(@PathVariable String applicationId, @RequestBody Mono<Application> payload) {
        return applicationService.updateApplication(applicationId, payload);
    }

    @DeleteMapping("/{applicationId}")
    @SaveAction
    @Operation(summary = "根据id删除应用管理信息")
    public Mono<Integer> deleteApplication(@PathVariable String applicationId) {
        return applicationService
            .deleteApplication(applicationId);
    }
}
