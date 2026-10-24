"""流体提取机与原料精炼机的方块贴图生成脚本。

外壳语言与其它机器一致（1px 描边 + 顶部高光 + 内凹面板），只换主色与正面纹样：

- 流体提取机：青蓝机身。正面是一支朝下的喷嘴，下面接一条液体槽（槽里是液态等离子那种亮青）；
  侧面是三条横向管路；顶面菱形接口 + 青蓝炉心。
- 原料精炼机：暖铜机身。正面是三条横向料槽（一宽两窄，暗示「一个主产物 + 两个副产物」）；
  侧面是竖向散热的细管；顶面菱形接口 + 橙色炉心。

配色与形状函数复用 gen_generator_blocks，避免第二份外壳代码。

用法：
    python tools/textures/gen_refinery_blocks.py
输出：
    src/main/resources/assets/inspiration/textures/block/{fluid_extractor,raw_refiner}_{front,side,top}.png
"""

from pathlib import Path

from PIL import Image

from gen_generator_blocks import (
    Palette,
    SIZE,
    draw_core,
    draw_inset_panel,
    draw_shell,
    new_canvas,
    put,
)

OUTPUT_DIR = (Path(__file__).resolve().parents[2]
              / "src/main/resources/assets/inspiration/textures/block")

# 流体提取机：等离子锭的青蓝，槽里的液体用更亮的青
EXTRACTOR = Palette(
    body=(0x1E, 0x4C, 0x6B, 0xFF),
    border=(0x08, 0x1C, 0x2A, 0xFF),
    bevel=(0x14, 0x38, 0x52, 0xFF),
    plate=(0x0C, 0x26, 0x38, 0xFF),
    dark=(0x0A, 0x1E, 0x2C, 0xFF),
    glow=(0x7A, 0xD8, 0xFF, 0xFF),
    glint=(0xE8, 0xFF, 0xFF, 0xFF),
    core=(0x4A, 0xB8, 0xF0, 0xFF),
    core_hot=(0xF0, 0xFF, 0xFF, 0xFF),
    highlight=(0x2E, 0x6A, 0x90, 0xFF),
)

# 原料精炼机：暖铜机身，炉心用橙
REFINER = Palette(
    body=(0x6B, 0x45, 0x28, 0xFF),
    border=(0x2A, 0x18, 0x0C, 0xFF),
    bevel=(0x4C, 0x30, 0x1C, 0xFF),
    plate=(0x33, 0x20, 0x12, 0xFF),
    dark=(0x26, 0x16, 0x0C, 0xFF),
    glow=(0xFF, 0xB4, 0x6A, 0xFF),
    glint=(0xFF, 0xF0, 0xDC, 0xFF),
    core=(0xFF, 0x9A, 0x3C, 0xFF),
    core_hot=(0xFF, 0xF2, 0xD0, 0xFF),
    highlight=(0x8F, 0x5F, 0x38, 0xFF),
)


def front_nozzle(p):
    """正面：上方喷嘴（两格宽的管口 + 亮边），下方一条液体槽。"""
    img = new_canvas(p)
    draw_shell(img, p)
    draw_inset_panel(img, p)

    # 喷嘴：3 格宽的口，向下收窄，出口压一圈亮边
    for x in range(6, 10):
        put(img, x, 3, p.bevel)
        put(img, x, 4, p.dark)
    for x in range(7, 9):
        put(img, x, 5, p.dark)
        put(img, x, 6, p.glow)

    # 液体槽：横向一条，上沿亮、槽底深
    for x in range(4, 12):
        put(img, x, 10, p.glow)
        put(img, x, 11, p.dark)
        put(img, x, 12, p.dark)
    for x in (4, 11):
        put(img, x, 9, p.bevel)

    # 槽里的两滴高光，读作「液体在流」
    put(img, 6, 11, p.glint)
    put(img, 9, 12, p.glint)

    for x, y in ((4, 3), (11, 3)):
        put(img, x, y, p.bevel)
    return img


def front_slots(p):
    """正面：三条横向料槽（上宽下窄两条），读作「一个主产物 + 两个副产物」。"""
    img = new_canvas(p)
    draw_shell(img, p)
    draw_inset_panel(img, p)

    # 主产物槽：宽
    for x in range(4, 12):
        put(img, x, 5, p.dark)
        put(img, x, 6, p.glow)
    # 两条副产物槽：窄，靠下并排
    for x in range(4, 8):
        put(img, x, 9, p.dark)
        put(img, x, 10, p.bevel)
    for x in range(8, 12):
        put(img, x, 9, p.dark)
        put(img, x, 10, p.bevel)
    put(img, 5, 9, p.glow)
    put(img, 9, 9, p.glow)

    # 槽之间的分隔
    put(img, 7, 9, p.plate)
    put(img, 8, 9, p.plate)
    for y in (7, 8):
        put(img, 7, y, p.plate)
    return img


def side_pipes(p):
    """侧面：三条横向管路，管口一点辉光。"""
    img = new_canvas(p)
    draw_shell(img, p)
    for y in (4, 8, 12):
        for x in range(3, 13):
            put(img, x, y, p.dark)
            put(img, x, y - 1, p.bevel)
        put(img, 12, y, p.glow)
    for x, y in ((3, 3), (12, 3), (3, 13), (12, 13)):
        put(img, x, y, p.bevel)
    return img


def side_fins_vertical(p):
    """侧面：竖向细管（与提取机的横向管路区分开）。"""
    img = new_canvas(p)
    draw_shell(img, p)
    for x in (5, 8, 11):
        for y in range(3, 13):
            put(img, x, y, p.dark)
        put(img, x, 12, p.glow)
        put(img, x - 1, 3, p.bevel)
        put(img, x + 1, 3, p.bevel)
    for x, y in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, x, y, p.bevel)
    return img


def top_port(p):
    """顶面：菱形接口 + 中心炉心（与发电机同款语言）。"""
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


MACHINES = (
    ("fluid_extractor", (front_nozzle(EXTRACTOR), side_pipes(EXTRACTOR), top_port(EXTRACTOR))),
    ("raw_refiner", (front_slots(REFINER), side_fins_vertical(REFINER), top_port(REFINER))),
)


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, faces in MACHINES:
        for face, image in zip(("front", "side", "top"), faces):
            path = OUTPUT_DIR / f"{name}_{face}.png"
            image.save(path)
            print(f"written {path}")


if __name__ == "__main__":
    main()
