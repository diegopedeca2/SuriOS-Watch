package com.suri.pipsurios

import com.suri.pipsurios.remoteprobe.RemoteProbeAssessment
import com.suri.pipsurios.remoteprobe.RemoteProbeComparator
import com.suri.pipsurios.remoteprobe.RemoteProbeNode
import com.suri.pipsurios.remoteprobe.RemoteProbeObservation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteProbeTest {
    @Test
    fun experimentalAssessmentUsesRssiDifferenceOnly() {
        assertEquals(
            RemoteProbeAssessment.NEAR_PROBE,
            RemoteProbeComparator.classify(operatorRssi = -82, probeRssi = -54)
        )
        assertEquals(
            RemoteProbeAssessment.NEAR_OPERATOR,
            RemoteProbeComparator.classify(operatorRssi = -54, probeRssi = -82)
        )
        assertEquals(
            RemoteProbeAssessment.BETWEEN,
            RemoteProbeComparator.classify(operatorRssi = -58, probeRssi = -61)
        )
        assertEquals(
            RemoteProbeAssessment.UNCERTAIN,
            RemoteProbeComparator.classify(operatorRssi = -58, probeRssi = -67)
        )
    }

    @Test
    fun comparisonRequiresSameObservedIdentifierAndNearbyTimestamps() {
        val observations = listOf(
            RemoteProbeObservation(RemoteProbeNode.OPERATOR, 9_000L, "AA:BB", -82),
            RemoteProbeObservation(RemoteProbeNode.PROBE, 9_200L, "AA:BB", -54),
            RemoteProbeObservation(RemoteProbeNode.OPERATOR, 9_000L, "ONLY-OPERATOR", -45),
            RemoteProbeObservation(RemoteProbeNode.PROBE, 1_000L, "OLD", -40),
            RemoteProbeObservation(RemoteProbeNode.OPERATOR, 9_000L, "OLD", -80)
        )

        val comparisons = RemoteProbeComparator.compare(observations, nowEpochMillis = 10_000L)

        assertEquals(1, comparisons.size)
        assertEquals("AA:BB", comparisons.single().deviceIdentifier)
        assertEquals(RemoteProbeAssessment.NEAR_PROBE, comparisons.single().assessment)
        assertTrue(comparisons.single().timestampDeltaMillis <= 8_000L)
    }
}
