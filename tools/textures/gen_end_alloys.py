"""末地合金 / 无恒合金 / 末地合金盾牌 / 世界数据探测器 的贴图生成脚本。

末地合金 = 「下级合金（下界合金锭）换成淡黄色」+「叠一层末地石材质」：
    下界合金锭的轮廓、高光与阴影全部保留，先按亮度重映射到淡黄色调，
    再用末地石的亮度做一层乘法调制 —— 于是合金表面带上了末地石那种细碎斑点。
无恒合金 = 「把无恒物质整张叠在末地合金上」+「盖一层浓紫色滤镜」：
    离中心越近无恒物质占比越高（中心满覆盖、外圈 60%、再往外保留合金本色），
    而锭轮廓之外的无恒物质**不裁剪**，原样画在锭外 —— 得到「紫色晶体从合金里长出来」的观感。
末地合金盾牌：直接改原版盾牌的 64×64 展开图（模板 `shield_base_nopattern.png`），
    只把盾正面那 12×22 换成末地石材质，盾背 / 侧棱 / 把手保持原版 —— 这样它用起
    来就是一面原版盾牌，只是盾面材质不同。
世界数据探测器：手绘，深紫灰机身 + 青色读数屏 + 紫色探头。

模板（`tools/textures/base/`，从原版资源包抽出）：
    netherite_ingot.png        —— 下级合金的形状来源
    end_stone.png              —— 末地石材质
    shield_base_nopattern.png  —— 原版盾牌的展开图（无纹饰版）
另有已生成好的 item/ephemeral_material.png 作为无恒物质的颜色来源。

用法：
    python tools/textures/gen_end_alloys.py
输出：
    src/main/resources/assets/inspiration/textures/item/end_alloy.png
    src/main/resources/assets/inspiration/textures/item/ephemeral_alloy.png
    src/main/resources/assets/inspiration/textures/entity/end_alloy_shield.png
    src/main/resources/assets/inspiration/textures/item/world_data_detector.png
"""

from pathlib import Path

from PIL import Image

TEXTURE_ROOT = Path(__file__).resolve().parents[2] / "src/main/resources/assets/inspiration/textures"
BASE_ROOT = Path(__file__).resolve().parent / "base"

ITEM_DIR = TEXTURE_ROOT / "item"
ENTITY_DIR = TEXTURE_ROOT / "entity"

# 淡黄色调：下界合金的暗部与亮部各自映射到这两个端点（整体偏亮，让无恒合金看着更「轻」）
ALLOY_DARK = (0xA9, 0x9C, 0x62)
ALLOY_LIGHT = (0xFF, 0xFB, 0xE4)

# 末地石材质对合金的调制强度：乘性系数落在 [1 - K, 1 + K]
END_STONE_STRENGTH = 0.16

# 淡紫色滤镜：几乎透明，只在合金上留一层紫调，不盖掉金属本色
PURPLE_FILTER = (0xE4, 0xCC, 0xFF)
PURPLE_FILTER_ALPHA = 0.10
# 三档淡紫：暗部藕紫、中间淡紫、亮部近白（刻意压低饱和，做成「薄薄一层」而非实心晶体）
PURPLE_DARK = (0xB4, 0x86, 0xDD)
PURPLE_MID = (0xD2, 0xB4, 0xF2)
PURPLE_LIGHT = (0xF2, 0xE9, 0xFF)
# 无恒物质贴图的明暗范围，用来把它的灰度拉满量程
CORE_LOW = 0.25
CORE_HIGH = 0.95

# 无恒物质在合金上的覆盖：中心稍厚、外圈更薄 —— 始终留得下底下的金属
CORE_INNER = 4      # |x-8| <= 3 与 |y-8| <= 3 视为中心
CORE_OUTER = 7      # |x-8| <= 6 视为外圈
CORE_INNER_WEIGHT = 0.40
CORE_OUTER_WEIGHT = 0.22

