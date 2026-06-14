package com.example.thermographicmodule.ui.controls

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermographicmodule.R
import com.example.thermographicmodule.data.ParameterIsChosen
import com.example.thermographicmodule.data.SectionCardData
import com.example.thermographicmodule.data.SectionIsChosen
import com.example.thermographicmodule.data.SectionType
import com.example.thermographicmodule.ui.theme.ThermographicModuleTheme

@Composable
fun ParameterControl(label: String, isChosen: Boolean,  onChosenParameterChange: () -> Unit, value: Int, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onChosenParameterChange)
            .then(
                if (isChosen) {
                    Modifier.drawBehind {
                        drawLine(
                            color = Color(0xffB0C6FF),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                } else Modifier
            ),
            shape = if (isChosen) { RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )} else {
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            },
            colors = CardColors(
                if (isChosen){
                    Color(0xff33343A)
                } else {
                    Color(0xff21222B)
                },
                contentColor = Color.Black,
                disabledContainerColor = Color.Black,
                disabledContentColor = Color.Black
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(120.dp)
                .height(100.dp)

        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            OutlinedTextField(
                value = value.toString(),
                onValueChange = onValueChange,
                modifier = Modifier
                    .height(52.dp)
                    .width(90.dp),
//                    .fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}

@Composable
fun SendButton(onSend: () -> Unit, modifier: Modifier = Modifier){
    Button(
        onClick = onSend,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 12.dp
        ),
        colors = ButtonColors(
            Color(0xff33343A),
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Gray
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_send_24),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text("Отправить", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun ToggleContinuousButton(state: Boolean,
                           textWhenTurnedOn: String,
                           textWhenTurnedOff: String,
                           actionOnClick: (Boolean)->Unit,
                           modifier: Modifier = Modifier){
    Button(
        onClick = { actionOnClick(!state) },
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 0.dp
        ),
        colors = ButtonColors(
            if (state) {
                Color(0xff33343A)
            } else {
                Color(0xff21222B)
            },
            contentColor = if (state) {
                ButtonDefaults.buttonColors().containerColor
            } else {
                Color.Gray
            },
            disabledContainerColor = Color.Black,
            disabledContentColor = Color.Black
        ),
        modifier = modifier.padding(0.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.continious_sending),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(if (state) { textWhenTurnedOn } else { textWhenTurnedOff },
            fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp)
        )
    }
}


@Composable
fun ZoomSection(selectedZoom: Int, onZoomSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
        Text("ZOOM", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Button(
                onClick = { onZoomSelected(1) },
                modifier = Modifier
                    .size(92.dp, 40.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 12.dp
                        )
                    )
                    .weight(1f)
                    ,
                shape = RoundedCornerShape(0.dp), // Отключаем стандартную форму кнопки,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonColors(
                    if (selectedZoom == 1) {
                        Color(0xff33343A)
                    } else {
                        Color(0xff21222B)
                    },
                    contentColor = if (selectedZoom == 1) {
                        ButtonDefaults.buttonColors().containerColor
                    } else {
                        Color.Gray
                    },
                    disabledContainerColor = Color.Black,
                    disabledContentColor = Color.Black
                ),
//                colors = if (selectedZoom == 1) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("X1", fontWeight = if (selectedZoom == 1) {
                    FontWeight.Bold} else {
                    FontWeight.Normal})
            }
            listOf(2, 4).forEach { zoom ->
                Button(
                    onClick = { onZoomSelected(zoom) },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .size(92.dp, 40.dp)
                        .weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonColors(
                        if (selectedZoom == zoom) {
                            Color(0xff33343A)
                        } else {
                            Color(0xff21222B)
                        },
                        contentColor = if (selectedZoom == zoom) {
                            ButtonDefaults.buttonColors().containerColor
                        } else {
                            Color.Gray
                        },
                        disabledContainerColor = Color.Black,
                        disabledContentColor = Color.Black
                    ),
//                    colors = if (selectedZoom == zoom) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text("X$zoom", fontWeight = if (selectedZoom == zoom) {
                        FontWeight.Bold} else {
                        FontWeight.Normal})
                }
            }
            Button(
                onClick = { onZoomSelected(8) },
                modifier = Modifier
                    .size(92.dp, 40.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 12.dp,
                            bottomEnd = 12.dp,
                            bottomStart = 0.dp
                        )
                    )
                    .weight(1f),
                shape = RoundedCornerShape(0.dp), // Отключаем стандартную форму кнопки,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonColors(
                    if (selectedZoom == 8) {
                        Color(0xff33343A)
                    } else {
                        Color(0xff21222B)
                    },
                    contentColor = if (selectedZoom == 8) {
                        ButtonDefaults.buttonColors().containerColor
                    } else {
                        Color.Gray
                    },
                    disabledContainerColor = Color.Black,
                    disabledContentColor = Color.Black
                ),
