package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.User
import io.reactivex.Single

interface ApiHelper {
    fun getUsers(): Single<List<User>>

    fun getUsersSecond(): Single<List<User>>
}