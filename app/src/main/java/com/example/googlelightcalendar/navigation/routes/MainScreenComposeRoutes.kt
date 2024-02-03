package com.example.googlelightcalendar.navigation.routes

import androidx.compose.material3.Scaffold
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.googlelightcalendar.core.toolBarStates.ToolBarState
import com.example.googlelightcalendar.navigation.components.BottomNavBarDestinations
import com.example.googlelightcalendar.screens.food_diary.DiaryScreen
import com.example.googlelightcalendar.screens.profile.ProfileScreen
import com.example.googlelightcalendar.ui_components.ScreenUnavailable
import com.example.googlelightcalendar.ui_components.toolbar.ChooseUToolBar

fun NavGraphBuilder.MainScreenRoutes(
) {
    composable(
        route = BottomNavBarDestinations.Home.destination
    ) {
        Scaffold(
            topBar = {
                ChooseUToolBar(
                    toolBarState = ToolBarState.Home(),
                    navigateBack = { /*TODO*/ },
                    navigateToActionDestination = {}
                )
            }
        ) { it ->
            ScreenUnavailable()
        }
    }

    composable(
        route = BottomNavBarDestinations.Diary.destination
    ) {
        DiaryScreen()
    }

    composable(
        route = BottomNavBarDestinations.Calendar.destination
    ) {
        Scaffold(
            topBar = {
                ChooseUToolBar(
                    toolBarState = ToolBarState.Home(),
                    navigateBack = { /*TODO*/ },
                    navigateToActionDestination = {}
                )
            }
        ) { it ->
            ScreenUnavailable()
        }
    }

    composable(
        route = BottomNavBarDestinations.Profile.destination
    ) {
        ProfileScreen()
    }

    ProfileRoutes()
}