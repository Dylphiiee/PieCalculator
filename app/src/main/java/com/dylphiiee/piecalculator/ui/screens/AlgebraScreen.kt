package com.dylphiiee.piecalculator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dylphiiee.piecalculator.ui.components.NeumorphicButton
import com.dylphiiee.piecalculator.ui.components.NeumorphicPanel
import com.dylphiiee.piecalculator.ui.theme.*
import com.dylphiiee.piecalculator.viewmodel.AlgebraMode
import com.dylphiiee.piecalculator.viewmodel.AlgebraViewModel

@Composable
fun AlgebraScreen(viewModel: AlgebraViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Aljabar", color = TextPrimary, fontSize = 22.sp)
        Text(
            if (viewModel.mode == AlgebraMode.SOLVE)
                "Contoh: 2x+3=7  atau  x^2-5x+6=0"
            else "Contoh: 2x+3x-5+x^2",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModeChip(
                text = "Solve",
                selected = viewModel.mode == AlgebraMode.SOLVE
            ) { viewModel.setMode(AlgebraMode.SOLVE) }
            ModeChip(
                text = "Simplify",
                selected = viewModel.mode == AlgebraMode.SIMPLIFY
            ) { viewModel.setMode(AlgebraMode.SIMPLIFY) }
        }

        NeumorphicPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
            TextField(
                value = viewModel.input,
                onValueChange = { viewModel.input = it },
                placeholder = { Text(if (viewModel.mode == AlgebraMode.SOLVE) "Masukkan persamaan" else "Masukkan ekspresi") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = OrangeAccent,
                    unfocusedIndicatorColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        NeumorphicButton(
            label = "Hitung",
            isAccent = true,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            viewModel.process()
        }

        if (viewModel.resultText.isNotBlank()) {
            NeumorphicPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                Text(
                    text = viewModel.resultText,
                    color = if (viewModel.isError) TextError else OrangeAccent,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun ModeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    NeumorphicPanel(
        modifier = Modifier.clickable(onClick = onClick),
        cornerRadius = 14,
        backgroundColor = if (selected) OrangeAccent else SurfaceDark
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else TextPrimary,
            fontSize = 14.sp
        )
    }
}
