"""16 px purple-steel controller shells. Each panel is drawn at native resolution."""
from pathlib import Path

from PIL import Image, ImageDraw


STEEL = {
    "s": "#44394f", "m": "#66546f", "l": "#957da4", "h": "#bbabbe",
    "k": "#211c2b", "b": "#342c40",
}
HOLOGRAM = {
    ".": "#142836", ":": "#1e404b", "+": "#28707a", "*": "#40a8ad",
    "o": "#75e1d7", "@": "#cbfff0",
}
SCANNER = {
    ".": "#2a212e", ":": "#493746", "+": "#75514e", "*": "#b77748",
    "o": "#edae58", "@": "#ffe3a1",
}

# The top and bottom rims remain visible at one block's normal viewing distance.
HOLO_FRONT = (
    "............",
    ".:...+*...:.",
    "...+*..o+...",
    ".+o..::..o+.",
    ".o.+*..*+.o.",
    ".o...o@...o.",
    ".o.:..o...*.",
    ".+o...o.:o+.",
    "...*+.o+*...",
    ".:...*o...:.",
)
ORE_FRONT = (
    "...::.....:.",
    "..:..:...:..",
    ".:....:.:.o.",
    "+++++..:.o*.",
    "o@@@@ooooooo",
    "....::.+*o..",
    ".++:..*o@*..",
    ":...:+o*..:.",
    "...:*o+..:..",
    ".::..:..:...",
)


def shell(casing):
    image = casing.copy().convert("RGB")
    draw = ImageDraw.Draw(image)
    draw.rectangle((1, 1, 14, 14), fill=STEEL["m"])
    draw.line((1, 1, 13, 1), fill=STEEL["h"])
    draw.line((1, 2, 1, 13), fill=STEEL["l"])
    draw.line((14, 2, 14, 14, 2, 14), fill=STEEL["s"])
    for x, y in ((1, 1), (13, 1), (1, 13), (13, 13)):
        draw.rectangle((x, y, x + 1, y + 1), fill=STEEL["s"])
        draw.point((x, y), fill=STEEL["h"])
    return image


def front(casing, panel, palette):
    image = shell(casing)
    draw = ImageDraw.Draw(image)
    draw.rectangle((1, 2, 14, 13), fill=STEEL["k"])
    for y, row in enumerate(panel, 3):
        for x, color in enumerate(row, 2):
            draw.point((x, y), fill=palette[color])
    draw.line((3, 13, 8, 13), fill=STEEL["l"])
    draw.point((11, 13), fill=palette["o"])
    draw.point((12, 13), fill=palette["*"])
    return image


def side(casing, palette, *, scanner):
    image = shell(casing)
    draw = ImageDraw.Draw(image)
    draw.rectangle((3, 3, 12, 11), fill=STEEL["b"])
    if scanner:
        # An armoured slotted exhaust next to an amber vertical level gauge.
        for y in (4, 6, 8, 10):
            draw.line((4, y + 1, 7, y + 1, 8, y), fill=STEEL["l"])
        draw.rectangle((10, 4, 11, 10), fill=palette[":"])
        for y in (6, 8, 10):
            draw.line((10, y, 11, y), fill=palette["o" if y == 10 else "*"])
    else:
        for x in (4, 7, 10):
            draw.line((x, 4, x, 9), fill=STEEL["l"])
            draw.line((x + 1, 4, x + 1, 9), fill=STEEL["k"])
        draw.line((4, 11, 9, 11, 11, 9), fill=palette["*"])
        draw.point((11, 9), fill=palette["@"])
    draw.line((4, 13, 11, 13), fill=STEEL["s"])
    return image


def top(casing, palette, *, scanner):
    image = shell(casing)
    draw = ImageDraw.Draw(image)
    if scanner:
        # Octagonal probe cap with a split brass ring and a dark central aperture.
        ring = [(5, 2), (10, 2), (13, 5), (13, 10), (10, 13), (5, 13), (2, 10), (2, 5)]
        draw.polygon(ring, fill=STEEL["k"], outline=STEEL["l"])
        draw.line((4, 6, 6, 4, 9, 4, 11, 6), fill=palette["o"])
        draw.line((11, 9, 9, 11, 6, 11, 4, 9), fill=palette["*"])
        draw.rectangle((6, 6, 9, 9), fill=palette[":"])
        draw.line((7, 8, 8, 7), fill=palette["@"])
    else:
        # A recessed projection lens held by opposed rails.
        draw.polygon([(7, 2), (8, 2), (13, 7), (13, 8), (8, 13), (7, 13), (2, 8), (2, 7)],
                     fill=STEEL["k"], outline=STEEL["l"])
        draw.polygon([(7, 4), (11, 8), (8, 11), (4, 7)], fill=palette["+"])
        draw.line((5, 7, 7, 5, 10, 8), fill=palette["o"])
        draw.rectangle((7, 7, 8, 8), fill=palette["@"])
        draw.line((2, 11, 4, 13), fill=STEEL["h"])
    return image


def controller_textures(casing):
    for name, panel, palette, scanner in (
        ("structure_simulator", HOLO_FRONT, HOLOGRAM, False),
        ("mineral_simulator", ORE_FRONT, SCANNER, True),
    ):
        yield f"block/{name}_front", front(casing, panel, palette)
        yield f"block/{name}_side", side(casing, palette, scanner=scanner)
        yield f"block/{name}_top", top(casing, palette, scanner=scanner)


if __name__ == "__main__":
    textures = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
    with Image.open(textures / "block/high_energy_machine_casing.png") as casing:
        for name, image in controller_textures(casing):
            image.save(textures / f"{name}.png")
