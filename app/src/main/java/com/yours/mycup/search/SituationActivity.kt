package com.yours.mycup.search

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.Slide
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivitySituationBinding
import com.yours.mycup.record.dpToPx
import com.yours.mycup.search.RV.ReviewAdapter
import com.yours.mycup.search.RV.ReviewData
import com.yours.mycup.search.RV.SitCupAdapter
import com.yours.mycup.search.RV.SitCupData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class SituationActivity : AppCompatActivity() {
    var situation = 0
    var btnselected = false
    var detailselected = 0
    lateinit var sitcupAdapter: SitCupAdapter
    lateinit var reviewAdapter: ReviewAdapter
    lateinit var binding: ActivitySituationBinding


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val docRef = db.collection("ALL").document("Review")

    var product = ""
    var cupsize = ""
    var period = ""
    var feeling = ""
    var length = ""
    var size = ""
    var hardness = ""

    var am = ""
    var len = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_situation)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        situation = intent.extras!!.getInt("SITUATION")

        sitcupAdapter = SitCupAdapter(this)
        binding.rvSituationCup.adapter = sitcupAdapter
        binding.rvSituationCup.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        reviewAdapter = ReviewAdapter(this)
        binding.rvReview.adapter = reviewAdapter
        binding.rvReview.layoutManager = LinearLayoutManager(this)


        binding.btnSituationBack.setOnClickListener {
            finish()
        }

        when (situation) {
            0 -> {
                binding.tvSituationTitle.text = "활동량이 많지 않을 때"
                binding.tvSituationExplain.text = "움직임이 많지 않은 일상에는\n이물감과 압박감이 없는 부드러운 컵을 사용해보세요!"
                binding.clSituation1.visibility = View.VISIBLE

                sitcupAdapter.data = mutableListOf(
                    SitCupData(img_sitcups = R.drawable.img_thumbnail_lunacup_ring, "루나컵 링"),
                    SitCupData(img_sitcups = R.drawable.img_thumbnail_lunacup_classic, "루나컵 클래식"),
                    SitCupData(img_sitcups = R.drawable.img_thumbnail_hannahcup, "한나컵")
                )
                sitcupAdapter.notifyDataSetChanged()

                reviewAll(binding, situation)
                optionReview(binding, situation)
            }
            1 -> {
                binding.tvSituationTitle.text = "격한 운동을 할 때"
                binding.tvSituationExplain.text = "이물감과 압박감이 느껴지지만,\n월경혈이 새지 않는 단단한 컵을 사용해보세요!"
                binding.clSituation2.visibility = View.VISIBLE

                sitcupAdapter.data = mutableListOf(
                    SitCupData(img_sitcups = R.drawable.img_thumbnail_lunacup_shorty, "루나컵 쇼티"),
                    SitCupData(img_sitcups = R.drawable.img_thumbnail_evecup, "이브컵")
                )
                sitcupAdapter.notifyDataSetChanged()

                reviewAll(binding, situation)
                optionReview(binding, situation)
            }
            2 -> {
                binding.tvSituationTitle.text = "월경컵 입문자라면"
                binding.tvSituationExplain.text = "착용과 교체가 보다 쉬운\n말랑하고 크기가 작은 컵을 사용해보세요!"
                binding.clSituation3.visibility = View.VISIBLE

                sitcupAdapter.data = mutableListOf(
                    SitCupData(img_sitcups = R.drawable.img_thum_lunacup_ring_t, "루나컵 링"),
                    SitCupData(img_sitcups = R.drawable.img_thum_lunacup_shorty_ts, "루나컵 쇼티"),
                    SitCupData(img_sitcups = R.drawable.img_thum_hannahcup_xs, "한나컵"),
                    SitCupData(img_sitcups = R.drawable.img_thum_evecup_m, "이브컵")
                )
                sitcupAdapter.notifyDataSetChanged()

                reviewAll(binding, situation)
                optionReview(binding, situation)
            }
        }

    }

    private fun reviewAll(binding: ActivitySituationBinding, situation: Int) {
        // 상황별 해당 컵 전체 후기 파이어베이스에서 받아오기 (일단 더미로 넣어놨음) 이 밑으로 후기 가져오기 주석 달아놓은 부분만 작성해주시면 됩니다
        reviewAdapter.data = mutableListOf<ReviewData>()
        reviewAdapter.data.clear()

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.get("rv_num") != null) {
                    val num = "${document.get("list_num")}"
                    val temp = num.toInt()

                    reviewAdapter.data.clear()
                    for (i in 1..temp) {
                        if (document.get("rvlist$i") != null) {
                            product = document.get("rvlist$i.product") as String
                            cupsize = document.get("rvlist$i.cupsize") as String
                            period = document.get("rvlist$i.period") as String
                            feeling = document.get("rvlist$i.feeling") as String
                            length = document.get("rvlist$i.length") as String
                            size = document.get("rvlist$i.size") as String
                            hardness = document.get("rvlist$i.hardness") as String
                            if (document.get("rvlist$i.am") != null)
                                am = document.get("rvlist$i.am") as String
                            if (document.get("rvlist$i.len") != null)
                                len = document.get("rvlist$i.len") as String

                            when (situation) {
                                0 -> {
                                    //활동량이 많지 않을때
                                    if (product == "루나컵 링" || product == "루나컵 클래식" || product == "한나컵") {
                                        binding.rvReview.visibility = View.VISIBLE
                                        binding.tvSituationDefault.visibility = View.GONE
                                        //리뷰 추가
                                        reviewAdapter.data.add(
                                            ReviewData(
                                                product,
                                                cupsize,
                                                am,
                                                len,
                                                period,
                                                feeling,
                                                length,
                                                size,
                                                hardness
                                            )
                                        )
                                        reviewAdapter.notifyDataSetChanged()
                                    }
                                }
                                1 -> {
                                    //격한 운동을 할때
                                    if (product == "루나컵 쇼티" || product == "이브컵") {
                                        binding.rvReview.visibility = View.VISIBLE
                                        binding.tvSituationDefault.visibility = View.GONE
                                        //리뷰 추가
                                        reviewAdapter.data.add(
                                            ReviewData(
                                                product,
                                                cupsize,
                                                am,
                                                len,
                                                period,
                                                feeling,
                                                length,
                                                size,
                                                hardness
                                            )
                                        )
                                        reviewAdapter.notifyDataSetChanged()
                                    }
                                }
                                2 -> {
                                    //입문자라면?
                                    if (cupsize == "타이니" || cupsize == "미니" || cupsize == "엑스트라 스몰") {
                                        binding.rvReview.visibility = View.VISIBLE
                                        binding.tvSituationDefault.visibility = View.GONE
                                        //리뷰 추가
                                        reviewAdapter.data.add(
                                            ReviewData(
                                                product,
                                                cupsize,
                                                am,
                                                len,
                                                period,
                                                feeling,
                                                length,
                                                size,
                                                hardness
                                            )
                                        )
                                        reviewAdapter.notifyDataSetChanged()
                                    } else if (product == "루나컵 쇼티" && cupsize == "스몰") {
                                        binding.rvReview.visibility = View.VISIBLE
                                        binding.tvSituationDefault.visibility = View.GONE
                                        //리뷰 추가
                                        reviewAdapter.data.add(
                                            ReviewData(
                                                product,
                                                cupsize,
                                                am,
                                                len,
                                                period,
                                                feeling,
                                                length,
                                                size,
                                                hardness
                                            )
                                        )
                                        reviewAdapter.notifyDataSetChanged()
                                    }
                                }
                            }

                        }
                        Log.e(
                            "situation 테스트",
                            "$product $cupsize $period $feeling $length $size $hardness, $am, $len"
                        )
                        //DataList.add(Data(timeRange, "테스트", 10, type, color))
                    }
                } else {
                    binding.rvReview.visibility = View.GONE
                    binding.tvSituationDefault.visibility = View.VISIBLE
                }
            }
    }


    private fun optionReview(binding: ActivitySituationBinding, situation: Int) {
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)

        // 질 길이 옵션
        binding.clReviewOption.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_review, null)
            val popupWindow = PopupWindow(
                popupView,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn

                val slideOut = Slide()
                slideOut.slideEdge = Gravity.TOP
                popupWindow.exitTransition = slideOut
            }
