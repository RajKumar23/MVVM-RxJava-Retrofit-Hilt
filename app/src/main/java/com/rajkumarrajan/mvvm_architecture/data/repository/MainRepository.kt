package com.rajkumarrajan.mvvm_architecture.data.repository

import com.rajkumarrajan.mvvm_architecture.data.api.ApiHelper
import com.rajkumarrajan.mvvm_architecture.data.model.User
import io.reactivex.Single
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun getUsers(): Single<List<User>> {
        return apiHelper.getUsersSecond()
    }

}