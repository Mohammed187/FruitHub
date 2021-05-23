package com.example.fruithub.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentPaymentBinding
import com.example.fruithub.model.Order
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class PaymentFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPaymentBinding

    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private val args: CheckoutFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        val currentTime = System.currentTimeMillis()

        val date = SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(currentTime))

        mViewModel.getBasket().observe(viewLifecycleOwner, { basket ->

            val order = Order(
                currentTime.toString(),
                basket,
               args.address,
                "Paid",
                "New",
                date,
            )

            binding.apply {
                lifecycleOwner = viewLifecycleOwner

                completeOrderBtn.setOnClickListener {

                    mViewModel.placeOrder(order)

                    Toast.makeText(requireContext(), "${order.id} Created.", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(PaymentFragmentDirections.actionCardPaymentSheetToSuccessFragment())

                }
            }
        })
        return binding.root
    }
}
