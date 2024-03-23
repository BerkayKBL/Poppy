package com.berkaykbl.poppy.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
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
import com.berkaykbl.poppy.activity.ChatInfoActivity
import com.berkaykbl.poppy.activity.GroupChatActivity
import com.berkaykbl.poppy.model.Chat
import com.berkaykbl.poppy.model.GroupChat
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import com.google.firebase.database.values
import de.hdodenhof.circleimageview.CircleImageView

class GroupChatAdapter(private val context: Context, private val chatlist: List<GroupChat>) :
    RecyclerView.Adapter<GroupChatAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.username)
        val txtLastMessage: TextView = view.findViewById(R.id.lastmessage)
        val imgUser: CircleImageView = view.findViewById(R.id.userAvatar)
        val userLayout: LinearLayout = view.findViewById(R.id.userLayout)
        val txtLastMessageSender: TextView = view.findViewById(R.id.lastmessagesender)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatlist[position]


        holder.txtUsername.text = chat.chat_name
        Glide.with(context).load(chat.chat_image).placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgUser)
        holder.txtLastMessage.text = chat.last_message

        val currentAuth = Utils().getCurrentUser()
        if (chat.last_message_sender_id.equals(currentAuth!!.userId)) {
            holder.txtLastMessageSender.text = "You: "
        } else {
            val currentUser = chat.members.get(currentAuth.userId)
            if (currentUser!!.containsKey("last_read_message_id")) {
                if (!(currentUser.get("last_read_message_id")!!.equals(chat.last_message_id))) {
                    holder.txtLastMessage.setTypeface(null, Typeface.BOLD)
                }
            }

        }

        holder.imgUser.setOnClickListener {
            val r = ChatInfoActivity()
            val bundle = Bundle()
            bundle.putString("chatId", chat.chat_id)
            bundle.putBoolean("isGroup", true)
            Utils().changeActivity(context, r::class.java, true, bundle)
        }

        holder.userLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", chat.chat_id)


            Utils().changeActivity(context, GroupChatActivity::class.java, true, bundle)
        }
    }

}