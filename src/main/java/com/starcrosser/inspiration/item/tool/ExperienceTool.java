package com.starcrosser.inspiration.item.tool;

/**
 * 「完成动作即产出经验」的工具。
 * <p>
 * 灵感系列与其翡翠钢升级版共用同一批工具类，经验量由构造参数决定，
 * 因此统一从这个接口取值，事件处理器不必再判断具体是哪个物品。
 */
public interface ExperienceTool {

    /**
     * 一次动作额外产出的经验点数。
     * <p>
     * 动作随工具而定：剑是命中敌人，镐是挖掉方块，斧是剥皮，锄是锄地。
     */
    int experiencePerAction();
}
