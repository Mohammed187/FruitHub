package com.example.fruithub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fruithub.databinding.FragmentLoginBinding
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            startOrderingBtn.setOnClickListener {
                val email = authEmail.editText?.text.toString()
                val password = authPassword.editText?.text.toString()

                mViewModel.loginUser(email, password)

                mViewModel.navigateToHome.observe(viewLifecycleOwner, { login ->
                    if (login == true) {
                        Snackbar.make(requireView(), "Login Success", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    } else {
                        Snackbar.make(requireView(), "Login Failed", Snackbar.LENGTH_SHORT).show()
                    }
                })
            }

            registerBtn.setOnClickListener {
                LoginFragmentDirections.actionLoginFragmentToAuthFragment()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (mViewModel.checkIfUserExists()) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }
}