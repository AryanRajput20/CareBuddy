package com.example.carebuddy.emergency

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String
)

