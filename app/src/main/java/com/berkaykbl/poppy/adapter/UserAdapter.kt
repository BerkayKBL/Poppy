package com.berkaykbl.poppy.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.activity.ChatActivity
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userlist: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.username)
        val txtBio: TextView = view.findViewById(R.id.underline)
        val imgUser: CircleImageView = view.findViewById(R.id.userAvatar)
        val userLayout: LinearLayout = view.findViewById(R.id.userLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userlist[position]
        holder.txtUsername.text = user.username
        holder.txtBio.text = user.bio
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgUser)
        holder.userLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("isChatId", "false")
            bundle.putString("id", user.userId)

            Utils().changeActivity(context, ChatActivity::class.java, true, bundle)
        }
    }
}