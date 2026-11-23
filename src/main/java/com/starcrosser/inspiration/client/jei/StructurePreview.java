package com.starcrosser.inspiration.client.jei;

/**
 * JEI 里「结构模拟机的结构」这一页的配方对象：其实只带一个层下标。
 * <p>
 * 每种多方块逐层看最清楚，所以每一层注册成一条配方，
 * JEI 自带的翻页就是「翻层」。{@code layer} 的 0 是最后面那一层。
 */
public record StructurePreview(com.starcrosser.inspiration.multiblock.MultiblockKind kind, int layer) {
}
