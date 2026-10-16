package com.suri.pipsurios.prs

/** Initial operating choices for the map-based P.R.S. v4.0 flow. */
enum class PrsV4Mode(
    val displayName: String,
    val operatingMode: PrsOperatingMode
) {
    ONLY_PIP_BOY(
        displayName = "PIP",
        operatingMode = PrsOperatingMode.LOCAL_SCAN
    ),
    PIP_BOY_PROBE(
        displayName = "PIP + PROBE",
        operatingMode = PrsOperatingMode.SCAN_PROBE
    )
}
