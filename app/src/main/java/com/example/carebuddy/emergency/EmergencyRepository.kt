package com.example.carebuddy.emergency

import kotlinx.coroutines.flow.Flow

class EmergencyRepository(
    private val dao: EmergencyContactDao
) {
    val contacts: Flow<List<EmergencyContact>> = dao.getContacts()

    suspend fun addContact(name: String, phone: String) {
        val contact = EmergencyContact(
            name = name.trim(),
            phone = phone.trim()
        )
        dao.insert(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        dao.delete(contact)
    }
}
