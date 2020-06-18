package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.User
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    fun getUsers(): Single<List<User>>

    @GET("users")
    fun getUsersSecond(): Single<List<User>>
}