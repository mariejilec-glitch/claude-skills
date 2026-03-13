package ca.boomerconx.feature.backup.data

import android.content.ContentResolver
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ContactData(
    val name: String,
    val phones: List<String>,
    val emails: List<String>
)

class ContactRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun readAllContacts(): List<ContactData> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactData>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex) ?: continue
                val phones = readPhones(id)
                val emails = readEmails(id)
                contacts.add(ContactData(name, phones, emails))
            }
        }
        contacts
    }

    private fun readPhones(contactId: String): List<String> {
        val phones = mutableListOf<String>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        cursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                it.getString(numberIndex)?.let { number -> phones.add(number) }
            }
        }
        return phones
    }

    private fun readEmails(contactId: String): List<String> {
        val emails = mutableListOf<String>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        cursor?.use {
            val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (it.moveToNext()) {
                it.getString(emailIndex)?.let { email -> emails.add(email) }
            }
        }
        return emails
    }

    fun serializeToVcf(contacts: List<ContactData>): ByteArray {
        val sb = StringBuilder()
        for (contact in contacts) {
            sb.appendLine("BEGIN:VCARD")
            sb.appendLine("VERSION:3.0")
            sb.appendLine("FN:${contact.name}")
            contact.phones.forEach { sb.appendLine("TEL:$it") }
            contact.emails.forEach { sb.appendLine("EMAIL:$it") }
            sb.appendLine("END:VCARD")
        }
        return sb.toString().toByteArray(Charsets.UTF_8)
    }
}
