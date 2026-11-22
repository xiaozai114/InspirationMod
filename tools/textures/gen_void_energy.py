"""虚空能量体系的贴图：物品、方块与团簇实体。

能复用的一律复用（换色 / 去饱和），只有确实没有模板的才新画：
- 幽匿合金钻头 = 玄武岩合金钻头换幽匿青
- 弱化核心 = 强化核心把炉心去饱和成灰
- 虚空等离子锭 = 等离子锭换虚空紫
- 虚空合金 = 末地合金换虚空紫
- 不稳金属 / 虚空露珠 / 零点模块 / 零点增幅器 / 三种方块 / 团簇光斑：新画

用法：
    python tools/textures/gen_void_energy.py
"""

import colorsys
import math
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = ROOT / "item"
BLOCK_DIR = ROOT / "block"
ENTITY_DIR = ROOT / "entity"

VOID_DARK = (0x24, 0x12, 0x3A)
VOID_BODY = (0x4A, 0x24, 0x74)
VOID_LIT = (0x8B, 0x4D, 0xD6)
VOID_GLOW = (0xE0, 0xB6, 0xFF)
SCULK_TEAL = (0x0E, 0x5A, 0x55)
SCULK_LIT = (0x2F, 0xC7, 0xB0)


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def shift_hue(img, target_hue, min_saturation=0.22, min_value=0.05):
    out = img.copy()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = out.getpixel((x, y))
            if a == 0:
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)
            if s < min_saturation or v < min_value:
                continue
            nr, ng, nb = colorsys.hsv_to_rgb(target_hue, max(s, 0.35), v)
            out.putpixel((x, y), (round(nr * 255), round(ng * 255), round(nb * 255), a))
    return out


def desaturate_center(img, min_saturation=0.18):
    """把饱和的炉心像素压成灰，金属外框与高光保持不变。"""
    out = img.copy()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = out.getpixel((x, y))
            if a == 0:
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)
            if s < min_saturation:
                continue
            grey = round(v * 210)
            out.putpixel((x, y), (grey, grey, grey, a))
    return out


def ingot(dark, body, lit):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    # 梯形锭体：上窄下宽
    for y in range(6, 13):
        inset = 6 if y < 8 else (4 if y < 11 else 5)
        for x in range(inset, 16 - inset):
            img.putpixel((x, y), body + (255,))
    for x in range(5, 11):
        img.putpixel((x, 5), lit + (255,))
    for x in range(4, 12):
        img.putpixel((x, 12), dark + (255,))
    for y in range(6, 10):
        for x in range(4, 6):
            img.putpixel((x, y), lit + (255,))
    return img


def core(dark, body, lit, glow):
    """16×16 的菱形核心：外框 + 炉心 + 两点高光。"""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            d = abs(x - 7.5) + abs(y - 7.5)
            if d > 7.4:
                continue
            if d > 5.6:
                img.putpixel((x, y), dark + (255,))
            elif d > 2.6:
                img.putpixel((x, y), body + (255,))
            else:
                img.putpixel((x, y), lit + (255,))
    img.putpixel((7, 7), glow + (255,))
    img.putpixel((6, 6), glow + (255,))
    img.putpixel((8, 8), dark + (255,))
    return img


def drop():
    """水滴：下部圆腹 + 上部细颈，左上一道高光。"""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(2, 14):
        for x in range(16):
            dx = abs(x - 7.5)
            dy = y - 9.5
            in_body = dx * dx + dy * dy <= 3.3 ** 2
            neck_half = 0.5 + (y - 2) * 0.42
            in_neck = 2 <= y <= 8 and dx <= neck_half
            if not (in_body or in_neck):
                continue
            shade = mix(VOID_GLOW, VOID_LIT, min(1.0, dx / 3.6 + (y - 2) / 16.0))
            img.putpixel((x, y), shade + (255,))
    img.putpixel((6, 9), (255, 255, 255, 255))
    img.putpixel((6, 10), VOID_GLOW + (255,))
    return img


def module():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            if abs(x - 7.5) > 6.2 or abs(y - 7.5) > 6.2:
                continue
            dist = math.hypot(x - 7.5, y - 7.5)
            if dist > 5.6:
                img.putpixel((x, y), VOID_DARK + (255,))
            elif dist > 3.4:
                img.putpixel((x, y), (0x3A, 0x3A, 0x44, 0xFF))
            elif dist > 1.6:
                img.putpixel((x, y), (0x6E, 0x6E, 0x7C, 0xFF))
            else:
                img.putpixel((x, y), (0x18, 0x18, 0x20, 0xFF))
    img.putpixel((7, 7), VOID_GLOW + (255,))
    return img


def amplifier():
    img = core(VOID_DARK, VOID_BODY, SCULK_TEAL, SCULK_LIT)
    for x, y in ((4, 4), (11, 4), (4, 11), (11, 11)):
        img.putpixel((x, y), VOID_GLOW + (255,))
    return img


