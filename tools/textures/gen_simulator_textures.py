"""结构模拟机一族的贴图：高能机器外壳、五种仓室、控制器，以及两份结构数据物品。

外壳走「原版铁块换淡紫」：把 `iron_block.png` 转成 HSV 后把色相钉在紫色上、饱和度抬一点，
亮度与细节纹理原样保留 —— 这样它看起来仍旧是一块金属，只是变成了淡紫色。
五种仓室都是**同一张外壳 + 一块内凹面板 + 一枚中央标记**，标记就是身份。
输入仓与输出仓共用同一枚槽口形状，**只靠面板颜色区分**（绿=进、橙=出），不画箭头：

- 输入仓 / 输出仓：横向槽口
- 能量端口：闪电
- 灵感提供仓：灵感充能那颗菱形
- 数据仓：带引脚的芯片

两台控制器的六个面图由 simulator_controllers 生成。两份数据物品是同一张卡的两种状态：
空白数据是一块空白屏幕，沉船数据在屏幕上画了一艘小船。
结构投影仪是一台机身在右下、镜头朝左上打出紫色光锥的小装置。

用法：
    python tools/textures/gen_simulator_textures.py [client-extra.jar 路径]
输出：
    src/main/resources/assets/inspiration/textures/block/{high_energy_machine_casing,
        simulator_input_hatch, simulator_output_hatch, simulator_energy_port,
        simulator_inspiration_hatch, simulator_data_hatch,
        {structure,mineral}_simulator_{front,side,top}}.png
    src/main/resources/assets/inspiration/textures/item/{blank_data,shipwreck_data,projector}.png
"""

import colorsys
import sys
import zipfile
from pathlib import Path

from PIL import Image
from simulator_controllers import controller_textures

TEXTURE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
BLOCK_DIR = TEXTURE_ROOT / "block"
ITEM_DIR = TEXTURE_ROOT / "item"

SIZE = 16
VANILLA_ENTRY = "assets/minecraft/textures/block/iron_block.png"

# 外壳色相（0.78 ≈ 280°，紫）与饱和度：铁块本身是灰的，饱和度全靠这里给
CASING_HUE = 0.78
CASING_SAT = 0.24

# 内凹面板：仓室中央那块深色底板
PLATE_DARK = (0x3A, 0x2B, 0x4E, 255)
PLATE_EDGE = (0x26, 0x1B, 0x36, 255)

def find_client_extra():
    """没传参数时，去构建目录里找 client-extra 资源包。"""
    artifacts = Path(__file__).resolve().parents[2] / "build/moddev/artifacts"
    candidates = sorted(artifacts.glob("neoforge-*-client-extra-*.jar"))
    if not candidates:
        raise SystemExit(f"找不到 client-extra 资源包，请把路径作为参数传进来（找过 {artifacts}）")
    return candidates[-1]


def load_vanilla(jar_path):
    with zipfile.ZipFile(jar_path) as jar:
        with jar.open(VANILLA_ENTRY) as stream:
            return Image.open(stream).convert("RGBA")


def tint_purple(source):
    """原版铁块 → 淡紫色铁块：只动色相与饱和度，亮度与纹理原样保留。"""
    out = Image.new("RGBA", source.size)
    for y in range(source.height):
        for x in range(source.width):
            r, g, b, a = source.getpixel((x, y))
            if a == 0:
                out.putpixel((x, y), (0, 0, 0, 0))
                continue
            _, saturation, value = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            nr, ng, nb = colorsys.hsv_to_rgb(
                CASING_HUE, min(1.0, saturation * 0.4 + CASING_SAT), value)
            out.putpixel((x, y), (round(nr * 255), round(ng * 255), round(nb * 255), a))
    return out


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def rect(img, x0, y0, x1, y1, color):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            put(img, x, y, color)


def pixels(img, points, color):
    for x, y in points:
        put(img, x, y, color)


def inset_plate(img):
    """仓室中央那块内凹面板：外圈压深、内部更暗，给标记留出干净的底。"""
    rect(img, 3, 3, 12, 12, PLATE_EDGE)
    rect(img, 4, 4, 11, 11, PLATE_DARK)


