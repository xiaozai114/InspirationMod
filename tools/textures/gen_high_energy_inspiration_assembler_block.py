"""高能灵感装配台的方块贴图生成脚本。

这是本阶段最重要的合成机器，因此贴图在三台机器里做得最讲究：
机身是无恒合金那种淡紫，边上压一圈金色镶条（与橄榄绿的普通装配台拉开档位），
正面是一枚「高能核心」舷窗 —— 外圈辉光环 + 深色玻璃 + 中心白热核心 + 四道十字光芒，
侧面是三条散热格栅（末端带辉光），顶面是同心环端口。

用法：
    python tools/textures/gen_high_energy_inspiration_assembler_block.py
输出：
    src/main/resources/assets/inspiration/textures/block/high_energy_inspiration_assembler_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 调色板
BODY = (0x55, 0x3C, 0x7E, 0xFF)       # 机身主体：淡紫
BODY_LOW = (0x3D, 0x2B, 0x5C, 0xFF)   # 机身下半部：压暗一档，做出立体感
BORDER = (0x1A, 0x12, 0x26, 0xFF)     # 外描边
BEVEL = (0x33, 0x24, 0x4B, 0xFF)      # 倒角 / 阴影
PLATE = (0x21, 0x17, 0x30, 0xFF)      # 面板底板
DARK = (0x0E, 0x09, 0x16, 0xFF)       # 面板内凹 / 玻璃
GLOW = (0xC9, 0xA8, 0xF5, 0xFF)       # 辉光环：亮紫
GLINT = (0xF6, 0xEE, 0xFF, 0xFF)      # 辉光高光
CORE = (0xFF, 0xF3, 0xC4, 0xFF)       # 核心：白热偏暖
CORE_HOT = (0xFF, 0xFF, 0xFF, 0xFF)   # 核心最亮的一点
GOLD = (0xC9, 0xA2, 0x27, 0xFF)       # 金色镶条
GOLD_DARK = (0x7A, 0x62, 0x14, 0xFF)  # 镶条暗部
RIVET = (0x9C, 0x7C, 0xD6, 0xFF)      # 铆钉高光
HIGHLIGHT = (0x8A, 0x6E, 0xBC, 0xFF)  # 顶部高光

SIZE = 16
OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"


def new_canvas():
    return Image.new("RGBA", (SIZE, SIZE), BORDER)


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_shell(img):
    """机身外壳：上半用主体色、下半压暗，顶部两角各一个高光点。"""
    for y in range(1, SIZE - 1):
        color = BODY if y <= 8 else BODY_LOW
        for x in range(1, SIZE - 1):
            put(img, x, y, color)
    put(img, 2, 1, HIGHLIGHT)
    put(img, SIZE - 3, 1, HIGHLIGHT)


def draw_gold_frame(img):
    """金色镶条：内缩一圈的方框，上左亮、下右暗，做出金属包边。"""
    for i in range(2, SIZE - 2):
        put(img, i, 2, GOLD)
        put(img, 2, i, GOLD)
        put(img, i, SIZE - 3, GOLD_DARK)
        put(img, SIZE - 3, i, GOLD_DARK)


def ring_distance(x, y):
    """到贴图中心的欧氏距离，用来画同心圆。"""
    return ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5


def front():
    """正面：高能核心舷窗（辉光环 + 深色玻璃 + 白热核心 + 十字光芒）+ 四角铆钉。"""
    img = new_canvas()
    draw_shell(img)
    draw_gold_frame(img)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = ring_distance(x, y)
            if distance <= 1.4:
                put(img, x, y, CORE)
            elif distance <= 2.7:
                put(img, x, y, DARK)
            elif distance <= 3.8:
                put(img, x, y, GLOW)
            else:
                put(img, x, y, PLATE)

    # 核心正中一点纯白，让中心看起来在发光
    put(img, 7, 7, CORE_HOT)
    put(img, 8, 8, CORE_HOT)

    # 四道十字光芒：从辉光环往外伸出两格，末端一点高光
    for dx, dy in ((0, -1), (0, 1), (-1, 0), (1, 0)):
        for step in (4, 5):
            x, y = round(7.5 + dx * step), round(7.5 + dy * step)
            put(img, x, y, GLINT if step == 5 else GLOW)

    # 舷窗左上与右下的反光
    for point in ((5, 5), (11, 11)):
        put(img, point[0], point[1], GLINT)

    # 四角铆钉
    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], RIVET)
    return img


def side():
    """侧面：三条散热格栅，格栅末端点辉光，上下各压一条金边。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], BEVEL)

    for row in (4, 7, 10):
        for x in range(4, 12):
            put(img, x, row, DARK)
            put(img, x, row + 1, DARK)
        # 格栅两端：进气口亮、排气口暗
        put(img, 4, row, GLOW)
        put(img, 11, row, GLOW)
        for x in range(4, 12):
            put(img, x, row - 1, BEVEL)

    # 上下金边，与正面的镶条接得上
    for x in range(3, 13):
        put(img, x, 2, GOLD)
        put(img, x, 13, GOLD_DARK)
    return img


def top():
    """顶面：两圈同心环端口，中心嵌 2x2 白热核心，四角铆钉。"""
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

    for point in ((7, 7), (8, 8)):
        put(img, point[0], point[1], CORE_HOT)

    for point in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, point[0], point[1], RIVET)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = OUTPUT_DIR / f"high_energy_inspiration_assembler_{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
