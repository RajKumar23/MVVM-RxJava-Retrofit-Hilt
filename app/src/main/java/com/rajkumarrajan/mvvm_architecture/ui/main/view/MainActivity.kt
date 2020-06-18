package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.ui.main.adapter.MainAdapter
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.MainViewModel
import com.rajkumarrajan.mvvm_architecture.utils.SessionManager
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var adapter: MainAdapter

    @Inject
    lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        setupAPICall()
    }

    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(session)
        renderList(listOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupAPICall() {
        mainViewModel.getUsers().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    session.dismissLoader()
                    it.data?.let { usersData -> renderList(usersData) }
                    recyclerView.visibility = View.VISIBLE
                    session.shortToast("Response Success")
                }
                Status.LOADING -> {
                    session.displayLoader(resources.getString(R.string.loading))
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    session.dismissLoader()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Status.INTERNET -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        mainViewModel.fetchUsers()
    }

    private fun renderList(users: List<User>) {
        adapter.apply {
            adapter.addData(users, session.context)
            adapter.notifyDataSetChanged()
        }
    }

}
