package com.rajkumarrajan.mvvm_architecture.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.ui.main.view.MainActivity
import com.rajkumarrajan.mvvm_architecture.utils.SessionManager
import kotlinx.android.synthetic.main.item_layout.view.*
import javax.inject.Inject

class MainAdapter @Inject constructor(
    val session: SessionManager
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    private var users: ArrayList<User> = ArrayList()
    private lateinit var context: Context

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User, context: Context, session: SessionManager) {
            itemView.textViewUserName.text = user.name
            itemView.textViewUserEmail.text = user.email
            Glide.with(itemView.imageViewAvatar.context)
                .load(user.avatar)
                .into(itemView.imageViewAvatar)
            itemView.imageViewAvatar.setOnClickListener {
                context.startActivity(Intent(context, MainActivity::class.java))
                session.shortToast(context.resources.getString(R.string.loading))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(users[position], context, session)

    fun addData(users: List<User>, context: Context) {
        this@MainAdapter.context = context
        this.users.apply {
            clear()
            addAll(users)
        }
    }

}