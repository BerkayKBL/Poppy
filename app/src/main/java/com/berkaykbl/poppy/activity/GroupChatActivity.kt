package com.berkaykbl.poppy.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.adapter.GroupMessageAdapter
import com.berkaykbl.poppy.databinding.ActivityChatBinding
import com.berkaykbl.poppy.manager.GroupMessageManger
import com.berkaykbl.poppy.model.GroupChat
import com.berkaykbl.poppy.model.GroupMessage
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Date

class GroupChatActivity : AppCompatActivity() {


    private lateinit var binding: ActivityChatBinding
    var sender: User? = null
    var chatDataRef: DatabaseReference? = null
    val messageList: ArrayList<GroupMessage> = ArrayList()
    var chat: GroupChat? = null
    var last_message_id = ""


    var chatName = ""
    var chatDesc = ""
    var chatImage = ""
    var members = HashMap<String, HashMap<String, Any>>()
    var membersUser = ArrayList<User>()

    val chatDataRefListener: ValueEventListener = object : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            chat = snapshot.getValue(GroupChat::class.java)
            if (chat!!.members.size == 0) {
                chatDataRef!!.removeValue()
                FirebaseDatabase.getInstance().getReference("GroupMessages").child(chat!!.chat_id).removeValue()
                d()
                return
            }
            if (chatName != chat!!.chat_name) {
                chatName = chat!!.chat_name
                binding.chatname.text = chatName
            }

            if (chatDesc != chat!!.chat_description) {
                chatName = chat!!.chat_description
                binding.undertext.text = chatDesc
            }

            if (chatImage != chat!!.chat_image) {
                chatImage = chat!!.chat_image
                Glide.with(this@GroupChatActivity).load(chatImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.userAvatar)
            }

            if (last_message_id != chat!!.last_message_id) {
                last_message_id = chat!!.last_message_id
            }

            if (members.size != chat!!.members.size) {
                members = chat!!.members
                val userRef = FirebaseDatabase.getInstance().getReference("Users")

                membersUser.clear()
                for (key in members.keys.toList()) {
                    userRef.child(key)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                membersUser.add(snapshot.getValue(User::class.java)!!)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }
            }

            GroupMessageManger().readMessage(chat!!.chat_id, chat!!.last_message_id, sender!!.userId)

            getMessageList()
        }
    }

    private fun d() {
        chatDataRef!!.removeEventListener(chatDataRefListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        d()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<RecyclerView>(R.id.messageRecyclerView).layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        binding.backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
        }
        val chatId = intent.getStringExtra("id")

        sender = Utils().getCurrentUser()
        chatDataRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId!!)


        chatDataRef!!.addValueEventListener(chatDataRefListener)



        binding.sendButton.setOnClickListener {
            val message = binding.message.text
            if (message.isNotEmpty()) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTime(Date())
                val mSec: Int = calendar.get(Calendar.MILLISECOND)
                GroupMessageManger().sendMessage(chatDataRef, chat!!.chat_id, message.toString(), mSec.toString(), sender!!.userId)
            }
        }

        binding.quitChat.setOnClickListener {
            Utils().changeActivity(this, MainActivity::class.java, false, null)
            GroupMessageManger().sendMessage(chatDataRef, chat!!.chat_id, sender!!.username + " exited the group", "12", "system")
            chatDataRef!!.child("members").child(sender!!.userId).removeValue()
        }
        binding.chatinfo.setOnClickListener {

            val r = ChatInfoActivity()
            val bundle = Bundle()
            bundle.putString("chatId", chatId)
            bundle.putBoolean("isGroup", true)
            Utils().changeActivity(this, r::class.java, true, bundle)

        }

        binding.addMember.setOnClickListener {
            val userList = ArrayList<User>()
            var databaseRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Users")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (datasnap: DataSnapshot in snapshot.children) {
                        val user = datasnap.getValue((User::class.java))

                        if (!members.containsKey(user!!.userId)) {
                            userList.add(user)
                        }
                    }
                    val r = CreateNewGroupActivity()
                    r.setUserList(userList, chatDataRef!!)
                    Utils().changeActivity(this@GroupChatActivity, r::class.java, true)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        binding.message.setOnFocusChangeListener { view, focus ->
            if (focus) {
                binding.undertext.text = sender!!.username + " printing..."
            } else {
                binding.undertext.text = ""
            }
        }
    }


    /*fun sendMessage(
        chat_id: String = chat!!.chat_id,
        message: String,
        time: String,
        senderId: String = sender!!.userId
    ) {
        var ref: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var messageHash = HashMap<String, String>()
        messageHash.put("chat_id", chat_id)
        messageHash.put("message", message)
        messageHash.put(
            "sender_id",
            senderId
        )
        val chatMessage = ref!!.child("GroupMessages").child(chat_id).push()
        messageHash.put("message_id", chatMessage.key!!)
        chatMessage.setValue(messageHash)
        chatDataRef!!.child("last_message_id").setValue(chatMessage.key!!)
        chatDataRef!!.child("last_message").setValue(message)
        chatDataRef!!.child("last_message_sender_id").setValue(senderId)

        binding.message.setText("")
    }*/

    val messagesValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            messageList.clear()
            for (datasnap: DataSnapshot in snapshot.children) {
                val chat = datasnap.getValue((GroupMessage::class.java))

                messageList.add(chat!!)
            }
            val userAdapter = GroupMessageAdapter(this@GroupChatActivity, messageList, membersUser)
            findViewById<RecyclerView>(R.id.messageRecyclerView).adapter = userAdapter
            binding.messageRecyclerView.scrollToPosition(userAdapter.itemCount - 1)
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    var isActive = false
    private var messagesDatabaseRef: Query? = null
    fun getMessageList() {
        if (isActive) return
        if (chat == null) return
        isActive = true
        messagesDatabaseRef =
            FirebaseDatabase.getInstance().getReference("GroupMessages").child(chat!!.chat_id)
        messagesDatabaseRef!!.addValueEventListener(messagesValueListener)
    }

}