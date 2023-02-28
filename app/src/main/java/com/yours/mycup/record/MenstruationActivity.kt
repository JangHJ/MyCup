package com.yours.mycup.record

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yours.mycup.databinding.ActivityMenstruationListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

class MenstruationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenstruationListBinding
    lateinit var menstruationAdapter: MenstruationAdapter

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val docRef = db.collection("${auth.currentUser?.email}").document("Record")

    var startTime = ""
    var endTime = ""
    var supply = ""
    var ty = ""
    var col = ""
    var amount = 0
    var time = ""

    // 월경 기록 recyclerview에 들어갈 데이터
    class Data(
        val timeRange: String,
        val time: String,
        val amount: Int,
        val type: String,
        val color: String?
    )

    // 월경 기록 recyclerview에 들어가는 item 사이 간격
    class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    // 월경 기록 recyclerview에 들어갈 임시 데이터
    var DataList = mutableListOf<Data>()


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataList.clear()


        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        menstruationAdapter = MenstruationAdapter(DataList)

        // 바인딩 후 뷰 세팅. 순서 지킬 것
        binding = ActivityMenstruationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val spaceDecoration = VerticalSpaceItemDecoration(dpToPx(this, 20))

        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        binding.menstruationRv.layoutManager = manager
        binding.menstruationRv.adapter = menstruationAdapter

        // floatingActionButton.scaleType = ImageView.ScaleType.CENTER

        binding.menstruationRv.addItemDecoration(spaceDecoration)
        binding.btnInit2Back.setOnClickListener {
            finish()
        }

        binding.floatingActionButton.bringToFront()

        binding.floatingActionButton.setOnClickListener {
            binding.menstruationRv.adapter?.notifyDataSetChanged()
            val intent = Intent(this, MenstruationSettingActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onPause() {
        super.onPause()
        binding.menstruationRv.adapter?.notifyDataSetChanged()
    }
    override fun onResume() {
        super.onResume()
        update()
    }
    fun update() {
        db.collection("${auth.currentUser?.email}").document("Record").get()
            .addOnSuccessListener { document ->
                docRef.get()
                    .addOnSuccessListener { document ->
                        DataList.clear()
                        val date = document.get("clicked_date")
                        val docRef2 = docRef.collection("$date").document("Menstruation")
                        docRef2.get().addOnSuccessListener { document ->
                            if (document.get("isEnabled") != null) {
                                val num = "${document.get("list_num")}"
                                val temp = num.toInt()

                                for (i in 1..temp) {
                                    var am = ""
                                    var len = ""
                                    var timeR = ""
                                    if(document.get("men_list$i") != null)
                                    {
                                        startTime = document.get("men_list$i.startTime") as String
                                        endTime = document.get("men_list$i.endTime") as String
                                        timeR = "$startTime - $endTime"
                                        var simpleDataFormat = SimpleDateFormat("어제 HH시 mm분", Locale.KOREA)
                                        var simpleDataFormat2 = SimpleDateFormat("어제 HH시 mm분", Locale.KOREA)
                                        if (startTime.contains("어제")) {
                                            val cal = Calendar.getInstance()
                                            simpleDataFormat = SimpleDateFormat("어제 HH시 mm분", Locale.KOREA)
                                            simpleDataFormat2 = SimpleDateFormat("HH시 mm분", Locale.KOREA)
                                            cal.time = simpleDataFormat.parse(startTime)
                                            cal.add(Calendar.HOUR_OF_DAY, -24)

                                            var timeMinus = (simpleDataFormat2.parse(endTime).time - cal.time.time) / (1000 * 60)
                                            time = "${timeMinus/60}시간 ${timeMinus%60}분"
                                        }
                                        else {
                                            simpleDataFormat = SimpleDateFormat("HH시 mm분", Locale.KOREA)
                                            var timeMinus = (simpleDataFormat.parse(endTime).time - simpleDataFormat.parse(startTime).time) / (1000 * 60)
                                            time = "${timeMinus/60}시간 ${timeMinus%60}분"
                                        }

                                        supply = document.get("men_list$i.supply") as String
                                        ty = document.get("men_list$i.type") as String
                                        col = document.get("men_list$i.color") as String
                                        am = "${document.get("men_list$i.amount")}"
                                        amount = am.toInt()

                                        DataList.add(Data("$timeR", time, amount, "$supply $ty", "$col"))
                                        binding.menstruationRv.adapter?.notifyDataSetChanged()
                                    }
                                }
                                binding.menstruationRv.adapter?.notifyDataSetChanged()
                                binding.menstruationRv.smoothScrollToPosition(DataList.size-1)
                            }
                        }
                    }
            }
        binding.menstruationRv.adapter?.notifyDataSetChanged()
    }
}