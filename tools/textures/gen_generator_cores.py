"""发电核心 / 反应核心 / 核废料 的贴图生成脚本。

两种核心都是「已有核心换个炉心色」——金属菱形外框不动，只把炉心像素的色相转过去：

- 发电核心 = 强能核心（powerful_core）+ 炉心转绿；
- 反应核心 = 高能核心（high_energy_core）+ 炉心转红。

只改色相、不动明度，金属外框（低饱和度的灰）因此完全不受影响；
炉心像素判定为「饱和度 > 0.25」，低于这个值的灰白高光与金属都保持原样。
这样既保住原来那套炉心分层（深 → 亮），又只换颜色，不必重画两张图。

核废料是新画的一张：深灰绿的废料桶，桶身一圈发光绿带 + 桶顶泄出的两点残渣。

用法：
    python tools/textures/gen_generator_cores.py
输出：
    src/main/resources/assets/inspiration/textures/item/{generation_core,reaction_core,nuclear_waste}.png
"""

import colorsys
import math
from pathlib import Path

from PIL import Image

ITEM_DIR = (Path(__file__).resolve().parents[2]
            / "src/main/resources/assets/inspiration/textures/item")

# 炉心判定与目标色相（0~1 的 HSV 色相环）
MIN_SATURATION = 0.25
GREEN_HUE = 0.31      # 约 112°，明快的反应堆绿
RED_HUE = 0.0         # 纯红

# 核废料调色板
WASTE_DARK = (0x2A, 0x33, 0x2C, 0xFF)
WASTE_BODY = (0x3E, 0x4C, 0x40, 0xFF)
WASTE_LIT = (0x59, 0x6B, 0x5A, 0xFF)
WASTE_BAND = (0x7C, 0xE0, 0x8A, 0xFF)
WASTE_GLOW = (0xD8, 0xFF, 0xDE, 0xFF)


def shift_hue(img, target_hue):
    """把饱和的炉心像素转到目标色相，保留原有明度与饱和度。"""
    out = img.copy()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = out.getpixel((x, y))
            if a == 0:
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)
            if s < MIN_SATURATION:
                continue
            nr, ng, nb = colorsys.hsv_to_rgb(target_hue, s, v)
            out.putpixel((x, y), (round(nr * 255), round(ng * 255), round(nb * 255), a))
    return out


# 核废料上的发光裂纹：坐标是 16×16 里的像素位置，依序连成几条裂缝
WASTE_CRACKS = (
    ((3, 9), (4, 9), (5, 10), (6, 10), (7, 11)),
    ((7, 11), (8, 12), (9, 12), (10, 13)),
    ((7, 11), (7, 9), (8, 8), (8, 6), (9, 5)),
    ((5, 5), (6, 6), (6, 7)),
)


def nuclear_waste():
    """核废料：一块结壳的废料块，表面裂开发出绿色的光。"""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))

    def put(x, y, color):
        if 0 <= x < 16 and 0 <= y < 16:
            img.putpixel((x, y), color)

    # 不规则块体：椭圆裁形，顶面偏左打亮、右下压深
    for y in range(2, 15):
        for x in range(2, 14):
            dx = (x - 7.5) / 5.0
            dy = (y - 8.5) / 5.6
            # 半径按角度轻微起伏，避免裁出一个规整的蛋形
            theta = math.atan2(dy, dx)
            limit = 1.0 + 0.10 * math.sin(3 * theta) + 0.06 * math.cos(5 * theta)
            if dx * dx + dy * dy > limit:
                continue
            shade = (x - 2) + (y - 2)
            if shade < 10:
                put(x, y, WASTE_LIT)
            elif shade > 18:
                put(x, y, WASTE_DARK)
            else:
                put(x, y, WASTE_BODY)

    # 描边：贴着块体外沿补一圈深色，让轮廓在物品栏里立得住
    solid = {(x, y) for y in range(16) for x in range(16) if img.getpixel((x, y))[3] > 0}
    for x, y in solid:
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nx < 16 and 0 <= ny < 16 and img.getpixel((nx, ny))[3] == 0:
                put(x, y, WASTE_DARK)
                break

    # 裂纹：绿色本体 + 交点上一点最亮的残渣
    for crack in WASTE_CRACKS:
        for x, y in crack:
            if img.getpixel((x, y))[3] > 0:
                put(x, y, WASTE_BAND)
    put(7, 11, WASTE_GLOW)
    put(8, 6, WASTE_GLOW)
    return img


def main():
    ITEM_DIR.mkdir(parents=True, exist_ok=True)

    generation = shift_hue(Image.open(ITEM_DIR / "powerful_core.png").convert("RGBA"), GREEN_HUE)
    reaction = shift_hue(Image.open(ITEM_DIR / "high_energy_core.png").convert("RGBA"), RED_HUE)

    for name, image in (("generation_core", generation),
                        ("reaction_core", reaction),
                        ("nuclear_waste", nuclear_waste())):
        path = ITEM_DIR / f"{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
