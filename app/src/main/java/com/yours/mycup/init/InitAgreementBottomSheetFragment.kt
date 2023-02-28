package com.yours.mycup.init

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInitAgreementBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class InitAgreementBottomSheetFragment : BottomSheetDialogFragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentInitAgreementBottomSheetBinding.inflate(inflater, container, false)

        binding.checkbox.isChecked = false
        binding.nextBtn.isEnabled = false
        binding.agreeText.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/haneulKimaa/mycup_terms_conditions/blob/main/README.md"))
            startActivity(intent)
        }
        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.nextBtn.isEnabled = binding.checkbox.isChecked == true
        }
        binding.nextBtn.setOnClickListener {
            // 여기서 각 user가 이용약관 동의했는지 데이터 올려줘야 함
            isAgree()
            // 이후부터 이용약관 띄우면 안되기 때문
            this.dismiss()
        }

        return binding.root
    }

    private fun isAgree(){
        val data = hashMapOf(
            "isAgreed" to true
        )
        db.collection("${auth.currentUser?.email}").document("Init").set(data, SetOptions.merge())
    }

}