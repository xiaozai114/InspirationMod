"""锭类物品贴图生成脚本。

以现成的翡翠钢锭贴图作为「锭形轮廓 + 明暗噪声」的模板，按亮度排名把模板里的每一档
颜色映射到目标配色上，从而在完全保留锭形与高光位置的前提下换色。
个别锭（见 SPARKLES）额外点几颗白色星点，用来做「水晶矩阵」那种闪烁质感。

用法：
    python tools/textures/gen_ingot_textures.py                 # 生成全部
    python tools/textures/gen_ingot_textures.py plasma_ingot    # 只生成指定锭
输出：
    src/main/resources/assets/inspiration/textures/item/<name>.png
"""

import sys
from pathlib import Path

from PIL import Image

from palettes import MATERIAL_COLORS
from recolor import remap, mix

TEMPLATE = "jade_steel_ingot"

# 每种锭的配色：由暗到亮两端，中间按模板的亮度档位线性插值。
PALETTES = {
    # 等离子锭：天蓝主色，暗部偏深蓝，亮部接近白青
    "plasma_ingot": ((0x14, 0x5E, 0x8C), (0x9B, 0xF2, 0xFF)),
    # 玄武岩合金：玄武灰蓝，暗部近黑，亮部是中灰
    "basalt_alloy": ((0x11, 0x13, 0x17), (0x76, 0x7C, 0x88)),
    # 注能翡翠钢：在翡翠钢的绿上叠一层青白辉光，暗部压成深青
    "energized_jade_steel": ((0x0B, 0x52, 0x5C), (0xD2, 0xFF, 0xF4)),
    # 思维矩阵锭：淡紫主色，暗部偏深紫，亮部接近淡紫白（参考无尽贪婪的水晶矩阵锭配色）
    "mind_matrix_ingot": ((0x4B, 0x22, 0x7A), (0xEC, 0xDB, 0xFF)),
    # 注能青金石锭：青金石的群青往靛紫偏（灵感掺进去的痕迹），亮部拉到淡紫白
    "energized_lapis_ingot": ((0x1A, 0x1F, 0x8F), (0xC6, 0xC2, 0xFF)),
    # 注能等离子锭：等离子锭的天蓝往冰青提一档，亮部几乎纯白，与青金石版拉开
    "energized_plasma_ingot": ((0x14, 0x6E, 0xA8), (0xE8, 0xFF, 0xFF)),
    # 注能红石锭：红石的正红提亮成朱红，亮部偏橙白，与注能青金石的靛紫形成对比
    "energized_redstone_ingot": ((0x8C, 0x11, 0x14), (0xFF, 0xC8, 0xA8)),
    # 幽匿合金：幽匿的深青底，亮部是回响碎片那种冷青白
    "sculk_alloy": ((0x06, 0x2A, 0x2F), (0x9C, 0xF6, 0xEC)),
}

# 精炼金属：颜色统一放在 palettes.py，锭 / 粒 / 尘三处共用一份
for _base, _colors in MATERIAL_COLORS.items():
    PALETTES[f"{_base}_ingot"] = _colors

# 白色星点：键为锭名，值是星心坐标。每颗星画成十字 —— 星心纯白，四臂往白里混 3/4，
# 于是看起来是嵌在晶体里的闪烁点，而不是贴上去的加号。星心必须落在锭的不透明区域里。
SPARKLES = {
    "mind_matrix_ingot": [(4, 6), (11, 5), (7, 11)],
    # 注能锭：星点比思维矩阵锭更密，突出「灌满了灵感」的感觉
    "energized_lapis_ingot": [(5, 5), (10, 7), (7, 10)],
    "energized_plasma_ingot": [(4, 7), (8, 5), (11, 9), (6, 11)],
    # 幽匿合金：呼应方块上的回响辉光颗粒
    "sculk_alloy": [(5, 6), (10, 9), (7, 4)],
}

# 星臂混入白色的比例
SPARKLE_ARM_WHITE = 0.55

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"


def draw_sparkles(out, centers):
    white = (255, 255, 255)
    for cx, cy in centers:
        for dx, dy in ((0, 0), (-1, 0), (1, 0), (0, -1), (0, 1)):
            x, y = cx + dx, cy + dy
            if not (0 <= x < out.size[0] and 0 <= y < out.size[1]):
                continue
            pixel = out.getpixel((x, y))
            if pixel[3] <= 20:
                continue
            color = white if (dx, dy) == (0, 0) else mix(pixel[:3], white, SPARKLE_ARM_WHITE)
            out.putpixel((x, y), color + (pixel[3],))


def build(name, dark, light):
    out = remap(Image.open(ITEM_DIR / f"{TEMPLATE}.png"), dark, light)
    draw_sparkles(out, SPARKLES.get(name, ()))
    return out


def main():
    names = sys.argv[1:] or list(PALETTES)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        if name not in PALETTES:
            raise SystemExit(f"未知的锭：{name}，可选 {sorted(PALETTES)}")
        dark, light = PALETTES[name]
        path = ITEM_DIR / f"{name}.png"
        build(name, dark, light).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
