package com.example.fruithub.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentCheckoutBinding
import com.example.fruithub.model.Order
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class CheckoutFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCheckoutBinding

    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        val currentTime = System.currentTimeMillis()

        val date = SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(currentTime))

        mViewModel.getBasket().observe(viewLifecycleOwner, { basket ->

            binding.apply {
                lifecycleOwner = viewLifecycleOwner

                payWithCardBtn.setOnClickListener {
                    val address = checkoutDeliveryAddress.editText?.text.toString()

                    findNavController().navigate(
                        CheckoutFragmentDirections.actionCheckoutSheetToCardPaymentSheet(address)
                    )
                }

                payOnDeliveryBtn.setOnClickListener {

                    val order = Order(
                        currentTime.toString(),
                        basket,
                        checkoutDeliveryAddress.editText?.text.toString(),
                        "Cash",
                        "New",
                        date,
                    )

                    mViewModel.placeOrder(order)

                    findNavController().navigate(CheckoutFragmentDirections.actionCheckoutSheetToSuccessFragment())
                }

            }

        })

        return binding.root
    }

}