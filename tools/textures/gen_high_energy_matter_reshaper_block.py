"""高能物质重塑机的方块贴图生成脚本。

贴图尺寸 16x16，沿用机器家族的外壳语言（1px 描边 + 顶部高光 + 内凹面板）。
纹样刻意与物质重塑机同构（正面同心方框 + 中心核心、侧面散热带 + 光点、顶面菱形口），
只把主色从黄绿换成无恒合金系的淡紫，并多加一圈内环 / 一个光点来表示「高能」这一档。

用法：
    python tools/textures/gen_high_energy_matter_reshaper_block.py
输出：
    src/main/resources/assets/inspiration/textures/block/high_energy_matter_reshaper_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 调色板
BODY = (0x4A, 0x35, 0x6E, 0xFF)       # 机身主体：淡紫（无恒合金的调子）
BORDER = (0x1C, 0x14, 0x28, 0xFF)     # 外描边
BEVEL = (0x33, 0x24, 0x4C, 0xFF)      # 倒角 / 阴影
PLATE = (0x24, 0x19, 0x36, 0xFF)      # 面板底板
DARK = (0x11, 0x0B, 0x1A, 0xFF)       # 面板内凹
GLOW = (0xC9, 0xA8, 0xEF, 0xFF)       # 辉光：亮紫
GLINT = (0xF4, 0xEC, 0xFF, 0xFF)      # 辉光高光
CORE = (0xFF, 0xDE, 0x92, 0xFF)       # 核心：暖金（沿用原机型的核心色）
CORE_HOT = (0xFF, 0xF6, 0xD8, 0xFF)   # 核心最亮的一点
HIGHLIGHT = (0x7A, 0x5F, 0xA8, 0xFF)  # 顶部高光

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


def chebyshev(x, y):
    """到贴图中心的切比雪夫距离，用来画方框而不是圆环。"""
    return max(abs(x - 7.5), abs(y - 7.5))


def front():
    """正面：三层同心方框（外环 / 内环 / 核心），比原机型多一圈内环。"""
    img = new_canvas()
    draw_shell(img)
    draw_inset_panel(img)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = chebyshev(x, y)
            if distance == 5.5:
                put(img, x, y, PLATE)
            elif distance == 4.5:
                put(img, x, y, GLOW)
            elif distance == 3.5:
                put(img, x, y, DARK)
            elif distance == 2.5:
                put(img, x, y, GLOW)
            elif distance < 2.5:
                put(img, x, y, DARK)
            else:
                put(img, x, y, PLATE)

    # 方框四角打亮，做出玻璃反光
    for point in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, point[0], point[1], GLINT)

    # 中心核心 2x2，左上一点最亮
    for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
        put(img, point[0], point[1], CORE)
    put(img, 7, 7, CORE_HOT)
    return img


def side():
    """侧面：横向散热带 + 三个光点（原机型是两个），带上下各有倒角。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 4), (11, 4),
                  (4, 12), (11, 12), (4, 13), (5, 13), (10, 13), (11, 13)):
        put(img, point[0], point[1], BEVEL)

    # 中间一条横向散热带，上下沿各留一道倒角
    for x in range(3, 13):
        put(img, x, 7, DARK)
        put(img, x, 8, DARK)
        put(img, x, 6, BEVEL)
        put(img, x, 9, BEVEL)
    for x in (5, 8, 11):
        put(img, x, 7, GLOW)
        put(img, x, 8, GLOW)
    put(img, 5, 7, GLINT)
    return img


def top():
    """顶面：菱形端口 + 中心核心，端口内多一圈亮边。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], BEVEL)

    for y in range(3, 13):
        for x in range(3, 13):
            distance = abs(x - 7.5) + abs(y - 7.5)
            if distance == 4.5:
                put(img, x, y, GLOW)
            elif distance < 4.5:
                put(img, x, y, DARK)
            elif distance == 5.5:
                put(img, x, y, PLATE)

    for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
        put(img, point[0], point[1], CORE)
    put(img, 7, 7, CORE_HOT)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = OUTPUT_DIR / f"high_energy_matter_reshaper_{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
