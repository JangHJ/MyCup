# MyCup:sparkles:

```
🗓️ 생리 기록 및 생리컵에 대한 정보를 찾아볼 수 있는 어플리케이션
```

![dbeaver_20230223.PNG](./app/src/main/res/mipmap-xxxhdpi/ic_mycup.png)

- **개발 기간** : 2021.06 ~ 2021.08
- **개발 인원** : 4명
    - UI/UX 디자이너 1명
    - 프론트 개발자 2명
    - 백엔드 개발자 1명 (본인)
- **IDE/Language :** XCode - Swift, Firebase
- **협업툴** : GitHub, SourceTree, **Notion** ([TEAM 유어스](https://www.notion.so/TEAM-017b5465b5204b2590d2351d1a408a54?pvs=21))
- **Github** : https://github.com/JangHJ/MyCup

<br>

### 🖥️ 구현한 기능 및 코드 설명

```
❓ 구현한 기능의 자세한 코드 내용과 그에 대한 설명이 첨부되어 있습니다
```

- **Google 로그인 결과 Firebase로 인증**
[https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/init/LoginActivity.kt]
```kotlin
private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d("Google Login", "signInWithCredential:success")
                    if (auth.currentUser != null) { // 로그인 성공했다면
                        val docRef1 = db.collection("${auth.currentUser?.email}").document("Init")
                        docRef1.get().addOnSuccessListener { document ->
                            if (document.get("isJoined") == true) { // 로그아웃했다가 다시 로그인하는 경우
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            else{
                                // 약관동의 하지 않은 경우 InitActivity로 (회원가입)
                                Log.e("테스트테스트2 : ", "${Firebase.auth.currentUser?.email}")
                                startActivity(Intent(this, InitActivity::class.java))
                            }
                            //finish()
                        }
                    }
                } else {
                    Log.w("Google Login", "signInWithCredential:failure", it.exception)
                }
            }
}
```

<br>
  
- **Firebase Firestore 값 저장/불러오기**

  - 초기 설정
    [https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/init/InitActivity2.kt]

    ```kotlin
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
    ```
    <br>
     
  - 월경 기록(분비물, 월경, 증상) 저장
      [https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/record/DischargeSettingActivity.kt]
      ```kotlin
          //분비물 저장
          binding.save.setOnClickListener {
            val docRef = db.collection("${auth.currentUser?.email}").document("Record")
            docRef.get().addOnSuccessListener { document ->
                val date = document.get("clicked_date")
                var nang :String ?= null
                var touch :String ?= null
        
                //냉 분비량 추가
                if (binding.veryLittle.isChecked == true) {
                    nang = "${binding.veryLittle.text}" // 아주 적음
                } else if (binding.little.isChecked == true) {
                    nang = "${binding.little.text}" // 적음
                } else if (binding.normal.isChecked == true) {
                    nang = "${binding.normal.text}" // 보통
                } else if (binding.much.isChecked == true) {
                    nang = "${binding.much.text}" // 많음
                } else if (binding.veryMuch.isChecked == true) {
                    nang = "${binding.veryMuch.text}" // 아주많음
                } else {
                    Log.e("오류!", "냉분비량 칩선택이 실패하였습니다.")
                }
        
                //촉감 추가
                if (binding.sticky.isChecked == true) {
                    touch = "${binding.sticky.text}" // 끈적하고 점도가 높다
                } else if (binding.creamy.isChecked == true) {
                    touch = "${binding.creamy.text}" // 크림 같다
                } else if (binding.eggWhite.isChecked == true) {
                    touch = "${binding.eggWhite.text}" // 계란 흰자같다
                } else if (binding.watery.isChecked == true) {
                    touch = "${binding.watery.text}" // 묽다
                } else {
                    Log.e("오류!", "촉감 칩선택이 실패하였습니다.")
                }
        
                if(nang == null && touch == null)
                {
                    docRef.collection("$date")
                        .document("Discharge").delete()
                        .addOnSuccessListener {
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot successfully deleted!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error deleting document",
                                e
                            )
                        }
                }else{
                    val data = hashMapOf(
                        "nang" to nang,
                        "touch" to touch,
                        "isEnabled" to true
                    )
                    docRef.collection("$date").document("Discharge").set(data, SetOptions.merge())
                }
            }
            Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
      ```

    [https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/record/MenstruationSettingActivity.kt]
    
    ```kotlin
    //월경 상세정보 저장
    binding.save.setOnClickListener {
        val startTime = binding.startTimeToUseSupply.text.toString()
        val endTime = binding.endTimeToUseSupply.text.toString()
        val supply = binding.supplySpinner.text.toString()
        var type: String = binding.supplySpinner.text.toString()
        var amount = 0
    
        if (startTime == "" || endTime == "" || supply == "" || binding.expectaionValueTextView.text == "월경량 - ml") {
            Toast.makeText(applicationContext, "시간, 월경용품을 모두 기록해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
         if (type == "생리대") {
            Log.e("정혈 색상", "${binding.lightRed.text.toString()}")
            type = binding.typePickerPad.text.toString()
        } else if (type == "탐폰") {
            Log.e("정혈 색상", "${binding.lightPink.text.toString()}")
            type = binding.typePickerTampon.text.toString()
        } else if (type == "월경컵") {
            Log.e("정혈 색상", "${binding.darkPurple.text.toString()}")
            type = binding.typeInputEdittext.text.toString()
        } else {
            Log.e("오류!", "칩선택이 실패하였습니다.")
        }
    
        docRef.get().addOnSuccessListener { document ->
            val date = "${document.get("clicked_date")}" //선택된 날짜 저장
            val date2 = "${document.get("Today")}"
    
            Log.e("date : ", "$date")
    
            var color: String = ""
    
            if (binding.lightRed.isChecked == true) {
                Log.e("정혈 색상", "${binding.lightRed.text}")
                color = "${binding.lightRed.text}"
            } else if (binding.lightPink.isChecked == true) {
                Log.e("정혈 색상", "${binding.lightPink.text}")
                color = "${binding.lightPink.text}"
            } else if (binding.darkPurple.isChecked == true) {
                Log.e("정혈 색상", "${binding.darkPurple.text}")
                color = "${binding.darkPurple.text}"
            } else if (binding.darkBrown.isChecked == true) {
                Log.e("정혈 색상", "${binding.darkBrown.text}")
                color = "${binding.darkBrown.text}"
            } else {
                Log.e("오류!", "칩선택이 실패하였습니다.")
            }
    
            if (supply == "생리대") {
                when (type) {
                    "라이너" -> amount = 1
                    "소형" -> amount = 3
                    "중형" -> amount = 5
                    "대형" -> amount = 7
                    "오버나이트" -> amount = 10
                }
            } else if (supply == "탐폰") {
                when (type) {
                    "레귤러" -> amount = 4
                    "슈퍼" -> amount = 8
                }
            } else if (supply == "월경컵") {
                amount = "${binding.typeInputEdittext.text}".toInt()
            }
            Log.e("선택된 날짜 : ", "$date")
    
    
            val docRef2 = docRef.collection("$date").document("Menstruation")
            var null_cnt = 0
            var arrnum = 1
            var cnt2 = 1
            var arr_end = false
            if(document.get("arr_num") != null)
                arrnum = "${document.get("arr_num")}".toInt()
            else{
                val data = hashMapOf(
                    "arr_num" to 1
                )
                docRef.set(data, SetOptions.merge())
            }
    
            if(document.get("null_cnt") != null)
                null_cnt = "${document.get("null_cnt")}".toInt()
    
    
            if(document.get("arr_end") != null)
                arr_end = document.get("arr_end") as Boolean
    
            if (document.get("am_cnt") == null) {
                val data = hashMapOf(
                    "am_cnt" to 1
                )
                docRef.set(data, SetOptions.merge())
            } else {
                cnt2 = "${document.get("am_cnt")}".toInt() //am_cnt 값 cnt에 넣어줌
            }
    
            docRef2.get().addOnSuccessListener { document ->  
                var totalamount = 0
                if(document.get("totalAmount") != null) {
                    totalamount = "${document.get("totalAmount")}".toInt()
                    val data = hashMapOf(
                        "am_cnt" to cnt2 + 1
                    )
                }
    
                if (document.get("list_num") == null) {
                    val data = hashMapOf(
                        "list_num" to 1,
                        "men_list1" to mapOf(
                            "startTime" to startTime,
                            "endTime" to endTime,
                            "supply" to supply,
                            "type" to type,
                            "color" to color,
                            "amount" to amount
                        ),
                        "isEnabled" to true,
                        "totalAmount" to amount
                    )
                    docRef.collection("$date").document("Menstruation")
                        .set(data, SetOptions.merge())
    
                    if(null_cnt < 4)
                    {
                        val data2 = hashMapOf(
                            "null_cnt" to null_cnt-1
                        )
                        docRef.set(data2, SetOptions.merge())
                        docRef.update("duration_arr$arrnum", FieldValue.arrayUnion("$date"))
                        val data3 = hashMapOf(
                            "amount_arr$arrnum" to mapOf(
                                "$date" to totalamount + amount
                            )
                        )
                        docRef.set(data3, SetOptions.merge())
    
                    }
                    else{ //공백이 3일이상 넘어갔다는 의미
                        arrnum += 1
                        val data = hashMapOf(
                            "arr_num" to arrnum,
                            "null_cnt" to 0,
                            "arr_end" to true // list1 배열 삽입과정이 끝남 -> 2로 넘어감
                        )
                        docRef.set(data, SetOptions.merge())
                        docRef.update("duration_arr$arrnum", FieldValue.arrayUnion("$date"))
    
                        val data3 = hashMapOf(
                            "amount_arr$arrnum" to mapOf(
                                "$date" to totalamount + amount
                            )
                        )
                        docRef.set(data3, SetOptions.merge())
    
                    }
                } else {
                    var temp = "${document.get("list_num")}"
                    val num = temp.toInt() + 1
    
                    val data = hashMapOf(
                        "men_list$num" to mapOf(
                            "startTime" to startTime,
                            "endTime" to endTime,
                            "supply" to supply,
                            "type" to type,
                            "color" to color,
                            "amount" to amount
                        ),
                        "list_num" to num,
                        "isEnabled" to true,
                        "totalAmount" to totalamount + amount
                    )
                    docRef.collection("$date").document("Menstruation")
                        .set(data, SetOptions.merge())
    
                    val data3 = hashMapOf(
                        "amount_arr$arrnum" to mapOf(
                            "$date" to totalamount + amount
                        )
                    )
                    docRef.set(data3, SetOptions.merge())
                }
                //var temp = "${document.get("list_num")}"
                //var map: Map<String, Any> = document.get("men_list".plus(temp)) as Map<String, Any>
                //val num = temp.toInt() + 1
                }
        }
            Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    ```
    
    [https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/record/SymptomSettingActivity.kt]
    
    ```kotlin
    //증상 저장
    binding.save.setOnClickListener {
        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            var pain = arrayOfNulls<Any>(6)
            var bowel: String? = null
            var etc = arrayOfNulls<Any>(5)
            var cnt_p = 0
            var cnt_b = 0
            var cnt_e = 0
    
            //통증 추가
            if (binding.painPelvic.isChecked == true) {
                pain[0] = "${binding.painPelvic.text}" // 골반 통증
                cnt_p++
            }
    
            if (binding.painHeadache.isChecked == true) {
                pain[1] = "${binding.painHeadache.text}" // 두통
                cnt_p++
            }
    
            if (binding.painUrinaryTract.isChecked == true) {
                pain[2] = "${binding.painUrinaryTract.text}" // 배뇨통
                cnt_p++
            }
    
            if (binding.painOvulatory.isChecked == true) {
                pain[3] = "${binding.painOvulatory.text}" // 배란통
                cnt_p++
            }
    
            if (binding.painMenstrual.isChecked == true) {
                pain[4] = "${binding.painMenstrual.text}" // 월경통
                cnt_p++
            }
    
            if (binding.painBreast.isChecked == true) {
                pain[5] = "${binding.painBreast.text}" // 유방 통증
                cnt_p++
            }
    
            //배변 추가
            if (binding.bowelConstipation.isChecked == true) {
                bowel = "${binding.bowelConstipation.text}" // 변비
                cnt_b++
    
            } else if (binding.bowelDiarrhea.isChecked == true) {
                bowel = "${binding.bowelDiarrhea.text}" // 설사
                cnt_b++
            } else {
                Log.e("오류!", "배변 칩선택이 실패하였습니다.")
            }
    
            //기타 추가
            if (binding.emotion.isChecked == true) {
                etc[0] = "${binding.emotion.text}" // 감정기복
                cnt_e++
            }
            if (binding.sexual.isChecked == true) {
                etc[1] = "${binding.sexual.text}" // 성욕변화
                cnt_e++
            }
            if (binding.appetite.isChecked == true) {
                etc[2] = "${binding.appetite.text}" // 식욕변화
                cnt_e++
            }
            if (binding.acne.isChecked == true) {
                etc[3] = "${binding.acne.text}" // 여드름
                cnt_e++
            }
            if (binding.tiredness.isChecked == true) {
                etc[4] = "${binding.tiredness.text}" // 피로감
                cnt_e++
            }
    
            if (cnt_p == 0 && cnt_b == 0 && cnt_e == 0) {
                docRef.collection("$date")
                    .document("Symptom").delete()
                    .addOnSuccessListener {
                        Log.d(
                            ContentValues.TAG,
                            "DocumentSnapshot successfully deleted!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            ContentValues.TAG,
                            "Error deleting document",
                            e
                        )
                    }
            } else {
                val data = hashMapOf(
                    "pain" to listOf(pain[0], pain[1], pain[2], pain[3], pain[4], pain[5]),
                    "bowel" to bowel,
                    "etc" to listOf(etc[0], etc[1], etc[2], etc[3], etc[4]),
                    "isEnabled" to true
                )
                docRef.collection("$date")
                    .document("Symptom").set(data, SetOptions.merge())
            }
        }
    
        Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }
    ```

  <br>
  
- 관심컵 등록 및 삭제
  
  ```kotlin
  ```

  <br>
  
- 후기 등록 및 수정/삭제
  
  ```kotlin
  ```

  <br>
      
- **월경컵 옵션 검색**
  
```kotlin
```

<br>

- **탈퇴 처리**
[https://github.com/JangHJ/MyCup/blob/main/app/src/main/java/com/yours/mycup/init/LoginActivity.kt]
```kotlin
//user: FirebaseUser
if (user != null) { // 자동로그인
    val docRef1 = db.collection("${auth.currentUser?.email}").document("Init")
    docRef1.get().addOnSuccessListener { document ->
        if(document.get("secession") == true){
            Toast.makeText(this, "탈퇴 완료되었습니다", Toast.LENGTH_SHORT).show()
            db.collection("${auth.currentUser?.email}").document("Init").delete()
                .addOnSuccessListener { Log.d("Init 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("Init 폴더 삭제", "Error deleting document", e) }
            db.collection("${auth.currentUser?.email}").document("Mypage").delete()
                .addOnSuccessListener { Log.d("Mypage 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("Mypage 폴더 삭제", "Error deleting document", e) }
            db.collection("${auth.currentUser?.email}").document("Record").delete()
                .addOnSuccessListener { Log.d("Record 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("Record 폴더 삭제", "Error deleting document", e) }
            db.collection("${auth.currentUser?.email}").document("Review").delete()
                .addOnSuccessListener {
                    Log.d("Review 폴더 삭제", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener {
                    Log.e("탈퇴확인33", Firebase.auth.currentUser.toString())
                    Log.e("Review 폴더 삭제", "실패하였습니다")
                    Toast.makeText(this, "탈퇴되지 않았습니다", Toast.LENGTH_SHORT).show()
                }
            Firebase.auth.signOut()
        }
        else if(document.get("isJoined") == true){
            Toast.makeText(this, "로그인 중입니다", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
```

<br>

### 🖥️ 회고

```
😣 추가되면 좋을 것 같은 기능 + 아쉬운 점
```

- Firebase Firestore에서 데이터를 불러오는 속도가 확실히 느리다
- 사용자 경험 측면에서, 앱이 데이터를 느린 속도로 불러오기 때문에 사용자들에게 불편함을 줄 수 있을 것 같다.
- 각 컬렉션, 문서 간의 종속성이 전혀 유지되지 않아 변경이 일어날 때 데이터의 일관성을 유지하기 어려웠다.

<br>
<br>

