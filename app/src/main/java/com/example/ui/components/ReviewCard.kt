package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Review
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun ReviewCard(
  review: Review,
  onUpvoteHelpful: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("review_card_${review.id}"),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Reviewer Avatar Initials
          val initials = review.reviewerName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "U" }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(ForestGreenPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = initials,
              color = ForestGreenPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = review.reviewerName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = review.formattedDate,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Verified Purchase Badge
        if (review.isVerifiedPurchase) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFE8F5E9))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified Purchase",
              tint = Color(0xFF2E7D32),
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Verified Buyer",
              color = Color(0xFF2E7D32),
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Star rating
      StarRatingBar(rating = review.rating.toFloat(), starSize = 16.dp)

      if (review.title.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = review.title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = review.comment,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onUpvoteHelpful,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.ThumbUp,
            contentDescription = "Helpful",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
        Text(
          text = "Helpful (${review.helpfulVotes})",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
