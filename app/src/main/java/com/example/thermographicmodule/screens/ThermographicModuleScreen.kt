package com.example.thermographicmodule.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thermographicmodule.R
import com.example.thermographicmodule.data.SectionCardData
import com.example.thermographicmodule.data.SectionType
import com.example.thermographicmodule.main.MainViewModel
import com.example.thermographicmodule.ui.controls.CompoundSlider
import com.example.thermographicmodule.ui.controls.JoystickPanel
import com.example.thermographicmodule.ui.controls.MessageToUser
import com.example.thermographicmodule.ui.controls.ParameterControl
import com.example.thermographicmodule.ui.controls.SectionCarousel
import com.example.thermographicmodule.ui.controls.SendButton
import com.example.thermographicmodule.ui.controls.SimpleJoystick
import com.example.thermographicmodule.ui.controls.SwitchModuleTurnOn
import com.example.thermographicmodule.ui.controls.ToggleButton
import com.example.thermographicmodule.ui.controls.ToggleContinuousButton
import com.example.thermographicmodule.ui.controls.ZoomSection
import com.example.thermographicmodule.Constants.HISTOGRAM_NAME
import com.example.thermographicmodule.Constants.GAIN_NAME
import com.example.thermographicmodule.Constants.BRIGHTNESS_NAME

private const val TAG = "ThermographicModuleScreen"

@OptIn(ExperimentalLayoutApi::class)
@Composable

fun ThermographicModuleScreen(viewModel: MainViewModel){
    val modes = viewModel.modes.collectAsState()
    val chosenSections = viewModel.sectionIsChosen.collectAsState()
    val chosenParameter = viewModel.parameterIsChosen.collectAsState()

    val focusManager = LocalFocusManager.current

    var joystickX by remember { mutableStateOf(0f) }
    var joystickY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row {
                    SwitchModuleTurnOn(
                        viewModel.isModuleOn,
                        viewModel::toggleModuleOn,
                        viewModel.messageToUser
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                HorizontalDivider(Modifier.height(10.dp))
                Text(
                    "Параметры", color = Color.LightGray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = dimensionResource(R.dimen.padding_small),
                            bottom = 4.dp
                        )
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding_small))
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RectangleShape)
                            .padding(top = 0.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            ParameterControl(
                                stringResource(R.string.histogram_name),
                                isChosen = chosenParameter.value.histogramIsChosen,
                                { viewModel.setParameterForSlider(HISTOGRAM_NAME) },
                                { viewModel.setParameterForSlider(HISTOGRAM_NAME) },
                                viewModel.histogram,
                                viewModel::setHistogram,
                                focusManager

                            )
                        }
                        item {
                            ParameterControl(
                                stringResource(R.string.brightness_name),
                                isChosen = chosenParameter.value.brightnessIsChosen,
                                { viewModel.setParameterForSlider(BRIGHTNESS_NAME) },
                                { viewModel.setParameterForSlider(BRIGHTNESS_NAME) },
                                viewModel.brightness,
                                viewModel::setBrightness,
                                focusManager
                            )
                        }
                        item {
                            ParameterControl(
                                stringResource(R.string.gain_name),
                                isChosen = chosenParameter.value.gainIsChosen,
                                { viewModel.setParameterForSlider(GAIN_NAME) },
                                { viewModel.setParameterForSlider(GAIN_NAME) },
                                viewModel.gain,
                                viewModel::setGain,
                                focusManager
                            )
                        }
                    }
                }


                CompoundSlider(
                    viewModel.currentParameterForSlider,
                    viewModel.getParameterForSlider(),
                    viewModel::onValueChangeForSlider,
                    viewModel::setDefaultParameter,
                    0f..254f
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.padding_small),
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding_small))
                ) {
                    ToggleContinuousButton(
                        viewModel.continuousSending,
                        "Непрерывная отправка: ВКЛ",
                        "Непрерывная отправка: выкл",
                        viewModel::setContinuousSendingFlag,
                        modifier = Modifier.weight(2.2f)
                    )
                    SendButton(onSend = viewModel::sendParameter, modifier = Modifier.weight(1f))
                }
                ZoomSection(viewModel.currentZoom, viewModel::setZoom)
                Spacer(modifier = Modifier.height(12.dp))
                val items = remember {
                    listOf(
                        SectionCardData(
                            "Запросы",
                            R.drawable.outline_terminal_2_24,
                            sectionType = SectionType.REQUEST
                        ),
                        SectionCardData(
                            "Поворот",
                            R.drawable.outline_flip_camera_android_24,
                            sectionType = SectionType.ROTATION
                        ),
                        SectionCardData(
                            "Зона анализа АРУ",
                            R.drawable.outline_activity_zone_24,
                            sectionType = SectionType.ANALYSIS_AREA
                        ),
                        SectionCardData(
                            "Зона масштабирования",
                            R.drawable.outline_feature_search_24,
                            sectionType = SectionType.ZOOM_AREA
                        ),
                        SectionCardData(
                            "Пользователькие параметры",
                            R.drawable.outline_settings_account_box_24,
                            sectionType = SectionType.USER_PARAMETER
                        )
                    )
                }
                Text(
                    "Разделы", color = Color.LightGray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = dimensionResource(R.dimen.padding_small))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding_small))
                ) {
                    SectionCarousel(chosenSections.value, items, viewModel::setSectionIsChosen)
                }
                Card(
                    modifier = Modifier
                        .padding(
                            start =dimensionResource(R.dimen.padding_small),
                            end = dimensionResource(R.dimen.padding_small)),
                    shape = RoundedCornerShape(
                        topEnd = 12.dp,
                        topStart = 0.dp,
                        bottomEnd = 12.dp,
                        bottomStart = 12.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    ) {
                        Text(viewModel.currentSectionName, modifier = Modifier.padding(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Другие команды", color = Color.LightGray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = dimensionResource(R.dimen.padding_small))
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                        top = dimensionResource(R.dimen.padding_small)
                    )
                ) {
                    item {
                        Button(
                            onClick = viewModel::startBlindCalibration,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.size(128.dp, 54.dp),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            colors = ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text("Калибровка по шторке")
                        }
                    }
                    item {
                        Button(
                            onClick = viewModel::startCapCalibration,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.size(128.dp, 54.dp),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            colors = ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text("Калибровка по крышке")
                        }
                    }
                    item {
                        Button(
                            onClick = viewModel::findDefectPixels,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.size(128.dp, 54.dp),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            colors = ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text("Нахождение битых пикселей")
                        }
                    }
                }
                Text(
                    "Режимы (вкл/выкл)", color = Color.LightGray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = dimensionResource(R.dimen.padding_small))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.padding_small),
                        end = dimensionResource(R.dimen.padding_small))) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
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
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(10.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painterResource(
                                R.drawable.outline_save_24
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text("Сохранение параметров")
                    }

                }
                Spacer(modifier = Modifier.height(if (viewModel.joystickIsVisible) 250.dp else 80.dp))
            }
//        }

        AnimatedVisibility(
                visible = viewModel.joystickIsVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
        JoystickPanel(
            onJoystickMove = { x, y ->
                joystickX = x
                joystickY = y
                // Отправляем координаты на управление
                println("Joystick: X=$x, Y=$y")
            }
        )
    }
    }

}