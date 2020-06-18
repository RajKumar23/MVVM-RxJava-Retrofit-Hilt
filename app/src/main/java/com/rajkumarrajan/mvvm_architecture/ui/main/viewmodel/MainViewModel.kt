package com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.data.repository.MainRepository
import com.rajkumarrajan.mvvm_architecture.utils.Resource
import com.rajkumarrajan.mvvm_architecture.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository,
    private val session: SessionManager
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<User>>>()
    private val compositeDisposable = CompositeDisposable()

    fun fetchUsers() {
        if (session.isNetworkConnection()) {
            users.postValue(Resource.loading(null))
            compositeDisposable.add(
                mainRepository.getUsers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ userList ->
                        users.postValue(Resource.success(userList))
                    }, {
                        users.postValue(Resource.error("Something Went Wrong", null))
                    })
            )
        } else {
            users.postValue(Resource.noInternet("No Internet connection", null))
        }

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return users
    }
}