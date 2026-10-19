"""高能核心贴图生成脚本。

高能核心 = 放大到整张画布的强能核心：菱形金属外框比强能核心更大（顶到画布四边），
中心换成粉色炉心，最外面再补一圈粉白泛光与四角星点 ——
泛光的处理方式照抄灵感结晶（外围稀疏亮点）与无恒物质（一圈亮边）的既有做法。

用法：
    python tools/textures/gen_high_energy_core.py
输出：
    src/main/resources/assets/inspiration/textures/item/high_energy_core.png
"""

from pathlib import Path

from PIL import Image

ITEM_DIR = (Path(__file__).resolve().parents[2]
            / "src/main/resources/assets/inspiration/textures/item")

# 金属外框：沿用强能核心的灰，但多压一档暗色让「更大」的核心更有分量
FRAME_LIT = (0x9C, 0xA0, 0xAC)
FRAME = (0x7C, 0x7F, 0x88)
FRAME_DARK = (0x55, 0x57, 0x5E)

# 粉色炉心：由外向内三档，越往里越亮
CORE_DEEP = (0xB4, 0x22, 0x76)
CORE_PINK = (0xFF, 0x6C, 0xB8)
CORE_HI = (0xFF, 0xE2, 0xF2)

# 泛光：贴着菱形外沿的一圈半透明粉光，以及四角的星点
GLOW = (0xFF, 0xBE, 0xE6, 0x78)
GLOW_BRIGHT = (0xFF, 0xDC, 0xF2, 0xB4)
SPARKLE = (0xFF, 0xF2, 0xFA, 0xFF)

# 菱形每一行的半宽（以中心为轴）：第 y 行占 x = 7..8 到 0..15 逐行展开
DIAMOND_HALF = {y: min(y, 15 - y) for y in range(16)}


def lerp(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(len(a)))


def build():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))

    def put(x, y, color):
        if 0 <= x < 16 and 0 <= y < 16:
            img.putpixel((x, y), color)

    def span(y):
        half = DIAMOND_HALF[y]
        return 8 - half, 7 + half

    # ---- 菱形主体：外两圈是金属框，里面是粉色炉心（按到中心的距离分层） ----
    center_y = 7.5
    for y in range(16):
        x0, x1 = span(y)
        for x in range(x0, x1 + 1):
            # 距离用「离开菱形边缘的深度」衡量，保证每一行的框厚度一致
            depth = min(x - x0, x1 - x, abs(y - 0), abs(y - 15))
            if depth <= 1:
                put(x, y, FRAME_DARK if depth == 0 else FRAME)
            elif depth <= 2:
                put(x, y, FRAME_LIT)
            else:
                # 炉心：中心亮、往外压深
                radial = max(abs(x - 7.5), abs(y - center_y)) / 4.5
                level = min(1.0, max(0.0, 1.0 - radial))
                if level > 0.70:
                    put(x, y, CORE_HI)
                elif level > 0.34:
                    put(x, y, lerp(CORE_PINK, CORE_HI, (level - 0.34) / 0.36))
                else:
                    put(x, y, lerp(CORE_DEEP, CORE_PINK, level / 0.34))

    # 炉心的十字高光，让中心更像在发光
    for d in range(-1, 2):
        put(7, 7 + d, CORE_HI)
        put(8 + d, 7, CORE_HI)

    # ---- 泛光：菱形外沿一圈半透明粉光（菱形上下已顶到画布，只有左右两侧有位置） ----
    for y in range(16):
        x0, x1 = span(y)
        for gx in (x0 - 1, x1 + 1):
            if 0 <= gx < 16 and img.getpixel((gx, y))[3] == 0:
                put(gx, y, GLOW)

    # ---- 四角星点：灵感结晶那种稀疏亮点，压在菱形外面的空角上 ----
    for cx, cy in ((0, 0), (15, 0), (0, 15), (15, 15)):
        put(cx, cy, SPARKLE)
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            x, y = cx + dx, cy + dy
            if 0 <= x < 16 and 0 <= y < 16 and img.getpixel((x, y))[3] == 0:
                put(x, y, GLOW_BRIGHT)

    return img


def main():
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    path = ITEM_DIR / "high_energy_core.png"
    build().save(path)
    print(f"written {path}")


if __name__ == "__main__":
    main()
