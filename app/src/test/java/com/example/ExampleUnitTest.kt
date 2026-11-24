package com.example

import com.example.model.StoreConstants
import com.example.util.QrCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun qrCodeGeneratorBuildsValidMatrixForStoreLinks() {
        val mapsQr = QrCodeGenerator.generate(StoreConstants.GOOGLE_MAPS_VIEW_URL)
        assertTrue(mapsQr.size >= 21)
        assertEquals(mapsQr.size * mapsQr.size, mapsQr.modules.size)
        assertTrue(mapsQr.isDark(0, 0))
        assertTrue(mapsQr.isDark(6, 6))

        val waQr = QrCodeGenerator.generate("https://wa.me/${StoreConstants.WHATSAPP_NUMBER}")
        assertTrue(waQr.size >= 21)
        assertEquals(waQr.size * waQr.size, waQr.modules.size)
    }

    @Test
    fun storeConstantsMatchVerifiedDetails() {
        assertEquals("SHRI KARTHIK ENTERPRISE", StoreConstants.BUSINESS_NAME)
        assertEquals("+91 84479 59727", StoreConstants.PHONE_DISPLAY)
        assertTrue(StoreConstants.GOOGLE_MAPS_DIRECTIONS_URL.contains("517508"))
    }
}
