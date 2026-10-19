# 方块（`block` 包）

方块注册中心 `ModBlocks`，带自定义行为的方块在 `block/custom/`（含思维流体方块）。
数据侧的伙伴在 [`../datagen`](../datagen)，贴图脚本见仓库根的 `tools/textures/`。

## 新增方块要补的登记点

最容易漏的就是这一串，缺一项通常表现为模型丢失或没有掉落：

1. `ModBlockStatesProvider`（半透明要额外加 `.renderType("translucent")` 与 `.noOcclusion()`）
2. `ModBlockLootTablesProvider`
3. `ModBlockTagsProvider`
4. `ModItemModelsProvider#blockItem`
5. `ModCreativeModeTabs`
6. 中英文语言文件

## 不完整方块

重写 `getBoundingBoxes` 与 `hasCustomBoxes`（返回空列表 = 无碰撞体），模型用手写 `elements`
（`models().getBuilder(name).parent(UncheckedModelFile(mcLoc("block/block")))` + `element()...allFaces(...)`）。
**模型坐标必须与包围盒一致**，否则看得见点不到（或反过来）。物品模型用 `simpleBlockItem`。

## RotatableBlock

- `FOUR_WAY` 的属性名是 `subfacing` **不是** `HORIZONTAL_FACING`，所以
  `BlockStateProvider#horizontalBlock` 不可用，必须手写 variant，旋转角 `(toYRot()+180)%360`。
- `SIX_WAY` 用 `FACING_ALL`（属性名 `facing`），UP 对应 `x=270`、DOWN 对应 `x=90`。

## BooleanProperty 默认值

取值顺序是 `true,false`，`any()` 取第一个 → 默认是 `true`。想默认关闭必须显式
`registerDefaultState`。`DirectionProperty` 同理，默认是 `DOWN`。

## 流体方块

流体方块要用 `registerBlockOnly` 注册（**不注册物品**），否则缺少 `models/item/*.json`
就是物品栏里的黑紫块。

## 黑紫块排查

物品在物品栏里显示成黑紫棋盘格，按顺序查：

- 缺 `models/item/<注册名>.json` —— 需要 `simpleBlockItem` 或 `basicItem`。
- 注册了物品却没模型（见上一条的流体方块）。
- JEI 面板背景黑紫见 [JEI 支持](../jei/README.md)（`ResourceLocation` 必须带 `.png`）。
- 客户端日志里的 `Unable to load model` / `Failed to texture` 是第一手线索。
