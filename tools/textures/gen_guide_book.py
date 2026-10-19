"""灵感指导书的物品贴图。

手绘的 16×16 小书：深紫封面 + 金色书脊 + 中央一颗灵感星。
配色沿用灵感的紫（`PURPLE_*` 与无恒物质同一族）与金，一眼能和「书 + 灵感」这条配方对得上。

用法：
    python tools/textures/gen_guide_book.py
输出：
    src/main/resources/assets/inspiration/textures/item/guide_book.png
"""

from pathlib import Path

from PIL import Image

TEXTURE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = TEXTURE_ROOT / "item"

SIZE = 16

COVER = (0x4A, 0x28, 0x78, 255)        # 封面主色
COVER_DARK = (0x2E, 0x17, 0x4B, 255)   # 封面暗部（底边、右侧）
COVER_LIT = (0x6B, 0x3E, 0xA8, 255)    # 封面受光边（顶边、左边）
SPINE = (0xC9, 0xA2, 0x27, 255)        # 金色书脊
SPINE_LIT = (0xE8, 0xC9, 0x5A, 255)
PAGES = (0xEF, 0xE3, 0xC8, 255)        # 露出的书页
PAGES_DARK = (0xC4, 0xB4, 0x92, 255)
STAR = (0xF6, 0xEF, 0xFF, 255)         # 灵感星
STAR_GLOW = (0xC0, 0x8B, 0xF0, 255)


def build():
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))

    def p(x, y, color):
        if 0 <= x < SIZE and 0 <= y < SIZE:
            img.putpixel((x, y), color)

    def rect(x0, y0, x1, y1, color):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                p(x, y, color)

    # 书页：封面右侧露出的一叠纸
    rect(11, 2, 13, 13, PAGES)
    for y in range(3, 13, 2):
        p(12, y, PAGES_DARK)
        p(13, y, PAGES_DARK)
    p(11, 13, PAGES_DARK)

    # 封面主体
    rect(2, 1, 11, 14, COVER)
    rect(2, 1, 11, 1, COVER_LIT)        # 顶部受光
    for y in range(1, 15):
        p(2, y, COVER_LIT)              # 左侧受光
    rect(2, 14, 11, 14, COVER_DARK)     # 底部压深
    for y in range(2, 15):
        p(11, y, COVER_DARK)            # 右侧压深（贴住书页的阴影）

    # 书脊
    rect(2, 1, 3, 14, SPINE)
    for y in range(1, 15):
        p(2, y, SPINE_LIT)
    p(3, 3, SPINE_LIT)                  # 书脊上的两处烫金
    p(3, 12, SPINE_LIT)

    # 中央的灵感星：十字星心 + 四条淡紫流光
    cx, cy = 6, 7
    p(cx, cy, STAR)
    for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        p(cx + dx, cy + dy, STAR)
    for dx, dy in ((2, 0), (-2, 0), (0, 2), (0, -2)):
        p(cx + dx, cy + dy, STAR_GLOW)
    return img


def main():
    path = ITEM_DIR / "guide_book.png"
    build().save(path)
    print(f"written {path}")


if __name__ == "__main__":
    main()
