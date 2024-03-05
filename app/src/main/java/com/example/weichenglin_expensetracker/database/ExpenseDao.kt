package com.example.weichenglin_expensetracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weichenglin_expensetracker.Expense
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE id=(:id)")
    suspend fun getExpense(id: UUID) : Expense

    @Query("SELECT * FROM expense WHERE category=(:category)")
    fun getCategory(category: String) : Flow<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Insert
    suspend fun addExpense(expense: Expense)
}