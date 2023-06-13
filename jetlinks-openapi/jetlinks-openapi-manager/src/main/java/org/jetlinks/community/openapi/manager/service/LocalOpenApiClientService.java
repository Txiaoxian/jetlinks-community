package org.jetlinks.community.openapi.manager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.rdb.mapping.defaults.SaveResult;
import org.hswebframework.web.crud.service.GenericReactiveCacheSupportCrudService;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.system.authorization.api.entity.UserEntity;
import org.hswebframework.web.system.authorization.api.service.reactive.ReactiveUserService;
import org.jetlinks.community.auth.entity.UserDetail;
import org.jetlinks.community.auth.service.OrganizationService;
import org.jetlinks.community.auth.service.RoleService;
import org.jetlinks.community.auth.service.UserDetailService;
import org.jetlinks.community.openapi.OpenApiClient;
import org.jetlinks.community.openapi.manager.entity.OpenApiClientEntity;
import org.jetlinks.community.openapi.manager.enums.DataStatus;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;


/**
 * @author Txiaoxian
 */
@Service
@Slf4j
@AllArgsConstructor
public class LocalOpenApiClientService extends GenericReactiveCacheSupportCrudService<OpenApiClientEntity, String> {


    private final ReactiveUserService userService;

    private final UserDetailService userDetailService;

    private final RoleService roleService;

    private final OrganizationService organizationService;


    @Override
    public String getCacheName() {
        return "open-api-client";
    }

    @Override
    public Mono<Integer> insert(Publisher<OpenApiClientEntity> entityPublisher) {
        return Flux
            .from(entityPublisher)
            .flatMap(this::doSyncUser)
            .reduce(Math::addExact);

    }

    @Override
    public Mono<SaveResult> save(Publisher<OpenApiClientEntity> entityPublisher) {
        return Flux
            .from(entityPublisher)
            .flatMap(this::doSyncUser)
            .reduce(Math::addExact)
            .map(i -> SaveResult.of(0, i));
    }

    protected Mono<Integer> doSyncUser(OpenApiClientEntity entity) {
        return findById(entity.getId())
            .flatMap(old -> super.updateById(old.getId(), Mono.just(entity)))
            .switchIfEmpty(Mono.defer(() -> userService
                .findByUsername(entity.getUsername())
                .flatMap(user -> Mono.error(new BusinessException("用户已存在,请勿重复添加!")))
                .switchIfEmpty(Mono.defer(() -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setName(entity.getClientName());
                    userEntity.setUsername(entity.getUsername());
                    userEntity.setPassword(entity.getPassword());
                    userEntity.setStatus(DataStatus.STATUS_ENABLED);
                    return userService
                        .saveUser(Mono.just(userEntity))
                        .doOnNext(b -> entity.setUserId(userEntity.getId()))
                        .then(super.save(Mono.just(entity)));
                })).thenReturn(1)));


    }

    public Mono<Integer> doSyncUser(OpenApiClient entity) {
        OpenApiClientEntity openApiClientEntity = new OpenApiClientEntity(entity);
        return findById(openApiClientEntity.getId())
            .flatMap(old -> super.updateById(old.getId(), Mono.just(openApiClientEntity))
                .then(roleService.bindUser(Collections.singleton(old.getUserId()), entity.getRoleIdList(), true))
                .then(organizationService.bindUser(Collections.singleton(old.getUserId()), entity.getOrgIdList(), true))
                .thenReturn(1))
            .switchIfEmpty(Mono.defer(() -> userService
                .findByUsername(entity.getAppId())
                .flatMap(user -> Mono.error(new BusinessException("用户已存在,请勿重复添加!")))
                .switchIfEmpty(Mono.defer(() -> {
                    UserDetail userDetail = new UserDetail();
                    userDetail.setName(entity.getAppId());
                    userDetail.setUsername(entity.getAppId());
                    userDetail.setPassword(entity.getSecureKey());
                    userDetail.setStatus(DataStatus.STATUS_ENABLED);
                    UserEntity userEntity = userDetail.toUserEntity();
                    return userService
                        .saveUser(Mono.just(userEntity))
                        .then(Mono.fromSupplier(userEntity::getId))
                        .flatMap(userId -> {
                            userDetail.setId(userId);
                            openApiClientEntity.setUserId(userId);
                            //保存详情
                            return
                                userDetailService.save(userDetail.toDetailEntity())
                                    //绑定角色
                                    .then(roleService.bindUser(Collections.singleton(userId), entity.getRoleIdList(), false))
                                    //绑定机构部门
                                    .then(organizationService.bindUser(Collections.singleton(userId), entity.getOrgIdList(), false))
                                    .then(super.save(Mono.just(openApiClientEntity)))
                                    .thenReturn(userId);
                        });
                })).thenReturn(1)));

    }

    public Mono<Integer> deleteByApplication(String applicationId) {

        return createQuery()
            .where()
            .and(OpenApiClientEntity::getApplicationId, applicationId)
            .fetchOne()
            .flatMap(o -> userService.deleteUser(o.getUserId())
                .then(super.deleteById(o.getId()))).thenReturn(1);
    }
}
