package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {
  const val SUPPORT_PHONE_NUMBER = "8801700000000"
  const val DISPLAY_PHONE_NUMBER = "+880 1700-000000"

  fun openWhatsAppChat(
    context: Context,
    message: String = "Hello PureHarvest Support, I have an inquiry about my organic food order."
  ) {
    val encodedMessage = Uri.encode(message)
    // Primary: universal wa.me link
    val uri = Uri.parse("https://wa.me/$SUPPORT_PHONE_NUMBER?text=$encodedMessage")
    val intent = Intent(Intent.ACTION_VIEW, uri)

    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      // Secondary fallback: api.whatsapp.com
      try {
        val fallbackIntent = Intent(
          Intent.ACTION_VIEW,
          Uri.parse("https://api.whatsapp.com/send?phone=$SUPPORT_PHONE_NUMBER&text=$encodedMessage")
        )
        context.startActivity(fallbackIntent)
      } catch (e2: Exception) {
        Toast.makeText(
          context,
          "WhatsApp not installed. Contact hotline: $DISPLAY_PHONE_NUMBER",
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  fun callSupportHotline(context: Context) {
    try {
      val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SUPPORT_PHONE_NUMBER"))
      context.startActivity(callIntent)
    } catch (e: Exception) {
      Toast.makeText(context, "Hotline: $DISPLAY_PHONE_NUMBER", Toast.LENGTH_SHORT).show()
    }
  }
}
