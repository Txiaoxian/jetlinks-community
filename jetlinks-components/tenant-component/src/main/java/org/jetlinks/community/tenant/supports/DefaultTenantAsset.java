package org.jetlinks.community.tenant.supports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetlinks.community.tenant.AssetType;
import org.jetlinks.community.tenant.TenantAsset;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DefaultTenantAsset implements TenantAsset {

    private String tenantId;

    private String assetId;

    private AssetType assetType;

    private String ownerId;

    private long permissionValue;

}
