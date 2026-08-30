package com.suri.pipsurios.prs

/**
 * Pluggable density model. Future movement/orientation fusion can implement
 * this boundary without changing BLE acquisition or the contact tracker.
 */
interface PrsDensityEstimator {
    fun estimate(
        proximity: PrsProximityBand,
        signalConfidence: Float,
        movement: PrsMovementContext? = null
    ): DensityCloud
}

class DefaultPrsDensityEstimator : PrsDensityEstimator {
    override fun estimate(
        proximity: PrsProximityBand,
        signalConfidence: Float,
        movement: PrsMovementContext?
    ): DensityCloud {
        val (radius, spread) = when (proximity) {
            PrsProximityBand.NEAR -> 0.27f to 0.15f
            PrsProximityBand.MEDIUM -> 0.50f to 0.20f
            PrsProximityBand.FAR -> 0.75f to 0.17f
            PrsProximityBand.UNKNOWN -> 0.66f to 0.28f
        }
        return DensityCloud(
            radialCenterFraction = radius,
            radialSpreadFraction = spread,
            confidence = signalConfidence.coerceIn(0.12f, 0.72f),
            azimuthCoverage = 1f
        )
    }
}
