package com.example.fruithub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentTrackOrderBinding

class TrackOrderFragment : Fragment() {

    private lateinit var binding: FragmentTrackOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_order, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner


        }

        return binding.root
    }
}