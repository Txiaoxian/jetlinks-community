package org.jetlinks.community.openapi.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.Dict;
import org.hswebframework.web.dict.EnumDict;

@Getter
@AllArgsConstructor
@Dict( "application-integration_modes")
public enum ApplicationIntegrationModes implements EnumDict<String> {
    apiServer("API服务"),
    ssoClient("单点登录");
    private final String text;

    @Override
    public String getValue() {
        return name();
    }
}
