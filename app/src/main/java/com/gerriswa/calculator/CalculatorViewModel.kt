package com.gerriswa.calculator

import android.R
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mariuszgromada.math.mxparser.Expression
import kotlin.random.Random
import kotlin.uuid.Uuid.Companion.random

/*Для примитивов — всегда вычисляемый геттер.

Для изменяемых объектов (контейнеры, Flow, State) — прямая ссылка с сужением типа до неизменяемого интерфейса.

Для иммутабельных объектов, которые заменяются целиком — вычисляемый геттер, чтобы читать последнюю версию.*/
class CalculatorViewModel : ViewModel() {
    private val _state: MutableStateFlow<CalculatorState> =
        MutableStateFlow(CalculatorState.Initial)
    var state = _state.asStateFlow() // это публичная переменная -> отдаем просто неизменяемую копию
    private var expression: String = ""

    fun processCommand(command: CalculatorCommand) {
        when (command) {

            CalculatorCommand.Clear -> {
                expression = ""
                _state.value = CalculatorState.Initial
                Log.d("Calculator", "AC button is clicked")
            }

            CalculatorCommand.Evaluate -> { // "оценивать" вызов при клике на '='
                val result = evaluate()
                if(result != null){
                    _state.value = CalculatorState.Success(result)
                }else{
                    _state.value = CalculatorState.Error(expression)
                }
                Log.d("Calculator", "Evaluate: = button is clicked")
            }

            is CalculatorCommand.Input -> {
                val gotSymbol = if(command.symbol == Symbol.PARENTHESIS){
                    getCorrectParenthesis()
                }else{
                    command.symbol.drawnSymbol // переданный enum.drawnSymbol
                }
                expression += gotSymbol
                _state.value = CalculatorState.Input(
                    expression = expression,
                    result = evaluate() ?: ""
                )
                Log.d(
                    "Calculator",
                    "$command.symbol: ${command.symbol.drawnSymbol} button is clicked"
                )
            }
        }
    }

    private fun evaluate(): String? { // вычесления
        return expression.replace('x', '*').replace(',','.')
             .let{Expression(it)}
             .calculate()
             .takeIf { it.isFinite() } ?. toString()
               // calc() возвращает double
    }

    fun getCorrectParenthesis(): String {
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        return when {
            expression.isEmpty() -> "("
            expression.last().let { !it.isDigit() &&  it != ')' && it != 'π' } -> "(" // any operation
            openCount > closeCount -> ")"
            else -> "("
        }
    }
}

sealed interface CalculatorState {

    data object Initial : CalculatorState// если парам отсутствуют

    data class Input(
        val expression: String,
        val result: String
    ) : CalculatorState

    data class Success(val result: String) : CalculatorState

    data class Error(val expression: String) : CalculatorState
}

sealed interface CalculatorCommand {
    data object Clear : CalculatorCommand
    data object Evaluate : CalculatorCommand
    data class Input(val symbol: Symbol) : CalculatorCommand
}

enum class Symbol(val drawnSymbol: String) {
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
