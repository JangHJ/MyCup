package com.yours.mycup.record

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentMonthCalendarBinding
import com.yours.mycup.databinding.MonthcalendarDayBinding
import com.yours.mycup.databinding.MonthcalendarHeaderBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.round

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = MonthcalendarHeaderBinding.bind(view).exTwoHeaderText
}

class ScrollCalendarFragment : Fragment() {

    private var selectedDate: LocalDate? = null
    private lateinit var binding: FragmentMonthCalendarBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val docRef = db.collection("${auth.currentUser?.email}").document("Record")

    private lateinit var callback: OnBackPressedCallback

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val nextMonthFirstDay = LocalDate.of(2021, Month.SEPTEMBER, 1)
    @RequiresApi(Build.VERSION_CODES.O)
    val currentMonth = YearMonth.now()

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    var cycleDays =
        mutableListOf("2021-07-11", "2021-07-12", "2021-07-13", "2021-07-14", "2021-07-15")

    var xValue: Int = 0
    var lastData = mutableListOf(cycleDays, mutableListOf("2021-03-25", "2021-06-23"))

    var cycleDay: Double = 26.7
    var period: Int = 5
    var expectCycle2_firstDate: Calendar = Calendar.getInstance()
    var expectCycle3_firstDate: Calendar = Calendar.getInstance()
    var expectCycle4_firstDate: Calendar = Calendar.getInstance()
    var expectCycle5_firstDate: Calendar = Calendar.getInstance()
    var expectCycle6_firstDate: Calendar = Calendar.getInstance()
    var expectCycle7_firstDate: Calendar = Calendar.getInstance()
    var recordedDate = mutableListOf(cycleDays)
    var isYellow = false
    lateinit var loading: LoadingDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_month_calendar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dateformat = SimpleDateFormat("yyyy-MM-dd")
        binding = FragmentMonthCalendarBinding.bind(view)

