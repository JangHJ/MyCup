package com.yours.mycup.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivitySituationBinding
import com.yours.mycup.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase


class MypageFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    lateinit var binding: FragmentMypageBinding

    var amount = ""
    var len = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage, container, false)

        // 사용자 계정 가져와서 보여주기
        binding.tvMypgAccount.text = Firebase.auth.currentUser?.email.toString()

        val docRef1 = db.collection("${auth.currentUser?.email}").document("Mypage")
        docRef1.get().addOnSuccessListener { document ->
            Log.e("질 길이 : ", "${document.get("vagina_len")}")
            Log.e("월경량 : ", "${document.get("men_amount")}")

            if(document.get("men_amount") != null)
                amount = document.get("men_amount") as String

            if(document.get("vagina_len") != null)
                len = document.get("vagina_len") as String

            if(document.get("vagina_len") != null && document.get("men_amount") != null)
                binding.tvMypgInfo.text = "질 길이 $len • 월경량 $amount"
            else if(document.get("vagina_len") == null && document.get("men_amount") != null)
                binding.tvMypgInfo.text = "질 길이를 입력해 주세요 • 월경량 $amount"
            else if(document.get("vagina_len") != null && document.get("men_amount") == null)
                binding.tvMypgInfo.text = "질 길이 $len • 월경량을 입력해 주세요"
            else
                binding.tvMypgInfo.text = "질 길이와 월경량이 입력되어 있지 않습니다"


        }


        // 사용자 정보 가져와서 보여주기
//        binding.tvMypgInfo.text

        binding.cl1.setOnClickListener{
            val intent = Intent(activity, MyAppSettingActivity::class.java)
            startActivity(intent)
        }

        binding.tvMypgReview.setOnClickListener{
            val intent = Intent(activity, MyReviewActivity::class.java)
            startActivity(intent)
        }

        binding.tvMypgInterest.setOnClickListener{
            val intent = Intent(activity, MyInterestActivity::class.java)
            startActivity(intent)
        }

        binding.tvMypgLength.setOnClickListener{
            val intent = Intent(activity, MyLengthActivity::class.java)
            startActivity(intent)
        }

        binding.tvMypgAmount.setOnClickListener{
            val intent = Intent(activity, MyAmountActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val docRef1 = db.collection("${auth.currentUser?.email}").document("Mypage")
        docRef1.get().addOnSuccessListener { document ->
            Log.e("질 길이 : ", "${document.get("vagina_len")}")
            Log.e("월경량 : ", "${document.get("men_amount")}")

            if(document.get("men_amount") != null)
                amount = document.get("men_amount") as String

            if(document.get("vagina_len") != null)
                len = document.get("vagina_len") as String

            if(document.get("vagina_len") != null && document.get("men_amount") != null)
                binding.tvMypgInfo.text = "질 길이 $len • 월경량 $amount"
            else if(document.get("vagina_len") == null && document.get("men_amount") != null)
                binding.tvMypgInfo.text = "질 길이를 입력해 주세요 • 월경량 $amount"
            else if(document.get("vagina_len") != null && document.get("men_amount") == null)
                binding.tvMypgInfo.text = "질 길이 $len • 월경량을 입력해 주세요"
            else
                binding.tvMypgInfo.text = "질 길이와 월경량이 입력되어 있지 않습니다"
        }
    }
}