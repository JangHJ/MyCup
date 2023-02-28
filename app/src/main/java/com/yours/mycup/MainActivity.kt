package com.yours.mycup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.yours.mycup.databinding.ActivityMainBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.database.setPersistenceEnabled(true)

        // 상태바 표시
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.mainViewpager.adapter = MainPagerAdapter(supportFragmentManager)
        binding.mainViewpager.offscreenPageLimit = 2

        binding.mainViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.mainBottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        binding.mainBottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_record -> binding.mainViewpager.currentItem = 0
                R.id.menu_search -> binding.mainViewpager.currentItem = 1
                R.id.menu_info -> binding.mainViewpager.currentItem = 2
                R.id.menu_mypage -> binding.mainViewpager.currentItem = 3
            }
            true
        }
    }
}