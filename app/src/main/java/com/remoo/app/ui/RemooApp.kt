package com.remoo.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remoo.app.ui.screens.AddDeviceScreen
import com.remoo.app.ui.screens.HomeScreen
import com.remoo.app.ui.screens.RemoteScreen

object Routes {
    const val HOME = "home"
    const val ADD_DEVICE = "add_device"
    const val REMOTE = "remote/{deviceId}"

    fun remote(deviceId: Long) = "remote/$deviceId"
}

@Composable
fun RemooApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddDevice = { navController.navigate(Routes.ADD_DEVICE) },
                onOpenRemote = { deviceId ->
                    navController.navigate(Routes.remote(deviceId))
                }
            )
        }
        composable(Routes.ADD_DEVICE) {
            AddDeviceScreen(
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.REMOTE,
            arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
        ) {
            RemoteScreen(onBack = { navController.popBackStack() })
        }
    }
}
