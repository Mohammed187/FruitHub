package com.example.fruithub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fruithub.R
import com.example.fruithub.adapter.BasketAdapter
import com.example.fruithub.adapter.OnItemSwipeListener
import com.example.fruithub.databinding.FragmentBasketBinding
import com.example.fruithub.model.Basket
import com.example.fruithub.viewmodel.FirestoreViewModel
import com.example.fruithub.viewmodel.FirestoreViewModelFactory
import com.google.android.material.snackbar.Snackbar

class BasketFragment : Fragment(), OnItemSwipeListener {

    private lateinit var binding: FragmentBasketBinding
    private lateinit var mViewModel: FirestoreViewModel
    private lateinit var mViewModelFactory: FirestoreViewModelFactory

    private lateinit var basketAdapter: BasketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basket, container, false)

        mViewModelFactory = FirestoreViewModelFactory()
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(FirestoreViewModel::class.java)

        basketAdapter = BasketAdapter(this)

        mViewModel.getBasket().observe(viewLifecycleOwner, {
            basketAdapter.submitList(it)
        })

        mViewModel.getBasketTotal().observe(viewLifecycleOwner, {
            val text = getString(R.string.price_model, it.toString())
            binding.basketTotal.text = text
        })

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            basketBackBtn.setOnClickListener {
                it.findNavController().navigate(
                    BasketFragmentDirections.actionBasketFragmentToHomeFragment()
                )
            }

            basketAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    checkEmpty()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    checkEmpty()
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    checkEmpty()
                }

                fun checkEmpty() {
                    binding.emptyView.visibility =
                        (if (basketAdapter.itemCount == 0) View.VISIBLE else View.GONE)
                }
            })

            basketRecyclerView.apply {
                setHasFixedSize(true)
                adapter = basketAdapter
                itemAnimator = DefaultItemAnimator()
            }

            val itemTouchHelper =
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        // Nothing to do
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val item = basketAdapter.getBasketItemAt(position)

                        basketAdapter.onItemSwipeListener.onItemSwipe(item)

                        basketRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                })

            itemTouchHelper.attachToRecyclerView(basketRecyclerView)

            checkoutBtn.setOnClickListener {
                findNavController().navigate(BasketFragmentDirections.actionBasketFragmentToCheckoutSheet())
            }
        }

        return binding.root
    }

    override fun onItemSwipe(basket: Basket) {
        mViewModel.deleteItemFromBasket(basket.id)
        Snackbar.make(
            binding.root,
            "Item ${basket.item?.name} Deleted.",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}