"""地核钻探者的贴图生成脚本。

机身沿用本模组机器那一套「1px 描边 + 顶部高光 + 内凹面板」的语言，
但主色改成深绿 —— 与已有的黄绿（重塑机）、青绿（扭曲机）、橄榄绿（装配台）都不撞，
再用紫色画钻芯 / 卡盘，凑成用户要的「绿 + 紫」。
同时生成「玄武岩合金钻头」物品贴图：仿格雷科技的麻花钻头（尖端朝上 + 两条螺旋排屑槽 + 加宽夹持座），
灰铁本体、刃口镶紫，与机身的绿紫配色呼应。

用法：
    python tools/textures/gen_core_driller.py
输出：
    src/main/resources/assets/inspiration/textures/block/core_driller_{front,side,top}.png
    src/main/resources/assets/inspiration/textures/item/basalt_alloy_drill_bit.png
"""

import math
from pathlib import Path

from PIL import Image

# ---------------------------------------------------------------- 机器调色板
BODY = (0x2B, 0x3B, 0x2C, 0xFF)       # 机身：墨绿
BORDER = (0x12, 0x18, 0x13, 0xFF)     # 外描边
BEVEL = (0x1D, 0x27, 0x1E, 0xFF)      # 倒角 / 阴影
PLATE = (0x14, 0x1B, 0x15, 0xFF)      # 面板底板
DARK = (0x0A, 0x0E, 0x0B, 0xFF)       # 面板内凹
GREEN = (0x5F, 0xE0, 0x7A, 0xFF)      # 主色：亮绿
GREEN_DIM = (0x34, 0x8A, 0x45, 0xFF)  # 绿的暗部
PURPLE = (0xA0, 0x6C, 0xE0, 0xFF)     # 副色：紫
GLINT = (0xD8, 0xA8, 0xFF, 0xFF)      # 紫的高光
HIGHLIGHT = (0x45, 0x5C, 0x48, 0xFF)  # 机身顶部高光

SIZE = 16
BLOCK_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"
ITEM_DIR = BLOCK_DIR.parent / "item"


def new_canvas():
    return Image.new("RGBA", (SIZE, SIZE), BORDER)


def put(img, x, y, color):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def draw_shell(img):
    """机身外壳：描边之内铺满主体色，顶部两角各一点高光。"""
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
    """正面：绿色卡盘窗口里竖着一支紫色钻头。"""
    img = new_canvas()
    draw_shell(img)
    draw_inset_panel(img)

    # 面板
    for y in range(3, SIZE - 3):
        for x in range(3, SIZE - 3):
            put(img, x, y, PLATE)

    # 窗口：亮绿方框
    for x in range(4, SIZE - 4):
        put(img, x, 4, GREEN)
        put(img, x, SIZE - 5, GREEN)
    for y in range(4, SIZE - 4):
        put(img, 4, y, GREEN)
        put(img, SIZE - 5, y, GREEN)

    # 窗口内压暗
    for y in range(5, SIZE - 5):
        for x in range(5, SIZE - 5):
            put(img, x, y, DARK)

    # 钻杆：中间两列紫色
    for y in range(5, SIZE - 4):
        put(img, 7, y, PURPLE)
        put(img, 8, y, PURPLE)

    # 螺旋槽：两侧交替点亮，做出旋转刃的错觉
    for y in range(6, SIZE - 5):
        if y % 2 == 0:
            put(img, 6, y, GLINT)
            put(img, 9, y, GREEN_DIM)
        else:
            put(img, 6, y, GREEN_DIM)
            put(img, 9, y, GLINT)

    # 卡盘：顶部一道亮绿横梁，底部留一颗紫色铆钉表示动力头
    put(img, 7, 5, GLINT)
    put(img, 8, 5, GLINT)
    put(img, 7, 11, GLINT)
    put(img, 8, 11, GLINT)
    return img


