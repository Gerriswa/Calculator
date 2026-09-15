package com.gerriswa.calculator

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mariuszgromada.math.mxparser.Expression

class CalculatorViewModel : ViewModel() {
    private val _state: MutableStateFlow<ScreenState> =
        MutableStateFlow(ScreenState.Initial)
    var state = _state.asStateFlow()
    private var expression: String = ""

    fun processCommand(command: CommandOperation) {
        when (command) {

            CommandOperation.Clear -> {
                expression = ""
                _state.value = ScreenState.Initial
                Log.d("Calculator", "AC button is clicked")
            }

            CommandOperation.Evaluate -> {
                val result = evaluate()
                if (result != null) {
                    _state.value = ScreenState.Success(result)
                } else {
                    _state.value = ScreenState.Error(expression)
                }
                Log.d("Calculator", "Evaluate: = button is clicked")
            }

            is CommandOperation.Input -> {
                val gotOperation = if (command.operation == Operation.PARENTHESIS) {
                    getCorrectParenthesis()
                } else {
                    command.operation.drawnSymbol
                }
                expression += gotOperation
                _state.value = ScreenState.Input(
                    expression = expression,
                    result = evaluate() ?: ""
                )
                Log.d(
                    "Calculator",
                    "$command.symbol: ${command.operation.drawnSymbol} button is clicked"
                )
            }
        }
    }

    private fun evaluate(): String? {
        return expression.replace('x', '*').replace(',', '.')
            .let { Expression(it) }
            .calculate()
            .takeIf { it.isFinite() }?.toString()
    }

    fun getCorrectParenthesis(): String {
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        return when {
            expression.isEmpty() -> "("
            expression.last().let { !it.isDigit() && it != ')' && it != 'π' } -> "("
            openCount > closeCount -> ")"
            else -> "("
        }
    }
}

sealed interface ScreenState {

    data object Initial : ScreenState

    data class Input(
        val expression: String,
        val result: String
    ) : ScreenState

    data class Success(val result: String) : ScreenState

    data class Error(val expression: String) : ScreenState
}

sealed interface CommandOperation {
    data object Clear : CommandOperation
    data object Evaluate : CommandOperation
    data class Input(val operation: Operation) : CommandOperation
}

enum class Operation(val drawnSymbol: String) {
    DIGITAL_0("0"),
    DIGITAL_1("1"),
    DIGITAL_2("2"),
    DIGITAL_3("3"),
    DIGITAL_4("4"),
    DIGITAL_5("5"),
    DIGITAL_6("6"),
    DIGITAL_7("7"),
    DIGITAL_8("8"),
    DIGITAL_9("9"),
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("x"),
    DIVIDE("÷"),
    PERCENT("%"),
    POWER("!"),
    FACTORIAL("^"),
    SQRT("√"),
    PI("π"),
    DOT(","),
    PARENTHESIS("( )")
}
