"""尘类物品贴图生成脚本。

尘比粒还小（9 尘 = 1 粒、81 尘 = 1 锭），形状模板取原版糖 —— 一小堆散着的细颗粒，
正好是「一撮尘」的样子。配色仍然与锭共用 palettes.py。

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

TEMPLATE = Path(__file__).resolve().parent / "base" / "grit_base.png"

# 哪些金属有尘：与 ModMaterials 里开了 grit(true) 的保持一致
GRITS = {"neptunium", "plutonium"}

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"


def main():
    names = sys.argv[1:] or sorted(f"{base}_grit" for base in GRITS)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        base = name[:-len("_grit")] if name.endswith("_grit") else name
        if base not in MATERIAL_COLORS or base not in GRITS:
            raise SystemExit(f"未知的尘：{name}，可选 {sorted(GRITS)}")
        dark, light = MATERIAL_COLORS[base]
        path = ITEM_DIR / f"{base}_grit.png"
        remap(Image.open(TEMPLATE), dark, light).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
