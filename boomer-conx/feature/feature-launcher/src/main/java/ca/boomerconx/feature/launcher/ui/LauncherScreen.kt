package ca.boomerconx.feature.launcher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.boomerconx.core.ui.components.BigButton
import ca.boomerconx.feature.launcher.viewmodel.LauncherViewModel

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val layoutItems by viewModel.layoutItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (layoutItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Votre lanceur est vide.",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Appuyez sur le bouton ci-dessous\npour ajouter des applications.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(layoutItems, key = { it.id }) { item ->
                    Button(
                        onClick = { viewModel.launchApp(item.packageName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        BigButton(
            text = "Toutes les applications",
            icon = Icons.Default.Apps,
            onClick = viewModel::toggleDrawer,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
