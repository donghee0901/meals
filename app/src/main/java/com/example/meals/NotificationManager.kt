package com.example.meals

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object NotificationManager {
    const val NOTIFICATION_CHANNEL_ID = "10001"
    fun MakeNotification(context : Context, meals : MealsData, intent: Intent){
        val localHour : Int = LocalDateTime.now().hour
        if(localHour <= 7 || localHour >= 19){
            NotificationSomething(context, "아침", meals.breakfast, intent)
        }
        else if(localHour in 8..12){
            NotificationSomething(context, "점심", meals.lunch, intent)
        }
        else if(localHour in 13..18){
            NotificationSomething(context, "저녁", meals.dinner, intent)
        }
    }
    fun NotificationSomething(context: Context, time: String, data: String, intent : Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
            .setContentTitle(time + Calendar.getInstance().time.toString())
            .setContentText(data) // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
            .setStyle(NotificationCompat.BigTextStyle().bigText("${data}\nrequest code : ${intent.extras!!["requestcode"].toString()}\n${(if(intent.extras!!["requestcode"] != 6) LocalDate.now() else LocalDate.now().plusDays(1)).toString().replace("-","")}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 MainActivity로 이동하도록 설정
            .setAutoCancel(true)

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            val channelName: CharSequence = "노티페케이션 채널"
            val description = "오레오 이상을 위한 것임"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        } else builder.setSmallIcon(R.mipmap.ic_launcher) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        notificationManager.notify(1234, builder.build()) // 고유숫자로 노티피케이션 동작시킴
    }
}