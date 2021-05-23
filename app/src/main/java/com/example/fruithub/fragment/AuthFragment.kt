package com.example.fruithub.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fruithub.R
import com.example.fruithub.databinding.FragmentAuthBinding
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            startOrderingBtn.setOnClickListener {

                val username = authUsername.editText?.text.toString()
                val email = authEmail.editText?.text.toString()
                val password = authPassword.editText?.text.toString()

                mViewModel.registerUser(username, email, password)

                Snackbar.make(
                    requireView(),
                    "You've registered Successfully.",
                    Snackbar.LENGTH_SHORT
                ).show()

                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToHomeFragment())

            }

            loginBtn.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
            }
        }

        return binding.root
    }

    companion object {
        private const val TAG = "User"
    }
}