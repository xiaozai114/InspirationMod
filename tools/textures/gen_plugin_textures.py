"""机器插件贴图生成脚本。

各种插件共用同一块「深底 + 亮边」的方形底板，各自用不同颜色与符号区分：
电容是闪电，灵感是四角星，速度是双箭头，电容降级是向下的双箭头（代表上限被压低），
回收是向上的双箭头（产量变多）。

用法：
    python tools/textures/gen_plugin_textures.py                # 生成全部
    python tools/textures/gen_plugin_textures.py speed_plugin    # 只生成指定插件
输出：
    src/main/resources/assets/inspiration/textures/item/<name>.png
"""

import sys
from pathlib import Path

from PIL import Image

SIZE = 16

PLATE_DARK = (0x1B, 0x1D, 0x24, 0xFF)   # 底板
PLATE_EDGE = (0x0B, 0x0C, 0x10, 0xFF)   # 底板描边
PLATE_LIT = (0x33, 0x37, 0x42, 0xFF)    # 底板上沿受光

# 名称 -> (符号颜色, 符号绘制函数用的形状名)
PLUGINS = {
    "capacitor_plugin": (0xFF, 0xD8, 0x4D, 0xFF),
    "inspiration_plugin": (0xC7, 0x7D, 0xFF, 0xFF),
    "speed_plugin": (0x5F, 0xE0, 0xFF, 0xFF),
    "capacitor_downgrade_plugin": (0xFF, 0x6B, 0x6B, 0xFF),
    "parallel_plugin": (0xF2, 0xAA, 0xFB, 0xFF),
    "recycling_plugin": (0x6B, 0xE8, 0x8A, 0xFF),
}

# 闪电（电容）：自上而下的折线
BOLT = [(8, 3), (7, 4), (7, 6), (5, 7), (5, 8), (7, 8), (7, 10), (6, 11), (8, 9), (9, 9), (9, 6), (8, 5)]

# 四角星（灵感）：上下左右四臂，靠近中心处逐渐收窄
STAR = ([(7, y) for y in range(3, 13)] + [(8, y) for y in range(3, 13)]
        + [(x, 7) for x in range(3, 13)] + [(x, 8) for x in range(3, 13)]
        + [(6, 5), (9, 5), (6, 10), (9, 10), (5, 6), (10, 6), (5, 9), (10, 9)])

# 双箭头（速度）：两个向右的尖角
CHEVRONS = [(4, 5), (5, 4), (5, 6), (6, 5), (6, 7), (7, 6), (7, 8), (8, 7),
            (9, 5), (10, 4), (10, 6), (11, 5), (11, 7), (12, 6), (12, 8), (13, 7)]

# 向下双箭头（电容降级）：与速度插件同源，只是把尖头翻过来，暗示「往下降」
CHEVRONS_DOWN = [(4, 4), (5, 5), (6, 6), (7, 7), (8, 7), (9, 6), (10, 5), (11, 4),
                 (4, 8), (5, 9), (6, 10), (7, 11), (8, 11), (9, 10), (10, 9), (11, 8)]


# 回收升级：两个向上的粗箭头（= 产量往上走）。
# 速度插件是横向双箭头、电容降级是向下的双箭头，这里沿用同一套「箭头」语汇，
# 只把方向朝上并换成绿色，一眼能看出是「变多」而不是「变快」或「变少」。
RECYCLE = [(7, 4), (8, 4), (6, 5), (9, 5), (5, 6), (10, 6), (4, 7), (11, 7),
           (7, 8), (8, 8), (6, 9), (9, 9), (5, 10), (10, 10), (4, 11), (11, 11)]

SYMBOLS = {
    "capacitor_plugin": BOLT,
    "inspiration_plugin": STAR,
    "speed_plugin": CHEVRONS,
    "capacitor_downgrade_plugin": CHEVRONS_DOWN,
    "parallel_plugin": [(x, y) for x in range(4, 12) for y in (5, 10)]
                       + [(5, 6), (5, 7), (4, 7), (4, 8), (5, 8), (5, 9),
                          (10, 4), (10, 6), (10, 9), (10, 11)],
    "recycling_plugin": RECYCLE,
}

ITEM_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/item"


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_plate(img):
    """底板：3..12 的方块，外描边一圈，顶边与左边各一像素受光。"""
    for y in range(3, 13):
        for x in range(3, 13):
            put(img, x, y, PLATE_DARK)
    for i in range(2, 14):
        put(img, i, 2, PLATE_EDGE)
        put(img, i, 13, PLATE_EDGE)
        put(img, 2, i, PLATE_EDGE)
        put(img, 13, i, PLATE_EDGE)
    for i in range(3, 13):
        put(img, i, 3, PLATE_LIT)
        put(img, 3, i, PLATE_LIT)


def draw_symbol(img, shape, color):
    for x, y in shape:
        put(img, x, y, color)


HIGH_ENERGY_BASE = {
    "high_energy_capacitor_plugin": "capacitor_plugin",
    "high_energy_inspiration_plugin": "inspiration_plugin",
    "high_energy_speed_plugin": "speed_plugin",
}


def draw_high_energy_frame(img):
    """紫钢边框、亮角和两枚金色引脚，保留同类插件的中心标志。"""
    for i in range(3, 13):
        put(img, i, 2, (0xB5, 0x94, 0xDD, 0xFF))
        put(img, 2, i, (0x89, 0x65, 0x9E, 0xFF))
        put(img, 13, i, (0x67, 0x48, 0x78, 0xFF))
        put(img, i, 13, (0x53, 0x36, 0x61, 0xFF))
    for x in (3, 12):
        put(img, x, 2, (0xEB, 0xDE, 0xFC, 0xFF))
    for x in (5, 9):
        for pin in (x, x + 1):
            put(img, pin, 13, (0xF2, 0xD1, 0x8C, 0xFF))
            put(img, pin, 14, (0xA7, 0x70, 0x40, 0xFF))


def build(name):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw_plate(img)
    base = HIGH_ENERGY_BASE.get(name, name)
    if name in HIGH_ENERGY_BASE:
        draw_high_energy_frame(img)
    draw_symbol(img, SYMBOLS[base], PLUGINS[base])
    return img


def main():
    available = [*PLUGINS, *HIGH_ENERGY_BASE]
    names = sys.argv[1:] or available
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        if name not in available:
            raise SystemExit(f"未知的插件：{name}，可选 {sorted(available)}")
        path = ITEM_DIR / f"{name}.png"
        build(name).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
