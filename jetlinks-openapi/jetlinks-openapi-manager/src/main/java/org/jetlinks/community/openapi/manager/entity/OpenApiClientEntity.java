package org.jetlinks.community.openapi.manager.entity;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.DefaultValue;
import org.hswebframework.ezorm.rdb.mapping.annotation.EnumCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.hswebframework.web.crud.generator.Generators;
import org.hswebframework.web.oauth2.server.OAuth2Client;
import org.jetlinks.community.openapi.OpenApiClient;
import org.jetlinks.community.openapi.Signature;
import org.jetlinks.community.openapi.manager.enums.ApplicationState;
import org.jetlinks.community.openapi.manager.enums.DataStatusEnum;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;

@Getter
@Setter
@Table(name = "s_open_api_client")
public class OpenApiClientEntity extends GenericEntity<String> implements RecordCreationEntity {

    @Override
    @GeneratedValue(generator = Generators.SNOW_FLAKE)
    @Schema(description = "客户端ID")
    public String getId() {
        return super.getId();
    }

    //应用ID
    @Column(name = "application_id")
    @Hidden
    private String applicationId;

    //客户端ID
    @Column(name = "app_id")
    @Hidden
    private String appId;

    //客户端名称
    @Column(name = "client_name")
    @Schema(description = "名称")
    private String clientName;

    //密钥
    @Column(name = "secure_key")
    @NotBlank
    @Schema(description = "密钥")
    private String secureKey;

    //白名单IP地址
    @Column(name = "ip_white_list")
    @Schema(description = "IP白名单,多个用回车分隔")
    private String ipWhiteList;

    //签名方式
    @Column(name = "signature")
//    @ColumnType(javaType = String.class)
//    @EnumCodec
    @Schema(description = "签名方式,MD5或者SHA256")
    @DefaultValue("MD5")
    private String signature;

    //用户ID
    @Column(name = "user_id", updatable = false)
    @Schema(description = "用户ID")
    private String userId;

    //用户名
    @Column(name = "username", updatable = false)
    @Schema(description = "用户名")
    private String username;

    //密码
    @Schema(description = "密码(仅新增)")
    private String password;

    //描述
    @Column(name = "description")
    @Schema(description = "说明")
    private String description;

    //状态
    @Schema(description = "状态")
    @Column(length = 32, nullable = false)
    @EnumCodec
    @ColumnType(javaType = String.class)
    @NotBlank
    @DefaultValue("enabled")
    private ApplicationState status;

    //创建用户ID
    @Column(name = "creator_id")
    @Schema(description = "创建人")
    private String creatorId;

    //创建时间
    @Column(name = "create_time")
    @Schema(description = "创建时间")
    private Long createTime;

    @Column
    @Schema(description = "是否开启OAuth2")
    private Boolean enableOAuth2;

    @Column(length = 2048)
    private String redirectUrl;

    public boolean clientIsEnableOAuth2() {
        return Boolean.TRUE.equals(enableOAuth2);
    }

    public boolean statusIsEnabled() {
        return status == ApplicationState.enabled;
    }

    public OAuth2Client toOAuth2Client() {
        OAuth2Client client = new OAuth2Client();
        client.setClientId(getId());
        client.setClientSecret(secureKey);
        client.setUserId(userId);
        client.setRedirectUrl(redirectUrl);
        client.setName(clientName);
        return client;
    }

    public static OpenApiClient of(OpenApiClientEntity entity) {
        return new OpenApiClientEntity().with(entity);
    }

    public OpenApiClient with(OpenApiClientEntity entity) {
        OpenApiClient openApiClient = new OpenApiClient();
        openApiClient.setId(entity.getId());
        openApiClient.setAppId(entity.getAppId());
        openApiClient.setSecureKey(entity.getSecureKey());
        openApiClient.setSignature(Signature.valueOf(entity.getSignature().toUpperCase()));
        openApiClient.setIpWhiteList(entity.getIpWhiteList());
        openApiClient.setRedirectUrl(entity.getRedirectUrl());
        openApiClient.setEnableOAuth2(entity.getEnableOAuth2());
        return openApiClient;
    }

    public OpenApiClientEntity(OpenApiClient openApiClient) {
        this.setId(openApiClient.getAppId());
        this.setApplicationId(openApiClient.getApplicationId());
        this.setAppId(openApiClient.getAppId());
        this.setSecureKey(openApiClient.getSecureKey());
        this.setIpWhiteList(openApiClient.getIpWhiteList());
        this.setRedirectUrl(openApiClient.getRedirectUrl());
        this.setEnableOAuth2(openApiClient.getEnableOAuth2());

    }

    public OpenApiClientEntity() {

    }

}
