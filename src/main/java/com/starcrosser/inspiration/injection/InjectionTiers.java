package com.starcrosser.inspiration.injection;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 注入等级体系。
 * <p>
 * 物质束缚器正下方的方块决定这套结构能执行的配方等级上限，判定方式是该方块属于哪一个
 * {@code inspiration:injection_tier_<n>} 方块标签。玄武岩合金块属于 1 级标签，
 * 将来加入更高阶的底座时，只需把它写进对应编号的标签，代码不用改。
 * <p>
 * 现有底座：1 级 = 玄武岩合金块，2 级 = 幽匿合金块。
 */
public final class InjectionTiers {

    /** 预留的最高等级。目前只有 1 级有内容，其余标签留空即可。 */
    public static final int MAX_TIER = 8;

    /** 下标即等级，0 号位空缺不用（等级从 1 开始）。 */
    private static final TagKey<Block>[] TAGS = buildTags();

    @SuppressWarnings("unchecked")
    private static TagKey<Block>[] buildTags() {
        TagKey<Block>[] tags = new TagKey[MAX_TIER + 1];
        for (int tier = 1; tier <= MAX_TIER; tier++) {
            tags[tier] = BlockTags.create(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "injection_tier_" + tier));
        }
        return tags;
    }

    /** 第 {@code tier} 级底座的方块标签。 */
    public static TagKey<Block> tag(int tier) {
        if (tier < 1 || tier > MAX_TIER) {
            throw new IllegalArgumentException("注入等级越界：" + tier + "，允许范围 1~" + MAX_TIER);
        }
        return TAGS[tier];
    }

    /** 方块所属的最高注入等级；不属于任何等级标签时返回 0。 */
    public static int tierOf(BlockState state) {
        for (int tier = MAX_TIER; tier >= 1; tier--) {
            if (state.is(TAGS[tier])) {
                return tier;
            }
        }
        return 0;
    }

    /** 读取某个位置正下方方块的注入等级。 */
    public static int tierBelow(BlockGetter level, BlockPos above) {
        if (level == null) {
            return 0;
        }
        return tierOf(level.getBlockState(above.below()));
    }

    private InjectionTiers() {
    }
}
