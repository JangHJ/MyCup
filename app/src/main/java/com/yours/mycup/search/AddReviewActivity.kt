package com.yours.mycup.search

import android.app.AlertDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityAddReviewBinding
import com.yours.mycup.mypage.MyAmountBottomSheet
import com.yours.mycup.mypage.ReviewCupBottonSheet
import com.yours.mycup.mypage.ReviewCupSizeBottomSheet
import com.yours.mycup.record.dpToPx
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class AddReviewActivity : AppCompatActivity(), ReviewCupBottonSheet.ReviewCupPassListener,
    ReviewCupSizeBottomSheet.ReviewCupSizePassListener {
    var product_list = arrayOf("루나컵 링", "루나컵 쇼티", "루나컵 클래식", "한나컵", "이브컵")
    var cupsize_list = arrayOf("타이니", "스몰", "라지")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var product = ""
    var cupsize = ""
    var requestCode = 0
    var period = ""
    var feeling = ""
    var length = ""
    var size = ""
    var hardness = ""
    var position = ""
    var am = ""
    var len = ""

    var rand = Random().nextInt(1000000)
    val userEmal = auth.currentUser?.email

    lateinit var binding: ActivityAddReviewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_review)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // 나의 후기에서 넘어온 경우
        if (intent.hasExtra("MYREVIEW")) {
            requestCode = intent.extras!!.getInt("MYREVIEW")

            product = intent.getStringExtra("product").toString()
            cupsize = intent.getStringExtra("cupsize").toString()
            period = intent.getStringExtra("period").toString()
            feeling = intent.getStringExtra("feeling").toString()
            length = intent.getStringExtra("length").toString()
            size = intent.getStringExtra("size").toString()
            hardness = intent.getStringExtra("hardness").toString()
            position = intent.getStringExtra("position").toString()

            Log.e("전달받음", "$product $cupsize $period $feeling $length $size $hardness $position")
            showInfo(binding)
        }

        binding.btnReviewClose.setOnClickListener {
            finish()
        }

        // 상품명
        binding.spinnerReviewCup.setOnClickListener {
            binding.spinnerReviewCupsize.text = null
            // 바텀시트
            val bottomSheetDialog = ReviewCupBottonSheet()
            bottomSheetDialog.show(supportFragmentManager, "reviewcupBottomSheet")
        }

        // 사이즈
        binding.spinnerReviewCupsize.setOnClickListener {
            when (product) {
                "루나컵 링" -> {
                    cupsize_list = arrayOf("타이니", "스몰", "라지")
                }
                "루나컵 쇼티" -> {
                    cupsize_list = arrayOf("타이니", "스몰", "라지")
                }
                "루나컵 클래식" -> {
                    cupsize_list = arrayOf("스몰", "라지")
                }
                "한나컵" -> {
                    cupsize_list = arrayOf("엑스트라 스몰", "스몰", "미디움")
                }
                // 이브컵
                "이브컵" -> {
                    cupsize_list = arrayOf("미니", "스몰", "라지")
                }
                else -> {
                    cupsize_list = arrayOf("타이니", "스몰", "라지")
                }
            }
            // 바텀시트
            val bottomSheetDialog = ReviewCupSizeBottomSheet()
            val bundle = Bundle()
            bundle.putString("product", product)
//            bundle.putStringArray("cupsize_list", cupsize_list)
            bottomSheetDialog.arguments = bundle
            bottomSheetDialog.show(supportFragmentManager, "reviewcupsizeBottomSheet")
        }

        periodChip(binding)
        feelingChip(binding)
        lengthChip(binding)
        sizeChip(binding)
        hardnessChip(binding)


        // 파이어베이스에 후기 저장
        binding.btnReviewSave.setOnClickListener {
            Log.e("입력값", "$product $cupsize $period $feeling $length $size $hardness")
            if (product.isNullOrBlank() || product == "null" || cupsize.isNullOrBlank() || cupsize == "null"
                || period.isNullOrBlank() || period == "null" || feeling.isNullOrBlank() || feeling == "null"
                || length.isNullOrBlank() || length == "null" || size.isNullOrBlank() || size == "null"
                || hardness.isNullOrBlank() || hardness == "null"
            ) {
                Toast.makeText(this, "항목을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (requestCode == 1) {
                    // 수정하시겠습니까? -> 취소선택
                    val dialog = AlertDialog.Builder(this).create()
                    val edialog: LayoutInflater = LayoutInflater.from(this)
                    val mView: View = edialog.inflate(R.layout.dialog_fix, null)
                    dialog.setView(mView)
                    dialog.show()
                    dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
                    dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))
                    dialog.create()

                    val cancel: TextView = mView.findViewById(R.id.cancel_button)
                    val fix: TextView = mView.findViewById(R.id.save_button)

                    // 취소 버튼 선택
                    cancel.setOnClickListener {
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    // 확인 버튼 선택
                    fix.setOnClickListener {
                        // 여기에 파이어베이스 원래 있던 후기 수정 코드 작성
                        val docRef_rv =
                            db.collection("${auth.currentUser?.email}").document("Review")
                        docRef_rv.get().addOnSuccessListener { document ->
                            if (document.get("rv_num") != null) {
                                val rvnum = "${document.get("rv_num")}".toInt()
                                val num = "${document.get("list_num")}".toInt() // 4
                                val pos = position.toInt() + 1 // -> 3 rvlist4가 수정되야함
                                var cnt = 0

                                for (i in 1..num) {
                                    if (document.get("rvlist$i") != null) {
                                        cnt++
                                        Log.e("position 값 : ", "$pos")
                                        if (cnt == pos) {
                                            //
                                            val rand_my = document.get("rvlist$i.random")
                                            Log.e("랜덤 마이 테스트 : ", "$rand_my")
                                            db.collection("ALL").document("Review").get()
                                                .addOnSuccessListener { document ->
                                                    var listnum_all = -1
                                                    if (document.get("list_num") != null)
                                                        listnum_all =
                                                            "${document.get("list_num")}".toInt()
                                                    val rvnum_all =
                                                        "${document.get("rv_num")}".toInt()

                                                    Log.e("리스트넘버 테스트 : ", "$listnum_all, $rvnum_all")

                                                    for (i in 1..listnum_all) {
                                                        if (document.get("rvlist$i.random") == rand_my) {
                                                            val updates =
                                                                hashMapOf<String, Any>(
                                                                    "rvlist$i" to mapOf(
                                                                        "period" to period,
                                                                        "product" to product,
                                                                        "cupsize" to cupsize,
                                                                        "feeling" to feeling,
                                                                        "length" to length,
                                                                        "size" to size,
                                                                        "hardness" to hardness
                                                                    )
                                                                )
                                                            db.collection("ALL")
                                                                .document("Review")
                                                                .set(
                                                                    updates,
                                                                    SetOptions.merge()
                                                                )
                                                        }
                                                    }

                                                }
                                            //

                                            val data = hashMapOf(
                                                "rvlist$i" to mapOf(
                                                    "period" to period,
                                                    "product" to product,
                                                    "cupsize" to cupsize,
                                                    "feeling" to feeling,
                                                    "length" to length,
                                                    "size" to size,
                                                    "hardness" to hardness
                                                )
                                            )
                                            db.collection("${auth.currentUser?.email}")
                                                .document("Review")
                                                .set(data, SetOptions.merge())
                                            break
                                        }
                                    }
                                }

                            }


                        }


                        // product -> 상품명 (루나컵 링, 루나컵 쇼티, 루나컵 클래식, 한나컵, 이브컵), cupsize -> 사이즈
                        // period, feeling, length, size, hardness


                        Toast.makeText(this, "수정되었습니다", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        dialog.cancel()
                        Log.e("저장", "$product $cupsize $period $feeling $length $size $hardness")
                        finish()
                    }
                } else {
                    // 여기에 파이어베이스 새로 추가한 후기 저장 코드 작성
                    if (binding.lessthan1month.isChecked == true) {
                        period = "${binding.lessthan1month.text}" // 1개월미만
                    } else if (binding.morethan1month.isChecked == true) {
                        period = "${binding.morethan1month.text}" // 1개월이상
                    } else if (binding.morethan3month.isChecked == true) {
                        period = "${binding.morethan3month.text}" // 3개월ㅇ이상
                    } else if (binding.morethan6month.isChecked == true) {
                        period = "${binding.morethan6month.text}" // 6개월이상
                    } else if (binding.morethan1year.isChecked == true) {
                        period = "${binding.morethan1year.text}" // 1년이상
                    } else {
                        Log.e("오류!", "기간 칩선택이 실패하였습니다.")
                    }

                    product = binding.spinnerReviewCup.text.toString()
                    cupsize = binding.spinnerReviewCupsize.text.toString()

                    if (binding.uncomfortable.isChecked == true) {
                        feeling = "${binding.uncomfortable.text}" // 불편해요
                    } else if (binding.soso.isChecked == true) {
                        feeling = "${binding.soso.text}" // 보통이에요
                    } else if (binding.comfortable.isChecked == true) {
                        feeling = "${binding.comfortable.text}" // 편안해요
                    } else {
                        Log.e("오류!", "착용감 칩선택이 실패하였습니다.")
                    }

                    if (binding.longLength.isChecked == true) {
                        length = "${binding.longLength.text}" // 길어요
                    } else if (binding.sosoLength.isChecked == true) {
                        length = "${binding.sosoLength.text}" // 보통이에요
                    } else if (binding.shortLength.isChecked == true) {
                        length = "${binding.shortLength.text}" // 짧아요
                    } else {
                        Log.e("오류!", "길이 칩선택이 실패하였습니다.")
                    }

                    if (binding.small.isChecked == true) {
                        size = "${binding.small.text}" // 작아요
                    } else if (binding.sosoSize.isChecked == true) {
                        size = "${binding.sosoSize.text}" // 보통이에요
                    } else if (binding.big.isChecked == true) {
                        size = "${binding.big.text}" // 커요
                    } else {
                        Log.e("오류!", "사이즈 칩선택이 실패하였습니다.")
                    }

                    if (binding.hard.isChecked == true) {
                        hardness = "${binding.hard.text}" // 단단해요
                    } else if (binding.sosoHardness.isChecked == true) {
                        hardness = "${binding.sosoHardness.text}" // 보통이에요
                    } else if (binding.soft.isChecked == true) {
                        hardness = "${binding.soft.text}" // 말랑해요
                    } else {
                        Log.e("오류!", "경도 칩선택이 실패하였습니다.")
                    }
                    //칩 값 코드 끝

                    //내 후기에 저장
                    // list_num: 파베에 저장된(삭제된 후기 포함) 모든 후기 개수
                    // rv_num: 파베에 저장된(삭제된 후기 제외) 모든 후기 개수
                    // i: 제일 마지막으로 저장될 후기가 (삭제포함)몇번째 후기인지
                    val docRef2 = db.collection("${auth.currentUser?.email}").document("Review")
                    db.collection("${auth.currentUser?.email}").document("Mypage").get()
                        .addOnSuccessListener { document ->
                            if (document.get("men_amount") != null)
                                am = document.get("men_amount") as String
                            if (document.get("vagina_len") != null)
                                len = document.get("vagina_len") as String
                            docRef2.get().addOnSuccessListener { document ->
                                if (document.get("rv_num") == null) {
                                    val data = hashMapOf(
                                        "rvlist1" to mapOf(
                                            "period" to period,
                                            "product" to product,
                                            "cupsize" to cupsize,
                                            "feeling" to feeling,
                                            "length" to length,
                                            "size" to size,
                                            "hardness" to hardness,
                                            "random" to rand,
                                            "am" to am,
                                            "len" to len
                                        ),
                                        "rv_num" to 1,
                                        "list_num" to 1
                                    )
                                    db.collection("${auth.currentUser?.email}").document("Review")
                                        .set(data, SetOptions.merge())
                                    //db.collection("User").document("${auth.currentUser?.email}").set(data, SetOptions.merge())
                                } else {
                                    var temp = "${document.get("rv_num")}"
                                    var temp2 = "${document.get("list_num")}"
                                    val num2 = temp2.toInt() + 1
                                    val num = temp.toInt() + 1
                                    val i = temp.toInt() + (num2 - temp.toInt())

                                    val data = hashMapOf(
                                        "rvlist$i" to mapOf(
                                            "period" to period,
                                            "product" to product,
                                            "cupsize" to cupsize,
                                            "feeling" to feeling,
                                            "length" to length,
                                            "size" to size,
                                            "hardness" to hardness,
                                            "random" to rand,
                                            "am" to am,
                                            "len" to len
                                        ),
                                        "rv_num" to num,
                                        "list_num" to num2
                                    )
                                    db.collection("${auth.currentUser?.email}").document("Review")
                                        .set(data, SetOptions.merge())
                                    //db.collection("User").document("${auth.currentUser?.email}").set(data, SetOptions.merge())
                                }
                            }
                        }
                    // 내 후기 저장 끝

                    Log.e("결과 : ", "$product, $period, $cupsize, $feeling, $size, $hardness")

                    // 전체 후기 저장
                    db.collection("${auth.currentUser?.email}").document("Mypage").get()
                        .addOnSuccessListener { document ->
                            if (document.get("men_amount") != null)
                                am = document.get("men_amount") as String
                            if (document.get("vagina_len") != null)
                                len = document.get("vagina_len") as String

                            val docRef = db.collection("ALL").document("Review")
                            docRef.get().addOnSuccessListener { document ->
                                if (document.get("rv_num") == null) {
                                    val data = hashMapOf(
                                        "rvlist1" to mapOf(
                                            "period" to period,
                                            "product" to product,
                                            "cupsize" to cupsize,
                                            "feeling" to feeling,
                                            "length" to length,
                                            "size" to size,
                                            "hardness" to hardness,
                                            "random" to rand,
                                            "am" to am,
                                            "len" to len,
                                            "email" to "${auth.currentUser?.email}"
                                        ),
                                        "rv_num" to 1,
                                        "list_num" to 1
                                    )
                                    db.collection("ALL").document("Review")
                                        .set(data, SetOptions.merge())
                                } else {
                                    var temp = "${document.get("rv_num")}"
                                    var temp2 = "${document.get("list_num")}"
                                    val num2 = temp2.toInt() + 1
                                    val num = temp.toInt() + 1
                                    val i = temp.toInt() + (num2 - temp.toInt())

                                    val data = hashMapOf(
                                        "rvlist$i" to mapOf(
                                            "period" to period,
                                            "product" to product,
                                            "cupsize" to cupsize,
                                            "feeling" to feeling,
                                            "length" to length,
                                            "size" to size,
                                            "hardness" to hardness,
                                            "random" to rand,
                                            "am" to am,
                                            "len" to len,
                                            "email" to "${auth.currentUser?.email}"
                                        ),
                                        "rv_num" to num,
                                        "list_num" to num2
                                    )
                                    db.collection("ALL").document("Review")
                                        .set(data, SetOptions.merge())
                                }
                            }
                        }
                    Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    //Log.e("저장", "$product $cupsize $period $feeling $length $size $hardness")
                    finish()
                }
            }
        }
    }

    // 사용기간 칩
    private fun periodChip(binding: ActivityAddReviewBinding) {
        val checked = arrayOf(false, false, false, false, false)
        val list = mutableListOf<String>()
        val chipGroup0 = binding.chipGroup
        for (index in 0 until chipGroup0.childCount) {
            val chip: Chip = chipGroup0.getChildAt(index) as Chip
            list.add(chip.text.toString())
        }
        binding.chipGroup.setOnCheckedChangeListener { view, isChecked ->
            checked[0] = binding.lessthan1month.isChecked
            checked[1] = binding.morethan1month.isChecked
            checked[2] = binding.morethan3month.isChecked
            checked[3] = binding.morethan6month.isChecked
            checked[4] = binding.morethan1year.isChecked

            period = if (!checked[0] && !checked[1] && !checked[2] && !checked[3] && !checked[4]) {
                ""
            } else {
                val idx = checked.indexOf(true)
                Log.e("idx", idx.toString())
                list[idx]
            }
            Log.e("선택결과", period)
        }
    }

    // 착용감 칩
    private fun feelingChip(binding: ActivityAddReviewBinding) {
        val checked = arrayOf(false, false, false)
        val list = mutableListOf<String>()
        val chipGroup0 = binding.chipGroup2
        for (index in 0 until chipGroup0.childCount) {
            val chip: Chip = chipGroup0.getChildAt(index) as Chip
            list.add(chip.text.toString())
        }
        binding.chipGroup2.setOnCheckedChangeListener { view, isChecked ->
            checked[0] = binding.uncomfortable.isChecked
            checked[1] = binding.soso.isChecked
            checked[2] = binding.comfortable.isChecked

            feeling = if (!checked[0] && !checked[1] && !checked[2]) {
                ""
            } else {
                val idx = checked.indexOf(true)
                Log.e("idx", idx.toString())
                list[idx]
            }
            Log.e("선택결과", feeling)
        }
    }

    // 길이 칩
    private fun lengthChip(binding: ActivityAddReviewBinding) {
        val checked = arrayOf(false, false, false)
        val list = mutableListOf<String>()
        val chipGroup0 = binding.chipGroup3
        for (index in 0 until chipGroup0.childCount) {
            val chip: Chip = chipGroup0.getChildAt(index) as Chip
            list.add(chip.text.toString())
        }
        binding.chipGroup3.setOnCheckedChangeListener { view, isChecked ->
            checked[0] = binding.longLength.isChecked
            checked[1] = binding.sosoLength.isChecked
            checked[2] = binding.shortLength.isChecked

            length = if (!checked[0] && !checked[1] && !checked[2]) {
                ""
            } else {
                val idx = checked.indexOf(true)
                Log.e("idx", idx.toString())
                list[idx]
            }
            Log.e("선택결과", length)
        }
    }

    // 사이즈 칩
    private fun sizeChip(binding: ActivityAddReviewBinding) {
        val checked = arrayOf(false, false, false)
        val list = mutableListOf<String>()
        val chipGroup0 = binding.chipGroup4
        for (index in 0 until chipGroup0.childCount) {
            val chip: Chip = chipGroup0.getChildAt(index) as Chip
            list.add(chip.text.toString())
        }
        binding.chipGroup4.setOnCheckedChangeListener { view, isChecked ->
            checked[0] = binding.small.isChecked
            checked[1] = binding.sosoSize.isChecked
            checked[2] = binding.big.isChecked

            size = if (!checked[0] && !checked[1] && !checked[2]) {
                ""
            } else {
                val idx = checked.indexOf(true)
                Log.e("idx", idx.toString())
                list[idx]
            }
            Log.e("선택결과", size)
        }
    }

    // 경도 칩
    private fun hardnessChip(binding: ActivityAddReviewBinding) {
        val checked = arrayOf(false, false, false)
        val list = mutableListOf<String>()
        val chipGroup0 = binding.chipGroup5
        for (index in 0 until chipGroup0.childCount) {
            val chip: Chip = chipGroup0.getChildAt(index) as Chip
            list.add(chip.text.toString())
        }
        binding.chipGroup5.setOnCheckedChangeListener { view, isChecked ->
            checked[0] = binding.hard.isChecked
            checked[1] = binding.sosoHardness.isChecked
            checked[2] = binding.soft.isChecked

            hardness = if (!checked[0] && !checked[1] && !checked[2]) {
                ""
            } else {
                val idx = checked.indexOf(true)
                Log.e("idx", idx.toString())
                list[idx]
            }
            Log.e("선택결과", hardness)
        }
    }

    // 나의 후기에서 넘어온 경우 기존 정보 보여주기
    private fun showInfo(binding: ActivityAddReviewBinding) {
        binding.spinnerReviewCup.setText(product)
        binding.spinnerReviewCupsize.setText(cupsize)
        when (period) {
            "1개월 미만" -> binding.lessthan1month.isChecked = true
            "1개월 이상" -> binding.morethan1month.isChecked = true
            "3개월 이상" -> binding.morethan3month.isChecked = true
            "6개월 이상" -> binding.morethan6month.isChecked = true
            "1년 이상" -> binding.morethan1year.isChecked = true
        }
        when (feeling) {
            "불편해요" -> binding.uncomfortable.isChecked = true
            "보통이에요" -> binding.soso.isChecked = true
            "편안해요" -> binding.comfortable.isChecked = true
        }
        when (length) {
            "길어요" -> binding.longLength.isChecked = true
            "보통이에요" -> binding.sosoLength.isChecked = true
            "짧아요" -> binding.shortLength.isChecked = true
        }
        when (size) {
            "작아요" -> binding.small.isChecked = true
            "보통이에요" -> binding.sosoSize.isChecked = true
            "커요" -> binding.big.isChecked = true
        }
        when (hardness) {
            "단단해요" -> binding.hard.isChecked = true
            "보통이에요" -> binding.sosoHardness.isChecked = true
            "말랑해요" -> binding.soft.isChecked = true
        }
    }

    // 상품명 바텀 시트
    override fun onReviewCupPass(data: Int?) {
        product = product_list[data!!]
        binding.spinnerReviewCup.setText(product_list[data])
    }

    // 사이즈 바텀 시트
    override fun onReviewCupSizePass(data: Int?) {
        cupsize = cupsize_list[data!!]
        binding.spinnerReviewCupsize.setText(cupsize_list[data])
    }

}