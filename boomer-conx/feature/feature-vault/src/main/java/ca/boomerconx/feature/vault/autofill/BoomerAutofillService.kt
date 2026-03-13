package ca.boomerconx.feature.vault.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import ca.boomerconx.feature.vault.data.VaultRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BoomerAutofillService : AutofillService() {

    @Inject lateinit var vaultRepository: VaultRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: run {
            callback.onSuccess(null)
            return
        }

        val fields = parseAutofillFields(structure)
        if (fields.isEmpty()) {
            callback.onSuccess(null)
            return
        }

        val domain = fields.firstNotNullOfOrNull { it.webDomain } ?: ""

        serviceScope.launch {
            try {
                val credentials = vaultRepository.findByDomain(domain)
                if (credentials.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val responseBuilder = FillResponse.Builder()
                for (credential in credentials.take(3)) {
                    val presentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                    presentation.setTextViewText(android.R.id.text1, credential.siteName)

                    val datasetBuilder = Dataset.Builder()
                    for (field in fields) {
                        if (field.isUsername) {
                            datasetBuilder.setValue(
                                field.autofillId,
                                AutofillValue.forText(credential.username),
                                presentation
                            )
                        } else if (field.isPassword) {
                            val password = vaultRepository.decryptPassword(credential)
                            datasetBuilder.setValue(
                                field.autofillId,
                                AutofillValue.forText(password),
                                presentation
                            )
                        }
                    }
                    responseBuilder.addDataset(datasetBuilder.build())
                }
                callback.onSuccess(responseBuilder.build())
            } catch (_: Exception) {
                callback.onSuccess(null)
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    private data class AutofillField(
        val autofillId: android.view.autofill.AutofillId,
        val isUsername: Boolean,
        val isPassword: Boolean,
        val webDomain: String?
    )

    private fun parseAutofillFields(structure: AssistStructure): List<AutofillField> {
        val fields = mutableListOf<AutofillField>()
        for (i in 0 until structure.windowNodeCount) {
            traverseNode(structure.getWindowNodeAt(i).rootViewNode, fields)
        }
        return fields
    }

    private fun traverseNode(
        node: AssistStructure.ViewNode,
        fields: MutableList<AutofillField>
    ) {
        val hints = node.autofillHints
        if (hints != null && node.autofillId != null) {
            val isUsername = hints.any {
                it == android.view.View.AUTOFILL_HINT_USERNAME ||
                it == android.view.View.AUTOFILL_HINT_EMAIL_ADDRESS
            }
            val isPassword = hints.any {
                it == android.view.View.AUTOFILL_HINT_PASSWORD
            }
            if (isUsername || isPassword) {
                fields.add(
                    AutofillField(
                        autofillId = node.autofillId!!,
                        isUsername = isUsername,
                        isPassword = isPassword,
                        webDomain = node.webDomain
                    )
                )
            }
        }
        for (i in 0 until node.childCount) {
            traverseNode(node.getChildAt(i), fields)
        }
    }
}
