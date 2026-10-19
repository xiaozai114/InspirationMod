"""注入合成的多方块结构贴图生成脚本。

三张（组）贴图各自承担一种视觉语言：

* ``matter_binder_*`` —— 物质束缚器：不完整方块，做成一座矮台座。
  顶面是中央青白束缚环，侧面是深靛紫机身加一道束缚槽。
* ``injection_gun_*`` —— 注入枪：不完整方块，底部座 + 前伸炮管。暗钢灰机身 + 翡翠绿枪口。
  枪口在「已绑定」时会亮起，因此额外出一张 ``front_linked``，由方块状态切换，
  作为玩家唯一的绑定状态反馈。
* ``basalt_alloy_block`` —— 玄武岩合金块：注入结构的 1 级底座，玄武灰带合金颗粒。
* ``sculk_alloy_block`` —— 幽匿合金块：注入结构的 2 级底座，冷青底带回响辉光颗粒。

用法：
    python tools/textures/gen_injection_blocks.py
输出：
    src/main/resources/assets/inspiration/textures/block/matter_binder_{side,top}.png
    src/main/resources/assets/inspiration/textures/block/injection_gun_{front,front_linked,side,top}.png
    src/main/resources/assets/inspiration/textures/block/basalt_alloy_block.png
    src/main/resources/assets/inspiration/textures/block/sculk_alloy_block.png
"""

from pathlib import Path

from PIL import Image

SIZE = 16
OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"

# ---------------------------------------------------------------- 物质束缚器
BINDER_BORDER = (0x0C, 0x0A, 0x16, 0xFF)
BINDER_BODY = (0x2C, 0x25, 0x4B, 0xFF)
BINDER_BEVEL = (0x1A, 0x15, 0x30, 0xFF)
BINDER_PLATE = (0x18, 0x14, 0x2C, 0xFF)
BINDER_VOID = (0x07, 0x06, 0x10, 0xFF)
BINDER_RING = (0x6F, 0xD8, 0xF5, 0xFF)
BINDER_CORE = (0xDC, 0xFF, 0xFF, 0xFF)
BINDER_BOLT = (0x4C, 0x42, 0x78, 0xFF)

# ---------------------------------------------------------------- 注入枪
GUN_BORDER = (0x10, 0x11, 0x13, 0xFF)
GUN_BODY = (0x3C, 0x40, 0x47, 0xFF)
GUN_BEVEL = (0x25, 0x28, 0x2C, 0xFF)
GUN_PLATE = (0x1D, 0x20, 0x24, 0xFF)
GUN_DARK = (0x0C, 0x0D, 0x0F, 0xFF)
GUN_RIM = (0x8A, 0x93, 0x9C, 0xFF)
GUN_HIGHLIGHT = (0x6B, 0x72, 0x7B, 0xFF)
JADE = (0x53, 0xBA, 0x50, 0xFF)
JADE_GLOW = (0x8C, 0xFF, 0x8A, 0xFF)

# ---------------------------------------------------------------- 玄武岩合金块
BASALT_DARK = (0x11, 0x13, 0x17, 0xFF)
BASALT_MID = (0x3B, 0x40, 0x48, 0xFF)
BASALT_LIGHT = (0x76, 0x7C, 0x88, 0xFF)
BASALT_EDGE = (0x08, 0x09, 0x0B, 0xFF)

# ---------------------------------------------------------------- 幽匿合金块
SCULK_DARK = (0x09, 0x24, 0x29, 0xFF)
SCULK_MID = (0x10, 0x3D, 0x44, 0xFF)
SCULK_LIGHT = (0x1E, 0x6E, 0x76, 0xFF)
SCULK_EDGE = (0x04, 0x14, 0x17, 0xFF)
SCULK_GLOW = (0x6F, 0xE8, 0xE0, 0xFF)   # 回响碎片那种冷青辉光


