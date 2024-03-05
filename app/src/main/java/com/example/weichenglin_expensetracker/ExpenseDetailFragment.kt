package com.example.weichenglin_expensetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weichenglin_expensetracker.databinding.FragmentExpenseDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "ExpenseDetailFragement"

class ExpenseDetailFragment : Fragment() {

    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot acess binding because it is null. Is the view visible?"
        }

    private val args: ExpenseDetailFragmentArgs by navArgs()

    private val expenseDetailViewModel: ExpenseDetailViewModel by viewModels {
        ExpenseDetailViewModelFactory(args.expenseId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.expense_category_spinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        binding.apply {
            expenseTitle.doOnTextChanged {text, _,_,_ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(title = text.toString())
                }
            }
            expenseAmount.doOnTextChanged { text, _, _, _ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(amount = text.toString().toDouble())
                }
            }

            expenseCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val categories = resources.getStringArray(R.array.categories_array)
                    expenseDetailViewModel.updateExpense { oldExpense ->
                        oldExpense.copy(category = categories[position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }


        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseDetailViewModel.expense.collect {expense ->
                    expense?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) {
            _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            expenseDetailViewModel.updateExpense { it.copy(date = newDate) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateUi(expense: Expense) {
        binding.apply {
            if (expenseTitle.text.toString() != expense.title) {
                expenseTitle.setText(expense.title)
            }

            expenseDate.text = expense.date.toString()
            expenseDate.setOnClickListener {
                findNavController().navigate(
                    ExpenseDetailFragmentDirections.selectDate(expense.date)
                )
            }

            if (expenseAmount.text.toString() != expense.amount.toString()) {
                expenseAmount.setText(expense.amount.toString())
            }

            if (expenseCategorySpinner.selectedItem.toString() != expense.category) {
                val categories = resources.getStringArray(R.array.categories_array)
                val desired = expense.category
                val position = categories.indexOf<String>(desired)
                expenseCategorySpinner.setSelection(position)
            }

        }
    }
}