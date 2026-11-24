package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import com.example.model.StoreConstants

object StoreIntents {

    fun dialStore(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${StoreConstants.PHONE_DIAL}")
        }
        launchSafely(context, intent, "Unable to open phone dialer")
    }

    fun openWhatsAppEnquiry(
        context: Context,
        message: String = StoreConstants.DEFAULT_WHATSAPP_MESSAGE
    ) {
        val encoded = Uri.encode(message)
        val url = "https://wa.me/${StoreConstants.WHATSAPP_NUMBER}?text=$encoded"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        launchSafely(context, intent, "Unable to open WhatsApp")
    }

    fun openProductEnquiry(context: Context, productName: String, brand: String) {
        val msg = "Hello ${StoreConstants.BUSINESS_NAME}, I would like to enquire about $productName ($brand) — availability and price."
        openWhatsAppEnquiry(context, msg)
    }

    fun openDirections(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(StoreConstants.GOOGLE_MAPS_DIRECTIONS_URL))
        launchSafely(context, intent, "Unable to open Google Maps")
    }

    fun openGoogleMapsLocation(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(StoreConstants.GOOGLE_MAPS_VIEW_URL))
        launchSafely(context, intent, "Unable to open Google Maps")
    }

    fun openNearbyPlaces(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(StoreConstants.GOOGLE_MAPS_NEARBY_URL))
        launchSafely(context, intent, "Unable to open Nearby Places in Maps")
    }

    fun shareStore(context: Context) {
        val shareBody = buildString {
            appendLine(StoreConstants.BUSINESS_NAME)
            appendLine(StoreConstants.TAGLINE)
            appendLine()
            appendLine("Address: ${StoreConstants.ADDRESS}")
            appendLine("Plus Code: ${StoreConstants.PLUS_CODE}")
            appendLine("Phone: ${StoreConstants.PHONE_DISPLAY}")
            appendLine("WhatsApp: https://wa.me/${StoreConstants.WHATSAPP_NUMBER}")
            appendLine("Directions: ${StoreConstants.GOOGLE_MAPS_DIRECTIONS_URL}")
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${StoreConstants.BUSINESS_NAME} — ${StoreConstants.TAGLINE}")
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        val chooser = Intent.createChooser(sendIntent, "Share ${StoreConstants.BUSINESS_NAME}")
        launchSafely(context, chooser, "Unable to share store details")
    }

    fun saveStoreContact(context: Context) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, StoreConstants.BUSINESS_NAME)
            putExtra(ContactsContract.Intents.Insert.PHONE, StoreConstants.PHONE_DISPLAY)
            putExtra(ContactsContract.Intents.Insert.POSTAL, StoreConstants.ADDRESS)
            putExtra(
                ContactsContract.Intents.Insert.NOTES,
                "${StoreConstants.TAGLINE} | Plus Code: ${StoreConstants.PLUS_CODE}"
            )
        }
        launchSafely(context, intent, "Unable to open Contacts app")
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
    }

    private fun launchSafely(context: Context, intent: Intent, fallbackMessage: String) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, fallbackMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
