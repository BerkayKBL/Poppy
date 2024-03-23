package com.berkaykbl.poppy.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.dialog.ChangeChatMember
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ChatMemberAdapter(
    private val context: Context,
    private val members: HashMap<String, HashMap<String, Any>>,
    private val userlist: ArrayList<User>,
    private val fragmentManager: FragmentManager,
    private val userAdmin: Boolean,
    private val userId : String,
) :
    RecyclerView.Adapter<ChatMemberAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.username)
        val txtBio: TextView = view.findViewById(R.id.underline)
        val imgUser: CircleImageView = view.findViewById(R.id.userAvatar)
        val isAdmin: TextView = view.findViewById(R.id.groupAdmin)
        val userLayout: LinearLayout = view.findViewById(R.id.userLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_member, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userlist[position]

        if (members.containsKey(user.userId)) {
            holder.txtUsername.text = user.username
            holder.txtBio.text = user.bio
            Glide.with(context).load(user.profileImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgUser)
            val member = members.get(user.userId)
            val isAdmin : Boolean? = member!!.get("isOwner") as Boolean?
            if (isAdmin == true) {
                holder.isAdmin.layoutParams.width = LayoutParams.WRAP_CONTENT
            }

            if (userAdmin && user.userId != userId) {
                holder.userLayout.setOnClickListener {
                    val dialog: DialogFragment = ChangeChatMember(user.userId, isAdmin == true)
                    dialog.show(fragmentManager, "")
                }
            }

        }

    }
}