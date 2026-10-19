"""思维流体与水 / 岩浆反应生成的四种方块贴图。

三种外观模式，共用同一个脚本：
- facet：随机晶面。撒种子点做 Voronoi 划分，每块晶面取一个明暗值，
  并在晶面相接处压暗一档，得到结晶体的棱线（两种灵感结晶用）。
- gel：粘液块式的果冻面。整体近似单色，外描边 + 内高光框 + 四角亮点（灵感凝胶用）。
- rock：噪点岩石。逐像素取石灰色阶，再嵌入紫色晶体簇（思维水晶岩用）。

用法：
    python tools/textures/gen_inspiration_crystal_blocks.py              # 生成全部
    python tools/textures/gen_inspiration_crystal_blocks.py inspiration_gel
输出：
    src/main/resources/assets/inspiration/textures/block/<name>.png
"""

import random
import sys
from pathlib import Path

from PIL import Image

SIZE = 16


class Block:
    """一种结晶方块的外观参数。

    alpha 是整张图的透明度：稀释灵感结晶要明显半透明，浓缩灵感结晶几乎不透明，
    灵感凝胶居中，思维水晶岩完全不透明（是石头，不是晶体）。
    """

    def __init__(self, mode, dark, light, alpha, seed, sites=12,
                 edge=None, seam=None, crystal=None):
        self.mode = mode
        self.dark = dark
        self.light = light
        self.alpha = alpha
        self.seed = seed
        self.sites = sites
        self.edge = edge        # 描边颜色
        self.seam = seam        # 晶面棱线颜色
        self.crystal = crystal  # 岩石中晶体簇的颜色


BLOCKS = {
    # 流动思维流体 + 水：薄荷绿，透明度高
    "diluted_inspiration_crystal": Block(
        mode="facet",
        dark=(0x3E, 0x9E, 0x72), light=(0xCF, 0xF7, 0xD2), alpha=140, seed=11, sites=13,
        edge=(0x24, 0x6E, 0x50), seam=(0x2E, 0x88, 0x60)),
    # 思维流体源 + 水：同色系但浓重得多，几乎不透明
    "concentrated_inspiration_crystal": Block(
        mode="facet",
        dark=(0x1C, 0x5C, 0x38), light=(0x8F, 0xE8, 0x86), alpha=238, seed=23, sites=11,
        edge=(0x0C, 0x33, 0x1F), seam=(0x12, 0x48, 0x2A)),
    # 流动思维流体 + 岩浆：行为像粘液块，用粘液块标志性的果冻面
    "inspiration_gel": Block(
        mode="gel",
        dark=(0x4C, 0xAE, 0x54), light=(0xA8, 0xF0, 0x8E), alpha=205, seed=37,
        edge=(0x25, 0x6E, 0x2C)),
    # 思维流体源 + 岩浆：深灰岩石，表面嵌紫色晶体簇
    "mind_crystal_rock": Block(
        mode="rock",
        dark=(0x33, 0x2D, 0x3D), light=(0x6E, 0x64, 0x7A), alpha=255, seed=53,
        edge=(0x14, 0x11, 0x1A), crystal=(0xB9, 0x6B, 0xF0)),
}

BLOCK_DIR = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures/block"


def rgba(rgb, alpha):
    return (rgb[0], rgb[1], rgb[2], alpha)


def mix(a, b, t):
    """在两个 RGB 颜色之间线性插值，t=0 取 a，t=1 取 b。"""
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))


