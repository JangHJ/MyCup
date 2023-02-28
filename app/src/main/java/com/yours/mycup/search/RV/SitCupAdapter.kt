package com.yours.mycup.search.RV

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yours.mycup.R
import com.yours.mycup.search.BrandDetailActivity

class SitCupAdapter(private val context: Context) :
    RecyclerView.Adapter<SitCupAdapter.SitCupViewHolder>() {
    var data = mutableListOf<SitCupData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitCupViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_situation_cup,
            parent, false
        )
        return SitCupViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SitCupViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    inner class SitCupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sitcup: ImageView = itemView.findViewById(R.id.item_sit_cup)

        fun onBind(sitCupData: SitCupData) {
            Glide.with(itemView).load(sitCupData.img_sitcups).into(sitcup)

            sitcup.setOnClickListener { view ->
                val context: Context = view!!.context
                val nextIntent = Intent(view.context, BrandDetailActivity::class.java)
                nextIntent.putExtra("cupname", sitCupData.cupname)

//                context.startActivity(nextIntent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                context.startActivity(nextIntent)
            }
        }
    }
}