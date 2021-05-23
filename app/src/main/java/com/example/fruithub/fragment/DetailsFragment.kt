package com.example.fruithub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentDetailsBinding
import com.example.fruithub.model.Basket
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private var mQuantity: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            detailsBackBtn.setOnClickListener {
                it.findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToHomeFragment()
                )
            }

            decreaseQBtn.setOnClickListener {
                if (mQuantity > 1) {
                    mQuantity -= 1
                    detailsQuantity.text = mQuantity.toString()
                } else {
                    mQuantity = 1
                }

            }

            increaseQBtn.setOnClickListener {
                mQuantity += 1
                detailsQuantity.text = mQuantity.toString()
            }
        }

        val args: DetailsFragmentArgs by navArgs()

        mViewModel.getItemDetails(args.id).observe(viewLifecycleOwner, { item ->
            binding.item = item

            binding.addToBasketBtn.setOnClickListener {
                val basket = Basket(item.id, item, mQuantity, (item.price?.times(mQuantity)))
                mViewModel.addItemToBasketFirebase(basket)
                Toast.makeText(requireContext(), "Item added to your basket.", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        return binding.root
    }

}