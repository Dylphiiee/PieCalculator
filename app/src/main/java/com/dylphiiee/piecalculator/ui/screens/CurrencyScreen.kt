package com.dylphiiee.piecalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dylphiiee.piecalculator.data.CurrencyList
import com.dylphiiee.piecalculator.ui.components.NeumorphicButton
import com.dylphiiee.piecalculator.ui.components.NeumorphicPanel
import com.dylphiiee.piecalculator.ui.theme.*
import com.dylphiiee.piecalculator.viewmodel.CurrencyUiState
import com.dylphiiee.piecalculator.viewmodel.CurrencyViewModel

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Konversi Mata Uang", color = TextPrimary, fontSize = 22.sp)
        Text("Kurs realtime via frankfurter.app (ECB)", color = TextSecondary, fontSize = 12.sp)

        NeumorphicPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
            TextField(
                value = viewModel.amountInput,
                onValueChange = { viewModel.amountInput = it },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CurrencyDropdown(
                selected = viewModel.fromCurrency,
                onSelect = { viewModel.fromCurrency = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.swapCurrencies() }) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Tukar", tint = OrangeAccent)
            }
            CurrencyDropdown(
                selected = viewModel.toCurrency,
                onSelect = { viewModel.toCurrency = it },
                modifier = Modifier.weight(1f)
            )
        }

        NeumorphicButton(
            label = "Konversi",
            isAccent = true,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            viewModel.convert()
        }

        when (val state = viewModel.uiState) {
            is CurrencyUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = OrangeAccent,
                    trackColor = SurfaceDark
                )
            }
            is CurrencyUiState.Success -> {
                NeumorphicPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                    Column {
                        Text(
                            "${viewModel.amountInput} ${viewModel.fromCurrency} =",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            "${"%.2f".format(state.result)} ${viewModel.toCurrency}",
                            color = OrangeAccent,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "1 ${viewModel.fromCurrency} = ${state.rate} ${viewModel.toCurrency} · ${state.lastUpdated}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            is CurrencyUiState.Error -> {
                Text(state.message, color = TextError, fontSize = 13.sp)
            }
            CurrencyUiState.Idle -> {}
        }
    }
}

@Composable
private fun CurrencyDropdown(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        NeumorphicPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 16) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selected, color = TextPrimary, fontSize = 16.sp)
                IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                    Text("▾", color = OrangeAccent)
                }
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CurrencyList.supported.forEach { code ->
                DropdownMenuItem(text = { Text(code) }, onClick = {
                    onSelect(code)
                    expanded = false
                })
            }
        }
    }
}
