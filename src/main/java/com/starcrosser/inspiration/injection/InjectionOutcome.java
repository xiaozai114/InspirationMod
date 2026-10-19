package com.starcrosser.inspiration.injection;

/**
 * 一次注入的能量判定结果。
 * <p>
 * 注入枪放电量落在配方的 [最小 FE, 最大 FE] 窗口内才算成功：
 * 低于下限配方不会发生、材料照损；高于上限则过载爆炸。
 */
public enum InjectionOutcome {

    /** 能量落在窗口内，注入成功。 */
    SUCCESS,

    /** 能量低于下限：配方不生效，材料已损失。 */
    INSUFFICIENT,

    /** 能量高于上限：过载，在物质束缚器处爆炸。 */
    OVERLOAD;

    public static InjectionOutcome of(int energy, int minEnergy, int maxEnergy) {
        if (energy < minEnergy) {
            return INSUFFICIENT;
        }
        if (energy > maxEnergy) {
            return OVERLOAD;
        }
        return SUCCESS;
    }
}
