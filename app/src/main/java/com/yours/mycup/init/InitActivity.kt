package com.yours.mycup.init

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yours.mycup.MainActivity
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityInitBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.io.File
import java.lang.System.out
import java.time.LocalDate
import java.util.jar.Manifest


class InitActivity : AppCompatActivity(),
    InitSettingStartDateBottomSheetFragment.onStartDatePassListener,
    InitSettingEndDateBottomSheetFragment.onEndDatePassListener {
    @RequiresApi(Build.VERSION_CODES.O)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var lastTimeBackPressed : Long = 0
    private var time3: Long = 0

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var binding: ActivityInitBinding
    var startDate: StartDate = StartDate(0, 0, 0)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInitBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.e("테스트테스트3 : ", "${Firebase.auth.currentUser?.email}")

        // 약관 동의 bottom Sheet
        db.collection("${auth.currentUser?.email}").document("Init").get()
            .addOnSuccessListener { document ->
                if (document.get("isAgreed") == null) {
                    val bottomSheet = InitAgreementBottomSheetFragment()
                    val bundle = Bundle()

                    bottomSheet.arguments = bundle
                    bottomSheet.isCancelable = false

                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            }

        // 상태바 보이게 설정
        this.window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        var endDate: EndDate = EndDate(0, 0, 0)

        binding.btnInitClose.setOnClickListener {
            Toast.makeText(this,"초기설정을 먼저 완료해주세요",Toast.LENGTH_LONG).show()
            finish()
        }

        binding.btnInitNext.setBackgroundResource(R.drawable.init_button_next_enabled_false)
        binding.tvInitCycle.setTextColor(ContextCompat.getColor(this, R.color.gray_66))
        binding.tvInitTerm.setTextColor(ContextCompat.getColor(this, R.color.gray_66))

        if (binding.tvInitCycle.revealOnFocusHint == false && binding.tvInitTerm.revealOnFocusHint == false) {
            //월경 시작일, 종료일 db 저장
            binding.btnInitNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            Log.e("테스트테스트4 : ", "${Firebase.auth.currentUser?.email}")

        }

        binding.btnInitNext.setOnClickListener {
            if (binding.tvInitCycle.text.toString()
                    .trim().length > 1 && binding.tvInitTerm.text.toString().trim().length > 1
            ) {
                binding.btnInitNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)

                val intent = Intent(this, InitActivity2::class.java)
                startActivity(intent)

            } else if (binding.tvInitCycle.text.toString()
                    .trim().length < 1 && binding.tvInitTerm.text.toString().trim().length > 1
            ) {
                binding.tvInitCycle.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvInitCycle.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
                Toast.makeText(applicationContext, "시작일를 입력하세요", Toast.LENGTH_SHORT).show()

            } else if (binding.tvInitCycle.text.toString()
                    .trim().length > 1 && binding.tvInitTerm.text.toString().trim().length < 1
            ) {
                Toast.makeText(this, "종료일을 입력하세요", Toast.LENGTH_SHORT).show()
                binding.tvInitTerm.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvInitTerm.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
            } else if (binding.tvInitCycle.text.toString()
                    .trim().length < 1 && binding.tvInitTerm.text.toString().trim().length < 1
            ) {
                Toast.makeText(this, "시작일과 종료일을 입력하세요", Toast.LENGTH_SHORT).show()
                binding.tvInitCycle.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvInitTerm.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvInitCycle.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
                binding.tvInitTerm.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
            }
        }
        binding.tvInitCycle.setOnClickListener {
            // 값 넘기기
            val bottomSheet = InitSettingStartDateBottomSheetFragment()
            val bundle = Bundle()

            bundle.putString("date", "start")
            bottomSheet.arguments = bundle

            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        binding.tvInitTerm.setOnClickListener {

            Log.e("startDate", "${startDate.year}.${startDate.month}.${startDate.day}")
            // 값 넘기기
            val bottomSheet = InitSettingEndDateBottomSheetFragment()
            val bundle2 = Bundle()

            bundle2.putString("date", "end")
            bundle2.putString("year", "${startDate.year}")
            bundle2.putString("month", "${startDate.month}")
            bundle2.putString("day", "${startDate.day}")
            bottomSheet.arguments = bundle2

            bottomSheet.show(supportFragmentManager, bottomSheet.tag)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartDataPass(data: StartDate?) {
        if (data != null) {
            startDate.year = data.year
            startDate.month = data.month
            startDate.day = data.day

            binding.tvInitCycle.backgroundTintList =
                ContextCompat.getColorStateList(applicationContext, R.color.gray_a7)
            binding.tvInitCycle.setTextColor(Color.BLACK)
            binding.tvInitCycle.setText(
                (startDate.year).toString() + "년 "
                        + (startDate.month).toString() + "월 "
                        + (startDate.day).toString() + "일"
            )
            if (binding.tvInitCycle.text.toString()
                    .trim().length > 1 && binding.tvInitTerm.text.toString().trim().length > 1
            ) {
                binding.btnInitNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            }
        } else {
            startDate.year = LocalDate.now().year
            startDate.month = LocalDate.now().monthValue
            startDate.day = LocalDate.now().dayOfWeek.value
        }
        Log.e("start", "${data?.year}.${data?.month}.${data?.day}")
        Log.e("startDatepass", "${startDate.year}.${startDate.month}.${startDate.day}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEndDataPass(data: EndDate?) {
        Log.e("end", "${data?.year}.${data?.month}.${data?.day}")

        binding.tvInitTerm.backgroundTintList =
            ContextCompat.getColorStateList(applicationContext, R.color.gray_a7)
        binding.tvInitTerm.setTextColor(Color.BLACK)
        binding.tvInitTerm.setText(
            (data?.year).toString() + "년 "
                    + (data?.month).toString() + "월 "
                    + (data?.day).toString() + "일"
        )

        if (binding.tvInitCycle.text.toString()
                .trim().length > 1 && binding.tvInitTerm.text.toString().trim().length > 1
        ) {
            binding.btnInitNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
        }
    }



    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"초기설정 진행 중에는 뒤로 갈 수 없습니다",Toast.LENGTH_LONG).show()
        }
    }


}