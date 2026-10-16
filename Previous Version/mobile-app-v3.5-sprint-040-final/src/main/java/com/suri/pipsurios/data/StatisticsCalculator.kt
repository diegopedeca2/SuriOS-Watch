package com.suri.pipsurios.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

data class PercentageDistributionEntry<T>(
    val option: T,
    val percentage: BigDecimal
)

data class PercentageDistribution<T>(
    val entries: List<PercentageDistributionEntry<T>>,
    val validRecordCount: Int
)

object StatisticsCalculator {
    fun <T> percentageDistribution(
        options: List<T>,
        recordedValues: Iterable<T?>
    ): PercentageDistribution<T> {
        val validOptions = options.toSet()
        val counts = mutableMapOf<T, Int>()
        var validRecordCount = 0

        recordedValues.forEach { value ->
            if (value != null && value in validOptions) {
                counts[value] = counts.getOrDefault(value, 0) + 1
                validRecordCount++
            }
        }

        val entries = options.map { option ->
            val percentage = if (validRecordCount == 0) {
                BigDecimal.ZERO
            } else {
                BigDecimal(counts.getOrDefault(option, 0))
                    .multiply(BigDecimal(100))
                    .divide(BigDecimal(validRecordCount), 2, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
            }
            PercentageDistributionEntry(option, percentage)
        }

        return PercentageDistribution(entries, validRecordCount)
    }

    fun formatPercentage(percentage: BigDecimal): String =
        "${percentage.stripTrailingZeros().toPlainString().replace('.', ',')}%"

    fun locationDistribution(locations: Iterable<String?>): PercentageDistribution<String> {
        val normalizedLocations = locations.mapNotNull { location ->
            location?.trim()?.takeIf(String::isNotEmpty)?.uppercase(Locale.ROOT)
        }
        val options = normalizedLocations.distinct().sorted()
        return percentageDistribution(options, normalizedLocations)
    }
}
