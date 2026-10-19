"""翡翠钢 / 注能翡翠钢工具贴图生成脚本。

灵感系列工具（灵感之剑 / 斧 / 镐 / 锄 / 铲）的贴图由「绿色工具头 + 木柄 + 黑色描边 + 高光」组成。
本脚本按像素分类后只替换工具头的颜色，保证轮廓、柄、阴影位置与原贴图完全一致：

* 中性色（饱和度低于阈值，即描边与高光）原样保留；
* 褐色木柄（色相落在木色窗口内）原样保留；
* 其余带色像素（绿色工具头、橄榄色暗面、黄色光晕）保留各自的明度与透明度，
  色相统一压到目标色相，饱和度做轻微压缩。

两档配色（见 STYLES）：翡翠钢是玉绿，注能翡翠钢是更亮更冷的青，并额外点几颗星点，
一眼能看出后者是「灌了灵感」的那一档。

用法：
    python tools/textures/gen_jade_steel_tools.py                  # 生成两档全部
    python tools/textures/gen_jade_steel_tools.py jade_steel       # 只生成翡翠钢一档
    python tools/textures/gen_jade_steel_tools.py jade_steel hoe   # 只生成某一件
输出：
    src/main/resources/assets/inspiration/textures/item/<档位>_<工具>.png
"""

import colorsys
import sys
from pathlib import Path

from PIL import Image

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"

# 工具名 -> 原贴图名
SOURCES = {
    "sword": "exp_sword",
    "axe": "sticky_axe",
    "pickaxe": "exp_pickaxe",
    "hoe": "exp_hoe",
    "shovel": "exp_shovel",
}


class Style:
    """一档配色的换色参数。"""

    def __init__(self, hue, saturation_base, saturation_scale, value_scale, sparkles=()):
        self.hue = hue
        self.saturation_base = saturation_base
        self.saturation_scale = saturation_scale
        self.value_scale = value_scale
        self.sparkles = sparkles


STYLES = {
    # 翡翠钢：玉青绿（灵感系列的绿在 90~125 之间，这里压到 140，两者一眼可分）
    "jade_steel": Style(hue=140.0, saturation_base=0.30, saturation_scale=0.55, value_scale=0.95),
    # 注能翡翠钢：色相继续往青推、提亮一档，并点缀星点
    "energized_jade_steel": Style(hue=178.0, saturation_base=0.34, saturation_scale=0.58, value_scale=1.06,
                                  sparkles=((5, 3), (10, 8), (7, 12))),
}

# 木柄的色相窗口与最低饱和度（实测贴图的木柄色相都在 38~40 之间）
WOOD_HUE_RANGE = (20.0, 45.0)
WOOD_SATURATION_MIN = 0.5

# 饱和度低于该值的像素视为描边 / 高光，保持原色
NEUTRAL_SATURATION_MAX = 0.18

# 星臂混入白色的比例
SPARKLE_ARM_WHITE = 0.6


def hue_saturation_value(color):
    r, g, b = color[:3]
    return colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)


def is_wood(hue_degrees, saturation):
    return WOOD_HUE_RANGE[0] <= hue_degrees <= WOOD_HUE_RANGE[1] and saturation >= WOOD_SATURATION_MIN


def convert(pixel, style):
    """把工具头换成目标色，明度与透明度不变。"""
    hue, saturation, value = hue_saturation_value(pixel)
    if pixel[3] == 0 or saturation < NEUTRAL_SATURATION_MAX or is_wood(hue * 360, saturation):
        return pixel
    r, g, b = colorsys.hsv_to_rgb(
        style.hue / 360,
        style.saturation_base + style.saturation_scale * saturation,
        min(1.0, value * style.value_scale),
    )
    return round(r * 255), round(g * 255), round(b * 255), pixel[3]


def mix_with_white(pixel, ratio):
    return tuple(round(c + (255 - c) * ratio) for c in pixel[:3]) + (pixel[3],)


def draw_sparkles(image, points):
    """在指定位置点十字星：星心纯白，四臂往白里混 3/4。"""
    for x, y in points:
        for dx, dy in ((0, 0), (-1, 0), (1, 0), (0, -1), (0, 1)):
            px, py = x + dx, y + dy
            if not (0 <= px < image.width and 0 <= py < image.height):
                continue
            pixel = image.getpixel((px, py))
            # 只在工具头的不透明区域里点，避免星点飘在空白处
            if pixel[3] == 0 or max(pixel[:3]) - min(pixel[:3]) < 12:
                continue
            image.putpixel((px, py), mix_with_white(pixel, 1.0 if (dx, dy) == (0, 0) else SPARKLE_ARM_WHITE))


def generate(style_name, tool_name, source_name):
    style = STYLES[style_name]
    image = Image.open(ITEM_DIR / f"{source_name}.png").convert("RGBA")
    out = Image.new("RGBA", image.size)
    converted = [convert(image.getpixel((x, y)), style)
                 for y in range(image.height) for x in range(image.width)]
    if all(new == old for new, old in zip(converted, image.getdata()) if old[3] > 0):
        raise SystemExit(f"{source_name} 没有像素被换色，配色规则可能已失效")
    out.putdata(converted)
    draw_sparkles(out, style.sparkles)
    path = ITEM_DIR / f"{style_name}_{tool_name}.png"
    out.save(path)
    return path


def main():
    args = sys.argv[1:]
    style_names = [a for a in args if a in STYLES] or list(STYLES)
    tool_names = [a for a in args if a in SOURCES] or list(SOURCES)
    unknown = [a for a in args if a not in STYLES and a not in SOURCES]
    if unknown:
        raise SystemExit(f"未知参数：{unknown}，可选档位 {sorted(STYLES)}、工具 {sorted(SOURCES)}")
    for style_name in style_names:
        for tool_name in tool_names:
            path = generate(style_name, tool_name, SOURCES[tool_name])
            print(f"written {path}")


if __name__ == "__main__":
    main()
