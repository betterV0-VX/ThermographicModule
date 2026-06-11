package com.example.thermographicmodule.ui.controls

import android.text.BoringLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.toRange
import com.example.thermographicmodule.R
import com.example.thermographicmodule.ui.theme.ThermographicModuleTheme

@Composable
fun ParameterControl(label: String, value: Int, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        OutlinedTextField(
            value = value.toString(),
            onValueChange = onValueChange,
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(4.dp)
        )
        Button(
            onClick = onSend,
            modifier = Modifier
                .width(94.dp)
                .padding(top = 4.dp)
                .height(32.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Отправить", fontSize = 12.sp)
        }
    }
}

@Composable
fun ZoomSection(selectedZoom: Int, onZoomSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ZOOM", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
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
                    ),
                shape = RoundedCornerShape(0.dp), // Отключаем стандартную форму кнопки,
                contentPadding = PaddingValues(0.dp),
                colors = if (selectedZoom == 1) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("X1")
            }
            listOf(2, 4).forEach { zoom ->
                Button(
                    onClick = { onZoomSelected(zoom) },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(92.dp, 40.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (selectedZoom == zoom) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text("X$zoom")
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
                    ),
                shape = RoundedCornerShape(0.dp), // Отключаем стандартную форму кнопки,
                contentPadding = PaddingValues(0.dp),
                colors = if (selectedZoom == 8) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("X8")
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
fun CompoundSlider(parameterName: String, initialValue: Int, range: ClosedFloatingPointRange<Float>){
    var sliderPosition by remember { mutableStateOf(1f)}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)){
        Card(shape = CardDefaults.elevatedShape, modifier = Modifier.padding(20.dp)) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                steps = 255,
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = "$parameterName: ")
                Text(text = sliderPosition.toInt().toString())
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CompoundSliderPreview() {
    ThermographicModuleTheme {
        CompoundSlider("Яркость", 100, 0f..254f)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ParameterControlPreview() {
//    ThermographicModuleTheme {
//        ParameterControl("Гистограмма", 100, {}, {})
//    }
//}
//
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