# 末地合金盾牌：直接沿用原版盾牌的 64×64 展开图，只把「盾正面」那一块换成末地石材质。
# 面在贴图里的位置由 ShieldModel 的 cube UV 决定（plate 是 12×22×1，texOffs(0,0)）：
#     UP   (1, 0) 12×1     DOWN (13, 0) 12×1
#     EAST (0, 1) 1×22     FRONT(1, 1) 12×22  ← 盾面，玩家看到的那一面
#     WEST (13, 1) 1×22    BACK (14, 1) 12×22 ← 盾背，保留原版木色
# handle 是 texOffs(26, 0) 的 2×6×6，同样原样保留。
SHIELD_FRONT = (1, 1, 12, 22)      # (x0, y0, 宽, 高)
# 末地石材质叠上去的强度：明度按原版盾面走，色相取末地石，再按原版明暗收放
SHIELD_STONE_MIX = 0.86
SHIELD_SHADE_RANGE = (0.62, 1.14)  # 原版盾面最暗 / 最亮处对应的明度系数

# 世界数据探测器的配色
CASE_DARK = (0x2E, 0x2A, 0x3E, 0xFF)
CASE = (0x46, 0x41, 0x5C, 0xFF)
CASE_LIT = (0x63, 0x5C, 0x7E, 0xFF)
SCREEN = (0x74, 0xE6, 0xEE, 0xFF)
SCREEN_DARK = (0x2F, 0x8F, 0xA8, 0xFF)
TRACE = (0xC9, 0x8B, 0xF5, 0xFF)
LENS = (0xE7, 0xD2, 0xFF, 0xFF)


def load(directory, name):
    return Image.open(directory / f"{name}.png").convert("RGBA")


def luminance(pixel):
    r, g, b = pixel[:3]
    return (0.299 * r + 0.587 * g + 0.114 * b) / 255.0


