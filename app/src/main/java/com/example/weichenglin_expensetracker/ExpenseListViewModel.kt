package com.example.weichenglin_expensetracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "ExpenseListViewModel"

class ExpenseListViewModel : ViewModel() {

    private val expenseRepository = ExpenseRepository.get()

    private val _expenses: MutableStateFlow<List<Expense>> = MutableStateFlow(emptyList())
    val expenses: StateFlow<List<Expense>>
        get() = _expenses.asStateFlow()

    init {
        viewModelScope.launch {
            expenseRepository.getExpenses().collect {
                _expenses.value = it
            }
        }

    }

    suspend fun addExpense(expense: Expense) {
        expenseRepository.addExpense(expense)
    }


}