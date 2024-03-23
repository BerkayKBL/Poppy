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
import com.berkaykbl.poppy.model.Chat
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

class ChatAdapter(private val context: Context, private val chatlist: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var firebaseUser: FirebaseUser? = null


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
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val chat = chatlist[position]
        var receiverId = chat.receiver_id
        var receiverName = chat.receiver_name
        var receiverImage = chat.receiver_image


        holder.txtUsername.text = receiverName
        Glide.with(context).load(receiverImage).placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgUser)
        holder.txtLastMessage.text = chat.last_message
        if (chat.last_message_issender.equals("true")) {
            holder.txtLastMessageSender.text = "You: "
        }

        if (chat.last_message_issender.equals("false") && chat.last_read_message_id != chat.last_message_id) {
            holder.txtLastMessage.setTypeface(null, Typeface.BOLD)
        }


        holder.userLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("isChatId", "true")
            bundle.putString("id", chat.chat_id)
            bundle.putString("receiverId", receiverId)

            var chat2 = FirebaseDatabase.getInstance().getReference("Chats")
                .child(Utils().getCurrentUser()!!.userId).child(chat.chat_id)

            chat2.child("last_message_id").get().addOnSuccessListener {
                dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val vari = dataSnapshot.value
                    chat2.child("last_read_message_id").setValue(vari)
                }
            }


            Utils().changeActivity(context, ChatActivity::class.java, true, bundle)
        }
    }

}