"""注入合成新增物品的贴图生成脚本。

* ``basalt_base``   —— 玄武岩基底：一块带凹槽的玄武岩石板，注入配方的底材。
* ``binding_wand``  —— 绑定器：斜置的短杖，杖头是翡翠绿的能量环，用来建立
  注入枪与物质束缚器之间的关联。

注能翡翠钢走 ``gen_ingot_textures.py``（与翡翠钢锭同轮廓，只换配色），这里不重复实现。

用法：
    python tools/textures/gen_injection_items.py
输出：
    src/main/resources/assets/inspiration/textures/item/{basalt_base,binding_wand}.png
"""

from pathlib import Path

from PIL import Image

SIZE = 16
OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/item"

# ---------------------------------------------------------------- 玄武岩基底
BASE_EDGE = (0x07, 0x08, 0x0A, 0xFF)
BASE_BODY = (0x3B, 0x40, 0x48, 0xFF)
BASE_TOP = (0x76, 0x7C, 0x88, 0xFF)
BASE_SHADOW = (0x11, 0x13, 0x17, 0xFF)
BASE_SOCKET = (0x0A, 0x0B, 0x0D, 0xFF)
BASE_RIM = (0x8A, 0x93, 0x9C, 0xFF)

# ---------------------------------------------------------------- 绑定器
WAND_HANDLE = (0x2A, 0x2C, 0x31, 0xFF)
WAND_GRIP = (0x4A, 0x3A, 0x2A, 0xFF)
WAND_SHAFT = (0x6B, 0x72, 0x7B, 0xFF)
JADE = (0x53, 0xBA, 0x50, 0xFF)
JADE_GLOW = (0x8C, 0xFF, 0x8A, 0xFF)
JADE_CORE = (0xE4, 0xFF, 0xE2, 0xFF)


def new_canvas():
    return Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def basalt_base():
    """一块厚石板：顶面受光、底部压暗，正中一个圆形凹槽用于承放底材。"""
    img = new_canvas()

    for y in range(3, 13):
        for x in range(2, 14):
            put(img, x, y, BASE_BODY)

    # 顶面高光与底部阴影，做出厚度
    for x in range(2, 14):
        put(img, x, 3, BASE_TOP)
        put(img, x, 12, BASE_SHADOW)
    for y in range(3, 13):
        put(img, 2, y, BASE_TOP if y < 8 else BASE_BODY)
        put(img, 13, y, BASE_SHADOW)

    for x in range(2, 14):
        put(img, x, 2, BASE_EDGE)
        put(img, x, 13, BASE_EDGE)
    for y in range(2, 14):
        put(img, 1, y, BASE_EDGE)
        put(img, 14, y, BASE_EDGE)

    # 中央凹槽：外圈亮边 + 内膛全黑
    for y in range(4, 12):
        for x in range(4, 12):
            distance = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if 2.5 <= distance <= 3.4:
                put(img, x, y, BASE_RIM)
            elif distance < 2.5:
                put(img, x, y, BASE_SOCKET)

    # 四角固定螺栓
    for point in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, point[0], point[1], BASE_RIM)
    return img


def binding_wand():
    """斜置短杖：左下握把 —— 中段杆身 —— 右上翡翠绿能量环。"""
    img = new_canvas()

    # 杆身：从左下到右上的一条 1px 阶梯线，两侧各加一像素做出粗细
    steps = ((5, 12), (6, 11), (7, 10), (8, 9), (9, 8), (10, 7), (11, 6))
    for x, y in steps:
        put(img, x, y, WAND_SHAFT)
        put(img, x + 1, y, WAND_HANDLE)
        put(img, x, y + 1, WAND_HANDLE)

    # 握把：末端两格深色
    for point in ((4, 13), (5, 13), (4, 12)):
        put(img, point[0], point[1], WAND_HANDLE)
    for point in ((5, 12), (6, 11)):
        put(img, point[0], point[1], WAND_GRIP)

    # 杖头能量环：菱形四角 + 中心亮核
    for point in ((11, 5), (13, 7), (11, 9), (9, 7)):
        put(img, point[0], point[1], JADE)
    for point in ((10, 5), (12, 5), (13, 6), (13, 8), (12, 9), (10, 9), (9, 8), (9, 6)):
        put(img, point[0], point[1], JADE_GLOW)
    for point in ((11, 6), (12, 6), (11, 7), (12, 7)):
        put(img, point[0], point[1], JADE_CORE)
    put(img, 11, 7, JADE_GLOW)
    return img


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("basalt_base", basalt_base()), ("binding_wand", binding_wand())):
        path = OUTPUT_DIR / f"{name}.png"
        image.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
