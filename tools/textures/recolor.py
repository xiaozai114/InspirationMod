"""贴图换色的公共工具：保留形状与高光位置，只把颜色换掉。

思路（与格雷科技那一类「同形异色」贴图一致）：
拿一张现成贴图当模板，把它用到的**不透明**颜色按亮度从暗到亮排成一列，
再把目标配色的两端（暗、亮）按同样的档数插值成一条渐变，最后一一对应。
于是模板里亮的地方仍然亮、暗的地方仍然暗，变的只有色相。

锭 / 粒 / 尘三个生成脚本都用它，形状模板各自不同。
"""

from PIL import Image


def luminance(color):
    r, g, b = color[:3]
    return 0.299 * r + 0.587 * g + 0.114 * b


def mix(color, target, t):
    return tuple(round(color[i] + (target[i] - color[i]) * t) for i in range(3))


def remap(template: Image.Image, dark, light) -> Image.Image:
    """把 template 重映射到 dark→light 这条渐变上，透明像素原样保留。"""
    image = template.convert("RGBA")
    pixels = list(image.getdata())

    # 透明像素不参与配色，也就不占渐变档位
    shades = sorted({p for p in pixels if p[3] > 0}, key=luminance)
    ramp = [
        tuple(
            round(dark[i] + (light[i] - dark[i]) * index / max(1, len(shades) - 1))
            for i in range(3)
        ) + (255,)
        for index in range(len(shades))
    ]
    mapping = {shade: ramp[index] for index, shade in enumerate(shades)}

    out = Image.new("RGBA", image.size)
    out.putdata([mapping.get(p, p) for p in pixels])
    return out
