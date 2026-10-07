package com.wintercogs.beyonddimensions.client.event.listener;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.client.init.BDShortKeys;
import net.fabricmc.api.EnvType;
public class ShortKeysListener
{
    public static void onKeyInput()
    {
        BDShortKeys.processKeyInput();
    }
}
