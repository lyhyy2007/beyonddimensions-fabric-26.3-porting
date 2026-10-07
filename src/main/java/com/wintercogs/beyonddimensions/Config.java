package com.wintercogs.beyonddimensions;

import com.wintercogs.beyonddimensions.api.ButtonState;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.config.BDConfigSpec;
import com.wintercogs.beyonddimensions.config.CommonConfigRuntime;
import com.wintercogs.beyonddimensions.config.ServerConfigRuntime;

/**
 * 模组配置（Fabric 原生版）。
 * <p>
 * 原先依赖 NeoForge 的 {@code ModContainer#registerConfig} + {@code ModConfigEvent}；
 * 现在改为模组自有实现：构造即装载，装载后直接刷新运行时快照，不再经由事件总线。
 */
public class Config
{
    public final StartUpConfig startUpConfig = new StartUpConfig();
    public final CommonConfig commonConfig = new CommonConfig();
    public final ServerConfig serverConfig = new ServerConfig();

    public static Config INSTANCE;

    private Config()
    {
    }

    /** 由模组初始化调用：构造并立即装载配置。 */
    public static void register()
    {
        INSTANCE = new Config();
        INSTANCE.loadAll();
    }

    /** 装载（或重载）全部配置。 */
    public void loadAll()
    {
        startUpConfig.spec.load(BDConfigSpec.Type.STARTUP);
        commonConfig.spec.load(BDConfigSpec.Type.COMMON);
        serverConfig.spec.load(BDConfigSpec.Type.SERVER);

        commonConfig.onLoaded();
        serverConfig.onLoaded();
    }

    public static class StartUpConfig
    {
        public final BDConfigSpec spec;

        public StartUpConfig()
        {
            BDConfigSpec.Builder builder = new BDConfigSpec.Builder(BDConstants.MODID);

            this.spec = builder.build();
        }
    }

    public static class CommonConfig
    {
        public final BDConfigSpec spec;

        public final BDConfigSpec.EnumValue<ButtonState> UI_SORT_BUTTON;
        public final BDConfigSpec.EnumValue<ButtonState> UI_SECOND_SORT_BUTTON;
        public final BDConfigSpec.EnumValue<ButtonState> UI_REVERSE_BUTTON;
        public final BDConfigSpec.EnumValue<ButtonState> UI_SEARCH_BUTTON;
        public final BDConfigSpec.EnumValue<ButtonState> UI_CRAFT_BUTTON;
        public final BDConfigSpec.EnumValue<ButtonState> UI_CRAFT_RETURN_BUTTON;
        public final BDConfigSpec.IntValue UI_PAGE_NUM;
        public final BDConfigSpec.ConfigValue<String> UI_SEARCH;
        public final BDConfigSpec.BooleanValue SEARCH_TEXT_WITH_JEI_EMI;
        public final BDConfigSpec.BooleanValue EMI_ALLOW_NETWORK_STORAGE_INFO;

        public final BDConfigSpec.BooleanValue INTERFACE_CAN_RECEIVE_RESOURCE;
        public final BDConfigSpec.BooleanValue INTERFACE_CAN_OUTPUT_RESOURCE;
        public final BDConfigSpec.BooleanValue INTERFACE_CAN_POP_RESOURCE;
        public final BDConfigSpec.IntValue INTERFACE_USABLE_CAPACITY;

