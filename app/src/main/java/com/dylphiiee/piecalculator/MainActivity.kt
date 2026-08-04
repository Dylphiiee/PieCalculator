package com.dylphiiee.piecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dylphiiee.piecalculator.ui.screens.AlgebraScreen
import com.dylphiiee.piecalculator.ui.screens.CalculatorScreen
import com.dylphiiee.piecalculator.ui.screens.CurrencyScreen
import com.dylphiiee.piecalculator.ui.theme.BackgroundDark
import com.dylphiiee.piecalculator.ui.theme.OrangeAccent
import com.dylphiiee.piecalculator.ui.theme.PieCalculatorTheme
import com.dylphiiee.piecalculator.ui.theme.SurfaceDark
import com.dylphiiee.piecalculator.ui.theme.TextSecondary

private sealed class Destination(val route: String, val label: String) {
    object Calculator : Destination("calculator", "Kalkulator")
    object Currency : Destination("currency", "Kurs")
    object Algebra : Destination("algebra", "Aljabar")
}

private val destinations = listOf(Destination.Calculator, Destination.Currency, Destination.Algebra)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PieCalculatorTheme {
                PieCalculatorApp()
            }
        }
    }
}

@Composable
fun PieCalculatorApp() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = { PieBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Calculator.route) { CalculatorScreen() }
            composable(Destination.Currency.route) { CurrencyScreen() }
            composable(Destination.Algebra.route) { AlgebraScreen() }
        }
    }
}

@Composable
private fun PieBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = SurfaceDark) {
        destinations.forEach { destination ->
            val icon = when (destination) {
                Destination.Calculator -> Icons.Default.Calculate
                Destination.Currency -> Icons.Default.CurrencyExchange
                Destination.Algebra -> Icons.Default.Functions
            }
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangeAccent,
                    selectedTextColor = OrangeAccent,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceDark
                )
            )
        }
    }
}
