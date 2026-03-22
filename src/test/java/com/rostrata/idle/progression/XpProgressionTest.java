package com.rostrata.idle.progression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XpProgressionTest {

    @Test
    void cumulativeXp_matchesComputedAnchors() {
        assertEquals(0, XpProgression.cumulativeXpToReachLevelRounded(1));
        assertEquals(350, XpProgression.cumulativeXpToReachLevelRounded(2));
        assertEquals(732, XpProgression.cumulativeXpToReachLevelRounded(3));
        assertEquals(4_555, XpProgression.cumulativeXpToReachLevelRounded(10));
        assertEquals(624_555, XpProgression.cumulativeXpToReachLevelRounded(60));
        assertEquals(1_464_608, XpProgression.cumulativeXpToReachLevelRounded(70));
        assertEquals(6_385_838, XpProgression.cumulativeXpToReachLevelRounded(90));
        assertEquals(11_666_884, XpProgression.cumulativeXpToReachLevelRounded(100));
    }

    @Test
    void multiplierAfterLeavingLevel_rampsAtExpectedLevels() {
        double eps = 1e-12;
        assertEquals(1.09, XpProgression.multiplierAfterLeavingLevel(59), eps);
        assertEquals(1.09, XpProgression.multiplierAfterLeavingLevel(60), eps);
        assertEquals(1.075, XpProgression.multiplierAfterLeavingLevel(69), eps);
        assertEquals(1.075, XpProgression.multiplierAfterLeavingLevel(70), eps);
        assertEquals(1.050, XpProgression.multiplierAfterLeavingLevel(89), eps);
        assertEquals(1.050, XpProgression.multiplierAfterLeavingLevel(90), eps);
        assertEquals(1.050, XpProgression.multiplierAfterLeavingLevel(99), eps);
    }

    @Test
    void xpToNextLevel_bandMatchesDelta() {
        for (int level = 1; level < XpProgression.MAX_LEVEL; level++) {
            long expected =
                    XpProgression.cumulativeXpToReachLevelRounded(level + 1)
                            - XpProgression.cumulativeXpToReachLevelRounded(level);
            assertEquals(expected, XpProgression.xpToNextLevelRounded(level));
        }
        assertEquals(0, XpProgression.xpToNextLevelRounded(XpProgression.MAX_LEVEL));
    }
}
