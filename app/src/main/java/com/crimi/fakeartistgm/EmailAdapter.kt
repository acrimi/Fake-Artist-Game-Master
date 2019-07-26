package com.crimi.fakeartistgm

import android.database.Observable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.crimi.fakeartistgm.databinding.RowEmailBinding

private const val ITEM_TYPE_EMAIL = 1
private const val ITEM_TYPE_ADD = 2

class EmailAdapter : RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {
    private val emails = mutableListOf(ObservableField<String>())

    fun reset() {
        emails.clear()
        emails.add(ObservableField())
        notifyDataSetChanged()
    }

    fun addEmail() {
        emails.add(ObservableField())
        notifyItemInserted(emails.size -1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        var binding: RowEmailBinding? = null
        val view = if (viewType == ITEM_TYPE_EMAIL) {
            binding = RowEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.row_add, parent, false)
        }
        return EmailViewHolder(view, binding)
    }

    override fun getItemCount() = emails.size + 1

    override fun getItemViewType(position: Int) = if (position == itemCount - 1) {
        ITEM_TYPE_ADD
    } else {
        ITEM_TYPE_EMAIL
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_EMAIL) {
            holder.binding?.let {
                it.emails = emails
                it.position = position
                it.executePendingBindings()
            }
        } else {
            holder.itemView.setOnClickListener { addEmail() }
        }
    }

    class EmailViewHolder(view: View, val binding: RowEmailBinding? = null) : RecyclerView.ViewHolder(view)
}