        // bottomSheet
        var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
        bottomSheetBehavior = BottomSheetBehavior.from<LinearLayout>(binding.sheet2.root)

        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = dpToPx(requireContext(), 0)

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
            }
        }

        // 연도 업데이트
        binding.exTwoCalendar.monthScrollListener = {
            binding.yearTitle.text = it.yearMonth.year.toString()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnBack.setOnClickListener {
            //parentFragmentManager.beginTransaction().remove(this)?.commit()
            // parentFragmentManager.popBackStack()

            this.onStop()
            activity?.onBackPressed()
        }
        // 시작 month, 마지막 month, 초기 화면 month 설정
        // 아예 무한(Int.MAX_VALUE)으로 보내면 다운.
        binding.exTwoCalendar.setup(
            YearMonth.now().minusMonths(6),
            YearMonth.now().plusMonths(6),
            daysOfWeek.first()
        )

        // 초기에 맨 위로 이동
        binding.exTwoCalendar.scrollToMonth(YearMonth.now().minusMonths(6))

        loading = LoadingDialog(requireContext())
        loading.show()



        class DayViewContainer(view: View) : ViewContainer(view) {

            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay

            // Example2CalendarDayBinding를 바인딩
            val binding2 = MonthcalendarDayBinding.bind(view)

            init {
                binding2.scrollCalendarDay.setOnClickListener {
                    val date = day.date

                    if (selectedDate == day.date) {
                        selectedDate = null
                        binding.exTwoCalendar.notifyDayChanged(day)
                    }
                    else {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.exTwoCalendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.exTwoCalendar.notifyDateChanged(oldDate) }

                        // 선택된 날짜 db 저장
                        val data = hashMapOf(
                            "clicked_date" to "${day.date}"
                        )

                        db.collection("${auth.currentUser?.email}").document("Record")
                            .set(data, SetOptions.merge())

                        //증상 텍스트 변경
                        val docRef =
                            db.collection("${auth.currentUser?.email}").document("Record")
                        docRef.get().addOnSuccessListener { document ->
                            val cdate = document.get("clicked_date")
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
                            val docRef2 = docRef.collection("${day.date}").document("Symptom")
                            val docRef3 = docRef.collection("${day.date}").document("Discharge")
                            val docRef0 =
                                docRef.collection("${day.date}").document("Menstruation")

                            docRef0.get().addOnSuccessListener { document ->
                                if (document.get("isEnabled") != null) {
                                    val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                                    val endTime =
                                        document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                                    var todayTotalAmount: Int = 0
                                    for (i in 1..num.toString().toInt()) {
                                        /*todayTotalAmount += document.get("men_list$i.amount")
                                            .toString().toInt()*/
                                        todayTotalAmount = "${document.get("totalAmount")}".toInt()
                                    }
                                    binding.sheet2.recordTextview1.visibility = View.VISIBLE
                                    binding.sheet2.recordTextview1.setTextColorRes(R.color.black_32)
                                    binding.sheet2.recordTextview1.setText("오늘 월경량 ${todayTotalAmount} ml, 주기 누적 월경량 ${weekTotalAmount} ml")

                                } else {
                                    binding.sheet2.recordTextview1.visibility = View.GONE
                                    docRef2.get().addOnSuccessListener { document ->
                                        if (document.get("isEnabled") == null) {
                                            docRef3.get().addOnSuccessListener { document ->
                                                if (document.get("isEnabled") == null) {
                                                    binding.sheet2.recordTextview1.visibility =
                                                        View.GONE
                                                    binding.sheet2.recordTextview2.visibility =
                                                        View.GONE
                                                    binding.sheet2.recordTextview3.visibility =
                                                        View.VISIBLE
                                                    binding.sheet2.recordTextview3.setTextColorRes(R.color.black_32)
                                                    binding.sheet2.recordTextview3.setText("기록이 존재하지 않습니다.")

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            docRef2.get().addOnSuccessListener { document ->
                                //isEnabled = document.get("isEnabled")
                                if (document.get("isEnabled") == true) {

                                    binding.sheet2.recordTextview2.visibility = View.VISIBLE
                                    val pain: ArrayList<String?> =
                                        document.get("pain") as ArrayList<String?>
                                    val bowel = document.get("bowel")
                                    val etc: ArrayList<String?> =
                                        document.get("etc") as ArrayList<String?>

                                    var cnt = 0
                                    var cnt2 = 0
                                    binding.sheet2.recordTextview2.text = ""
                                    for (i in 0..5) {
                                        if (pain[i] != null) {
                                            cnt++
                                            if (cnt == 1) {
                                                binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                                binding.sheet2.recordTextview2.text = "${pain[i]}"
                                            } else {
                                                binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                                binding.sheet2.recordTextview2.text =
                                                    "${binding.sheet2.recordTextview2.text}, ${pain[i]}"
                                            }
                                        } else {
                                            cnt2++
                                            if (cnt2 == 6)
                                                binding.sheet2.recordTextview2.text = ""
                                        }
                                    }

                                    if (bowel != null) {
                                        if (binding.sheet2.recordTextview2.text == "") {
                                            binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                            binding.sheet2.recordTextview2.text = "$bowel"
                                        } else {
                                            binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                            binding.sheet2.recordTextview2.text =
                                                "${binding.sheet2.recordTextview2.text}, $bowel"
                                        }
                                    }

                                    if (binding.sheet2.recordTextview2.text == "") {
                                        for (i in 0..4) {
                                            if (etc[i] != null) {
                                                cnt++
                                                if (cnt == 1) {
                                                    binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                                    binding.sheet2.recordTextview2.text =
                                                        "${etc[i]}"
                                                } else {
                                                    binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                                    binding.sheet2.recordTextview2.text =
                                                        "${binding.sheet2.recordTextview2.text}, ${etc[i]}"
                                                }
                                            }
                                        }
                                    } else {
                                        for (i in 0..4) {
                                            if (etc[i] != null) {
                                                binding.sheet2.recordTextview2.setTextColorRes(R.color.black_32)
                                                binding.sheet2.recordTextview2.text =
                                                    "${binding.sheet2.recordTextview2.text}, ${etc[i]}"
                                            }
                                        }
                                    }
                                } else {
                                    binding.sheet2.recordTextview2.visibility = View.GONE
                                }
                            }
                            // 분비물 텍스트 변경

                            docRef3.get().addOnSuccessListener { document ->
                                val nang = document.get("nang").toString()
                                val touch = document.get("touch").toString()

                                var cnt = 0
                                if (nang == "null" && touch == "null") {
                                    val docRef0 =
                                        docRef.collection("$day.date").document("Menstruation")
                                    binding.sheet2.recordTextview3.visibility = View.GONE
                                } else if (nang != "null") {
                                    binding.sheet2.recordTextview3.setTextColorRes(R.color.black_32)
                                    binding.sheet2.recordTextview3.visibility = View.VISIBLE
                                    binding.sheet2.recordTextview3.text = "냉 분비량 $nang"
                                    if (touch != "null") {
                                        binding.sheet2.recordTextview3.setTextColorRes(R.color.black_32)
                                        binding.sheet2.recordTextview3.text =
                                            "냉 분비량 $nang, 촉감 $touch"
                                    }
                                } else {
                                    binding.sheet2.recordTextview3.setTextColorRes(R.color.black_32)
                                    binding.sheet2.recordTextview3.visibility = View.VISIBLE
                                    binding.sheet2.recordTextview3.text = "촉감 $touch"
                                }
                            }
                        }

                    }

                    if (startDate != null) {
                        if (date < startDate || endDate != null) {
                            startDate = date
                            endDate = null
                        } else if (date != startDate) {
                            endDate = date
                        }
                    } else {
                        startDate = date
                    }
                    // binding.exTwoCalendar.notifyCalendarChanged()


                    if (date.isAfter(today)) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    else {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        binding.sheet2.dateTextview.text =
                            DateTimeFormatter.ofPattern("MM월 dd일 EEEE")
                                .withLocale(Locale.forLanguageTag("ko")).format(
                                    date
                                )
                        // bottom sheet update
                        // 월경 날짜 확인하기
                        var valueforBreak: Int = 0
                        for (i in 0..lastData.size - 1) {
                            if (valueforBreak > 0) break
                            for (j in 0..lastData[i].size - 1) {
                                if (valueforBreak > 0) break

                                // 월경 중일 경우
                                if (lastData[i][j].split("-")[0].toInt() == date.year
                                    && lastData[i][j].split("-")[1].toInt() == date.monthValue
                                    && lastData[i][j].split("-")[2].toInt() == date.dayOfMonth
                                ) {
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i][0])


                                    binding.sheet2.dayCountTextview.text = "월경 예상 ${j + 1}일째"
                                    binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_yellow)

                                    for (i in 0..recordedDate.size - 1) {
                                        for (j in 0..recordedDate[i].size - 1) {
                                            if (recordedDate[i][j].contains(day.date.toString()) == true) {
                                                binding.sheet2.dayCountTextview.text = "월경 ${j + 1}일째"

                                                binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_red)
                                            }
                                        }
                                    }

                                    valueforBreak += 1
                                    break
                                }
                                // 이번 년도, 다음 달이 다음 주기인 경우
                                else if (lastData[i][j].split("-")[0].toInt() == date.year
                                    && lastData[i][j].split("-")[1].toInt() == date.monthValue + 1
                                ) {
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i][0])

                                    // 남은 일수
                                    var todayDateString =
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                                    var dayminus =
                                        (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                            todayDateString
                                        ).time) / (24 * 60 * 60 * 1000)

                                    binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"
                                    binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_green)

                                    valueforBreak += 1
                                    break
                                }
                                // 이번 년도, 이번 달, 이후 날짜가 주기일 경우
                                else if (lastData[i][j].split("-")[0].toInt() == date.year
                                    && lastData[i][j].split("-")[1].toInt() == date.monthValue
                                    && lastData[i][j].split("-")[2].toInt() > date.dayOfMonth
                                ) {
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i][0])

                                    // 남은 일수
                                    var todayDateString =
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                                    var dayminus =
                                        (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                            todayDateString
                                        ).time) / (24 * 60 * 60 * 1000)

                                    binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"
                                    binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_green)

                                    valueforBreak += 1
                                    break
                                }
                                // 내년이 주기인 경우
                                else if (lastData[i][j].split("-")[0].toInt() == date.year + 1) {

                                    // 남은 일수
                                    var expectLocaldDateformat: LocalDate =
                                        LocalDate.parse(lastData[i + 1][0])
                                    var todayDateString =
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                                    var dayminus =
                                        (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                            todayDateString
                                        ).time) / (24 * 60 * 60 * 1000)

                                    binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"
                                    binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_green)

                                    valueforBreak += 1
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.exTwoCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding2.exTwoDayText

                val checkLine = container.binding2.lineForCheck
                val addedDot = container.binding2.lineForAdded
                checkLine.bringToFront()
                checkLine.setBackgroundResource(R.drawable.line_for_cycle_middle)
                checkLine.visibility = View.INVISIBLE
                addedDot.visibility = View.INVISIBLE

                val docRef =
                    db.collection("${auth.currentUser?.email}").document("Record")
                // 기록된 것이 있을 경우 점찍기
                val date = day.date
                val docRef1 = docRef.collection("$date").document("Menstruation")
                val docRef3 = docRef.collection("$date").document("Discharge")
                val docRef2 = docRef.collection("$date").document("Symptom")


                // 한 날짜만 선택되도록 설정
                if (selectedDate != today) {
                    textView.background = null
                }
                else {
                    textView.background = null
                }

                if (container.day.date.isAfter(today)) {
                    container.view.isEnabled = false
                }
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.view.visibility = View.VISIBLE

                    db.collection("${auth.currentUser?.email}").document("Record").get()
                        .addOnSuccessListener { document ->
                            docRef.collection("$date").document("Symptom").get()
                                .addOnSuccessListener { document ->
                                    docRef2.get().addOnSuccessListener { document ->
                                        var isEnabled = document.get("isEnabled")
                                        if (isEnabled != true) {
                                            docRef1.get().addOnSuccessListener { document ->
                                                if (document.get("isEnabled") != true) {
                                                    docRef3.get().addOnSuccessListener { document ->
                                                        if (document.get("isEnabled") != true) {
                                                            addedDot.visibility = View.INVISIBLE
                                                        } else {
                                                            addedDot.visibility = View.VISIBLE
                                                        }
                                                    }
                                                } else {
                                                    addedDot.visibility = View.VISIBLE
                                                }
                                            }
                                        } else {
                                            addedDot.visibility = View.VISIBLE
                                        }
                                    }
                                }
                        }
                    if (selectedDate == day.date) {
                        textView.setTextColorRes(R.color.black)
                        textView.setBackgroundResource(R.drawable.circle_calendar_today)
                    }
                    if (today == day.date) {
                        textView.setTextColorRes(R.color.black)
                        textView.setBackgroundResource(R.drawable.circle_calendar_today)
                    }
                    when {
                        // 오늘 날짜
                        day.date == today -> {
                            textView.setTextColorRes(R.color.black)
                        }

                        // 이전 날짜
                        day.date.isBefore(today) -> {
                            textView.setTextColorRes(R.color.black)
                        }
                        // 이후 날짜
                        else -> {
                            textView.setTextColorRes(R.color.gray_a7)
                            textView.background = null
                            textView.isEnabled = false
                        }
                    }
                    for (i in 0..lastData.size - 1) {
                        val startDate = dateformat.parse(lastData[i].first()).toInstant().atZone(
                            ZoneId.systemDefault()
                        ).toLocalDate()
                        val endDate = dateformat.parse(lastData[i].last()).toInstant().atZone(
                            ZoneId.systemDefault()
                        ).toLocalDate()

                        if (startDate == day.date) {
                            if (startDate.isAfter(today)) {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start_after_today)

                                checkLine.visibility = View.VISIBLE
                            } else {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start)

                                checkLine.visibility = View.VISIBLE
                            }
                        }
                        if (startDate != null && endDate != null && (day.date > startDate && day.date < endDate)) {
                            checkLine.visibility = View.VISIBLE
                            if (startDate.isAfter(today)) {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start_after_today)
                            } else {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start)
                            }
                        }
                        if (endDate == day.date) {
                            if (startDate.isAfter(today)) {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start_after_today)
                                checkLine.visibility = View.VISIBLE
                            } else {
                                checkLine.setBackgroundResource(R.drawable.line_for_cycle_start)
                                checkLine.visibility = View.VISIBLE
                            }
                        }

                    }
                    textView.text = day.day.toString()

                }
                else {
                    container.view.visibility = View.INVISIBLE
                    container.view.isEnabled = false
                    textView.background = null
                }
            }
        }

        binding.exTwoCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val display = requireActivity().windowManager.defaultDisplay
                val size = Point()
                display.getRealSize(size)

                if (month.yearMonth.atDay(1).dayOfWeek.toString() == "SUNDAY") {
                    xValue = 30
                } else if (month.yearMonth.atDay(1).dayOfWeek.toString() == "MONDAY") {
                    xValue = size.x / 7 + 20
                } else if (month.yearMonth.atDay(1).dayOfWeek.toString() == "TUESDAY") {
                    xValue = size.x / 7 * 2 + 20
                } else if (month.yearMonth.atDay(1).dayOfWeek.toString() == "WEDNESDAY") {
                    xValue = size.x / 7 * 3 + 10
                } else if (month.yearMonth.atDay(1).dayOfWeek.toString() == "THURSDAY") {
                    xValue = size.x / 7 * 4 + 10
                } else if (month.yearMonth.atDay(1).dayOfWeek.toString() == "FRIDAY") {
                    xValue = size.x - size.x / 7 * 2 - 10
                } else {
                    xValue = size.x - size.x / 7 - 10
                }
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(xValue, 0, 0, 0)
                container.textView.layoutParams = layoutParams

                // 헤더 텍스트 색상 번경
                if (month.year == today.year && month.month > today.monthValue) {
                    container.textView.setTextColorRes(R.color.gray_c5)
                } else if (month.year > today.year) {
                    container.textView.setTextColorRes(R.color.gray_c5)
                } else {
                    container.textView.setTextColorRes(R.color.black)
                }


                if (month.yearMonth.month.toString() == "JANUARY") {
                    container.textView.text = "1월"
                }
                if (month.yearMonth.month.toString() == "FEBRUARY") {
                    container.textView.text = "2월"
                }
                if (month.yearMonth.month.toString() == "MARCH") {
                    container.textView.text = "3월"
                }
                if (month.yearMonth.month.toString() == "APRIL") {
                    container.textView.text = "4월"
                }
                if (month.yearMonth.month.toString() == "MAY") {
                    container.textView.text = "5월"
                }
                if (month.yearMonth.month.toString() == "JUNE") {
                    container.textView.text = "6월"
                }
                if (month.yearMonth.month.toString() == "JULY") {
                    container.textView.text = "7월"
                }
                if (month.yearMonth.month.toString() == "AUGUST") {
                    container.textView.text = "8월"
                }
                if (month.yearMonth.month.toString() == "SEPTEMBER") {
                    container.textView.text = "9월"
                }
                if (month.yearMonth.month.toString() == "OCTOBER") {
                    container.textView.text = "10월"
                }
                if (month.yearMonth.month.toString() == "NOVEMBER") {
                    container.textView.text = "11월"
                }
                if (month.yearMonth.month.toString() == "DECEMBER") {
                    container.textView.text = "12월"
                }

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        lastData.clear()
        update()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isInDateBetween(
        inDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (inDate.yearMonth == startDate.yearMonth) return true
        val firstDateInThisMonth = inDate.plusMonths(1).yearMonth.atDay(1)
        return firstDateInThisMonth >= startDate && firstDateInThisMonth <= endDate && startDate != firstDateInThisMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isOutDateBetween(
        outDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (outDate.yearMonth == endDate.yearMonth) return true
        val lastDateInThisMonth = outDate.minusMonths(1).yearMonth.atEndOfMonth()
        return lastDateInThisMonth >= startDate && lastDateInThisMonth <= endDate && endDate != lastDateInThisMonth
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun update() {
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


                db.collection("${auth.currentUser?.email}").document("Record").get()
                    .addOnSuccessListener { document ->
                        docRef.get().addOnSuccessListener { document ->
                            var cnt = 0
                            var arrnum = 0
                            if (document.get("arr_num") != null)
                                arrnum = "${document.get("arr_num")}".toInt()

                            Log.e("arrnum : ", "$arrnum")

                            if (document.get("null_cnt") == null) {
                                val data = hashMapOf(
                                    "null_cnt" to 0
                                )
                                db.collection("${auth.currentUser?.email}").document("Record").set(data, SetOptions.merge())
                            } else {
                                cnt = "${document.get("null_cnt")}".toInt() //null_cnt 값 cnt에 넣어줌
                            }
                            db.collection("${auth.currentUser?.email}").document("Record").collection("$date").document("Menstruation").get()
                                .addOnSuccessListener { document ->
                                    if (document.get("isEnabled") == null && document.get("isChecked") == null) {
                                        val data = hashMapOf(
                                            "null_cnt" to cnt + 1
                                        )
                                        docRef.set(data, SetOptions.merge())
                                    }
                                    val data = hashMapOf(
                                        "isChecked" to true
                                    )
                                    db.collection("${auth.currentUser?.email}").document("Record").collection("$date").document("Menstruation")
                                        .set(data, SetOptions.merge())
                                }

                            recordedDate.clear()
                            if (document.get("null_cnt") != null) {
                                val null_cnt = "${document.get("null_cnt")}".toInt()



                                Log.e("null_cnt 결과 : ", "$null_cnt")



                                for (i in 1..arrnum) {
                                    if (document.get("duration_arr$i") != null) {
                                        val du_arr: MutableList<String> =
                                            document.get("duration_arr$i") as MutableList<String> //날짜 저장되어 있는 배열 불러오기
                                        Log.e("du_arr 결과 : ", "$du_arr")
                                        recordedDate.add(du_arr)
                                        lastData.add(du_arr)
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

                                // startDate, endDate 선언부 (추가)
                                // string -> localDate
                                if (recordedDate.size > 0) {
                                    startDate = dateformat.parse(recordedDate[recordedDate.size-1][0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    endDate =
                                        dateformat.parse(recordedDate[recordedDate.size-1].last()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                }
                                else {
                                    for (i in 0..lastData.size-1) {
                                        if (lastData[i][0].split("-")[0].toInt() == today.year
                                            && lastData[i][0].split("-")[1].toInt() == today.monthValue) {
                                            startDate =
                                                dateformat.parse(lastData[i][0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                            endDate =
                                                dateformat.parse(lastData[i][lastData[i].size-1]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                            break
                                        }
                                    }

                                }

                                // binding.exTwoCalendar.visibility = View.VISIBLE
                                // 연산이 다 끝났을 때 현 날짜로 돌아옴
                                loading.dismiss()
                                binding.exTwoCalendar.smoothScrollToMonth(currentMonth)


                                val docRef = db.collection("${auth.currentUser?.email}").document("Record")
                                docRef.get().addOnSuccessListener { document ->
                                    val date = document.get("clicked_date")
                                    //
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
                                            }
                                            //Log.e("n 테스트 : ", "$n")
                                            Log.e("주기월경량 테스트 : ", "$weekTotalAmount")
                                        }
                                    }
                                    //
                                    val docRef2 = docRef.collection("$date").document("Menstruation")
                                    docRef2.get().addOnSuccessListener { document ->
                                        if (document.get("isEnabled") == null) {

                                        } else {
                                            val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                                            val endTime =
                                                document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                                            var todayTotalAmount: Int = 0
                                            for (i in 1..num.toString().toInt()) {
                                                /*todayTotalAmount += document.get("men_list$i.amount").toString()
                                                    .toInt()*/

                                                todayTotalAmount = "${document.get("totalAmount")}".toInt()
                                            }
                                            // 오늘 월경량 todayTotalAmount, 주기 누적 월경량 여기서 처리

                                        }
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
                                        binding.sheet2.chip.setBackgroundResource(R.drawable.record_chip_yellow)
                                        changeBackValue += 1
                                        isYellow = true
                                        break
                                    }
                                }
                            }
                            if (changeBackValue == 0) {
                                binding.sheet2.chip.setBackgroundResource(R.drawable.record_chip_green)
                            }
                            for (i in 0..recordedDate.size - 1) {
                                for (j in 0..recordedDate[i].size - 1) {
                                    if (recordedDate[i][j].contains(date) == true) {
                                        binding.sheet2.chip.setBackgroundResource(R.drawable.record_chip_red)
                                    }
                                }
                            }

                            var valueforBreak: Int = 0
                            for (i in 0..lastData.size - 1) {
                                if (valueforBreak > 0) break
                                for (j in 0..lastData[i].size - 1) {
                                    if (valueforBreak > 0) break

                                    // 월경 중일 경우
                                    if (lastData[i][j].split("-")[0].toInt() == today.year
                                        && lastData[i][j].split("-")[1].toInt() == today.monthValue
                                        && lastData[i][j].split("-")[2].toInt() == today.dayOfMonth
                                    ) {
                                        var expectLocaldDateformat: LocalDate =
                                            LocalDate.parse(lastData[i][0])

                                        var todayDateString =
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)


                                        binding.sheet2.dayCountTextview.text = "월경 예상 ${j + 1}일째"
                                        binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_yellow)
                                        if (isYellow) {
                                        }
                                        for (i in 0..recordedDate.size - 1) {
                                            for (j in 0..recordedDate[i].size - 1) {
                                                if (recordedDate[i][j].contains(date) == true) {
                                                    binding.sheet2.dayCountTextview.text = "월경 ${j + 1}일째"
                                                    binding.sheet2.chip.setBackgroundResource(R.drawable.record_month_chip_red)
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


                                        binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"
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

                                        binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"

                                        valueforBreak += 1
                                        break

                                    }
                                    // 내년이 주기인 경우
                                    else if (lastData[i][j].split("-")[0].toInt() == today.year + 1) {

                                        // 남은 일수
                                        var expectLocaldDateformat: LocalDate =
                                            LocalDate.parse(lastData[i + 1][0])
                                        var todayDateString =
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd").format(today)
                                        var dayminus =
                                            (dateformat.parse(lastData[i][0]).time - dateformat.parse(
                                                todayDateString
                                            ).time) / (24 * 60 * 60 * 1000)

                                        binding.sheet2.dayCountTextview.text = "월경 ${dayminus}일 전"
                                        valueforBreak += 1
                                        break
                                    }
                                }
                            }
                        }
                    }
                // startDate, endDate 선언부 (수정)
            }
    }

}