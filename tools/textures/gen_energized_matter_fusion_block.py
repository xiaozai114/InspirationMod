"""强能物质融合机的方块贴图生成脚本。

沿用物质融合器那一套「1px 描边 + 顶部高光 + 内凹面板」的机器语言，
但做了三处区分：机身压成偏紫的深灰蓝、面板外圈改成更亮的方环、
核心从橙点换成淡紫色的 2x2 方芯 —— 一眼能看出是同一系列的强化版。

用法：
    python tools/textures/gen_energized_matter_fusion_block.py
输出：
    src/main/resources/assets/inspiration/textures/block/energized_matter_fusion_machine_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 调色板
BODY = (0x3C, 0x36, 0x4A, 0xFF)       # 机身主体：偏紫的深灰蓝
BORDER = (0x14, 0x11, 0x1C, 0xFF)     # 外描边
BEVEL = (0x26, 0x21, 0x33, 0xFF)      # 倒角 / 阴影
PLATE = (0x1B, 0x17, 0x26, 0xFF)      # 面板底板
DARK = (0x0B, 0x09, 0x11, 0xFF)       # 面板内凹
GLOW = (0x6F, 0xE8, 0xFF, 0xFF)       # 外圈方环：亮青
GLINT = (0xD8, 0xA8, 0xFF, 0xFF)      # 高光 / 核心亮点：淡紫
CORE = (0xA0, 0x6C, 0xE0, 0xFF)       # 核心本体：紫
HIGHLIGHT = (0x6B, 0x62, 0x84, 0xFF)  # 顶部高光

SIZE = 16
OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"


def new_canvas():
    return Image.new("RGBA", (SIZE, SIZE), BORDER)


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_shell(img):
    """机身外壳：外描边之内铺满主体色，顶部两角各一个高光点。"""
    for x in range(1, SIZE - 1):
        put(img, x, 1, BODY)
        put(img, x, SIZE - 2, BODY)
    for y in range(1, SIZE - 1):
        put(img, 1, y, BODY)
        put(img, SIZE - 2, y, BODY)
    put(img, 2, 1, HIGHLIGHT)
    put(img, SIZE - 3, 1, HIGHLIGHT)


def draw_inset_panel(img):
    """内凹面板：2..13 的一圈倒角。"""
    for i in range(2, SIZE - 2):
        put(img, i, 2, BEVEL)
        put(img, i, SIZE - 3, BEVEL)
        put(img, 2, i, BEVEL)
        put(img, SIZE - 3, i, BEVEL)


def front():
    """正面：亮青方环 + 淡紫方芯。"""
    img = new_canvas()
    draw_shell(img)
    draw_inset_panel(img)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            put(img, x, y, PLATE)

    # 外圈方环
    for y in range(4, SIZE - 4):
        for x in range(4, SIZE - 4):
            if x in (4, SIZE - 5) or y in (4, SIZE - 5):
                put(img, x, y, GLOW)

    # 环内压暗
    for y in range(5, SIZE - 5):
        for x in range(5, SIZE - 5):
            put(img, x, y, DARK)

    # 核心：4x4 紫块，中间 2x2 打亮
    for y in range(6, SIZE - 6):
        for x in range(6, SIZE - 6):
            put(img, x, y, CORE)
    for y in range(7, SIZE - 7):
        for x in range(7, SIZE - 7):
            put(img, x, y, GLINT)

    # 方环四角提亮，强化「强能」
    for point in ((4, 4), (SIZE - 5, 4), (4, SIZE - 5), (SIZE - 5, SIZE - 5)):
        put(img, point[0], point[1], GLINT)
    return img


def side():
    """侧面：三条竖直散热槽，槽口各点一颗青光，底部一枚紫色铆钉 —— 与重塑机的方形口区分。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 12), (5, 12), (10, 12), (11, 12)):
        put(img, point[0], point[1], BEVEL)

    for x in (5, 8, 11):
        for y in range(4, 12):
            put(img, x, y, DARK)
            put(img, x - 1, y, BEVEL)
        put(img, x, 4, GLOW)

    # 底部紫色铆钉：与正面的紫芯呼应
    for x in (7, 8):
        put(img, x, 13, CORE)
    return img


def top():
    """顶面：方形端口里嵌一枚紫色核心。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], BEVEL)

    for y in range(4, 12):
        for x in range(4, 12):
            if x in (4, 11) or y in (4, 11):
                put(img, x, y, GLOW)

    for y in range(5, 11):
        for x in range(5, 11):
            put(img, x, y, DARK)

    for y in range(6, 10):
        for x in range(6, 10):
            put(img, x, y, CORE)
    for x in (7, 8):
        put(img, x, 7, GLINT)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = OUTPUT_DIR / f"energized_matter_fusion_machine_{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
