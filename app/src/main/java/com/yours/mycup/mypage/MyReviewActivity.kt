package com.yours.mycup.mypage

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityBrandDetailBinding
import com.yours.mycup.databinding.ActivityMyReviewBinding
import com.yours.mycup.init.LoginActivity
import com.yours.mycup.mypage.RV.MyReviewAdapter
import com.yours.mycup.record.MenstruationActivity
import com.yours.mycup.record.dpToPx
import com.yours.mycup.search.AddReviewActivity
import com.yours.mycup.search.BrandDetailActivity
import com.yours.mycup.search.RV.ReviewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase


class MyReviewActivity : AppCompatActivity(), BottomSheetDialog.BottomSheetButtonClickListener {
    lateinit var reviewAdapter: MyReviewAdapter
    lateinit var mFragmentManager: FragmentManager
    lateinit var datas: MutableList<ReviewData>

    lateinit var position1: String

    var product = ""
    var cupsize = ""
    var period = ""
    var feeling = ""
    var length = ""
    var size = ""
    var hardness = ""

    var am = ""
    var len = ""

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val docRef = db.collection("${auth.currentUser?.email}").document("Review")

    lateinit var binding: ActivityMyReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_review)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        mFragmentManager = this.supportFragmentManager
        reviewAdapter = MyReviewAdapter(this, mFragmentManager)
        binding.rvMyReview.adapter = reviewAdapter
        binding.rvMyReview.layoutManager = LinearLayoutManager(this)
        datas = reviewAdapter.data
        datas.clear()

        // viewholder에서 제어하지 않고 activity에서 아이템 클릭 이벤트 제어
        reviewAdapter.setOnItemClickListener(object : MyReviewAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.
                position1 = position.toString()
                val reviewData: ReviewData = reviewAdapter.data[position]

                val bottomSheetDialog = BottomSheetDialog()
                val bundle = Bundle()
                bundle.putString("product", reviewData.cupname)
                bundle.putString("cupsize", reviewData.cupsize)
                bundle.putString("period", reviewData.userexperience)
                bundle.putString("feeling", reviewData.feeling)
                bundle.putString("length", reviewData.length)
                bundle.putString("size", reviewData.size)
                bundle.putString("hardness", reviewData.hardness)
                bundle.putString("position", position.toString())
                bottomSheetDialog.arguments = bundle

                bottomSheetDialog.show(mFragmentManager, "myreviewBottomSheet")
            }
        })

        binding.btnMyreviewBack.setOnClickListener {
            finish()
        }

        binding.btnMyreviewAdd.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            startActivity(intent)
        }

        // 특정 글자 색깔 변경
        val content = binding.tvMyreviewDefault.text.toString()
        val spannableString = SpannableString(content)

        val word = "월경컵 사용 후기"
        val start = content.indexOf(word)
        val end = start + word.length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#e16248")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvMyreviewDefault.text = spannableString

        // 파이어베이스에서 나의 후기 가져오기
        //myReviewFB()
    }

    override fun onEditButtonClicked() {

    }


    // 바텀시트 삭제 버튼 눌렀을 때
    override fun onDeleteButtonClicked() {
        // 다이얼로그
        val dialog = AlertDialog.Builder(this).create()
        val edialog: LayoutInflater = LayoutInflater.from(this)
        val mView: View = edialog.inflate(R.layout.dialog_delete, null)
        dialog.setView(mView)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
        dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))
        dialog.create()

        val cancel: TextView = mView.findViewById(R.id.dialog_delete_no)
        val ok: TextView = mView.findViewById(R.id.dialog_delete_ok)

        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        ok.setOnClickListener {
            reviewAdapter.data.removeAt(position1.toInt())
            reviewAdapter.notifyItemRemoved(position1.toInt())
            // 파이어베이스에서 후기 삭제 코드 작성

            docRef.get().addOnSuccessListener { document ->
                if (document.get("rv_num") != null) {
                    val rvnum = "${document.get("rv_num")}".toInt()
                    if (rvnum == 1) {
                        //
                        val num = "${document.get("list_num")}".toInt()
                        val pos = position1.toInt() + 1
                        var cnt = 0

                        for (i in 1..num) {
                            if (document.get("rvlist$i") != null) {
                                cnt++
                                Log.e("position 값 : ", "$pos")
                                if (cnt == pos) {
                                    val rand_my = document.get("rvlist$i.random")
                                    //
                                    db.collection("ALL").document("Review").get()
                                        .addOnSuccessListener { document ->

                                            var listnum_all = 1
                                            if (document.get("list_num") != null)
                                                listnum_all =
                                                    "${document.get("list_num")}".toInt()
                                            var rvnum_all = 0
                                            if(document.get("rv_num") != null)
                                                rvnum_all = "${document.get("rv_num")}".toInt()

                                            var rand_all = 0
                                            if(document.get("rvlist$i.random") != null)
                                                rand_all = "${document.get("rvlist$i.random")}".toInt()

                                            if (rvnum_all <= 1) {
                                                db.collection("ALL").document("Review").delete()
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            TAG,
                                                            "DocumentSnapshot successfully deleted!"
                                                        )
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            TAG,
                                                            "Error deleting document",
                                                            e
                                                        )
                                                    }
                                            } else {
                                                for (i in 1..listnum_all) {
                                                    if (document.get("rvlist$i.random") == rand_my) {
                                                        val updates = hashMapOf<String, Any>(
                                                            "rvlist$i" to FieldValue.delete(),
                                                            "rv_num" to rvnum_all - 1
                                                        )
                                                        db.collection("ALL").document("Review")
                                                            .update(updates)
                                                            .addOnCompleteListener {}
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                        //

                        docRef.delete()
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot successfully deleted!"
                                )
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                        //Log.e("이퀄 투 테스트 : ", "$test")
                    } else {
                        val num = "${document.get("list_num")}".toInt()
                        val pos = position1.toInt() + 1
                        var cnt = 0

                        for (i in 1..num) {
                            if (document.get("rvlist$i") != null) {
                                cnt++
                                Log.e("position 값 : ", "$pos")
                                if (cnt == pos) {
                                    val rand_my = document.get("rvlist$i.random")
                                    //
                                    db.collection("ALL").document("Review").get()
                                        .addOnSuccessListener { document ->

                                            var listnum_all = -1
                                            if (document.get("list_num") != null)
                                                listnum_all =
                                                    "${document.get("list_num")}".toInt()
                                            var rvnum_all = 0
                                            if(document.get("rv_num") != null)
                                               rvnum_all = "${document.get("rv_num")}".toInt()

                                            var rand_all = 0
                                            if(document.get("rvlist$i.random") != null)
                                                rand_all = "${document.get("rvlist$i.random")}".toInt()

                                            if (rvnum_all <= 1) {
                                                db.collection("ALL").document("Review").delete()
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            TAG,
                                                            "DocumentSnapshot successfully deleted!"
                                                        )
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            TAG,
                                                            "Error deleting document",
                                                            e
                                                        )
                                                    }
                                            } else {
                                                for (i in 1..listnum_all) {
                                                    if (document.get("rvlist$i.random") == rand_my) {
                                                        val updates = hashMapOf<String, Any>(
                                                            "rvlist$i" to FieldValue.delete(),
                                                            "rv_num" to rvnum_all - 1
                                                        )
                                                        db.collection("ALL").document("Review")
                                                            .update(updates)
                                                            .addOnCompleteListener {}
                                                    }
                                                }
                                            }
                                        }
                                    //
                                    val updates = hashMapOf<String, Any>(
                                        "rvlist$i" to FieldValue.delete(),
                                        "rv_num" to rvnum - 1
                                    )
                                    docRef.update(updates).addOnCompleteListener {}
                                    break
                                }
                            }
                        }
                    }
                }
            }

            Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
            dialog.cancel()
        }
    }

    private fun myReviewFB() {
        db.collection("${auth.currentUser?.email}").document("Review").get()
            .addOnSuccessListener {
                datas.clear()
                // 파이어베이스에서 나의 후기 가져오기 (일단 더미로 넣어놨음)
                db.collection("${auth.currentUser?.email}").document("Review").get()
                    .addOnSuccessListener { document ->
                        if (document.get("rv_num") != null) {

                            binding.clMyreview.visibility = View.VISIBLE
                            binding.clMyreviewDefault.visibility = View.GONE

                            val num = "${document.get("list_num")}"
                            val temp = num.toInt()

                            for (i in 1..temp) {
                                if (document.get("rvlist$i") != null) {
                                    product = document.get("rvlist$i.product") as String
                                    cupsize = document.get("rvlist$i.cupsize") as String
                                    period = document.get("rvlist$i.period") as String
                                    feeling = document.get("rvlist$i.feeling") as String
                                    length = document.get("rvlist$i.length") as String
                                    size = document.get("rvlist$i.size") as String
                                    hardness = document.get("rvlist$i.hardness") as String
                                    am = document.get("rvlist$i.am") as String
                                    len = document.get("rvlist$i.len") as String

                                    //리뷰 추가
                                    datas.add(
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
                                    "$product $cupsize $period $feeling $length $size $hardness $am $len"
                                )
                                //DataList.add(Data(timeRange, "테스트", 10, type, color))
                            }
                        }
                        else{
                            binding.clMyreview.visibility = View.GONE
                            binding.clMyreviewDefault.visibility = View.VISIBLE
                        }
                    }
            }


    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "myreview")

        // 파이어베이스에서 나의 후기 가져오기
        reviewAdapter.data.clear()
        myReviewFB()
    }
}