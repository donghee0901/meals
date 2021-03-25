package com.example.meals

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply { //logging에 HttpLoggingInterceptor()를 넣는 작업이 성공했을때 그 다음 작업 실행
        setLevel(HttpLoggingInterceptor.Level.BODY) //
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    @Volatile
    private var instance: Retrofit? = null //동기화

    fun getInstance(): Retrofit {
        synchronized(this) { //싱글톤 - 한번에 하나씩만 쓸 수 있다. (쓰지않으면 여러 쓰레드에서 동시에 접근할때 인스턴스가 여러개 만들어질 수 있음)
            if (instance == null) { //인스턴스가 없을때 새로 만드는 과정
            instance = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()) // Retrofit을 빌드할때 ConverterFactory, 즉 데이터를 변환할때 GsonConverterFactory를 사용하겠다는 말
                    .client(client)
                    .baseUrl("http://15.164.219.30") //baseUrl, API를 가져올 URL의 원본 (path와 query는 나중에 쓰므로 원본만 가져온다.)
                    .build() //Retrofit 빌드
            }
            return instance!! // 만들어지거나 기존에 있던 인스턴스 반환
        }
    }
}