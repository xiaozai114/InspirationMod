package com.starcrosser.inspiration.voidenergy.entity;

import com.starcrosser.inspiration.voidenergy.VoidClusterStage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 虚空能量团簇：占据一格、无碰撞的漂浮能量体。
 * <p>
 * 能量上限按 int 收口（{@link #MAX_ENERGY}），阶段效果全部在服务端结算：
 * 攀升自减、震荡随机抖动、危险阶段有概率自爆。能量归零并持续
 * {@link #EMPTY_LIFETIME} 刻之后自行消散 —— 提取器一旦重新充能就会被拉回来。
 */
public class VoidClusterEntity extends Entity {

    public static final int MAX_ENERGY = 1_000_000_000;
    public static final int EMPTY_LIFETIME = 200;

    private static final EntityDataAccessor<Integer> DATA_ENERGY =
            SynchedEntityData.defineId(VoidClusterEntity.class, EntityDataSerializers.INT);

    /** 能量为 0 的持续刻数，用于判消散。 */
    private int emptyTicks;

    public VoidClusterEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ENERGY, 0);
    }

    public int energy() {
        return entityData.get(DATA_ENERGY);
    }

    public void setEnergy(int value) {
        entityData.set(DATA_ENERGY, Math.max(0, Math.min(MAX_ENERGY, value)));
    }

    public void addEnergy(int delta) {
        setEnergy(energy() + delta);
    }

    public VoidClusterStage stage() {
        return VoidClusterStage.of(energy());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnAmbientParticles();
            return;
        }
        if (energy() <= 0) {
            emptyTicks++;
            if (emptyTicks >= EMPTY_LIFETIME) {
                discard();
            }
            return;
        }
        emptyTicks = 0;

        VoidClusterStage stage = stage();
        setEnergy(energy() + stage.tickDelta(getRandom()));
        if (stage.rollsExplosion(getRandom())) {
            explode();
        }
    }

    private void spawnAmbientParticles() {
        if (random.nextInt(6) != 0) {
            return;
        }
        Vec3 center = position().add(0, 0.5, 0);
        level().addParticle(ParticleTypes.END_ROD,
                center.x + (random.nextDouble() - 0.5) * 0.8,
                center.y + (random.nextDouble() - 0.5) * 0.8,
                center.z + (random.nextDouble() - 0.5) * 0.8,
                0, 0.01, 0);
    }

    private void explode() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.explode(this, getX(), getY(0.5), getZ(), 6.0F, Level.ExplosionInteraction.BLOCK);
        discard();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            VoidClusterStage stage = stage();
            serverPlayer.displayClientMessage(Component.translatable("message.inspiration.void_cluster",
                    energy(), Component.translatable("void_energy.stage." + stage.key())), false);
            level().playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.6F, 1.4F);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setEnergy(tag.getInt("Energy"));
        emptyTicks = tag.getInt("EmptyTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Energy", energy());
        tag.putInt("EmptyTicks", emptyTicks);
    }
}
