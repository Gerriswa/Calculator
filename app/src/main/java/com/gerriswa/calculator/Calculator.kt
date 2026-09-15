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
    viewModel: CalculatorViewModel = viewModel() // можно просто viewModel
) {
    val state = viewModel.state.collectAsState()

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
            var colorExpression =
                MaterialTheme.colorScheme.onPrimaryContainer // exception - CalculatorState.Error
            Log.d("Calculator", "${state.value}")

            val (textExpression, textResult) =
                when (val currentState =
                    state.value) { // !!! надо еще раз разобраться почему значение может поменяться , разве отрисовка просто не начнется заново
                    ScreenState.Initial -> {
                        Log.d("Calculator", "state init")
                        "" to ""
                    }

                    is ScreenState.Success -> {
                        Log.d("Calculator", "state success")
                        currentState.result to ""
                    }

                    is ScreenState.Error -> {
                        colorExpression = MaterialTheme.colorScheme.error
                        Log.d("Calculator", "state error")
                        currentState.expression to ""
                    }

                    is ScreenState.Input -> {
                        Log.d("Calculator", "state input")
                        currentState.expression to currentState.result
                    }
                }

            Text(
                modifier = Modifier,
                text = textExpression,
                lineHeight = 36.sp,
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

        val onClick: (symbol: Operation) -> Unit = { operation ->
            viewModel.processCommand(CommandOperation.Input(operation))
            Log.d("Calculator", "Click, input:${operation.name}")
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
                            onClick(Operation.FACTORIAL)
                        }
                } else {
                    Modifier
                        .weight(1f)
                        .clickable {
                            when (it) {
                                "√" -> onClick(Operation.SQRT)
                                "π" -> onClick(Operation.PI)
                                "!" -> onClick(Operation.POWER)
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
                                                0 -> viewModel.processCommand(CommandOperation.Clear)
                                                1 -> onClick(Operation.PARENTHESIS)
                                                2 -> onClick(Operation.PERCENT)
                                                3 -> onClick(Operation.DIVIDE)
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
                                                onClick(Operation.DIGITAL_0)
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
                                                    1 -> onClick(Operation.DOT)
                                                    2 -> viewModel.processCommand(
                                                        CommandOperation.Evaluate
                                                    )
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
                                                "1" -> onClick(Operation.DIGITAL_1)
                                                "2" -> onClick(Operation.DIGITAL_2)
                                                "3" -> onClick(Operation.DIGITAL_3)
                                                "4" -> onClick(Operation.DIGITAL_4)
                                                "5" -> onClick(Operation.DIGITAL_5)
                                                "6" -> onClick(Operation.DIGITAL_6)
                                                "7" -> onClick(Operation.DIGITAL_7)
                                                "8" -> onClick(Operation.DIGITAL_8)
                                                "9" -> onClick(Operation.DIGITAL_9)
                                                "+" -> onClick(Operation.ADD)
                                                "-" -> onClick(Operation.SUBTRACT)
                                                "X" -> onClick(Operation.MULTIPLY)
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
