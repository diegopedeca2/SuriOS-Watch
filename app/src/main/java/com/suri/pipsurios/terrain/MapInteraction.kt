package com.suri.pipsurios.terrain

sealed interface MapSelection {
    data object None : MapSelection
    data class RespawnSelected(val id: String) : MapSelection
    data class DeleteRespawnConfirm(val id: String) : MapSelection
    data class ZoneSelected(val id: String) : MapSelection
    data class ClearZoneConfirm(val id: String) : MapSelection
    data object EmptyOffered : MapSelection
    data object EmptyConfirm : MapSelection
}

class MapDestructiveActions(initial: MapOverlays) {
    var overlays = initial; private set
    var selection: MapSelection = MapSelection.None; private set

    fun selectRespawn(id: String) { selection = MapSelection.RespawnSelected(id) }
    fun requestDelete() { (selection as? MapSelection.RespawnSelected)?.let { selection = MapSelection.DeleteRespawnConfirm(it.id) } }
    fun selectZone(id: String) { selection = MapSelection.ZoneSelected(id) }
    fun requestClear() { (selection as? MapSelection.ZoneSelected)?.let { selection = MapSelection.ClearZoneConfirm(it.id) } }
    fun offerEmpty() { selection = MapSelection.EmptyOffered }
    fun requestEmpty() { if (selection == MapSelection.EmptyOffered) selection = MapSelection.EmptyConfirm }
    fun cancel() { selection = MapSelection.None }
    fun confirm() {
        overlays = when (val current = selection) {
            is MapSelection.DeleteRespawnConfirm -> overlays.copy(respawns = overlays.respawns.filterNot { it.id == current.id })
            is MapSelection.ClearZoneConfirm -> overlays.copy(radZones = overlays.radZones.filterNot { it.id == current.id })
            MapSelection.EmptyConfirm -> MapOverlays()
            else -> overlays
        }
        selection = MapSelection.None
    }
}
