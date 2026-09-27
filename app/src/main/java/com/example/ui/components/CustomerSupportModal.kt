package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.WhatsAppHelper

@Composable
fun CustomerSupportModal(
  onDismiss: () -> Unit,
  orderNumber: String? = null
) {
  val context = LocalContext.current
  val whatsappColor = Color(0xFF25D366)
  val whatsappDark = Color(0xFF128C7E)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("customer_support_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(whatsappColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.HeadsetMic,
                contentDescription = null,
                tint = whatsappDark,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Customer Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Online via WhatsApp 24/7",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_support_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // WhatsApp Main Action
        Button(
          onClick = {
            val msg = if (orderNumber != null) {
              "Hello PureHarvest Support, I have an inquiry about my order #$orderNumber."
            } else {
              "Hello PureHarvest Support, I would like to inquire about organic items and order placement."
            }
            WhatsAppHelper.openWhatsAppChat(context, msg)
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("whatsapp_direct_chat_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = whatsappColor),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Chat on WhatsApp Now",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phone call direct option
        OutlinedButton(
          onClick = {
            WhatsAppHelper.callSupportHotline(context)
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("call_hotline_btn"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Call Hotline: ${WhatsAppHelper.DISPLAY_PHONE_NUMBER}", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Quick Inquiry Topics (হোয়াটসঅ্যাপ বার্তা):",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Topic chips / rows
        val topics = listOf(
          Triple(Icons.Default.LocalShipping, "Order Tracking & Delivery", "Check dispatch status for my organic harvest"),
          Triple(Icons.Default.Verified, "Purity & Lab Test Guarantee", "Inquire about honey/ghee purity & certifications"),
          Triple(Icons.Default.QuestionAnswer, "Custom Combo & Bulk Gift Order", "Special bundle discount for family or event")
        )

        topics.forEach { (icon, title, desc) ->
          SupportTopicItem(
            icon = icon,
            title = title,
            desc = desc,
            onClick = {
              val msg = "Hello PureHarvest Support, I have a question regarding: $title. Details: $desc."
              WhatsAppHelper.openWhatsAppChat(context, msg)
              onDismiss()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun SupportTopicItem(
  icon: ImageVector,
  title: String,
  desc: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(vertical = 6.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
      Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
