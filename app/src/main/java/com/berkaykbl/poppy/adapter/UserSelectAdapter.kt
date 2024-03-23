package com.berkaykbl.poppy.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.activity.ChatActivity
import com.berkaykbl.poppy.activity.CreateNewGroupActivity
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserSelectAdapter(private val context: Context, private val userlist: ArrayList<User>) :
    RecyclerView.Adapter<UserSelectAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.username)
        val radioButton: RadioButton = view.findViewById(R.id.radioButton)
        val imgUser: CircleImageView = view.findViewById(R.id.userAvatar)
        val userLayout: LinearLayout = view.findViewById(R.id.userLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("qwe", "user.userId")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_select, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userlist[position]
        Log.d("qwe", user.userId)
        holder.txtUsername.text = user.username
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgUser)
        holder.userLayout.setOnClickListener {
            holder.radioButton.isChecked = !holder.radioButton.isChecked
        }

        holder.radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                CreateNewGroupActivity().addSelectedUser(user.userId, user)
            } else {
                CreateNewGroupActivity().removeSelectedUser(user.userId)
            }
        }

    }
}