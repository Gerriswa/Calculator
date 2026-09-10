package com.gerriswa.calculator

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    calculatorViewModel: CalculatorViewModel = viewModel() // можно просто viewModel
) {
    val state = calculatorViewModel.state.collectAsState()

    Column(
        modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp, topEnd = 0.dp,
                        bottomEnd = 40.dp, bottomStart = 40.dp
                    )
                )
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .padding(bottom = 16.dp, end = 40.dp, start = 40.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            var colorExpression = MaterialTheme.colorScheme.onPrimaryContainer // exception - CalculatorState.Error

            val (textExpression, textResult) =
            when (val currentState = state.value) {
                is CalculatorState.Error -> {
                    colorExpression = MaterialTheme.colorScheme.error
                    currentState.expression to ""}
                CalculatorState.Initial -> "" to ""
                is CalculatorState.Input -> currentState.expression to currentState.result
                is CalculatorState.Success -> currentState.result to ""
            }

            Text(
                modifier = Modifier,
                text = textExpression,
                lineHeight =  36.sp,
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp,
                color = colorExpression
            )
            Text(
                modifier = Modifier,
                text = textResult,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
        ) {
            listOf("√", "π", "^", "!").forEach {
                val modifier = if (it == "^") {
                    Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                        .clickable {
                            calculatorViewModel.processCommand(CalculatorCommand.Input(Symbol.FACTORIAL))
                        }
                } else {
                    Modifier
                        .weight(1f)
                        .clickable {
                            when (it) {
                                "√" -> calculatorViewModel.processCommand(
                                    CalculatorCommand.Input(Symbol.SQRT)
                                )

                                "π" -> calculatorViewModel.processCommand(
                                    CalculatorCommand.Input(Symbol.PI)
                                )

                                "!" -> calculatorViewModel.processCommand(
                                    CalculatorCommand.Input(Symbol.POWER)
                                )
                            }
                        }
                }
                Text(
                    modifier = modifier,
                    color = MaterialTheme.colorScheme.onBackground,
                    text = it,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // PrintButton() // думаю ,что надо передавать модификатор от Column !
            val numbers: String = ("7894561230")
            val symbols = listOf<String>("AC", "(  )", "%", "÷", "X", "-", "+", ",", "=")
            var counterSymbols = 0
            var countersNumbers = 0

            for (row in 1..5) {
                Row(
                    modifier = Modifier,
                    Arrangement.spacedBy(8.dp)
                ) {
                    when (row) {
                        1 -> {
                            repeat(4) {
                                val color =
                                    if (it == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            when (it) {
                                                0 -> calculatorViewModel.processCommand(
                                                    CalculatorCommand.Clear
                                                )

                                                1 -> calculatorViewModel.processCommand(
                                                    CalculatorCommand.Input(
                                                        Symbol.PARENTHESIS
                                                    )
                                                )

                                                2 -> calculatorViewModel.processCommand(
                                                    CalculatorCommand.Input(
                                                        Symbol.PERCENT
                                                    )
                                                )

                                                3 -> calculatorViewModel.processCommand(
                                                    CalculatorCommand.Input(
                                                        Symbol.DIVIDE
                                                    )
                                                )
                                            }
                                        }
                                        .aspectRatio(1f)
                                        .weight(1f)
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = symbols[counterSymbols++],
                                        fontSize = 40.sp,
                                    )
                                }
                            }
                        }

                        5 -> {
                            repeat(3) {
                                val color =
                                    if (it == 2) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                if (it == 0) {
                                    Box(
                                        Modifier
                                            .clip(CircleShape)
                                            .weight(2.1f)
                                            .background(color)
                                            .aspectRatio(2 / 0.95f)
                                            .clickable {
                                                calculatorViewModel.processCommand(
                                                    CalculatorCommand.Input(
                                                        Symbol.DIGITAL_0
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = numbers[countersNumbers].toString(),
                                            fontSize = 40.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                } else {
                                    Box(
                                        Modifier
                                            .clip(CircleShape)
                                            .weight(1f)
                                            .background(color)
                                            .clickable {
                                                when (it) {
                                                    1 -> {
                                                        calculatorViewModel.processCommand(
                                                            CalculatorCommand.Input(
                                                                Symbol.DOT
                                                            )
                                                        )
                                                    }

                                                    2 -> {
                                                        calculatorViewModel.processCommand(
                                                            CalculatorCommand.Evaluate
                                                            ///логика отрисовки
                                                        )
                                                    }
                                                }

                                            }
                                            .aspectRatio(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = symbols[counterSymbols++],
                                            fontSize = 40.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        else -> { // 2 3 4 строка
                            repeat(4) {
                                val color =
                                    if (it == 3) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

                                val buttonText =
                                    if (it == 3) symbols[counterSymbols++] else numbers[countersNumbers++].toString()


                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .weight(1f)
                                        .background(color)
                                        .aspectRatio(1f)
                                        .clickable {
                                            when (buttonText) {
                                                "1" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_1)
                                                    )
                                                }

                                                "2" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_2)
                                                    )
                                                }

                                                "3" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_3)
                                                    )
                                                }

                                                "4" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_4)
                                                    )
                                                }

                                                "5" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_5)
                                                    )
                                                }

                                                "6" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_6)
                                                    )
                                                }

                                                "7" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_7)
                                                    )
                                                }

                                                "8" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_8)
                                                    )
                                                }

                                                "9" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.DIGITAL_9)
                                                    )
                                                }

                                                "X" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.MULTIPLY)
                                                    )
                                                }

                                                "-" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.SUBTRACT)
                                                    )
                                                }

                                                "+" -> {
                                                    calculatorViewModel.processCommand(
                                                        CalculatorCommand.Input(Symbol.ADD)
                                                    )
                                                }
                                            }
                                        },
                                    Alignment.Center

                                ) {

                                    Text(
                                        text = buttonText,
                                        fontSize = 40.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/*....
Создайте отдельный файл, например ButtonsData.kt, и объявите там список как val (не var):

kotlin
// ButtonsData.kt
data class ButtonData(val text: String, val command: CalculatorCommand)

val BUTTONS = listOf(
    ButtonData("
........


// 1. Заранее готовим список с командами
data class ButtonData(val text: String, val command: CalculatorCommand)

val buttons = listOf(
    ButtonData("1", Command.Input(Symbol.DIGITAL_1)),
    ButtonData("2", Command.Input(Symbol.DIGITAL_2)),
    // ... все кнопки
    ButtonData("X", Command.Input(Symbol.MULTIPLY)),
)

// 2. В композиции просто используем цикл
buttons.forEach { data ->
    Box(
        modifier = Modifier
            .clickable { viewModel.processCommand(data.command) } // Вот тут нет when!
    ) {
        Text(data.text)
    }
}
 */