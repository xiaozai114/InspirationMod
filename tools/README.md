# 贴图生成脚本

`tools/textures/` 下是一组 Pillow 脚本，机器面板、JEI 背景、插件、结晶方块、工具的贴图都由它们产出。
**脚本内路径全部相对仓库根目录**，运行时要在仓库根执行：

```bash
D:/programfiles/conda/python.exe tools/textures/gen_plugin_textures.py              # 生成全部
D:/programfiles/conda/python.exe tools/textures/gen_plugin_textures.py <插件名>       # 只生成指定插件
```

用 conda 的 python（`D:/programfiles/conda/python.exe`）—— 托管版 python 没装 Pillow。

| 脚本 | 产出 |
| --- | --- |
| `gen_jei_background.py` | JEI 各分类面板背景 |
| `gen_plugin_textures.py` | 插件贴图（表驱动，新增插件往 `PLUGINS` 与 `SYMBOLS` 各加一行） |
| `gen_injection_blocks.py` / `gen_injection_items.py` | 注入结构方块与物品 |
| `gen_basalt_alloy_firestriker.py` | 玄武岩合金打火石 = 原版打火石 + 暗色滤镜（原版是纯灰度图，所以直接在亮度上压暗 0.55 并往玄武岩的冷蓝灰偏，不动形状与透明区；哪天不再是灰度会报错） |
| `gen_inspiration_assembler_block.py` / `gen_mineral_twister_block.py` / `gen_energized_matter_fusion_block.py` | 机器方块贴图（16×16：1px 描边 + 顶部高光 + 内凹面板，靠主色与纹样区分） |
| `gen_high_energy_matter_reshaper_block.py` / `gen_high_energy_mineral_twister_block.py` / `gen_high_energy_inspiration_assembler_block.py` | 高能机器方块贴图（淡紫机身：重塑机正面同心方框、侧面横向散热口带三个辉光点、顶面菱形；装配台多加一圈金色镶条与「高能核心」舷窗：辉光环 + 深色玻璃 + 白热核心 + 四道十字光芒） |
| `gen_core_driller.py` | 地核钻探者方块三面 + 玄武岩合金钻头物品贴图（仿格雷科技的麻花钻头：锥形刃头 + 螺旋排屑槽 + 加宽夹持座，最后整体旋转 45° 斜置 —— 自己写逆向最近邻采样，不用 `Image.rotate`，否则 45° 会留下锯齿与孔洞） |
| `gen_guide_book.py` | 灵感指导书（深紫封面 + 金色书脊 + 中央灵感星，手绘 16×16） |
| `gen_inspiration_fragments.py` / `gen_inspiration_charge_fluid.py` | 灵感碎片 / 灵感充能流体 |
| `gen_inspiration_crystal_blocks.py` | 四种结晶方块 |
| `gen_ingot_textures.py` | 同形异色锭（按亮度重映射到目标调色板，新增配色往 `PALETTES` 加一行；白色星点往 `SPARKLES` 加） |
| `gen_francium_textures.py` | 钫盐系列（铯系换色相）+ 手绘盖革计数器（钫盐矿换成地狱岩底） |
| `gen_end_alloys.py` | 末地合金 / 无恒合金 / 末地合金盾牌 / 世界数据探测器（下界合金锭换淡黄 + 末地石材质；无恒物质整张叠上去且**不被锭轮廓裁剪**、只做一层几乎透明的淡紫；盾牌 = **原版盾牌展开图**只换盾正面材质，输出到 `textures/entity/`） |
| `gen_high_energy_core.py` | 高能核心（放大到满画布的菱形外框 + 粉色炉心 + 外沿粉光与四角星点） |
| `gen_generator_cores.py` | 发电核心 / 反应核心 / 核废料：前两张是「强能核心换绿炉心」「高能核心换红炉心」（只转色相，饱和度 < 0.25 的灰白与金属不动），核废料是新画的结壳废料块（椭圆裁形 + 不规则半径 + 绿色裂纹） |
| `gen_generator_blocks.py` | 两种发电机的方块三面：碱性反应堆（青绿机身 + 圆形反应腔 + 竖排电解槽）、RTG 发电机（暗红机身 + 同心方框 + 横向散热帯）；表驱动，新增发电机往 `MACHINES` 加一行 |
| `gen_simulator_textures.py` | 结构模拟机一族：高能机器外壳 = **原版 `iron_block.png` 转 HSV 后把色相钉在紫色上**（亮度与纹理保留，所以仍像金属）；五种仓室 = 同一张外壳 + 内凹面板 + 一枚中央标记（槽口 / 槽口 / 闪电 / 菱形 / 芯片，**输入仓与输出仓共用槽口，只靠绿/橙两色区分**，不再画箭头）；控制器三面（正面是 3×3 结构网格、侧面散热帯、顶面菱形接口）；两份结构数据物品（同一张卡，沉船数据在屏幕上多了「帆 + 桅杆 + 船体 + 海」四行）；结构投影仪（机身在右下、镜头朝左上打出紫色光锥） |
| `gen_exp_shovel.py` | 灵感之铲（从 client-extra jar 取原版 `iron_shovel.png`，按显式 `COLOUR_MAP` 把铁灰换成灵感绿、保留木柄；**遇到未映射的颜色直接报错**，别静默放过） |
| `gen_jade_steel_tools.py` | 表驱动：`STYLES` 里两套色相（`jade_steel` 绿 / `energized_jade_steel` 青带星点）× `SOURCES` 里 5 种工具（含 `shovel` ← `exp_shovel`），一次出 10 张 |
| `gen_jade_steel_upgrade_template.py` | 两个升级模板（从原版下界合金升级模板取图，`PALETTES` 分别产出翡翠钢的绿色版与注能版的紫底电青宝石） |

`tools/textures/base/` 放**底材与形状模板**（`netherrack.png`、`netherite_ingot.png`、`end_stone.png`，
都从原版资源包 `build/moddev/artifacts/neoforge-*-client-extra-aka-minecraft-resources.jar` 里抽出）。
钫盐矿要把矿脉叠在地狱岩上，末地合金要拿末地石给合金做材质调制，
所以这两处都不是整张换色，而是「模板换色后与底图合成」；换底材只换这几张图。

两条经验：

- 同形异色锭可以按亮度重映射；成套工具/武器**只能换色相**，按亮度映射会把工具画成一块色块
  （见 `gen_jade_steel_tools.py`）。
- 原版公式（掉落算法、作物判定等）要抄就去 `build/moddev/artifacts/neoforge-<版本>-sources.jar`
  逐行核对，不要凭印象。

产出的贴图直接写到 `src/main/resources/assets/inspiration/textures/`；面板尺寸与布局常量必须同步，
见各模块的 README（例如 JEI 的 `JeiLayout`）。
