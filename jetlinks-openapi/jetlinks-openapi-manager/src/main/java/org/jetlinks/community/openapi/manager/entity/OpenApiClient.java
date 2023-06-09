package org.jetlinks.community.openapi.manager.entity;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.crud.generator.Generators;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class OpenApiClient implements Serializable {
    @Schema(description = "ID")
    private String id;

    @Hidden
    @Schema(description = "applicationId")
    private String applicationId;

    @NotBlank
    @Schema(description = "appId")
    private String appId;

    @Schema(description = "签名方式,MD5或者SHA256")
    private String signature;

    @NotBlank
    @Schema(description = "密钥")
    private String secureKey;


    @Schema(description = "IP白名单,多个用,分隔")
    private String ipWhiteList;

    @Schema(description = "是否开启OAuth2")
    private Boolean enableOAuth2;

    private String redirectUrl;

    private Set<String> orgIdList;

    private Set<String> roleIdList;


    public OpenApiClient addRoleIdList(String roleId) {
        if (this.roleIdList == null) {
            this.roleIdList = new HashSet<>();
        }
        this.roleIdList.add(roleId);
        return this;
    }

    public static OpenApiClient of(OpenApiClientEntity entity) {
        return new OpenApiClient().with(entity);
    }

    public OpenApiClient with(OpenApiClientEntity entity) {
        this.setSecureKey(entity.getSecureKey());
        this.setSignature(entity.getSignature());

        return this;
    }

    public OpenApiClientEntity toOpenApiClientEntity() {
        OpenApiClientEntity openApiClientEntity = new OpenApiClientEntity();
        openApiClientEntity.setId(this.appId);
        openApiClientEntity.setApplicationId(this.applicationId);
        openApiClientEntity.setAppId(this.appId);
        openApiClientEntity.setSecureKey(this.secureKey);
        openApiClientEntity.setIpWhiteList(this.ipWhiteList);
        openApiClientEntity.setRedirectUrl(this.redirectUrl);
        openApiClientEntity.setEnableOAuth2(this.enableOAuth2);

        return openApiClientEntity;
    }

}
