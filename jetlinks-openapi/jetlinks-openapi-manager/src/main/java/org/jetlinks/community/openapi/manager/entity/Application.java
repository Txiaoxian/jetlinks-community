package org.jetlinks.community.openapi.manager.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetlinks.community.openapi.OpenApiClient;
import org.jetlinks.community.openapi.manager.enums.ApplicationIntegrationModes;
import org.jetlinks.community.openapi.manager.enums.ApplicationState;

import java.io.Serializable;

@Getter
@Setter
public class Application implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private String provider;

    @Schema(description = "接入方式")
    private ApplicationIntegrationModes[] integrationModes;

    @Schema(description = "说明")
    private String description;


    @Schema(description = "api服务")
    private OpenApiClient apiServer;

    @Schema(description = "状态")
    private ApplicationState state;

    private String creatorId;

    private Long createTime;

    public static Application of(ApplicationEntity entity) {
        return new Application().with(entity);
    }

    public Application with(ApplicationEntity entity) {
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setIntegrationModes(entity.getIntegrationModes());
        this.setProvider(entity.getProvider());
        this.setState(entity.getState());
        this.setDescription(entity.getDescription());
        if (entity.getCreateTime() != null) {
            this.setCreateTime(entity.getCreateTime());
        }
        if (entity.getCreatorId() != null) {
            this.setCreatorId(entity.getCreatorId());
        }

        return this;
    }

    public ApplicationEntity toApplicationEntity() {
        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(this.id);
        applicationEntity.setName(this.name);
        applicationEntity.setDescription(this.description);
        applicationEntity.setProvider(this.provider);
        applicationEntity.setIntegrationModes(this.integrationModes);
        applicationEntity.setState(this.state);
        return applicationEntity;
    }
}
