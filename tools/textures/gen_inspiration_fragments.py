"""结晶方块掉落的两种碎片物品贴图。

灵感碎片仿匠魂（Tinkers' Construct）的碎片：不规则多边形、深色描边、
内部用一条对角分界做出两个受光面。
思维晶体仿原版紫水晶碎片：细长的尖锥晶体，紫色系。

用法：
    python tools/textures/gen_inspiration_fragments.py              # 生成全部
    python tools/textures/gen_inspiration_fragments.py inspiration_shard
输出：
    src/main/resources/assets/inspiration/textures/item/<name>.png
"""

import sys
from pathlib import Path

from PIL import Image

SIZE = 16


class Shard:
    """一种碎片的外观参数。

    rows 是「y -> (起始 x, 结束 x 的下界)」的逐行跨度，用来画出不规则轮廓；
    fill / shade / edge 依次是主色、暗面、描边；highlight 是受光像素的坐标表。
    """

    def __init__(self, rows, fill, shade, edge, highlight_color,
                 highlight=(), split=0.55):
        self.rows = rows
        self.fill = fill
        self.shade = shade
        self.edge = edge
        self.highlight_color = highlight_color
        self.highlight = highlight
        self.split = split


FRAGMENTS = {
    # 匠魂风格的碎片：上宽下窄的歪斜多边形，只留一小段受光
    "inspiration_shard": Shard(
        rows={
            3: (7, 11), 4: (6, 11), 5: (5, 11), 6: (5, 10), 7: (4, 10),
            8: (4, 9), 9: (4, 9), 10: (5, 8), 11: (5, 8), 12: (6, 8),
            13: (7, 8),
        },
        fill=(0x9B, 0xE8, 0x6A, 255),
        shade=(0x63, 0xB4, 0x40, 255),
        edge=(0x2C, 0x6A, 0x22, 255),
        highlight_color=(0xE4, 0xFB, 0xC8, 255),
        highlight=((6, 5), (7, 4), (5, 7), (5, 8))),
    # 紫水晶风格的晶体：细长尖锥，越往下越窄
    "mind_crystal": Shard(
        rows={
            2: (8, 10), 3: (8, 10), 4: (7, 10), 5: (7, 10), 6: (7, 10),
            7: (6, 10), 8: (6, 9), 9: (6, 9), 10: (6, 9), 11: (7, 9),
            12: (7, 9), 13: (8, 9),
        },
        fill=(0xC9, 0x86, 0xF5, 255),
        shade=(0x7E, 0x3B, 0xB5, 255),
        edge=(0x3E, 0x1B, 0x60, 255),
        highlight_color=(0xF0, 0xD8, 0xFF, 255),
        highlight=((8, 4), (7, 5), (7, 6), (7, 7)),
        split=0.5),
}

ITEM_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/item"


def inside(rows, x, y):
    span = rows.get(y)
    return span is not None and span[0] <= x < span[1]


def build(spec):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))

    # 先铺主体：右半边受光更暗，做出两个切面
    for y, span in spec.rows.items():
        for x in range(span[0], span[1]):
            cut = span[0] + (span[1] - span[0]) * spec.split
            img.putpixel((x, y), spec.shade if x >= cut else spec.fill)

    # 高光：只点几颗亮像素，避免糊成一团
    for x, y in spec.highlight:
        if inside(spec.rows, x, y):
            img.putpixel((x, y), spec.highlight_color)

    # 描边：与轮廓外侧相邻的像素
    for y in range(SIZE):
        for x in range(SIZE):
            if inside(spec.rows, x, y):
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                if inside(spec.rows, x + dx, y + dy):
                    img.putpixel((x, y), spec.edge)
                    break

    return img


def main():
    names = sys.argv[1:] or list(FRAGMENTS)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        if name not in FRAGMENTS:
            raise SystemExit(f"未知的碎片：{name}，可选 {sorted(FRAGMENTS)}")
        path = ITEM_DIR / f"{name}.png"
        build(FRAGMENTS[name]).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
