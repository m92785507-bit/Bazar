package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun OrganicProductArt(
  iconType: String,
  modifier: Modifier = Modifier,
  badgeText: String? = "100% PURE & ORGANIC"
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFFCFBF7),
            Color(0xFFF3EFE4)
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h / 2f

      // Background ambient warm aura
      drawCircle(
        color = Color(0x33FBBF24),
        radius = size.minDimension * 0.44f,
        center = Offset(cx, cy)
      )

      when (iconType) {
        "nuts" -> {
          // Shahi Mixed Nuts Jar with Honey
          val jarPath = Path().apply {
            moveTo(cx - w * 0.22f, cy - h * 0.16f)
            lineTo(cx + w * 0.22f, cy - h * 0.16f)
            lineTo(cx + w * 0.25f, cy + h * 0.26f)
            lineTo(cx - w * 0.25f, cy + h * 0.26f)
            close()
          }
          drawPath(
            path = jarPath,
            brush = Brush.verticalGradient(
              listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFB45309))
            )
          )
          // Jar Wooden Lid
          drawRoundRect(
            color = Color(0xFF78350F),
            topLeft = Offset(cx - w * 0.24f, cy - h * 0.24f),
            size = Size(w * 0.48f, h * 0.09f),
            cornerRadius = CornerRadius(6f)
          )
          // Almonds & Cashews visible in honey
          drawOval(
            brush = Brush.radialGradient(listOf(Color(0xFFFFE0B2), Color(0xFFD7CCC8))),
            topLeft = Offset(cx - w * 0.16f, cy - h * 0.04f),
            size = Size(w * 0.14f, h * 0.20f)
          )
          drawOval(
            brush = Brush.radialGradient(listOf(Color(0xFFFFF8E1), Color(0xFFFFD54F))),
            topLeft = Offset(cx + w * 0.04f, cy + h * 0.02f),
            size = Size(w * 0.16f, h * 0.16f)
          )
          // Pistachio green dots
          drawCircle(color = Color(0xFF689F38), radius = w * 0.035f, center = Offset(cx - w * 0.02f, cy + h * 0.14f))
          drawCircle(color = Color(0xFF558B2F), radius = w * 0.03f, center = Offset(cx + w * 0.12f, cy - h * 0.06f))
          // Golden reflection highlight
          drawArc(
            color = Color(0x66FFFFFF),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.20f, cy - h * 0.12f),
            size = Size(w * 0.38f, h * 0.32f),
            style = Stroke(width = 5f)
          )
        }

        "ghee" -> {
          // Desi Gawa Ghee Clay Pot / Glass Jar
          val potPath = Path().apply {
            moveTo(cx - w * 0.22f, cy - h * 0.14f)
            cubicTo(cx - w * 0.32f, cy + h * 0.06f, cx - w * 0.26f, cy + h * 0.26f, cx, cy + h * 0.26f)
            cubicTo(cx + w * 0.26f, cy + h * 0.26f, cx + w * 0.32f, cy + h * 0.06f, cx + w * 0.22f, cy - h * 0.14f)
            close()
          }
          drawPath(
            path = potPath,
            brush = Brush.radialGradient(
              listOf(Color(0xFFFFF9C4), Color(0xFFFFEE58), Color(0xFFFBC02D), Color(0xFFF57F17)),
              center = Offset(cx, cy),
              radius = w * 0.32f
            )
          )
          // Ghee Pot Neck & Lid
          drawRoundRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(cx - w * 0.18f, cy - h * 0.22f),
            size = Size(w * 0.36f, h * 0.09f),
            cornerRadius = CornerRadius(6f)
          )
          // Danedar golden butter drops
          drawCircle(color = Color(0xFFFFF176), radius = w * 0.04f, center = Offset(cx - w * 0.06f, cy + h * 0.02f))
          drawCircle(color = Color(0xFFFFD54F), radius = w * 0.05f, center = Offset(cx + w * 0.08f, cy + h * 0.08f))
          drawCircle(color = Color(0xFFFFEE58), radius = w * 0.035f, center = Offset(cx, cy + h * 0.16f))
        }

        "honey" -> {
          // Pure Honey Jar with Wooden Dipper
          val jar = Path().apply {
            moveTo(cx - w * 0.20f, cy - h * 0.12f)
            lineTo(cx + w * 0.20f, cy - h * 0.12f)
            lineTo(cx + w * 0.24f, cy + h * 0.26f)
            lineTo(cx - w * 0.24f, cy + h * 0.26f)
            close()
          }
          drawPath(
            jar,
            brush = Brush.verticalGradient(
              listOf(Color(0xFFFFB300), Color(0xFFFB8C00), Color(0xFFE65100))
            )
          )
          // Jar Lid
          drawRoundRect(
            color = Color(0xFFBCAAA4),
            topLeft = Offset(cx - w * 0.22f, cy - h * 0.20f),
            size = Size(w * 0.44f, h * 0.09f),
            cornerRadius = CornerRadius(6f)
          )
          // Honey comb motif
          drawCircle(color = Color(0x55FFFFFF), radius = w * 0.07f, center = Offset(cx, cy + h * 0.06f))
        }

        "combo" -> {
          // 3-in-1 Royal Trio Combo: Honey, Ghee & Shahi Nuts
          // Left Ghee pot
          drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFF59D), Color(0xFFFBC02D))),
            radius = w * 0.15f,
            center = Offset(cx - w * 0.16f, cy + h * 0.05f)
          )
          // Right Honey jar
          drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFB74D), Color(0xFFE65100))),
            topLeft = Offset(cx + w * 0.04f, cy - h * 0.08f),
            size = Size(w * 0.24f, h * 0.32f),
            cornerRadius = CornerRadius(10f)
          )
          // Center Shahi Nut Jar
          drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFD97706))),
            topLeft = Offset(cx - w * 0.12f, cy - h * 0.16f),
            size = Size(w * 0.24f, h * 0.36f),
            cornerRadius = CornerRadius(10f)
          )
          // Royal Golden Ribbon
          val ribbon = Path().apply {
            moveTo(cx - w * 0.26f, cy + h * 0.10f)
            lineTo(cx + w * 0.26f, cy + h * 0.10f)
            lineTo(cx + w * 0.24f, cy + h * 0.15f)
            lineTo(cx - w * 0.24f, cy + h * 0.15f)
            close()
          }
          drawPath(ribbon, color = Color(0xFFB71C1C))
        }

        "apple" -> {
          // Fresh Organic Honeycrisp Apple
          val applePath = Path().apply {
            moveTo(cx, cy - h * 0.18f)
            cubicTo(cx + w * 0.32f, cy - h * 0.28f, cx + w * 0.38f, cy + h * 0.22f, cx + w * 0.08f, cy + h * 0.26f)
            cubicTo(cx, cy + h * 0.28f, cx, cy + h * 0.28f, cx - w * 0.08f, cy + h * 0.26f)
            cubicTo(cx - w * 0.38f, cy + h * 0.22f, cx - w * 0.32f, cy - h * 0.28f, cx, cy - h * 0.18f)
          }
          drawPath(
            path = applePath,
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFE53935), Color(0xFFC62828), Color(0xFFB71C1C)),
              center = Offset(cx - w * 0.1f, cy - h * 0.05f),
              radius = w * 0.45f
            )
          )
          // Stem & Leaf
          drawArc(
            color = Color(0xFF5D4037),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.05f, cy - h * 0.32f),
            size = Size(w * 0.15f, h * 0.18f),
            style = Stroke(width = 6f)
          )
          val leafPath = Path().apply {
            moveTo(cx + w * 0.03f, cy - h * 0.22f)
            cubicTo(cx + w * 0.22f, cy - h * 0.32f, cx + w * 0.22f, cy - h * 0.16f, cx + w * 0.05f, cy - h * 0.18f)
          }
          drawPath(leafPath, color = Color(0xFF43A047))
        }

        "avocado" -> {
          // Avocado Skin & Flesh
          val avocadoPath = Path().apply {
            moveTo(cx, cy - h * 0.28f)
            cubicTo(cx + w * 0.24f, cy - h * 0.24f, cx + w * 0.35f, cy + h * 0.25f, cx, cy + h * 0.32f)
            cubicTo(cx - w * 0.35f, cy + h * 0.25f, cx - w * 0.24f, cy - h * 0.24f, cx, cy - h * 0.28f)
          }
          drawPath(avocadoPath, color = Color(0xFF2E4028))
          val fleshPath = Path().apply {
            moveTo(cx, cy - h * 0.24f)
            cubicTo(cx + w * 0.20f, cy - h * 0.20f, cx + w * 0.29f, cy + h * 0.21f, cx, cy + h * 0.27f)
            cubicTo(cx - w * 0.29f, cy + h * 0.21f, cx - w * 0.20f, cy - h * 0.20f, cx, cy - h * 0.24f)
          }
          drawPath(
            fleshPath,
            brush = Brush.radialGradient(
              listOf(Color(0xFFE8F5B5), Color(0xFFC5E1A5), Color(0xFF9CCC65)),
              center = Offset(cx, cy + h * 0.05f),
              radius = w * 0.32f
            )
          )
          drawCircle(
            brush = Brush.radialGradient(
              listOf(Color(0xFF8D6E63), Color(0xFF4E342E)),
              center = Offset(cx - w * 0.03f, cy + h * 0.06f),
              radius = w * 0.14f
            ),
            radius = w * 0.13f,
            center = Offset(cx, cy + h * 0.08f)
          )
        }

        else -> {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFF81C784), Color(0xFF388E3C)),
              center = Offset(cx, cy),
              radius = w * 0.35f
            ),
            radius = w * 0.28f,
            center = Offset(cx, cy)
          )
        }
      }
    }

    // Top left Organic Badge Pill
    if (badgeText != null) {
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xEEFFFFFF))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      ) {
        androidx.compose.foundation.layout.Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = ForestGreenPrimary,
            modifier = Modifier.size(12.dp)
          )
          androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(3.dp))
          Text(
            text = badgeText,
            color = ForestGreenPrimary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
