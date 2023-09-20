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
     
- 월경 기록 저장
  
  ```kotlin
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

