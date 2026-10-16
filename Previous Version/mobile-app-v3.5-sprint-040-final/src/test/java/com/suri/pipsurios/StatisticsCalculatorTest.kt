package com.suri.pipsurios

import com.suri.pipsurios.data.StatisticsCalculator
import com.suri.pipsurios.ui.screens.SecondaryWeaponCatalog
import com.suri.pipsurios.ui.screens.HeadgearCatalog
import com.suri.pipsurios.ui.screens.UniformCatalog
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StatisticsCalculatorTest {
    private val weapons = listOf("L96", "LevAR-15", "MCX", "APC-9K", "MGL", "VOLCANO")

    @Test
    fun zeroLogsProducesNoValidDataAndZeroPercentages() {
        val result = StatisticsCalculator.percentageDistribution(weapons, emptyList())

        assertEquals(0, result.validRecordCount)
        assertTrue(result.entries.all { it.percentage.compareTo(BigDecimal.ZERO) == 0 })
    }

    @Test
    fun oneLogProducesOneHundredPercentForItsWeaponAndKeepsZeroOptions() {
        val result = StatisticsCalculator.percentageDistribution(weapons, listOf("MCX"))

        assertEquals(1, result.validRecordCount)
        assertEquals("100%", formatted(result, "MCX"))
        assertEquals("0%", formatted(result, "L96"))
        assertEquals(weapons, result.entries.map { it.option })
    }

    @Test
    fun repeatedWeaponProducesOneHundredPercent() {
        val result = StatisticsCalculator.percentageDistribution(
            weapons,
            listOf("APC-9K", "APC-9K", "APC-9K")
        )

        assertEquals("100%", formatted(result, "APC-9K"))
        assertEquals("0%", formatted(result, "VOLCANO"))
    }

    @Test
    fun multipleWeaponsUseAtMostTwoDecimalsAndSpanishSeparator() {
        val result = StatisticsCalculator.percentageDistribution(
            weapons,
            listOf("L96", "MCX", "MCX")
        )

        assertEquals("33,33%", formatted(result, "L96"))
        assertEquals("66,67%", formatted(result, "MCX"))
        assertEquals("0%", formatted(result, "LevAR-15"))
    }

    @Test
    fun exactPercentagesDoNotShowDecimals() {
        val result = StatisticsCalculator.percentageDistribution(
            weapons,
            listOf("L96", "LevAR-15", "MCX", "APC-9K")
        )

        assertTrue(result.entries.take(4).all { StatisticsCalculator.formatPercentage(it.percentage) == "25%" })
    }

    @Test
    fun invalidAndMissingWeaponsAreIgnored() {
        val result = StatisticsCalculator.percentageDistribution(
            weapons,
            listOf(null, "", "UNKNOWN", "MCX")
        )

        assertEquals(1, result.validRecordCount)
        assertEquals("100%", formatted(result, "MCX"))
    }

    @Test
    fun roundedPercentagesSumCloseToOneHundred() {
        val result = StatisticsCalculator.percentageDistribution(
            weapons,
            listOf("L96", "MCX", "VOLCANO")
        )
        val sum = result.entries.fold(BigDecimal.ZERO) { total, entry -> total + entry.percentage }

        assertTrue(sum.subtract(BigDecimal(100)).abs() <= BigDecimal("0.01"))
    }

    @Test
    fun secondaryWeaponCatalogUsesCurrentGearOrderAndSharedCalculation() {
        val secondaryWeapons = SecondaryWeaponCatalog.weapons.map { it.displayName }
        val result = StatisticsCalculator.percentageDistribution(
            secondaryWeapons,
            listOf("AAP-01C", "AAP-01C", "DESERT EAGLE", "AAP-01C", null, "UNKNOWN")
        )

        assertEquals(listOf("DESERT EAGLE", "AAP-01C", "MGL", "VOLCANO"), secondaryWeapons)
        assertEquals(4, result.validRecordCount)
        assertEquals("25%", formatted(result, "DESERT EAGLE"))
        assertEquals("75%", formatted(result, "AAP-01C"))
        assertEquals("0%", formatted(result, "MGL"))
        assertEquals("0%", formatted(result, "VOLCANO"))
    }

    @Test
    fun oneNormalizedLocationProducesOneHundredPercent() {
        val result = StatisticsCalculator.locationDistribution(listOf("VALLE ARENA"))

        assertEquals(1, result.validRecordCount)
        assertEquals(listOf("VALLE ARENA"), result.entries.map { it.option })
        assertEquals("100%", formatted(result, "VALLE ARENA"))
    }

    @Test
    fun locationsAreTrimmedCaseNormalizedDynamicAndAlphabeticallySorted() {
        val result = StatisticsCalculator.locationDistribution(
            listOf(" Valle Arena ", "valle arena", "NAVY", "fortuna", "NAVY")
        )

        assertEquals(5, result.validRecordCount)
        assertEquals(listOf("FORTUNA", "NAVY", "VALLE ARENA"), result.entries.map { it.option })
        assertEquals("20%", formatted(result, "FORTUNA"))
        assertEquals("40%", formatted(result, "NAVY"))
        assertEquals("40%", formatted(result, "VALLE ARENA"))
    }

    @Test
    fun emptyAndMissingLocationsAreIgnoredAndCanProduceNoData() {
        val noData = StatisticsCalculator.locationDistribution(listOf(null, "", "   "))
        val withData = StatisticsCalculator.locationDistribution(listOf("", " NAVY ", null))

        assertEquals(0, noData.validRecordCount)
        assertTrue(noData.entries.isEmpty())
        assertEquals(1, withData.validRecordCount)
        assertEquals("100%", formatted(withData, "NAVY"))
    }

    @Test
    fun headgearUsesProfileOrderAndIgnoresHistoricalItemValues() {
        val result = StatisticsCalculator.percentageDistribution(
            HeadgearCatalog.profiles,
            listOf("SURI-14", "SURI-14", "BROTHERHOOD", null, "", "VYPER", "UNKNOWN")
        )

        assertEquals(listOf("SURI-14", "BROTHERHOOD"), result.entries.map { it.option })
        assertEquals(3, result.validRecordCount)
        assertEquals("66,67%", formatted(result, "SURI-14"))
        assertEquals("33,33%", formatted(result, "BROTHERHOOD"))
    }

    @Test
    fun headgearNoDataAndOneHundredPercentCasesAreDeterministic() {
        val noData = StatisticsCalculator.percentageDistribution(
            HeadgearCatalog.profiles,
            listOf(null, "", "VYPER")
        )
        val oneOption = StatisticsCalculator.percentageDistribution(
            HeadgearCatalog.profiles,
            listOf("BROTHERHOOD", "BROTHERHOOD")
        )

        assertEquals(0, noData.validRecordCount)
        assertTrue(noData.entries.all { it.percentage.compareTo(BigDecimal.ZERO) == 0 })
        assertEquals("100%", formatted(oneOption, "BROTHERHOOD"))
    }

    @Test
    fun suri14CanProduceOneHundredPercent() {
        val result = StatisticsCalculator.percentageDistribution(
            HeadgearCatalog.profiles,
            listOf("SURI-14")
        )

        assertEquals("100%", formatted(result, "SURI-14"))
        assertEquals("0%", formatted(result, "BROTHERHOOD"))
    }

    @Test
    fun uniformUsesFixedCatalogIgnoresMissingValuesAndKeepsZeroOptions() {
        val result = StatisticsCalculator.percentageDistribution(
            UniformCatalog.options,
            listOf("MCBCK - SUMMER", "DESERT", "DESERT", null, "", "UNKNOWN")
        )

        assertEquals(listOf("MCBCK - SUMMER", "MCBCK - LONG", "DESERT"), result.entries.map { it.option })
        assertEquals(3, result.validRecordCount)
        assertEquals("33,33%", formatted(result, "MCBCK - SUMMER"))
        assertEquals("0%", formatted(result, "MCBCK - LONG"))
        assertEquals("66,67%", formatted(result, "DESERT"))
    }

    @Test
    fun uniformWithoutValidHistoricalValuesProducesNoData() {
        val result = StatisticsCalculator.percentageDistribution(
            UniformCatalog.options,
            listOf(null, "", "LEGACY")
        )

        assertEquals(0, result.validRecordCount)
        assertTrue(result.entries.all { it.percentage.compareTo(BigDecimal.ZERO) == 0 })
    }

    private fun formatted(
        result: com.suri.pipsurios.data.PercentageDistribution<String>,
        weapon: String
    ): String = StatisticsCalculator.formatPercentage(
        result.entries.single { it.option == weapon }.percentage
    )
}
