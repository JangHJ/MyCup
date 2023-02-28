package com.yours.mycup.info

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityManageBinding

class ManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityManageBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_manage)

        // 상태바 투명
        this.window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        binding.btnManageBack.setOnClickListener {
            finish()
        }
    }
}