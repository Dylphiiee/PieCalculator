package com.dylphiiee.piecalculator.logic

import kotlin.math.*

/**
 * Evaluator ekspresi matematika mendukung: + - * / % ^ ( )
 * serta fungsi: sin cos tan sqrt log ln
 * Menggunakan algoritma Shunting-Yard -> RPN -> evaluasi.
 */
object ExpressionEvaluator {

    class EvaluationException(message: String) : Exception(message)

    private val functions = setOf("sin", "cos", "tan", "sqrt", "log", "ln")

    fun evaluate(rawExpression: String): Double {
        val expression = normalize(rawExpression)
        if (expression.isBlank()) throw EvaluationException("Ekspresi kosong")
        val tokens = tokenize(expression)
        val rpn = toRpn(tokens)
        return evalRpn(rpn)
    }

    private fun normalize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(",", ".")
            .replace(" ", "")
    }

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val symbol: String) : Token()
        data class Function(val name: String) : Token()
        object LParen : Token()
        object RParen : Token()
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) i++
                    tokens.add(Token.Number(expr.substring(start, i).toDouble()))
                    continue
                }
                c.isLetter() -> {
                    val start = i
                    while (i < expr.length && expr[i].isLetter()) i++
                    val word = expr.substring(start, i)
                    if (word == "pi") {
                        tokens.add(Token.Number(Math.PI))
                    } else if (word == "e") {
                        tokens.add(Token.Number(Math.E))
                    } else if (functions.contains(word)) {
                        tokens.add(Token.Function(word))
                    } else {
                        throw EvaluationException("Fungsi tidak dikenal: $word")
                    }
                    continue
                }
                c == '(' -> { tokens.add(Token.LParen); i++ }
                c == ')' -> { tokens.add(Token.RParen); i++ }
                c == '-' && (tokens.isEmpty() || tokens.last() is Token.Operator || tokens.last() == Token.LParen) -> {
                    // unary minus -> treat as 0 - x
                    tokens.add(Token.Number(0.0))
                    tokens.add(Token.Operator("-"))
                    i++
                }
                c in "+-*/%^" -> { tokens.add(Token.Operator(c.toString())); i++ }
                else -> throw EvaluationException("Karakter tidak valid: $c")
            }
        }
        return tokens
    }

    private fun precedence(op: String) = when (op) {
        "+", "-" -> 1
        "*", "/", "%" -> 2
        "^" -> 3
        else -> 0
    }

    private fun isRightAssociative(op: String) = op == "^"

    private fun toRpn(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Number -> output.add(token)
                is Token.Function -> stack.addLast(token)
                is Token.Operator -> {
                    while (stack.isNotEmpty() && stack.last() is Token.Operator) {
                        val top = stack.last() as Token.Operator
                        if ((precedence(top.symbol) > precedence(token.symbol)) ||
                            (precedence(top.symbol) == precedence(token.symbol) && !isRightAssociative(token.symbol))
                        ) {
                            output.add(stack.removeLast())
                        } else break
                    }
                    stack.addLast(token)
                }
                Token.LParen -> stack.addLast(token)
                Token.RParen -> {
                    while (stack.isNotEmpty() && stack.last() != Token.LParen) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isEmpty()) throw EvaluationException("Tanda kurung tidak seimbang")
                    stack.removeLast() // pop LParen
                    if (stack.isNotEmpty() && stack.last() is Token.Function) {
                        output.add(stack.removeLast())
                    }
                }
            }
        }
        while (stack.isNotEmpty()) {
            val top = stack.removeLast()
            if (top == Token.LParen || top == Token.RParen) throw EvaluationException("Tanda kurung tidak seimbang")
            output.add(top)
        }
        return output
    }

    private fun evalRpn(rpn: List<Token>): Double {
        val stack = ArrayDeque<Double>()
        for (token in rpn) {
            when (token) {
                is Token.Number -> stack.addLast(token.value)
                is Token.Operator -> {
                    if (stack.size < 2) throw EvaluationException("Ekspresi tidak valid")
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    val result = when (token.symbol) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> if (b == 0.0) throw EvaluationException("Tidak bisa membagi dengan nol") else a / b
                        "%" -> a % b
                        "^" -> a.pow(b)
                        else -> throw EvaluationException("Operator tidak dikenal")
                    }
                    stack.addLast(result)
                }
                is Token.Function -> {
                    if (stack.isEmpty()) throw EvaluationException("Ekspresi tidak valid")
                    val a = stack.removeLast()
                    val result = when (token.name) {
                        "sin" -> sin(Math.toRadians(a))
                        "cos" -> cos(Math.toRadians(a))
                        "tan" -> tan(Math.toRadians(a))
                        "sqrt" -> if (a < 0) throw EvaluationException("Akar dari bilangan negatif") else sqrt(a)
                        "log" -> log10(a)
                        "ln" -> ln(a)
                        else -> throw EvaluationException("Fungsi tidak dikenal")
                    }
                    stack.addLast(result)
                }
                else -> {}
            }
        }
        if (stack.size != 1) throw EvaluationException("Ekspresi tidak valid")
        return stack.last()
    }
}
