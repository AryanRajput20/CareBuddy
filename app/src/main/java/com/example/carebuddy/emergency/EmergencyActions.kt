package com.example.carebuddy.emergency

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

fun callEmergencyNumber(context: Context, number: String = "112") {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
    context.startActivity(intent)
}

fun sendSmsToAllWithBody(
    context: Context,
    contacts: List<EmergencyContact>,
    body: String
) {
    if (contacts.isEmpty()) {
        Toast.makeText(context, "No contacts added", Toast.LENGTH_SHORT).show()
        return
    }

    val numbers = contacts.joinToString(";") { it.phone }
    val uri = "smsto:$numbers".toUri()

    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra("sms_body", body)
    }
    context.startActivity(intent)
}

fun sendSmsToAll(context: Context, contacts: List<EmergencyContact>) {
    val defaultBody = "🚨 SOS! I need immediate help. Please contact me ASAP!"
    sendSmsToAllWithBody(context, contacts, defaultBody)
}

fun openLocationInMaps(
    context: Context,
    lat: Double? = null,
    lng: Double? = null
) {
    val uri = if (lat != null && lng != null) {
        "geo:$lat,$lng?q=$lat,$lng(My+Location)".toUri()
    } else {
        "geo:0,0?q=My+Location".toUri()
    }

    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show()
    }
}

fun shareLocationText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share location via"))
}