def side():
    """侧面：两道绿色导轨 + 中间的紫芯散热道。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((4, 3), (5, 3), (10, 3), (11, 3), (4, 12), (5, 12), (10, 12), (11, 12)):
        put(img, point[0], point[1], BEVEL)

    # 中央紫色散热道
    for x in (7, 8):
        for y in range(4, 13):
            put(img, x, y, DARK)
    put(img, 7, 4, PURPLE)
    put(img, 8, 4, PURPLE)
    for y in (6, 8, 10, 12):
        put(img, 7, y, PURPLE)
        put(img, 8, y, PURPLE)

    # 两侧绿色导轨
    for x in (4, 5):
        for y in range(4, 13):
            put(img, x, y, GREEN if (x + y) % 2 == 0 else GREEN_DIM)
    for x in (10, 11):
        for y in range(4, 13):
            put(img, x, y, GREEN if (x + y) % 2 == 0 else GREEN_DIM)
    return img


def top():
    """顶面：方形的作业口，四角绿边框、中间紫色钻芯。"""
    img = new_canvas()
    draw_shell(img)

    for point in ((3, 2), (12, 2), (3, 13), (12, 13)):
        put(img, point[0], point[1], BEVEL)

    for y in range(4, 12):
        for x in range(4, 12):
            if x in (4, 11) or y in (4, 11):
                put(img, x, y, GREEN)

    for y in range(5, 11):
        for x in range(5, 11):
            put(img, x, y, DARK)

    for y in range(6, 10):
        for x in range(6, 10):
            put(img, x, y, PURPLE)
    for x in (7, 8):
        put(img, x, 7, GLINT)
        put(img, x, 8, GLINT)
    return img


# ---------------------------------------------------------------- 钻头物品
METAL = (0x4C, 0x50, 0x59, 0xFF)       # 玄武岩合金的本体灰
METAL_LIT = (0x6E, 0x73, 0x7F, 0xFF)   # 受光面
METAL_DARK = (0x32, 0x36, 0x3D, 0xFF)  # 暗部
METAL_DEEP = (0x22, 0x25, 0x2B, 0xFF)  # 排屑槽底
BIT_PURPLE = (0xA0, 0x6C, 0xE0, 0xFF)

# 刃头（锥形）：y -> (左, 右)
BIT_HEAD = {0: (7, 8), 1: (7, 8), 2: (6, 9), 3: (6, 9), 4: (5, 10), 5: (4, 11)}
# 杆身：靠 4 格一循环的两条斜槽做出螺旋排屑槽
BIT_SHAFT = {y: (4, 11) for y in range(6, 13)}
# 夹持座：比杆身宽一圈
BIT_BASE = {13: (3, 12), 14: (3, 12), 15: (3, 12)}
BIT_SPIRAL_PERIOD = 4


def drill_bit():
    """
    玄武岩合金钻头：仿格雷科技的麻花钻头 —— 尖端朝上，中间是带两条螺旋排屑槽的杆身，
    下面接一段加宽的夹持座，刃尖镶紫。缩到 16×16 也能看出是钻头而不是一根铁棍。
    """
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))

    def p(x, y, color):
        if 0 <= x < SIZE and 0 <= y < SIZE:
            img.putpixel((x, y), color)

    def mix(a, b, t):
        return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(4))

    # 刃头：左侧受光向右压暗，右侧描一线暗边
    for y, (x0, x1) in BIT_HEAD.items():
        for x in range(x0, x1 + 1):
            p(x, y, mix(METAL_LIT, METAL, (x - x0) / max(1, x1 - x0)))
        p(x1, y, METAL_DARK)
    p(7, 0, GLINT)      # 钻尖高光

    # 杆身：左侧受光向右压暗
    for y, (x0, x1) in BIT_SHAFT.items():
        for x in range(x0, x1 + 1):
            p(x, y, mix(METAL_LIT, METAL, (x - x0) / max(1, x1 - x0)))
        p(x1, y, METAL_DARK)

    # 螺旋排屑槽：两条平行斜线，槽底压深、迎光边提亮
    for y, (x0, x1) in BIT_SHAFT.items():
        a = (y - 6) % BIT_SPIRAL_PERIOD
        for x in (x0 + a, x0 + a + 4):
            if x0 <= x <= x1:
                p(x, y, METAL_DEEP)
                if x - 1 >= x0:
                    p(x - 1, y, METAL_LIT)

    # 夹持座：上沿提亮、下沿压深，做出圆柱的厚度
    for y, (x0, x1) in BIT_BASE.items():
        for x in range(x0, x1 + 1):
            p(x, y, mix(METAL_DARK, METAL_LIT, 1.0 - (y - 13) / 2.0))
        p(x0, y, METAL_DEEP)
        p(x1, y, METAL_DEEP)
        p(x0 + 1, y, METAL_LIT)

    # 刃口的紫色涂层：锥面上左右各点两道
    for y in (2, 4):
        x0, x1 = BIT_HEAD[y]
        p(x1, y, BIT_PURPLE)
        p(x0, y + 1, BIT_PURPLE)
    return tilt(img)


def tilt(img, degrees=45.0):
    """
    把画好的竖直钻头整体斜过来（尖端指向右上）。

    不是拿 PIL 的 rotate —— 45° 下 NEAREST 采样会落在像素边界上，出来一圈锯齿还带孔洞。
    这里自己做逆向最近邻：遍历画布上的每个像素，反算回原图的坐标再取样，一个目标像素
    对应一个源像素，边缘始终是硬的；缩放系数按「旋转后的包围盒刚好塞满画布」现算，
    所以钻头既不会出框也不会缩得太小。
    """
    cos_a = math.cos(math.radians(degrees))
    sin_a = math.sin(math.radians(degrees))
    center = (SIZE - 1) / 2.0

    points = [(x - center, y - center)
              for y in range(SIZE) for x in range(SIZE)
              if img.getpixel((x, y))[3] > 20]
    span_x = max(p[0] * cos_a - p[1] * sin_a for p in points)
    span_y = max(p[0] * sin_a + p[1] * cos_a for p in points)
    span_min_x = min(p[0] * cos_a - p[1] * sin_a for p in points)
    span_min_y = min(p[0] * sin_a + p[1] * cos_a for p in points)
    span = max(span_x - span_min_x, span_y - span_min_y, 1.0)
    scale = (SIZE - 1) / span

    out = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    for ty in range(SIZE):
        for tx in range(SIZE):
            rx, ry = tx - center, ty - center
            sx = round(center + (rx * cos_a + ry * sin_a) / scale)
            sy = round(center + (-rx * sin_a + ry * cos_a) / scale)
            if 0 <= sx < SIZE and 0 <= sy < SIZE:
                pixel = img.getpixel((sx, sy))
                if pixel[3] > 20:
                    out.putpixel((tx, ty), pixel)
    return out


def main():
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name, image in (("front", front()), ("side", side()), ("top", top())):
        path = BLOCK_DIR / f"core_driller_{name}.png"
        image.save(path)
        print(f"written {path}")
    item_path = ITEM_DIR / "basalt_alloy_drill_bit.png"
    drill_bit().save(item_path)
    print(f"written {item_path}")


if __name__ == "__main__":
    main()
