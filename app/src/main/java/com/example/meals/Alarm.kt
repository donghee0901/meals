package com.example.meals

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.meals.NotificationManager.MakeNotification
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class Alarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MainActivity.setAlarm(context)
            return;
        }
        val retrofit = RetrofitClient.getInstance() //RetrofitClient에서 Instance를 가져옴 (이미 만들어져있다면 기존의 Instance를 가져오고, 없다면 새로 만든다)
        val service = retrofit.create(MealsService::class.java) // Retrofit Instance를 MealsService를 통해 사용할 수 있게 함
        lateinit var getMealsDay : String
        if(intent.extras!!["requestcode"] != 6){
            getMealsDay = MainActivity.parseDate(LocalDate.now())
        }
        else{
            getMealsDay = MainActivity.parseDate(LocalDate.now().plusDays(1))
        }
        Log.e("TAG", "request code : ${intent.extras!!["requestcode"].toString()} getMealsDay = ${getMealsDay}")
        service.getMeals(getMealsDay).enqueue(object : Callback<MealsData> { //MealsService에 있던 getMeals함수를 실행하여 결과를 받아왔을때 콜백을 통해 아래 함수 실행
            override fun onFailure(call: Call<MealsData>, t: Throwable) {
                Log.e("TAG", "onFailure: requestCode=${intent.extras!!["requestcode"]}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<MealsData>, response: Response<MealsData>) {
                Log.d("Log", response.body().toString())
                if(response.code()==200) { // 성공 200, 서버에 없으면 404, 서버가 오류 500, 내가 잘못주면(서버에서 막을때) 400
                    response.body()?.let {
                        MakeNotification(context, it, intent)
                    }
                }
            }
        })

        Toast.makeText(context, "알람~!!", Toast.LENGTH_SHORT).show() // AVD 확인용
        Log.e("Alarm", "알람입니다.") // 로그 확인용
    }


}

