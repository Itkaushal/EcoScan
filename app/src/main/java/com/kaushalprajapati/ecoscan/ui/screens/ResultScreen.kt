package com.kaushalprajapati.ecoscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaushalprajapati.ecoscan.data.modal.EcoScoreResult
import com.kaushalprajapati.ecoscan.data.modal.Product
import com.kaushalprajapati.ecoscan.ui.components.EcoScoreBadge

@Composable
fun ResultScreen(
    product: Product,
    score: EcoScoreResult,
    onScanAnother: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(product.imageEmoji, fontSize = 48.sp)
            Column {
                Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "${product.brand} · ${product.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                EcoScoreBadge(grade = score.grade, size = 72.dp)
                Column {
                    Text("Eco Score", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${score.score} / 100",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (product.description.isNotBlank()) {
            Text(product.description, style = MaterialTheme.typography.bodyLarge)
        }

        Text("Score Breakdown", style = MaterialTheme.typography.titleLarge)
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                score.breakdown.forEach { factor ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(factor.label, style = MaterialTheme.typography.bodyMedium)
                            Text("${factor.points}/${factor.maxPoints}", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { factor.points.toFloat() / factor.maxPoints.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (factor.positive) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                        )
                    }
                }
            }
        }

        if (product.certifications.isNotEmpty()) {
            Text("Certifications", style = MaterialTheme.typography.titleLarge)
            Text(product.certifications.joinToString(" · "), style = MaterialTheme.typography.bodyLarge)
        }

        Divider()

        Text("Tips", style = MaterialTheme.typography.titleLarge)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            score.tips.forEach { tip ->
                Text("• $tip", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f)) { Text("Done") }
            Button(onClick = onScanAnother, modifier = Modifier.weight(1f)) { Text("Scan Another") }
        }
    }
}
