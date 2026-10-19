package com.starcrosser.inspiration.injection;

import net.minecraft.core.BlockPos;

/**
 * 注入结构的空间约束。
 * <p>
 * 注入枪必须落在物质束缚器周围 5x5x5 的方框内（切比雪夫距离不超过 2）。
 * 绑定与触发时都会校验一次，避免绑定之后把枪搬到远处仍然生效。
 */
public final class InjectionStructure {

    /** 以物质束缚器为中心、单边 5 格，因此允许的偏移是 -2~+2。 */
    public static final int RANGE = 2;

    /** 单边长度。 */
    public static final int SIDE_LENGTH = RANGE * 2 + 1;

    /** 「5×5×5」这样的尺寸表述，用于提示文案。 */
    public static String sideLabel() {
        return SIDE_LENGTH + "×" + SIDE_LENGTH + "×" + SIDE_LENGTH;
    }

    public static boolean isInRange(BlockPos gun, BlockPos binder) {
        if (gun.equals(binder)) {
            return false;
        }
        return Math.abs(gun.getX() - binder.getX()) <= RANGE
                && Math.abs(gun.getY() - binder.getY()) <= RANGE
                && Math.abs(gun.getZ() - binder.getZ()) <= RANGE;
    }

    private InjectionStructure() {
    }
}
