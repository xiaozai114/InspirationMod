"""发电机的方块贴图生成脚本。

沿用机器家族的外壳语言（1px 描边 + 顶部高光 + 内凹面板），只把主色与正面纹样换掉：

- 简易火力发电机使用钢灰外壳与橙色炉膛。
- 碱性反应堆：青绿机身，正面是一个圆形反应腔（深色腔体 + 亮绿环 + 中心炉心），
  侧面是三条竖排的电解槽，顶面是家用菱形接口 + 绿色炉心 —— 一眼能认出是「烧东西出电」的罐子。
- RTG 发电机：暗红机身，正面沿用高能机型的同心方框，方框四角外侧补三个辐射点，
  侧面是横向散热帯，顶面菱形接口 + 红色炉心。

用法：
    python tools/textures/gen_generator_blocks.py
输出：
    src/main/resources/assets/inspiration/textures/block/{combustion_generator,alkaline_reactor,rtg_generator}_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image, ImageColor, ImageDraw

SIZE = 16
OUTPUT_DIR = (Path(__file__).resolve().parents[2]
              / "src/main/resources/assets/inspiration/textures/block")


class Palette:
    """一台机器的一套配色。字段名与既有机器脚本保持一致，便于对照。"""

    def __init__(self, body, border, bevel, plate, dark, glow, glint, core, core_hot, highlight):
        self.body = body
        self.border = border
        self.bevel = bevel
        self.plate = plate
        self.dark = dark
        self.glow = glow
        self.glint = glint
        self.core = core
        self.core_hot = core_hot
        self.highlight = highlight


# 碱性反应堆：取翡翠钢的青绿，炉心用发电核心那种明亮的反应堆绿
ALKALINE = Palette(
    body=(0x36, 0x6B, 0x52, 0xFF),
    border=(0x12, 0x26, 0x1C, 0xFF),
    bevel=(0x24, 0x4C, 0x3A, 0xFF),
    plate=(0x18, 0x33, 0x26, 0xFF),
    dark=(0x14, 0x2E, 0x20, 0xFF),
    glow=(0x7B, 0xE8, 0xA8, 0xFF),
    glint=(0xE6, 0xFF, 0xEE, 0xFF),
    core=(0x5C, 0xFF, 0x8A, 0xFF),
    core_hot=(0xEA, 0xFF, 0xF0, 0xFF),
    highlight=(0x54, 0x8F, 0x72, 0xFF),
)

# RTG 发电机：暗红机身（反应核心的调子），炉心用更亮一点的红
RTG = Palette(
    body=(0x6B, 0x32, 0x32, 0xFF),
    border=(0x24, 0x0E, 0x0E, 0xFF),
    bevel=(0x4C, 0x22, 0x22, 0xFF),
    plate=(0x33, 0x16, 0x16, 0xFF),
    dark=(0x26, 0x0E, 0x0E, 0xFF),
    glow=(0xFF, 0x8A, 0x74, 0xFF),
    glint=(0xFF, 0xE6, 0xE0, 0xFF),
    core=(0xFF, 0x5C, 0x4A, 0xFF),
    core_hot=(0xFF, 0xE2, 0xD8, 0xFF),
    highlight=(0x8F, 0x4A, 0x4A, 0xFF),
)


def new_canvas(p):
    return Image.new("RGBA", (SIZE, SIZE), p.border)


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_shell(img, p):
    """机身外壳：外描边之内铺满主体色，顶部两角各一个高光点。"""
    for x in range(1, SIZE - 1):
        put(img, x, 1, p.body)
        put(img, x, SIZE - 2, p.body)
    for y in range(1, SIZE - 1):
        put(img, 1, y, p.body)
        put(img, SIZE - 2, y, p.body)
    put(img, 2, 1, p.highlight)
    put(img, SIZE - 3, 1, p.highlight)


def draw_inset_panel(img, p):
    """内凹面板：2..13 的一圈倒角。"""
    for i in range(2, SIZE - 2):
        put(img, i, 2, p.bevel)
        put(img, i, SIZE - 3, p.bevel)
        put(img, 2, i, p.bevel)
        put(img, SIZE - 3, i, p.bevel)


def draw_core(img, p, x=7, y=7):
    """中心 2×2 炉心，左上一点最亮。"""
    for point in ((x, y), (x + 1, y), (x, y + 1), (x + 1, y + 1)):
        put(img, point[0], point[1], p.core)
    put(img, x, y, p.core_hot)


def front_chamber(p):
    """正面：圆形反应腔（外圈亮环、腔体压深、中心炉心），四角补螺栓。"""
    img = new_canvas(p)
    draw_shell(img, p)
    draw_inset_panel(img, p)

    for y in range(3, 13):
        for x in range(3, 13):
            distance = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if distance <= 2.1:
                put(img, x, y, p.dark)
            elif distance <= 3.2:
                put(img, x, y, p.glow)
            elif distance <= 4.4:
                put(img, x, y, p.dark)
            else:
                put(img, x, y, p.plate)

    # 腔体顶部反光，做出玻璃罩的感觉
    for x in (6, 7):
        put(img, x, 5, p.glint)

    for x, y in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, x, y, p.bevel)

    draw_core(img, p)
    return img


def front_rings(p):
    """正面：同心方框 + 中心炉心，方框外侧补三个辐射点。"""
    img = new_canvas(p)
    draw_shell(img, p)
    draw_inset_panel(img, p)

    for y in range(3, 13):
        for x in range(3, 13):
            distance = max(abs(x - 7.5), abs(y - 7.5))
            if distance == 4.5:
                put(img, x, y, p.plate)
            elif distance == 3.5:
                put(img, x, y, p.glow)
            elif distance == 2.5:
                put(img, x, y, p.dark)
            else:
                put(img, x, y, p.plate)

    for x, y in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, x, y, p.glint)

    draw_core(img, p)
    return img


def side_fins(p):
    """侧面：三条竖排电解槽，槽底一点辉光。"""
    img = new_canvas(p)
    draw_shell(img, p)

    for x in (5, 8, 11):
        for y in range(4, 12):
            put(img, x, y, p.dark)
        put(img, x, 3, p.bevel)
        put(img, x, 12, p.bevel)
        put(img, x, 11, p.glow)

    for x, y in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 12), (11, 12)):
        put(img, x, y, p.bevel)
    return img


def side_band(p):
    """侧面：横向散热帯 + 三个光点。"""
    img = new_canvas(p)
    draw_shell(img, p)

    for point in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 4), (11, 4),
                  (4, 12), (11, 12), (4, 13), (5, 13), (10, 13), (11, 13)):
        put(img, point[0], point[1], p.bevel)

    for x in range(3, 13):
        put(img, x, 7, p.dark)
        put(img, x, 8, p.dark)
        put(img, x, 6, p.bevel)
        put(img, x, 9, p.bevel)
    for x in (5, 8, 11):
        put(img, x, 7, p.glow)
        put(img, x, 8, p.glow)
    put(img, 5, 7, p.glint)
    return img


def top_port(p):
    """顶面：菱形接口 + 中心炉心。"""
    img = new_canvas(p)
    draw_shell(img, p)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], p.bevel)

    for y in range(3, 13):
        for x in range(3, 13):
            distance = abs(x - 7.5) + abs(y - 7.5)
            if distance == 4.5:
                put(img, x, y, p.glow)
            elif distance == 3.5:
                put(img, x, y, p.plate)
            elif distance < 3.5:
                put(img, x, y, p.dark)
            elif distance == 5.5:
                put(img, x, y, p.plate)

    draw_core(img, p)
    return img


def combustion_faces():
    colors = ("#686976", "#292b34", "#474a55", "#373843", "#24252d",
              "#b37545", "#dfd6bf", "#a55831", "#ffc56e", "#90939b")
    iron = Palette(*(ImageColor.getcolor(color, "RGBA") for color in colors))
    front = new_canvas(iron)
    draw_shell(front, iron)
    grate = ImageDraw.Draw(front)
    grate.rectangle((3, 5, 12, 12), fill=iron.dark, outline=iron.bevel)
    grate.line((4, 10, 11, 10), fill=iron.core)
    for x in (5, 8, 10):
        grate.line((x, 8, x, 10), fill=iron.core_hot)
    grate.line((4, 9, 11, 9), fill=iron.plate)
    return front, side_fins(iron), top_port(iron)


MACHINES = (
    ("combustion_generator", None, combustion_faces()),
    ("alkaline_reactor", ALKALINE, (front_chamber(ALKALINE), side_fins(ALKALINE), top_port(ALKALINE))),
    ("rtg_generator", RTG, (front_rings(RTG), side_band(RTG), top_port(RTG))),
)


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, _, faces in MACHINES:
        for face, image in zip(("front", "side", "top"), faces):
            path = OUTPUT_DIR / f"{name}_{face}.png"
            image.save(path)
            print(f"written {path}")


if __name__ == "__main__":
    main()
