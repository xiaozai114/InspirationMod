"""灵感充能流体的贴图生成脚本。

流体只在机器罐体中出现，因此贴图只需要静止与流动两张 16x16 图：
底色为深紫，叠加暖白的“灵感火花”噪点，流动图在竖直方向做流动条纹。

用法：
    python tools/textures/gen_inspiration_charge_fluid.py
输出：
    src/main/resources/assets/inspiration/textures/block/inspiration_charge_still.png
    src/main/resources/assets/inspiration/textures/block/inspiration_charge_flowing.png
"""

from pathlib import Path

from PIL import Image

SIZE = 16

BASE = (0x4A, 0x25, 0x6B, 0xFF)
BASE_DARK = (0x33, 0x18, 0x4D, 0xFF)
SPARK = (0xF2, 0xC8, 0xFF, 0xFF)
GLOW = (0xB5, 0x6B, 0xE8, 0xFF)

# 火花像素（x, y），对称布置以保证任意朝向都不显得偏斜。
SPARK_PIXELS = [(3, 2), (6, 4), (11, 3), (13, 6), (2, 9), (8, 8), (5, 12), (12, 11), (9, 14)]
# 火花周围的辉光像素。
GLOW_PIXELS = [(3, 3), (7, 4), (10, 4), (12, 7), (3, 8), (7, 8), (6, 11), (11, 12), (8, 13)]

OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"


def draw_base(image: Image.Image) -> None:
    """铺一层上浅下深的底色，模拟液体在容器中的明暗。"""
    for y in range(SIZE):
        for x in range(SIZE):
            image.putpixel((x, y), BASE if y < SIZE - 4 else BASE_DARK)


def draw_pixels(image: Image.Image, pixels, color) -> None:
    for x, y in pixels:
        image.putpixel((x, y), color)


def make_still() -> Image.Image:
    image = Image.new("RGBA", (SIZE, SIZE))
    draw_base(image)
    draw_pixels(image, GLOW_PIXELS, GLOW)
    draw_pixels(image, SPARK_PIXELS, SPARK)
    return image


def make_flowing() -> Image.Image:
    """流动图：把火花整体下移两行，并加一条贯穿的流动亮带。"""
    image = Image.new("RGBA", (SIZE, SIZE))
    draw_base(image)
    draw_pixels(image, [(x, (y + 2) % SIZE) for x, y in GLOW_PIXELS], GLOW)
    draw_pixels(image, [(x, (y + 2) % SIZE) for x, y in SPARK_PIXELS], SPARK)
    for x in range(SIZE):
        image.putpixel((x, 6), GLOW)
        image.putpixel((x, 7), SPARK)
    return image


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    make_still().save(OUTPUT_DIR / "inspiration_charge_still.png")
    make_flowing().save(OUTPUT_DIR / "inspiration_charge_flowing.png")
    print("wrote inspiration_charge_still.png / inspiration_charge_flowing.png")


if __name__ == "__main__":
    main()
