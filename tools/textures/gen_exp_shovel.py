"""灵感之铲的贴图生成脚本。

灵感系列前四件（剑 / 斧 / 镐 / 锄）的原画是手绘的，没有生成脚本；铲子改用
「原版铁铲同形、只换配色」的做法（与 `gen_jade_steel_upgrade_template.py` 同一套路）：
原版材质不进仓库，运行时从 client-extra 资源包里读 `iron_shovel.png`，
按显式映射表把铁色换成灵感系列的绿，木柄原样保留。表里没覆盖到的颜色直接报错，
避免原版贴图改版后这里悄悄失效。

用法：
    python tools/textures/gen_exp_shovel.py [client-extra.jar 路径]
输出：
    src/main/resources/assets/inspiration/textures/item/exp_shovel.png
"""

import sys
import zipfile
from pathlib import Path

from PIL import Image

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
ITEM_DIR = RESOURCE_ROOT / "item"

VANILLA_ENTRY = "assets/minecraft/textures/item/iron_shovel.png"

# 原版铁铲配色 -> 灵感系列配色。左边是原版贴图里出现过的全部颜色。
COLOUR_MAP = {
    (24, 24, 24): (24, 24, 24),        # 描边
    (68, 68, 68): (68, 68, 68),        # 深灰（灵感之锄的同一处也是灰的）
    (193, 193, 193): (166, 217, 115),  # 铁中调 -> 中绿
    (216, 216, 216): (173, 225, 122),  # 铁亮调 -> 亮绿
    (255, 255, 255): (185, 237, 134),  # 高光 -> 最亮绿
    (40, 30, 11): (40, 30, 11),        # 木柄描边
    (73, 54, 21): (73, 54, 21),        # 木柄暗
    (104, 78, 30): (104, 78, 30),      # 木柄中
    (137, 103, 39): (137, 103, 39),    # 木柄亮
}


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


def main():
    jar_path = Path(sys.argv[1]) if len(sys.argv) > 1 else find_client_extra()
    source = load_vanilla(jar_path)

    out = Image.new("RGBA", source.size)
    unmapped = set()
    for y in range(source.height):
        for x in range(source.width):
            pixel = source.getpixel((x, y))
            if pixel[3] == 0:
                continue
            target = COLOUR_MAP.get(pixel[:3])
            if target is None:
                unmapped.add(pixel[:3])
                continue
            out.putpixel((x, y), target + (pixel[3],))

    if unmapped:
        raise SystemExit(f"原版贴图里有未覆盖的颜色，请补进 COLOUR_MAP：{sorted(unmapped)}")

    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    path = ITEM_DIR / "exp_shovel.png"
    out.save(path)
    print(f"written {path}（取自 {jar_path.name}）")


if __name__ == "__main__":
    main()
