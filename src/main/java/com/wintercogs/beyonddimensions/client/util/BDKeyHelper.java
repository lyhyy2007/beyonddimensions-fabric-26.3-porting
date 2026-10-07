package com.wintercogs.beyonddimensions.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

/**
 * 26.3 原版把 {@code KeyMapping#getKey()} 去掉了（字段私有且无 getter），
 * 改由 Fabric 的 {@link KeyMappingHelper#getBoundKeyOf(KeyMapping)} 取当前绑定键。
 */
public final class BDKeyHelper
{
    private BDKeyHelper() {}

    public static InputConstants.Key boundKey(KeyMapping mapping)
    {
        return KeyMappingHelper.getBoundKeyOf(mapping);
    }
}