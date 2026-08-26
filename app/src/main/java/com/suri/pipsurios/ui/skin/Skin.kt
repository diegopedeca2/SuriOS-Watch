package com.suri.pipsurios.ui.skin

enum class SkinId(val displayName: String, val implemented: Boolean) {
    BROTHERHOOD_OF_STEEL("BROTHERHOOD OF STEEL", true),
    SALAMANDER("SALAMANDER", false),
    IRON_HAND("IRON HAND", false),
    ADEPTUS_MECHANICUS("ADEPTUS MECHANICUS", false),
    NECRON("NECRON", false),
    MANDALORIAN("MANDALORIAN", false)
}

object SkinCatalog {
    val all: List<SkinId> = SkinId.entries
}
