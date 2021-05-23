package com.example.fruithub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {

    private lateinit var binding: FragmentSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            trackOrderBtn.setOnClickListener {
                findNavController().navigate(
                    SuccessFragmentDirections.actionSuccessFragmentToTrackOrderFragment()
                )
            }

            continueShoppingBtn.setOnClickListener {
                findNavController().navigate(
                    SuccessFragmentDirections.actionSuccessFragmentToHomeFragment()
                )
            }
        }

        return binding.root
    }

}