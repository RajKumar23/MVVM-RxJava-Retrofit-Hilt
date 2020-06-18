package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.User
import io.reactivex.Single
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) :
    ApiHelper {
    override fun getUsers(): Single<List<User>> {
        return apiService.getUsers()
    }

    override fun getUsersSecond(): Single<List<User>> {
        return apiService.getUsersSecond()
    }
}