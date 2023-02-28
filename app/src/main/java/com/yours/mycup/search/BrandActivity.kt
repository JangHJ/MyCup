package com.yours.mycup.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityBrandBinding

class BrandActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityBrandBinding = DataBindingUtil.setContentView(this, R.layout.activity_brand)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.btnBrandBack.setOnClickListener{
            finish()
        }

        binding.imgThumbnailLunacupRing.setOnClickListener{
            val intent = Intent(this, BrandDetailActivity::class.java)
            intent.putExtra("cupname", "루나컵 링")
            startActivity(intent)
        }

        binding.imgThumbnailLunacupShorty.setOnClickListener{
            val intent = Intent(this, BrandDetailActivity::class.java)
            intent.putExtra("cupname", "루나컵 쇼티")
            startActivity(intent)
        }

        binding.imgThumbnailLunacupClassic.setOnClickListener{
            val intent = Intent(this, BrandDetailActivity::class.java)
            intent.putExtra("cupname", "루나컵 클래식")
            startActivity(intent)
        }

        binding.imgThumbnailHannahcup.setOnClickListener{
            val intent = Intent(this, BrandDetailActivity::class.java)
            intent.putExtra("cupname", "한나컵")
            startActivity(intent)
        }

        binding.imgThumbnailEvecup.setOnClickListener{
            val intent = Intent(this, BrandDetailActivity::class.java)
            intent.putExtra("cupname", "이브컵")
            startActivity(intent)
        }
    }
}