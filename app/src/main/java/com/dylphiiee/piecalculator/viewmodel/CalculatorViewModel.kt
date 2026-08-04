package com.dylphiiee.piecalculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dylphiiee.piecalculator.logic.ExpressionEvaluator

class CalculatorViewModel : ViewModel() {

    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("0")
        private set

    var isError by mutableStateOf(false)
        private set

    fun onInput(token: String) {
        isError = false
        expression += token
    }

    fun onClear() {
        expression = ""
        result = "0"
        isError = false
    }

    fun onBackspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
        isError = false
    }

    fun onEquals() {
        if (expression.isBlank()) return
        try {
            val value = ExpressionEvaluator.evaluate(expression)
            result = formatResult(value)
            isError = false
        } catch (e: Exception) {
            result = e.message ?: "Error"
            isError = true
        }
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            "%.8f".format(value).trimEnd('0').trimEnd('.')
        }
    }
}
