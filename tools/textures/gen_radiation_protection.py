"""Generate the radiation equipment textures from Minecraft 1.21.1 client assets."""
import argparse
from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
PIECES = ("helmet", "chestplate", "leggings", "boots")
JADE = ((12, 32, 32), (22, 69, 64), (40, 110, 91), (89, 169, 125), (167, 225, 164))
MINT = (152, 246, 195)
GOLD = (249, 222, 123)
BRASS = (175, 145, 75)


def texture(archive, name):
    return Image.open(BytesIO(archive.read("assets/minecraft/textures/" + name + ".png"))).convert("RGBA")


def jade_steel(image):
    # Preserve the shading, transparent cutouts and UV layout of the parent armor.
    colors = sorted({sum(px[:3]) for px in image.getdata() if px[3]})
    for y in range(image.height):
        for x in range(image.width):
            r, g, b, alpha = image.getpixel((x, y))
            if alpha:
                position = colors.index(r + g + b) * (len(JADE) - 1) / max(1, len(colors) - 1)
                lower = int(position)
                fraction = position - lower
                upper = min(lower + 1, len(JADE) - 1)
                color = tuple(round(a + (b - a) * fraction) for a, b in zip(JADE[lower], JADE[upper]))
                image.putpixel((x, y), (*color, alpha))
    return image


def accent(image, points, color):
    for point in points:
        if image.getpixel(point)[3]:
            image.putpixel(point, (*color, 255))


def armor(archive):
    details = {
        "helmet": [(4, 7), (5, 7), (10, 7), (11, 7)],
        "chestplate": [(6, 6), (7, 7), (8, 7), (9, 6), (7, 9), (8, 9)],
        "leggings": [(5, 4), (6, 4), (9, 4), (10, 4), (4, 10), (11, 10)],
        "boots": [(4, 8), (5, 8), (10, 8), (11, 8)],
    }
    for piece in PIECES:
        image = jade_steel(texture(archive, "item/netherite_" + piece))
        accent(image, details[piece], GOLD)
        image.save(ROOT / ("item/energized_jade_steel_" + piece + ".png"))
    for layer in (1, 2):
        image = jade_steel(texture(archive, f"models/armor/netherite_layer_{layer}"))
        if layer == 1:
            accent(image, [(9, 11), (10, 11), (13, 11), (14, 11),
                           (21, 22), (22, 23), (25, 23), (26, 22), (23, 24), (24, 24)], GOLD)
        else:
            accent(image, [(x, 20) for x in range(20, 28)], GOLD)
        image.save(ROOT / f"models/armor/energized_jade_steel_layer_{layer}.png")


def apples(archive):
    for enchanted, name in [(False, "energized_golden_apple"), (True, "energized_enchanted_golden_apple")]:
        image = texture(archive, "item/golden_apple")
        draw = ImageDraw.Draw(image)
        draw.line([(4, 8), (6, 8), (7, 6), (8, 10), (9, 8), (11, 8)], fill=(32, 123, 105))
        draw.point((7, 7), fill=MINT)
        draw.point((9, 8), fill=MINT)
        if enchanted:
            draw.line([(11, 3), (12, 4), (13, 3), (12, 2), (11, 3)], fill=(232, 182, 255))
        image.save(ROOT / f"item/{name}.png")


def machine():
    for face in ("front", "top", "side"):
        image = Image.new("RGBA", (16, 16), (43, 48, 49))
        draw = ImageDraw.Draw(image)
        draw.rectangle((1, 1, 14, 14), fill=(81, 89, 87), outline=(110, 116, 108))
        draw.rectangle((3, 3, 12, 12), fill=(34, 41, 41), outline=(45, 58, 54))
        if face == "front":
            draw.rectangle((4, 5, 11, 10), fill=(22, 49, 43), outline=BRASS)
            draw.line([(5, 8), (6, 8), (7, 6), (8, 9), (9, 7), (10, 7)], fill=MINT)
            draw.line((4, 12, 11, 12), fill=BRASS)
        elif face == "top":
            for start in (30, 150, 270):
                draw.pieslice((3, 3, 12, 12), start, start + 58, fill=BRASS)
            draw.ellipse((6, 6, 9, 9), fill=(28, 42, 37))
            draw.rectangle((7, 7, 8, 8), fill=MINT)
        else:
            for y in (4, 7, 10):
                draw.line((4, y, 11, y), fill=(112, 132, 114))
                draw.line((5, y + 1, 10, y + 1), fill=(20, 36, 34))
        image.save(ROOT / f"block/radiation_absorber_{face}.png")


def resistance():
    image = Image.new("RGBA", (18, 18))
    draw = ImageDraw.Draw(image)
    draw.polygon([(9, 1), (16, 4), (15, 10), (12, 14), (9, 16), (6, 14), (3, 10), (2, 4)],
                 fill=(34, 87, 75), outline=MINT)
    for start in (30, 150, 270):
        draw.pieslice((5, 4, 13, 12), start, start + 58, fill=GOLD)
    draw.ellipse((8, 7, 10, 9), fill=(34, 87, 75))
    draw.point((9, 8), fill=MINT)
    image.save(ROOT / "mob_effect/radiation_resistance.png")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("client_assets", type=Path)
    with ZipFile(parser.parse_args().client_assets) as archive:
        armor(archive)
        apples(archive)
    machine()
    resistance()
