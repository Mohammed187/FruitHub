package com.example.fruithub.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.fruithub.R
import com.example.fruithub.adapter.ItemClickListener
import com.example.fruithub.adapter.ItemsAdapter
import com.example.fruithub.databinding.FragmentHomeBinding
import com.example.fruithub.databinding.HeaderNavDrawerBinding
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private lateinit var filterAdapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        val mAdapter = ItemsAdapter(ItemClickListener { id ->
            mViewModel.onMenuItemClicked(id)
        }, mViewModel)

        mViewModel.getAllItems().observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })

        filterAdapter = ItemsAdapter(ItemClickListener { id ->
            mViewModel.onMenuItemClicked(id)
        }, mViewModel)

        loadMenuByCat("Hottest")

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            mViewModel.getUserData().observe(viewLifecycleOwner, {
                mUser = it

                // Nav Header Info
                HeaderNavDrawerBinding.bind(navView.getHeaderView(0)).apply {
                    mUser = it
                }

            })

            recommendedRecyclerView.apply {
                setHasFixedSize(true)
                adapter = mAdapter
                itemAnimator = DefaultItemAnimator()
            }
            tablayoutRecyclerView.apply {
                setHasFixedSize(true)
                adapter = filterAdapter
                itemAnimator = DefaultItemAnimator()
            }

            homeToolbar.setNavigationOnClickListener {
                drawerLayout.open()
            }

            navView.setNavigationItemSelectedListener { menuItem ->
                menuItem.isChecked = true
                when (menuItem.itemId) {
                    R.id.ic_orders -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToOrdersFragment()
                        )
                    }
                    R.id.ic_basket -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBasketFragment()
                        )
                    }
                    R.id.ic_profile -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                        )
                    }

                    R.id.ic_logout -> {
                        mViewModel.userLogout()
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToSplashFragment()
                        )
                    }

                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

        }

        mViewModel.navigateToItemDetail.observe(viewLifecycleOwner, { id ->
            id?.let {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(id)
                )
                mViewModel.onItemDetailNavigated()
            }
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadMenuByCat(tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                loadMenuByCat(tab?.text.toString())
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                loadMenuByCat(tab?.text.toString())
            }

        })

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).setSupportActionBar(binding.homeToolbar)

        binding.homeToolbar.title = ""

        return binding.root
    }

    fun loadMenuByCat(category: String) {
        mViewModel.getItemsByCat(category).observe(viewLifecycleOwner, {
            filterAdapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.ic_basket -> {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBasketFragment()
                )
                true
            }
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}







