package com.suri.pipsurios.geiger

import android.view.KeyEvent

class VolumeKeyController {
    private var active = false
    private var volumeUpListener: ((Boolean) -> Unit)? = null
    private var volumeDownListener: (() -> Unit)? = null

    fun activate(
        onPressedChanged: (Boolean) -> Unit,
        onVolumeDown: () -> Unit = {}
    ) {
        active = true
        volumeUpListener = onPressedChanged
        volumeDownListener = onVolumeDown
    }

    fun deactivate() {
        volumeUpListener?.invoke(false)
        volumeUpListener = null
        volumeDownListener = null
        active = false
    }

    fun handle(event: KeyEvent): Boolean {
        if (!active || event.keyCode !in setOf(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN)) return false

        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) volumeDownListener?.invoke()
            return true
        }
        when (event.action) {
            KeyEvent.ACTION_DOWN -> volumeUpListener?.invoke(true)
            KeyEvent.ACTION_UP -> volumeUpListener?.invoke(false)
        }
        return true
    }
}
