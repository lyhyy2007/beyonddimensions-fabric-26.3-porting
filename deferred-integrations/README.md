# deferred-integrations

本目录存放**尚未纳入编译**的上游模块，供后续按模块移植时取用。它们没有任何构建脚本引用，因此不会影响 `gradlew build`。

## 内容

| 目录/文件 | 内容 | 移植要点 |
| --- | --- | --- |
| `integration-framework/` | 联动框架接口层 | 多数是平台无关的抽象，可优先移植 |
| `integration-module/rs/` | Refined Storage 联动 | 需 RS 的 Fabric 版，API 与 NeoForge 版差异较大 |
| `integration-module/jei/` | JEI 联动 | JEI 有 Fabric 版，但 `IGuiHelper` 等接口签名不同 |
| `integration-module/curios/` | Curios 饰品栏联动 | Fabric 侧对应 Trinkets / Accessories，需重写挂载层 |
| `integration-module/jech/` | JECh 联动 | 同上，需确认 Fabric 侧可用性 |
| `datagen/` | 数据生成器（10 个 provider） | 需改写为 Fabric `fabric-data-generation-api-v1`；上游产物已在 `src/main/resources` 中，故当前不影响运行 |
| `data-integration/` | 联动方块/配方相关的数据文件 | 随对应联动模块一起启用 |
| `accesstransformer.cfg` | 上游 NeoForge 的 AT 配置 | 已等价改写为 `src/main/resources/beyonddimensions.accesswidener`，此处仅作对照留档 |

## 启用某一模块时

1. 把对应目录移动到 `src/main/java/com/wintercogs/beyonddimensions/integration/` 下；
2. 把其中的 NeoForge 依赖改写为 Fabric 原生写法（参考主源码中 `fabric/`、`registry/`、`api/capability/` 的做法）；
3. 在 `build.gradle` 中按需添加对应模组库的 `compileOnly`（Fabric 侧通常是 `modCompileOnly` 或 `compileOnly` + 仓库声明）；
4. 在 `IntegrationManager` 中注册该模块；
5. 跑一次 `gradlew build` 与 `runServer` / `runClient` 验证。

## 许可提示

本目录内容同属上游 Beyond Dimensions 项目，遵循 **MIT License**（见仓库根目录 `LICENSE`）。若某个联动模块在移植时引入了新的第三方库，请在根目录 `THIRD_PARTY_NOTICES.md` 中补充说明。
