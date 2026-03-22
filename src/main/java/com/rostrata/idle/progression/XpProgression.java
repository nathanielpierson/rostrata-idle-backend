package com.rostrata.idle.progression;

/**
 * Skill XP curve for Rostrata Idle (levels 1–100).
 * <p>
 * Canonical definition for the game (keep client woodcutting UI in sync).
 * <ul>
 *   <li><strong>First band (level 1 → 2):</strong> {@value #BASE_XP_LEVEL_1_TO_2} XP.</li>
 *   <li>Each later band scales the previous band by a per-level multiplier {@code mult(L)},
 *       where {@code L} is the level you are leaving (the band you just finished).</li>
 *   <li>{@code mult(L) = 1.09} for {@code L = 1 … 59}.</li>
 *   <li>{@code mult(L)} ramps linearly from 1.09 down to 1.075 for {@code L = 60 … 69}.</li>
 *   <li>{@code mult(L)} ramps linearly from 1.075 down to 1.050 for {@code L = 70 … 89}.</li>
 *   <li>{@code mult(L) = 1.050} for {@code L ≥ 90} (through level 99 → 100).</li>
 * </ul>
 * After each band, the next band size is {@code round(previousBand * mult(L))} (integer XP).
 */
public final class XpProgression {

    public static final int BASE_XP_LEVEL_1_TO_2 = 350;
    public static final double MULT_FLAT_HIGH = 1.09;
    public static final double MULT_AFTER_BRACKET_1 = 1.075;
    public static final double MULT_AFTER_BRACKET_2 = 1.050;
    public static final int MAX_LEVEL = 100;

    private static final long[] CUMULATIVE_XP_TO_ENTER_LEVEL = computeCumulativeRounded();

    private XpProgression() {
    }

    /**
     * Multiplier applied after completing the band from {@code leavingLevel} → {@code leavingLevel + 1}
     * to scale the size of the next band.
     */
    static double multiplierAfterLeavingLevel(int leavingLevel) {
        if (leavingLevel < 60) {
            return MULT_FLAT_HIGH;
        }
        if (leavingLevel <= 69) {
            return MULT_FLAT_HIGH
                    + (MULT_AFTER_BRACKET_1 - MULT_FLAT_HIGH) * (leavingLevel - 60) / 9.0;
        }
        if (leavingLevel <= 89) {
            return MULT_AFTER_BRACKET_1
                    + (MULT_AFTER_BRACKET_2 - MULT_AFTER_BRACKET_1) * (leavingLevel - 70) / 19.0;
        }
        return MULT_AFTER_BRACKET_2;
    }

    private static long[] computeCumulativeRounded() {
        long[] cumulative = new long[MAX_LEVEL + 1];
        cumulative[1] = 0;
        long bandXp = BASE_XP_LEVEL_1_TO_2;
        for (int level = 2; level <= MAX_LEVEL; level++) {
            cumulative[level] = cumulative[level - 1] + bandXp;
            if (level < MAX_LEVEL) {
                bandXp = Math.round(bandXp * multiplierAfterLeavingLevel(level - 1));
            }
        }
        return cumulative;
    }

    /**
     * Total XP required to <strong>enter</strong> {@code level} (same as “reach” that level threshold).
     * Level 1 → 0. Uses integer-rounded band sizes.
     */
    public static long cumulativeXpToReachLevelRounded(int level) {
        if (level <= 1) {
            return 0;
        }
        int capped = Math.min(level, MAX_LEVEL);
        return CUMULATIVE_XP_TO_ENTER_LEVEL[capped];
    }

    /**
     * @deprecated Prefer {@link #cumulativeXpToReachLevelRounded(int)}; this returns the same value as a double.
     */
    @Deprecated
    public static double cumulativeXpToReachLevel(int level) {
        return cumulativeXpToReachLevelRounded(level);
    }

    /**
     * XP needed to go from {@code level} to {@code level + 1}. At {@code MAX_LEVEL}, returns 0.
     */
    public static long xpToNextLevelRounded(int level) {
        if (level < 1 || level >= MAX_LEVEL) {
            return 0;
        }
        return cumulativeXpToReachLevelRounded(level + 1) - cumulativeXpToReachLevelRounded(level);
    }
}
