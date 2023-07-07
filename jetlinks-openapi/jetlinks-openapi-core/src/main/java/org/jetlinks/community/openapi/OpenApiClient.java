package org.jetlinks.community.openapi;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.authorization.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    private String clientName;

    @Schema(description = "签名方式,MD5或者SHA256")
    private Signature signature;

    @NotBlank
    @Schema(description = "密钥")
    private String secureKey;


    @Schema(description = "IP白名单,多个用回车分隔")
    private String ipWhiteList;

    @Schema(description = "是否开启OAuth2")
    private Boolean enableOAuth2;

    private String redirectUrl;

    private Set<String> orgIdList;

    private Set<String> roleIdList;

    private Authentication authentication;

    public boolean verifyIpAddress(String ipAddress) {

        if (!StringUtils.hasLength(ipWhiteList) || !StringUtils.hasLength(ipAddress)) {
            return true;
        }
        List<String> ipWhiteLists = Arrays.asList(ipWhiteList.split("[,;\n]"));
        if (ipAddress.contains(" ")) {
            ipAddress = ipAddress.split("[ ]")[0].trim();
        }
        return ipWhiteLists.contains(ipAddress);
    }

    public OpenApiClient addRoleIdList(String roleId) {
        if (this.roleIdList == null) {
            this.roleIdList = new HashSet<>();
        }
        this.roleIdList.add(roleId);
        return this;
    }

    public OpenApiClient addOrgIdList(String orgId) {
        if (this.orgIdList == null) {
            this.orgIdList = new HashSet<>();
        }
        this.orgIdList.add(orgId);
        return this;
    }
}
