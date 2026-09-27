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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun WriteReviewDialog(
  productName: String,
  isPurchasedByUser: Boolean,
  onDismiss: () -> Unit,
  onSubmit: (name: String, rating: Int, title: String, comment: String) -> Unit
) {
  var rating by remember { mutableIntStateOf(5) }
  var name by remember { mutableStateOf("Elena Rostova") }
  var title by remember { mutableStateOf("") }
  var comment by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val ratingDescription = when (rating) {
    1 -> "1 / 5 - Needs improvement"
    2 -> "2 / 5 - Below expectation"
    3 -> "3 / 5 - Good organic quality"
    4 -> "4 / 5 - Very fresh & flavorful"
    5 -> "5 / 5 - Exceptional farm fresh!"
    else -> ""
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("write_review_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Write a Review",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = productName,
              style = MaterialTheme.typography.bodyMedium,
              color = ForestGreenPrimary,
              fontWeight = FontWeight.Medium
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_review_dialog_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Verified Purchaser Banner
        if (isPurchasedByUser) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE8F5E9))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color(0xFF2E7D32),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Verified Purchase Review",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF2E7D32)
            )
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Star Rating Selection
        Text(
          text = "Your Rating",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        InteractiveStarRatingBar(
          selectedRating = rating,
          onRatingChanged = { rating = it }
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = ratingDescription,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reviewer Name Field
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Your Name / Display Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("review_name_input"),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Review Headline
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Review Headline (optional)") },
          placeholder = { Text("e.g. Crisp, sweet, and perfectly fresh!") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("review_title_input"),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Review Comment
        OutlinedTextField(
          value = comment,
          onValueChange = {
            comment = it
            if (it.isNotBlank()) errorMessage = null
          },
          label = { Text("Written Review *") },
          placeholder = { Text("Share details about the flavor, freshness, texture, origin, or how you enjoyed this organic item...") },
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("review_comment_input"),
          shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("cancel_review_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Cancel")
          }

          Button(
            onClick = {
              if (comment.trim().length < 5) {
                errorMessage = "Please write at least a few words about your experience."
              } else {
                onSubmit(name, rating, title, comment)
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("submit_review_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = ForestGreenPrimary
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Submit")
          }
        }
      }
    }
  }
}
