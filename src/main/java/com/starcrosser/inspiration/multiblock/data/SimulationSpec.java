package com.starcrosser.inspiration.multiblock.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 一份「结构数据」所描述的模拟参数。
 * <p>
 * 三项分别回答：跑多久、每刻烧多少电、以及跑完之后去哪张战利品表里摸奖。
 * 战利品表给的是**列表**而不是单个：沉船这类结构在游戏里有好几种箱子
 * （补给箱 / 宝藏箱 / 地图箱），每次模拟随机挑其中一种来 roll。
 *
 * @param duration     一次模拟需要的刻数
 * @param energyPerTick 每刻消耗的 FE（灵感充能另算，固定 8 mB/刻）
 * @param lootTables   候选战利品表，产出时随机取一张
 */
public record SimulationSpec(int duration, int energyPerTick, List<ResourceLocation> lootTables) {
}
