package com.dylphiiee.piecalculator.logic

import kotlin.math.abs
import kotlin.math.sqrt

sealed class AlgebraResult {
    data class Linear(val x: Double) : AlgebraResult()
    data class Quadratic(
        val x1: Double? = null,
        val x2: Double? = null,
        val realPart: Double? = null,
        val imagPart: Double? = null
    ) : AlgebraResult()
    data class Simplified(val expression: String) : AlgebraResult()
    data class Info(val message: String) : AlgebraResult()
}

/**
 * Engine aljabar sederhana untuk persamaan satu variabel (x).
 * Mendukung: solve linear (ax + b = c), solve kuadrat (ax^2 + bx + c = 0),
 * dan simplifikasi ekspresi (menggabungkan suku sejenis).
 *
 * Batasan: hanya variabel "x", pangkat maksimal 2, tanpa pecahan bertingkat/tanda kurung bersarang.
 */
object AlgebraEngine {

    class AlgebraException(message: String) : Exception(message)

    private val termRegex = Regex("""([+-]?\d*\.?\d*)\*?x(?:\^(\d+))?|([+-]?\d+\.?\d*)""")

    /** Parse ekspresi menjadi map: pangkat -> koefisien */
    fun parseExpression(expr: String): Map<Int, Double> {
        val cleaned = expr.replace(" ", "").replace(",", ".")
        if (cleaned.isBlank()) throw AlgebraException("Ekspresi kosong")

        val terms = mutableMapOf<Int, Double>()
        var matched = false

        for (match in termRegex.findAll(cleaned)) {
            if (match.value.isBlank()) continue
            matched = true
            val coefStr = match.groupValues[1]
            val powerStr = match.groupValues[2]
            val constStr = match.groupValues[3]

            if (constStr.isNotBlank()) {
                val value = parseSignedNumber(constStr)
                terms[0] = (terms[0] ?: 0.0) + value
            } else {
                val coef = when (coefStr) {
                    "", "+" -> 1.0
                    "-" -> -1.0
                    else -> coefStr.toDouble()
                }
                val power = if (powerStr.isNotBlank()) powerStr.toInt() else 1
                if (power > 2) throw AlgebraException("Hanya mendukung pangkat maksimal 2")
                terms[power] = (terms[power] ?: 0.0) + coef
            }
        }

        if (!matched) throw AlgebraException("Ekspresi tidak valid")
        return terms
    }

    private fun parseSignedNumber(s: String): Double = when (s) {
        "+", "" -> 1.0
        "-" -> -1.0
        else -> s.toDouble()
    }

    /** Selesaikan persamaan mengandung "=" (linear atau kuadrat) */
    fun solveEquation(equation: String): AlgebraResult {
        val sides = equation.split("=")
        if (sides.size != 2) throw AlgebraException("Persamaan harus mengandung satu tanda '='")

        val left = parseExpression(sides[0])
        val right = parseExpression(sides[1])

        val combined = mutableMapOf<Int, Double>()
        for ((power, coef) in left) combined[power] = (combined[power] ?: 0.0) + coef
        for ((power, coef) in right) combined[power] = (combined[power] ?: 0.0) - coef

        val maxPower = combined.entries.filter { abs(it.value) > 1e-12 }.maxOfOrNull { it.key } ?: 0

        return when (maxPower) {
            0 -> {
                val remainder = combined[0] ?: 0.0
                if (abs(remainder) < 1e-9) AlgebraResult.Info("Persamaan benar untuk semua nilai x")
                else AlgebraResult.Info("Persamaan tidak memiliki solusi")
            }
            1 -> {
                val a = combined[1] ?: 0.0
                val b = combined[0] ?: 0.0
                AlgebraResult.Linear(-b / a)
            }
            2 -> {
                val a = combined[2] ?: 0.0
                val b = combined[1] ?: 0.0
                val c = combined[0] ?: 0.0
                val discriminant = b * b - 4 * a * c
                if (discriminant >= 0) {
                    val sqrtD = sqrt(discriminant)
                    AlgebraResult.Quadratic(
                        x1 = (-b + sqrtD) / (2 * a),
                        x2 = (-b - sqrtD) / (2 * a)
                    )
                } else {
                    AlgebraResult.Quadratic(
                        realPart = -b / (2 * a),
                        imagPart = sqrt(-discriminant) / (2 * a)
                    )
                }
            }
            else -> AlgebraResult.Info("Hanya mendukung persamaan linear & kuadrat")
        }
    }

    /** Sederhanakan ekspresi (tanpa "=") dengan menggabungkan suku sejenis */
    fun simplify(expr: String): AlgebraResult {
        if (expr.contains("=")) throw AlgebraException("Gunakan mode Solve untuk persamaan")
        val terms = parseExpression(expr).filterValues { abs(it) > 1e-12 }
        if (terms.isEmpty()) return AlgebraResult.Simplified("0")

        val sb = StringBuilder()
        for (power in terms.keys.sortedDescending()) {
            val coef = terms[power]!!
            val absCoef = abs(coef)
            val coefStr = if (absCoef == 1.0 && power != 0) "" else formatNumber(absCoef)
            val termStr = when (power) {
                0 -> formatNumber(absCoef)
                1 -> "${coefStr}x"
                else -> "${coefStr}x^$power"
            }
            if (sb.isEmpty()) {
                sb.append(if (coef < 0) "-$termStr" else termStr)
            } else {
                sb.append(if (coef < 0) " - $termStr" else " + $termStr")
            }
        }
        return AlgebraResult.Simplified(sb.toString())
    }

    private fun formatNumber(n: Double): String =
        if (n == n.toLong().toDouble()) n.toLong().toString() else n.toString()
}
