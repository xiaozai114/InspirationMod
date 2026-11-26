"""Generate simulator sample-card screens, the End tungsten ore and the void-fluid bucket."""
from pathlib import Path

from PIL import Image, ImageDraw

from gen_simulator_textures import data_card

ROOT = Path(__file__).resolve().parents[2]
TEXTURES = ROOT / "src/main/resources/assets/inspiration/textures"
BASE = Path(__file__).resolve().parent / "base"

# Dark LCD, bright pixels, 6-by-5 pictogram. All cards keep the blank card's frame.
SCREENS = {
    "nether": ("#481622", "#ff9d39", "..#.../.##.#./.####./######/.####."),
    "end": ("#2f1545", "#f1dba6", "...#../..###./.#####/..###./...#.."),
    "mineshaft": ("#382b2b", "#d9ab69", ".#..#./######/.#..#./######/.#..#."),
    "monster_room": ("#26323e", "#94c9bf", "######/#.#..#/#.##.#/#..#.#/######"),
    "desert_pyramid": ("#573a26", "#ffcf74", "..##../..##../.####./.####./######"),
    "desert_well": ("#443e3b", "#e4c279", ".####./..##../.#..#./.#..#./######"),
    "jungle_temple": ("#1e3932", "#99c96d", "..##../.####./.#..#./######/##..##"),
    "igloo": ("#395776", "#d9f8ff", "..##../.####./######/####../###..."),
    "ocean_ruin": ("#21424f", "#74e9d4", "#..#../##.#../.#.#.#/.###.#/######"),
    "trail_ruins": ("#553836", "#e3a782", ".#..#./.##.#./###.#./#.####/######"),
    "buried_treasure": ("#543a31", "#ffdf78", "#....#/.#..#./..##../.#..#./#....#"),
    "village": ("#423222", "#edc681", "..##../.####./######/.#..#./.##.#."),
    "pillager_outpost": ("#353244", "#afbfcc", ".####./.####./..##../..##../.####."),
    "stronghold": ("#384037", "#9ae68c", "..##../.#..#./#.##.#/.#..#./..##.."),
    "woodland_mansion": ("#3d2927", "#d8ae83", "#....#/######/#.##.#/######/##..##"),
    "ancient_city": ("#112f3d", "#5aebdb", "#....#/.#..#./.####./..##../..##.."),
    "ruined_portal": ("#37263f", "#e68afb", ".####./.#..#./.#..../.#..#./.####."),
    "nether_fortress": ("#422536", "#ef7b54", "#.#..#/######/.#..#./.#..#./.#..#."),
    "bastion_remnant": ("#39313e", "#ffd15b", "##..##/######/.####./.#..#./.#..#."),
    "end_city": ("#36294c", "#e1adff", ".#..#./.####./...#../.####./.#..#."),
    "trial_chambers": ("#233d3d", "#eb9c63", ".####./##..##/#.##.#/##..##/.####."),
    "trial_vault": ("#3e3435", "#ffc878", ".##.../#..#../.###../...##./....##"),
    "ominous_vault": ("#23243c", "#73e2e7", ".##.#./#..#.#/.###../...##./....##"),
}


def make_card(background, foreground, glyph):
    image = data_card()
    draw = ImageDraw.Draw(image)
    draw.rectangle((5, 5, 10, 9), fill=background)
    for y, row in enumerate(glyph.split("/")):
        for x, pixel in enumerate(row):
            if pixel == "#":
                draw.point((5 + x, 5 + y), fill=foreground)
    return image


def tungsten_ore():
    image = Image.open(BASE / "end_stone.png").convert("RGBA")
    draw = ImageDraw.Draw(image)
    for x, y in [(1, 2), (10, 1), (6, 7), (12, 10), (2, 12)]:
        draw.polygon([(x, y + 1), (x + 2, y), (x + 3, y + 1),
                      (x + 2, y + 3), (x, y + 2)], fill="#393f55")
        draw.line((x, y + 1, x + 2, y), fill="#c4d7dc")
        draw.line((x + 1, y + 2, x + 2, y + 1), fill="#70859a")
    return image


def void_bucket():
    image = Image.open(BASE / "water_bucket.png").convert("RGBA")
    for y in range(image.height):
        for x in range(image.width):
            r, g, b, a = image.getpixel((x, y))
            if a and b > r + 20 and b > g + 10:
                image.putpixel((x, y), (int(b * .56), int(b * .34), int(b * .82), a))
    return image


if __name__ == "__main__":
    for name, screen in SCREENS.items():
        make_card(*screen).save(TEXTURES / "item" / f"{name}_data.png")
    tungsten_ore().save(TEXTURES / "block/tungsten_ore.png")
    void_bucket().save(TEXTURES / "item/void_fluid_bucket.png")