def new_canvas(border):
    return Image.new("RGBA", (SIZE, SIZE), border)


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_shell(img, body, highlight=None):
    """机身外壳：外描边之内铺满主体色，顶部两角各一个高光点。"""
    for x in range(1, SIZE - 1):
        put(img, x, 1, body)
        put(img, x, SIZE - 2, body)
    for y in range(1, SIZE - 1):
        put(img, 1, y, body)
        put(img, SIZE - 2, y, body)
    if highlight is not None:
        put(img, 2, 1, highlight)
        put(img, SIZE - 3, 1, highlight)


def draw_inset(img, bevel, x0=2, x1=SIZE - 2):
    """内凹倒角：从 x0 到 x1 围一圈。"""
    for i in range(x0, x1 + 1):
        put(img, i, x0, bevel)
        put(img, i, x1, bevel)
        put(img, x0, i, bevel)
        put(img, x1, i, bevel)


# ---------------------------------------------------------------- 物质束缚器
def matter_binder_side():
    """台座侧面：深靛紫机身，中部一道青白束缚槽，上下压暗做出台座的层次。"""
    img = new_canvas(BINDER_BORDER)
    draw_shell(img, BINDER_BODY)
    draw_inset(img, BINDER_BEVEL)

    # 顶沿与底沿：压暗的一圈，让它读起来是台座而不是一块方料
    for x in range(3, SIZE - 3):
        put(img, x, 3, BINDER_BEVEL)
        put(img, x, SIZE - 4, BINDER_BEVEL)

    # 中部束缚槽：一条青白细线 + 上下阴影
    for x in range(4, SIZE - 4):
        put(img, x, 7, BINDER_RING)
        put(img, x, 8, BINDER_RING)
        put(img, x, 6, BINDER_BEVEL)
        put(img, x, 9, BINDER_BEVEL)

    for point in ((4, 4), (11, 4), (4, 11), (11, 11)):
        put(img, point[0], point[1], BINDER_BOLT)
    return img


def matter_binder_top():
    """台座顶面：中央束缚环 + 悬浮核心 + 四角螺栓。"""
    img = new_canvas(BINDER_BORDER)
    draw_shell(img, BINDER_BODY)
    draw_inset(img, BINDER_BEVEL)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if 3.6 <= distance <= 4.6:
                put(img, x, y, BINDER_RING)
            elif distance < 3.6:
                put(img, x, y, BINDER_VOID)
            else:
                put(img, x, y, BINDER_PLATE)

    # 悬浮核心：2x2 亮块 + 上下左右各一颗延伸，像被场束缚住的粒子
    for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
        put(img, point[0], point[1], BINDER_CORE)
    for point in ((7, 6), (8, 9), (6, 7), (9, 8)):
        put(img, point[0], point[1], BINDER_RING)

    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], BINDER_BOLT)
    return img


# ---------------------------------------------------------------- 注入枪
def gun_front(linked):
    """正面：中央枪口。已绑定时枪口亮起翡翠绿，未绑定时是暗膛。"""
    img = new_canvas(GUN_BORDER)
    draw_shell(img, GUN_BODY, GUN_HIGHLIGHT)
    draw_inset(img, GUN_BEVEL)

    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if distance <= 3.4:
                put(img, x, y, GUN_DARK)
            else:
                put(img, x, y, GUN_PLATE)

    # 枪口：外圈钢环 + 内膛
    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            distance = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if 2.6 <= distance <= 3.4:
                put(img, x, y, GUN_RIM)
            elif distance <= 1.5:
                put(img, x, y, JADE_GLOW if linked else GUN_DARK)

    if linked:
        for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
            put(img, point[0], point[1], BINDER_CORE)
    else:
        for point in ((7, 7), (8, 7), (7, 8), (8, 8)):
            put(img, point[0], point[1], GUN_BEVEL)

    # 四角散热钉，未绑定时暗、绑定时点亮成翡翠绿
    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], JADE if linked else GUN_BEVEL)
    return img


