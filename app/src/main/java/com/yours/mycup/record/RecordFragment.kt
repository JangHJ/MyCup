package com.yours.mycup.record

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.isVisible
import com.yours.mycup.R
import com.yours.mycup.databinding.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.round

class RecordFragment : Fragment() {

    // 다중 선택
    private val selectedDates = mutableSetOf<LocalDate>()

    // 단일 선택
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()

    // 예상날짜를 받아오기 위한 변수
    var cycleDays =
        mutableListOf("2021-07-11", "2021-07-12", "2021-07-13", "2021-07-14", "2021-07-15")
    var lastData = mutableListOf(cycleDays)
    var cycleDay: Double = 28.4
    var period: Int = 5
    var expectCycle_firstDate: Calendar = Calendar.getInstance()
    var expectCycle2_firstDate: Calendar = Calendar.getInstance()
    var expectCycle3_firstDate: Calendar = Calendar.getInstance()
    var expectCycle4_firstDate: Calendar = Calendar.getInstance()
    var expectCycle5_firstDate: Calendar = Calendar.getInstance()
    var expectCycle6_firstDate: Calendar = Calendar.getInstance()
    var expectCycle7_firstDate: Calendar = Calendar.getInstance()
    var recordedDate = mutableListOf(cycleDays)
    var isYellow = false

    @RequiresApi(Build.VERSION_CODES.O)
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    lateinit var binding: FragmentRecordBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val list: List<String> = listOf()
    private val str: String? = null
    private val num: Number? = null
    private var nlist: Number? = null

    private val docRef = db.collection("${auth.currentUser?.email}").document("Record")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("테스트테스트7 : ", "${Firebase.auth.currentUser?.email}")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        var dateformat = SimpleDateFormat("yyyy-MM-dd")

