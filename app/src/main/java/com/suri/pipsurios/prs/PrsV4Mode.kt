package com.suri.pipsurios.prs

/** Initial operating choices for the map-based P.R.S. v4.0 flow. */
enum class PrsV4Mode(
    val displayName: String,
    val operatingMode: PrsOperatingMode
) {
    ONLY_PIP_BOY(
        displayName = "ONLY PIP-BOY",
        operatingMode = PrsOperatingMode.LOCAL_SCAN
    ),
    PIP_BOY_PROBE(
        displayName = "PIP-BOY + PROBE",
        operatingMode = PrsOperatingMode.SCAN_PROBE
    )
}
