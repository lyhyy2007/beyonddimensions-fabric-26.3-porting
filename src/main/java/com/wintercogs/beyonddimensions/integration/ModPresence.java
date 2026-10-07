package com.wintercogs.beyonddimensions.integration;

import net.fabricmc.loader.api.FabricLoader;

public final class ModPresence
{
    private ModPresence()
    {
    }

    public static boolean isLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
