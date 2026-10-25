"""缸中之脑产线的贴图：大脑、寄生虫、营养液、培养缸、寄生图标。

用法：
    python tools/textures/gen_brain_line.py
"""

import math
from pathlib import Path

from PIL import Image

from recolor import remap

ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
BASE_DIR = Path(__file__).resolve().parent / "base"

VILLAGER = ((0x8C, 0x4A, 0x56), (0xF2, 0xB7, 0xBC))
ENDERMAN = ((0x2B, 0x1B, 0x3D), (0xC5, 0x6B, 0xE0))
INACTIVE = ((0x4A, 0x44, 0x3C), (0x9C, 0x94, 0x8A))
PARASITE = ((0x33, 0x53, 0x1F), (0xA8, 0xD4, 0x6B))
MECHANICAL = ((0x3A, 0x42, 0x47), (0x9F, 0xE8, 0xE0))
NUTRIENT = ((0x4E, 0x5C, 0x1A), (0xCB, 0xE0, 0x7A))

BRAIN_LOBES = [(5.5, 6.5, 3.6), (9.5, 5.5, 3.2), (8.5, 9.0, 3.4), (11.5, 8.0, 2.2), (4.0, 9.5, 2.0)]
VILLAGER_WRINKLES = [(6, 5), (7, 5), (8, 6), (9, 7), (7, 8), (6, 9), (10, 4), (5, 7), (9, 10)]
ENDERMAN_WRINKLES = [(6, 5), (7, 6), (8, 7), (6, 8), (9, 5), (10, 8), (5, 7), (8, 10)]

PARASITE_PATH = [(4.5, 12.0), (5.5, 10.5), (7.0, 9.5), (8.5, 9.0), (9.5, 7.5), (10.5, 6.0), (11.0, 4.5)]


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def depth(x, y, lobes):
    return -min(math.hypot(x - cx, y - cy) - r for cx, cy, r in lobes)


def make_brain(palette, wrinkles):
    dark, light = palette
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            d = depth(x + 0.5, y + 0.5, BRAIN_LOBES)
            if d <= 0:
                continue
            t = min(1.0, d / 2.0)
            color = mix(dark, light, 0.2 + 0.8 * t)
            image.putpixel((x, y), color + (255,))
    for x, y in wrinkles:
        if image.getpixel((x, y))[3] > 0:
            image.putpixel((x, y), dark + (255,))
    return image


def make_parasite(palette, eye):
    dark, light = palette
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            cx, cy = x + 0.5, y + 0.5
            d = -min(math.hypot(cx - px, cy - py) - 1.5 for px, py in PARASITE_PATH)
            if d <= 0:
                continue
            t = min(1.0, d / 1.4)
            image.putpixel((x, y), mix(dark, light, 0.25 + 0.75 * t) + (255,))
    image.putpixel((11, 4), eye + (255,))
    image.putpixel((10, 4), dark + (255,))
    return image


def make_fluid_still():
    dark, light = NUTRIENT
    image = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            image.putpixel((x, y), mix(dark, light, 0.15 + 0.5 * (y / 15)) + (255,))
    for x, y in [(3, 3), (9, 2), (6, 7), (12, 6), (2, 11), (8, 12), (13, 11), (5, 14)]:
        image.putpixel((x, y), light + (255,))
    return image


def make_fluid_flowing():
    dark, light = NUTRIENT
    image = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            band = 0.5 + 0.5 * math.sin((x + y * 0.6) * math.pi / 5.0)
            image.putpixel((x, y), mix(dark, light, 0.1 + 0.6 * band) + (255,))
    return image


def make_bucket():
    template = Image.open(BASE_DIR / "water_bucket.png").convert("RGBA")
    liquids = [p for p in template.getdata() if p[3] > 0 and p[2] > p[0] + 20 and p[2] > p[1] + 10]
    shades = sorted(set(liquids), key=lambda p: 0.299 * p[0] + 0.587 * p[1] + 0.114 * p[2])
    dark, light = NUTRIENT
    ramp = [mix(dark, light, i / max(1, len(shades) - 1)) + (255,) for i in range(len(shades))]
    mapping = {shade: ramp[i] for i, shade in enumerate(shades)}
    out = template.copy()
    out.putdata([mapping.get(p, p) for p in template.getdata()])
    return out