def lerp(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def opaque_bounds(img):
    """返回不透明像素的亮度范围，用来把模板的明暗拉开成满量程。"""
    values = [luminance(p) for p in img.getdata() if p[3] > 20]
    return (min(values), max(values)) if values else (0.0, 1.0)


def normalize(value, low, high):
    if high - low < 1e-6:
        return 1.0
    return (value - low) / (high - low)


def build_end_alloy():
    """淡黄色的下级合金 —— 以末地石材质调制。"""
    ingot = load(BASE_ROOT, "netherite_ingot")
    end_stone = load(BASE_ROOT, "end_stone")
    low, high = opaque_bounds(ingot)
    end_low, end_high = opaque_bounds(end_stone)

    out = Image.new("RGBA", ingot.size, (0, 0, 0, 0))
    for x in range(ingot.size[0]):
        for y in range(ingot.size[1]):
            pixel = ingot.getpixel((x, y))
            if pixel[3] <= 20:
                continue
            base = lerp(ALLOY_DARK, ALLOY_LIGHT, normalize(luminance(pixel), low, high))
            grain = normalize(luminance(end_stone.getpixel((x, y))), end_low, end_high)
            factor = 1.0 + END_STONE_STRENGTH * (grain * 2.0 - 1.0)
            color = tuple(min(255, max(0, round(c * factor))) for c in base)
            out.putpixel((x, y), color + (pixel[3],))
    return out


def covered_mask(img, threshold=20):
    """
    无恒物质贴图的有效覆盖范围：原像素不透明，或上下左右有一个不透明邻居。

    原贴图是个「带孔洞的噪声块」，直接叠上去会在合金上留下一个个棕色小洞；
    往外膨胀一格把洞补上，锭身里才是一整块紫色晶体。
    （锭外仍然用原样的 alpha，保留贴图本身的轮廓与孔洞，这样边缘才有泛光感。）
    """
    width, height = img.size
    mask = set()
    for y in range(height):
        for x in range(width):
            if img.getpixel((x, y))[3] <= threshold:
                continue
            for px, py in ((x, y), (x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
                mask.add((px, py))
    return mask


def core_shade_map(img):
    """把无恒物质每个不透明像素映射成紫色，得到 (x, y) -> 颜色 的表。"""
    return {
        (x, y): core_shade(img.getpixel((x, y)))
        for y in range(img.size[1])
        for x in range(img.size[0])
        if img.getpixel((x, y))[3] > 20
    }


def neighbor_shade(shades, x, y):
    """膨胀出来的补洞像素：取四周已有紫色像素的平均值，保住贴图原本的纹理走向。"""
    picked = [shades[p] for p in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)) if p in shades]
    if not picked:
        return PURPLE_MID
    return tuple(round(sum(c[i] for c in picked) / len(picked)) for i in range(3))


def build_ephemeral_alloy(end_alloy):
    """
    在末地合金上盖一层「几乎透明的淡紫」，再补一层极淡的紫色滤镜。

    关键点有两个：
    一是淡紫只做覆盖层 —— 权重压到 0.4 / 0.22，底下的合金金属色必须透出来，
    成品是「泛着紫光的合金」而不是「紫色晶体」；
    二是锭的轮廓只约束「合金画到哪里」，不约束无恒物质 —— 只要无恒物质贴图在
    这里有不透明像素，就照原样画出来，于是淡紫会溢出锭外，不再被剪掉。
    """
    ephemeral = load(ITEM_DIR, "ephemeral_material")
    coverage = covered_mask(ephemeral)
    shades = core_shade_map(ephemeral)

    out = Image.new("RGBA", end_alloy.size, (0, 0, 0, 0))
    for x in range(end_alloy.size[0]):
        for y in range(end_alloy.size[1]):
            alloy = end_alloy.getpixel((x, y))
            core = ephemeral.getpixel((x, y))
            in_alloy = alloy[3] > 20
            in_core = core[3] > 20
            if not in_alloy and not in_core:
                continue

            if in_alloy:
                color = alloy[:3]
                if (x, y) in coverage:
                    color = lerp(color, shades.get((x, y)) or neighbor_shade(shades, x, y),
                                 core_weight(x, y))
                color = lerp(color, PURPLE_FILTER, PURPLE_FILTER_ALPHA)
                alpha = alloy[3]
            else:
                # 锭外：无恒物质直接出框，全强度铺开
                color = shades[(x, y)]
                alpha = core[3]

            out.putpixel((x, y), color + (alpha,))
    return out


def core_shade(color):
    """
    无恒物质的这块像素经过「浓紫色滤镜」后的颜色。
    <p>
    做法是把它当成灰度再映射到三档紫色上（深紫 → 鲜紫 → 淡紫白）：原贴图里那些高饱和的
    杂色（青、品红、绿）只保留明暗关系，色相统一压到紫，叠上去才像一块紫色晶体而不是
    撒了一把彩纸；三档里中间一档刻意选高饱和紫，成品才不会灰掉。
    """
    level = normalize(luminance(color), CORE_LOW, CORE_HIGH)
    if level < 0.5:
        return lerp(PURPLE_DARK, PURPLE_MID, level * 2.0)
    return lerp(PURPLE_MID, PURPLE_LIGHT, (level - 0.5) * 2.0)


def core_weight(x, y):
    """离贴图中心越近，无恒物质的覆盖越强（锭外由调用方直接给满覆盖）。"""
    distance = max(abs(x - 8), abs(y - 8))
    if distance <= CORE_INNER - 1:
        return CORE_INNER_WEIGHT
    if distance <= CORE_OUTER:
        return CORE_OUTER_WEIGHT
    return 0.0


def build_end_alloy_shield():
    """
    末地合金盾牌：原版盾牌的展开图，盾正面换成末地石材质。

    其余部分（盾背、侧棱、上下沿、把手）一律保持原版配色 —— 这块盾要的是
    「换了盾面材质的原版盾牌」，不是重新设计的一面盾。
    盾面的做法是：末地石提供色相与颗粒，原版盾面提供明暗（边框、铆钉、中央凸起的
    那些亮暗块），两者相乘，于是材质换了而浮雕感还在。
    """
    base = load(BASE_ROOT, "shield_base_nopattern")
    end_stone = load(BASE_ROOT, "end_stone")
    img = base.copy().convert("RGBA")

    x0, y0, width, height = SHIELD_FRONT
    front = [luminance(img.getpixel((x, y)))
             for y in range(y0, y0 + height) for x in range(x0, x0 + width)]
    low, high = (min(front), max(front)) if front else (0.0, 1.0)

    for y in range(y0, y0 + height):
        for x in range(x0, x0 + width):
            shade = normalize(luminance(img.getpixel((x, y))), low, high)
            stone = end_stone.getpixel((x % 16, y % 16))[:3]
            factor = SHIELD_SHADE_RANGE[0] + (
                SHIELD_SHADE_RANGE[1] - SHIELD_SHADE_RANGE[0]) * shade
            color = tuple(min(255, max(0, round(c * factor))) for c in stone)
            # 往原版盾面靠一点，避免整块变成纯末地石的黄绿
            color = lerp(color, img.getpixel((x, y))[:3], 1.0 - SHIELD_STONE_MIX)
            img.putpixel((x, y), color + (255,))
    return img


def build_world_data_detector():
    """世界数据探测器：手持读数仪，青色屏幕上有一条紫色扫描线。"""
    size = 16
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    def p(x, y, color):
        if 0 <= x < size and 0 <= y < size:
            img.putpixel((x, y), color)

    def rect(x0, y0, x1, y1, color):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                p(x, y, color)

    # 探头（顶部右侧伸出的紫色天线）
    rect(11, 1, 12, 3, CASE)
    p(11, 1, CASE_LIT)
    p(12, 2, TRACE)

    # 机身
    rect(2, 4, 13, 13, CASE)
    rect(2, 4, 13, 4, CASE_LIT)          # 顶面高光
    rect(2, 13, 13, 13, CASE_DARK)       # 底面阴影
    for y in range(4, 14):
        p(2, y, CASE_LIT)
        p(13, y, CASE_DARK)

    # 屏幕
    rect(4, 6, 11, 10, SCREEN)
    p(4, 6, SCREEN_DARK)
    p(11, 6, SCREEN_DARK)
    for x in range(4, 12):
        p(x, 10, SCREEN_DARK)
    # 扫描线：屏幕中央一条紫色折线
    for x in range(5, 11):
        p(x, 8, TRACE)
    p(7, 7, TRACE)
    p(8, 9, TRACE)

    # 侧边读数灯
    rect(12, 6, 12, 7, TRACE)
    rect(12, 9, 12, 10, SCREEN)

    # 底部握把 + 按钮
    rect(5, 14, 10, 15, CASE_DARK)
    rect(6, 14, 9, 14, CASE)
    rect(4, 11, 6, 12, CASE_LIT)
    p(5, 11, TRACE)
    rect(8, 11, 10, 12, CASE)
    return img


def main():
    end_alloy = build_end_alloy()
    ephemeral_alloy = build_ephemeral_alloy(end_alloy)

    outputs = [
        (ITEM_DIR / "end_alloy.png", end_alloy),
        (ITEM_DIR / "ephemeral_alloy.png", ephemeral_alloy),
        (ENTITY_DIR / "end_alloy_shield.png", build_end_alloy_shield()),
        (ITEM_DIR / "world_data_detector.png", build_world_data_detector()),
    ]
    for path, img in outputs:
        path.parent.mkdir(parents=True, exist_ok=True)
        img.save(path)
        print(f"written {path}")


if __name__ == "__main__":
    main()
