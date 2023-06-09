package org.jetlinks.community.openapi.manager.service;

import lombok.AllArgsConstructor;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.DefaultDimensionType;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.system.authorization.api.entity.DimensionUserEntity;
import org.hswebframework.web.system.authorization.defaults.service.DefaultDimensionUserService;
import org.jetlinks.community.openapi.manager.entity.Application;
import org.jetlinks.community.openapi.manager.entity.ApplicationEntity;
import org.jetlinks.community.openapi.manager.entity.OpenApiClient;
import org.jetlinks.community.openapi.manager.entity.OpenApiClientEntity;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Comparator;

@Service
@AllArgsConstructor
public class ApplicationService extends GenericReactiveCrudService<ApplicationEntity, String> {

    private final LocalOpenApiClientService localOpenApiClientService;

    private final DefaultDimensionUserService dimensionUserService;

    public Mono<PagerResult<Application>> queryPage(QueryParamEntity query) {

        return this
            .queryPager(query)
            .flatMap(result -> Flux
                .fromIterable(result.getData())
                .index()
                .flatMap(tp2 -> this
                    .convertApplication(tp2.getT2())
                    .map(application -> Tuples.of(tp2.getT1(), application)))
                .sort(Comparator.comparing(Tuple2::getT1))
                .map(Tuple2::getT2)
                .collectList()
                .map(application -> PagerResult.of(result.getTotal(), application, query)));

    }

    public Mono<Application> convertApplication(ApplicationEntity entity) {
        return localOpenApiClientService
            .createQuery()
            .where(OpenApiClientEntity::getApplicationId, entity.getId())
            .fetchOne()
            .flatMap(openApiClientEntity -> this.convertOpenApiClient(OpenApiClient.of(openApiClientEntity)))
            .map(openApiClient -> {
                Application application = Application.of(entity);
                application.setApiServer(openApiClient);
                return application;
            });
    }

    private Mono<OpenApiClient> convertOpenApiClient(OpenApiClient openApiClient) {
        return dimensionUserService
            .createQuery()
            .where()
            .and(DimensionUserEntity::getUserId, openApiClient.getAppId())
            .and(DimensionUserEntity::getDimensionTypeId, DefaultDimensionType.role.getId())
            .fetch()
            .map(dimensionUserEntities -> openApiClient.addRoleIdList(dimensionUserEntities.getDimensionId()))
            .then(Mono.just(openApiClient));
    }

    public Mono<Application> saveApplication(Publisher<Application> applicationPublisher) {
        return Mono
            .from(applicationPublisher)
            .flatMap(this::doSyncApplication);
    }

    protected Mono<Application> doSyncApplication(Application entity) {
        ApplicationEntity applicationEntity = entity.toApplicationEntity();

        return findById(applicationEntity.getId() == null ? "" : applicationEntity.getId())
            .flatMap(old -> super.updateById(old.getId(), Mono.just(entity.toApplicationEntity())))
            .switchIfEmpty(Mono.defer(() -> super.save(Mono.just(applicationEntity))
                .then(Mono.fromSupplier(applicationEntity::getId))
                .flatMap(applicationid -> {
                    OpenApiClient openApiClientEntity = entity.getApiServer();
                    openApiClientEntity.setApplicationId(applicationid);
                    return localOpenApiClientService.doSyncUser(openApiClientEntity);
                })
                .thenReturn(1)))
            .thenReturn(entity);
    }
}
