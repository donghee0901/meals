package com.example.meals

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    private var alarmManager: AlarmManager? = null
    lateinit var retrofit : Retrofit
    lateinit var service : MealsService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, MainActivity::class.java)
        var selectDate = LocalDate.now()
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        retrofit = RetrofitClient.getInstance() //RetrofitClient에서 Instance를 가져옴 (이미 만들어져있다면 기존의 Instance를 가져오고, 없다면 새로 만든다)
        service = retrofit.create(MealsService::class.java) // Retrofit Instance를 MealsService를 통해 사용할 수 있게 함

        var todayMealsData = MealsData()
        getMeals(parseDate(selectDate), todayMealsData)
        setAlarm(this)


        next_day.setOnClickListener{
            selectDate = selectDate.plusDays(1)
            getMeals(parseDate(selectDate))
        }
        prev_day.setOnClickListener {
            selectDate = selectDate.minusDays(1)
            getMeals(parseDate(selectDate))
        }
        curr_day.setOnClickListener {
            selectDate = LocalDate.now()
            getMeals(parseDate(selectDate))
        }
        notification_button.setOnClickListener {
            intent.putExtra("requestcode", 7)
            NotificationManager.MakeNotification(this, todayMealsData, intent)
        }
    }

    companion object {
        fun parseDate(date: LocalDate): String {
            return date.toString().replace("-", "")
        }

        fun setAlarm(context: Context){
            AlarmCreator.register(context, 6, 20, 0)
            AlarmCreator.register(context, 7, 15, 1)
            AlarmCreator.register(context, 8, 15, 2)
            AlarmCreator.register(context, 12, 30, 3)
            AlarmCreator.register(context, 13, 30, 4)
            AlarmCreator.register(context, 18, 10, 5)
            AlarmCreator.register(context, 19, 10, 6)
        }
    }

    fun getMeals(selectDate : String, mealData : MealsData? = null){
        service.getMeals(selectDate).enqueue(object : Callback<MealsData> { //MealsService에 있던 getMeals함수를 실행하여 결과를 받아왔을때 콜백을 통해 아래 함수 실행
            override fun onFailure(call: Call<MealsData>, t: Throwable) {
                Toast.makeText(this@MainActivity, "급식을 불러오는데 실패했습니다. ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }

            override fun onResponse(call: Call<MealsData>, response: Response<MealsData>) {
                Log.d("Log", response.body().toString())

                if(response.code()==200){ // 성공 200, 서버에 없으면 404, 서버가 오류 500, 내가 잘못주면(서버에서 막을때) 400
                    response.body()?.let { meals ->
                        breakfast.text = meals.breakfast
                        lunch.text = meals.lunch
                        dinner.text = meals.dinner
                        date.text = meals.date

                        mealData?.breakfast = meals.breakfast
                        mealData?.lunch = meals.lunch
                        mealData?.dinner = meals.dinner
                        mealData?.date = meals.date
                    } ?: run{
                        date.text = "급식"
                        breakfast.text = "불러오기"
                        lunch.text = "실패"
                        dinner.text = "했어용"
                    }
                }
                else{
                    response.errorBody()?.string()?.let { Log.d("Log", it) }
                    date.text = "오류코드 : ${response.code()}"
                    breakfast.text = "오류가"
                    lunch.text = "발생"
                    dinner.text = "했어용"
                }
            }

        })
    }
}