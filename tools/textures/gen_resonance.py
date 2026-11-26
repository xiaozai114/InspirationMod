"""Generate the resonance machines and materials using the void-energy palette."""
from PIL import Image, ImageDraw
from gen_void_energy import BLOCK_DIR, ITEM_DIR, ingot, machine, shift_hue

FRAME = (45, 36, 66)
STEEL = (86, 76, 104)
VIOLET = (184, 126, 255)
MINT = (128, 245, 219)
WHITE = (239, 246, 255)


def panel():
    image = machine(STEEL, FRAME, VIOLET, [])
    draw = ImageDraw.Draw(image)
    for x, y in [(1, 1), (13, 1), (1, 13), (13, 13)]:
        draw.point((x, y), fill=(170, 158, 181))
    return image


def machines():
    front = panel()
    draw = ImageDraw.Draw(front)
    draw.ellipse((3, 3, 12, 12), outline=VIOLET)
    draw.line([(4, 8), (6, 8), (7, 5), (9, 10), (10, 7), (12, 7)], fill=MINT)
    front.save(BLOCK_DIR / "resonance_chamber_front.png")

    side = panel()
    draw = ImageDraw.Draw(side)
    for y in (4, 7, 10):
        draw.line((3, y, 12, y), fill=FRAME)
        draw.line((5, y + 1, 10, y + 1), fill=VIOLET)
    side.save(BLOCK_DIR / "resonance_chamber_side.png")

    top = panel()
    draw = ImageDraw.Draw(top)
    draw.ellipse((4, 4, 11, 11), outline=MINT)
    draw.line((6, 8, 9, 8), fill=WHITE)
    top.save(BLOCK_DIR / "resonance_chamber_top.png")

    collector = panel()
    draw = ImageDraw.Draw(collector)
    for x, y in ((3, 3), (12, 3), (3, 12), (12, 12)):
        draw.line((x, y, 7 if x < 8 else 8, 7 if y < 8 else 8), fill=VIOLET)
    draw.ellipse((5, 5, 10, 10), outline=MINT)
    draw.rectangle((7, 7, 8, 8), fill=WHITE)
    collector.save(BLOCK_DIR / "void_energy_collector.png")


def materials():
    quantum = ingot((27, 55, 95), (65, 160, 191), (173, 252, 247))
    draw = ImageDraw.Draw(quantum)
    draw.line((5, 8, 10, 6), fill=WHITE)
    quantum.save(ITEM_DIR / "quantum_ingot.png")

    end = shift_hue(Image.open(ITEM_DIR / "end_alloy.png").convert("RGBA"), 0.73)
    draw = ImageDraw.Draw(end)
    draw.line([(4, 9), (6, 9), (7, 7), (9, 7), (10, 5)], fill=MINT)
    end.save(ITEM_DIR / "resonant_end_alloy.png")

    inspiration = shift_hue(Image.open(ITEM_DIR / "inspiration.png").convert("RGBA"), 0.88)
    draw = ImageDraw.Draw(inspiration)
    for x, y in ((3, 4), (12, 5), (11, 12)):
        draw.point((x, y), fill=WHITE)
    inspiration.save(ITEM_DIR / "resonant_inspiration.png")

    lattice = Image.new("RGBA", (16, 16))
    draw = ImageDraw.Draw(lattice)
    corners = [(7, 1), (13, 5), (13, 10), (7, 14), (2, 10), (2, 5)]
    draw.line(corners + corners[:1], fill=VIOLET, width=2)
    for x, y in corners:
        draw.line((7, 7, x, y), fill=MINT)
        draw.point((x, y), fill=WHITE)
    draw.rectangle((6, 6, 8, 8), fill=WHITE)
    lattice.save(ITEM_DIR / "resonant_mind_lattice.png")


if __name__ == "__main__":
    machines()
    materials()
