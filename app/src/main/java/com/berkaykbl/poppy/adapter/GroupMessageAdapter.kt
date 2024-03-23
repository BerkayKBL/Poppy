package com.berkaykbl.poppy.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.activity.ChatActivity
import com.berkaykbl.poppy.model.Chat
import com.berkaykbl.poppy.model.GroupMessage
import com.berkaykbl.poppy.model.Message
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random

private var oldMessageLayout: LinearLayout? = null

class GroupMessageAdapter(
    private val context: Context,
    private val chatlist: ArrayList<GroupMessage>,
    private val membersList: ArrayList<User>
) :
    RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>() {

    var firebaseUser: User? = null
    private var last_sender_id: String = ""
    private val userColor: HashMap<String, Int> = HashMap()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.messageText)
        val txtMessageLayout: LinearLayout? = view.findViewById(R.id.messageLayout)
        val txtMessageTime: TextView? = view.findViewById(R.id.messageTime)

        val username: TextView? = view.findViewById(R.id.username)
        val userAvatar: ImageView? = view.findViewById(R.id.userAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0) {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_outgoing_message, parent, false)
            return ViewHolder(view)
        } else if (viewType == 2) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_system_message, parent, false)
            return ViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incoming_group_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatlist.size
    }

    private fun randomColor(): Int {
        val random = Random
        val red = random.nextInt(256) // Kırmızı için 0-255 arası rastgele bir değer
        val green = random.nextInt(256) // Yeşil için 0-255 arası rastgele bir değer
        val blue = random.nextInt(256) // Mavi için 0-255 arası rastgele bir değer

        // RGB değerlerini birleştirerek bir renk oluştur
        return android.graphics.Color.rgb(red, green, blue)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = chatlist[position]
        holder.txtMessage.text = message.message

        if (message.sender_id != "system") {

            holder.txtMessageTime!!.text = "10:15"

            if (message.sender_id.equals(firebaseUser!!.userId)) {
                return
            }
            val user = membersList.find {
                it.userId == message.sender_id
            }

            if (user == null) {
                holder.username!!.text = "Unknown"
                holder.userAvatar?.let {
                    Glide.with(context).load(R.drawable.ic_launcher_foreground)
                        .into(it)
                }
                return
            }

            if (holder.username != null) {
                holder.username.text = user!!.username
                if (userColor.containsKey(user.userId)) {
                    holder.username.setTextColor(userColor[user.userId]!!)
                } else {
                    val rc = randomColor()
                    userColor.put(user.userId, rc)
                    holder.username.setTextColor(rc)
                }
            }

            holder.userAvatar?.let {
                Glide.with(context).load(user!!.profileImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(it)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {

        firebaseUser = Utils().getCurrentUser()

        val chat = chatlist[position]
        val sender_id = chat.sender_id
        /*if (sender_id.equals(firebaseUser!!.userId)) {
            Log.d("id aynı", chatlist[position].message)
            Log.d("id aynı", "id aynı")
            if (sender_id == last_sender_id) {
                viewType = 3
                Log.d("id aynı", "last sender")
            }else {
                viewType = 0
                Log.d("id aynı", "last sender değil")
            }
        } else if (sender_id == "system") {
            viewType = 2
        } else {
            if (sender_id == last_sender_id) {
                viewType = 4
            }
        }*/
        val viewType = if (sender_id.equals(firebaseUser!!.userId)) {
            0
        } else if (sender_id == "system") {
            2
        } else {
            1
        }

        return viewType
    }

}