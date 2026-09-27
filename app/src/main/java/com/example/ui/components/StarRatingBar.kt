package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberStar
import com.example.ui.theme.AmberStarEmpty

@Composable
fun StarRatingBar(
  rating: Float,
  modifier: Modifier = Modifier,
  starSize: Dp = 18.dp,
  maxStars: Int = 5,
  starColor: Color = AmberStar,
  emptyColor: Color = AmberStarEmpty
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    for (i in 1..maxStars) {
      val isFull = rating >= i
      val isHalf = !isFull && (rating >= i - 0.75f)

      when {
        isFull -> {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = starColor,
            modifier = Modifier.size(starSize)
          )
        }
        isHalf -> {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.StarHalf,
            contentDescription = null,
            tint = starColor,
            modifier = Modifier.size(starSize)
          )
        }
        else -> {
          Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = emptyColor,
            modifier = Modifier.size(starSize)
          )
        }
      }
    }
  }
}

@Composable
fun InteractiveStarRatingBar(
  selectedRating: Int,
  onRatingChanged: (Int) -> Unit,
  modifier: Modifier = Modifier,
  starSize: Dp = 38.dp,
  maxStars: Int = 5
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    for (i in 1..maxStars) {
      val isSelected = i <= selectedRating
      val tint by animateColorAsState(
        targetValue = if (isSelected) AmberStar else AmberStarEmpty,
        label = "star_color_$i"
      )

      Box(
        modifier = Modifier
          .size(starSize)
          .testTag("star_rating_$i")
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            onRatingChanged(i)
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
          contentDescription = "$i stars",
          tint = tint,
          modifier = Modifier.size(starSize)
        )
      }
    }
  }
}
