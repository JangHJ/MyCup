package com.yours.mycup.mypage

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMyInterestBinding
import com.yours.mycup.mypage.RV.MyInterestAdapter
import com.yours.mycup.mypage.RV.MyInterestData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class MyInterestActivity : AppCompatActivity() {
    lateinit var myInterestAdapter: MyInterestAdapter
    lateinit var binding: ActivityMyInterestBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val docRef_mp = db.collection("${auth.currentUser?.email}").document("Mypage")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_interest)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        myInterestAdapter = MyInterestAdapter(this)
        binding.rvMyInterest.adapter = myInterestAdapter
        binding.rvMyInterest.layoutManager = LinearLayoutManager(this)

        binding.btnMyinterestBack.setOnClickListener {
            finish()
        }

        // 파이어베이스에서 관심 컵 가져오기
//        interestCup(binding)
    }

    private fun interestCup(binding: ActivityMyInterestBinding){
        myInterestAdapter.data = mutableListOf<MyInterestData>()

        docRef_mp.get().addOnSuccessListener { document ->
            if (document.get("list_num") != null) {
                if ("${document.get("like_num")}".toInt() > 0) {
                    binding.clInterest.visibility = View.VISIBLE
                    binding.clInterestDefault.visibility = View.GONE

                    val listnum = "${document.get("list_num")}".toInt()
                    val likenum = "${document.get("like_num")}".toInt()
                    for (i in 1..listnum) {
                        if(document.get("like_list$i") != null) {
                            val cupname = document.get("like_list$i.cupname") as String
                            val cupsize = document.get("like_list$i.cupsize") as String
                            val amount = "${document.get("like_list$i.amount")}".toInt()
                            val diameter = "${document.get("like_list$i.diameter")}".toFloat()
                            val length = "${document.get("like_list$i.length")}".toInt()

                            Log.e("관심컵 결과 출력 : ", "$cupname, $cupsize, $amount, $diameter, $length")

                            when (cupname) {
                                "루나컵 링" -> {
                                    when (cupsize) {
                                        "타이니" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_ring_tiny,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )

                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "스몰" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_ring_small,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "라지" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_ring_large,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                "루나컵 쇼티" -> {
                                    when (cupsize) {
                                        "타이니" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_shorty_tiny,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "스몰" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_shorty_small,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "라지" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_shorty_large,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                "루나컵 클래식" -> {
                                    when (cupsize) {
                                        "스몰" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_classic_small,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "라지" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_lunacup_classic_large,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                "한나컵" -> {
                                    when (cupsize) {
                                        "엑스트라 스몰" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_hannahcup_extrasmall,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "스몰" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_hannahcup_small,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                        "미디움" -> {
                                            myInterestAdapter.data.add(
                                                MyInterestData(
                                                    img_cups = R.drawable.img_hannahcup_medium,
                                                    cupname,
                                                    cupsize,
                                                    amount,
                                                    diameter,
                                                    length
                                                )
                                            )
                                            myInterestAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                "이브컵" -> {
                                    myInterestAdapter.data.add(
                                        MyInterestData(
                                            img_cups = R.drawable.img_evecup,
                                            cupname,
                                            cupsize,
                                            amount,
                                            diameter,
                                            length
                                        )
                                    )
                                    myInterestAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
            else{
                binding.clInterest.visibility = View.GONE
                binding.clInterestDefault.visibility = View.VISIBLE
            }
        }
        myInterestAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "myinterest")

        // 파이어베이스에서 관심 컵 가져오기
        interestCup(binding)
    }

}