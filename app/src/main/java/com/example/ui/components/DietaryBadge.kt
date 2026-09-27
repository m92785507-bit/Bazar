package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BadgeGlutenFreeBg
import com.example.ui.theme.BadgeGlutenFreeText
import com.example.ui.theme.BadgeOrganicBg
import com.example.ui.theme.BadgeOrganicText
import com.example.ui.theme.BadgeRawBg
import com.example.ui.theme.BadgeRawText
import com.example.ui.theme.BadgeVeganBg
import com.example.ui.theme.BadgeVeganText

@Composable
fun DietaryBadge(
  tag: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (tag.lowercase()) {
    "vegan" -> BadgeVeganBg to BadgeVeganText
    "gluten-free" -> BadgeGlutenFreeBg to BadgeGlutenFreeText
    "100% raw", "raw" -> BadgeRawBg to BadgeRawText
    "dairy-free" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
    "usda organic" -> BadgeOrganicBg to BadgeOrganicText
    else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Text(
      text = tag,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      maxLines = 1
    )
  }
}
