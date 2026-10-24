"""JEI 配方背景贴图生成脚本。

背景是一块原版风格面板：上边与左边 1px 白，右边与下边 1px 深灰，面板本体 #C6C6C6，
中间画一支指向右侧的箭头（箭头颜色按分类区分）。面板尺寸与箭头位置按分类配置，
默认是「两输入 + 一输出」的 104x54，「灵感装配台」则是四输入布局的 104x74。

用法：
    python tools/textures/gen_jei_background.py           # 生成全部背景
    python tools/textures/gen_jei_background.py twisting  # 只生成指定背景
输出：
    src/main/resources/assets/inspiration/textures/gui/jei/<name>.png
"""

import sys
from pathlib import Path
from typing import NamedTuple

from PIL import Image

WIDTH = 104
DEFAULT_HEIGHT = 54
DEFAULT_ARROW_Y = 15

PANEL = (0xC6, 0xC6, 0xC6, 0xFF)
BORDER_LIGHT = (0xFF, 0xFF, 0xFF, 0xFF)
BORDER_DARK = (0x55, 0x55, 0x55, 0xFF)

# 箭杆与箭头的尺寸；箭头尖端的横坐标由箭杆长度决定。
ARROW_SHAFT_LENGTH = 15
ARROW_HEAD_LENGTH = 8
ARROW_HEAD_HALF_HEIGHT = 7


class Background(NamedTuple):
    """一种分类的背景配置。"""

    color: tuple
    shaft_x: int
    height: int = DEFAULT_HEIGHT
    arrow_y: int = DEFAULT_ARROW_Y
    # 需要比默认更宽的面板时单独指定；分隔线（>=0 时画一条横线）用来把槽位区与说明区隔开
    width: int = WIDTH
    divider_y: int = -1


BACKGROUNDS = {
    # 融合：三材料配方（思维矩阵锭）要多一个输入槽，面板统一加高一行
    "fusion": Background((0x3F, 0xA9, 0xC9, 0xFF), 51, 74),
    "reshaping": Background((0x60, 0xD6, 0x84, 0xFF), 51),
    "right_click_block": Background((0x3F, 0xA9, 0xC9, 0xFF), 51),
    "twisting": Background((0x4F, 0xF0, 0xC0, 0xFF), 41),
    # 灵感装配台：最多 3x2 输入槽 + 底下两格流体罐，面板加宽到 122、加高到 96
    "assembling": Background((0xB6, 0xF0, 0x5A, 0xFF), 74, 96, 24, 122),
    # 注入：两行输入各带来源图标与角色标签，右侧箭头指向产物，
    # 分隔线下方是说明区（三行固定信息，种子随机配方再加一行解释）
    "injection": Background((0x9C, 0x6B, 0xFF, 0xFF), 94, 112, 33, 150, 50),
    # 结构模拟机的结构预览：**没有箭头**（不是「输入→输出」，而是逐层的方块网格），
    # shaft_x 给负数让 draw_arrow 整个跳过；面板正好放下 4 列 × 3 行、间距 18 的网格。
    # 分隔线下方写「第几层 / 朝向」这类说明。
    "structure_preview": Background((0x9C, 0x6B, 0xFF, 0xFF), -1, 86, 0, 104, 60),
    # 流体提取：单输入 -> 一格流体罐（产物是流体不是物品），箭头沿用单输入的居中位置，
    # 颜色取熔体的橙红以区别于其它分类。面板加高到 74：底部除耗时与耗电外还要写产物量
    "fluid_extracting": Background((0xF0, 0x8A, 0x2E, 0xFF), 41, 74),
    # 原料精炼：单输入 -> 三个输出槽竖排（主产物在上、两个副产物在下），
    # 箭头仍指向最上面那个主产物；面板加高到 86 才放得下三格加两行说明
    "refining": Background((0xE8, 0x8A, 0x3A, 0xFF), 41, 86),
}

OUTPUT_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/gui/jei"


def draw_arrow(img, background):
    shaft_x = background.shaft_x
    if shaft_x < 0:
        # 负数表示这个面板不画箭头（例如结构预览：画的是方块网格，没有输入输出方向）
        return
    center_y = background.arrow_y
    # 箭杆
    for y in range(center_y - 2, center_y + 3):
        for x in range(shaft_x, shaft_x + ARROW_SHAFT_LENGTH):
            img.putpixel((x, y), background.color)
    # 箭头（向右收窄的三角形）
    head_x = shaft_x + ARROW_SHAFT_LENGTH
    for step in range(ARROW_HEAD_LENGTH + 1):
        half = round(ARROW_HEAD_HALF_HEIGHT * (ARROW_HEAD_LENGTH - step) / ARROW_HEAD_LENGTH)
        for y in range(center_y - half, center_y + half + 1):
            img.putpixel((head_x + step, y), background.color)


def build(background):
    height = background.height
    width = background.width
    img = Image.new("RGBA", (width, height), PANEL)
    for x in range(width):
        img.putpixel((x, 0), BORDER_LIGHT)
        img.putpixel((x, height - 1), BORDER_DARK)
    for y in range(height):
        img.putpixel((0, y), BORDER_LIGHT)
        img.putpixel((width - 1, y), BORDER_DARK)
    draw_arrow(img, background)
    divider = background.divider_y
    if divider >= 0:
        for x in range(3, width - 3):
            img.putpixel((x, divider), BORDER_LIGHT)
            img.putpixel((x, divider + 1), BORDER_DARK)
    return img


def main():
    names = sys.argv[1:] or list(BACKGROUNDS)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        if name not in BACKGROUNDS:
            raise SystemExit(f"未知的背景名：{name}，可选 {sorted(BACKGROUNDS)}")
        path = OUTPUT_DIR / f"{name}.png"
        build(BACKGROUNDS[name]).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
