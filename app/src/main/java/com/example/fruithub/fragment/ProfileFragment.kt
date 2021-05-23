package com.example.fruithub.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentProfileBinding
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private var selectedPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            mViewModel.getUserData().observe(viewLifecycleOwner, {
                user = it
            })

            profileUpdateBtn.setOnClickListener {
                val name = userProfileName.editText?.text.toString()
                val phone = userProfilePhone.editText?.text.toString()

                mViewModel.updateUserDetails(name, phone)

                Toast.makeText(
                    requireContext(),
                    "User Details Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
            }

            userProfilePic.setOnClickListener {
                launchGallery()
            }

            profileBackBtn.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
            }
        }

        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture.."), PICK_IMAGE_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            selectedPhotoUri = data.data
            mViewModel.uploadUserImage(selectedPhotoUri)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
    }
}