        public CommonConfig()
        {
            BDConfigSpec.Builder builder = new BDConfigSpec.Builder(BDConstants.MODID);

            UI_SORT_BUTTON = builder
                    .comment("存储UI搜索按钮值 (除非你知道你在做什么，否则不要手动修改)")
                    .defineEnum("ui_sort_button", ButtonState.SORT_NAME);
            UI_SECOND_SORT_BUTTON = builder
                    .comment("存储UI搜索按钮值 (除非你知道你在做什么，否则不要手动修改)")
                    .defineEnum("ui_second_sort_button", ButtonState.SORT_INSERTED_TIME);
            UI_REVERSE_BUTTON = builder
                    .comment("存储UI倒序按钮值 (除非你知道你在做什么，否则不要手动修改)")
                    .defineEnum("ui_reverse_button", ButtonState.DISABLED);
            UI_SEARCH_BUTTON = builder
                    .comment("存储UI搜索按钮值 (除非你知道你在做什么，否则不要手动修改)")
                    .defineEnum("ui_search_button", ButtonState.DISABLED);
            UI_CRAFT_BUTTON = builder
                    .comment("决定打开菜单时是否显示合成槽")
                    .defineEnum("ui_craft_button", ButtonState.DISABLED);
            UI_CRAFT_RETURN_BUTTON = builder
                    .comment("决定工艺菜单关闭时，物品优先转移的方向；启用则优先向存储，关闭则优先向背包")
                    .defineEnum("ui_craft_return_button", ButtonState.DISABLED);
            UI_PAGE_NUM = builder
                    .comment("存储UI当前显示的总页数 (除非你知道你在做什么，否则不要手动修改)")
                    .defineInRange("ui_page_num", 5, 2, 99);
            UI_SEARCH = builder
                    .comment("存储UI搜索框内容 (除非你知道你在做什么，否则不要手动修改)")
                    .define("ui_search", "");
            SEARCH_TEXT_WITH_JEI_EMI = builder
                    .comment("是否与JEI或EMI同步搜索")
                    .define("search_text_with_jei_emi", true);
            EMI_ALLOW_NETWORK_STORAGE_INFO = builder
                    .comment("是否允许EMI获取维度网络内全部物品信息")
                    .define("emi_allow_network_storage_info", false);

            INTERFACE_CAN_RECEIVE_RESOURCE = builder
                    .comment("是否允许网络接口将资源送入网络")
                    .define("interface_can_receive_resource", true);
            INTERFACE_CAN_OUTPUT_RESOURCE = builder
                    .comment("是否允许网络接口从网络提取标记的资源")
                    .define("interface_can_output_resource", true);
            INTERFACE_CAN_POP_RESOURCE = builder
                    .comment("是否允许网络接口将内容物弹出到附近容器")
                    .define("interface_can_pop_resource", true);
            INTERFACE_USABLE_CAPACITY = builder
                    .comment("网络接口有多少个槽位实际可用？")
                    .comment("注意：仅在确定需要时使用，后续版本更新会将其移除并添加其他替代方案，会保证存档兼容。")
                    .defineInRange("interface_usable_capacity", 27, 1, 27);

            this.spec = builder.build();
        }

        public void onLoaded()
        {
            CommonConfigRuntime.uiSortButton = UI_SORT_BUTTON.get();
            CommonConfigRuntime.uiSecondSortButton = UI_SECOND_SORT_BUTTON.get();
            CommonConfigRuntime.uiReverseButton = UI_REVERSE_BUTTON.get();
            CommonConfigRuntime.uiSearchButton = UI_SEARCH_BUTTON.get();
            CommonConfigRuntime.uiCraftButton = UI_CRAFT_BUTTON.get();
            CommonConfigRuntime.uiCraftReturnButton = UI_CRAFT_RETURN_BUTTON.get();
            CommonConfigRuntime.uiPageNum = UI_PAGE_NUM.get();
            CommonConfigRuntime.uiSearch = UI_SEARCH.get();
            CommonConfigRuntime.searchTextWithJEIEMI = SEARCH_TEXT_WITH_JEI_EMI.get();
            CommonConfigRuntime.emiAllowNetworkStorageInfo = EMI_ALLOW_NETWORK_STORAGE_INFO.get();

            CommonConfigRuntime.interfaceCanReceiveResource = INTERFACE_CAN_RECEIVE_RESOURCE.get();
            CommonConfigRuntime.interfaceCanOutputResource = INTERFACE_CAN_OUTPUT_RESOURCE.get();
            CommonConfigRuntime.interfaceCanPopResource = INTERFACE_CAN_POP_RESOURCE.get();
            CommonConfigRuntime.interfaceUsableCapacity = INTERFACE_USABLE_CAPACITY.get();
        }
    }

    public static class ServerConfig
    {
        public final BDConfigSpec spec;

        public final BDConfigSpec.LongValue UNSTABLE_SPACE_TIME_FRAGMENT_TRANSFER_TIME;
        public final BDConfigSpec.IntValue SHATTERED_SPACE_TIME_CRYSTALLIZATION_GENERATE_TIME;

        public ServerConfig()
        {
            BDConfigSpec.Builder builder = new BDConfigSpec.Builder(BDConstants.MODID);

            UNSTABLE_SPACE_TIME_FRAGMENT_TRANSFER_TIME = builder
                    .comment("碎片转化间隔")
                    .defineInRange("fragmentTransferTime", 3600L, 1L, Long.MAX_VALUE);
            SHATTERED_SPACE_TIME_CRYSTALLIZATION_GENERATE_TIME = builder
                    .comment("结晶生成间隔（0代表不生成）")
                    .defineInRange("crystalGenerateTime", 600, 0, Integer.MAX_VALUE);

            this.spec = builder.build();
        }

        public void onLoaded()
        {
            ServerConfigRuntime.fragmentTransferTime = UNSTABLE_SPACE_TIME_FRAGMENT_TRANSFER_TIME.get();
            ServerConfigRuntime.crystalGenerateTime = SHATTERED_SPACE_TIME_CRYSTALLIZATION_GENERATE_TIME.get();
        }
    }
}
