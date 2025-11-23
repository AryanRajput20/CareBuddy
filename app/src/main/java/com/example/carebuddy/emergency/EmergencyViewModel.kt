package com.example.carebuddy.emergency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmergencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: EmergencyRepository

    val contacts: StateFlow<List<EmergencyContact>>

    init {
        val db = EmergencyDatabase.getInstance(application)
        repo = EmergencyRepository(db.emergencyContactDao())

        contacts = repo.contacts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addContact(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            repo.addContact(name, phone)
        }
    }

    fun deleteContact(id: String) {
        val contact = contacts.value.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repo.deleteContact(contact)
        }
    }
}

