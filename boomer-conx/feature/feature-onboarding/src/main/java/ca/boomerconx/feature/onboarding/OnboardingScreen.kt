package ca.boomerconx.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.boomerconx.core.ui.components.StepIndicator

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StepIndicator(
            totalSteps = 3,
            currentStep = uiState.currentStep
        )

        Spacer(Modifier.height(32.dp))

        when (uiState.currentStep) {
            0 -> LanguageSelectionScreen(
                selectedLanguage = uiState.selectedLanguage,
                onLanguageSelected = viewModel::selectLanguage,
                onNext = viewModel::nextStep
            )
            1 -> PinSetupScreen(
                pin = uiState.pin,
                confirmPin = uiState.confirmPin,
                pinError = uiState.pinError,
                onPinChange = viewModel::updatePin,
                onConfirmPinChange = viewModel::updateConfirmPin,
                onSubmit = viewModel::submitPin
            )
            2 -> WelcomeFinishScreen(
                onComplete = viewModel::completeOnboarding
            )
        }
    }
}

@Composable
private fun WelcomeFinishScreen(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue!",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Votre téléphone est prêt.\nBoomer ConX vous accompagne.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        androidx.compose.material3.Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text("Commencer", style = MaterialTheme.typography.titleMedium)
        }
    }
}