//                colors = if (selectedZoom == 8) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("X8", fontWeight = if (selectedZoom == 8) {
                    FontWeight.Bold} else {
                    FontWeight.Normal})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchModuleTurnOn(checked: Boolean, onCheckedChange: (Boolean) -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 24.dp)
            .fillMaxWidth(1f)) {
        Switch(checked = checked, onCheckedChange = { onCheckedChange(it) },
            thumbContent = if (checked) {
                {
                    Icon(
                        painter = painterResource(R.drawable.outline_mode_off_on_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.size(50.dp))
        Spacer(Modifier.width(8.dp))
        if (checked){
            Text("Подключен",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
        } else {
            Text("Отключен",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToggleButton(state: Boolean,
                 textWhenTurnedOn: String,
                 textWhenTurnedOff: String,
                 actionOnClick: (Boolean)->Unit) {
    Button(
        onClick = { if (state) { actionOnClick(false) } else { actionOnClick(true) } },
        shape = RoundedCornerShape(4.dp),
        colors = if (state) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Text(if (state) { textWhenTurnedOn } else { textWhenTurnedOff },
            fontSize = 12.sp,
        )
    }
}

@Composable
fun CompoundSlider(parameterName: String, initialValue: Int, onValueChange: (Float) -> Unit, range: ClosedFloatingPointRange<Float>){
    var sliderPosition by remember { mutableStateOf(1f)}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)){
        Card(shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 12.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ), modifier = Modifier.padding(start=8.dp, end=8.dp, bottom = 4.dp, top = 8.dp)) {
            val resId = when(parameterName){
                "ГИСТОГРАММА" -> R.drawable.outline_bar_chart_24
                "ЯРКОСТЬ" -> R.drawable.outline_brightness_5_24
                "УСИЛЕНИЕ" -> R.drawable.gain
                else -> R.drawable.outline_error_24
            }
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(resId),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 20.dp, start = 16.dp).size(30.dp),
                    tint = Color(0xffB0C6FF)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Slider(
                        value = initialValue.toFloat(), //sliderPosition
                        onValueChange = onValueChange, //{ sliderPosition = it },
                        steps = 255,
                        valueRange = range,
                        colors = SliderDefaults.colors(
                            activeTickColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            inactiveTickColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        modifier = Modifier
                            .height(70.dp)
                            .padding(end = 24.dp, start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = range.start.toInt().toString(),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 7.dp)
                        )
                        Text(text = "...", fontSize = 12.sp)
                        Row {
                            //                        Text(text = "$parameterName: ", fontSize = 12.sp)
                            Text(text = initialValue.toString(), fontSize = 12.sp)
                        }
                        Text(text = "...", fontSize = 12.sp)
                        Text(
                            text = range.endInclusive.toInt().toString(),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 23.dp)
                        )
                    }
                }
            }



        }

    }
}

@Composable
fun SectionCard(isChosen: Boolean, sectionCardData: SectionCardData, onClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .height(110.dp)
        .then(
            if (isChosen) {
                Modifier.drawBehind {
                    drawLine(
                        color = Color(0xffB0C6FF),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            } else Modifier
        )
        .clickable(true, onClick = onClick),
        shape = if (isChosen) { RoundedCornerShape(
            topStart = 16.dp,    // Верхний левый
            topEnd = 16.dp,      // Верхний правый
            bottomStart = 0.dp, // Нижний левый - 0 скругление
            bottomEnd = 0.dp    // Нижний правый - 0 скругление
        )} else {
            RoundedCornerShape(
                topStart = 16.dp,    // Верхний левый
                topEnd = 16.dp,      // Верхний правый
                bottomStart = 16.dp, // Нижний левый - 0 скругление
                bottomEnd = 16.dp    // Нижний правый - 0 скругление
            )
        },
        colors = CardColors(
            if (isChosen){
                Color(0xff33343A)
            } else {
                Color(0xff21222B)
            },
            contentColor = Color.Black,
            disabledContainerColor = Color.Black,
            disabledContentColor = Color.Black
        )
    )
         {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(4.dp)
                .wrapContentHeight()){
            Icon(
                painter = painterResource(sectionCardData.sectionIconResId),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = if (isChosen) { Color(0xffB0C6FF) } else {
                    Color(0xff4F515C)
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(sectionCardData.sectionName,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = if (isChosen) {Color.LightGray } else {
                    Color.Gray
                },
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(68.dp)
                    .padding(2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionCarousel(chosenSections: SectionIsChosen,
                    listOfSectionCardsData: List<SectionCardData>,
                    onSectionClick: (SectionType) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RectangleShape)
            .padding(top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
        items(listOfSectionCardsData) { sectionData ->
            val isChosen = when (sectionData.sectionType) {
                SectionType.REQUEST -> chosenSections.requestIsChosen
                SectionType.ROTATION -> chosenSections.rotationIsChosen
                SectionType.ANALYSIS_AREA -> chosenSections.analysisAreaIsChosen
                SectionType.ZOOM_AREA -> chosenSections.zoomAreaIsChosen
                SectionType.USER_PARAMETER -> chosenSections.userParameterIsChosen
            }
            SectionCard(
                isChosen = isChosen,
                sectionCardData = sectionData,
                onClick = { onSectionClick(sectionData.sectionType) }
            )
        }
    }
}





//@Preview(showBackground = true)
//@Composable
//fun CompoundSliderPreview() {
//    ThermographicModuleTheme {
//        CompoundSlider("Яркость", 100, {(Float)-> Unit }, { }..254f)
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun ParameterControlPreview() {
//    ThermographicModuleTheme {
//        ParameterControl("Гистограмма", 100, {}, {})
//    }
//}

//
//@Preview(showBackground = true)
//@Composable
//fun ZoomSectionPreview() {
//    ThermographicModuleTheme {
//        ZoomSection(1, { Int-> Unit })
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun SwitchPreview() {
//    ThermographicModuleTheme {
//        SwitchModuleTurnOn(false, {Boolean -> Unit})
//    }
//}

@Preview(showBackground = true)
@Composable
fun ToggleButtonPreview() {
    ThermographicModuleTheme {
        ToggleButton (false, "Включено", "Выключено", {},)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SectionCardPreview() {
//    ThermographicModuleTheme {
//        SectionCard(SectionCardData(isChosen = true, "Зона анализа АРУ", R.drawable.outline_terminal_2_24))
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SectionCarouselPreview() {
//    ThermographicModuleTheme {
//        val items = remember {
//            listOf(
//                SectionCardData(isChosen = true, "Запросы", R.drawable.outline_terminal_2_24),
//                SectionCardData(isChosen = false, "Поворот", R.drawable.outline_flip_camera_android_24),
//                SectionCardData(isChosen = false, "Зона анализа АРУ", R.drawable.outline_activity_zone_24),
//                SectionCardData(isChosen = false, "Зона масштабирования", R.drawable.outline_feature_search_24)
//            )
//        }
//        SectionCarousel(items)
//    }
//}