package com.qurba.android.network

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.android.gms.common.util.CollectionUtils
import com.qurba.android.BuildConfig
import com.qurba.android.utils.Constants
import com.qurba.android.utils.QurbaApplication
import com.qurba.android.utils.SharedPreferencesManager
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var retrofit: Retrofit? = null
        private set

    // Create the Collector
    private val chuckerCollector = ChuckerCollector(
        context = QurbaApplication.getContext(),
        // Toggles visibility of the push notification
        showNotification = true,
        // Allows to customize the retention period of collected data
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )

    @get:Synchronized
    private val client: Retrofit?
        get() {
            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .protocols(CollectionUtils.listOf(Protocol.HTTP_1_1))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(
                    if (BuildConfig.DEBUG)
                        ChuckerInterceptor.Builder(QurbaApplication.getContext())
                            // The previously created Collector
                            .collector(chuckerCollector)
                            .build()
                    else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                )
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val request = chain.request()
                    val header: MutableMap<String, String> =
                        HashMap()
                    header["backend-version"] = "3"
                    header["language"] = SharedPreferencesManager.getInstance().language
                    header["platform"] = "android"
                    header["Authorization"] = "JWT " + SharedPreferencesManager.getInstance().token
                    header["app-version"] = BuildConfig.VERSION_NAME
                    val newRequest: Request = request.newBuilder()
                        .headers(header.toHeaders())
                        .build()
                    chain.proceed(newRequest)
                })
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .build()
            }
            return retrofit
        }

    val service: EndPoints
        get() = client!!.create(EndPoints::class.java)
}