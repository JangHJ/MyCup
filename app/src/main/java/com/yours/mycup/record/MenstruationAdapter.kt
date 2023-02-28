package com.yours.mycup.record

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yours.mycup.R
import com.yours.mycup.databinding.ItemMenstruationListBinding
import com.yours.mycup.search.RV.ReviewData

class CustomViewHolder(val binding: ItemMenstruationListBinding) : RecyclerView.ViewHolder(binding.root) {

    val timerange = binding.timeRange
    val time = binding.time
    val amount = binding.amount
    val type = binding.type
    val color = binding.color
    var pos: Int = 0

    init {
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, MenstruationSettingActivity::class.java)
            intent.putExtra("type", "${binding.type.text}")
            intent.putExtra("amount", "${binding.amount.text}")
            intent.putExtra("time", "${binding.timeRange.text}")
            intent.putExtra("color", "${binding.color.text}")
            intent.putExtra("isTodayMenstruation", true)
            intent.putExtra("pos", "${pos}")
            ContextCompat.startActivity(binding.root.context, intent, null)
        }
    }
}

class MenstruationAdapter(var DataList: MutableList<MenstruationActivity.Data>):
    RecyclerView.Adapter<CustomViewHolder>() {
    var data = DataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemMenstruationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.menstruation_list_items, parent, false)

        for (i in 0..data.size-1) {
            Log.e("DataList $i", "${data[i].time}")
        }
        return CustomViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.timerange.text = data[position].timeRange
        holder.time.text = data[position].time
        holder.amount.text = data[position].amount.toString() + "ml"
        if (data[position].type.contains("월경컵")) {
            holder.type.text = "월경컵"
        }
        else {
            holder.type.text = data[position].type
        }
        holder.color.text = data[position].color
        holder.pos = position

        if (holder.color.text == "" || holder.color.text == "null" || holder.color.text == null) {
            holder.color.visibility = View.GONE
        }
        else {
            holder.color.visibility = View.VISIBLE
        }
        // xml에서 backgroudcolor가 지정되지 않아 여기서 지정
        holder.time.setChipStrokeColorResource(R.color.gray_a7)
        holder.time.setChipBackgroundColorResource(R.color.chipBackgroundColor_unclicked)
        holder.amount.setChipBackgroundColorResource(R.color.gray_a7)
        holder.type.setChipBackgroundColorResource(R.color.gray_a7)
        holder.color.setChipBackgroundColorResource(R.color.gray_a7)

    }

    override fun getItemCount(): Int {
        Log.e("getitemcount", "${data.size}")

        return data.size
    }
}