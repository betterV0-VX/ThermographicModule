package com.example.thermographicmodule.data
import android.hardware.usb.UsbDevice
import android.util.Log

private const val TAG = "UsbRepository"

class UsbRepository(private val usbSerialManager: IUsbSerialManager) {

    fun findDevices(): List<UsbDevice> = usbSerialManager.findDevices()

    fun findAvailablePorts(device: UsbDevice): List<Int> = usbSerialManager.findAvailablePorts(device)

    fun connect(device: UsbDevice, portNumber: Int=0): Boolean = usbSerialManager.connectDevice(device, portNumber)

    fun disconnect() = usbSerialManager.disconnect()

    @OptIn(ExperimentalUnsignedTypes::class)
    fun sendCommand(command: String, value: Int? = null) {
        val viscaCommand = createViscaCommand(command,value )
        usbSerialManager.sendCommand(viscaCommand.toByteArray())
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun createViscaCommand(command: String, parameter: Int? =null): UByteArray {
        val uByteParameter: UByte = parameter?.toUByte() ?: 0x00u

        return when (command) {
            // === COMMANDS (управление) - таблица 1 ===
            "STREAM_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x00u, 0x01u, 0xFFu)  // p=1 On
            "STREAM_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x00u, 0x00u, 0xFFu) // p=0 Off

            "BLIND_CORRECTION_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x10u, 0x01u, 0xFFu)
            "BLIND_CORRECTION_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x10u, 0x00u, 0xFFu)

            "BLIND_CALIBRATION_START" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x14u, 0x01u, 0xFFu)
            "CAP_CALIBRATION_START" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x18u, 0x01u, 0xFFu)

            "GAIN" -> ubyteArrayOf(
                0x81u,
                0x01u,
                0x04u,
                0x20u,
                uByteParameter,
                0xFFu
            )  // pp: 0-254 default=100
            "HISTOGRAM_THRESHOLD" -> ubyteArrayOf(
                0x81u,
                0x01u,
                0x04u,
                0x24u,
                uByteParameter,
                0xFFu
            )  // pp: 0-254 default=32
            "BRIGHTNESS" -> ubyteArrayOf(
                0x81u,
                0x01u,
                0x04u,
                0x28u,
                uByteParameter,
                0xFFu
            )  // pp: 0-254 default=128

            "SAVE_PARAMETERS" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x2Fu, 0x01u, 0xFFu)
            "FIND_DEFECT_PIXELS" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x30u, 0x01u, 0xFFu)
            "ZOOM" -> ubyteArrayOf(
                0x81u,
                0x01u,
                0x04u,
                0x40u,
                uByteParameter,
                0xFFu
            )  // pp: 0=x1,1=x2,2=x4,3=x8

            // Переключатели
            "POLARITY_INVERSION_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0xD5u, 0x00u, 0xFFu)
            "POLARITY_INVERSION_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0xD5u, 0x00u, 0xFFu)
            "WAITING_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x00u, 0x02u, 0xFFu)
            "WAITING_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x00u, 0x03u, 0xFFu)
            "BINNING_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x02u, 0xFFu)
            "BINNING_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x02u, 0xFFu)
            "AUTO_BINNING_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x03u, 0xFFu)
            "AUTO_BINNING_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x03u, 0xFFu)
            "ALC_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x04u, 0xFFu)
            "ALC_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x04u, 0xFFu)
            "ALC_BORDER_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0xC8u, 0x00u, 0xFFu)
            "ALC_BORDER_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0xC8u, 0x00u, 0xFFu)
            "FPS_SLOWDOWN_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x05u, 0xFFu)
            "FPS_SLOWDOWN_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x05u, 0xFFu)
            "AUTO_FPS_SLOWDOWN_ON" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x06u, 0xFFu)
            "AUTO_FPS_SLOWDOWN_OFF" -> ubyteArrayOf(0x81u, 0x01u, 0x04u, 0x09u, 0x06u, 0xFFu)

            // === INQUIRY (запросы) - таблица 2 ===
            "INQUIRE_STREAM" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x00u, 0xFFu)
            "INQUIRE_BLIND_CORRECTION" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x10u, 0xFFu)
            "INQUIRE_GAIN" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x20u, 0xFFu)
            "INQUIRE_HISTOGRAM_THRESHOLD" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x24u, 0xFFu)
            "INQUIRE_BRIGHTNESS" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x28u, 0xFFu)
            "INQUIRE_DEFECT_PIXELS_NUMBER" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x30u, 0xFFu)
            "INQUIRE_ZOOM" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0x40u, 0xFFu)
            "INQUIRE_TABLES_STATUS" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0xD0u, 0xFFu)
            "INQUIRE_FIRMWARE_VERSION" -> ubyteArrayOf(0x81u, 0x09u, 0x04u, 0xF0u, 0xFFu)

            else -> {
                Log.e(TAG, " while createViscaCommand - $command")
                throw IllegalArgumentException("Unknown command: $command")
            }
        }
    }
}