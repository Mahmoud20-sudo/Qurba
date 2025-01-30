package com.qurba.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qurba.android.R
import com.qurba.android.databinding.ItemSimilarPlaceBinding
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.utils.BaseActivity

class SimilarPlacesAdapter : RecyclerView.Adapter<SimilarPlacesAdapter.MyViewHolder>() {
    private val listFaqs = mutableListOf<PlaceModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemSimilarPlaceBinding>(inflater, R.layout.item_similar_place, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount() = listFaqs.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listFaqs[position])
    }

    fun updateList(faqs: List<PlaceModel>) {
        this.listFaqs.clear()
        this.listFaqs.addAll(faqs)
        notifyDataSetChanged() // line also executes but does nothing.
    }

    class MyViewHolder(private val binding: ItemSimilarPlaceBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(modelFaqs: PlaceModel) {
            val viewModel = com.qurba.android.ui.places.view_models.PlaceItemViewModel(itemView.context as BaseActivity, modelFaqs)
//            viewModel.bind(modelFaqs)
            Glide.with(itemView.context).load(modelFaqs.placeProfilePictureUrl).into(binding.placeImageIv)
            binding.placeDetailsLikeTv.text = itemView.context.getString(R.string.like)
            binding.placeAreaTv.text =  if (modelFaqs.branchName == null) "" else modelFaqs.branchName.en
            binding.placeNameTv.text =  modelFaqs.name.en
            binding.viewModel = viewModel
        }
    }
}