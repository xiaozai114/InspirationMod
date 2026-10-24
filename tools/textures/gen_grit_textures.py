"""尘类物品贴图生成脚本。

尘的贴图就是等比缩小的粉：9 尘 = 1 粒、81 尘 = 1 锭，形状上它该是「比粒还小的一小撮」，
所以模板取粉的贴图（base/dust_base.png），按 SCALE 围绕粉堆中心缩小（位置也跟着居中），
颜色仍然与锭共用 palettes.py。

用法：
    python tools/textures/gen_grit_textures.py                 # 生成全部
    python tools/textures/gen_grit_textures.py neptunium_grit  # 只生成指定尘
输出：
    src/main/resources/assets/inspiration/textures/item/<name>.png
"""

import sys
from pathlib import Path

from PIL import Image

from palettes import MATERIAL_COLORS
from recolor import remap

TEMPLATE = Path(__file__).resolve().parent / "base" / "dust_base.png"

# 缩小比例：粉堆 12×11 → 尘 7×7，比粒（8×6）再小一号
SCALE = 0.6

# 哪些金属有尘：与 ModMaterials 里开了 grit(true) 的保持一致
GRITS = {"neptunium", "plutonium"}

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"


def shrink(image: Image.Image) -> Image.Image:
    """把粉堆等比缩小，围绕粉堆中心贴回同尺寸画布（缩放前后中心不变）。

    取样用「就近取色 + 覆盖率判透明」：颜色直接取源像素（不插值，免得糊成一团），
    是否透明看这一格覆盖到的源像素里不透明的是否过半 —— 于是缩小后边缘依然干脆。
    """
    source = image.convert("RGBA")
    left, top, right, bottom = source.getchannel("A").getbbox()
    width = max(1, round((right - left) * SCALE))
    height = max(1, round((bottom - top) * SCALE))
    origin_x = left + (right - left - width) // 2
    origin_y = top + (bottom - top - height) // 2

    canvas = Image.new("RGBA", source.size, (0, 0, 0, 0))
    span_x = (right - left) / width
    span_y = (bottom - top) / height
    for y in range(height):
        for x in range(width):
            x0 = left + int(x * span_x)
            y0 = top + int(y * span_y)
            x1 = max(x0 + 1, left + int((x + 1) * span_x))
            y1 = max(y0 + 1, top + int((y + 1) * span_y))
            cells = [((sx, sy), source.getpixel((sx, sy)))
                     for sy in range(y0, y1) for sx in range(x0, x1)]
            opaque = [cell for cell in cells if cell[1][3] > 0]
            if len(opaque) * 2 < len(cells):
                continue
            center_x = (x0 + x1 - 1) / 2
            center_y = (y0 + y1 - 1) / 2
            _, pixel = min(opaque,
                           key=lambda cell: (cell[0][0] - center_x) ** 2 + (cell[0][1] - center_y) ** 2)
            canvas.putpixel((origin_x + x, origin_y + y), pixel)
    return canvas


def main():
    names = sys.argv[1:] or sorted(f"{base}_grit" for base in GRITS)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        base = name[:-len("_grit")] if name.endswith("_grit") else name
        if base not in MATERIAL_COLORS or base not in GRITS:
            raise SystemExit(f"未知的尘：{name}，可选 {sorted(GRITS)}")
        dark, light = MATERIAL_COLORS[base]
        path = ITEM_DIR / f"{base}_grit.png"
        remap(shrink(Image.open(TEMPLATE)), dark, light).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
