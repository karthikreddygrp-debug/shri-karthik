package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.StoreConstants
import com.example.util.QrCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches store branding`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Shri Karthik", appName)
    }

    @Test
    fun `qr code generator builds valid square matrix for store links`() {
        val mapsQr = QrCodeGenerator.generate(StoreConstants.GOOGLE_MAPS_VIEW_URL)
        assertTrue(mapsQr.size >= 21)
        assertEquals(mapsQr.size * mapsQr.size, mapsQr.modules.size)
        // Top-left finder corner must be dark
        assertTrue(mapsQr.isDark(0, 0))
        assertTrue(mapsQr.isDark(6, 6))
    }
}
