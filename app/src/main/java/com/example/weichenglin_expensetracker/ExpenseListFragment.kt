package com.example.weichenglin_expensetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weichenglin_expensetracker.databinding.FragmentExpenseListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "ExpenseListFragment"
class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val expenseListViewModel : ExpenseListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)

        binding.expenseReyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.filter_category_spinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.filters_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categories = resources.getStringArray(R.array.filters_array)
                val selectedCategory = categories[position]
                Log.d(TAG, "selectedCategory: $selectedCategory, position: $position")

                if (position == 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            expenseListViewModel.expenses.collect { expenses ->
                                Log.d(TAG, "Expenses: {${expenses.size}}")
                                binding.expenseReyclerView.adapter = ExpenseListAdapter(expenses) {
                                    findNavController().navigate(ExpenseListFragmentDirections.showExpenseDetail(it))
                                }

                            }
                        }
                    }
                }
                else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            expenseListViewModel.expenses.collect { expenses ->
                                val filteredExpenses = expenses.filter { it.category == selectedCategory }
                                binding.expenseReyclerView.adapter = ExpenseListAdapter(filteredExpenses) {
                                    findNavController().navigate(ExpenseListFragmentDirections.showExpenseDetail(it))
                                }
                            }
                        }
                    }
                }



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
               expenseListViewModel.expenses.collect { expenses ->
                   binding.expenseReyclerView.adapter = ExpenseListAdapter(expenses) {
                       findNavController().navigate(ExpenseListFragmentDirections.showExpenseDetail(it))
                   }

               }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_expense_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_expense -> {
                showNewExpense()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewExpense() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newExpense = Expense (
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                amount = 0.0,
                category = "Food"
            )
            expenseListViewModel.addExpense(newExpense)
            findNavController().navigate(
                ExpenseListFragmentDirections.showExpenseDetail(newExpense.id)
            )
        }
    }
}