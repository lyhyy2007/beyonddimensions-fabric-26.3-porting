package com.wintercogs.beyonddimensions.fabric;

import com.wintercogs.beyonddimensions.BeyondDimensionsClient;
import com.wintercogs.beyonddimensions.client.command.ClientCommands;
import com.wintercogs.beyonddimensions.client.event.listener.ShortKeysListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/** Fabric 客户端入口点（原生版）。 */
public class BeyondDimensionsFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BeyondDimensionsClient.initClient();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                ClientCommands.registerFabric(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> ShortKeysListener.onKeyInput());
    }
}