def cracked_bedrock():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            noise = ((x * 7 + y * 13) % 5) / 5.0
            img.putpixel((x, y), mix((0x2E, 0x2E, 0x33), (0x55, 0x55, 0x5C), noise) + (255,))
    for crack in (
        ((2, 3), (4, 4), (5, 6), (7, 7)),
        ((7, 7), (9, 6), (11, 7), (13, 6)),
        ((7, 7), (6, 9), (7, 12), (6, 14)),
        ((7, 7), (10, 9), (11, 11)),
    ):
        for x, y in crack:
            img.putpixel((x, y), (0x14, 0x14, 0x18, 0xFF))
    for x, y in ((7, 7), (5, 6), (9, 6), (7, 12)):
        img.putpixel((x, y), (0x6B, 0x2F, 0x9E, 0xFF))
    return img


def machine(body, frame, glow, core_pixels):
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            edge = x in (0, 15) or y in (0, 15)
            img.putpixel((x, y), (frame if edge else body) + (255,))
    for y in range(3, 13):
        for x in range(3, 13):
            img.putpixel((x, y), mix(body, (0, 0, 0), 0.35) + (255,))
    for x, y in core_pixels:
        img.putpixel((x, y), glow + (255,))
    return img


def cluster_glow():
    size = 32
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    for y in range(size):
        for x in range(size):
            dist = math.hypot(x - (size - 1) / 2, y - (size - 1) / 2) / (size / 2)
            if dist > 1:
                continue
            alpha = round(255 * (1 - dist) ** 2)
            img.putpixel((x, y), (255, 255, 255, alpha))
    return img


def main():
    for folder in (ITEM_DIR, BLOCK_DIR, ENTITY_DIR):
        folder.mkdir(parents=True, exist_ok=True)

    base = lambda name: Image.open(ITEM_DIR / f"{name}.png").convert("RGBA")

    shift_hue(base("basalt_alloy_drill_bit"), 0.47, min_saturation=0.02).save(ITEM_DIR / "sculk_alloy_drill_bit.png")
    desaturate_center(base("reinforced_core")).save(ITEM_DIR / "weakened_core.png")
    shift_hue(base("plasma_ingot"), 0.78).save(ITEM_DIR / "void_plasma_ingot.png")
    shift_hue(base("end_alloy"), 0.78).save(ITEM_DIR / "void_alloy.png")

    drop().save(ITEM_DIR / "void_dew.png")
    module().save(ITEM_DIR / "null_point_module.png")
    amplifier().save(ITEM_DIR / "zero_point_amplifier.png")

    cracked_bedrock().save(BLOCK_DIR / "cracked_bedrock.png")
    machine(VOID_BODY, VOID_DARK, VOID_GLOW,
            [(7, 7), (8, 8), (6, 8), (8, 6), (7, 11)]).save(BLOCK_DIR / "void_energy_extractor.png")
    machine(VOID_DARK, (0x14, 0x0A, 0x22), SCULK_LIT,
            [(7, 5), (9, 7), (7, 9), (5, 7), (7, 7)]).save(BLOCK_DIR / "void_energy_relay.png")

    machine(VOID_BODY, VOID_DARK, VOID_GLOW,
            [(7, 7), (8, 8), (6, 8), (8, 6), (7, 11)]).save(BLOCK_DIR / "void_condenser_front.png")
    machine(VOID_BODY, VOID_DARK, SCULK_LIT, [(4, 4), (4, 11), (11, 4), (11, 11)]) \
        .save(BLOCK_DIR / "void_condenser_side.png")
    machine(VOID_DARK, (0x14, 0x0A, 0x22), VOID_GLOW, [(7, 4), (7, 7), (7, 10)]) \
        .save(BLOCK_DIR / "void_condenser_top.png")

    machine((0x20, 0x3A, 0x2E), (0x0E, 0x1A, 0x14), (0x7B, 0xFF, 0xC4),
            [(7, 7), (8, 8), (6, 8), (8, 6), (7, 11)]).save(BLOCK_DIR / "zero_point_generator_front.png")
    machine((0x20, 0x3A, 0x2E), (0x0E, 0x1A, 0x14), (0x7B, 0xFF, 0xC4),
            [(4, 4), (4, 11), (11, 4), (11, 11)]).save(BLOCK_DIR / "zero_point_generator_side.png")
    machine((0x0E, 0x1A, 0x14), (0x08, 0x10, 0x0C), (0xDC, 0xFF, 0xEE),
            [(7, 4), (7, 7), (7, 10)]).save(BLOCK_DIR / "zero_point_generator_top.png")

    cluster_glow().save(ENTITY_DIR / "void_cluster.png")
    print("wrote void energy textures")


if __name__ == "__main__":
    main()
