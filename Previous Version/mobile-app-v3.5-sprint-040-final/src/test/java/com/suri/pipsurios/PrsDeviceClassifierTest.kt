package com.suri.pipsurios

import android.bluetooth.BluetoothClass
import com.suri.pipsurios.prs.PrsDeviceCategory
import com.suri.pipsurios.prs.PrsDeviceClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrsDeviceClassifierTest {
    @Test
    fun nameHintsClassifyCommonDevices() {
        assertEquals(PrsDeviceCategory.PHONE, PrsDeviceClassifier.classify("Galaxy Z Flip6", null, null))
        assertEquals(PrsDeviceCategory.WATCH, PrsDeviceClassifier.classify("Galaxy Watch", null, null))
        assertEquals(PrsDeviceCategory.TV, PrsDeviceClassifier.classify("Samsung TV", null, null))
        assertEquals(PrsDeviceCategory.AUDIO, PrsDeviceClassifier.classify("Galaxy Buds", null, null))
        assertEquals(PrsDeviceCategory.COMPUTER, PrsDeviceClassifier.classify("ThinkPad", null, null))
    }

    @Test
    fun bluetoothClassAndAppearanceProvideFallbacks() {
        assertEquals(
            PrsDeviceCategory.PHONE,
            PrsDeviceClassifier.classify(null, null, BluetoothClass.Device.Major.PHONE)
        )
        assertEquals(
            PrsDeviceCategory.TV,
            PrsDeviceClassifier.classify(null, null, BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX)
        )
        assertEquals(PrsDeviceCategory.WATCH, PrsDeviceClassifier.classify(null, "0319C000", null))
        assertEquals(PrsDeviceCategory.PHONE, PrsDeviceClassifier.classify(null, "03194000", null))
        assertNull(PrsDeviceClassifier.classify("UNKNOWN BEACON", "0102FF", null))
    }
}
