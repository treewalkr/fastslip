package com.xdev.fastslip

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xdev.fastslip.screens.camera.CameraScreen
import com.xdev.fastslip.screens.detail.DetailScreen
import com.xdev.fastslip.screens.home.HomeScreen
import com.xdev.fastslip.screens.list.ListScreen
import kotlinx.serialization.Serializable


@Serializable
object HomeDestination

@Serializable
object ScanQrCodeDestination

@Serializable
object CameraDestination

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = HomeDestination) {
                composable<HomeDestination> {
                    HomeScreen(
                        navigateToScanQr = {
                            navController.navigate(CameraDestination)
                        }, navigateToHome = {
                            navController.navigate(HomeDestination)
                        })
                }
                composable<CameraDestination> {
                    CameraScreen(
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}