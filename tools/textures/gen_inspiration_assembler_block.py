"""灵感装配台的方块贴图生成脚本。

贴图尺寸 16x16，沿用机器家族的外壳语言（1px 描边 + 顶部高光 + 内凹面板），
主色调是橄榄绿，纹样改为「同心圆舷窗 / 横向格栅 / 同心环端口」，
以便和黄绿色的物质重塑机、青绿色的矿物扭曲机一眼区分开。

用法：
    python tools/textures/gen_inspiration_assembler_block.py
输出：
    src/main/resources/assets/inspiration/textures/block/inspiration_assembler_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 调色板
BODY = (0x4E, 0x7A, 0x2A, 0xFF)       # 机身主体：橄榄绿
BORDER = (0x1A, 0x24, 0x12, 0xFF)     # 外描边
BEVEL = (0x2C, 0x44, 0x18, 0xFF)      # 倒角 / 阴影
PLATE = (0x1E, 0x30, 0x12, 0xFF)      # 面板底板
DARK = (0x0E, 0x16, 0x08, 0xFF)       # 面板内凹
GLOW = (0xB6, 0xF0, 0x5A, 0xFF)       # 辉光：柠檬绿
GLINT = (0xE4, 0xFF, 0xB0, 0xFF)      # 辉光高光
CORE = (0xFF, 0xE0, 0x6B, 0xFF)       # 核心：琥珀
RIVET = (0x8F, 0xC8, 0x4C, 0xFF)      # 铆钉高光
HIGHLIGHT = (0x79, 0xB0, 0x40, 0xFF)  # 顶部高光

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


def ring_distance(x, y):
    """到贴图中心的欧氏距离，用来画同心圆。"""
    return ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5


def front():
    """正面：同心圆舷窗（外圈辉光 + 内圈深色玻璃 + 中心琥珀核心）+ 四角铆钉。"""
    img = new_canvas()
    draw_shell(img)
    draw_inset_panel(img)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = ring_distance(x, y)
            if distance <= 2.2:
                put(img, x, y, CORE)
            elif distance <= 4.2:
                put(img, x, y, DARK)
            elif distance <= 5.4:
                put(img, x, y, GLOW)
            else:
                put(img, x, y, PLATE)

    # 舷窗左上与右下的反光
    for point in ((5, 5), (10, 10)):
        put(img, point[0], point[1], GLINT)

    # 四角铆钉
    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], RIVET)
    return img


def side():
    """侧面：三条横向格栅，与另外两台机器的竖槽 / 斜槽区分。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], BEVEL)

    for row in (4, 7, 10):
        for x in range(4, 12):
            put(img, x, row, DARK)
            put(img, x, row + 1, DARK)
        put(img, 4, row, GLOW)
        put(img, 11, row, GLOW)
        for x in range(4, 12):
            put(img, x, row - 1, BEVEL)
    return img


def top():
    """顶面：两圈同心环端口，中心嵌 2x2 核心。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], BEVEL)

    for y in range(4, 12):
        for x in range(4, 12):
            distance = ring_distance(x, y)
            if distance <= 1.5:
                put(img, x, y, CORE)
            elif distance <= 2.6:
                put(img, x, y, DARK)
            elif distance <= 3.6:
                put(img, x, y, GLOW)

    for point in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, point[0], point[1], RIVET)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = OUTPUT_DIR / f"inspiration_assembler_{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
