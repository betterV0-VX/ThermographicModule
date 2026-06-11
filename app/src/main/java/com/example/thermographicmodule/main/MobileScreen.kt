package com.example.thermographicmodule.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thermographicmodule.R
import com.example.thermographicmodule.data.UsbRepository
import com.example.thermographicmodule.data.UsbSerialManager
import com.example.thermographicmodule.screens.DebugScreen
import com.example.thermographicmodule.screens.EngineSettingsScreen
import com.example.thermographicmodule.screens.ThermographicModuleScreen
import com.example.thermographicmodule.ui.theme.ThermographicModuleTheme


sealed class Screen(val route: String, val title: String, val  iconId: Int) {
    object Camera : Screen("Тепловизионный модуль", "Камера", R.drawable.camera_video_24px)
    object Motors : Screen("Двигатели", "Двигатели", R.drawable.motion_sensor_active_24px)
    object Debug : Screen("Отладка", "Отладка", R.drawable.developer_mode_tv_24px)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    val availableDevices by viewModel.availableDevices.collectAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    var showDeviceDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text=currentRoute.toString(),
                        fontSize=20.sp,
                        fontWeight= FontWeight.Bold,
                        color = if (viewModel.isModuleOn) MaterialTheme.colorScheme.primary else { MaterialTheme.colorScheme.secondary}
                    )
                },

                actions = {
                    if (currentRoute.toString() != "Отладка"){
                        if (viewModel.currentConnectedComPortNumber != -1){
                            TextButton(
                                onClick = {
                                    viewModel.refreshDevices()
                                    showDeviceDialog = true
                                },
                            ){
                                Text(
                                    "COM: ${viewModel.currentConnectedComPortNumber+1}",
                                    color = Color.Gray
                                )
                            }
                        }
                        IconButton(onClick = {
                            viewModel.refreshDevices()
                            showDeviceDialog = true
                        },
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = "Select Device",modifier = Modifier.size(30.dp))
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(Screen.Camera, Screen.Motors, Screen.Debug).forEach { screen ->
                    NavigationBarItem(
                        currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(painter = painterResource(screen.iconId), contentDescription = screen.title)
                        },
                        label = {Text(screen.title)}
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Camera.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(Screen.Camera.route) {
                ThermographicModuleScreen(viewModel)
            }
            composable(Screen.Motors.route) {
                EngineSettingsScreen()
            }
            composable(Screen.Debug.route){
                DebugScreen(viewModel)
            }
        }


    //innerPadding ->
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
////            modifier = Modifier.padding(innerPadding)
////                .fillMaxSize()
////                .padding(horizontal = 16.dp),
////            horizontalAlignment = Alignment.CenterHorizontally,
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ){
//        }
    }

    if (showDeviceDialog) {
        AlertDialog(
            onDismissRequest = { showDeviceDialog = false },
            title = { Text("Выберите COM-порт устройства") },
            text = {
                if (availableDevices.isEmpty()) {
                    Text("Устройства не найдены")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableDevices) { device ->
                            Card(
                                border = BorderStroke(width = 1.dp, color = Color.Gray),
                                modifier = Modifier.fillMaxWidth().padding(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = device.productName ?: "Неизвестное устройство",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                    viewModel.getAvailablePorts(device).forEach { port ->
                                        TextButton (onClick = {
                                            // Handle port selection if needed
                                            viewModel.connect(device, port)
                                            showDeviceDialog = false
                                        }) {
                                            Text("Подключиться к порту ${port+1}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.refreshDevices() }) {
                    Text("Обновить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeviceDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

//@Preview
//@Composable
//fun MobileScreenPreview(){
//    val usbSerialManager = UsbSerialManager(context= null)
//    val usbRepository = UsbRepository(usbSerialManager)
//    val mainViewModel = MainViewModel(usbRepository)
//    ThermographicModuleTheme {
//        MobileScreen(mainViewModel)
//    }
//}