def gun_side():
    """侧面：横向散热槽，与机器的斜槽区分开。"""
    img = new_canvas(GUN_BORDER)
    draw_shell(img, GUN_BODY, GUN_HIGHLIGHT)

    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], GUN_BEVEL)

    for y in (5, 8, 11):
        for x in range(4, 12):
            put(img, x, y, GUN_DARK)
            put(img, x, y - 1, GUN_BEVEL)

    # 枪身后段的一条翡翠绿能量导管
    for y in range(3, 13):
        put(img, 2, y, GUN_PLATE)
    for y in (5, 8, 11):
        put(img, 13, y, GUN_PLATE)
    for point in ((13, 6), (13, 9)):
        put(img, point[0], point[1], JADE)
    return img


def gun_top():
    """顶面：钢板 + 四角铆钉 + 中央准星。"""
    img = new_canvas(GUN_BORDER)
    draw_shell(img, GUN_BODY, GUN_HIGHLIGHT)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], GUN_BEVEL)

    for y in range(4, 12):
        for x in range(4, 12):
            put(img, x, y, GUN_PLATE)

    for y in range(6, 10):
        put(img, 7, y, GUN_DARK)
        put(img, 8, y, GUN_DARK)
    put(img, 7, 5, JADE)
    put(img, 8, 5, JADE)
    return img


# ---------------------------------------------------------------- 玄武岩合金块
def basalt_alloy_block():
    """玄武岩合金块：玄武灰底 + 随机分布的合金亮斑，四边压暗做出砖缝。"""
    img = new_canvas(BASALT_EDGE)
    for y in range(1, SIZE - 1):
        for x in range(1, SIZE - 1):
            # 用位置哈希代替随机数，保证每次生成结果一致
            noise = (x * 73856093) ^ (y * 19349663)
            bucket = (noise >> 8) & 0xFF
            if bucket < 60:
                put(img, x, y, BASALT_DARK)
            elif bucket < 215:
                put(img, x, y, BASALT_MID)
            else:
                put(img, x, y, BASALT_LIGHT)

    # 内圈倒角，让它在机器语言里读起来是「砌块」而不是一片噪声
    for i in range(1, SIZE - 1):
        put(img, i, 1, BASALT_DARK)
        put(img, i, SIZE - 2, BASALT_DARK)
        put(img, 1, i, BASALT_MID)
        put(img, SIZE - 2, i, BASALT_MID)

    # 合金亮斑：固定位置的金属颗粒，模拟未完全熔合的碎块
    for point in ((4, 4), (11, 6), (6, 11), (12, 12), (3, 9)):
        put(img, point[0], point[1], BASALT_LIGHT)
    return img


# ---------------------------------------------------------------- 幽匿合金块
def sculk_alloy_block():
    """幽匿合金块：与玄武岩合金块同构（噪声底 + 内圈倒角 + 亮斑），
    换成幽匿的冷青，亮斑做成回响碎片那种发光颗粒，暗示这是更高一档的底座。"""
    img = new_canvas(SCULK_EDGE)
    for y in range(1, SIZE - 1):
        for x in range(1, SIZE - 1):
            noise = (x * 73856093) ^ (y * 19349663)
            bucket = (noise >> 8) & 0xFF
            if bucket < 70:
                put(img, x, y, SCULK_DARK)
            elif bucket < 215:
                put(img, x, y, SCULK_MID)
            else:
                put(img, x, y, SCULK_LIGHT)

    for i in range(1, SIZE - 1):
        put(img, i, 1, SCULK_DARK)
        put(img, i, SIZE - 2, SCULK_DARK)
        put(img, 1, i, SCULK_MID)
        put(img, SIZE - 2, i, SCULK_MID)

    # 发光颗粒：比玄武岩合金块的亮斑多一颗，位置错开
    for point in ((4, 4), (11, 6), (6, 11), (12, 12), (3, 9), (9, 3)):
        put(img, point[0], point[1], SCULK_GLOW)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    images = {
        "matter_binder_side": matter_binder_side(),
        "matter_binder_top": matter_binder_top(),
        "injection_gun_front": gun_front(False),
        "injection_gun_front_linked": gun_front(True),
        "injection_gun_side": gun_side(),
        "injection_gun_top": gun_top(),
        "basalt_alloy_block": basalt_alloy_block(),
        "sculk_alloy_block": sculk_alloy_block(),
    }
    for name, image in images.items():
        path = OUTPUT_DIR / f"{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
