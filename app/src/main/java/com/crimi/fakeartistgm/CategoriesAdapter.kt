package com.crimi.fakeartistgm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crimi.fakeartistgm.databinding.RowCategoryBinding
import com.crimi.fakeartistgm.generator.Category

class CategoriesAdapter(val categories: List<Category>) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    fun resetDefaults() {
        categories.forEach { it.weight = it.defaultWeight }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RowCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {
            category = categories[position]
            executePendingBindings()
        }
    }

    class CategoryViewHolder(val binding: RowCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}