def slot():
    """输入仓 / 输出仓：一枚横向的槽口。两个仓不画箭头，靠面板颜色区分。"""
    return [(x, y) for y in (6, 9) for x in range(5, 11)] + \
           [(5, 7), (5, 8), (10, 7), (10, 8)]


def bolt():
    """能量端口：一道从上往左下劈的闪电，肚子那两行最宽。"""
    return [(9, 4), (10, 4),
            (8, 5), (9, 5),
            (7, 6), (8, 6),
            (6, 7), (7, 7), (8, 7), (9, 7),
            (7, 8), (8, 8), (9, 8), (10, 8),
            (7, 9), (8, 9),
            (6, 10), (7, 10),
            (5, 11), (6, 11)]


def diamond():
    """灵感提供仓：灵感充能那颗菱形，半径 4 格。"""
    rows = {4: range(7, 9), 5: range(6, 10), 6: range(5, 11), 7: range(4, 12),
            8: range(5, 11), 9: range(6, 10), 10: range(7, 9)}
    return [(x, y) for y, xs in rows.items() for x in xs]


def chip():
    """数据仓：带左右引脚的芯片，中心一点亮芯。"""
    points = []
    for y in (4, 11):                       # 上下边框
        points += [(x, y) for x in range(4, 12)]
    for y in range(5, 11):                  # 左右边框
        points += [(4, y), (11, y)]
    rect_points = [(x, y) for y in range(6, 10) for x in range(6, 10)]
    pins = [(3, 6), (3, 7), (3, 9), (3, 10), (12, 6), (12, 7), (12, 9), (12, 10)]
    return points + rect_points + pins


# 五种仓室：中央那枚标记的形状与颜色。颜色彼此拉开，隔着结构一眼能分清。
# 输入仓与输出仓共用同一枚槽口形状，**只靠颜色区分**（绿=进、橙=出），不再画箭头。
HATCHES = (
    ("simulator_input_hatch", slot(), (0x7C, 0xF0, 0xB4, 255)),
    ("simulator_output_hatch", slot(), (0xF2, 0xA0, 0x5C, 255)),
    ("simulator_energy_port", bolt(), (0xFF, 0xD8, 0x4A, 255)),
    ("simulator_inspiration_hatch", diamond(), (0xC8, 0x8B, 0xFF, 255)),
    ("simulator_data_hatch", chip(), (0x7F, 0xD6, 0xFF, 255)),
)


def hatch_texture(casing, shape, color):
    img = casing.copy()
    inset_plate(img)
    pixels(img, shape, color)
    return img


# ===================== 两份结构数据物品 =====================

CARD = (0x2E, 0x3A, 0x56, 255)        # 卡身：冷灰蓝
CARD_LIT = (0x45, 0x55, 0x78, 255)    # 受光边
CARD_DARK = (0x1B, 0x23, 0x36, 255)   # 描边与暗部
SCREEN = (0x8F, 0xA6, 0xC8, 255)      # 空白数据的屏幕
PIN = (0xC9, 0xA2, 0x27, 255)         # 金色触点
SEA = (0x2E, 0x6E, 0x9E, 255)         # 沉船数据的海色
SEA_LIT = (0x5A, 0xA8, 0xD8, 255)
HULL = (0x7A, 0x52, 0x2E, 255)        # 船体木色
SAIL = (0xEF, 0xE6, 0xD2, 255)


def data_card():
    """两张数据共用的卡身：外框 + 屏幕区 + 底部三枚金色触点（触点之间留缝）。"""
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    rect(img, 3, 2, 12, 13, CARD_DARK)
    rect(img, 4, 3, 11, 12, CARD)
    for x in range(4, 12):
        put(img, x, 3, CARD_LIT)
    rect(img, 5, 4, 10, 10, CARD_DARK)
    rect(img, 5, 5, 10, 9, SCREEN)
    for x in (5, 8, 11):
        rect(img, x, 11, x, 12, PIN)
    return img


