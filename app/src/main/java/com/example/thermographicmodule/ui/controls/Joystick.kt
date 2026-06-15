package com.example.thermographicmodule.ui.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermographicmodule.R
import kotlin.math.*

@Composable
fun JoystickPanel(
    onJoystickMove: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
//            .fillMaxHeight(0.4f)
            .background(Color(0xff33343A))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row (verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    painter = painterResource(R.drawable.outline_joystick_24),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Управление движением",
                    fontWeight = FontWeight.Light,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(0.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Джойстик
            SimpleJoystick()

        }
    }
}

@Composable
fun SimpleJoystick() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val joystickSize = 136.dp
    val maxRadius = 60f // Максимальное отклонение в пикселях

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "(X: ${"%.1f".format(offsetX)}, Y: ${"%.1f".format(offsetY)})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xffB0C6FF)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Джойстик
            Box(
                modifier = Modifier
                    .size(joystickSize)
                    .background(Color(0xff4F515C)/*Color.Gray.copy(alpha = 1f)*/, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = (offsetX + dragAmount.x).coerceIn(-maxRadius, maxRadius)
                                val newY = (offsetY + dragAmount.y).coerceIn(-maxRadius, maxRadius)
                                offsetX = newX
                                offsetY = newY
                            },
                            onDragEnd = {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    // Рисуем пунктирную горизонтальную линию
                    drawLine(
                        color = Color(0xffB0C6FF),//Color.White.copy(alpha = 0.5f),
                        start = Offset(0f, centerY),
                        end = Offset(size.width, centerY),
                        strokeWidth = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = floatArrayOf(12f, 12f),
                            phase = 0f
                        )
                    )

                    // Рисуем пунктирную вертикальную линию
                    drawLine(
                        color = Color(0xffB0C6FF),//Color.White.copy(alpha = 0.5f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = floatArrayOf(12f, 12f),
                            phase = 0f
                        )
                    )
                    drawCircle(
                        color = Color(0xffEDEDED), //Color(0xffB0C6FF),
                        radius = 30f,
                        center = Offset(centerX + offsetX, centerY + offsetY)
                    )

                }
            }
        }
//        Spacer(modifier = Modifier.height(16.dp))
////        // Вывод координат
//        Text(
//            text = "(X: ${"%.1f".format(offsetX)}, Y: ${"%.1f".format(offsetY)})",
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Light,
//            color = Color(0xffB0C6FF)
//        )
//
//        // Нормализованные координаты (-1 до 1)
//        val normX = (offsetX / maxRadius).coerceIn(-1f, 1f)
//        val normY = (offsetY / maxRadius).coerceIn(-1f, 1f)
//        Text(
//            text = "Normalized: X: ${"%.2f".format(normX)} | Y: ${"%.2f".format(normY)}",
//            fontSize = 14.sp,
//            color = Color.Gray
//        )
    }

}

@Preview
@Composable
fun PreviewSimpleJoystick() {
    SimpleJoystick()
}