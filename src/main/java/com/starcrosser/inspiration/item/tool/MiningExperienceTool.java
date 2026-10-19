package com.starcrosser.inspiration.item.tool;

/**
 * 挖掉方块即产出经验的工具。
 * <p>
 * 与 {@link ExperienceTool} 分开，是因为剑、斧、锄的经验来自「使用动作」，
 * 只有镐类工具的产出挂在挖掘事件上；将来若出现别的挖掘类工具，实现本接口即可自动接入。
 */
public interface MiningExperienceTool extends ExperienceTool {
}
