package ca.boomerconx.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.boomerconx.feature.accessibility.ui.AccessibilitySettingsScreen
import ca.boomerconx.feature.backup.ui.BackupDashboardScreen
import ca.boomerconx.feature.home.HomeScreen
import ca.boomerconx.feature.home.HomeViewModel
import ca.boomerconx.feature.ice.ui.IceScreen
import ca.boomerconx.feature.onboarding.OnboardingScreen
import ca.boomerconx.feature.scamshield.ui.ScamShieldScreen
import ca.boomerconx.feature.vault.ui.VaultScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val BACKUP = "backup"
    const val VAULT = "vault"
    const val ICE = "ice"
    const val SCAM_SHIELD = "scam_shield"
    const val ACCESSIBILITY = "accessibility"
    const val LAUNCHER = "launcher"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val isOnboarded by homeViewModel.isOnboarded.collectAsState()

    val startDestination = if (isOnboarded) Routes.HOME else Routes.ONBOARDING

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToBackup = { navController.navigate(Routes.BACKUP) },
                onNavigateToVault = { navController.navigate(Routes.VAULT) },
                onNavigateToIce = { navController.navigate(Routes.ICE) },
                onNavigateToScamShield = { navController.navigate(Routes.SCAM_SHIELD) },
                onNavigateToAccessibility = { navController.navigate(Routes.ACCESSIBILITY) },
                onNavigateToLauncher = { navController.navigate(Routes.LAUNCHER) }
            )
        }

        composable(Routes.BACKUP) {
            BackupDashboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.VAULT) {
            VaultScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ICE) {
            IceScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SCAM_SHIELD) {
            ScamShieldScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ACCESSIBILITY) {
            AccessibilitySettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
