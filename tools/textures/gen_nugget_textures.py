"""粒类物品贴图生成脚本。

形状模板是原版铁粒（一小块金属），配色与锭共用 palettes.py ——
于是「镍粒」和「镍锭」看起来是同一种金属，只是一个大一个小。

用法：
    python tools/textures/gen_nugget_textures.py                 # 生成全部
    python tools/textures/gen_nugget_textures.py nickel_nugget   # 只生成指定粒
输出：
    src/main/resources/assets/inspiration/textures/item/<name>.png
"""

import sys
from pathlib import Path

from PIL import Image

from palettes import MATERIAL_COLORS
from recolor import remap

TEMPLATE = Path(__file__).resolve().parent / "base" / "nugget_base.png"

# 哪些金属有粒：与 ModMaterials 里开了 nugget(true) 的保持一致（钙只有锭）
NUGGETS = {
    "nickel", "uranium", "thorium", "rubidium", "actinium", "neptunium", "plutonium",
}

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"


def main():
    names = sys.argv[1:] or sorted(f"{base}_nugget" for base in NUGGETS)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for name in names:
        base = name[:-len("_nugget")] if name.endswith("_nugget") else name
        if base not in MATERIAL_COLORS or base not in NUGGETS:
            raise SystemExit(f"未知的粒：{name}，可选 {sorted(NUGGETS)}")
        dark, light = MATERIAL_COLORS[base]
        path = ITEM_DIR / f"{base}_nugget.png"
        remap(Image.open(TEMPLATE), dark, light).save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
