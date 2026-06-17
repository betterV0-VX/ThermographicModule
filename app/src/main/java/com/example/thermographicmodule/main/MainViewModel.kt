package com.example.thermographicmodule.main

import android.hardware.usb.UsbDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import com.example.thermographicmodule.Constants.DEFAULT_COM_PORT
import com.example.thermographicmodule.Constants.DEFAULT_ZOOM
import com.example.thermographicmodule.Constants.DEFAULT_HISTOGRAM
import com.example.thermographicmodule.Constants.DEFAULT_GAIN
import com.example.thermographicmodule.Constants.DEFAULT_BRIGHTNESS
import com.example.thermographicmodule.Constants.HISTOGRAM_NAME
import com.example.thermographicmodule.Constants.GAIN_NAME
import com.example.thermographicmodule.Constants.BRIGHTNESS_NAME
import com.example.thermographicmodule.Constants.DEFAULT_SECTION_NAME
import com.example.thermographicmodule.data.UsbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.thermographicmodule.data.ModeUiState
import com.example.thermographicmodule.data.ParameterIsChosen
import com.example.thermographicmodule.data.SectionIsChosen
import com.example.thermographicmodule.data.SectionType
import kotlinx.coroutines.flow.update


private const val TAG = "MainViewModel"

class MainViewModel(
    private val usbRepository: UsbRepository,
) : ViewModel() {

    var messageToUser by mutableStateOf("COM-порт не выбран.\nИнтерфейс заблокирован.")

    var joystickIsVisible by mutableStateOf(false)
    var isModuleOn by mutableStateOf(false)
    var currentZoom by mutableIntStateOf(DEFAULT_ZOOM)
    var gain by mutableIntStateOf(DEFAULT_GAIN)
    var histogram by mutableIntStateOf(DEFAULT_HISTOGRAM)
    var brightness by mutableIntStateOf(DEFAULT_BRIGHTNESS)

    var currentParameterForSlider by mutableStateOf(HISTOGRAM_NAME)
    var continuousSending by mutableStateOf(false)

    var currentSectionName by mutableStateOf(DEFAULT_SECTION_NAME)

    private val _modeUiState = MutableStateFlow(ModeUiState())
    val modes: StateFlow<ModeUiState> = _modeUiState.asStateFlow()

    private val _parameterIsChosen = MutableStateFlow(ParameterIsChosen())
    val parameterIsChosen: StateFlow<ParameterIsChosen> = _parameterIsChosen.asStateFlow()

    private val _sectionIsChosen = MutableStateFlow(SectionIsChosen())
    val sectionIsChosen: StateFlow<SectionIsChosen> = _sectionIsChosen.asStateFlow()

    var log by mutableStateOf("")
        private set

    var currentConnectedComPortNumber by mutableIntStateOf(DEFAULT_COM_PORT)
        private set

    private val _availableDevices = MutableStateFlow<List<UsbDevice>>(emptyList())
    val availableDevices: StateFlow<List<UsbDevice>> = _availableDevices.asStateFlow()

    fun refreshDevices(){
        _availableDevices.value = usbRepository.findDevices()
    }

    fun connect(device: UsbDevice, portNumber: Int=0){
        val connectionResult = usbRepository.connect(device, portNumber)
        if (connectionResult) {
            currentConnectedComPortNumber = portNumber
            log += "${Date().formatTime()} : Connected to ${device.productName}, COM-port: ${portNumber+1} \n"
        }

    }

    fun disconnect(){
        usbRepository.disconnect()
        currentConnectedComPortNumber = -1
        messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
    }

    fun Date.formatTime(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(this)

    fun setContinuousSendingFlag(value: Boolean) {
        continuousSending = value
        log += "${Date().formatTime()} : Continuous sending is ${if (value) "ON" else "OFF"}\n"
        Log.i(TAG, "setContinuousSending called. New value: $value")
    }

    fun setParameterForSlider(parameterName: String) {
        currentParameterForSlider = parameterName
        _parameterIsChosen.update { parameterState ->
            parameterState.copy(
                histogramIsChosen = parameterName == HISTOGRAM_NAME,
                brightnessIsChosen = parameterName == BRIGHTNESS_NAME,
                gainIsChosen = parameterName == GAIN_NAME,
            )
        }
    }

    fun getParameterForSlider(): Int {
        when (currentParameterForSlider) {
            HISTOGRAM_NAME -> {
                return histogram
            }
            BRIGHTNESS_NAME -> {
                return brightness
            }
            GAIN_NAME -> {
                return gain
            }
        }
        return 0
    }

    fun onValueChangeForSlider(value: Float) {
        when (currentParameterForSlider) {
            HISTOGRAM_NAME -> {
                histogram = value.toInt()
                if (continuousSending)
                    sendHistogram()
            }
            BRIGHTNESS_NAME -> {
                brightness = value.toInt()
                if (continuousSending)
                    sendBrightness()
            }
            GAIN_NAME -> {
                gain = value.toInt()
                if (continuousSending)
                    sendGain()
            }
        }
    }

    fun sendParameter(){
        when (currentParameterForSlider) {
            HISTOGRAM_NAME -> {
                sendHistogram()
            }
            BRIGHTNESS_NAME -> {
                sendBrightness()
            }
            GAIN_NAME -> {
                sendGain()
            }
        }
    }

    fun setSectionIsChosen(selectedSectionType: SectionType) {
        _sectionIsChosen.update { sectionState ->
            val chosenSection = when (selectedSectionType) {
                SectionType.REQUEST -> "Запросы"
                SectionType.ROTATION -> "Поворот"
                SectionType.ANALYSIS_AREA -> "Зона анализа АРУ"
                SectionType.ZOOM_AREA -> "Зона масштабирования"
                SectionType.USER_PARAMETER -> "Пользовательские параметры"
            }
            currentSectionName = chosenSection

            sectionState.copy(
                requestIsChosen = selectedSectionType == SectionType.REQUEST,
                rotationIsChosen = selectedSectionType == SectionType.ROTATION,
                analysisAreaIsChosen = selectedSectionType == SectionType.ANALYSIS_AREA,
                zoomAreaIsChosen = selectedSectionType == SectionType.ZOOM_AREA,
                userParameterIsChosen = selectedSectionType == SectionType.USER_PARAMETER
            )
        }
    }

    fun toggleJoystickIsVisible(){
        joystickIsVisible = !joystickIsVisible
    }

    // Режимы и переключатели
    fun toggleWaiting(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Waiting is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendWaitingOn()
        } else {
            sendWaitingOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(waiting = value)
            }
        }
    }
    fun sendWaitingOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("WAITING_ON")
        log += "${Date().formatTime()} : Command WAITING_ON send\n"
        return true
    }
    fun sendWaitingOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("WAITING_OFF")
        log += "${Date().formatTime()} : Command WAITING_OFF send\n"
        return true
    }

    fun toggleBinning(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Binning is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendBinningOn()
        } else {
            sendBinningOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(binning = value)
            }
        }
    }
    fun sendBinningOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("BINNING_ON")
        log += "${Date().formatTime()} : Command BINNING_ON send\n"
        return true
    }
    fun sendBinningOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("BINNING_OFF")
        log += "${Date().formatTime()} : Command BINNING_OFF send\n"
        return true
    }

    fun toggleAutoBinning(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Auto binning is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendAutoBinningOn()
        } else {
            sendAutoBinningOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(autoBinning = value)
            }
        }
    }
    fun sendAutoBinningOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("AUTO_BINNING_ON")
        log += "${Date().formatTime()} : Command AUTO_BINNING_ON send\n"
        return true
    }
    fun sendAutoBinningOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("AUTO_BINNING_OFF")
        log += "${Date().formatTime()} : Command AUTO_BINNING_OFF send\n"
        return true
    }

    fun toggleAlc(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : ALC is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendAlcOn()
        } else {
            sendAlcOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(alc = value)
            }
        }
    }
    fun sendAlcOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("ALC_ON")
        log += "${Date().formatTime()} : Command ALC_ON send\n"
        return true
    }
    fun sendAlcOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("ALC_OFF")
        log += "${Date().formatTime()} : Command ALC_OFF send\n"
        return true
    }

    fun toggleAlcBorder(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : ALC border is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendAlcBorderOn()
        } else {
            sendAlcBorderOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(alcBorder = value)
            }
        }
    }
    fun sendAlcBorderOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("ALC_BORDER_ON")
        log += "${Date().formatTime()} : Command ALC_BORDER_ON send\n"
        return true
    }
    fun sendAlcBorderOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("ALC_BORDER_OFF")
        log += "${Date().formatTime()} : Command ALC_BORDER_OFF send\n"
        return true
    }

    fun toggleFpsSlowdown(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : FPS slowdown is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendFpsSlowdownOn()
        } else {
            sendFpsSlowdownOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(fpsSlowdown = value)
            }
        }
    }
    fun sendFpsSlowdownOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("FPS_SLOWDOWN_ON")
        log += "${Date().formatTime()} : Command FPS_SLOWDOWN_ON send\n"
        return true
    }
    fun sendFpsSlowdownOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("FPS_SLOWDOWN_OFF")
        log += "${Date().formatTime()} : Command FPS_SLOWDOWN_OFF send\n"
        return true
    }

    fun toggleAutoFpsSlowdown(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Auto FPS slowdown is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendAutoFpsSlowdownOn()
        } else {
            sendAutoFpsSlowdownOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(autoFpsSlowdown = value)
            }
        }
    }
    fun sendAutoFpsSlowdownOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("AUTO_FPS_SLOWDOWN_ON")
        log += "${Date().formatTime()} : Command AUTO_FPS_SLOWDOWN_ON send\n"
        return true
    }
    fun sendAutoFpsSlowdownOff(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("AUTO_FPS_SLOWDOWN_OFF")
        log += "${Date().formatTime()} : Command AUTO_FPS_SLOWDOWN_OFF send\n"
        return true
    }

    fun togglePolarityInversion(value: Boolean) {
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Polarity inversion is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendPolarityInversionOn()
        } else {
            sendPolarityInversionOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(polarityInversion = value)
            }
        }
    }
    fun sendPolarityInversionOn(): Boolean {
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("POLARITY_INVERSION_ON")
        log += "${Date().formatTime()} : Command POLARITY_INVERSION_ON send\n"
        return true
    }
    fun sendPolarityInversionOff(): Boolean{
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("POLARITY_INVERSION_OFF")
        log += "${Date().formatTime()} : Command POLARITY_INVERSION_OFF send\n"
        return true
    }

    fun toggleCorrection(value: Boolean){
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Correction is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        val commandWasSent: Boolean = if (value){
            sendCorrectionOn()
        } else {
            sendCorrectionOff()
        }
        if (commandWasSent) {
            _modeUiState.update { currentMode ->
                currentMode.copy(correction = value)
            }
        }
    }
    fun sendCorrectionOn(): Boolean{
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("BLIND_CORRECTION_ON")
        log += "${Date().formatTime()} : Command BLIND_CORRECTION_ON send\n"
        return true
    }
    fun sendCorrectionOff(): Boolean{
        if (!logIsComPortConnected())
            return false
        usbRepository.sendCommand("BLIND_CORRECTION_OFF")
        log += "${Date().formatTime()} : Command BLIND_CORRECTION_OFF send\n"
        return true
    }


    // Режимы и переключатели
    // __________________________________________________________________

    fun clearLog(){
        log = ""
    }

    fun logIsComPortConnected(): Boolean {
        if (!isComPortConnected()) {
            log += "${Date().formatTime()} : Com-port was not set\n"
            return false
        }
        return true
    }

    fun isComPortConnected(): Boolean {
        return (currentConnectedComPortNumber != -1)
    }

    fun getAvailablePorts(device: UsbDevice): List<Int> {
        return usbRepository.findAvailablePorts(device)
    }

    fun toggleModuleOn(value: Boolean? = null) {
        if (currentConnectedComPortNumber == -1) {
            log += "${Date().formatTime()} : Module On/off is disabled. COM-port not selected\n"
            messageToUser = "COM-порт не выбран.\nИнтерфейс заблокирован."
            return
        }
        isModuleOn = !isModuleOn
        if (isModuleOn){
            sendStreamOn()
            messageToUser = ""
        } else {
            sendStreamOff()
            messageToUser = ""
        }
    }

    fun sendStreamOn(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("STREAM_ON")
        log += "${Date().formatTime()} : Command STREAM_ON send\n"
    }
    fun sendStreamOff(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("STREAM_OFF")
        log += "${Date().formatTime()} : Command STREAM_OFF send\n"
    }

    fun setZoom(zoom: Int){
        if (!logIsComPortConnected())
            return

        currentZoom = zoom
        if (zoom in listOf(1, 2, 4, 8)){      // 0, 1, 2, 3
            val zoomTransformed =  when(zoom){
                1->0
                2->1
                4->2
                8->3
                else -> -1
            }
            usbRepository.sendCommand("ZOOM", zoomTransformed)
            log += "${Date().formatTime()} : Command ZOOM x$zoom send\n"
        } else {
            Log.e(TAG, "setZoom error")
        }
    }

    fun setDefaultParameter(){
        when (currentParameterForSlider) {
            HISTOGRAM_NAME -> {
                setDefaultHistogram()
            }
            BRIGHTNESS_NAME -> {
                setDefaultBrightness()
            }
            GAIN_NAME -> {
                setDefaultGain()
            }
        }
    }

    fun setDefaultBrightness(){
        brightness = 128
        Log.e(TAG, "Default brightness value is set")
    }

    fun setBrightness(value: String){
        val valueInt = value.toIntOrNull() ?: 0
        if (valueInt in 0..254){
            setParameterForSlider(BRIGHTNESS_NAME)
            brightness = valueInt
        } else {
            Log.e(TAG, "setBrightness error (value out of bounds)")
        }
    }

    fun setDefaultGain(){
        gain = 100
        Log.e(TAG, "Default gain value is set")
    }

    fun setGain(value: String){
        val valueInt = value.toIntOrNull() ?: 0
        if (valueInt in 0..254){
            setParameterForSlider(GAIN_NAME)
            gain = valueInt
        } else {
            Log.e(TAG, "setGain error (value out of bounds)")
        }
    }

    fun setDefaultHistogram(){
        histogram = 32
        Log.e(TAG, "Default histogram value is set")
    }

    fun setHistogram(value: String){
        val valueInt = value.toIntOrNull() ?: 0
        if (valueInt in 0..254){
            setParameterForSlider(HISTOGRAM_NAME)
            histogram = valueInt
        } else {
            Log.e(TAG, "setHistogram error (value out of bounds)")
        }
    }

    fun sendHistogram(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("HISTOGRAM_THRESHOLD", histogram)
        log += "${Date().formatTime()} : Command HISTOGRAM_THRESHOLD $histogram send\n"
    }

    fun sendGain(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("GAIN", gain)
        log += "${Date().formatTime()} : Command GAIN $gain send\n"
    }

    fun sendBrightness(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("BRIGHTNESS", brightness)
        log += "${Date().formatTime()} : Command BRIGHTNESS $brightness send\n"
    }

    fun findDefectPixels(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("FIND_DEFECT_PIXELS")
        log += "${Date().formatTime()} : Command FIND_DEFECT_PIXELS send\n"
    }

    fun saveParametersToHardware(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("SAVE_PARAMETERS")
        log += "${Date().formatTime()} : Command SAVE_PARAMETERS send\n"
    }

    fun startCapCalibration(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("CAP_CALIBRATION_START")
        log += "${Date().formatTime()} : Command CAP_CALIBRATION_START send\n"
    }

    fun startBlindCalibration(){
        if (!logIsComPortConnected())
            return
        usbRepository.sendCommand("BLIND_CALIBRATION_START")
        log += "${Date().formatTime()} : Command BLIND_CALIBRATION_START send\n"
    }

}
