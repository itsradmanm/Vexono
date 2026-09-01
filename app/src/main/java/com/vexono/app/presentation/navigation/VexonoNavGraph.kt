package com.vexono.app.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vexono.app.di.AppContainer
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.presentation.screens.calendar.CalendarScreen
import com.vexono.app.presentation.screens.daydetail.DayDetailScreen
import com.vexono.app.presentation.screens.event_editor.EventEditorScreen
import com.vexono.app.presentation.screens.occasions.OccasionsScreen
import com.vexono.app.presentation.screens.settings.SettingsScreen
import com.vexono.app.presentation.screens.splash.SplashScreen
import com.vexono.app.presentation.screens.tasks.TasksScreen
import com.vexono.app.presentation.viewmodel.CalendarViewModel
import com.vexono.app.presentation.viewmodel.DayDetailViewModel
import com.vexono.app.presentation.viewmodel.EventEditorViewModel
import com.vexono.app.presentation.viewmodel.OccasionsViewModel
import com.vexono.app.presentation.viewmodel.SettingsViewModel
import com.vexono.app.presentation.viewmodel.TasksViewModel
import com.vexono.app.presentation.viewmodel.ViewModelFactory

@Composable
fun VexonoNavGraph(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = currentRoute in listOf(
        Screen.Calendar.route,
        Screen.Tasks.route,
        Screen.Occasions.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        BottomNavItem.entries.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Calendar Home Screen
            composable(Screen.Calendar.route) {
                val calendarViewModel: CalendarViewModel = viewModel(
                    factory = ViewModelFactory(container)
                )
                CalendarScreen(
                    viewModel = calendarViewModel,
                    onDayDetailRequested = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date.year, date.month, date.day))
                    },
                    onAddEventRequested = { date ->
                        navController.navigate(Screen.EventEditor.createRoute(0L, date.year, date.month, date.day))
                    },
                    onAddTaskRequested = { date ->
                        navController.navigate(Screen.Tasks.route)
                    }
                )
            }

            // 3. Day Detail Screen
            composable(
                route = Screen.DayDetail.route,
                arguments = listOf(
                    navArgument("year") { type = NavType.IntType },
                    navArgument("month") { type = NavType.IntType },
                    navArgument("day") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: 1403
                val month = backStackEntry.arguments?.getInt("month") ?: 1
                val day = backStackEntry.arguments?.getInt("day") ?: 1
                val selectedDate = JalaliDate(year, month, day)

                val dayDetailViewModel: DayDetailViewModel = viewModel(
                    factory = ViewModelFactory(container, selectedDate)
                )

                DayDetailScreen(
                    viewModel = dayDetailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onAddEventRequested = { date ->
                        navController.navigate(Screen.EventEditor.createRoute(0L, date.year, date.month, date.day))
                    },
                    onEditEventRequested = { eventId ->
                        navController.navigate(Screen.EventEditor.createRoute(eventId, 0, 0, 0))
                    },
                    onAddTaskRequested = { date ->
                        navController.navigate(Screen.Tasks.route)
                    }
                )
            }

            // 4. Event Editor Screen
            composable(
                route = Screen.EventEditor.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.LongType; defaultValue = 0L },
                    navArgument("year") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("month") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("day") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId")?.takeIf { it > 0 }
                val year = backStackEntry.arguments?.getInt("year") ?: 0
                val month = backStackEntry.arguments?.getInt("month") ?: 0
                val day = backStackEntry.arguments?.getInt("day") ?: 0
                val initialDate = if (year > 0 && month > 0 && day > 0) JalaliDate(year, month, day) else null

                val eventEditorViewModel: EventEditorViewModel = viewModel(
                    factory = ViewModelFactory(container, Pair(eventId, initialDate))
                )

                EventEditorScreen(
                    viewModel = eventEditorViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 5. Tasks Screen
            composable(Screen.Tasks.route) {
                val tasksViewModel: TasksViewModel = viewModel(
                    factory = ViewModelFactory(container)
                )
                TasksScreen(viewModel = tasksViewModel)
            }

            // 6. Occasions Screen
            composable(Screen.Occasions.route) {
                val occasionsViewModel: OccasionsViewModel = viewModel(
                    factory = ViewModelFactory(container)
                )
                OccasionsScreen(viewModel = occasionsViewModel)
            }

            // 7. Settings Screen
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = ViewModelFactory(container)
                )
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
