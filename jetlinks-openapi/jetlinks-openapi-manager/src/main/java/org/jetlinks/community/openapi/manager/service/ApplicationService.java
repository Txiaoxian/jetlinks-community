package org.jetlinks.community.openapi.manager.service;

import lombok.AllArgsConstructor;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.DefaultDimensionType;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.system.authorization.api.entity.DimensionUserEntity;
import org.hswebframework.web.system.authorization.defaults.service.DefaultDimensionUserService;
import org.jetlinks.community.auth.dimension.OrgDimensionType;
import org.jetlinks.community.openapi.OpenApiClient;
import org.jetlinks.community.openapi.manager.entity.Application;
import org.jetlinks.community.openapi.manager.entity.ApplicationEntity;
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


    public Mono<Application> saveApplication(Publisher<Application> applicationPublisher) {
        return Mono
            .from(applicationPublisher)
            .flatMap(this::doSyncApplication);
    }

    public Mono<Integer> updateApplication(String applicationId, Mono<Application> applicationPublisher) {
        return Mono.from(applicationPublisher)
            .flatMap(application ->
                application.getApiServer() == null
                    ? super.updateById(applicationId, application.toApplicationEntity())
                    : localOpenApiClientService.doSyncUser(application.getApiServer())
                    .then(super.updateById(applicationId, application.toApplicationEntity()))
            ).thenReturn(1);

    }

    public Mono<Integer> deleteApplication(String applicationId) {

        return findById(applicationId)
            .flatMap(old -> super.deleteById(applicationId)
                .flatMap(a -> localOpenApiClientService.deleteByApplication(applicationId)))
            .thenReturn(1);
    }

    public Mono<Application> convertApplication(ApplicationEntity entity) {
        return localOpenApiClientService
            .createQuery()
            .where(OpenApiClientEntity::getApplicationId, entity.getId())
            .fetchOne()
            .flatMap(openApiClientEntity -> this.convertOpenApiClient(openApiClientEntity)
                .map(openApiClient -> {
                    Application application = Application.of(entity);
                    application.setApiServer(openApiClient);
                    return application;
                }));
    }

    private Mono<OpenApiClient> convertOpenApiClient(OpenApiClientEntity entity) {
        OpenApiClient openApiClient = OpenApiClientEntity.of(entity);
        return dimensionUserService
            .createQuery()
            .where()
            .and(DimensionUserEntity::getUserId, entity.getUserId())
            .fetch()
            .map(dimensionUserEntities -> {
                if (dimensionUserEntities.getDimensionTypeId().equals(DefaultDimensionType.role.getId())) {
                    openApiClient.addRoleIdList(dimensionUserEntities.getDimensionId());
                } else if (dimensionUserEntities.getDimensionTypeId().equals(OrgDimensionType.org.getId())) {
                    openApiClient.addOrgIdList(dimensionUserEntities.getDimensionId());
                }
                return openApiClient;
            })
            .then(Mono.just(openApiClient));
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
