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
import com.berkaykbl.poppy.model.Chat
import com.berkaykbl.poppy.model.Message
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val context: Context, private val chatlist: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var firebaseUser:FirebaseUser? = null


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.messageText)
        val txtMessageTime: TextView = view.findViewById(R.id.messageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0) {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_outgoing_message, parent, false)
            return ViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_incoming_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = chatlist[position]
        holder.txtMessage.text = user.message
        holder.txtMessageTime.text = "10:15"
    }

    override fun getItemViewType(position: Int): Int {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        Log.d("qe", (chatlist[position].sender_id))
        if (chatlist[position].sender_id.equals(firebaseUser!!.uid)) {
            return 0
        }
        return 1
    }

}