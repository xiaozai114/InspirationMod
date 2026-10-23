"""液态等离子（熔融等离子）的贴图生成脚本。

熔融金属比灵感充能更热：底色是等离子锭那种天蓝，叠白热的火花，流动图是一条斜向亮带。
桶沿用原版水桶的桶形 —— 只把「蓝色系」的像素换成本次的配色，桶身的灰与描边原样保留，
于是桶的形状与原版完全一致，看起来只是里面装的东西换了。

用法：
    python tools/textures/gen_molten_plasma_fluid.py
输出：
    src/main/resources/assets/inspiration/textures/block/molten_plasma_still.png
    src/main/resources/assets/inspiration/textures/block/molten_plasma_flowing.png
    src/main/resources/assets/inspiration/textures/item/molten_plasma_bucket.png
"""

from pathlib import Path

from PIL import Image

SIZE = 16

# 与 palettes.py 里的等离子锭同色系：暗端深青蓝、亮端接近白青
BASE = (0x1A, 0x62, 0x96, 0xFF)
BASE_DARK = (0x0E, 0x3C, 0x66, 0xFF)
SPARK = (0xE8, 0xFF, 0xFF, 0xFF)
GLOW = (0x7A, 0xD8, 0xFF, 0xFF)

# 白热火花（熔融金属在流动时会翻起亮点）
SPARK_PIXELS = [
    (2, 3), (5, 1), (9, 4), (13, 2), (4, 7), (11, 8), (1, 11), (7, 12), (14, 12), (10, 14),
]
GLOW_PIXELS = [
    (3, 3), (6, 1), (10, 4), (12, 3), (5, 7), (12, 8), (2, 11), (6, 12), (13, 12), (9, 14),
]

BASE_DIR = Path(__file__).resolve().parent / "base"
BLOCK_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"
ITEM_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/item"


def draw_base(image: Image.Image) -> None:
    """上浅下深的底色。"""
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
    """流动图：亮点下移两行，再加一条斜向的流动亮带。"""
    image = Image.new("RGBA", (SIZE, SIZE))
    draw_base(image)
    draw_pixels(image, [(x, (y + 2) % SIZE) for x, y in GLOW_PIXELS], GLOW)
    draw_pixels(image, [(x, (y + 2) % SIZE) for x, y in SPARK_PIXELS], SPARK)
    for i in range(SIZE):
        image.putpixel((i, (i + 5) % SIZE), GLOW)
        image.putpixel((i, (i + 6) % SIZE), SPARK)
    return image


def is_liquid(pixel) -> bool:
    """原版水桶里「水」的判定：蓝色明显压过红、绿，且不透明。"""
    r, g, b, a = pixel
    return a > 0 and b > r + 20 and b > g + 10


def make_bucket() -> Image.Image:
    template = Image.open(BASE_DIR / "water_bucket.png").convert("RGBA")
    out = template.copy()
    liquid = [p for p in template.getdata() if is_liquid(p)]
    if not liquid:
        raise SystemExit("水桶模板里没找到蓝色像素，换色逻辑要重写")
    # 水色按亮度分档，映射到本次的 PLASMA 渐变
    shades = sorted(set(liquid), key=lambda p: 0.299 * p[0] + 0.587 * p[1] + 0.114 * p[2])
    dark, light = BASE[:3], SPARK[:3]
    ramp = [
        tuple(round(dark[i] + (light[i] - dark[i]) * index / max(1, len(shades) - 1)) for i in range(3)) + (255,)
        for index in range(len(shades))
    ]
    mapping = {shade: ramp[index] for index, shade in enumerate(shades)}
    out.putdata([mapping.get(p, p) for p in template.getdata()])
    return out


def main() -> None:
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    make_still().save(BLOCK_DIR / "molten_plasma_still.png")
    make_flowing().save(BLOCK_DIR / "molten_plasma_flowing.png")
    make_bucket().save(ITEM_DIR / "molten_plasma_bucket.png")
    print("wrote molten_plasma_still.png / molten_plasma_flowing.png / molten_plasma_bucket.png")


if __name__ == "__main__":
    main()
