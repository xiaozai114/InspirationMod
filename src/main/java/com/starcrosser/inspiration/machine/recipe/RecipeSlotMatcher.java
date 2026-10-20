package com.starcrosser.inspiration.machine.recipe;

import org.jetbrains.annotations.Nullable;

/**
 * 顺序无关的槽位指派：把配方声明的若干条「需求」分配到机器的若干个槽位上。
 * <p>
 * 融合、装配的物品匹配与装配的流体匹配原先各写了一份回溯求解，逻辑相同、
 * 只有「某个槽能不能满足某条需求」这一句判定不同。这里把回溯本身抽出来，
 * 判定交给调用方以 {@link Accepts} 传入。
 * <p>
 * 求解顺序与原先保持一致（需求从第 0 条开始、槽位从第 0 个开始试），
 * 因此同一个输入得到的指派结果与重构前完全相同 —— 扣料扣的是哪几个槽不会变。
 */
public final class RecipeSlotMatcher {

    private RecipeSlotMatcher() {
    }

    /** 第 {@code requirementIndex} 条需求能否由第 {@code candidateIndex} 个槽位满足。 */
    @FunctionalInterface
    public interface Accepts {
        boolean test(int requirementIndex, int candidateIndex);
    }

    /**
     * 为每条需求找一个互不冲突的槽位。
     *
     * @return 长度等于 {@code requirementCount} 的数组，第 i 项是第 i 条需求分配到的槽位下标；
     *         凑不齐时返回 {@code null}
     */
    @Nullable
    public static int[] assign(int requirementCount, int candidateCount, Accepts accepts) {
        int[] assignment = new int[requirementCount];
        return search(requirementCount, candidateCount, accepts,
                new boolean[candidateCount], assignment, 0) ? assignment : null;
    }

    private static boolean search(int requirementCount, int candidateCount, Accepts accepts,
                                  boolean[] used, int[] assignment, int index) {
        if (index >= requirementCount) {
            return true;
        }
        for (int candidate = 0; candidate < candidateCount; candidate++) {
            if (used[candidate] || !accepts.test(index, candidate)) {
                continue;
            }
            used[candidate] = true;
            assignment[index] = candidate;
            if (search(requirementCount, candidateCount, accepts, used, assignment, index + 1)) {
                return true;
            }
            used[candidate] = false;
        }
        return false;
    }
}
