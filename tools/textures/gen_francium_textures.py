"""钫盐系列与盖革计数器的贴图生成脚本。

钫系贴图以铯系为形状模板：把「有颜色」的像素（饱和度达标）整体旋转色相到钫的紫红色调，
灰色围岩与透明像素原样保留。色相偏移量不是写死的常数，而是「目标色相 − 模板的平均色相」，
这样贴图内部的明暗色偏（高光、阴影那点冷暖差）能完整保留下来。

**钫盐矿是例外**：矿长在地狱，底色得是地狱岩，所以不是整张换色，而是
「模板的矿脉像素（换色后）盖在 `base/netherrack.png` 上」。
底图是从原版资源包里抽出来的
（`build/moddev/artifacts/neoforge-*-client-extra-aka-minecraft-resources.jar`
内的 `assets/minecraft/textures/block/netherrack.png`），要换底材只换这一张。

用法：
    python tools/textures/gen_francium_textures.py
输出：
    src/main/resources/assets/inspiration/textures/item/<名字>.png
    src/main/resources/assets/inspiration/textures/block/<名字>.png
"""

import colorsys
import math
from pathlib import Path

from PIL import Image

# 目标贴图 ->（模板所在目录, 模板名, 目标色相角度）。钫取紫红系，与铯的黄橙系拉开距离。
TARGETS = {
    ("item", "raw_francium_salt"): ("item", "raw_cesium_salt", 292),
    ("item", "francium_salt"): ("item", "cesium_salt", 300),
    ("item", "francium_dust"): ("item", "cesium_dust", 305),
    ("item", "francium_ingot"): ("item", "cesium_ingot", 285),
    ("block", "francium_salt_block"): ("block", "cesium_salt_block", 296),
    ("block", "francium_block"): ("block", "cesium_block", 285),
}

# 地狱岩底的钫盐矿：模板矿脉换色后叠到 base 底图上
ORE_TARGET = ("block", "francium_salt_ore")
ORE_TEMPLATE = ("block", "cesium_salt_ore")
ORE_HUE = 300
ORE_BASE = ("base", "netherrack")

# 低于这个饱和度的像素视为灰色（石头、金属底、地狱岩），不参与换色、也不算矿脉
MIN_SATURATION = 0.25

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
TEXTURE_ROOT = Path(__file__).resolve().parent


def is_vein(r, g, b, a):
    """像素算不算「矿脉」：不透明且饱和度达标。"""
    if a < 20:
        return False
    _, s, _ = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    return s >= MIN_SATURATION


def hue_delta(pixels, target_hue_degrees):
    """色相偏移量 = 目标色相 − 有颜色像素的「圆均值」。"""
    x = y = count = 0.0
    for r, g, b, a in pixels:
        if not is_vein(r, g, b, a):
            continue
        h, _, _ = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
        x += math.cos(2 * math.pi * h)
        y += math.sin(2 * math.pi * h)
        count += 1
    if count == 0:
        return 0.0
    return target_hue_degrees / 360.0 - math.atan2(y / count, x / count) / (2 * math.pi) % 1.0


def shift(pixel, delta):
    r, g, b, a = pixel
    h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    nr, ng, nb = colorsys.hsv_to_rgb((h + delta) % 1.0, s, v)
    return (round(nr * 255), round(ng * 255), round(nb * 255), a)


def recolor(source, target_hue_degrees):
    """整张换色：只动矿脉/金属像素，灰色围岩与透明像素原样保留。"""
    image = Image.open(source).convert("RGBA")
    pixels = list(image.getdata())
    delta = hue_delta(pixels, target_hue_degrees)

    out = [shift(p, delta) if is_vein(*p) else p for p in pixels]

    result = Image.new("RGBA", image.size)
    result.putdata(out)
    return result


def ore_on_base(template, base, target_hue_degrees):
    """地狱岩底的矿石：矿脉像素换色后盖在底图上，其余像素取底图。"""
    src = Image.open(template).convert("RGBA")
    base_image = Image.open(base).convert("RGBA")
    if src.size != base_image.size:
        raise ValueError(f"模板与底图尺寸不一致：{src.size} vs {base_image.size}")

    src_pixels = list(src.getdata())
    delta = hue_delta(src_pixels, target_hue_degrees)

    out = []
    for pixel, base_pixel in zip(src_pixels, base_image.getdata()):
        out.append(shift(pixel, delta) if is_vein(*pixel) else base_pixel)

    result = Image.new("RGBA", src.size)
    result.putdata(out)
    return result


def geiger_counter():
    """盖革计数器：深色机身 + 绿色读数窗 + 侧面探针，16×16 手绘。"""
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    put = image.putpixel

    body = (58, 63, 69, 255)
    body_dark = (34, 38, 42, 255)
    screen = (124, 255, 176, 255)
    screen_dark = (30, 120, 78, 255)
    metal = (154, 163, 171, 255)
    grip = (110, 84, 52, 255)

    def rect(x0, y0, x1, y1, color):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                put((x, y), color)

    rect(2, 5, 12, 14, body)          # 机身
    rect(2, 5, 12, 5, body_dark)      # 顶部描边
    rect(2, 14, 12, 14, body_dark)    # 底部描边
    rect(3, 6, 8, 9, screen)          # 读数窗
    rect(4, 7, 7, 7, screen_dark)     # 读数窗里的指针
    rect(9, 10, 11, 13, grip)         # 握把
    rect(12, 6, 13, 12, metal)        # 侧面探针
    put((11, 3), metal)               # 天线
    rect(11, 4, 11, 4, metal)
    put((10, 2), screen)              # 顶端指示灯
    return image


def main():
    for (folder, name), (source_folder, source, hue) in TARGETS.items():
        path = RESOURCE_ROOT / folder / f"{name}.png"
        path.parent.mkdir(parents=True, exist_ok=True)
        recolor(RESOURCE_ROOT / source_folder / f"{source}.png", hue).save(path)
        print(f"written {path}")

    folder, name = ORE_TARGET
    source_folder, source = ORE_TEMPLATE
    base_folder, base = ORE_BASE
    path = RESOURCE_ROOT / folder / f"{name}.png"
    ore_on_base(
        RESOURCE_ROOT / source_folder / f"{source}.png",
        TEXTURE_ROOT / base_folder / f"{base}.png",
        ORE_HUE,
    ).save(path)
    print(f"written {path}")

    counter = RESOURCE_ROOT / "item" / "geiger_counter.png"
    geiger_counter().save(counter)
    print(f"written {counter}")


if __name__ == "__main__":
    main()
