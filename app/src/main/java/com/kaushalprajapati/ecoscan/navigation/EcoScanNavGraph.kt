package com.kaushalprajapati.ecoscan.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaushalprajapati.ecoscan.EcoScanApp
import com.kaushalprajapati.ecoscan.ui.screens.HistoryScreen
import com.kaushalprajapati.ecoscan.ui.screens.HomeScreen
import com.kaushalprajapati.ecoscan.ui.screens.NotFoundScreen
import com.kaushalprajapati.ecoscan.ui.screens.ResultScreen
import com.kaushalprajapati.ecoscan.ui.screens.ScannerScreen
import com.kaushalprajapati.ecoscan.ui.screens.profile.AboutScreen
import com.kaushalprajapati.ecoscan.ui.screens.profile.AuthViewModel
import com.kaushalprajapati.ecoscan.ui.screens.profile.HelpSupportScreen
import com.kaushalprajapati.ecoscan.ui.screens.profile.ProfileScreen
import com.kaushalprajapati.ecoscan.viewmodel.ScanUiState
import com.kaushalprajapati.ecoscan.viewmodel.ScanViewModel


@Composable
fun EcoScanNavGraph(viewModel: ScanViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()
    val scanCount by viewModel.scanCount.collectAsState()
    val averageScore by viewModel.averageScore.collectAsState()

    val bottomNavRoutes = setOf(Destination.Home.route, Destination.History.route, Destination.Profile.route)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.hierarchy?.firstOrNull()?.route
            if (currentRoute in bottomNavRoutes) {
                TrendyBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    scanCount = scanCount,
                    averageScore = averageScore,
                    recentHistory = history,
                    onStartScan = {
                        viewModel.resetScan()
                        navController.navigate(Destination.Scanner.route)
                    }
                )
            }

            composable(Destination.Scanner.route) {
                ScannerScreen(
                    sampleBarcodes = viewModel.sampleBarcodes(),
                    onBarcodeDetected = { barcode ->
                        viewModel.onBarcodeDetected(barcode)
                    },
                    onUseSampleBarcode = { barcode ->
                        viewModel.onManualBarcodeEntered(barcode)
                    }
                )

                // React to state changes produced by the scan.
                LaunchedEffect(uiState) {
                    when (val state = uiState) {
                        is ScanUiState.Success -> navController.navigate(Destination.Result.route) {
                            launchSingleTop = true
                        }
                        is ScanUiState.NotFound -> navController.navigate(Destination.NotFound.route) {
                            launchSingleTop = true
                        }
                        is ScanUiState.Loading -> {
                            // Optional: show a loading dialog or overlay
                        }
                        is ScanUiState.Error -> {
                            // Handle error state
                        }
                        else -> Unit
                    }
                }
            }

            composable(Destination.Result.route) {
                val state = uiState
                if (state is ScanUiState.Success) {
                    ResultScreen(
                        product = state.product,
                        score = state.score,
                        onScanAnother = {
                            viewModel.resetScan()
                            navController.popBackStack(Destination.Scanner.route, inclusive = false)
                        },
                        onDone = {
                            viewModel.resetScan()
                            navController.popBackStack(Destination.Home.route, inclusive = false)
                        }
                    )
                }
            }

            composable(Destination.NotFound.route) {
                val state = uiState
                val barcode = (state as? ScanUiState.NotFound)?.barcode.orEmpty()
                NotFoundScreen(
                    barcode = barcode,
                    onTryAgain = {
                        viewModel.resetScan()
                        navController.popBackStack(Destination.Scanner.route, inclusive = false)
                    }
                )
            }

            composable(Destination.History.route) {
                HistoryScreen(
                    history = history,
                    onDeleteItem = { viewModel.deleteHistoryItem(it) },
                    onClearAll = { viewModel.clearHistory() }
                )
            }

            composable(Destination.Profile.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val app = context.applicationContext as EcoScanApp
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(app.userPreferences))
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val name by authViewModel.userName.collectAsState()
                val email by authViewModel.userEmail.collectAsState()
                val profilePictureUri by authViewModel.profilePictureUri.collectAsState()
                val notificationsEnabled by authViewModel.notificationsEnabled.collectAsState()
                val darkModeEnabled by authViewModel.darkModeEnabled.collectAsState()

                ProfileScreen(
                    isLoggedIn = isLoggedIn,
                    userName = name,
                    userEmail = email,
                    profilePictureUri = profilePictureUri,
                    notificationsEnabled = notificationsEnabled,
                    darkModeEnabled = darkModeEnabled,
                    scanCount = scanCount,
                    averageScore = averageScore,
                    onLogin = { authViewModel.login(it) },
                    onRegister = { n, e -> authViewModel.register(n, e) },
                    onLogout = { authViewModel.logout() },
                    onToggleNotifications = { authViewModel.toggleNotifications(it) },
                    onToggleDarkMode = { authViewModel.toggleDarkMode(it) },
                    onUpdateProfile = { n, e, p -> authViewModel.updateProfile(n, e, p) },
                    onHelpSupportClick = { navController.navigate(Destination.HelpSupport.route) },
                    onAboutAppClick = {navController.navigate(Destination.About.route)}
                )
            }

            composable(Destination.About.route){
                AboutScreen(onBack = { navController.popBackStack() })
            }

            composable(Destination.HelpSupport.route) {
                HelpSupportScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

/**
 * A floating, pill-shaped bottom navigation bar in the "trending" style seen in
 * modern apps: rounded floating container with soft shadow, an animated pill
 * background that slides/fades behind the selected item, a bouncy icon scale,
 * and a label that only appears for the active tab.
 */
@Composable
private fun TrendyBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = remember {
        listOf(
            TrendyNavItem(
                route = Destination.Home.route,
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            TrendyNavItem(
                route = Destination.History.route,
                label = "History",
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History
            ),
            TrendyNavItem(
                route = Destination.Profile.route,
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(68.dp),
        shape = RoundedCornerShape(34.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                TrendyNavBarItem(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

private data class TrendyNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun TrendyNavBarItem(
    item: TrendyNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val pillColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 280),
        label = "navPillColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 280),
        label = "navContentColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "navIconScale"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(pillColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (selected) 18.dp else 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier
                .size(22.dp)
                .scale(iconScale)
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(220)),
            exit = fadeOut(tween(150)) + shrinkHorizontally(tween(150))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.label,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}