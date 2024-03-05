package com.example.weichenglin_expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Expense(
    @PrimaryKey val id: UUID,
    val date: Date,
    val title: String,
    val amount: Double,
    val category: String
)
