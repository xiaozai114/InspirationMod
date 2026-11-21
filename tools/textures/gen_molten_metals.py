"""熔融金属的贴图：**只补缺失的，绝不覆盖已有美术**。

材料声明 `.molten(true)` 之后，客户端会按 `block/molten_<base>_still` 与 `_flowing` 找图。
已经有专属贴图的材料（例如无恒合金）这里一律跳过，保持原样；没有图的才用液态等离子
那两张当模板、按目标色相染一份出来。桶的贴图同理（`item/molten_<base>_bucket`）。

新增一条熔融金属时：
1. 在下面的 TINTS 里加一行 `"<base>": (色相0~1, 明度系数)`；
2. 跑本脚本 —— 已有贴图不会被碰，缺的会补上。

用法：
    python tools/textures/gen_molten_metals.py
"""

import colorsys
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
BLOCK_DIR = ROOT / "block"
ITEM_DIR = ROOT / "item"

TEMPLATE_STILL = "molten_plasma_still.png"
TEMPLATE_FLOWING = "molten_plasma_flowing.png"
TEMPLATE_BUCKET = "molten_plasma_bucket.png"

# base -> (色相, 明度系数)
TINTS = {
    "ephemeral_alloy": (0.78, 0.92),
}


def tinted(source, hue, value_scale):
    out = source.copy()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = out.getpixel((x, y))
            if a == 0:
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            nr, ng, nb = colorsys.hsv_to_rgb(hue, min(1.0, s * 1.05), v * value_scale)
            out.putpixel((x, y), (round(nr * 255), round(ng * 255), round(nb * 255), a))
    return out


def fill(base, hue, value_scale):
    targets = [
        (BLOCK_DIR / f"molten_{base}_still.png", BLOCK_DIR / TEMPLATE_STILL),
        (BLOCK_DIR / f"molten_{base}_flowing.png", BLOCK_DIR / TEMPLATE_FLOWING),
        (ITEM_DIR / f"molten_{base}_bucket.png", ITEM_DIR / TEMPLATE_BUCKET),
    ]
    for target, template in targets:
        if target.exists():
            print(f"skip (exists) {target.name}")
            continue
        if not template.exists():
            print(f"missing template {template.name}")
            continue
        tinted(Image.open(template).convert("RGBA"), hue, value_scale).save(target)
        print(f"written {target.name}")


def main():
    for base, (hue, value_scale) in TINTS.items():
        fill(base, hue, value_scale)


if __name__ == "__main__":
    main()
