package com.yours.mycup.mypage.RV

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yours.mycup.R
import com.yours.mycup.search.BrandDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class MyInterestAdapter(private val context: Context) :
    RecyclerView.Adapter<MyInterestAdapter.MyInterestViewHolder>() {
    var data = mutableListOf<MyInterestData>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val docRef_mp = db.collection("${auth.currentUser?.email}").document("Mypage")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyInterestViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_interest,
            parent, false
        )
        return MyInterestViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyInterestViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    inner class MyInterestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img_cup: ImageView = itemView.findViewById(R.id.item_interest_img_cup)
        private val cupname: TextView = itemView.findViewById(R.id.item_interest_cupname)
        private val cupsize: TextView = itemView.findViewById(R.id.item_interest_cupsize)
        private val info: TextView = itemView.findViewById(R.id.item_interest_info)
        private val btn_heart: ImageButton = itemView.findViewById(R.id.item_btn_heart)
        private val cl_interest_cup: ConstraintLayout =
            itemView.findViewById(R.id.item_cl_interest_cup)

        var click = true

        fun onBind(myInterestData: MyInterestData) {
            Glide.with(itemView).load(myInterestData.img_cups).into(img_cup)
            cupname.text = myInterestData.cupname
            cupsize.text = myInterestData.cupsize
            info.text = ("용량 " + myInterestData.amount.toString() + "ml "
                    + "지름 " + myInterestData.diameter.toString() + "mm " + "총 길이 " + myInterestData.length + "mm")

            cl_interest_cup.setOnClickListener { view ->
                val context: Context = view!!.context
                val nextIntent = Intent(view.context, BrandDetailActivity::class.java)
                nextIntent.putExtra("cupname", myInterestData.cupname)

                context.startActivity(nextIntent)
            }

            info.movementMethod = ScrollingMovementMethod()

            // 특정 글자 색 변경
            val content = info.text.toString()
            val spannableString = SpannableString(content)

            var word = "용량"
            var start = content.indexOf(word)
            var end = start + word.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            word = "지름"
            start = content.indexOf(word)
            end = start + word.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            word = "총 길이"
            start = content.indexOf(word)
            end = start + word.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#a7a1a1")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            info.text = spannableString


            // 관심컵 삭제
            btn_heart.setOnClickListener {
                click = false
                data.removeAt(position)
                notifyItemRemoved(position)

                // 파이어베이스에서 관심 컵 삭제
                docRef_mp.get().addOnSuccessListener { document ->
                    val listnum = "${document.get("list_num")}".toInt()
                    val likenum = "${document.get("like_num")}".toInt()


                    for(i in 1..listnum){
                        if(document.get("like_list$i") != null) {
                            val name = document.get("like_list$i.cupname") as String
                            val csize = document.get("like_list$i.cupsize") as String

                            if (name == myInterestData.cupname && csize == myInterestData.cupsize) {
                                if (likenum == 1) {
                                    val updates = hashMapOf<String, Any>(
                                        "like_list$i" to FieldValue.delete(),
                                        "like_num" to FieldValue.delete(),
                                        "list_num" to FieldValue.delete()
                                    )
                                    docRef_mp.update(updates).addOnCompleteListener {}
                                } else {
                                    val updates = hashMapOf<String, Any>(
                                        "like_list$i" to FieldValue.delete(),
                                        "like_num" to likenum - 1
                                    )
                                    docRef_mp.update(updates).addOnCompleteListener {}
                                }
                            }
                        }

                    }
                }

            }



        }
    }


}