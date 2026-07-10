package com.kaushalprajapati.ecoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaushalprajapati.ecoscan.navigation.EcoScanNavGraph
import com.kaushalprajapati.ecoscan.ui.theme.EcoScanTheme
import com.kaushalprajapati.ecoscan.viewmodel.ScanViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as EcoScanApp

        setContent {
            EcoScanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: ScanViewModel = viewModel(factory = ScanViewModel.factory(app.repository))
                    EcoScanRoot(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun EcoScanRoot(viewModel: ScanViewModel) {
    EcoScanNavGraph(viewModel = viewModel)
}
