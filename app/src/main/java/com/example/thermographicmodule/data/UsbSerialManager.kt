package com.example.thermographicmodule.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.IOException


interface IUsbSerialManager {
    fun connectDevice(device: UsbDevice, portNumber: Int = 0): Boolean
    fun disconnect()
    fun sendCommand(command: ByteArray)
    fun findDevices(): List<UsbDevice>
    fun findAvailablePorts(device: UsbDevice): List<Int>
}

private const val TAG = "UsbSerialManager"

class UsbSerialManager(private val context: Context) : IUsbSerialManager {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private var usbSerialPort: UsbSerialPort? = null
    private var usbIoManager: SerialInputOutputManager? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    private val ACTION_USB_PERMISSION = "${context.packageName}.USB_PERMISSION"
    private lateinit var permissionIntent: PendingIntent

    private val permissionReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                if (granted) {
                    // Разрешение получено
                    device?.let { connectDevice(it) }
                }
            }
        }
    }

    init {
        permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            context,
            permissionReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }



    fun requestDevicePermission(device: UsbDevice) {
        if (!usbManager.hasPermission(device)) {
            usbManager.requestPermission(device, permissionIntent)
        }
    }


    override fun connectDevice(device: UsbDevice, portNumber: Int): Boolean {

        requestDevicePermission(device)
        if (!usbManager.hasPermission(device)){
            Log.d(TAG, "No permission was given")
            return false
        }

        val driver = UsbSerialProber.getDefaultProber().probeDevice(device) ?: return false
        val connection = usbManager.openDevice(driver.device) ?: return false

        val port = driver.ports[portNumber]
        try {
            port.open(connection)
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1,UsbSerialPort.PARITY_NONE)
            usbSerialPort = port
//            usbIoManager = SerialInputOutputManager(port, usbListener)
            Log.d(TAG, "Connected to ${device.deviceName}, port: $port")
            return true
        } catch (e: IOException){
            Log.e(TAG, "Error opening port", e)
            disconnect()
            return false
        }
    }

//    private fun createPermissionIntent(): PendingIntent {
//        return PendingIntent.getBroadcast(
//            applicationContext,
//            0,
//            Intent(ACTION_USB_PERMISSION),
//        )
//    }
    override fun disconnect() {
        try {
            usbSerialPort?.close()
        } catch (e: IOException){
            Log.e(TAG, "Error closing port $usbSerialPort", e)
        }
        usbSerialPort = null
    }

    override fun sendCommand(command: ByteArray){
        try {
            usbSerialPort?.let { port->
                port.write(command, 200)
                Log.i(TAG, "Байты отправлены: ${command.joinToString(" ") { "%02X".format(it) }}")
            }
        } catch (e: IOException){
            Log.e(TAG, "Error sending command", e)
        }

    }

    override fun findDevices(): List<UsbDevice> {
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        return drivers.map { it.device }
    }

    override fun findAvailablePorts(device: UsbDevice): List<Int> {
        val driver = UsbSerialProber.getDefaultProber().probeDevice(device) ?: return emptyList()
        val portsNumber = driver.ports.size
        return if (portsNumber > 0) {
            (0 until portsNumber).toList()
        } else {
            emptyList()
        }
    }


}