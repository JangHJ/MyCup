package com.yours.mycup.search.RV

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yours.mycup.R

class ReviewAdapter(private val context: Context) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    var data = mutableListOf<ReviewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_review,
            parent, false
        )
        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cupname: TextView = itemView.findViewById(R.id.item_review_cupname)
        private val userinfo: TextView = itemView.findViewById(R.id.item_review_userinfo)
        private val feeling: TextView = itemView.findViewById(R.id.item_review_feeling)
        private val length: TextView = itemView.findViewById(R.id.item_review_length)
        private val size: TextView = itemView.findViewById(R.id.item_review_size)
        private val hardness: TextView = itemView.findViewById(R.id.item_review_hardness)

        fun onBind(reviewData: ReviewData) {
            cupname.text = (reviewData.cupname + " (" + reviewData.cupsize + ")")
            if(reviewData.userlength == "" && reviewData.useramount != "") { //월경량만 있을때
                userinfo.text = (reviewData.useramount.toString() + " • " + reviewData.userexperience + " 사용")
            }
            else if(reviewData.userlength != "" && reviewData.useramount == "") { //질길이만 있을때
                userinfo.text = (reviewData.userlength.toString() + " • " + reviewData.userexperience + " 사용")
            }
            else if(reviewData.userlength == "" && reviewData.useramount == ""){ //둘 다 없을때
                userinfo.text = (reviewData.userexperience + " 사용")
            }
            else{ //둘 다 있을때
                userinfo.text = (reviewData.userlength.toString() + " • "
                        + reviewData.useramount.toString() + " • " + reviewData.userexperience + " 사용")
            }
            feeling.text = reviewData.feeling
            length.text = ("길이 " + reviewData.length)
            size.text = ("사이즈 " + reviewData.size)
            hardness.text = ("경도 " + reviewData.hardness)


            // 특정 글자 색 변경
            var content = length.text.toString()
            var spannableString = SpannableString(content)

            var word = "길이"
            var start = content.indexOf(word)
            var end = start + word.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            length.text = spannableString


            content = size.text.toString()
            spannableString = SpannableString(content)
            word = "사이즈"
            start = content.indexOf(word)
            end = start + word.length
            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            size.text = spannableString


            content = hardness.text.toString()
            spannableString = SpannableString(content)
            word = "경도"
            start = content.indexOf(word)
            end = start + word.length
            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            hardness.text = spannableString
        }

    }
}