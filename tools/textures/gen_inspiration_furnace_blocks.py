"""灵感高炉三档的方块贴图：同形换色，口径与其它机器一致（1px 描边 + 顶部高光 + 内凹面板）。

用法：
    python tools/textures/gen_inspiration_furnace_blocks.py
"""

from pathlib import Path

from PIL import Image

SIZE = 16
BLOCK_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"

TIERS = {
    "inspiration_furnace": ((0x7A, 0x4A, 0x28), (0x3A, 0x26, 0x18), (0xE6, 0xA9, 0x58)),
    "energized_inspiration_furnace": ((0x35, 0x69, 0x6E), (0x1A, 0x2B, 0x2D), (0x86, 0xE4, 0xE0)),
    "high_energy_inspiration_furnace": ((0x4B, 0x34, 0x68), (0x22, 0x18, 0x30), (0xD2, 0x90, 0xFF)),
}


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def base(body, frame):
    image = Image.new("RGBA", (SIZE, SIZE))
    for y in range(SIZE):
        for x in range(SIZE):
            edge = x in (0, 15) or y in (0, 15)
            image.putpixel((x, y), (frame if edge else body) + (255,))
    for y in range(1, 15):
        for x in range(1, 15):
            shade = 0.16 if y < 3 else (-0.12 if y > 12 else 0.0)
            color = mix(body, (255, 255, 255) if shade > 0 else (0, 0, 0), abs(shade))
            image.putpixel((x, y), color + (255,))
    return image


def front(body, frame, glow):
    image = base(body, frame)
    for y in range(5, 12):
        for x in range(4, 12):
            image.putpixel((x, y), tuple(max(0, c - 40) for c in body) + (255,))
    for y in range(6, 11):
        for x in range(5, 11):
            image.putpixel((x, y), mix(frame, (0, 0, 0), 0.35) + (255,))
    for x, y in [(6, 9), (7, 10), (8, 9), (9, 10), (7, 7), (8, 8)]:
        image.putpixel((x, y), glow + (255,))
    for x in range(5, 11):
        image.putpixel((x, 5), mix(glow, (255, 255, 255), 0.35) + (255,))
    return image


def side(body, frame, glow):
    image = base(body, frame)
    for y in range(3, 13):
        image.putpixel((3, y), tuple(max(0, c - 30) for c in body) + (255,))
        image.putpixel((12, y), tuple(max(0, c - 30) for c in body) + (255,))
    for x in range(6, 10):
        for y in (4, 8, 12):
            image.putpixel((x, y), frame + (255,))
    for y in (7, 11):
        image.putpixel((7, y), glow + (255,))
    return image


def top(body, frame, glow):
    image = base(body, frame)
    for x in (4, 7, 10):
        for y in range(4, 12):
            image.putpixel((x, y), frame + (255,))
            image.putpixel((x + 1, y), tuple(max(0, c - 25) for c in body) + (255,))
    for x, y in [(5, 5), (8, 8), (11, 10)]:
        image.putpixel((x, y), glow + (255,))
    return image


def main():
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    for name, (body, frame, glow) in TIERS.items():
        front(body, frame, glow).save(BLOCK_DIR / f"{name}_front.png")
        side(body, frame, glow).save(BLOCK_DIR / f"{name}_side.png")
        top(body, frame, glow).save(BLOCK_DIR / f"{name}_top.png")
    print("wrote inspiration furnace textures")


if __name__ == "__main__":
    main()
