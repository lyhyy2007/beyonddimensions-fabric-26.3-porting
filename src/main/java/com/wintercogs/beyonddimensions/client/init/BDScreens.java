package com.wintercogs.beyonddimensions.client.init;

import com.wintercogs.beyonddimensions.client.gui.DimensionsCraftGUI;
import com.wintercogs.beyonddimensions.client.gui.DimensionsNetGUI;
import com.wintercogs.beyonddimensions.client.gui.DimensionsTerminalCraftGUI;
import com.wintercogs.beyonddimensions.client.gui.NetControlGUI;
import com.wintercogs.beyonddimensions.client.gui.NetEnergyGUI;
import com.wintercogs.beyonddimensions.client.gui.NetFeederGUI;
import com.wintercogs.beyonddimensions.client.gui.NetFurnaceGUI;
import com.wintercogs.beyonddimensions.client.gui.NetHopperGUI;
import com.wintercogs.beyonddimensions.client.gui.NetInterfaceBaseGUI;
import com.wintercogs.beyonddimensions.client.gui.NetMagnetGUI;
import com.wintercogs.beyonddimensions.client.gui.NetPumpGUI;
import com.wintercogs.beyonddimensions.client.gui.NetRestockerGUI;
import com.wintercogs.beyonddimensions.client.gui.PrimaryNetSwitcherGUI;
import com.wintercogs.beyonddimensions.client.gui.XpExchangeGUI;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenu;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenuTerminal;
import com.wintercogs.beyonddimensions.common.menu.DimensionsNetMenu;
import com.wintercogs.beyonddimensions.common.menu.NetControlMenu;
import com.wintercogs.beyonddimensions.common.menu.NetEnergyMenu;
import com.wintercogs.beyonddimensions.common.menu.NetFeederMenu;
import com.wintercogs.beyonddimensions.common.menu.NetFurnaceMenu;
import com.wintercogs.beyonddimensions.common.menu.NetHopperMenu;
import com.wintercogs.beyonddimensions.common.menu.NetInterfaceBaseMenu;
import com.wintercogs.beyonddimensions.common.menu.NetMagnetMenu;
import com.wintercogs.beyonddimensions.common.menu.NetPumpMenu;
import com.wintercogs.beyonddimensions.common.menu.NetRestockerMenu;
import com.wintercogs.beyonddimensions.common.menu.PrimaryNetSwitcherMenu;
import com.wintercogs.beyonddimensions.common.menu.XpExchangeMenu;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * 菜单界面注册（Fabric 原生版）。
 * <p>
 * 26.3 原版把 {@code MenuScreens.register} 收成 private，已由 Access Widener 打开；
 * 这里直接调用，不再需要 NeoForge 的注册事件。
 */
public class BDScreens
{
    public static void registerScreens()
    {
        // 显式说明类型，防止 javac 无法推断泛型
        MenuScreens.<DimensionsNetMenu, DimensionsNetGUI<DimensionsNetMenu>>register(
                DimensionsNetMenu.Dimensions_Net_Menu, DimensionsNetGUI::new);
        MenuScreens.register(NetControlMenu.Net_Control_Menu, NetControlGUI::new);
        MenuScreens.register(NetInterfaceBaseMenu.Net_Interface_Menu, NetInterfaceBaseGUI::new);
        MenuScreens.register(NetEnergyMenu.Net_Energy_Menu, NetEnergyGUI::new);
        MenuScreens.<DimensionsCraftMenu, DimensionsCraftGUI<DimensionsCraftMenu>>register(
                DimensionsCraftMenu.Dimensions_Craft_Menu, DimensionsCraftGUI::new);
        MenuScreens.register(DimensionsCraftMenuTerminal.Dimensions_Craft_Menu_Terminal, DimensionsTerminalCraftGUI::new);
        MenuScreens.register(NetPumpMenu.Net_Pump_Menu, NetPumpGUI::new);
        MenuScreens.register(NetHopperMenu.Net_Hopper_Menu, NetHopperGUI::new);
        MenuScreens.register(NetFurnaceMenu.Net_Furnace_Menu, NetFurnaceGUI::new);
        MenuScreens.register(NetMagnetMenu.Net_Magnet_Menu, NetMagnetGUI::new);
        MenuScreens.register(NetFeederMenu.Net_Feeder_Menu, NetFeederGUI::new);
        MenuScreens.register(NetRestockerMenu.Net_Restocker_Menu, NetRestockerGUI::new);
        MenuScreens.register(XpExchangeMenu.XP_EXCHANGE_MENU, XpExchangeGUI::new);
        MenuScreens.register(PrimaryNetSwitcherMenu.PRIMARY_NET_SWITCHER_MENU, PrimaryNetSwitcherGUI::new);
    }
}
