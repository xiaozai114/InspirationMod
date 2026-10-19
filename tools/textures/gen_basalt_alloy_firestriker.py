"""玄武岩合金打火石的贴图生成脚本。

做法就是名字本身：**原版打火石 + 一层暗色滤镜**。原版 `flint_and_steel.png` 是一张
纯灰度图（12 档灰），所以不需要按颜色逐条映射，直接在亮度上做两件事：

1. 整体压暗（{SHADE}）—— 玄武岩合金本来就是玄武岩 + 远古残骸熔出来的深色合金；
2. 往冷蓝灰偏一点（{COOL_R} / {COOL_B}）—— 沿用 `gen_injection_blocks.py` 里玄武岩那套
   冷灰蓝调，让它跟玄武岩合金块、玄武岩基底看起来是一路的。

只动亮度不动形状，所以火石与钢片的轮廓、高光层次全部保留；
透明像素原样跳过。原版贴图要是哪天不再是灰度，脚本会直接报错提醒。

用法：
    python tools/textures/gen_basalt_alloy_firestriker.py [client-extra.jar 路径]
输出：
    src/main/resources/assets/inspiration/textures/item/basalt_alloy_firestriker.png
"""

import sys
import zipfile
from pathlib import Path

from PIL import Image

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"

VANILLA_ENTRY = "assets/minecraft/textures/item/flint_and_steel.png"

# 整体压暗系数：0.55 意味着最亮的高光也从 185 掉到 102，暗部仍然沉得下去。
SHADE = 0.55

# 冷调：红略压、蓝略抬，得到玄武岩那种偏蓝的深灰。
COOL_R = 0.94
COOL_G = 1.00
COOL_B = 1.10


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


def shade(gray):
    """把一档灰压暗并染上玄武岩的冷调。"""
    value = gray * SHADE
    return (
        clamp(value * COOL_R),
        clamp(value * COOL_G),
        clamp(value * COOL_B),
    )


def clamp(value):
    return max(0, min(255, round(value)))


def main():
    jar_path = Path(sys.argv[1]) if len(sys.argv) > 1 else find_client_extra()
    source = load_vanilla(jar_path)

    out = Image.new("RGBA", source.size, (0, 0, 0, 0))
    off_gray = set()
    for y in range(source.height):
        for x in range(source.width):
            r, g, b, a = source.getpixel((x, y))
            if a == 0:
                continue
            if not (r == g == b):
                off_gray.add((r, g, b))
                continue
            out.putpixel((x, y), shade(r) + (a,))

    if off_gray:
        raise SystemExit(
            "原版打火石不再是纯灰度图，滤镜方案要重新设计。非灰度像素："
            f"{sorted(off_gray)}"
        )

    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    path = ITEM_DIR / "basalt_alloy_firestriker.png"
    out.save(path)
    print(f"written {path}（取自 {jar_path.name}）")


if __name__ == "__main__":
    main()
