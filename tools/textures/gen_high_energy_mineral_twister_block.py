"""高能矿物扭曲机的方块贴图生成脚本。

贴图尺寸 16x16，沿用机器家族的外壳语言（1px 描边 + 顶部高光 + 内凹面板），
主色调改成无恒合金那种淡紫（与青绿色的矿物扭曲机拉开档位），
纹样沿用「菱形辉光 + 中心风车」但把风车改成双叶螺旋，暗示时运翻倍的扭曲强度。

用法：
    python tools/textures/gen_high_energy_mineral_twister_block.py
输出：
    src/main/resources/assets/inspiration/textures/block/high_energy_mineral_twister_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 调色板
BODY = (0x53, 0x3C, 0x7A, 0xFF)       # 机身主体：淡紫（无恒合金的调子）
BORDER = (0x1C, 0x14, 0x28, 0xFF)     # 外描边
BEVEL = (0x36, 0x26, 0x4E, 0xFF)      # 倒角 / 阴影
PLATE = (0x22, 0x18, 0x32, 0xFF)      # 面板底板
DARK = (0x10, 0x0A, 0x18, 0xFF)       # 面板内凹
GLOW = (0xD2, 0xB4, 0xF2, 0xFF)       # 辉光：亮紫
GLINT = (0xF2, 0xE9, 0xFF, 0xFF)      # 辉光高光
CORE = (0xBF, 0xF2, 0x8C, 0xFF)       # 核心：淡黄绿（保留原机型的一点绿）
HIGHLIGHT = (0x86, 0x6C, 0xB4, 0xFF)  # 顶部高光

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
    """正面：菱形辉光镜片 + 中心双叶螺旋（比原机型多两片叶，暗示时运更高）。"""
    img = new_canvas()
    draw_shell(img)
    draw_inset_panel(img)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = abs(x - 7.5) + abs(y - 7.5)
            if distance == 5:
                put(img, x, y, GLOW)
            elif distance < 5:
                put(img, x, y, DARK)
            else:
                put(img, x, y, PLATE)

    # 菱形的四个尖端打亮，做出玻璃反光的感觉
    for point in ((5, 5), (10, 5), (5, 10), (10, 10)):
        put(img, point[0], point[1], GLINT)

    # 双叶螺旋：四个象限各一个 2x2 的叶片，绕中心转一圈
    for point in ((6, 6), (7, 6), (6, 7), (7, 7),
                  (8, 8), (9, 8), (8, 9), (9, 9),
                  (8, 6), (9, 7), (6, 8), (7, 9)):
        put(img, point[0], point[1], CORE)
    return img


def side():
    """侧面：斜向散热槽（与原机型同构），槽口换成紫色辉光。"""
    img = new_canvas()
    draw_shell(img)

    # 四角倒角，延续同一套机器语言
    for point in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 4), (11, 4),
                  (4, 12), (11, 12), (4, 13), (5, 13), (10, 13), (11, 13)):
        put(img, point[0], point[1], BEVEL)

    # 三条等距斜槽：槽体深色，左上沿受光，起点各点一颗辉光
    for offset in (-2, 1, 4):
        for y in range(4, 12):
            x = y + offset
            if 3 <= x <= 12:
                put(img, x, y, DARK)
                if 3 <= x - 1 <= 12:
                    put(img, x - 1, y, BEVEL)
    for offset in (-2, 1, 4):
        first_y = next((y for y in range(4, 12) if 3 <= y + offset <= 12), None)
        if first_y is not None:
            put(img, first_y + offset, first_y, GLINT)
    return img


def top():
    """顶面：菱形端口中嵌 2x2 核心。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], BEVEL)

    for y in range(4, 12):
        for x in range(4, 12):
            distance = abs(x - 7.5) + abs(y - 7.5)
            if distance == 3:
                put(img, x, y, GLOW)
            elif distance < 3:
                put(img, x, y, DARK)

    for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
        put(img, point[0], point[1], CORE)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = OUTPUT_DIR / f"high_energy_mineral_twister_{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
