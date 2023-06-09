package org.jetlinks.community.openapi.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.Dict;
import org.hswebframework.web.dict.EnumDict;

@Getter
@AllArgsConstructor
@Dict( "application-state")
public enum ApplicationState implements EnumDict<String> {
    enabled("正常"),
    disabled("禁用");
    private final String text;

    @Override
    public String getValue() {
        return name();
    }
}
