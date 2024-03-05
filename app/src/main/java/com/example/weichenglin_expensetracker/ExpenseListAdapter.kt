package com.example.weichenglin_expensetracker

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weichenglin_expensetracker.databinding.ListItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class ExpenseHolder(
    private val binding: ListItemExpenseBinding
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(expense: Expense, onExpenseClicked: (expenseId: UUID) -> Unit) {
        val sdf = SimpleDateFormat("E MMM dd HH:mm z yyyy", Locale.US)
        val formattedDate = sdf.format(expense.date)
        Log.d("TEMP", "formatted date: $formattedDate")

        binding.expenseAmount.text = "$" + expense.amount.toString()
        binding.expenseCategory.text = expense.category
        binding.expenseDate.text = formattedDate
        binding.expenseTitle.text = expense.title

        binding.root.setOnClickListener {
            onExpenseClicked(expense.id)
        }
    }
}



class ExpenseListAdapter(
    private val expenses: List<Expense>,
    private val onExpenseClicked: (expenseId: UUID) -> Unit
) : RecyclerView.Adapter<ExpenseHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemExpenseBinding.inflate(inflater,parent, false)
        return ExpenseHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense, onExpenseClicked)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}