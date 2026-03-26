package com.flynnd273.activitytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.flynnd273.activitytracker.ui.screens.ActivityScreen
import com.flynnd273.activitytracker.ui.screens.HomeScreen
import com.flynnd273.activitytracker.ui.theme.ActivityTrackerTheme

@Composable
fun App(viewModel: SharedViewModel) {
    val activities by viewModel.activities.collectAsState()
    val navController = rememberNavController()
    ActivityTrackerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {}
            NavHost(navController, startDestination = HomeScreenRoute) {
                composable<HomeScreenRoute> {
                    HomeScreen(activities, { navController.navigate(ActivityScreenRoute(it)) })
                }
                composable<ActivityScreenRoute> {
                    val activity: ActivityScreenRoute = it.toRoute()
                    ActivityScreen(viewModel, activity.uid)
                }
            }
        }
    }
}
