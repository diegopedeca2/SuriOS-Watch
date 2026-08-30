package com.suri.pipsurios.ui.skin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.suri.pipsurios.R

enum class SkinId(val displayName: String, val implemented: Boolean) {
    BROTHERHOOD_OF_STEEL("BROTHERHOOD OF STEEL", true),
    SALAMANDER("SALAMANDER", false),
    IRON_HAND("IRON HAND", false),
    ADEPTUS_MECHANICUS("ADEPTUS MECHANICUS", false),
    NECRON("NECRON", true),
    MANDALORIAN("MANDALORIAN", false)
}

object SkinCatalog {
    val all: List<SkinId> = SkinId.entries
}

/**
 * Process-scoped skin selection. It intentionally is not persisted: the
 * private application returns to Brotherhood of Steel after a process restart.
 */
object SkinSession {
    var activeSkin: SkinId by mutableStateOf(SkinId.BROTHERHOOD_OF_STEEL)

    val emblemResource: Int
        get() = when (activeSkin) {
            SkinId.NECRON -> R.drawable.necron_emblem
            else -> R.drawable.brotherhood_emblem_pipgreen
        }
}
