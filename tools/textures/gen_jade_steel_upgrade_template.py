"""升级模板贴图生成脚本（翡翠钢档 + 注能翡翠钢档）。

沿用原版下界合金升级模板的像素形制，只换配色，因此几张模板贴图的轮廓、
锯齿边缘与宝石位置完全一致：

* ``jade_steel_upgrade_template`` —— 暗红底 + 青宝石 -> 深玉底 + 薄荷宝石；
* ``energized_jade_steel_upgrade_template`` —— 深紫底 + 电光青宝石（与无恒合金、高能机型同一族的紫）。

原版贴图属于游戏本体资源，不放进本仓库：默认从 client-extra 资源包里读，
也可以把某一份原版材质路径作为参数传进来。

用法：
    python tools/textures/gen_jade_steel_upgrade_template.py [原版模板.png]
输出：
    src/main/resources/assets/inspiration/textures/item/{jade_steel,energized_jade_steel}_upgrade_template.png
"""

import sys
import zipfile
from pathlib import Path

from PIL import Image

RESOURCE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
TARGET_DIR = RESOURCE_ROOT / "item"

VANILLA_ENTRY = "assets/minecraft/textures/item/netherite_upgrade_smithing_template.png"

# 原版下界合金升级模板用到的全部颜色（键），以及两档目标配色（值）。
# 渲染时若有未覆盖的颜色会直接报错，避免原版贴图改版后悄悄漏色。
PLATE_COLORS = [(52, 14, 14, 255), (81, 21, 21, 255), (101, 40, 40, 255),
                (114, 50, 50, 255), (133, 66, 66, 255)]
GEM_COLORS = [(32, 154, 184, 255), (75, 201, 201, 255), (96, 232, 232, 255)]

PALETTES = {
    # 底板：暗红棕色阶 -> 深玉绿色阶（由暗到亮）；宝石：青蓝 -> 薄荷绿
    "jade_steel_upgrade_template": [
        (16, 60, 26, 255), (22, 80, 34, 255), (30, 104, 42, 255),
        (36, 118, 48, 255), (48, 140, 58, 255),
        (46, 176, 140, 255), (120, 236, 194, 255), (196, 255, 232, 255),
    ],
    # 底板：暗紫 -> 淡紫（与无恒合金同族）；宝石：电光青，亮部近白
    "energized_jade_steel_upgrade_template": [
        (30, 16, 58, 255), (44, 25, 80, 255), (60, 37, 102, 255),
        (72, 45, 118, 255), (94, 63, 146, 255),
        (52, 164, 208, 255), (140, 224, 252, 255), (228, 250, 255, 255),
    ],
}


def find_client_extra():
    """没传参数时，去构建目录里找 client-extra 资源包。"""
    artifacts = Path(__file__).resolve().parents[2] / "build/moddev/artifacts"
    candidates = sorted(artifacts.glob("neoforge-*-client-extra-*.jar"))
    if not candidates:
        raise SystemExit(f"找不到 client-extra 资源包，请把原版模板路径作为参数传进来（找过 {artifacts}）")
    return candidates[-1]


def load_vanilla(source):
    if source is not None:
        if not source.is_file():
            raise SystemExit(f"找不到原版贴图：{source}")
        return Image.open(source).convert("RGBA")
    jar_path = find_client_extra()
    with zipfile.ZipFile(jar_path) as jar:
        with jar.open(VANILLA_ENTRY) as stream:
            print(f"取自 {jar_path.name}")
            return Image.open(stream).convert("RGBA")


def main():
    source_path = Path(sys.argv[1]).expanduser() if len(sys.argv) > 1 else None
    image = load_vanilla(source_path)

    colors = list(PLATE_COLORS) + list(GEM_COLORS)
    unknown = {p for p in image.getdata() if p[3] > 0} - set(colors)
    if unknown:
        raise SystemExit(f"原版贴图里出现了未覆盖的颜色：{sorted(unknown)}")

    TARGET_DIR.mkdir(parents=True, exist_ok=True)
    for name, targets in PALETTES.items():
        mapping = dict(zip(colors, targets))
        out = Image.new("RGBA", image.size)
        out.putdata([mapping.get(p, p) for p in image.getdata()])
        path = TARGET_DIR / f"{name}.png"
        out.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
