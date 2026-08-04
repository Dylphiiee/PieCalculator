package com.dylphiiee.piecalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dylphiiee.piecalculator.ui.components.NeumorphicButton
import com.dylphiiee.piecalculator.ui.components.NeumorphicPanel
import com.dylphiiee.piecalculator.ui.theme.OrangeAccent
import com.dylphiiee.piecalculator.ui.theme.TextError
import com.dylphiiee.piecalculator.ui.theme.TextPrimary
import com.dylphiiee.piecalculator.ui.theme.TextSecondary
import com.dylphiiee.piecalculator.viewmodel.CalculatorViewModel

private val buttonRows = listOf(
    listOf("C", "⌫", "%", "÷"),
    listOf("7", "8", "9", "×"),
    listOf("4", "5", "6", "-"),
    listOf("1", "2", "3", "+"),
    listOf("sin(", "0", ".", "="),
)

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display
        NeumorphicPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cornerRadius = 24
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = viewModel.expression.ifBlank { "0" },
                    color = TextSecondary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.result,
                    color = if (viewModel.isError) TextError else TextPrimary,
                    fontSize = 40.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button grid
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            buttonRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    row.forEach { label ->
                        NeumorphicButton(
                            label = label,
                            isAccent = label == "=",
                            modifier = Modifier.weight(1f).height(64.dp)
                        ) {
                            handleButton(viewModel, label)
                        }
                    }
                }
            }
        }
    }
}

private fun handleButton(viewModel: CalculatorViewModel, label: String) {
    when (label) {
        "C" -> viewModel.onClear()
        "⌫" -> viewModel.onBackspace()
        "=" -> viewModel.onEquals()
        "×" -> viewModel.onInput("*")
        "÷" -> viewModel.onInput("/")
        else -> viewModel.onInput(label)
    }
}
