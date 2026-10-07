package com.wintercogs.beyonddimensions.common.init;

import net.minecraft.world.inventory.MenuType;

import com.wintercogs.beyonddimensions.common.menu.*;

public class BDMenus
{
    public static void register()
    {
        // 菜单类型在各类的静态初始化中即完成注册；这里显式引用以触发类加载
        MenuType<?>[] types = {
                DimensionsNetMenu.Dimensions_Net_Menu,
                NetControlMenu.Net_Control_Menu,
                NetInterfaceBaseMenu.Net_Interface_Menu,
                NetEnergyMenu.Net_Energy_Menu,
                DimensionsCraftMenu.Dimensions_Craft_Menu,
                DimensionsCraftMenuTerminal.Dimensions_Craft_Menu_Terminal,
                NetPumpMenu.Net_Pump_Menu,
                NetHopperMenu.Net_Hopper_Menu,
                NetFurnaceMenu.Net_Furnace_Menu,
                NetMagnetMenu.Net_Magnet_Menu,
                NetFeederMenu.Net_Feeder_Menu,
                NetRestockerMenu.Net_Restocker_Menu,
                XpExchangeMenu.XP_EXCHANGE_MENU,
                PrimaryNetSwitcherMenu.PRIMARY_NET_SWITCHER_MENU
        };
        if (types.length == 0)
        {
            throw new IllegalStateException("菜单注册未生效");
        }
    }
}
