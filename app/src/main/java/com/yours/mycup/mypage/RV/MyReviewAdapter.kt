package com.yours.mycup.mypage.RV

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.yours.mycup.R
import com.yours.mycup.mypage.BottomSheetDialog
import com.yours.mycup.search.AddReviewActivity
import com.yours.mycup.search.RV.ReviewData

class MyReviewAdapter(private val context: Context, fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder>() {
    var data = mutableListOf<ReviewData>()
    var mFragmentManager: FragmentManager = fragmentManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_my_review,
            parent, false
        )
        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.onBind(data[position], mFragmentManager)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    // 리스너 객체 참조를 저장하는 변수
    private var mListener: OnItemClickListener? = null

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cupname: TextView = itemView.findViewById(R.id.item_review_cupname)
        private val userinfo: TextView = itemView.findViewById(R.id.item_review_userinfo)
        private val feeling: TextView = itemView.findViewById(R.id.item_review_feeling)
        private val length: TextView = itemView.findViewById(R.id.item_review_length)
        private val size: TextView = itemView.findViewById(R.id.item_review_size)
        private val hardness: TextView = itemView.findViewById(R.id.item_review_hardness)
        private val btnkebab: ImageButton = itemView.findViewById(R.id.item_btn_kebab)

        fun onBind(reviewData: ReviewData, mFragmentManager: FragmentManager) {
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

            // adapter 말고 activity에서 아이템 클릭 이벤트 제어함
            btnkebab.setOnClickListener { v ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener!!.onItemClick(v, pos)
                    }
                }
            }

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