package com.example.googlelightcalendar.ui.screens.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.googlelightcalendar.core.main_screen.BottomNavViewModel
import com.example.googlelightcalendar.navigation.navgraphs.MainScreenRoutes
import com.example.googlelightcalendar.ui.theme.appColor
import com.example.googlelightcalendar.ui_components.bottomBar.ChooseUBottomBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(
    userId: String,
    vm: BottomNavViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(key1 = userId){
        vm.resetNavBarTab()
    }

    LaunchedEffect(
        key1 = vm.navigationManager.navigationState,
    ) {
        vm.navigationManager.navigationState.collectLatest { navDirection ->
            navController.popBackStack()
            navController.navigate(navDirection.destination) {
                this.launchSingleTop = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.background(
            color = appColor,
        ),
        bottomBar = {
            ChooseUBottomBar(
                tabs = vm.navigationsTabs,
                tabPosition = vm.selectedOption,
                onClick = { item ->
                    vm.navigate(
                        item,
                        arguments = mapOf(
                            "userID" to userId
                        )
                    )
                }
            )
        }) { innerPadding ->
        NavHost(
            modifier = Modifier
                .padding(innerPadding)
                .background(
                    // to prevent flicker while transitioning
                    color = appColor
                ),
            navController = navController,
            startDestination = vm.navigationsTabs.get(0).destination
        ) {
            MainScreenRoutes()
        }
    }
}