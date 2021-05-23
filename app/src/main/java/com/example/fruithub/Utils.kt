package com.example.fruithub

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fruithub.adapter.BasketAdapter
import com.google.android.material.imageview.ShapeableImageView

@BindingAdapter("loadShapeableImage")
fun bindImage(imageView: ShapeableImageView, src: String?) {
    Glide.with(imageView).load(src)
        .centerInside()
        .fitCenter()
        .into(imageView)
}
