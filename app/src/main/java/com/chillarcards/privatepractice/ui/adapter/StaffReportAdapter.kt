package com.chillarcards.privatepractice.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.ui.Dummy

class StaffReportAdapter(private val items: List<Dummy>,
                         context: Context?) :
    RecyclerView.Adapter<StaffReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.staff_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val staffNameTextView: TextView = itemView.findViewById(R.id.staff_name)

        fun bind(item: Dummy) {
            staffNameTextView.text = item.name
//            itemView.idTextView.text = "ID: ${item.id}"
//            itemView.imageView.setImageResource(item.imageResId)
        }
    }

}
