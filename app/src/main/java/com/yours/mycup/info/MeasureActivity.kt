package com.yours.mycup.info

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMeasureBinding

class MeasureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMeasureBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_measure)

        // 상태바 투명
        this.window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        binding.btnMeasureBack.setOnClickListener {
            finish()
        }

        // 특정 글자 색 변경
        val content = binding.tv9.text.toString()
        val spannableString = SpannableString(content)

        val word = "탐색탭"
        val start = content.indexOf(word)
        val end = start + word.length
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#e16248")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tv9.text = spannableString
    }
}