        //최근 시작날짜, 끝날짜, 주기, 기간 데이터 받아왔음
        db.collection("${auth.currentUser?.email}").document("Init").get()
            .addOnSuccessListener { document ->
                val interval =
                    document.get("interval") //월경주기 -> String타입임 Int로 바꾸려면 val interval  = "${document.get("interval")}".toInt()
                cycleDay = interval.toString().toDouble()

                val duration = document.get("duration") //월경기간
                period = duration.toString().toInt()

                val start = document.get("mendate_start") // 최근 월경시작일 "2021-08-20" 문자열로 저장되어있음
                val end = document.get("mendate_end") // 최근 월경종료일

                var initDateRange =
                    (dateformat.parse(end.toString()).time - dateformat.parse(start.toString()).time) / (24 * 60 * 60 * 1000)

                // 초기 값에 의한 초기 주기 넣기
                var forplusDate: Calendar = Calendar.getInstance()
                cycleDays = mutableListOf("${start.toString()}")
                forplusDate.time = dateformat.parse(cycleDays[0])
                for (i in 0..initDateRange - 1) {
                    forplusDate.add(Calendar.DATE, 1)
                    cycleDays.add(dateformat.format(Date(forplusDate.time.time)))
                }

                lastData = mutableListOf(cycleDays)

                // 입력된 기록갑 넣기
                var m = today.monthValue.toString()
                var d = today.dayOfMonth.toString()

                if (m.toInt() < 10)
                    m = "0${m}"
                if (d.toInt() < 10)
                    d = "0${d}"

                val date = "${today.year}-$m-$d"
                val data = hashMapOf(
                    "clicked_date" to date,
                    "Today" to date
                )
                db.collection("${auth.currentUser?.email}").document("Record")
                    .set(data, SetOptions.merge())
                db.collection("${auth.currentUser?.email}").document("Record").update("date", FieldValue.arrayUnion("$date"))

                docRef.get().addOnSuccessListener { document ->
                    var cnt = 0

                    var arrnum = 0
                    if (document.get("arr_num") != null)
                        arrnum = "${document.get("arr_num")}".toInt()

                    Log.e("arrnum : ", "$arrnum")

                    if (document.get("null_cnt") == null || "${document.get("null_cnt")}".toInt() < 0) {
                        val data = hashMapOf(
                            "null_cnt" to 0
                        )
                        docRef.set(data, SetOptions.merge())
                    } else {
                        cnt = "${document.get("null_cnt")}".toInt() //null_cnt 값 cnt에 넣어줌
                    }

                    docRef.collection("$date").document("Menstruation").get()
                        .addOnSuccessListener { document ->
                            if (document.get("isEnabled") == null && document.get("isChecked") != true) {
                                val data = hashMapOf(
                                    "null_cnt" to cnt + 1
                                )
                                docRef.set(data, SetOptions.merge())
                            }
                            val data = hashMapOf(
                                "isChecked" to true
                            )
                            docRef.collection("$date").document("Menstruation")
                                .set(data, SetOptions.merge())
                        }

                    recordedDate.clear()

                    if (arrnum > 0) {
                        for (i in 1..arrnum) {
                            if (document.get("duration_arr$i") != null) {
                                val du_arr: MutableList<String> =
                                    document.get("duration_arr$i") as MutableList<String> //날짜 저장되어 있는 배열 불러오기
                                Log.e("du_arr 결과 : ", "$du_arr")
                                recordedDate.add(du_arr)
                                lastData.add(du_arr)
                            }
                        }
                    }

                    // 초기값으로 주기 계산(마지막 시작 날짜 받아서 예상값 주기)
                    var lastCycle_firstDate = Calendar.getInstance()
                    if (recordedDate.size > 0) {
                        // 기록된 값이 있는 경우
                        lastCycle_firstDate.time =
                            dateformat.parse(recordedDate[recordedDate.size - 1][0])
                    } else {
                        // 기록된 값이 없는 경우
                        lastCycle_firstDate.time = dateformat.parse(lastData[0][0])
                    }


                    // 평균 기간 계산
                    // 기록된 값들을 받아와 각 (cycleDays 마지막 날짜-첫 날짜)로 계산한다.
                    var sumPeriod = 0
                    for (i in 0..lastData.size - 1) {
                        val lastDay = dateformat.parse(lastData[i].last())
                        val firstDay = dateformat.parse(lastData[i].first())
                        var dayminus = (lastDay.time - firstDay.time) / (24 * 60 * 60 * 1000)

                        sumPeriod += dayminus.toInt() + 1
                    }
                    period = round((sumPeriod / lastData.size).toDouble()).toInt()


                    // 평균 주기 계산
                    // 기록된 값들을 받아와 '이번 생리 시작일-지난 생리 시작일'로 계산한다.
                    // lastData 대신 기록된 데이터가 들어가야함
                    var sumCycle = 0
                    var cycleCount = 0
                    var down = lastData.size - 1

                    // 기록된 값이 있을 경우 주기 업데이트. 없을 땐 초기 입력 주기 그대로 사용
                    if (lastData.size > 1) {
                        for (i in down downTo 0) {
                            val nextCycle = dateformat.parse(lastData[i].first())
                            var lastCycle = Date()
                            if (i - 1 >= 0) {
                                lastCycle = dateformat.parse(lastData[i - 1].first())
                                var dayminus =
                                    (nextCycle.time - lastCycle.time) / (24 * 60 * 60 * 1000)
                                sumCycle += dayminus.toInt()
                                cycleCount += 1
                            } else {
                                break
                            }
                        }
                        var testcycleDay =
                            (sumCycle + interval.toString().toDouble()) / (cycleCount + 1)
                        cycleDay = testcycleDay
                    }

                    // 칩에 주기 표시
                    binding.term.text = "${round(cycleDay).toInt()}일"


                    // 예상 날짜 추가 (6개의 예상 주기 만들어서 예상날짜와 이후 날짜 넣기)


                    // 기록된 값 또는 초기값 이후 첫 예상날짜
                    var lastCycle2_firstDate = lastCycle_firstDate

                    lastCycle2_firstDate.add(Calendar.DATE, floor(cycleDay).toInt())
                    expectCycle2_firstDate = lastCycle2_firstDate

                    var nextCycle2 =
                        mutableListOf("${dateformat.format(Date(expectCycle2_firstDate.time.time))}")
                    lastData.add(nextCycle2)
                    for (i in 0..period - 2) {
                        expectCycle2_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle2_firstDate.time.time)))
                    }

                    // 두번째 예상 날짜
                    var lastCycle3_firstDate = Calendar.getInstance()
                    lastCycle3_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle3_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle3_firstDate = lastCycle3_firstDate

                    var nextCycle3 =
                        mutableListOf("${dateformat.format(Date(expectCycle3_firstDate.time.time))}")
                    lastData.add(nextCycle3)
                    for (i in 0..period - 2) {
                        expectCycle3_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle3_firstDate.time.time)))
                    }

                    // 세번째 예상 날짜
                    var lastCycle4_firstDate = Calendar.getInstance()
                    lastCycle4_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle4_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle4_firstDate = lastCycle4_firstDate

                    var nextCycle4 =
                        mutableListOf("${dateformat.format(Date(expectCycle4_firstDate.time.time))}")
                    lastData.add(nextCycle4)
                    for (i in 0..period - 2) {
                        expectCycle4_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle4_firstDate.time.time)))
                    }

                    // 네번째 예상 날짜
                    var lastCycle5_firstDate = Calendar.getInstance()
                    lastCycle5_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle5_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle5_firstDate = lastCycle5_firstDate

                    var nextCycle5 =
                        mutableListOf("${dateformat.format(Date(expectCycle5_firstDate.time.time))}")
                    lastData.add(nextCycle5)
                    for (i in 0..period - 2) {
                        expectCycle5_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle5_firstDate.time.time)))
                    }

                    // 다섯번째 예상 날짜
                    var lastCycle6_firstDate = Calendar.getInstance()
                    lastCycle6_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle6_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle6_firstDate = lastCycle6_firstDate

                    var nextCycle6 =
                        mutableListOf("${dateformat.format(Date(expectCycle6_firstDate.time.time))}")
                    lastData.add(nextCycle6)
                    for (i in 0..period - 2) {
                        expectCycle6_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle6_firstDate.time.time)))
                    }

                    // 여섯번째 예상 날짜
                    var lastCycle7_firstDate = Calendar.getInstance()
                    lastCycle7_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle7_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle7_firstDate = lastCycle7_firstDate

                    var nextCycle7 =
                        mutableListOf("${dateformat.format(Date(expectCycle7_firstDate.time.time))}")
                    lastData.add(nextCycle7)
                    for (i in 0..period - 2) {
                        expectCycle7_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle7_firstDate.time.time)))
                    }

                    val docRef = db.collection("${auth.currentUser?.email}").document("Record")
                    docRef.get().addOnSuccessListener { document ->
                        val date = document.get("clicked_date")

                        val docRef2 = docRef.collection("$date").document("Menstruation")
                        var weekTotalAmount: Int = 0
                        var arrnum: Int = 0
                        var amcnt: Int = 1
                        var arr = {0}
                        if (document.get("am_cnt") != null)
                            amcnt = "${document.get("am_cnt")}".toInt()
                        if (document.get("arr_num") != null)
                            arrnum = "${document.get("arr_num")}".toInt()
                        if (document.get("amount_arr$arrnum") != null) {
                            for (i in 1..arrnum) {
                                if(document.get("amount_arr$i.$date") != null)
                                {
                                    val temp : Map<String, Int> = document.get("amount_arr$i") as Map<String, Int>
                                    for(value in temp.values)
                                        weekTotalAmount += value
                                    Log.e("arr 결과 : ", "$weekTotalAmount")
                                }
                                //Log.e("n 테스트 : ", "$n")
                                Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                            }
                        }
                        docRef2.get().addOnSuccessListener { document ->
                            if (document.get("isEnabled") == null) {
                                binding.menstruationTitle.setText("월경을 기록해 보세요")
                                binding.menstruationContent.setText("상세한 월경량 수치 측정과 색상 기록을 통해 \n여성 질환을 예방할 수 있습니다.")
                                binding.menstruationContent2.visibility = View.GONE
                                binding.menstruationContent3.visibility = View.GONE
                                binding.menstruationContent4.visibility = View.GONE

                            } else {
                                val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                                val endTime =
                                    document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                                var todayTotalAmount: Int = 0
                                for (i in 1..num.toString().toInt()) {
                                    /* todayTotalAmount += document.get("men_list$i.amount")
                                         .toString()
                                         .toInt()*/
                                    todayTotalAmount = "${document.get("totalAmount")}".toInt()
                                }
                                binding.menstruationContent2.visibility = View.VISIBLE
                                binding.menstruationContent3.visibility = View.VISIBLE
                                binding.menstruationContent4.visibility = View.VISIBLE
                                binding.menstruationTitle.setText("최근 교체시간 ${endTime}")
                                binding.menstruationContent.setText("오늘 월경량")
                                binding.menstruationContent2.setText("${todayTotalAmount} ml")
                                binding.menstruationContent3.setText("주기 누적 월경량")
                                Log.e("주기 누적 월경량 순서1 -> ", "$weekTotalAmount")
                                binding.menstruationContent4.setText("${weekTotalAmount} ml")

                            }
                        }
                    }


                    // view update
                    // 다음 주기 확인하기
                    var changeBackValue = 0
                    for (i in 0..lastData.size - 1) {
                        if (changeBackValue > 0) {
                            break
                        }
                        for (j in 0..lastData[i].size - 1) {
                            if (changeBackValue > 0) {
                                break
                            }
                            if (lastData[i][j].contains(date)) {
                                binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_yellow)
                                binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_yellow)
                                binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_yellow)
                                binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_yellow)
                                changeBackValue += 1
                                isYellow = true
                                break
                            }
                        }
                    }
                    if (changeBackValue == 0) {
                        binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_green)
                        binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_green)
                        binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_green)
                        binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_green)
                    }
                    for (i in 0..recordedDate.size - 1) {
                        for (j in 0..recordedDate[i].size - 1) {
                            if (recordedDate[i][j].contains(date) == true) {
                                binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_red)
                                binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_red)
                                binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_red)
                                binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_red)
                            }
                        }
                    }

                    var valueforBreak: Int = 0
                    for (i in 0..lastData.size - 1) {
                        if (valueforBreak > 0) break
                        for (j in 0..lastData[i].size - 1) {
                            if (valueforBreak > 0) break

                            // 예상 월경일이거나 월경 중일 경우
                            if (lastData[i][j].split("-")[0].toInt() == today.year
                                && lastData[i][j].split("-")[1].toInt() == today.monthValue
                                && lastData[i][j].split("-")[2].toInt() == today.dayOfMonth
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])

                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                if ((i+1) == lastData.size) {
                                    ovulationCalculate.time =
                                        Date(dateformat.parse(lastData[i][0]).time)
                                }
                                else {
                                    ovulationCalculate.time =
                                        Date(dateformat.parse(lastData[i + 1][0]).time)
                                }
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()
                                binding.nextOvulationDate.text =
                                    DateTimeFormatter.ofPattern("MM.dd (EE)")
                                        .withLocale(Locale.forLanguageTag("ko"))
                                        .format(ovulationLocaldDateformat)

                                if ((i+1) == lastData.size) {
                                    binding.nextDurationDate.text =
                                        "${lastData[i][0].split("-")[1]}.${
                                            lastData[i][0].split(
                                                "-"
                                            )[2]
                                        } (${
                                            DateTimeFormatter.ofPattern("EE")
                                                .withLocale(Locale.forLanguageTag("ko"))
                                                .format(expectLocaldDateformat)
                                        })"

                                    binding.nextDurationTitle.text = "오늘은 \n월경 예상 0일째입니다."
                                }
                                else {
                                    binding.nextDurationDate.text =
                                        "${lastData[i + 1][0].split("-")[1]}.${
                                            lastData[i + 1][0].split(
                                                "-"
                                            )[2]
                                        } (${
                                            DateTimeFormatter.ofPattern("EE")
                                                .withLocale(Locale.forLanguageTag("ko"))
                                                .format(expectLocaldDateformat)
                                        })"

                                    binding.nextDurationTitle.text = "오늘은 \n월경 예상 ${j + 1}일째입니다."
                                }

                                if (isYellow) {
                                }
                                for (i in 0..recordedDate.size - 1) {
                                    for (j in 0..recordedDate[i].size - 1) {
                                        if (recordedDate[i][j].contains(date) == true) {
                                            binding.nextDurationTitle.text =
                                                "오늘은 \n월경 ${j + 1}일째입니다."
                                        }
                                    }
                                }
                                valueforBreak += 1
                                break
                            }
                            // 이번 년도, 다음 달이 다음 주기인 경우
                            else if (lastData[i][j].split("-")[0].toInt() == today.year
                                && lastData[i][j].split("-")[1].toInt() == today.monthValue + 1
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])

                                // 남은 일수
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)


                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()
                                if (ovulationCalculate.time >= Date.from(
                                        today.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size) {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }


                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"
                                valueforBreak += 1
                                break
                            }
                            // 이번 년도, 이번 달, 이후 날짜가 주기일 경우
                            else if (lastData[i][j].split("-")[0].toInt() == today.year
                                && lastData[i][j].split("-")[1].toInt() == today.monthValue
                                && lastData[i][j].split("-")[2].toInt() > today.dayOfMonth
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])

                                // 남은 일수
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)

                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()

                                if (ovulationCalculate.time >= Date.from(
                                        today.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size){
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }

                                valueforBreak += 1
                                break

                            }
                            // 내년이 주기인 경우
                            else if (lastData[i][j].split("-")[0].toInt() == today.year + 1) {

                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])
                                // 남은 일수
                                if ((i+1) == lastData.size){
                                    expectLocaldDateformat = LocalDate.parse(lastData[i][0])
                                }
                                else {
                                    expectLocaldDateformat = LocalDate.parse(lastData[i+1][0])
                                }
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()

                                if (ovulationCalculate.time >= Date.from(
                                        today.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size){
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }

                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"
                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                valueforBreak += 1
                                break
                            }
                        }
                    }
                }
                for (i in 0..lastData.size - 1) {
                    Log.e("lastData${i}", "${lastData[i]}")
                }


            }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecordBinding.bind(view)

        // 정보 추가
        binding.menstruationFrame.setOnClickListener {
            val intent = Intent(context, MenstruationActivity::class.java)
            startActivity(intent)
        }
        binding.symptomFrame.setOnClickListener {
            val intent = Intent(context, SymptomSettingActivity::class.java)
            startActivity(intent)
        }
        binding.dischargeFrame.setOnClickListener {
            val intent = Intent(context, DischargeSettingActivity::class.java)
            startActivity(intent)
        }


        val docRef =
            db.collection("${auth.currentUser?.email}").document("Record")

        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            val docRef0 = docRef.collection("${date}").document("Menstruation")

            var weekTotalAmount: Int = 0
            var arrnum: Int = 0
            var amcnt: Int = 0
            if (document.get("am_cnt") != null)
                amcnt = "${document.get("am_cnt")}".toInt()
            if (document.get("arr_num") != null)
                arrnum = "${document.get("arr_num")}".toInt()
            if (document.get("amount_arr$arrnum") != null) {
                for (i in 1..arrnum) {
                    if(document.get("amount_arr$i.$date") != null)
                    {
                        val temp : Map<String, Int> = document.get("amount_arr$i") as Map<String, Int>
                        for(value in temp.values)
                            weekTotalAmount += value
                        Log.e("arr 결과 : ", "$weekTotalAmount")
                    }
                    //Log.e("n 테스트 : ", "$n")
                    Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                }
            }


            // binding.symptomContent.text = ""
            // 월경 텍스트 변경
            val docRef2 = docRef.collection("${date}").document("Symptom")
            docRef0.get().addOnSuccessListener { document ->
                if (document.get("isEnabled") != null) {
                    val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                    val endTime = document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                    var todayTotalAmount: Int = 0
                    for (i in 1..num.toString().toInt()) {
                        // += document.get("men_list$i.amount").toString().toInt()
                        todayTotalAmount = "${document.get("totalAmount")}".toInt()
                    }
                    binding.menstruationFrame.setOnClickListener {
                        val intent = Intent(context, MenstruationActivity::class.java)
                        startActivity(intent)
                    }
                    binding.menstruationContent2.visibility = View.VISIBLE
                    binding.menstruationContent3.visibility = View.VISIBLE
                    binding.menstruationContent4.visibility = View.VISIBLE
                    binding.menstruationTitle.setText("최근 교체시간 ${endTime}")
                    binding.menstruationContent.setText("오늘 월경량")
                    binding.menstruationContent2.setText("${todayTotalAmount} ml")
                    binding.menstruationContent3.setText("주기 누적 월경량")
                    Log.e("주기 누적 월경량 순서2 -> ", "$weekTotalAmount")
                    binding.menstruationContent4.setText("${weekTotalAmount} ml")

                } else {

                    binding.menstruationTitle.setText("월경을 기록해 보세요")
                    binding.menstruationContent.setText("상세한 월경량 수치 측정과 색상 기록을 통해 \n여성 질환을 예방할 수 있습니다.")
                    binding.menstruationContent2.visibility = View.GONE
                    binding.menstruationContent3.visibility = View.GONE
                    binding.menstruationContent4.visibility = View.GONE
                }
            }
            // 통증 텍스트 변경
            docRef2.get().addOnSuccessListener { document ->
                if (document.get("isEnabled") == true) {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") != null) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.symptomContent.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.symptomContent.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.symptomContent.setTextColorRes(R.color.yellow_d7)
                                        isYellow = true
                                        break
                                    }
                                }
                            }
                        }
                    }
                    binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.symtomTitle.text =
                        "오늘의 증상"

                    val pain: ArrayList<String?> =
                        document.get("pain") as ArrayList<String?>
                    val bowel = document.get("bowel")
                    val etc: ArrayList<String?> =
                        document.get("etc") as ArrayList<String?>

                    var cnt = 0
                    var cnt2 = 0
                    binding.symptomContent.text = ""
                    for (i in 0..5) {
                        if (pain[i] != null) {
                            cnt++
                            if (cnt == 1) {
                                binding.symptomContent.text = "${pain[i]}"
                            } else {
                                binding.symptomContent.text =
                                    "${binding.symptomContent.text}, ${pain[i]}"
                            }
                        } else {
                            cnt2++
                            if (cnt2 == 6)
                                binding.symptomContent.text = ""
                        }
                    }

                    if (bowel != null) {
                        if (binding.symptomContent.text == "")
                            binding.symptomContent.text = "$bowel"
                        else
                            binding.symptomContent.text =
                                "${binding.symptomContent.text}, $bowel"
                    }

                    if (binding.symptomContent.text == "") {
                        for (i in 0..4) {
                            if (etc[i] != null) {
                                cnt++
                                if (cnt == 1) {
                                    binding.symptomContent.text = "${etc[i]}"
                                } else {
                                    binding.symptomContent.text =
                                        "${binding.symptomContent.text}, ${etc[i]}"
                                }
                            }
                        }
                    } else {
                        for (i in 0..4) {
                            if (etc[i] != null) {
                                binding.symptomContent.text =
                                    "${binding.symptomContent.text}, ${etc[i]}"
                            }
                        }
                    }
                    if (binding.symptomContent.text == "") {
                        binding.symptomContent.setTextColorRes(R.color.gray_a7)
                        binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                        binding.symtomTitle.text = "어떤 증상이 있었나요?"
                        binding.symptomContent.text =
                            "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                    }
                } else {
                    binding.symptomContent.setTextColorRes(R.color.gray_a7)
                    binding.symtomTitle.text = "어떤 증상이 있었나요?"
                    binding.symptomContent.text =
                        "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                    binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                }
            }
            // 분비물 텍스트 변경
            val docRef3 = docRef.collection("$date").document("Discharge")

            docRef3.get().addOnSuccessListener { document ->
                val nang = document.get("nang").toString()
                val touch = document.get("touch").toString()

                var cnt = 0
                if (nang == "null" && touch == "null") {
                    binding.dischargeTitle.text = "분비물이 있었나요?"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_very_small)
                    binding.dischargeContent.text = ""
                    binding.dischargeContent.visibility = View.GONE
                    binding.dischargeContent2.visibility = View.GONE
                    binding.fixDischargeContent1.visibility = View.GONE
                    binding.fixDischargeContent2.visibility = View.GONE
                    binding.nangAll.visibility = View.GONE
                    binding.touchAll.visibility = View.GONE
                    binding.nangPlusTouch.visibility = View.GONE
                    binding.dischargeContent.isEnabled = false
                } else if (nang != "null") {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.dischargeContent.setTextColorRes(R.color.red_e1)
                            binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.dischargeContent.setTextColorRes(R.color.green_32)
                            binding.dischargeContent2.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                        binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                    }
                                }
                            }
                        }
                    }
                    binding.dischargeTitle.text = "오늘의 분비물"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.dischargeContent.visibility = View.VISIBLE
                    binding.dischargeContent2.visibility = View.GONE
                    binding.fixDischargeContent1.visibility = View.VISIBLE
                    binding.fixDischargeContent2.visibility = View.GONE
                    binding.nangAll.visibility = View.VISIBLE
                    binding.touchAll.visibility = View.GONE
                    binding.nangPlusTouch.visibility = View.VISIBLE
                    binding.dischargeContent.isEnabled = true
                    binding.dischargeContent.text = "$nang"
                    if (touch != "null") {
                        docRef0.get().addOnSuccessListener { document ->
                            if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                binding.dischargeContent.setTextColorRes(R.color.red_e1)
                                binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                            } else {
                                binding.dischargeContent.setTextColorRes(R.color.green_32)
                                binding.dischargeContent2.setTextColorRes(R.color.green_32)
                                for (i in 0..lastData.size - 1) {
                                    for (j in 0..lastData[i].size - 1) {
                                        if (lastData[i][j].contains(date.toString())) {
                                            binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                            binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                        }
                                    }
                                }
                            }
                        }
                        binding.dischargeTitle.text = "오늘의 분비물"
                        binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder)
                        binding.dischargeContent.visibility = View.VISIBLE
                        binding.dischargeContent2.visibility = View.VISIBLE
                        binding.fixDischargeContent1.visibility = View.VISIBLE
                        binding.fixDischargeContent2.visibility = View.VISIBLE
                        binding.nangAll.visibility = View.VISIBLE
                        binding.touchAll.visibility = View.VISIBLE
                        binding.nangPlusTouch.visibility = View.VISIBLE
                        val text = binding.dischargeContent.text.toString()
                        binding.dischargeContent.text = "${text}"
                        binding.dischargeContent2.text = "$touch"
                    }
                } else {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.dischargeContent.setTextColorRes(R.color.red_e1)
                            binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.dischargeContent.setTextColorRes(R.color.green_32)
                            binding.dischargeContent2.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                        binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                    }
                                }
                            }
                        }
                    }
                    binding.dischargeTitle.text = "오늘의 분비물"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.dischargeContent.visibility = View.GONE
                    binding.dischargeContent2.visibility = View.VISIBLE
                    binding.fixDischargeContent1.visibility = View.GONE
                    binding.fixDischargeContent2.visibility = View.VISIBLE
                    binding.nangAll.visibility = View.GONE
                    binding.touchAll.visibility = View.VISIBLE
                    binding.nangPlusTouch.visibility = View.VISIBLE
                    binding.dischargeContent2.isEnabled = true
                    binding.dischargeContent2.text = "$touch"
                }
            }
        }


        binding.exOneYearText.text =
            DateTimeFormatter.ofPattern("MM월 dd일 EEEE").withLocale(Locale.forLanguageTag("ko"))
                .format(
                    LocalDateTime.now()
                )

        val daysOfWeek = arrayOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        binding.legendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                if (index == 0) {
                    text = "일"
                } else if (index == 1) {
                    text = "월"
                } else if (index == 2) {
                    text = "화"
                } else if (index == 3) {
                    text = "수"
                } else if (index == 4) {
                    text = "목"
                } else if (index == 5) {
                    text = "금"
                } else {
                    text = "토"
                }
                // 꼭 Res로 설정
                setTextColorRes(R.color.gray_a7)
                //setTextColorRes(R.color.gray_a7)
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        // 시작 month, 마지막 month, 초기 화면 month 설정

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay

            // 아래처럼 정확한 위치를 받아줘야함
            val textView1 = WeekcalendarDayBinding.bind(view).dayText
            val scrollCalendarDay = WeekcalendarDayBinding.bind(view).scrollCalendarDay
            val dot = WeekcalendarDayBinding.bind(view).lineForAdded

            // 클릭 리스너
            init {
                view.setOnClickListener {

                    binding.menstruationFrame.setOnClickListener {
                        val intent = Intent(context, MenstruationSettingActivity::class.java)

                        startActivity(intent)
                    }
                    update()


                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate == day.date) {
                            selectedDate = null
                            binding.exOneCalendar.notifyDayChanged(day)

                            val text = "날짜(${
                                DateTimeFormatter.ofPattern("d MMMM yyyy").format(day.date)
                            })가 선택 취소 되었습니다."
                            // Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()

                        } else {
                            // selectedDates.add(day.date)
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exOneCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exOneCalendar.notifyDateChanged(oldDate) }

                            // 선택된 날짜 db 저장
                            val data = hashMapOf(
                                "clicked_date" to "${day.date}"
                            )

                            db.collection("${auth.currentUser?.email}").document("Record")
                                .set(data, SetOptions.merge())
                            db.collection("${auth.currentUser?.email}").document("Record").update("date", FieldValue.arrayUnion("${day.date}"))


                            // 배경 및 칩 색상 지정
                            var changeBackValue = 0
                            for (i in 0..lastData.size - 1) {
                                if (changeBackValue > 0) {
                                    break
                                }
                                for (j in 0..lastData[i].size - 1) {
                                    if (changeBackValue > 0) {
                                        break
                                    }
                                    if (lastData[i][j].contains(day.date.toString())) {
                                        binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_yellow)
                                        binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_yellow)
                                        binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_yellow)
                                        binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_yellow)
                                        changeBackValue += 1

                                        break
                                    }
                                }
                            }
                            if (changeBackValue == 0) {
                                binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_green)
                                binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_green)
                                binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_green)
                                binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_green)
                            }
                            for (i in 0..recordedDate.size - 1) {
                                for (j in 0..recordedDate[i].size - 1) {
                                    if (recordedDate[i][j].contains(day.date.toString()) == true) {
                                        binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_red)
                                        binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_red)
                                        binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_red)
                                        binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_red)
                                    }
                                }
                            }

                            //월경 텍스트 변경
                            val docRef =
                                db.collection("${auth.currentUser?.email}").document("Record")
                            docRef.get().addOnSuccessListener { document ->
                                val date = document.get("clicked_date")
                                var weekTotalAmount: Int = 0
                                var arrnum: Int = 0
                                var amcnt: Int = 0
                                if (document.get("am_cnt") != null)
                                    amcnt = "${document.get("am_cnt")}".toInt()
                                if (document.get("arr_num") != null)
                                    arrnum = "${document.get("arr_num")}".toInt()
                                if (document.get("amount_arr$arrnum") != null) {
                                    for (i in 1..arrnum) {
                                        if(document.get("amount_arr$i.$date") != null)
                                        {
                                            val temp : Map<String, Int> = document.get("amount_arr$i") as Map<String, Int>
                                            for(value in temp.values)
                                                weekTotalAmount += value
                                            Log.e("arr 결과 : ", "$weekTotalAmount")
                                        }
                                        //Log.e("n 테스트 : ", "$n")
                                        Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                                    }
                                }
                                // binding.symptomContent.text = ""
                                // 텍스트 변경
                                val docRef2 = docRef.collection("${day.date}").document("Symptom")
                                val docRef0 =
                                    docRef.collection("${day.date}").document("Menstruation")
                                docRef0.get().addOnSuccessListener { document ->
                                    if (document.get("isEnabled") != null) {
                                        val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                                        val endTime =
                                            document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                                        var todayTotalAmount: Int = 0
                                        for (i in 1..num.toString().toInt()) {
                                            /* todayTotalAmount += document.get("men_list$i.amount")
                                                 .toString().toInt()*/
                                            todayTotalAmount =
                                                "${document.get("totalAmount")}".toInt()

                                            val data2 = hashMapOf(
                                                "amount$arrnum" to mapOf(
                                                    "arr$arrnum" to todayTotalAmount
                                                )
                                            )
                                        }
                                        binding.menstruationFrame.setOnClickListener {
                                            val intent =
                                                Intent(context, MenstruationActivity::class.java)
                                            startActivity(intent)
                                        }
                                        binding.menstruationContent2.visibility = View.VISIBLE
                                        binding.menstruationContent3.visibility = View.VISIBLE
                                        binding.menstruationContent4.visibility = View.VISIBLE
                                        binding.menstruationTitle.setText("최근 교체시간 ${endTime}")
                                        binding.menstruationContent.setText("오늘 월경량")
                                        binding.menstruationContent2.setText("${todayTotalAmount} ml")
                                        binding.menstruationContent3.setText("주기 누적 월경량")
                                        Log.e("주기 누적 월경량 순서3 -> ", "$weekTotalAmount")
                                        binding.menstruationContent4.setText("${weekTotalAmount} ml")

                                    } else {
                                        binding.menstruationFrame.setOnClickListener {
                                            val intent = Intent(
                                                context,
                                                MenstruationSettingActivity::class.java
                                            )
                                            for (i in 0..lastData.size-1) {
                                                if (lastData[i].contains(day.date.toString())) {
                                                    intent.putExtra("isyellow", true)
                                                }
                                            }

                                            startActivity(intent)
                                        }
                                        binding.menstruationTitle.setText("월경을 기록해 보세요")
                                        binding.menstruationContent.setText("상세한 월경량 수치 측정과 색상 기록을 통해 \n여성 질환을 예방할 수 있습니다.")
                                        binding.menstruationContent2.visibility = View.GONE
                                        binding.menstruationContent3.visibility = View.GONE
                                        binding.menstruationContent4.visibility = View.GONE
                                    }
                                }
                                // 증상 텍스트 변경
                                docRef2.get().addOnSuccessListener { document ->
                                    //isEnabled = document.get("isEnabled")
                                    binding.symptomFrame.setOnClickListener {
                                        val intent = Intent(
                                            context,
                                            SymptomSettingActivity::class.java
                                        )
                                        for (i in 0..lastData.size-1) {
                                            if (lastData[i].contains(day.date.toString())) {
                                                intent.putExtra("isyellow", true)
                                            }
                                        }
                                        startActivity(intent)
                                    }
                                    if (document.get("isEnabled") == true) {
                                        docRef0.get().addOnSuccessListener { document ->
                                            if (document.get("isEnabled") != null) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                                binding.symptomContent.setTextColorRes(R.color.red_e1)
                                            } else {
                                                binding.symptomContent.setTextColorRes(R.color.green_32)
                                                for (i in 0..lastData.size - 1) {
                                                    for (j in 0..lastData[i].size - 1) {
                                                        if (lastData[i][j].contains(day.date.toString())) {
                                                            binding.symptomContent.setTextColorRes(R.color.yellow_d7)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        val pain: ArrayList<String?> =
                                            document.get("pain") as ArrayList<String?>
                                        val bowel = document.get("bowel")
                                        val etc: ArrayList<String?> =
                                            document.get("etc") as ArrayList<String?>
                                        binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                                        binding.symtomTitle.text =
                                            "오늘의 증상"

                                        var cnt = 0
                                        var cnt2 = 0
                                        binding.symptomContent.text = ""
                                        for (i in 0..5) {
                                            if (pain[i] != null) {
                                                cnt++
                                                if (cnt == 1) {
                                                    binding.symptomContent.text = "${pain[i]}"
                                                } else {
                                                    binding.symptomContent.text =
                                                        "${binding.symptomContent.text}, ${pain[i]}"
                                                }
                                            } else {
                                                cnt2++
                                                if (cnt2 == 6)
                                                    binding.symptomContent.text = ""
                                            }
                                        }

                                        if (bowel != null) {
                                            if (binding.symptomContent.text == "")
                                                binding.symptomContent.text = "$bowel"
                                            else
                                                binding.symptomContent.text =
                                                    "${binding.symptomContent.text}, $bowel"
                                        }

                                        if (binding.symptomContent.text == "") {
                                            for (i in 0..4) {
                                                if (etc[i] != null) {
                                                    cnt++
                                                    if (cnt == 1) {
                                                        binding.symptomContent.text = "${etc[i]}"
                                                    } else {
                                                        binding.symptomContent.text =
                                                            "${binding.symptomContent.text}, ${etc[i]}"
                                                    }
                                                }
                                            }
                                        } else {
                                            for (i in 0..4) {
                                                if (etc[i] != null) {
                                                    binding.symptomContent.text =
                                                        "${binding.symptomContent.text}, ${etc[i]}"
                                                }
                                            }
                                        }
                                        if (binding.symptomContent.text == "") {
                                            binding.symptomContent.setTextColorRes(R.color.gray_a7)
                                            binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                                            binding.symptomContent.text =
                                                "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                                            binding.symtomTitle.text = "어떤 증상이 있었나요?"
                                        }
                                    } else {
                                        binding.symptomContent.setTextColorRes(R.color.gray_a7)
                                        binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                                        binding.symptomContent.text =
                                            "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                                        binding.symtomTitle.text = "어떤 증상이 있었나요?"
                                    }
                                }
                                // 분비물 텍스트 변경
                                val docRef3 = docRef.collection("${day.date}").document("Discharge")

                                docRef3.get().addOnSuccessListener { document ->
                                    val nang = document.get("nang").toString()
                                    val touch = document.get("touch").toString()

                                    var cnt = 0
                                    binding.dischargeFrame.setOnClickListener {
                                        val intent = Intent(
                                            context,
                                            DischargeSettingActivity::class.java
                                        )
                                        for (i in 0..lastData.size-1) {
                                            if (lastData[i].contains(day.date.toString())) {
                                                intent.putExtra("isyellow", true)
                                            }
                                        }
                                        startActivity(intent)
                                    }
                                    if (nang == "null" && touch == "null") {
                                        val docRef0 =
                                            docRef.collection("$day.date").document("Menstruation")
                                        binding.dischargeTitle.text = "분비물이 있었나요?"
                                        binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_very_small)
                                        binding.dischargeContent.text = ""
                                        binding.dischargeContent.visibility = View.GONE
                                        binding.dischargeContent2.visibility = View.GONE
                                        binding.fixDischargeContent1.visibility = View.GONE
                                        binding.fixDischargeContent2.visibility = View.GONE
                                        binding.nangAll.visibility = View.GONE
                                        binding.touchAll.visibility = View.GONE
                                        binding.nangPlusTouch.visibility = View.GONE
                                        binding.dischargeContent.isEnabled = false
                                    } else if (nang != "null") {
                                        docRef0.get().addOnSuccessListener { document ->
                                            if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                                binding.dischargeContent.setTextColorRes(R.color.red_e1)
                                                binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                                            } else {
                                                binding.dischargeContent.setTextColorRes(R.color.green_32)
                                                binding.dischargeContent2.setTextColorRes(R.color.green_32)
                                                for (i in 0..lastData.size - 1) {
                                                    for (j in 0..lastData[i].size - 1) {
                                                        if (lastData[i][j].contains(day.date.toString())) {
                                                            binding.dischargeContent.setTextColorRes(
                                                                R.color.yellow_d7
                                                            )
                                                            binding.dischargeContent2.setTextColorRes(
                                                                R.color.yellow_d7
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        binding.dischargeTitle.text = "오늘의 분비물"
                                        binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                                        binding.dischargeContent.visibility = View.VISIBLE
                                        binding.dischargeContent2.visibility = View.GONE
                                        binding.fixDischargeContent1.visibility = View.VISIBLE
                                        binding.fixDischargeContent2.visibility = View.GONE
                                        binding.nangAll.visibility = View.VISIBLE
                                        binding.touchAll.visibility = View.GONE
                                        binding.nangPlusTouch.visibility = View.VISIBLE
                                        binding.dischargeContent.isEnabled = true
                                        binding.dischargeContent.text = "$nang"
                                        if (touch != "null") {
                                            docRef0.get().addOnSuccessListener { document ->
                                                if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                                    binding.dischargeContent.setTextColorRes(R.color.red_e1)
                                                    binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                                                } else {
                                                    binding.dischargeContent.setTextColorRes(R.color.green_32)
                                                    binding.dischargeContent2.setTextColorRes(R.color.green_32)
                                                    for (i in 0..lastData.size - 1) {
                                                        for (j in 0..lastData[i].size - 1) {
                                                            if (lastData[i][j].contains(day.date.toString())) {
                                                                binding.dischargeContent.setTextColorRes(
                                                                    R.color.yellow_d7
                                                                )
                                                                binding.dischargeContent2.setTextColorRes(
                                                                    R.color.yellow_d7
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            binding.dischargeTitle.text = "오늘의 분비물"
                                            binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder)
                                            binding.dischargeContent.visibility = View.VISIBLE
                                            binding.dischargeContent2.visibility = View.VISIBLE
                                            binding.fixDischargeContent1.visibility = View.VISIBLE
                                            binding.fixDischargeContent2.visibility = View.VISIBLE
                                            binding.nangAll.visibility = View.VISIBLE
                                            binding.touchAll.visibility = View.VISIBLE
                                            binding.nangPlusTouch.visibility = View.VISIBLE
                                            val text = binding.dischargeContent.text.toString()
                                            binding.dischargeContent.text = "${text}"
                                            binding.dischargeContent2.text = "$touch"
                                        }
                                    } else {
                                        docRef0.get().addOnSuccessListener { document ->
                                            if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                                binding.dischargeContent.setTextColorRes(R.color.red_e1)
                                                binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                                            } else {
                                                binding.dischargeContent.setTextColorRes(R.color.green_32)
                                                binding.dischargeContent2.setTextColorRes(R.color.green_32)
                                                for (i in 0..lastData.size - 1) {
                                                    for (j in 0..lastData[i].size - 1) {
                                                        if (lastData[i][j].contains(day.date.toString())) {
                                                            binding.dischargeContent.setTextColorRes(
                                                                R.color.yellow_d7
                                                            )
                                                            binding.dischargeContent2.setTextColorRes(
                                                                R.color.yellow_d7
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        binding.dischargeTitle.text = "오늘의 분비물"
                                        binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                                        binding.dischargeContent.visibility = View.GONE
                                        binding.dischargeContent2.visibility = View.VISIBLE
                                        binding.fixDischargeContent1.visibility = View.GONE
                                        binding.fixDischargeContent2.visibility = View.VISIBLE
                                        binding.nangAll.visibility = View.GONE
                                        binding.touchAll.visibility = View.VISIBLE
                                        binding.nangPlusTouch.visibility = View.VISIBLE
                                        binding.dischargeContent2.isEnabled = true
                                        binding.dischargeContent2.text = "$touch"
                                    }
                                }
                            }
                            //


                            val text = "날짜(${
                                DateTimeFormatter.ofPattern("d MMMM yyyy").format(day.date)
                            })가 선택 되었습니다."

                            // Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
                            binding.exOneYearText.text = selectedDate?.format(
                                DateTimeFormatter.ofPattern("MM월 dd일 EEEE")
                                    .withLocale(Locale.forLanguageTag("ko"))
                            )
                        }
                        binding.exOneCalendar.notifyDayChanged(day)
                    }
                }
            }

            fun bind(day: CalendarDay) {
                this.day = day
                //  binding.exOneYearText.text = DateTimeFormatter.ofPattern("MM월 dd일 EEEE").withLocale(Locale.forLanguageTag("ko")).format(day.date)
            }
        }

        binding.exOneCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                //container.day = day 오류
                container.bind(day)
                val textView = container.textView1
                val dot = container.dot
                textView.text = day.date.dayOfMonth.toString()

                val docRef =
                    db.collection("${auth.currentUser?.email}").document("Record")

                // 기록된 것이 있을 경우 점찍기
                val date = day.date
                val docRef1 = docRef.collection("$date").document("Menstruation")
                val docRef3 = docRef.collection("$date").document("Discharge")
                val docRef2 = docRef.collection("$date").document("Symptom")

                db.collection("${auth.currentUser?.email}").document("Record").get().addOnSuccessListener { document ->
                    docRef.collection("$date").document("Symptom").get().addOnSuccessListener { document ->
                        docRef2.get().addOnSuccessListener { document ->
                            var isEnabled = document.get("isEnabled")
                            if (isEnabled != true) {
                                docRef.collection("$date").document("Menstruation").get()
                                    .addOnSuccessListener { document ->
                                        docRef1.get().addOnSuccessListener { document ->
                                            if (document.get("isEnabled") != true) {
                                                docRef.collection("$date").document("Discharge").get().addOnSuccessListener { document ->
                                                    docRef3.get().addOnSuccessListener { document ->
                                                        if (document.get("isEnabled") != true) {
                                                            dot.visibility = View.INVISIBLE
                                                        } else {
                                                            dot.visibility = View.VISIBLE
                                                        }
                                                    }
                                                }
                                            } else {
                                                dot.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                            } else {
                                dot.visibility = View.VISIBLE
                            }
                        }
                    }
                }
//                docRef2.get().addOnSuccessListener { document ->
//                    var isEnabled = document.get("isEnabled")
//                    if (isEnabled != true) {
//                        docRef1.get().addOnSuccessListener { document ->
//                            if (document.get("isEnabled") != true) {
//                                docRef3.get().addOnSuccessListener { document ->
//                                    if (document.get("isEnabled") != true) {
//                                        dot.visibility = View.INVISIBLE
//                                    } else {
//                                        dot.visibility = View.VISIBLE
//                                    }
//                                }
//                            } else {
//                                dot.visibility = View.VISIBLE
//                            }
//                        }
//                    } else {
//                        dot.visibility = View.VISIBLE
//                    }
//                }

                if (selectedDate == null) {
                    selectedDate = today
                }
                if (selectedDate != today) {
                    textView.background = null
                }
                if (day.owner == DayOwner.THIS_MONTH) {
                    when {
                        selectedDate == day.date -> {
                            textView.setTextColorRes(R.color.black)
                            textView.setBackgroundResource(R.drawable.circle_calendar_today)
                        }
                        today == day.date -> {
                            // textView.setBackgroundResource(R.drawable.circle_calendar_today)
                            textView.setTextColorRes(R.color.black)
                        }
                        else -> {
                            // inDates, outDates
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.black)
                    textView.background = null

                }
            }
        }
        binding.exOneCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        // 첫 시작 화면
        // 여기에 꼭..! LocalDate.now 넣어줘야
        binding.exOneCalendar.scrollToDate(LocalDate.now())
        binding.showFullCalendar.setOnClickListener {
            val intent = Intent(this.context, MonthCalendarActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        binding.symptomContent.text == ""

        update()

        //증상 텍스트 변경
        val docRef =
            db.collection("${auth.currentUser?.email}").document("Record")
        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            var weekTotalAmount: Int = 0
            var arrnum: Int = 0
            var amcnt: Int = 0
            if (document.get("am_cnt") != null)
                amcnt = "${document.get("am_cnt")}".toInt()
            if (document.get("arr_num") != null)
                arrnum = "${document.get("arr_num")}".toInt()
            if (document.get("amount_arr$arrnum") != null) {
                for (i in 1..arrnum) {
                    if(document.get("amount_arr$i.$date") != null)
                    {
                        val temp : Map<String, Int> = document.get("amount_arr$i") as Map<String, Int>
                        for(value in temp.values)
                            weekTotalAmount += value
                        Log.e("arr 결과 : ", "$weekTotalAmount")
                    }
                    //Log.e("n 테스트 : ", "$n")
                    Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                }
            }
            // binding.symptomContent.text = ""
            // 통증 텍스트 변경
            val docRef2 = docRef.collection("${date}").document("Symptom")
            val docRef0 = docRef.collection("$date").document("Menstruation")
            docRef0.get().addOnSuccessListener { document ->
                binding.menstruationFrame.setOnClickListener {
                    val intent = Intent(
                        context,
                        MenstruationSettingActivity::class.java
                    )
                    for (i in 0..lastData.size-1) {
                        if (lastData[i].contains(date.toString())) {
                            intent.putExtra("isyellow", true)
                        }
                    }

                    startActivity(intent)
                }
                if (document.get("isEnabled") != null) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행

                    val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                    val endTime = document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                    var todayTotalAmount: Int = 0
                    for (i in 1..num.toString().toInt()) {
                        //todayTotalAmount += document.get("men_list$i.amount").toString().toInt()
                        todayTotalAmount = "${document.get("totalAmount")}".toInt()
                    }
                    binding.menstruationFrame.setOnClickListener {
                        val intent = Intent(context, MenstruationActivity::class.java)
                        startActivity(intent)
                    }
                    binding.menstruationContent2.visibility = View.VISIBLE
                    binding.menstruationContent3.visibility = View.VISIBLE
                    binding.menstruationContent4.visibility = View.VISIBLE
                    binding.menstruationTitle.setText("최근 교체시간 ${endTime}")
                    binding.menstruationContent.setText("오늘 월경량")
                    binding.menstruationContent2.setText("${todayTotalAmount} ml")
                    binding.menstruationContent3.setText("주기 누적 월경량")
                    Log.e("주기 누적 월경량 순서4 -> ", "$weekTotalAmount")
                    binding.menstruationContent4.setText("${weekTotalAmount} ml")

                } else {

                    binding.menstruationTitle.setText("월경을 기록해 보세요")
                    binding.menstruationContent.setText("상세한 월경량 수치 측정과 색상 기록을 통해 \n여성 질환을 예방할 수 있습니다.")
                    binding.menstruationContent2.visibility = View.GONE
                    binding.menstruationContent3.visibility = View.GONE
                    binding.menstruationContent4.visibility = View.GONE
                }
            }
            docRef2.get().addOnSuccessListener { document ->
                //isEnabled = document.get("isEnabled")
                binding.symptomFrame.setOnClickListener {
                    val intent = Intent(
                        context,
                        SymptomSettingActivity::class.java
                    )
                    for (i in 0..lastData.size-1) {
                        if (lastData[i].contains(date.toString())) {
                            intent.putExtra("isyellow", true)
                        }
                    }
                    startActivity(intent)
                }
                if (document.get("isEnabled") == true) {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") != null) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.symptomContent.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.symptomContent.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.symptomContent.setTextColorRes(R.color.yellow_d7)
                                        isYellow = true
                                        break
                                    }
                                }
                            }
                        }
                    }
                    val pain: ArrayList<String?> =
                        document.get("pain") as ArrayList<String?>
                    val bowel = document.get("bowel")
                    val etc: ArrayList<String?> =
                        document.get("etc") as ArrayList<String?>
                    binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.symtomTitle.text =
                        "오늘의 증상"

                    var cnt = 0
                    var cnt2 = 0
                    binding.symptomContent.text = ""
                    for (i in 0..5) {
                        if (pain[i] != null) {
                            cnt++
                            if (cnt == 1) {
                                binding.symptomContent.text = "${pain[i]}"
                            } else {
                                binding.symptomContent.text =
                                    "${binding.symptomContent.text}, ${pain[i]}"
                            }
                        } else {
                            cnt2++
                            if (cnt2 == 6)
                                binding.symptomContent.text = ""
                        }
                    }

                    if (bowel != null) {
                        if (binding.symptomContent.text == "")
                            binding.symptomContent.text = "$bowel"
                        else
                            binding.symptomContent.text =
                                "${binding.symptomContent.text}, $bowel"
                    }

                    if (binding.symptomContent.text == "") {
                        for (i in 0..4) {
                            if (etc[i] != null) {
                                cnt++
                                if (cnt == 1) {
                                    binding.symptomContent.text = "${etc[i]}"
                                } else {
                                    binding.symptomContent.text =
                                        "${binding.symptomContent.text}, ${etc[i]}"
                                }
                            }
                        }
                    } else {
                        for (i in 0..4) {
                            if (etc[i] != null) {
                                binding.symptomContent.text =
                                    "${binding.symptomContent.text}, ${etc[i]}"
                            }
                        }
                    }
                    if (binding.symptomContent.text == "") {
                        binding.symptomContent.text =
                            "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                        binding.symtomTitle.text = "어떤 증상이 있었나요?"
                        binding.symptomContent.setTextColorRes(R.color.gray_a7)
                        binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                    }
                } else {
                    binding.symptomContent.text =
                        "오늘 당신의 하루에 \n어떤 변화가 있었는지 알려주세요"
                    binding.symtomTitle.text = "어떤 증상이 있었나요?"
                    binding.symptomContent.setTextColorRes(R.color.gray_a7)
                    binding.symptomFrame.setBackgroundResource(R.drawable.record_boxborder)
                }
            }
            // 분비물 텍스트 변경
            val docRef3 = docRef.collection("$date").document("Discharge")

            docRef3.get().addOnSuccessListener { document ->
                val nang = document.get("nang").toString()
                val touch = document.get("touch").toString()

                binding.dischargeFrame.setOnClickListener {
                    val intent = Intent(
                        context,
                        DischargeSettingActivity::class.java
                    )
                    for (i in 0..lastData.size-1) {
                        if (lastData[i].contains(date.toString())) {
                            intent.putExtra("isyellow", true)
                        }
                    }
                    startActivity(intent)
                }

                val sym_arr = arrayListOf<String>(nang, touch)
                var cnt = 0
                if (nang == "null" && touch == "null") {
                    binding.dischargeTitle.text = "분비물이 있었나요?"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_very_small)
                    binding.dischargeContent.text = ""
                    binding.dischargeContent.visibility = View.GONE
                    binding.dischargeContent2.visibility = View.GONE
                    binding.fixDischargeContent1.visibility = View.GONE
                    binding.fixDischargeContent2.visibility = View.GONE
                    binding.nangAll.visibility = View.GONE
                    binding.touchAll.visibility = View.GONE
                    binding.nangPlusTouch.visibility = View.GONE
                    binding.dischargeContent.isEnabled = false
                } else if (nang != "null") {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.dischargeContent.setTextColorRes(R.color.red_e1)
                            binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.dischargeContent.setTextColorRes(R.color.green_32)
                            binding.dischargeContent2.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                        binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                    }
                                }
                            }
                        }
                    }
                    binding.dischargeTitle.text = "오늘의 분비물"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.dischargeContent.visibility = View.VISIBLE
                    binding.dischargeContent2.visibility = View.GONE
                    binding.fixDischargeContent1.visibility = View.VISIBLE
                    binding.fixDischargeContent2.visibility = View.GONE
                    binding.nangAll.visibility = View.VISIBLE
                    binding.touchAll.visibility = View.GONE
                    binding.nangPlusTouch.visibility = View.VISIBLE
                    binding.dischargeContent.isEnabled = true
                    binding.dischargeContent.text = "$nang"
                    if (touch != "null") {
                        docRef0.get().addOnSuccessListener { document ->
                            if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                                binding.dischargeContent.setTextColorRes(R.color.red_e1)
                                binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                            } else {
                                binding.dischargeContent.setTextColorRes(R.color.green_32)
                                binding.dischargeContent2.setTextColorRes(R.color.green_32)
                                for (i in 0..lastData.size - 1) {
                                    for (j in 0..lastData[i].size - 1) {
                                        if (lastData[i][j].contains(date.toString())) {
                                            binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                            binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                        }
                                    }
                                }
                            }
                        }
                        binding.dischargeTitle.text = "오늘의 분비물"
                        binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder)
                        binding.dischargeContent.visibility = View.VISIBLE
                        binding.dischargeContent2.visibility = View.VISIBLE
                        binding.fixDischargeContent1.visibility = View.VISIBLE
                        binding.fixDischargeContent2.visibility = View.VISIBLE
                        binding.nangAll.visibility = View.VISIBLE
                        binding.touchAll.visibility = View.VISIBLE
                        binding.nangPlusTouch.visibility = View.VISIBLE
                        val text = binding.dischargeContent.text.toString()
                        binding.dischargeContent.text = "${text}"
                        binding.dischargeContent2.text = "$touch"
                    }
                } else {
                    docRef0.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") == true) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
                            binding.dischargeContent.setTextColorRes(R.color.red_e1)
                            binding.dischargeContent2.setTextColorRes(R.color.red_e1)
                        } else {
                            binding.dischargeContent.setTextColorRes(R.color.green_32)
                            binding.dischargeContent2.setTextColorRes(R.color.green_32)
                            for (i in 0..lastData.size - 1) {
                                for (j in 0..lastData[i].size - 1) {
                                    if (lastData[i][j].contains(date.toString())) {
                                        binding.dischargeContent.setTextColorRes(R.color.yellow_d7)
                                        binding.dischargeContent2.setTextColorRes(R.color.yellow_d7)
                                    }
                                }
                            }
                        }
                    }
                    binding.dischargeTitle.text = "오늘의 분비물"
                    binding.dischargeFrame.setBackgroundResource(R.drawable.record_boxborder_small)
                    binding.dischargeContent.visibility = View.GONE
                    binding.dischargeContent2.visibility = View.VISIBLE
                    binding.fixDischargeContent1.visibility = View.GONE
                    binding.fixDischargeContent2.visibility = View.VISIBLE
                    binding.nangAll.visibility = View.GONE
                    binding.touchAll.visibility = View.VISIBLE
                    binding.nangPlusTouch.visibility = View.VISIBLE
                    binding.dischargeContent2.isEnabled = true
                    binding.dischargeContent2.text = "$touch"
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun update() {
        var dateformat = SimpleDateFormat("yyyy-MM-dd")

        //최근 시작날짜, 끝날짜, 주기, 기간 데이터 받아왔음
        db.collection("${auth.currentUser?.email}").document("Init").get()
            .addOnSuccessListener { document ->
                lastData.clear()
                val interval =
                    document.get("interval") //월경주기 -> String타입임 Int로 바꾸려면 val interval  = "${document.get("interval")}".toInt()
                cycleDay = interval.toString().toDouble()

                val duration = document.get("duration") //월경기간
                period = duration.toString().toInt()

                val start = document.get("mendate_start") // 최근 월경시작일 "2021-08-20" 문자열로 저장되어있음
                val end = document.get("mendate_end") // 최근 월경종료일

                var initDateRange =
                    (dateformat.parse(end.toString()).time - dateformat.parse(start.toString()).time) / (24 * 60 * 60 * 1000)

                // 초기 값에 의한 초기 주기 넣기
                var forplusDate: Calendar = Calendar.getInstance()
                cycleDays = mutableListOf("${start.toString()}")
                forplusDate.time = dateformat.parse(cycleDays[0])
                for (i in 0..initDateRange - 1) {
                    forplusDate.add(Calendar.DATE, 1)
                    cycleDays.add(dateformat.format(Date(forplusDate.time.time)))
                }

                lastData = mutableListOf(cycleDays)

                // 입력된 기록갑 넣기
                var m = today.monthValue.toString()
                var d = today.dayOfMonth.toString()

                if (m.toInt() < 10)
                    m = "0${m}"
                if (d.toInt() < 10)
                    d = "0${d}"

                val date2 = "${today.year}-$m-$d"
                val data = hashMapOf(
                    "Today" to date2
                )

                db.collection("${auth.currentUser?.email}").document("Record")
                    .set(data, SetOptions.merge())

                docRef.get().addOnSuccessListener { document ->
                    val date = "${document.get("clicked_date")}"

                    var cnt = 0
                    var arrnum = 0
                    if (document.get("arr_num") != null)
                        arrnum = "${document.get("arr_num")}".toInt()

                    Log.e("arrnum : ", "$arrnum")

                    if (document.get("null_cnt") == null || "${document.get("null_cnt")}".toInt() < 0) {
                        val data = hashMapOf(
                            "null_cnt" to 0
                        )
                        docRef.set(data, SetOptions.merge())
                    } else {
                        cnt = "${document.get("null_cnt")}".toInt() //null_cnt 값 cnt에 넣어줌
                    }

                    docRef.collection("$date").document("Menstruation").get()
                        .addOnSuccessListener { document ->
                            if (document.get("isEnabled") == null && document.get("isChecked") != true) {
                                val data = hashMapOf(
                                    "null_cnt" to cnt + 1
                                )
                                docRef.set(data, SetOptions.merge())
                            }
                            val data = hashMapOf(
                                "isChecked" to true
                            )
                            docRef.collection("$date").document("Menstruation")
                                .set(data, SetOptions.merge())
                        }

                    recordedDate.clear()

                    if (arrnum > 0) {
                        for (i in 1..arrnum) {
                            if (document.get("duration_arr$i") != null) {
                                val du_arr: MutableList<String> =
                                    document.get("duration_arr$i") as MutableList<String> //날짜 저장되어 있는 배열 불러오기
                                Log.e("du_arr 결과 : ", "$du_arr")
                                recordedDate.add(du_arr)
                                lastData.add(du_arr)
                            }
                        }
                    }

                    // 초기값으로 주기 계산(마지막 시작 날짜 받아서 예상값 주기)
                    var lastCycle_firstDate = Calendar.getInstance()
                    if (recordedDate.size > 0) {
                        // 기록된 값이 있는 경우
                        lastCycle_firstDate.time =
                            dateformat.parse(recordedDate[recordedDate.size - 1][0])
                    } else {
                        // 기록된 값이 없는 경우
                        lastCycle_firstDate.time = dateformat.parse(lastData[0][0])
                    }


                    // 평균 기간 계산
                    // 기록된 값들을 받아와 각 (cycleDays 마지막 날짜-첫 날짜)로 계산한다.
                    var sumPeriod = 0
                    for (i in 0..lastData.size - 1) {
                        val lastDay = dateformat.parse(lastData[i].last())
                        val firstDay = dateformat.parse(lastData[i].first())
                        var dayminus = (lastDay.time - firstDay.time) / (24 * 60 * 60 * 1000)

                        sumPeriod += dayminus.toInt() + 1
                    }
                    period = round((sumPeriod / lastData.size).toDouble()).toInt()


                    // 평균 주기 계산
                    // 기록된 값들을 받아와 '이번 생리 시작일-지난 생리 시작일'로 계산한다.
                    // lastData 대신 기록된 데이터가 들어가야함
                    var sumCycle = 0
                    var cycleCount = 0
                    var down = lastData.size - 1

                    // 기록된 값이 있을 경우 주기 업데이트. 없을 땐 초기 입력 주기 그대로 사용
                    if (lastData.size > 1) {
                        for (i in down downTo 0) {
                            val nextCycle = dateformat.parse(lastData[i].first())
                            var lastCycle = Date()
                            if (i - 1 >= 0) {
                                lastCycle = dateformat.parse(lastData[i - 1].first())
                                var dayminus =
                                    (nextCycle.time - lastCycle.time) / (24 * 60 * 60 * 1000)
                                sumCycle += dayminus.toInt()
                                cycleCount += 1
                            } else {
                                break
                            }
                        }
                        var testcycleDay =
                            (sumCycle + interval.toString().toDouble()) / (cycleCount + 1)
                        cycleDay = testcycleDay
                    }

                    // 칩에 주기 표시
                    binding.term.text = "${round(cycleDay).toInt()}일"


                    // 예상 날짜 추가 (6개의 예상 주기 만들어서 예상날짜와 이후 날짜 넣기)


                    // 기록된 값 또는 초기값 이후 첫 예상날짜
                    var lastCycle2_firstDate = lastCycle_firstDate

                    lastCycle2_firstDate.add(Calendar.DATE, floor(cycleDay).toInt())
                    expectCycle2_firstDate = lastCycle2_firstDate

                    var nextCycle2 =
                        mutableListOf("${dateformat.format(Date(expectCycle2_firstDate.time.time))}")
                    lastData.add(nextCycle2)
                    for (i in 0..period - 2) {
                        expectCycle2_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle2_firstDate.time.time)))
                    }

                    // 두번째 예상 날짜
                    var lastCycle3_firstDate = Calendar.getInstance()
                    lastCycle3_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle3_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle3_firstDate = lastCycle3_firstDate

                    var nextCycle3 =
                        mutableListOf("${dateformat.format(Date(expectCycle3_firstDate.time.time))}")
                    lastData.add(nextCycle3)
                    for (i in 0..period - 2) {
                        expectCycle3_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle3_firstDate.time.time)))
                    }

                    // 세번째 예상 날짜
                    var lastCycle4_firstDate = Calendar.getInstance()
                    lastCycle4_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle4_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle4_firstDate = lastCycle4_firstDate

                    var nextCycle4 =
                        mutableListOf("${dateformat.format(Date(expectCycle4_firstDate.time.time))}")
                    lastData.add(nextCycle4)
                    for (i in 0..period - 2) {
                        expectCycle4_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle4_firstDate.time.time)))
                    }

                    // 네번째 예상 날짜
                    var lastCycle5_firstDate = Calendar.getInstance()
                    lastCycle5_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle5_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle5_firstDate = lastCycle5_firstDate

                    var nextCycle5 =
                        mutableListOf("${dateformat.format(Date(expectCycle5_firstDate.time.time))}")
                    lastData.add(nextCycle5)
                    for (i in 0..period - 2) {
                        expectCycle5_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle5_firstDate.time.time)))
                    }

                    // 다섯번째 예상 날짜
                    var lastCycle6_firstDate = Calendar.getInstance()
                    lastCycle6_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle6_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle6_firstDate = lastCycle6_firstDate

                    var nextCycle6 =
                        mutableListOf("${dateformat.format(Date(expectCycle6_firstDate.time.time))}")
                    lastData.add(nextCycle6)
                    for (i in 0..period - 2) {
                        expectCycle6_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle6_firstDate.time.time)))
                    }

                    // 여섯번째 예상 날짜
                    var lastCycle7_firstDate = Calendar.getInstance()
                    lastCycle7_firstDate.time = dateformat.parse(lastData.last()[0])

                    lastCycle7_firstDate.add(Calendar.DATE, round(cycleDay).toInt())
                    expectCycle7_firstDate = lastCycle7_firstDate

                    var nextCycle7 =
                        mutableListOf("${dateformat.format(Date(expectCycle7_firstDate.time.time))}")
                    lastData.add(nextCycle7)
                    for (i in 0..period - 2) {
                        expectCycle7_firstDate.add(Calendar.DATE, 1)
                        lastData.last()
                            .add(dateformat.format(Date(expectCycle7_firstDate.time.time)))
                    }



                    for (i in 0..lastData.size - 1) {
                        Log.e("lastData update ${i}", "${lastData[i]}")
                    }


                    val docRef = db.collection("${auth.currentUser?.email}").document("Record")
                    docRef.get().addOnSuccessListener { document ->
                        val date = document.get("clicked_date")


                        val docRef2 = docRef.collection("$date").document("Menstruation")
                        var weekTotalAmount: Int = 0
                        var arrnum: Int = 0
                        var amcnt: Int = 1
                        var arr = {0}
                        if (document.get("am_cnt") != null)
                            amcnt = "${document.get("am_cnt")}".toInt()
                        if (document.get("arr_num") != null)
                            arrnum = "${document.get("arr_num")}".toInt()
                        if (document.get("amount_arr$arrnum") != null) {
                            for (i in 1..arrnum) {
                                if(document.get("amount_arr$i.$date") != null)
                                {
                                    val temp : Map<String, Int> = document.get("amount_arr$i") as Map<String, Int>
                                    for(value in temp.values)
                                        weekTotalAmount += value
                                    Log.e("arr 결과 : ", "$weekTotalAmount")
                                }
                                //Log.e("n 테스트 : ", "$n")
                                Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                            }
                        }
                        Log.e("테스트테스트테스트 : ", "$arr")
                        docRef2.get().addOnSuccessListener { document ->
                            if (document.get("isEnabled") == null) {
                                binding.menstruationTitle.setText("월경을 기록해 보세요")
                                binding.menstruationContent.setText("상세한 월경량 수치 측정과 색상 기록을 통해 \n여성 질환을 예방할 수 있습니다.")
                                binding.menstruationContent2.visibility = View.GONE
                                binding.menstruationContent3.visibility = View.GONE
                                binding.menstruationContent4.visibility = View.GONE

                            } else {
                                val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                                val endTime =
                                    document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                                var todayTotalAmount: Int = 0
                                for (i in 1..num.toString().toInt()) {
                                    /* todayTotalAmount += document.get("men_list$i.amount")
                                         .toString()
                                         .toInt()*/
                                    todayTotalAmount = "${document.get("totalAmount")}".toInt()
                                }
                                binding.menstruationContent2.visibility = View.VISIBLE
                                binding.menstruationContent3.visibility = View.VISIBLE
                                binding.menstruationContent4.visibility = View.VISIBLE
                                binding.menstruationTitle.setText("최근 교체시간 ${endTime}")
                                binding.menstruationContent.setText("오늘 월경량")
                                binding.menstruationContent2.setText("${todayTotalAmount} ml")
                                binding.menstruationContent3.setText("주기 누적 월경량")
                                Log.e("주기 누적 월경량 순서1 -> ", "$weekTotalAmount")
                                binding.menstruationContent4.setText("${weekTotalAmount} ml")

                            }
                        }
                    }


                    // view update
                    // 다음 주기 확인하기
                    var changeBackValue = 0
                    for (i in 0..lastData.size - 1) {
                        if (changeBackValue > 0) {
                            break
                        }
                        for (j in 0..lastData[i].size - 1) {
                            if (changeBackValue > 0) {
                                break
                            }
                            if (lastData[i][j].contains(date)) {
                                binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_yellow)
                                binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_yellow)
                                binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_yellow)
                                binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_yellow)
                                changeBackValue += 1
                                isYellow = true
                                break
                            }
                        }
                    }
                    if (changeBackValue == 0) {
                        binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_green)
                        binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_green)
                        binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_green)
                        binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_green)
                    }
                    for (i in 0..recordedDate.size - 1) {
                        for (j in 0..recordedDate[i].size - 1) {
                            if (recordedDate[i][j].contains(date) == true) {
                                binding.exOneAppBarLayout.setBackgroundResource(R.drawable.gradient_main_red)
                                binding.chipFrame1.setBackgroundResource(R.drawable.record_chip_red)
                                binding.chipFrame2.setBackgroundResource(R.drawable.record_chip_red)
                                binding.chipFrame3.setBackgroundResource(R.drawable.record_chip_red)
                            }
                        }
                    }

                    var valueforBreak: Int = 0
                    for (i in 0..lastData.size - 1) {
                        if (valueforBreak > 0) break
                        for (j in 0..lastData[i].size - 1) {
                            if (valueforBreak > 0) break

                            var selectDate: LocalDate =
                                LocalDate.parse(date)
                            // 월경 중일 경우
                            if (lastData[i][j].split("-")[0].toInt() == date.split("-")[0].toInt()
                                && lastData[i][j].split("-")[1].toInt() == date.split("-")[1].toInt()
                                && lastData[i][j].split("-")[2].toInt() == date.split("-")[2].toInt()
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])
                                if ((i+1) == lastData.size) {
                                    expectLocaldDateformat = LocalDate.parse(lastData[i][0])
                                }
                                else {
                                    expectLocaldDateformat = LocalDate.parse(lastData[i+1][0])
                                }

                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(selectDate)

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                if ((i+1) == lastData.size){
                                    ovulationCalculate.time =
                                        Date(dateformat.parse(lastData[i][0]).time)
                                }
                                else {
                                    ovulationCalculate.time =
                                        Date(dateformat.parse(lastData[i + 1][0]).time)
                                }
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()
                                binding.nextOvulationDate.text =
                                    DateTimeFormatter.ofPattern("MM.dd (EE)")
                                        .withLocale(Locale.forLanguageTag("ko"))
                                        .format(ovulationLocaldDateformat)

                                if ((i+1) == lastData.size){
                                    binding.nextDurationDate.text =
                                        "${lastData[i][0].split("-")[1]}.${
                                            lastData[i][0].split(
                                                "-"
                                            )[2]
                                        } (${
                                            DateTimeFormatter.ofPattern("EE")
                                                .withLocale(Locale.forLanguageTag("ko"))
                                                .format(expectLocaldDateformat)
                                        })"

                                    binding.nextDurationTitle.text = "오늘은 \n월경 예상 0일째입니다."
                                }
                                else {
                                    binding.nextDurationDate.text =
                                        "${lastData[i + 1][0].split("-")[1]}.${
                                            lastData[i + 1][0].split(
                                                "-"
                                            )[2]
                                        } (${
                                            DateTimeFormatter.ofPattern("EE")
                                                .withLocale(Locale.forLanguageTag("ko"))
                                                .format(expectLocaldDateformat)
                                        })"

                                    binding.nextDurationTitle.text = "오늘은 \n월경 예상 ${j + 1}일째입니다."
                                }

                                if (isYellow) {
                                }
                                for (i in 0..recordedDate.size - 1) {
                                    for (j in 0..recordedDate[i].size - 1) {
                                        if (recordedDate[i][j].contains(date) == true) {
                                            binding.nextDurationTitle.text =
                                                "오늘은 \n월경 ${j + 1}일째입니다."
                                        }
                                    }
                                }
                                valueforBreak += 1
                                break
                            }
                            // 이번 년도, 다음 달이 다음 주기인 경우
                            else if (lastData[i][j].split("-")[0].toInt() == date.split("-")[0].toInt()
                                && lastData[i][j].split("-")[1].toInt() == date.split("-")[1].toInt() +1
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])
                                var selectDate: LocalDate =
                                    LocalDate.parse(date)

                                // 남은 일수
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(selectDate)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)


                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()
                                if (ovulationCalculate.time >= Date.from(
                                        selectDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size) {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }


                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"
                                valueforBreak += 1
                                break
                            }
                            // 이번 년도, 이번 달, 이후 날짜가 주기일 경우
                            else if (lastData[i][j].split("-")[0].toInt() == date.split("-")[0].toInt()
                                && lastData[i][j].split("-")[1].toInt() == date.split("-")[1].toInt()
                                && lastData[i][j].split("-")[2].toInt() > date.split("-")[2].toInt()
                            ) {
                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])

                                // 남은 일수
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(selectDate)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)

                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()

                                if (ovulationCalculate.time >= Date.from(
                                        selectDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size) {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }

                                valueforBreak += 1
                                break

                            }
                            // 내년이 주기인 경우
                            else if (lastData[i][j].split("-")[0].toInt() == selectDate.year + 1) {

                                var expectLocaldDateformat: LocalDate =
                                    LocalDate.parse(lastData[i][0])
                                // 남은 일수
                                if ((i+1) == lastData.size) {
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i][0])
                                }
                                else {
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i+1][0])
                                }
                                var todayDateString =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(selectDate)
                                var dayminus =
                                    (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                        todayDateString
                                    ).time) / (24 * 60 * 60 * 1000)

                                // 배란일 계산
                                val ovulationCalculate = Calendar.getInstance()
                                ovulationCalculate.time =
                                    Date(dateformat.parse(lastData[i][0]).time)
                                ovulationCalculate.add(Calendar.DATE, -14)
                                var ovulationLocaldDateformat: LocalDate =
                                    ovulationCalculate.toInstant().atZone(
                                        ZoneId.systemDefault()
                                    ).toLocalDate()

                                if (ovulationCalculate.time >= Date.from(
                                        selectDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ) {
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                } else {
                                    if ((i+1) == lastData.size) {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i][0]).time)
                                    }
                                    else {
                                        ovulationCalculate.time =
                                            Date(dateformat.parse(lastData[i + 1][0]).time)
                                    }
                                    ovulationCalculate.add(Calendar.DATE, -14)
                                    var ovulationLocaldDateformat: LocalDate =
                                        ovulationCalculate.toInstant().atZone(
                                            ZoneId.systemDefault()
                                        ).toLocalDate()
                                    binding.nextOvulationDate.text =
                                        DateTimeFormatter.ofPattern("MM.dd (EE)")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(ovulationLocaldDateformat)
                                }

                                binding.nextDurationDate.text =
                                    "${lastData[i][0].split("-")[1]}.${lastData[i][0].split("-")[2]} (${
                                        DateTimeFormatter.ofPattern("EE")
                                            .withLocale(Locale.forLanguageTag("ko"))
                                            .format(expectLocaldDateformat)
                                    })"
                                binding.nextDurationTitle.text = "다음 월경까지\n${dayminus}일 남았습니다."
                                valueforBreak += 1
                                break
                            }
                        }
                    }
                }
                for (i in 0..lastData.size - 1) {
                    Log.e("lastData${i}", "${lastData[i]}")
                }


            }
    }
}