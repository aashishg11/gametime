/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.aashishgodambe.gametime

import com.aashishgodambe.gametime.models.Schedules
import com.aashishgodambe.gametime.models.Teams
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://www.thesportsdb.com/api/v1/json/1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val loggingInterceptor = run {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.apply {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okHttpClient =  OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .readTimeout(5, TimeUnit.SECONDS)
    .connectTimeout(5, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()


interface SportsService {

    @GET("searchteams.php")
    suspend fun getTeams(@Query("t") team: String): Teams

    @GET("eventslast.php")
    suspend fun getSchedules(@Query("id") team: String): Schedules

    @GET("lookupteam.php")
    suspend fun getTeam(@Query("id") id: String): Teams
}

object SportsApi {
    val retrofitService : SportsService by lazy { retrofit.create(SportsService::class.java) }
}
