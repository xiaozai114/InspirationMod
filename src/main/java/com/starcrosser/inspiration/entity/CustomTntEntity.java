package com.starcrosser.inspiration.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 强化 TNT 实体的共同基类。
 * <p>
 * 存在的唯一理由：原版 {@code PrimedTnt(Level, x, y, z, owner)} 这个构造函数内部
 * **硬编码了 {@code EntityType.TNT}**。用它构造出来的实体虽然 Java 类型是我们的子类，
 * 实体类型却是原版 TNT —— 存档重载、跨维度后会还原成原版 {@code PrimedTnt}，
 * 我们的 {@code explode()} 覆盖随之失效（爆炸威力从 8/16 退回 4），
 * 客户端也永远用不到我们注册的渲染器。
 * <p>
 * 所以这里照抄原版的初始化流程，只把实体类型换成传入的那一个。
 * 代价是原版 {@code owner} 字段是私有的、子类写不了，因此自己存一份并覆盖
 * {@link #getOwner()}：父类除该 getter 之外不使用这个字段。
 */
public abstract class CustomTntEntity extends PrimedTnt {

    /** 原版 TNT 的引信时长。 */
    private static final int DEFAULT_FUSE = 80;

    @Nullable
    private LivingEntity owner;

    /** 供 {@code EntityType.Builder} 的实体工厂使用；位置与引信由后续读取存档/数据包时设定。 */
    protected CustomTntEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }

    protected CustomTntEntity(EntityType<? extends PrimedTnt> type, Level level,
                              double x, double y, double z, @Nullable LivingEntity owner) {
        super(type, level);
        this.setPos(x, y, z);
        // 与原版一致：随机水平初速度 + 一点上抛，引信 80 tick
        double angle = level.random.nextDouble() * (float) (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(angle) * 0.02, 0.2F, -Math.cos(angle) * 0.02);
        this.setFuse(DEFAULT_FUSE);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    /**
     * 跨维度时会走这里。父类的实现写的是它自己的私有字段，读不出来，
     * 所以从源实体公开的 {@link #getOwner()} 再抄一份。
     */
    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof PrimedTnt tnt) {
            this.owner = tnt.getOwner();
        }
    }
}
