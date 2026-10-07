# Beyond Dimensions — Fabric 26.3 移植版

> ## ⚠️ 原作者声明
>
> **本模组《超越维度 / Beyond Dimensions》的原作者是 [Frostbite-time](https://github.com/Frostbite-time/BeyondDimensions)。**
>
> - 原作仓库：**<https://github.com/Frostbite-time/BeyondDimensions>**
> - 版权与许可：**MIT License，`Copyright (c) 2025 Frostbite-time`**（全文见 [LICENSE](LICENSE)）
> - **本仓库是社区移植版，不是原作者的官方发布。** 原始设计、美术素材与绝大部分源代码均出自 Frostbite-time 之手；移植维护者的工作仅限于加载器与版本适配。
> - 上游元数据：`mod_license=MIT`、`mod_authors=Frostbite`、`mod_id=beyonddimensions`、`mod_group_id=com.wintercogs.beyonddimensions`
> - Fabric 分支（26.1 基线）作者：**[Dehowy](https://github.com/Dehowy/BeyondDimensions-Fabric)**
>
> 完整的署名、来源链路与 MIT 义务说明见 **[NOTICE](NOTICE)**。

- **上游原作**：[Frostbite-time/BeyondDimensions](https://github.com/Frostbite-time/BeyondDimensions)（作者 **Frostbite-time**，MIT 许可）
- **移植基线**：[Dehowy/BeyondDimensions-Fabric](https://github.com/Dehowy/BeyondDimensions-Fabric) 的 **26.1 分支**（MC 26.1.2 / NeoForge 26.1.2.12-beta / mod_version 0.7.13）
- **目标平台**：Minecraft **26.3** · Fabric Loader 0.19.5 · Fabric API 0.161.0+26.3 · Java 25
- **本仓库许可**：**MIT**（沿用上游，见 [LICENSE](LICENSE)）

---

## 简介

超越维度是一个提供存储与实用工具的模组。它引入了「维度网络」作为存储系统，支持物品、流体、FE 能量和通用机械化学品，容量上限为 `2^63-1`（Java `long` 最大值）。

- **大容量存储**：默认存储空间即为最大，可存放约 21 亿种不同资源
- **通用槽位**：任何界面中的任何槽位都能存取所有受支持的资源类型
- **搜索**：支持名称 / 工具提示 / 模组 id 匹配、拼音搜索、多种排序
- **自动化方块**：维度网络通道（物品/能量）、网络接口、网络泵、网络漏斗、网络熔炉、网络喂食器、网络磁铁、主手物品快速转移
- **联动**：JEI / EMI 拖拽标记、AE2 存储元件与通用包裹、Mek 化学品、KubeJS（详见 §联动状态）

## 构建

需求：**JDK 25**（`build.gradle` 用 toolchain 声明，缺 JDK 时由 foojay 自动下载）。

```sh
# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

产物：`build/libs/beyonddimensions-0.7.13+26.3.jar`（另附 `-sources.jar`）。

其他任务：

```sh
./gradlew runServer     # 本地 Fabric 服务端
./gradlew runClient     # 本地 Fabric 客户端
```

## 安装

把 `build/libs/beyonddimensions-0.7.13+26.3.jar` 放入 `mods/` 目录，同时确保已安装对应版本的 **Fabric API**，然后启动游戏/服务器。

## 目录结构

```
├── build.gradle / settings.gradle / gradle.properties   # 构建配置（loom 1.17.21）
├── gradle/wrapper/                                      # Gradle Wrapper 9.5.1
├── src/main/java/com/wintercogs/beyonddimensions/       # 全部源码（255 个 Java 文件）
│   ├── awt / api / client / common / integration / mixin / util   # 上游结构
│   ├── fabric/          # Fabric 入口点与网络层
│   ├── config/          # 配置实现（BDConfigSpec）
│   ├── registry/        # 注册入口（BDRegistry / BDHolder）
│   └── ...
├── src/main/resources/                                  # 资源、Access Widener、Mixin 配置
├── docs/                                                # 移植报告与验证命令
├── deferred-integrations/                               # 暂未编译的集成模块与数据生成器
└── licenses/                                            # 第三方许可全文
```

## 移植说明（要点）

本移植版与上游的主要差异集中在**加载器层**：

1. **零 `net.neoforged` 依赖**：产物 jar 内不存在任何 `net/neoforged/**` 条目，也不依赖任何 NeoForge 兼容层。
   - 注册：`registry.BDRegistry` + `BDHolder` / `BDItemHolder` / `BDBlockHolder`（内部直接调用原版 `Registry.register`）
   - 事件：直接挂 Fabric 回调（`ModInitializer` / `ClientModInitializer` / `ServerLifecycleEvents` / `CommandRegistrationCallback` / `ClientTickEvents`）
   - 网络：`fabric.BDNetworking`（`PayloadTypeRegistry` + `Server/ClientPlayNetworking`）
   - 能力：Fabric 官方 `BlockApiLookup` / `ItemApiLookup`，并保留 `ItemStorage` / `FluidStorage` 回退以维持跨模组互操作
   - 配置：`config.BDConfigSpec`（JSON，落盘到 `config/beyonddimensions-<type>.json`）
   - 菜单：`ExtendedMenuType<T, byte[]>` 复现上游「打开界面附带一段额外数据」的写法
2. **自有领域实现**：`api/transfer/**`（`ResourceHandler` / `Transaction` / `SnapshotJournal` 等）与 `api/fluid/**`（`FluidStack` / `FluidType` / `BaseFlowingFluid`）是模组自有的领域 API，直接位于本模组命名空间下。
3. **26.1 → 26.3 原版 API 适配**：涉及 `MenuScreens.register` 可见性、`KeyMapping` 取键、`GuiGraphics` tooltip、`drop` / `placeItemBackInInventory` 的 `Prediction` 参数、`BlockEntityType` 构造器、`FluidStateModelSet` 流体模型烘培等，清单见移植报告 §5。

完整的移植记录、逐项验证结果与已知缺口见 **[docs/移植报告-26.3.md](docs/移植报告-26.3.md)**。

## 验证状态

已在**真实 Fabric 26.3** 环境验证：

| 项目 | 结果 |
| --- | --- |
| 构建 | ✅ `gradlew build` 通过 |
| 服务端 | ✅ `Loading 44 mods` → `Done`，0 ERROR；方块 / 流体 / 方块实体 NBT / 物品 / 命令 实测通过 |
| 客户端 | ✅ `Loading 52 mods` → 客户端入口点执行 → 进入世界；0 链接期异常 |
| 与整合包共存 | ✅ 与 140 模组整合包中 66 个第三方 `net/neoforged` 类的重叠数为 **0** |

**尚未验证**：存储网络的实机交互（建网络、界面操作、搬运逻辑）、全部 GUI 视觉走查、世界内流体渲染细节、`CookingFuel` 数值精度，以及 `deferred-integrations/` 中的全部联动模块。

## 联动状态

| 联动 | 状态 |
| --- | --- |
| JEI / EMI | ⏸ 接口已在源码中就位，Fabric 侧适配尚未启用 |
| AE2 / Refined Storage | ⏸ 延后，原件见 `deferred-integrations/` |
| Curios | ⏸ Fabric 侧对应 Trinkets / Accessories，需重写挂载层 |
| Mekanism 化学品 | ⏸ 延后 |
| KubeJS | ⏸ 延后 |
| 通用拼音搜索 | ✅ 可用（内置 TinyPinyin，若安装了通用拼音搜索模组则优先使用它） |

## 致谢

- **Frostbite-time** — 原作《超越维度 / Beyond Dimensions》的作者。本项目的全部设计、美术素材与绝大部分代码来自上游，版权归其所有（MIT，`Copyright (c) 2025 Frostbite-time`）。原作仓库：<https://github.com/Frostbite-time/BeyondDimensions>
- **Dehowy** — Fabric 分支（26.1 基线）作者：<https://github.com/Dehowy/BeyondDimensions-Fabric>
- **TinyPinyin** — 拼音搜索库（Apache-2.0，jar-in-jar 内嵌，见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)）

## 许可与署名

本项目沿用上游的 **MIT License**，原始版权声明为 **`Copyright (c) 2025 Frostbite-time`**，全文见 [LICENSE](LICENSE)。

依据 MIT 许可，你可以自由使用、修改、分发本项目，但**必须保留原始版权声明与许可声明**。为此本仓库做了三件事：

1. `LICENSE` 中的上游版权行**逐字保留**，未做任何修改；
2. 构建产物把 `LICENSE`、`NOTICE`、`THIRD_PARTY_NOTICES.md`、`licenses/` 一并打进 jar 的 `META-INF/`，确保声明随分发物一起传播；
3. 在 [NOTICE](NOTICE) 中完整列出原作者、来源链路、第三方组件与 MIT 义务。

若你二次分发或再移植，请同样保留上述声明，并注明你的版本是移植/分支而非上游官方发布。

第三方组件的许可见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 与 [licenses/](licenses/)。
