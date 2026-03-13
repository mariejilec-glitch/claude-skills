package ca.boomerconx.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.boomerconx.core.ui.components.SeniorCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToBackup: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToIce: () -> Unit,
    onNavigateToScamShield: () -> Unit,
    onNavigateToAccessibility: () -> Unit,
    onNavigateToLauncher: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Boomer ConX",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(8.dp))

        SeniorCard(
            title = "Sauvegarde",
            subtitle = uiState.lastBackupTimestamp?.let {
                "Dernière : ${formatDate(it)}"
            } ?: "Aucune sauvegarde",
            icon = Icons.Default.Backup,
            onClick = onNavigateToBackup
        )

        SeniorCard(
            title = "Coffre-fort",
            subtitle = "${uiState.vaultItemCount} mots de passe",
            icon = Icons.Default.Lock,
            onClick = onNavigateToVault
        )

        SeniorCard(
            title = "Urgence (ICE)",
            subtitle = if (uiState.iceContactCount > 0) {
                "${uiState.iceContactCount} contacts configurés"
            } else {
                "Non configuré"
            },
            icon = Icons.Default.LocalHospital,
            onClick = onNavigateToIce
        )

        SeniorCard(
            title = "Protection arnaque",
            subtitle = "SMS, appels et liens",
            icon = Icons.Default.Shield,
            onClick = onNavigateToScamShield
        )

        SeniorCard(
            title = "Accessibilité",
            subtitle = "Taille du texte, contraste",
            icon = Icons.Default.Accessibility,
            onClick = onNavigateToAccessibility
        )

        SeniorCard(
            title = "Lanceur",
            subtitle = "Écran d'accueil simplifié",
            icon = Icons.Default.Apps,
            onClick = onNavigateToLauncher
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.CANADA_FRENCH)
    return sdf.format(Date(timestamp))
}