def facet_grid(sites, seed):
    """逐像素返回 (晶面明暗 0..1, 是否处于晶面交界)。

    种子点撒在贴图外侧一圈，避免晶面全部挤在中心。
    """
    rnd = random.Random(seed)
    points = [(rnd.uniform(-4.0, SIZE + 4.0), rnd.uniform(-4.0, SIZE + 4.0), rnd.random())
              for _ in range(sites)]

    values = [[0.0] * SIZE for _ in range(SIZE)]
    owners = [[0] * SIZE for _ in range(SIZE)]
    for y in range(SIZE):
        for x in range(SIZE):
            best, best_distance, best_index = 0.0, None, 0
            for index, (px, py, value) in enumerate(points):
                distance = (x + 0.5 - px) ** 2 + (y + 0.5 - py) ** 2
                if best_distance is None or distance < best_distance:
                    best_distance, best, best_index = distance, value, index
            values[y][x] = best
            owners[y][x] = best_index

    # 把明暗值归一化到 0..1，否则晶面之间几乎看不出差别
    flat = [v for row in values for v in row]
    low, high = min(flat), max(flat)
    span = (high - low) or 1.0

    grid = []
    for y in range(SIZE):
        row = []
        for x in range(SIZE):
            seam = (x + 1 < SIZE and owners[y][x + 1] != owners[y][x]) or \
                   (y + 1 < SIZE and owners[y + 1][x] != owners[y][x])
            row.append(((values[y][x] - low) / span, seam))
        grid.append(row)
    return grid


def build_facet(spec):
    grid = facet_grid(spec.sites, spec.seed)
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    for y in range(SIZE):
        for x in range(SIZE):
            value, seam = grid[y][x]
            color = mix(spec.dark, spec.light, value)
            if seam:
                color = mix(color, spec.seam, 0.55)
            img.putpixel((x, y), rgba(color, spec.alpha))

    for i in range(SIZE):
        for x, y in ((i, 0), (i, SIZE - 1), (0, i), (SIZE - 1, i)):
            img.putpixel((x, y), rgba(spec.edge, min(255, spec.alpha + 40)))
    return img


def build_gel(spec):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    rnd = random.Random(spec.seed)

    for y in range(SIZE):
        for x in range(SIZE):
            color = mix(spec.dark, spec.light, rnd.uniform(0.45, 0.8))
            if (x // 4 + y // 4) % 2 == 0:
                color = mix(color, spec.light, 0.25)
            img.putpixel((x, y), rgba(color, spec.alpha))

    # 粘液块式的方格：浅色分格线 + 交点更亮
    grid_color = mix(spec.light, (255, 255, 255), 0.35)
    for i in range(0, SIZE, 4):
        for j in range(SIZE):
            img.putpixel((i, j), rgba(mix(spec.light, grid_color, 0.6), spec.alpha))
            img.putpixel((j, i), rgba(mix(spec.light, grid_color, 0.6), spec.alpha))

    for i in range(SIZE):
        for x, y in ((i, 0), (i, SIZE - 1), (0, i), (SIZE - 1, i)):
            img.putpixel((x, y), rgba(spec.edge, min(255, spec.alpha + 40)))
    return img


def build_rock(spec):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    rnd = random.Random(spec.seed)

    for y in range(SIZE):
        for x in range(SIZE):
            img.putpixel((x, y), rgba(mix(spec.dark, spec.light, rnd.random()), spec.alpha))

    # 三簇紫色晶体，用菱形轮廓 + 中心亮点做出晶体感
    for cx, cy in ((4, 5), (10, 4), (7, 11)):
        for dy in range(-2, 3):
            width = 2 - abs(dy)
            for dx in range(-width, width + 1):
                x, y = cx + dx, cy + dy
                if 0 < x < SIZE - 1 and 0 < y < SIZE - 1:
                    shade = mix(spec.crystal, spec.light, 0.25 if abs(dx) == width else 0.0)
                    img.putpixel((x, y), rgba(shade, 255))
        img.putpixel((cx, cy), rgba(mix(spec.crystal, (255, 255, 255), 0.55), 255))

    for i in range(SIZE):
        for x, y in ((i, 0), (i, SIZE - 1), (0, i), (SIZE - 1, i)):
            img.putpixel((x, y), rgba(spec.edge, 255))
    return img


BUILDERS = {"facet": build_facet, "gel": build_gel, "rock": build_rock}


def build(spec):
    return BUILDERS[spec.mode](spec)


def main():
    names = sys.argv[1:] or list(BLOCKS)
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        if name not in BLOCKS:
            raise SystemExit(f"未知的方块：{name}，可选 {sorted(BLOCKS)}")
        path = BLOCK_DIR / f"{name}.png"
        build(BLOCKS[name]).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
