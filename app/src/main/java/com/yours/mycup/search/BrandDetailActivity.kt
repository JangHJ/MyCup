package com.yours.mycup.search

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.Slide
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityBrandDetailBinding
import com.yours.mycup.record.dpToPx
import com.yours.mycup.search.RV.ReviewAdapter
import com.yours.mycup.search.RV.ReviewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class BrandDetailActivity : AppCompatActivity() {
    var btn1selected = false
    var btn2selected = false
    var option1_id = ""
    var option2_id = 0
    lateinit var reviewAdapter: ReviewAdapter
    lateinit var binding: ActivityBrandDetailBinding
    lateinit var cupname: String

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val docRef = db.collection("ALL").document("Review")
    val docRef_mp = db.collection("${auth.currentUser?.email}").document("Mypage")

    var product = ""
    var cupsize = ""
    var period = ""
    var feeling = ""
    var length = ""
    var size = ""
    var hardness = ""

    var am = ""
    var len = ""

    var fav_selected = false
    var fav_selected2 = false
    var fav_selected3 = false
    var fav_selected4 = false
    var fav_selected5 = false
    var fav_selected6 = false
    var fav_selected7 = false
    var fav_selected8 = false
    var fav_selected9 = false
    var fav_selected10 = false
    var fav_selected11 = false
    var fav_selected12 = false
    var fav_selected13 = false
    var fav_selected14 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brand_detail)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        reviewAdapter = ReviewAdapter(this)
        binding.rvBrandReview.adapter = reviewAdapter
        binding.rvBrandReview.layoutManager = LinearLayoutManager(this)

        cupname = intent.getStringExtra("cupname").toString()

        binding.pageTitle.text = cupname
        binding.tvBrand.text = ("크기별 $cupname")

        binding.btnDetailBack.setOnClickListener {
            finish()
        }

        binding.btnAddReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            startActivity(intent)
        }

        docRef_mp.get().addOnSuccessListener { document ->
            if (document.get("list_num") != null) {
                val listnum = "${document.get("list_num")}".toInt()
                for (i in 1..listnum) {
                    if (document.get("like_list$i") != null) {
                        val cname = document.get("like_list$i.cupname")
                        val csize = document.get("like_list$i.cupsize")

                        when (cname) {
                            "루나컵 링" -> {
                                when (csize) {
                                    "타이니" -> {
                                        fav_selected = true
                                        binding.btnLunacupRingTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "스몰" -> {
                                        fav_selected2 = true
                                        binding.btnLunacupRingSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "라지" -> {
                                        fav_selected3 = true
                                        binding.btnLunacupRingLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                }
                            }
                            "루나컵 쇼티" -> {
                                when (csize) {
                                    "타이니" -> {
                                        fav_selected4 = true
                                        binding.btnLunacupShortyTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "스몰" -> {
                                        fav_selected5 = true
                                        binding.btnLunacupShortySmallFavorite.setBackgroundResource(
                                            R.drawable.btn_favorite_active
                                        )
                                    }
                                    "라지" -> {
                                        fav_selected6 = true
                                        binding.btnLunacupShortyLargeFavorite.setBackgroundResource(
                                            R.drawable.btn_favorite_active
                                        )
                                    }
                                }
                            }
                            "루나컵 클래식" -> {
                                when (csize) {
                                    "스몰" -> {
                                        fav_selected7 = true
                                        binding.btnLunacupClassicSmallFavorite.setBackgroundResource(
                                            R.drawable.btn_favorite_active
                                        )
                                    }
                                    "라지" -> {
                                        fav_selected8 = true
                                        binding.btnLunacupClassicLargeFavorite.setBackgroundResource(
                                            R.drawable.btn_favorite_active
                                        )
                                    }
                                }
                            }
                            "한나컵" -> {
                                when (csize) {
                                    "엑스트라 스몰" -> {
                                        fav_selected9 = true
                                        binding.btnHannahcupExtrasmallFavorite.setBackgroundResource(
                                            R.drawable.btn_favorite_active
                                        )
                                    }
                                    "스몰" -> {
                                        fav_selected10 = true
                                        binding.btnHannahcupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "미디움" -> {
                                        fav_selected11 = true
                                        binding.btnHannahcupMediumFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                }
                            }
                            "이브컵" -> {
                                when (csize) {
                                    "미니" -> {
                                        fav_selected12 = true
                                        binding.btnEvecupMiniFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "스몰" -> {
                                        fav_selected13 = true
                                        binding.btnEvecupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                    "라지" -> {
                                        fav_selected14 = true
                                        binding.btnEvecupLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                                    }
                                }
                            }
                        }
                        //when문 끝
                    }
                }
            }
        }
        when (cupname) {
            "루나컵 링" -> {
                fav_selected = false
                fav_selected2 = false
                fav_selected3 = false
                binding.clLunacupRing.visibility = View.VISIBLE
                binding.btnLunacupRingTinyFavorite.setOnClickListener {
                    if (!fav_selected) {
                        fav_selected = true
                        binding.btnLunacupRingTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 링", "타이니", 15, 38f, 58)

                    } else if (fav_selected) {
                        fav_selected = false
                        binding.btnLunacupRingTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 링", "타이니")
                    }
                }
                binding.btnLunacupRingSmallFavorite.setOnClickListener {
                    if (!fav_selected2) {
                        fav_selected2 = true
                        binding.btnLunacupRingSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 링", "스몰", 20, 42f, 64)

                    } else if (fav_selected2) {
                        fav_selected2 = false
                        binding.btnLunacupRingSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 링", "스몰")
                    }
                }
                binding.btnLunacupRingLargeFavorite.setOnClickListener {
                    if (!fav_selected3) {
                        fav_selected3 = true
                        binding.btnLunacupRingLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 링", "라지", 25, 46f, 68)

                    } else if (fav_selected3) {
                        fav_selected3 = false
                        binding.btnLunacupRingLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 링", "라지")
                    }
                }
                review(binding, cupname)
            }

            "루나컵 쇼티" -> {
                fav_selected4 = false
                fav_selected5 = false
                fav_selected6 = false
                binding.clLunacupShorty.visibility = View.VISIBLE
                binding.btnLunacupShortyTinyFavorite.setOnClickListener {
                    if (!fav_selected4) {
                        fav_selected4 = true
                        binding.btnLunacupShortyTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 쇼티", "타이니", 15, 38f, 55)

                    } else if (fav_selected4) {
                        fav_selected4 = false
                        binding.btnLunacupShortyTinyFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 쇼티", "타이니")
                    }
                }
                binding.btnLunacupShortySmallFavorite.setOnClickListener {
                    if (!fav_selected5) {
                        fav_selected5 = true
                        binding.btnLunacupShortySmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 쇼티", "스몰", 20, 42f, 59)

                    } else if (fav_selected5) {
                        fav_selected5 = false
                        binding.btnLunacupShortySmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 쇼티", "스몰")
                    }
                }
                binding.btnLunacupShortyLargeFavorite.setOnClickListener {
                    if (!fav_selected6) {
                        fav_selected6 = true
                        binding.btnLunacupShortyLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 쇼티", "라지", 25, 46f, 76)

                    } else if (fav_selected6) {
                        fav_selected6 = false
                        binding.btnLunacupShortyLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 쇼티", "라지")
                    }
                }
                review(binding, cupname)
            }

            "루나컵 클래식" -> {
                fav_selected7 = false
                fav_selected8 = false
                binding.clLunacupClassic.visibility = View.VISIBLE
                binding.btnLunacupClassicSmallFavorite.setOnClickListener {
                    if (!fav_selected7) {
                        fav_selected7 = true
                        binding.btnLunacupClassicSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 클래식", "스몰", 20, 42f, 70)

                    } else if (fav_selected7) {
                        fav_selected7 = false
                        binding.btnLunacupClassicSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 클래식", "스몰")
                    }
                }
                binding.btnLunacupClassicLargeFavorite.setOnClickListener {
                    if (!fav_selected8) {
                        fav_selected8 = true
                        binding.btnLunacupClassicLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("루나컵 클래식", "라지", 25, 46f, 76)

                    } else if (fav_selected8) {
                        fav_selected8 = false
                        binding.btnLunacupClassicLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("루나컵 클래식", "라지")
                    }
                }
                review(binding, cupname)
            }

            "한나컵" -> {
                fav_selected9 = false
                fav_selected10 = false
                fav_selected11 = false
                binding.clHannahcup.visibility = View.VISIBLE
                binding.btnHannahcupExtrasmallFavorite.setOnClickListener {
                    if (!fav_selected9) {
                        fav_selected9 = true
                        binding.btnHannahcupExtrasmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("한나컵", "엑스트라 스몰", 16, 36.8f, 64)

                    } else if (fav_selected9) {
                        fav_selected9 = false
                        binding.btnHannahcupExtrasmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("한나컵", "엑스트라 스몰")
                    }
                }
                binding.btnHannahcupSmallFavorite.setOnClickListener {
                    if (!fav_selected10) {
                        fav_selected10 = true
                        binding.btnHannahcupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("한나컵", "스몰", 23, 40f, 66)

                    } else if (fav_selected10) {
                        fav_selected10 = false
                        binding.btnHannahcupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("한나컵", "스몰")
                    }
                }
                binding.btnHannahcupMediumFavorite.setOnClickListener {
                    if (!fav_selected11) {
                        fav_selected11 = true
                        binding.btnHannahcupMediumFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("한나컵", "미디움", 30, 44f, 71)

                    } else if (fav_selected11) {
                        fav_selected11 = false
                        binding.btnHannahcupMediumFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("한나컵", "미디움")
                    }
                }
                review(binding, cupname)
            }

            "이브컵" -> {
                fav_selected12 = false
                fav_selected13 = false
                fav_selected14 = false
                binding.clEvecup.visibility = View.VISIBLE
                binding.btnEvecupMiniFavorite.setOnClickListener {
                    if (!fav_selected12) {
                        fav_selected12 = true
                        binding.btnEvecupMiniFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("이브컵", "미니", 20, 42f, 60)

                    } else if (fav_selected12) {
                        fav_selected12 = false
                        binding.btnEvecupMiniFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("이브컵", "미니")
                    }
                }
                binding.btnEvecupSmallFavorite.setOnClickListener {
                    if (!fav_selected13) {
                        fav_selected13 = true
                        binding.btnEvecupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("이브컵", "스몰", 25, 42f, 70)

                    } else if (fav_selected13) {
                        fav_selected13 = false
                        binding.btnEvecupSmallFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("이브컵", "스몰")
                    }
                }
                binding.btnEvecupLargeFavorite.setOnClickListener {
                    if (!fav_selected14) {
                        fav_selected14 = true
                        binding.btnEvecupLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_active)
                        Toast.makeText(this, "관심 컵에 추가되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 추가
                        saveInterest("이브컵", "라지", 30, 45.5f, 70)

                    } else if (fav_selected14) {
                        fav_selected14 = false
                        binding.btnEvecupLargeFavorite.setBackgroundResource(R.drawable.btn_favorite_inactive)
                        Toast.makeText(this, "관심 컵에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        // 파이어베이스에 관심컵 삭제
                        deleteInterest("이브컵", "라지")
                    }
                }
                review(binding, cupname)
            }
        }

    }

    private fun review(binding: ActivityBrandDetailBinding, cupname: String) {
        // 파이어베이스에서 전체 후기 가져오기(옵션 선택X) (후기 로직은 다 짜놔서 전체후기랑 제일 하단 optionReview() 부분 파베에서 가져와서 리사이클러뷰에 보여주는 코드만 추가해주시면 됩니다!)
        reviewAdapter.data = mutableListOf<ReviewData>()

        Log.e("review 함수 호출", "되었습니다")
        // 파이어베이스에서 전체 후기 가져오기

        docRef.get()
            .addOnSuccessListener { document ->
                reviewAdapter.data.clear()
                if (document.get("rv_num") != null) {

                    val num = "${document.get("list_num")}"
                    val temp = num.toInt()
                    var len2 = ""
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

                            if (len != "")
                                len2 = len.replace("mm", "")

                            if (product == cupname) {
                                binding.rvBrandReview.visibility =
                                    View.VISIBLE
                                binding.tvBranddetailDefault.visibility =
                                    View.GONE
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
                            Log.e(
                                "테스트",
                                "$product $cupsize $period $feeling $length $size $hardness"
                            )
                        }
                    }
                } else {
                    binding.rvBrandReview.visibility = View.GONE
                    binding.tvBranddetailDefault.visibility = View.VISIBLE
                }
            }//docRef 끝


        //
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)

        // 여기부터 옵션 선택
        // 사이즈 옵션
        binding.clBrandReviewOption1.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var popupView = inflater.inflate(R.layout.popup_size1, null)

            when (cupname) {
                "루나컵 링" -> {
                    popupView = inflater.inflate(R.layout.popup_size1, null)
                }
                "루나컵 쇼티" -> {
                    popupView = inflater.inflate(R.layout.popup_size1, null)
                }
                "루나컵 클래식" -> {
                    popupView = inflater.inflate(R.layout.popup_size2, null)
                }
                "한나컵" -> {
                    popupView = inflater.inflate(R.layout.popup_size3, null)
                }
                "이브컵" -> {
                    popupView = inflater.inflate(R.layout.popup_size4, null)
                }
            }
            val popupWindow = PopupWindow(
                popupView,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindowSlide(popupWindow)
            popupWindow.setOnDismissListener {
                btn1selected = false
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
            }

            if (!btn1selected) {
                btn1selected = true
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_up)
            } else {
                btn1selected = false
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
            }
            when (cupname) {
                "루나컵 링", "루나컵 쇼티" -> {
//                    popupWindow.showAsDropDown(binding.btnBrandReviewOption1, -46 * (metrics.densityDpi / 160), 8 * (metrics.densityDpi / 160))
                    popupWindow.showAsDropDown(
                        binding.btnBrandReviewOption1,
                        dpToPx(this, -51),
                        dpToPx(this, 8)
                    )
                    val size11 = popupView.findViewById<TextView>(R.id.popup_size11)
                    val size12 = popupView.findViewById<TextView>(R.id.popup_size12)
                    val size13 = popupView.findViewById<TextView>(R.id.popup_size13)

                    when (option1_id) {
                        "타이니" -> size11.setTextColor(Color.BLACK)
                        "스몰" -> size12.setTextColor(Color.BLACK)
                        "라지" -> size13.setTextColor(Color.BLACK)
                    }
                    optionSize11Event(popupWindow, size11, size12, size13)
                    optionSize12Event(popupWindow, size11, size12, size13)
                    optionSize13Event(popupWindow, size11, size12, size13)
                }
                "루나컵 클래식" -> {
//                    popupWindow.showAsDropDown(binding.btnBrandReviewOption1, -36 * (metrics.densityDpi / 160), 8 * (metrics.densityDpi / 160))
                    popupWindow.showAsDropDown(
                        binding.btnBrandReviewOption1,
                        dpToPx(this, -41),
                        dpToPx(this, 8)
                    )
                    val size21 = popupView.findViewById<TextView>(R.id.popup_size21)
                    val size22 = popupView.findViewById<TextView>(R.id.popup_size22)

                    when (option1_id) {
                        "스몰" -> size21.setTextColor(Color.BLACK)
                        "라지" -> size22.setTextColor(Color.BLACK)
                    }
                    optionSize21Event(popupWindow, size21, size22)
                    optionSize22Event(popupWindow, size21, size22)
                }
                "한나컵" -> {
//                    popupWindow.showAsDropDown(binding.btnBrandReviewOption1, -84 * (metrics.densityDpi / 160), 8 * (metrics.densityDpi / 160))
                    popupWindow.showAsDropDown(
                        binding.btnBrandReviewOption1,
                        dpToPx(this, -89),
                        dpToPx(this, 8)
                    )
                    val size31 = popupView.findViewById<TextView>(R.id.popup_size31)
                    val size32 = popupView.findViewById<TextView>(R.id.popup_size32)
                    val size33 = popupView.findViewById<TextView>(R.id.popup_size33)

                    when (option1_id) {
                        "엑스트라 스몰" -> size31.setTextColor(Color.BLACK)
                        "스몰" -> size32.setTextColor(Color.BLACK)
                        "미디움" -> size33.setTextColor(Color.BLACK)
                    }
                    optionSize31Event(popupWindow, size31, size32, size33)
                    optionSize32Event(popupWindow, size31, size32, size33)
                    optionSize33Event(popupWindow, size31, size32, size33)
                }
                "이브컵" -> {
//                    popupWindow.showAsDropDown(binding.btnBrandReviewOption1, -36 * (metrics.densityDpi / 160), 8 * (metrics.densityDpi / 160))
                    popupWindow.showAsDropDown(
                        binding.btnBrandReviewOption1,
                        dpToPx(this, -41),
                        dpToPx(this, 8)
                    )
                    val size41 = popupView.findViewById<TextView>(R.id.popup_size41)
                    val size42 = popupView.findViewById<TextView>(R.id.popup_size42)
                    val size43 = popupView.findViewById<TextView>(R.id.popup_size43)

                    when (option1_id) {
                        "미니" -> size41.setTextColor(Color.BLACK)
                        "스몰" -> size42.setTextColor(Color.BLACK)
                        "라지" -> size43.setTextColor(Color.BLACK)
                    }
                    optionSize41Event(popupWindow, size41, size42, size43)
                    optionSize42Event(popupWindow, size41, size42, size43)
                    optionSize43Event(popupWindow, size41, size42, size43)
                }
            }
        }

        // 질 길이 옵션
        binding.clBrandReviewOption2.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_review, null)
            val popupWindow = PopupWindow(
                popupView,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindowSlide(popupWindow)

//            popupWindow.showAsDropDown(binding.btnBrandReviewOption2, -110 * (metrics.densityDpi / 160), 8 * (metrics.densityDpi / 160), Gravity.END)
            popupWindow.showAsDropDown(
                binding.btnBrandReviewOption2,
                dpToPx(this, -102),
                dpToPx(this, 8)
            )
            popupWindow.setOnDismissListener {
                btn2selected = false
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
            }

            val length1 = popupView.findViewById<TextView>(R.id.popup_length1)
            val length2 = popupView.findViewById<TextView>(R.id.popup_length2)
            val length3 = popupView.findViewById<TextView>(R.id.popup_length3)

            if (!btn2selected) { // 버튼 클릭되지 않은 상태에서 클릭함 -> 옵션창 보여줌
                btn2selected = true
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_up)
            } else {    // 버튼 클릭된 상태에서 다시 클릭함 -> 옵션창 닫음
                btn2selected = false
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
            }
            when (option2_id) {
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
            optionLength1Event(popupWindow, length1, length2, length3)
            optionLength2Event(popupWindow, length1, length2, length3)
            optionLength3Event(popupWindow, length1, length2, length3)
        }
    }

    private fun popupWindowSlide(popupWindow: PopupWindow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.TOP
            popupWindow.exitTransition = slideOut
        }
    }

    // 컵 사이즈 옵션 이벤트 함수
    private fun optionSize11Event(
        popupWindow: PopupWindow,
        size11: TextView,
        size12: TextView,
        size13: TextView
    ) {
        size11.setOnClickListener {
            if (option1_id == "타이니") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 타이니 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size11.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "타이니"
                Log.e("타이니 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size11.setTextColor(Color.BLACK)
                size12.setTextColor(Color.parseColor("#c5c7cc"))
                size13.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size11.text.toString()
                this.option1_id = size11.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize12Event(
        popupWindow: PopupWindow,
        size11: TextView,
        size12: TextView,
        size13: TextView
    ) {
        size12.setOnClickListener {
            if (option1_id == "스몰") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 스몰 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size12.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "스몰"
                Log.e("스몰 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size12.setTextColor(Color.BLACK)
                size11.setTextColor(Color.parseColor("#c5c7cc"))
                size13.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size12.text.toString()
                this.option1_id = size12.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize13Event(
        popupWindow: PopupWindow,
        size11: TextView,
        size12: TextView,
        size13: TextView
    ) {
        size13.setOnClickListener {
            if (option1_id == "라지") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 라지 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size13.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "라지"
                Log.e("라지 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size13.setTextColor(Color.BLACK)
                size11.setTextColor(Color.parseColor("#c5c7cc"))
                size12.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size13.text.toString()
                this.option1_id = size13.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize21Event(popupWindow: PopupWindow, size21: TextView, size22: TextView) {
        size21.setOnClickListener {
            if (option1_id == "스몰") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 스몰 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size21.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "스몰"
                Log.e("스몰 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size21.setTextColor(Color.BLACK)
                size22.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size21.text.toString()
                this.option1_id = size21.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize22Event(popupWindow: PopupWindow, size21: TextView, size22: TextView) {
        size22.setOnClickListener {
            if (option1_id == "라지") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 라지 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size22.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "라지"
                Log.e("라지 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size22.setTextColor(Color.BLACK)
                size21.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size22.text.toString()
                this.option1_id = size22.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize31Event(
        popupWindow: PopupWindow,
        size31: TextView,
        size32: TextView,
        size33: TextView
    ) {
        size31.setOnClickListener {
            if (option1_id == "엑스트라 스몰") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 엑스트라 스몰 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)

                size31.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "엑스트라 스몰"
                Log.e("엑스트라 스몰 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size31.setTextColor(Color.BLACK)
                size32.setTextColor(Color.parseColor("#c5c7cc"))
                size33.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size31.text.toString()
                this.option1_id = size31.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize32Event(
        popupWindow: PopupWindow,
        size31: TextView,
        size32: TextView,
        size33: TextView
    ) {
        size32.setOnClickListener {
            if (option1_id == "스몰") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 스몰 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size32.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "스몰"
                Log.e("스몰 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size32.setTextColor(Color.BLACK)
                size31.setTextColor(Color.parseColor("#c5c7cc"))
                size33.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size32.text.toString()
                this.option1_id = size32.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize33Event(
        popupWindow: PopupWindow,
        size31: TextView,
        size32: TextView,
        size33: TextView
    ) {
        size33.setOnClickListener {
            if (option1_id == "미디움") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 미디움 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size33.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "미디움"
                Log.e("미디움 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size33.setTextColor(Color.BLACK)
                size31.setTextColor(Color.parseColor("#c5c7cc"))
                size32.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size33.text.toString()
                this.option1_id = size33.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize41Event(
        popupWindow: PopupWindow,
        size41: TextView,
        size42: TextView,
        size43: TextView
    ) {
        size41.setOnClickListener {
            if (option1_id == "미니") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 미니 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size41.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "미니"
                Log.e("미니 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size41.setTextColor(Color.BLACK)
                size42.setTextColor(Color.parseColor("#c5c7cc"))
                size43.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size41.text.toString()
                this.option1_id = size41.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize42Event(
        popupWindow: PopupWindow,
        size41: TextView,
        size42: TextView,
        size43: TextView
    ) {
        size42.setOnClickListener {
            if (option1_id == "스몰") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 스몰 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size42.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "스몰"
                Log.e("스몰 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size42.setTextColor(Color.BLACK)
                size41.setTextColor(Color.parseColor("#c5c7cc"))
                size43.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size42.text.toString()
                this.option1_id = size42.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionSize43Event(
        popupWindow: PopupWindow,
        size41: TextView,
        size42: TextView,
        size43: TextView
    ) {
        size43.setOnClickListener {
            if (option1_id == "라지") {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn1selected = false
                option1_id = ""
                Log.e("재선택해서 라지 선택해제", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size43.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = "사이즈"
                this.option1_id = ""
            } else {
                btn1selected = false
                option1_id = "라지"
                Log.e("라지 선택", option1_id)
                binding.btnBrandReviewOption1.setBackgroundResource(R.drawable.btn_align_down)
                size43.setTextColor(Color.BLACK)
                size41.setTextColor(Color.parseColor("#c5c7cc"))
                size42.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption1.text = size43.text.toString()
                this.option1_id = size43.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    // 질 길이 옵션 이벤트 함수
    private fun optionLength1Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length1.setOnClickListener {
            if (option2_id == 1) {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn2selected = false
                this.option2_id = 0
                Log.e("재선택해서 1번 선택해제", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = "질 길이"
            } else {
                btn2selected = false
                this.option2_id = 1
                Log.e("1번 선택", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length1.setTextColor(Color.BLACK)
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = length1.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionLength2Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length2.setOnClickListener {
            if (option2_id == 2) {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn2selected = false
                this.option2_id = 0
                Log.e("재선택해서 2번 선택해제", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = "질 길이"
            } else {
                btn2selected = false
                this.option2_id = 2
                Log.e("2번 선택", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length2.setTextColor(Color.BLACK)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = length2.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }

    private fun optionLength3Event(
        popupWindow: PopupWindow,
        length1: TextView,
        length2: TextView,
        length3: TextView
    ) {
        length3.setOnClickListener {
            if (option2_id == 3) {    // 이미 선택되어있는 경우 -> 옵션 해제되고 전체로
                btn2selected = false
                this.option2_id = 0
                Log.e("재선택해서 3번 선택해제", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length3.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = "질 길이"
            } else {
                btn2selected = false
                this.option2_id = 3
                Log.e("3번 선택", this.option2_id.toString())
                binding.btnBrandReviewOption2.setBackgroundResource(R.drawable.btn_align_down)
                length3.setTextColor(Color.BLACK)
                length1.setTextColor(Color.parseColor("#c5c7cc"))
                length2.setTextColor(Color.parseColor("#c5c7cc"))
                binding.tvBrandReviewOption2.text = length3.text.toString()
            }
            popupWindow.dismiss()
            // 옵션 2개 다 적용된 후기
            optionReview(binding, cupname)
        }
    }


    // 파이어베이스에서 각 변수 cupname(컵종류), option1_id(사이즈)와 option2_id(질길이)에 해당되는 후기 가져와서 리사이클러뷰에 보여주기
    // option1_id=""일 경우는 전체 사이즈 후기 가져오면 됩니다.

    private fun optionReview(binding: ActivityBrandDetailBinding, cupname: String) {

        Log.e("후기함수 사이즈", option1_id)
        Log.e("후기함수 길이", option2_id.toString())
        Log.e("컵 이름", cupname)

        //질길이
        when (option2_id) { //질길이
            1 -> {
                // 질 길이는 30mm~45mm이고 cupname(컵종류), option1_id(사이즈)에 해당하는 후기들 가져옴
                lenOption(30, 45)
            }
            2 -> {
                // 질 길이는 46mm~60mm이고 cupname(컵종류), option1_id(사이즈)에 해당하는 후기들 가져옴
                lenOption(46, 60)
            }
            3 -> {
                // 질 길이는 61mm 이상이고 cupname(컵종류), option1_id(사이즈)에 해당하는 후기들 가져옴
                lenOption(61, 70)
            }
            else -> {
                // 질 길이는 전체이고 cupname(컵종류), option1_id(사이즈)에 해당하는 후기들 가져옴
                lenOption(30, 70)
            }

        }
        //////////////////////////////////////////////////////////////


    }

    private fun lenOption(min: Int, max: Int) {
        binding.rvBrandReview.visibility = View.GONE
        binding.tvBranddetailDefault.visibility = View.VISIBLE

        db.collection("ALL").document("Review").get()
            .addOnSuccessListener {
                reviewAdapter.data.clear()
                db.collection("ALL").document("Review").get()
                    .addOnSuccessListener {
                        reviewAdapter.data.clear()

                        db.collection("ALL").document("Review").get()
                            .addOnSuccessListener { document ->
                                reviewAdapter.data.clear()
                                if (document.get("rv_num") != null) {
                                    val num = "${document.get("list_num")}"
                                    val temp = num.toInt()

                                    Log.e("몇개", "${document.get("rv_num")}")
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

                                            Log.e("시작테스트 길이", option2_id.toString())

                                            // 리뷰에 질길이 입력된 경우
                                            if (len != "")
                                                len2 = len.replace("mm", "")

                                            // 질길이가 전체이고 제품사이즈가 전체인 경우
                                            if(option2_id == 0 && option1_id == "")
                                            {
                                                if (product == cupname) {
                                                    binding.rvBrandReview.visibility =
                                                        View.VISIBLE
                                                    binding.tvBranddetailDefault.visibility =
                                                        View.GONE
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
                                            // 질길이가 옵션선택되었고 제품사이즈가 전체인 경우
                                            else if(option2_id != 0 && option1_id == ""){
                                                if (min <= len2.toInt() && max >= len2.toInt() && product == cupname) {
                                                    binding.rvBrandReview.visibility =
                                                        View.VISIBLE
                                                    binding.tvBranddetailDefault.visibility =
                                                        View.GONE
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
                                            // 질길이가 전체이고 제품사이즈가 옵션선택된 경우
                                            else if(option2_id == 0 && option1_id != ""){
                                                if (option1_id == cupsize && product == cupname) {
                                                    binding.rvBrandReview.visibility =
                                                        View.VISIBLE
                                                    binding.tvBranddetailDefault.visibility =
                                                        View.GONE
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
                                            // 질길이와 제품사이즈 모두 옵션선택된 경우
                                            else{
                                                if (min <= len2.toInt() && max >= len2.toInt() && option1_id == cupsize && product == cupname) {
                                                    binding.rvBrandReview.visibility =
                                                        View.VISIBLE
                                                    binding.tvBranddetailDefault.visibility =
                                                        View.GONE
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
                                        Log.e(
                                            "테스트",
                                            "$product $cupsize $period $feeling $length $size $hardness"
                                        )
                                    }//for문 종료
                                    reviewAdapter.notifyDataSetChanged()
                                } else {
                                    binding.rvBrandReview.visibility = View.GONE
                                    binding.tvBranddetailDefault.visibility = View.VISIBLE
                                }
                            }

                    }//docRef 끝
            }
    }

    private fun deleteInterest(cupname: String, cupsize: String) {
        docRef_mp.get().addOnSuccessListener { document ->
            val listnum = "${document.get("list_num")}".toInt()
            val likenum = "${document.get("like_num")}".toInt()

            if (likenum > 1) {
                for (i in 1..listnum) {
                    if (document.get("like_list$i") != null) {
                        val name = document.get("like_list$i.cupname") as String
                        val csize = document.get("like_list$i.cupsize") as String

                        if (name == cupname && csize == cupsize) {
                            val updates = hashMapOf<String, Any>(
                                "like_list$i" to FieldValue.delete(),
                                "like_num" to likenum - 1
                            )
                            docRef_mp.update(updates).addOnCompleteListener {}
                        }
                    }
                }
            } else {
                for (i in 1..listnum) {
                    if (document.get("like_list$i") != null) {
                        val name = document.get("like_list$i.cupname") as String
                        val csize = document.get("like_list$i.cupsize") as String

                        if (name == cupname && csize == cupsize) {
                            val updates = hashMapOf<String, Any>(
                                "like_list$i" to FieldValue.delete(),
                                "like_num" to FieldValue.delete(),
                                "list_num" to FieldValue.delete()
                            )
                            docRef_mp.update(updates).addOnCompleteListener {}
                        }
                    }
                }
            }
        }
    }

    private fun saveInterest(
        cupname: String,
        cupsize: String,
        amount: Int,
        diameter: Float,
        length: Int
    ) {
        docRef_mp.get().addOnSuccessListener { document ->
            if (document.get("like_num") == null || "${document.get("like_num")}".toInt() <= 0) {
                val data = hashMapOf(
                    "like_list1" to mapOf(
                        "cupname" to cupname,
                        "cupsize" to cupsize,
                        "amount" to amount,
                        "diameter" to diameter,
                        "length" to length
                    ),
                    "like_num" to 1,
                    "list_num" to 1
                )
                docRef_mp.set(data, SetOptions.merge())
            } else {
                var temp = "${document.get("list_num")}"
                var temp2 = "${document.get("like_num")}"
                val num = temp.toInt() + 1
                val num2 = temp2.toInt() + 1

                val data = hashMapOf(
                    "like_list$num" to mapOf(
                        "cupname" to cupname,
                        "cupsize" to cupsize,
                        "amount" to amount,
                        "diameter" to diameter,
                        "length" to length
                    ),
                    "like_num" to num2,
                    "list_num" to num
                )
                docRef_mp.set(data, SetOptions.merge())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "branddetail: $option1_id 두번째는 $option2_id")

        // 전체 후기 보여주는 걸로 초기화
        binding.tvBrandReviewOption1.text = "사이즈"
        binding.tvBrandReviewOption2.text = "질 길이"
        option1_id = ""
        option2_id = 0
        optionReview(binding, cupname)
    }
}