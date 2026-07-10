package com.kaushalprajapati.ecoscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaushalprajapati.ecoscan.data.modal.EcoGrade

@Composable
fun EcoScoreBadge(
    grade: EcoGrade,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color(grade.colorHex), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = grade.label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value / 2.2).sp
        )
    }
}

@Composable
fun EcoScoreLabel(score: Int) {
    Text(
        text = "$score / 100",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}
