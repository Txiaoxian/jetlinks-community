package org.jetlinks.community.openapi.manager.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.DefaultValue;
import org.hswebframework.ezorm.rdb.mapping.annotation.EnumCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.hswebframework.web.crud.annotation.EnableEntityEvent;
import org.hswebframework.web.crud.generator.Generators;
import org.jetlinks.community.openapi.manager.enums.ApplicationIntegrationModes;
import org.jetlinks.community.openapi.manager.enums.ApplicationState;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import static java.sql.JDBCType.BIGINT;

@Getter
@Setter
@Table(name = "s_application")
@EnableEntityEvent
public class ApplicationEntity extends GenericEntity<String> implements RecordCreationEntity {

    @Override
    @GeneratedValue(generator = Generators.SNOW_FLAKE)
    @Schema(description = "应用ID")
    public String getId() {
        return super.getId();
    }

    /**
     * 名称
     */
    @Column(name = "name")
    @Schema(description = "名称")
    private String name;

    /**
     * 类型
     */
    @Column(name = "provider")
    @Schema(description = "类型")
    private String provider;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @Column(length = 32, nullable = false)
    @EnumCodec
    @ColumnType(javaType = String.class)
    @NotBlank
    @DefaultValue("enabled")
    private ApplicationState state;


    /**
     * 接入方式
     */
    @Column
    @EnumCodec(toMask = true)
    @ColumnType(jdbcType = BIGINT, javaType = Long.class)
    @Schema(description = "接入方式")
    @DefaultValue("0")
    private ApplicationIntegrationModes[] integrationModes;

    /**
     * 说明
     */
    @Column(name = "description")
    @Schema(description = "说明")
    private String description;

    /**
     * 创建用户ID
     */
    @Column(name = "creator_id", updatable = false)
    @Schema(
        description = "创建者ID(只读)"
        , accessMode = Schema.AccessMode.READ_ONLY
    )
    private String creatorId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", updatable = false)
    @DefaultValue(generator = Generators.CURRENT_TIME)
    @Schema(
        description = "创建时间(只读)"
        , accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long createTime;
}
