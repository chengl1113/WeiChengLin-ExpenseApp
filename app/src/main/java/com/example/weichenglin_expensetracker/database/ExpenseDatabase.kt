package com.example.weichenglin_expensetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weichenglin_expensetracker.Expense

@Database(entities = [ Expense::class ], version=1)
@TypeConverters(ExpenseTypeConverters::class)
abstract class ExpenseDatabase : RoomDatabase(){
    abstract fun expenseDao() : ExpenseDao
}