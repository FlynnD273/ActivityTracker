package com.flynnd273.activitytracker

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.flynnd273.activitytracker.ui.screens.ActivityScreen
import com.flynnd273.activitytracker.ui.screens.HomeScreen
import com.flynnd273.activitytracker.ui.screens.SettingsScreen

@Composable
fun App(viewModel: SharedViewModel) {
    val activities by viewModel.activities.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val uri = viewModel.appContext.getString(R.string.app_uri)
    val transitionLength = 200

    SharedTransitionLayout {
        Scaffold(bottomBar = {
            val isHome = navBackStackEntry?.destination?.hasRoute<HomeScreenRoute>() == true
            val isSettings = navBackStackEntry?.destination?.hasRoute<SettingsScreenRoute>() == true
            val shouldDisplayBottomBar = isHome || isSettings

            val density = LocalDensity.current
            var barHeight by remember { mutableStateOf(0.dp) }
            val offset by animateDpAsState(
                targetValue = if (shouldDisplayBottomBar) 0.dp else barHeight,
                animationSpec = tween(
                    durationMillis = 400
                ),
                label = "bottomBarOffset"
            )

//                if (offset < barHeight) {
            NavigationBar(
                modifier = Modifier
                    .onSizeChanged {
                        barHeight = with(density) { it.height.toDp() }
                    }
                    .offset(y = offset)) {
                NavigationBarItem(
                    selected = isHome,
                    onClick = { navController.navigate(HomeScreenRoute) },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            "Home"
                        )
                    },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = isSettings,
                    onClick = { navController.navigate(SettingsScreenRoute) },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            "Settings"
                        )
                    },
                    label = { Text("Settings") }
                )
//                    }
            }
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavHost(navController, startDestination = HomeScreenRoute) {
                    composable<HomeScreenRoute>(
                        deepLinks = listOf(
                            navDeepLink<ActivityScreenRoute>(basePath = uri)
                        ),
                        enterTransition = {
                            fadeIn(animationSpec = tween(transitionLength))
                        },
                        exitTransition = {
                            fadeOut(animationSpec = tween(transitionLength))
                        },
                        popEnterTransition = {
                            fadeIn(animationSpec = tween(transitionLength))
                        },
                        popExitTransition = {
                            fadeOut(animationSpec = tween(transitionLength))
                        }
                    ) {
                        HomeScreen(
                            activities,
                            { viewModel.createNewActivity(it) },
                            { navController.navigate(ActivityScreenRoute(it)) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                    composable<ActivityScreenRoute>(
                        deepLinks = listOf(
                            navDeepLink<ActivityScreenRoute>(basePath = "${uri}activity")
                        ),
                        enterTransition = {
                            fadeIn(animationSpec = tween(transitionLength))
                        },
                        exitTransition = {
                            fadeOut(animationSpec = tween(transitionLength))
                        },
                        popEnterTransition = {
                            fadeIn(animationSpec = tween(transitionLength))
                        },
                        popExitTransition = {
                            fadeOut(animationSpec = tween(transitionLength))
                        }
                    ) {
                        val activity: ActivityScreenRoute = it.toRoute()
                        ActivityScreen(
                            { viewModel.loadActivity(it) },
                            { viewModel.updateActivity(it) },
                            { viewModel.deleteActivity(it) },
                            activity.uid,
                            { navController.navigate(HomeScreenRoute) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                    composable<SettingsScreenRoute>(
                        deepLinks = listOf(
                            navDeepLink<SettingsScreenRoute>(basePath = "${uri}settings")
                        ),
                        enterTransition = {
                            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(transitionLength))
                        },
                        exitTransition = {
                            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(transitionLength))
                        },
                        popEnterTransition = {
                            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(transitionLength))
                        },
                        popExitTransition = {
                            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(transitionLength))
                        }
                    ) {
                        SettingsScreen(
                            viewModel.reminderTime,
                            { viewModel.setReminderTime(it) },
                            { activities.forEach { viewModel.updateActivity(it.toReset()) } },
                            activities.any { it.progress > 0 || it.lastStart != null }
                        )
                    }
                }
            }
        }
    }
}
