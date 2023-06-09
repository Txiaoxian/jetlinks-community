package org.jetlinks.community.tenant.supports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetlinks.community.tenant.Asset;
import org.jetlinks.community.tenant.AssetType;


@Getter
@Setter
@AllArgsConstructor
public class DefaultAsset implements Asset {

    private String id;

    private String name;

    private AssetType type;

}
