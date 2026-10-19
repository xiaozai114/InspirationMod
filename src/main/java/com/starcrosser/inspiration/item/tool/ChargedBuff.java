package com.starcrosser.inspiration.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 「长按右键蓄力、消耗经验换增益」的共同实现。
 * <p>
 * 五件注能翡翠钢工具的行为完全一致，只是效果不同，所以流程集中在这里，
 * 各工具类只把 {@code use} / {@code onUseTick} / {@code finishUsingItem} 转调过来。
 * <p>
 * 经验消耗用的是「总经验点数」（{@link Player#totalExperience}），不是等级：
 * 一次发动 {@value #EXPERIENCE_COST} 点，经验不足时连蓄力都不让开始。
 */
public final class ChargedBuff {

    /** 蓄力所需刻数（1.5 秒）。 */
    public static final int CHARGE_TICKS = 30;

    /** 一次发动消耗的经验点数。 */
    public static final int EXPERIENCE_COST = 20;

    /** 增益持续刻数（30 秒）。 */
    public static final int EFFECT_DURATION = 600;

    /** 增益等级：I 级。 */
    public static final int EFFECT_AMPLIFIER = 0;

    /** 蓄力不足就松手 / 经验不够时的提示。 */
    public static final String TOOLTIP_KEY = "tooltip.inspiration.energized_jade_steel.charged_buff";

    /** 开始蓄力；经验不够时直接失败，省得玩家白按。 */
    public static InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand,
                                                         ChargedBuffTool tool) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.totalExperience < tool.experienceCost()) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /** 蓄力过程中每 5 刻冒一圈附魔粒子，作为「正在灌灵感」的进度反馈。 */
    public static void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration,
                                 ChargedBuffTool tool) {
        if (!level.isClientSide) {
            return;
        }
        int elapsed = tool.chargeTicks() - remainingUseDuration;
        if (elapsed > 0 && elapsed % 5 == 0) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    entity.getX() + (level.random.nextDouble() - 0.5) * 1.2,
                    entity.getY() + 0.8 + level.random.nextDouble() * 0.6,
                    entity.getZ() + (level.random.nextDouble() - 0.5) * 1.2,
                    0.0, 0.0, 0.0);
        }
    }

    /** 蓄满并松手：扣经验、上增益。中途松手不会走到这里。 */
    public static ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity,
                                            ChargedBuffTool tool) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return stack;
        }
        if (player.totalExperience < tool.experienceCost()) {
            return stack;
        }
        player.giveExperiencePoints(-tool.experienceCost());
        player.addEffect(new MobEffectInstance(tool.chargedEffect(), tool.effectDuration(),
                EFFECT_AMPLIFIER, false, true, true));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);
        return stack;
    }

    /** 工具提示：写清长按右键给什么效果、要多少经验、持续多久。 */
    public static void appendHoverText(List<Component> tooltip, TooltipFlag flag, ChargedBuffTool tool) {
        tooltip.add(Component.translatable(TOOLTIP_KEY,
                        Component.translatable(tool.chargedEffect().value().getDescriptionId()),
                        tool.experienceCost(),
                        tool.effectDuration() / 20)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private ChargedBuff() {
    }
}
