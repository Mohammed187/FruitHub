package com.example.fruithub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.fruithub.R
import com.example.fruithub.adapter.OrdersAdapter
import com.example.fruithub.databinding.FragmentOrdersBinding
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        ordersAdapter = OrdersAdapter()

        mViewModel.getOrders().observe(viewLifecycleOwner, Observer {
            ordersAdapter.submitList(it)
        })

        binding.apply {
            lifecycleOwner = lifecycleOwner

            ordersBackBtn.setOnClickListener {
                findNavController().navigate(OrdersFragmentDirections.actionOrdersFragmentToHomeFragment())
            }

            ordersRecyclerView.apply {
                setHasFixedSize(true)
                adapter = ordersAdapter
                itemAnimator = DefaultItemAnimator()
            }

        }

        return binding.root
    }

}