def blank_data():
    """空白数据：屏幕是空的，只在左上角留一道反光。"""
    img = data_card()
    for x in range(5, 8):
        put(img, x, 5, (0xB6, 0xC8, 0xE4, 255))
    return img


def shipwreck_data():
    """沉船数据：屏幕上一条漂在海面的小船 —— 帆、桅杆、船体各占一行，下面留两行海。"""
    img = data_card()
    rect(img, 5, 5, 10, 9, SEA)
    # 帆（2×2）+ 桅杆（1×2），都压在船体上沿之上
    rect(img, 8, 5, 9, 6, SAIL)
    rect(img, 7, 5, 7, 6, HULL)
    # 船体：单独一行，上宽下窄的观感靠下面那行海的反光补
    pixels(img, [(x, 7) for x in range(5, 11)], HULL)
    # 海面：船体下方两点反光，位置错开，看起来像起伏
    pixels(img, [(6, 8), (9, 8)], SEA_LIT)
    return img


# ===================== 结构投影仪 =====================

BODY = (0x4C, 0x54, 0x6A, 255)        # 机身：冷灰
BODY_DARK = (0x2C, 0x32, 0x42, 255)   # 机身描边与暗部
BODY_LIT = (0x72, 0x7C, 0x98, 255)    # 机身受光的顶边
LENS = (0xE6, 0xC8, 0xFF, 255)        # 镜头：亮紫
LENS_RIM = (0xB0, 0x88, 0xE8, 255)
BEAM_CORE = (0xA4, 0x72, 0xE0, 255)   # 光锥内部：中紫
BEAM_HI = (0xD8, 0xB4, 0xFF, 255)     # 光锥里的亮线（结构的分层）
BEAM_EDGE = (0x6E, 0x44, 0xA8, 255)   # 光锥边缘：压深，别糊成一团


def projector():
    """结构投影仪：右下角一台小机身，镜头朝左打出一个张开的紫色光锥。

    锥体是**实心**的：从镜头 (8,9) 往左逐渐变宽，边缘压深、内部中紫，
    中间留两道亮线当作「结构的分层」，一眼能看出是在投影而不是撒了一把点。
    """
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))

    # 光锥：从左往右逐渐收窄，越靠近镜头越细
    for x in range(1, 9):
        t = (8 - x) / 7.0                 # 0 在镜头处、1 在最远端
        half = 1 + round(3.4 * t)
        top, bottom = 9 - half, 9 + half
        for y in range(top, bottom + 1):
            if y in (top, bottom):
                put(img, x, y, BEAM_EDGE)
            else:
                put(img, x, y, BEAM_CORE)
        # 两道亮线：把光锥切出「层」的层次
        for y in (top + 1, bottom - 1):
            put(img, x, y, BEAM_HI)

    # 机身：右下角一块带握把的装置，压在光锥根部
    rect(img, 8, 9, 14, 14, BODY_DARK)
    rect(img, 9, 10, 13, 13, BODY)
    for x in range(9, 14):
        put(img, x, 10, BODY_LIT)
    rect(img, 11, 12, 12, 15, BODY_DARK)

    # 镜头：机身朝左的那一格，外圈深、中心最亮
    rect(img, 8, 9, 9, 10, LENS_RIM)
    put(img, 8, 9, LENS)
    put(img, 8, 10, LENS)
    return img


def main():
    jar_path = Path(sys.argv[1]) if len(sys.argv) > 1 else find_client_extra()
    casing = tint_purple(load_vanilla(jar_path))

    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)

    outputs = [("block/high_energy_machine_casing", casing)]
    for name, shape, color in HATCHES:
        outputs.append((f"block/{name}", hatch_texture(casing, shape, color)))
    outputs.extend(controller_textures(casing))
    outputs += [
        ("item/blank_data", blank_data()),
        ("item/shipwreck_data", shipwreck_data()),
        ("item/projector", projector()),
    ]

    for relative, image in outputs:
        path = TEXTURE_ROOT / f"{relative}.png"
        image.save(path)
        print(f"written {path}")
    print(f"（铁块底材取自 {jar_path.name}）")


if __name__ == "__main__":
    main()
