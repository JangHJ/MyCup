package com.yours.mycup.init

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yours.mycup.MainActivity
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityInit2Binding
import com.yours.mycup.databinding.ActivityInitBinding
import com.yours.mycup.record.dpToPx
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.io.File

class InitActivity2 : AppCompatActivity(), InitSettingIntervalBottomSheetFragment.onIntervalPassListener, InitSettingDurationBottomSheetFragment.onDurationPassListener {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityInit2Binding
    private var lastTimeBackPressed : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInit2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.e("테스트테스트5 : ", "${Firebase.auth.currentUser?.email}")

        // 상태바 보이게 설정
        this.window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.btnInit2Back.setOnClickListener{
            finish()
        }

        binding.btnInit2Next.setBackgroundResource(R.drawable.init_button_next_enabled_false)
        binding.tvInit2Cycle.setTextColor(ContextCompat.getColor(this, R.color.gray_66))
        binding.tvInit2Term.setTextColor(ContextCompat.getColor(this, R.color.gray_66))


        binding.btnInit2Next.setOnClickListener{
            if (binding.tvInit2Cycle.text.toString().trim().length > 1  && binding.tvInit2Term.text.toString().trim().length > 1 ) {

                val edialog : LayoutInflater = LayoutInflater.from(this)
                val mView : View = edialog.inflate(R.layout.dialog_join_complete, null)

                val save : TextView = mView.findViewById(R.id.joinBtn)
                binding.btnInit2Next.setBackgroundResource(R.drawable.init_button_next_enabled_true)


                val dialog = AlertDialog.Builder(this).create()
                save.setOnClickListener {
                    // 월경 주기,기간 db 저장
                    val str1 = "${binding.tvInit2Cycle.text}".replace("일", "")
                    val str2 = "${binding.tvInit2Term.text}".replace("일", "")

                    val data = hashMapOf(
                        "isJoined" to true,
                        "interval" to str1,
                        "duration" to str2
                    )
                    db.collection("${auth.currentUser?.email}").document("Init").set(data, SetOptions.merge())

                    Log.e("테스트테스트6 : ", "${Firebase.auth.currentUser?.email}")


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    ActivityCompat.finishAffinity(this)
                    System.runFinalization()
                    finish()
                    dialog.dismiss()
                }
                dialog.setView(mView)
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
                dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))
                dialog.setCancelable(false) // 배경 클릭 불가능하게 막기

                dialog.create()
            }
            else if (binding.tvInit2Cycle.text.toString().trim().length < 1 && binding.tvInit2Term.text.toString().trim().length > 1) {
                binding.tvInit2Cycle.setHintTextColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.tvInit2Cycle.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
                Toast.makeText(applicationContext, "주기를 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else if (binding.tvInit2Cycle.text.toString().trim().length > 1 && binding.tvInit2Term.text.toString().trim().length < 1 ) {
                Toast.makeText(this, "기간을 입력하세요", Toast.LENGTH_SHORT).show()
                binding.tvInit2Term.setHintTextColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.tvInit2Term.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
            }
            else if (binding.tvInit2Cycle.text.toString().trim().length < 1  && binding.tvInit2Term.text.toString().trim().length < 1 ) {
                Toast.makeText(this, "주기와 기간을 입력하세요", Toast.LENGTH_SHORT).show()
                binding.tvInit2Cycle.setHintTextColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.tvInit2Term.setHintTextColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.tvInit2Cycle.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
                binding.tvInit2Term.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.mainPointColor)
            }
        }
        binding.tvInit2Cycle.setOnClickListener {
            val bottomSheet = InitSettingIntervalBottomSheetFragment()
            val bundle = Bundle()

            bottomSheet.arguments = bundle

            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        binding.tvInit2Term.setOnClickListener {
            val bottomSheet = InitSettingDurationBottomSheetFragment()
            val bundle = Bundle()

            bottomSheet.arguments = bundle

            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    override fun onIntervalDataPass(data: Int?) {
        if (data != null) {
            binding.tvInit2Cycle.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.gray_a7)
            binding.tvInit2Cycle.setTextColor(Color.BLACK)
            binding.tvInit2Cycle.setText(data.toString()+ "일")

            if (binding.tvInit2Cycle.text.toString().trim().length > 1  && binding.tvInit2Term.text.toString().trim().length > 1 ) {
                // binding.btnInit2Next.setBackgroundColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.btnInit2Next.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            }
        }
    }

    override fun onDurationDataPass(data: Int?) {
        if (data != null) {
            binding.tvInit2Term.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.gray_a7)
            binding.tvInit2Term.setTextColor(Color.BLACK)
            binding.tvInit2Term.setText(data.toString() + "일")

            if (binding.tvInit2Cycle.text.toString().trim().length > 1  && binding.tvInit2Term.text.toString().trim().length > 1 ) {
                // binding.btnInit2Next.setBackgroundColor(ContextCompat.getColor(this, R.color.mainPointColor))
                binding.btnInit2Next.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            }
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"초기설정을 먼저 완료해주세요",Toast.LENGTH_LONG).show()
        }
    }

}