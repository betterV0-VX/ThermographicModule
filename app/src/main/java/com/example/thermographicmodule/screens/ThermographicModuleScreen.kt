package com.example.thermographicmodule.screens

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thermographicmodule.R
import com.example.thermographicmodule.main.MainViewModel
import com.example.thermographicmodule.ui.controls.CompoundSlider
import com.example.thermographicmodule.ui.controls.ParameterControl
import com.example.thermographicmodule.ui.controls.SwitchModuleTurnOn
import com.example.thermographicmodule.ui.controls.ToggleButton
import com.example.thermographicmodule.ui.controls.ZoomSection

private const val TAG = "ThermographicModuleScreen"

@OptIn(ExperimentalLayoutApi::class)
@Composable

fun ThermographicModuleScreen(viewModel: MainViewModel){
    val modes = viewModel.modes.collectAsState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SwitchModuleTurnOn(viewModel.isModuleOn, viewModel::toggleModuleOn)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ParameterControl("[0.. ГИСТОГРАММА", viewModel.histogram, viewModel::setHistogram, viewModel::sendHistogram)
            ParameterControl("ЯРКОСТЬ", viewModel.brightness, viewModel::setBrightness, viewModel::sendBrightness)
            ParameterControl("УСИЛЕНИЕ  ..254]", viewModel.gain, viewModel::setGain, viewModel::sendGain)
        }
        Spacer(modifier = Modifier.height(8.dp))
        CompoundSlider("ЯРКОСТЬ", viewModel.brightness, 0f..254f)
        ZoomSection(viewModel.currentZoom, viewModel::setZoom)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = viewModel::startBlindCalibration,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(128.dp, 54.dp),
                contentPadding = PaddingValues(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Калибровка по шторке")
            }

            Button(
                onClick = viewModel::startCapCalibration,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(128.dp, 54.dp),
                contentPadding = PaddingValues(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Калибровка по крышке")
            }

            Button(
                onClick = viewModel::findDefectPixels,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(128.dp, 54.dp),
                contentPadding = PaddingValues(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Нахождение битых пикселей")
            }
        }
        Text("Режимы (вкл/выкл)", color=Color.LightGray,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ToggleButton(
                    modes.value.waiting, "Режим ожидания",
                    "Режим ожидания",
                    viewModel::toggleWaiting,
                )
                ToggleButton(
                    modes.value.binning, "Биннинг", "Биннинг",
                    viewModel::toggleBinning,
                )
                ToggleButton(
                    modes.value.autoBinning, "Автобиннинг", "Автобиннинг",
                    viewModel::toggleAutoBinning,
                )
                ToggleButton(
                    modes.value.alc, "ALC", "ALC",
                    viewModel::toggleAlc,
                )
                ToggleButton(
                    modes.value.alcBorder, "Граница ALC",
                    "Граница ALC",
                    viewModel::toggleAlcBorder,
                )
                ToggleButton(
                    modes.value.fpsSlowdown, "Замедление FPS",
                    "Замедление FPS",
                    viewModel::toggleFpsSlowdown,
                )
                ToggleButton(
                    modes.value.autoFpsSlowdown, "Автозамедление FPS",
                    "Автозамедление FPS",
                    viewModel::toggleAutoFpsSlowdown,
                )
                ToggleButton(
                    modes.value.polarityInversion, "Инверсия полярности",
                    "Инверсия полярности",
                    viewModel::togglePolarityInversion,
                )
                ToggleButton(
                    modes.value.correction, "Коррекция",
                    "Коррекция",
                    viewModel::toggleCorrection,
                )


            }
        }
        TextButton(onClick = viewModel::saveParametersToHardware) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(1f).padding(10.dp)
            ) {
                Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                Icon(
                    painterResource(
                        R.drawable.outline_save_24
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Text(" Сохранение параметров")
            }

        }
    }

}