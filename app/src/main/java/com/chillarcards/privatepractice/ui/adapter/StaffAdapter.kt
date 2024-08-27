package com.chillarcards.privatepractice.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.ui.DummyStaff
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.utills.CommonDBaseModel

class StaffAdapter(private val items: List<DummyStaff>,
                   private val getAdapterUtil: IAdapterViewUtills,
                   context: Context?) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.staff_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.StaffView.setOnClickListener {
            val commonDObj = CommonDBaseModel()
            commonDObj.mastIDs = item.id.toString()
            commonDObj.itmName = item.name
            val sCommonDAry: ArrayList<CommonDBaseModel> = ArrayList()
            sCommonDAry.add(commonDObj)
            getAdapterUtil.getAdapterPosition(position, sCommonDAry, "VIEW")
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val StaffView: CardView = itemView.findViewById(R.id.order_frm)
        val StaffNameTextView: TextView = itemView.findViewById(R.id.staff_name)

        fun bind(item: DummyStaff) {
            StaffNameTextView.text = item.name
//            itemView.idTextView.text = "ID: ${item.id}"
//            itemView.imageView.setImageResource(item.imageResId)
        }

    }

}
