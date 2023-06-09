package org.jetlinks.community.rule.engine.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.community.tenant.AssetPermission;
import org.jetlinks.community.tenant.EnumAssetType;
import org.jetlinks.community.tenant.supports.CrudAssetPermission;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum RuleEngineAssetType implements EnumAssetType {

    ruleModel("规则模型", Arrays.asList(CrudAssetPermission.values())),
    ruleInstance("规则实例", Arrays.asList(CrudAssetPermission.values()))
    ;

    private final String name;

    private final List<AssetPermission> permissions;

    @Override
    public String getId() {
        return name();
    }

}