def make_tank(face):
    frame = (0x3B, 0x44, 0x3A)
    frame_light = (0x6E, 0x7C, 0x6C)
    glass = (0x8F, 0xB4, 0x7A)
    glass_light = (0xD6, 0xEC, 0xB4)
    image = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            edge = x in (0, 15) or y in (0, 15)
            image.putpixel((x, y), (frame if edge else frame_light) + (255,))
    for y in range(1, 15):
        for x in range(1, 15):
            image.putpixel((x, y), glass + (255,))
    if face == "front":
        for y in range(3, 13):
            for x in range(3, 13):
                image.putpixel((x, y), (0x4E, 0x60, 0x42) + (255,))
        for y in range(4, 12):
            for x in range(4, 12):
                image.putpixel((x, y), NUTRIENT[1] + (255,))
        for x, y in [(5, 6), (6, 6), (7, 7), (8, 8), (6, 9), (9, 6), (10, 9)]:
            image.putpixel((x, y), VILLAGER[1] + (255,))
        for y in range(13, 15):
            image.putpixel((7, y), frame)
            image.putpixel((8, y), frame)
    elif face == "top":
        for y in range(4, 12):
            for x in range(4, 12):
                image.putpixel((x, y), (0x4E, 0x60, 0x42) + (255,))
        for x, y in [(6, 6), (7, 6), (8, 6), (9, 6), (10, 6), (5, 7), (6, 7), (7, 7), (8, 7), (9, 7),
                     (10, 7), (5, 8), (6, 8), (7, 8), (8, 8), (9, 8), (10, 8), (11, 8), (6, 9), (7, 9),
                     (8, 9), (9, 9), (10, 9)]:
            image.putpixel((x, y), glass_light + (255,))
    else:
        for y in range(2, 14):
            image.putpixel((2, y), frame_light)
            image.putpixel((13, y), frame_light)
        for x in range(2, 14):
            image.putpixel((x, 7), frame)
    return image


def make_effect(palette, eye):
    icon = make_parasite(palette, eye).resize((18, 18), Image.NEAREST)
    out = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    out.paste(icon, (0, 0), icon)
    return out


def main():
    item_dir = ROOT / "item"
    block_dir = ROOT / "block"
    effect_dir = ROOT / "mob_effect"
    for folder in (item_dir, block_dir, effect_dir):
        folder.mkdir(parents=True, exist_ok=True)

    make_brain(VILLAGER, VILLAGER_WRINKLES).save(item_dir / "villager_brain.png")
    make_brain(ENDERMAN, ENDERMAN_WRINKLES).save(item_dir / "enderman_brain.png")
    make_brain(INACTIVE, VILLAGER_WRINKLES).save(item_dir / "inactive_brain.png")
    make_parasite(PARASITE, (0xC8, 0xE8, 0x8A)).save(item_dir / "parasite.png")
    make_parasite(MECHANICAL, (0x7F, 0xE8, 0xE0)).save(item_dir / "mechanical_parasite.png")
    make_bucket().save(item_dir / "nutrient_fluid_bucket.png")

    make_fluid_still().save(block_dir / "nutrient_fluid_still.png")
    make_fluid_flowing().save(block_dir / "nutrient_fluid_flowing.png")

    for face, name in (("front", "front"), ("side", "side"), ("top", "top")):
        make_tank(face).save(block_dir / f"culture_tank_{name}.png")

    make_effect(PARASITE, (0xC8, 0xE8, 0x8A)).save(effect_dir / "parasite.png")
    make_effect(MECHANICAL, (0x7F, 0xE8, 0xE0)).save(effect_dir / "mechanical_parasite.png")
    print("wrote brain line textures")


if __name__ == "__main__":
    main()
