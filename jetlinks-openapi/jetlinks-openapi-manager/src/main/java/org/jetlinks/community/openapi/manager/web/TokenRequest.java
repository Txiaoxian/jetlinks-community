package org.jetlinks.community.openapi.manager.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    private long expires = 7200;

}
