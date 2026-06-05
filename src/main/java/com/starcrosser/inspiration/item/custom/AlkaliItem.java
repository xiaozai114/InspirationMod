package com.starcrosser.inspiration.item.custom;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AlkaliItem extends Item {
    private final Item target;
    private final float explosion_range;
    public AlkaliItem(Item.Properties properties,Item target,float explosion_range) {
        super(properties);
        this.target = target;
        this.explosion_range=explosion_range;

    }
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        // 1. 检查物品实体是否在水中
        if (entity.isInWater()) {
            Level level = entity.level();
            // 如果逻辑发生在客户端，则跳过执行，避免逻辑重复
            if (level.isClientSide) {
                return false;
            }

            // 2. 获取当前实体在世界中的位置
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            entity.level().explode(
                    entity,  // 爆炸源实体
                    entity.getX(), entity.getY()+0.5, entity.getZ(),
                    explosion_range,  // 使用自定义的爆炸威力
                    Level.ExplosionInteraction.TNT  // 模拟 TNT 的交互行为
            );
            // 3. 销毁当前的物品实体
            entity.discard();

            // 4. 生成目标物品的实体，数量保持原样
            ItemEntity newEntity = new ItemEntity(level, x, y, z, new ItemStack(target, stack.getCount()));
            level.addFreshEntity(newEntity);


            // 5. 事件已处理，不再进行默认更新
            return true;
        }
        // 事件未处理，继续默认更新逻辑
        return false;
    }

}
