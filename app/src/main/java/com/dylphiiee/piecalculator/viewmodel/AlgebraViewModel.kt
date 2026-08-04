package com.dylphiiee.piecalculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dylphiiee.piecalculator.logic.AlgebraEngine
import com.dylphiiee.piecalculator.logic.AlgebraResult

enum class AlgebraMode { SOLVE, SIMPLIFY }

class AlgebraViewModel : ViewModel() {

    var input by mutableStateOf("")
    var mode by mutableStateOf(AlgebraMode.SOLVE)
        private set

    var resultText by mutableStateOf("")
        private set
    var isError by mutableStateOf(false)
        private set

    fun setMode(newMode: AlgebraMode) {
        mode = newMode
        resultText = ""
        isError = false
    }

    fun process() {
        if (input.isBlank()) return
        try {
            val result = when (mode) {
                AlgebraMode.SOLVE -> AlgebraEngine.solveEquation(input)
                AlgebraMode.SIMPLIFY -> AlgebraEngine.simplify(input)
            }
            resultText = formatResult(result)
            isError = false
        } catch (e: Exception) {
            resultText = e.message ?: "Ekspresi tidak valid"
            isError = true
        }
    }

    private fun formatResult(result: AlgebraResult): String = when (result) {
        is AlgebraResult.Linear -> "x = ${trim(result.x)}"
        is AlgebraResult.Quadratic -> {
            if (result.x1 != null && result.x2 != null) {
                "x₁ = ${trim(result.x1)}\nx₂ = ${trim(result.x2)}"
            } else {
                val re = trim(result.realPart ?: 0.0)
                val im = trim(result.imagPart ?: 0.0)
                "x₁ = $re + ${im}i\nx₂ = $re - ${im}i"
            }
        }
        is AlgebraResult.Simplified -> result.expression
        is AlgebraResult.Info -> result.message
    }

    private fun trim(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else "%.4f".format(value).trimEnd('0').trimEnd('.')
}