//            popupWindow.showAsDropDown(binding.btnReviewOption, -110*(metrics.densityDpi/160), 8*(metrics.densityDpi/160), Gravity.END)
//            popupWindow.showAsDropDown(binding.btnReviewOption, dpToPx(this, -121), dpToPx(this, 8), Gravity.END)
            popupWindow.showAsDropDown(binding.btnReviewOption, dpToPx(this, -102), dpToPx(this, 8))
            popupWindow.setOnDismissListener {
                btnselected = false
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
            }

            val length1 = popupView.findViewById<TextView>(R.id.popup_length1)
            val length2 = popupView.findViewById<TextView>(R.id.popup_length2)
            val length3 = popupView.findViewById<TextView>(R.id.popup_length3)

            if (!btnselected) { // 버튼 클릭되지 않은 상태에서 클릭함 -> 옵션창 보여줌
                btnselected = true
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_up)
            } else {    // 버튼 클릭된 상태에서 다시 클릭함 -> 옵션창 닫음
                btnselected = false
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
            }
            when (detailselected) {
                1 -> {
                    length1.setTextColor(Color.BLACK)
                }
                2 -> {
                    length2.setTextColor(Color.BLACK)
                }
                3 -> {
                    length3.setTextColor(Color.BLACK)
                }
            }
            option1Event(popupWindow, length1, length2, length3)
            option2Event(popupWindow, length1, length2, length3)
            option3Event(popupWindow, length1, length2, length3)
        }
    }

    private fun option1Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length1.setOnClickListener {
            if (detailselected == 1) {    // 이미 선택되어있는 경우
                btnselected = false
                detailselected = 0
                Log.e("재선택해서 1번 선택해제", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = "질 길이"
                popupWindow.dismiss()
                // 전체 후기
                reviewAll(binding, situation)
            } else {
                btnselected = false
                detailselected = 1
                Log.e("1번 선택", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length1.setTextColor(Color.BLACK)
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = length1.text.toString()
                popupWindow.dismiss()
                // 30mm~45mm에 해당하는 후기만 가져오기
                lenOption(30, 45)
            }
        }
    }

    private fun option2Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length2.setOnClickListener {
            if (detailselected == 2) {    // 이미 선택되어있는 경우
                btnselected = false
                detailselected = 0
                Log.e("재선택해서 2번 선택해제", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = "질 길이"
                popupWindow.dismiss()
                // 전체 후기
                reviewAll(binding, situation)
            } else {
                btnselected = false
                detailselected = 2
                Log.e("2번 선택", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length2.setTextColor(Color.BLACK)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = length2.text.toString()
                popupWindow.dismiss()
                // 46mm~60mm에 해당하는 후기만 가져오기
                lenOption(46, 60)

            }
        }
    }

    private fun option3Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length3.setOnClickListener {
            if (detailselected == 3) {    // 이미 선택되어있는 경우
                btnselected = false
                detailselected = 0
                Log.e("재선택해서 3번 선택해제", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = "질 길이"
                popupWindow.dismiss()
                // 전체 후기
                reviewAll(binding, situation)
            } else {
                btnselected = false
                detailselected = 3
                Log.e("3번 선택", detailselected.toString())
                binding.btnReviewOption.setBackgroundResource(R.drawable.btn_align_down)
                length3.setTextColor(Color.BLACK)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvReviewOption.text = length3.text.toString()
                popupWindow.dismiss()
                // 61mm 이상에 해당하는 후기만 가져오기
                lenOption(61, 70)

            }
        }
    }

    private fun lenOption(min: Int, max: Int) {
        reviewAdapter.data.clear()
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.get("rv_num") != null) {
                    val num = "${document.get("list_num")}"
                    val temp = num.toInt()
                    var count = 0   // 옵션에 해당하는 후기 개수

                    reviewAdapter.data.clear()

                    for (i in 1..temp) {
                        var len2 = "0"
                        if (document.get("rvlist$i") != null) {
                            product = document.get("rvlist$i.product") as String
                            cupsize = document.get("rvlist$i.cupsize") as String
                            period = document.get("rvlist$i.period") as String
                            feeling = document.get("rvlist$i.feeling") as String
                            length = document.get("rvlist$i.length") as String
                            size = document.get("rvlist$i.size") as String
                            hardness = document.get("rvlist$i.hardness") as String
                            if (document.get("rvlist$i.am") != null)
                                am = document.get("rvlist$i.am") as String
                            if (document.get("rvlist$i.len") != null)
                                len = document.get("rvlist$i.len") as String

                            // 질길이 입력된 경우
                            if (len != "")
                                len2 = len.replace("mm", "")

                            when (situation) {
                                0 -> {
                                    //활동량이 많지 않을때
                                    if (product == "루나컵 링" || product == "루나컵 클래식" || product == "한나컵") {
                                        //리뷰 추가
                                        when (len2.toInt()) {
                                            in min..max -> {
                                                count += 1
                                                //리뷰 추가
                                                reviewAdapter.data.add(
                                                    ReviewData(
                                                        product,
                                                        cupsize,
                                                        am,
                                                        len,
                                                        period,
                                                        feeling,
                                                        length,
                                                        size,
                                                        hardness
                                                    )
                                                )
                                                reviewAdapter.notifyDataSetChanged()
//                                                    Log.e(
//                                                        "질 길이에 부합됩니다 -> ",
//                                                        "$product $cupsize $period $feeling $length $size $hardness"
//                                                    )
                                                Log.e("count는 ", count.toString())
                                            }
                                            else -> Log.e(
                                                "질 길이가 맞지않습니다 -> ",
                                                "$product $cupsize $period $feeling $length $size $hardness"
                                            )
                                        }// when문 끝
                                        Log.e("count는 ", count.toString())
                                        if (count == 0) {
                                            binding.rvReview.visibility = View.GONE
                                            binding.tvSituationDefault.visibility = View.VISIBLE
                                        } else {
                                            binding.rvReview.visibility = View.VISIBLE
                                            binding.tvSituationDefault.visibility = View.GONE
                                        }
                                        reviewAdapter.notifyDataSetChanged()
                                    }
                                }
                                1 -> {
                                    //격한 운동을 할때
                                    if (product == "루나컵 쇼티" || product == "이브컵") {
                                        //리뷰 추가
                                        when (len2.toInt()) {
                                            in min..max -> {
                                                count += 1
                                                //리뷰 추가
                                                reviewAdapter.data.add(
                                                    ReviewData(
                                                        product,
                                                        cupsize,
                                                        am,
                                                        len,
                                                        period,
                                                        feeling,
                                                        length,
                                                        size,
                                                        hardness
                                                    )
                                                )
                                                reviewAdapter.notifyDataSetChanged()
                                                Log.e(
                                                    "질 길이에 부합됩니다 -> ",
                                                    "$product $cupsize $period $feeling $length $size $hardness"
                                                )
                                            }
                                            else -> Log.e(
                                                "질 길이가 맞지않습니다 -> ",
                                                "$product $cupsize $period $feeling $length $size $hardness"
                                            )
                                        }// when문 끝
                                        if (count == 0) {
                                            binding.rvReview.visibility = View.GONE
                                            binding.tvSituationDefault.visibility = View.VISIBLE
                                        } else {
                                            binding.rvReview.visibility = View.VISIBLE
                                            binding.tvSituationDefault.visibility = View.GONE
                                        }
                                        reviewAdapter.notifyDataSetChanged()
                                    }
                                }
                                2 -> {
                                    //입문자라면?
                                    if (cupsize == "타이니" || cupsize == "미니" || cupsize == "엑스트라 스몰") {
                                        when (len2.toInt()) {
                                            in min..max -> {
                                                count += 1
                                                //리뷰 추가
                                                reviewAdapter.data.add(
                                                    ReviewData(
                                                        product,
                                                        cupsize,
                                                        am,
                                                        len,
                                                        period,
                                                        feeling,
                                                        length,
                                                        size,
                                                        hardness
                                                    )
                                                )
                                                reviewAdapter.notifyDataSetChanged()
                                                Log.e(
                                                    "질 길이에 부합됩니다 -> ",
                                                    "$product $cupsize $period $feeling $length $size $hardness"
                                                )
                                            }
                                            else -> Log.e(
                                                "질 길이가 맞지않습니다 -> ",
                                                "$product $cupsize $period $feeling $length $size $hardness"
                                            )
                                        }// when문 끝
                                        if (count == 0) {
                                            binding.rvReview.visibility = View.GONE
                                            binding.tvSituationDefault.visibility = View.VISIBLE
                                        } else {
                                            binding.rvReview.visibility = View.VISIBLE
                                            binding.tvSituationDefault.visibility = View.GONE
                                        }
                                        reviewAdapter.notifyDataSetChanged()
                                    } else if (product == "루나컵" && cupsize == "스몰") {
                                        when (len2.toInt()) {
                                            in min..max -> {
                                                count += 1
                                                //리뷰 추가
                                                reviewAdapter.data.add(
                                                    ReviewData(
                                                        product,
                                                        cupsize,
                                                        am,
                                                        len,
                                                        period,
                                                        feeling,
                                                        length,
                                                        size,
                                                        hardness
                                                    )
                                                )
                                                reviewAdapter.notifyDataSetChanged()
                                                Log.e(
                                                    "질 길이에 부합됩니다 -> ",
                                                    "$product $cupsize $period $feeling $length $size $hardness"
                                                )
                                            }
                                            else -> Log.e(
                                                "질 길이가 맞지않습니다 -> ",
                                                "$product $cupsize $period $feeling $length $size $hardness"
                                            )
                                        }// when문 끝
                                        if (count == 0) {
                                            binding.rvReview.visibility = View.GONE
                                            binding.tvSituationDefault.visibility = View.VISIBLE
                                        } else {
                                            binding.rvReview.visibility = View.VISIBLE
                                            binding.tvSituationDefault.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                            Log.e("len2 값 : ", "$len2")
                            reviewAdapter.notifyDataSetChanged()
                        }
                    }
                    reviewAdapter.notifyDataSetChanged()
                } else {
                    binding.rvReview.visibility = View.GONE
                    binding.tvSituationDefault.visibility = View.VISIBLE
                }
            }//docRef 끝
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "situation: $situation")

        // 전체 후기 보여주는 걸로 초기화
        binding.tvReviewOption.text = "질 길이"
        detailselected = 0
        reviewAdapter.data.clear()
        reviewAll(binding, situation)
    }
}