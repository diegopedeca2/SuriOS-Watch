package com.suri.pipsurios.geiger

import android.view.KeyEvent

class VolumeKeyController {
    private var active = false
    private var listener: ((Boolean) -> Unit)? = null

    fun activate(onPressedChanged: (Boolean) -> Unit) {
        active = true
        listener = onPressedChanged
    }

    fun deactivate() {
        listener?.invoke(false)
        listener = null
        active = false
    }

    fun handle(event: KeyEvent): Boolean {
        if (!active || event.keyCode != KeyEvent.KEYCODE_VOLUME_UP) return false

        when (event.action) {
            KeyEvent.ACTION_DOWN -> listener?.invoke(true)
            KeyEvent.ACTION_UP -> listener?.invoke(false)
        }
        return true
    }
}
