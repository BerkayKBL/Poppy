package com.berkaykbl.poppy.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.adapter.MessageAdapter
import com.berkaykbl.poppy.databinding.ActivityChatBinding
import com.berkaykbl.poppy.model.Chat
import com.berkaykbl.poppy.model.Message
import com.berkaykbl.poppy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private var firebase: FirebaseUser? = null
    private var databaseRef: Query? = null

    private var receiverUserDataRef: DatabaseReference? = null

    private var chatDataRef : DatabaseReference? = null

    private lateinit var binding: ActivityChatBinding
    var receiver: User? = null
    var sender: User? = null
    val messageList: ArrayList<Message> = ArrayList()

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
        var receiverId: String

        sender = Utils().getCurrentUser()
        val isChatId = intent.getStringExtra("isChatId").equals("true")
        if (!isChatId) {
            receiverUserDataRef =
                FirebaseDatabase.getInstance().getReference("Users").child(chatId!!)
            receiverId = receiverUserDataRef!!.key!!
        } else {
            receiverId = intent.getStringExtra("receiverId")!!
            if (!chatId.equals("none")) {
                chatDataRef =
                    FirebaseDatabase.getInstance().getReference("Chats").child(chatId!!)
            }
            receiverUserDataRef =
                FirebaseDatabase.getInstance().getReference("Users").child(receiverId)
        }
        firebase = FirebaseAuth.getInstance().currentUser


        receiverUserDataRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isChatId) {
                    receiver = snapshot.getValue(User::class.java)
                } else {
                    receiver = snapshot.getValue(User::class.java)

                }
                binding.chatname.text = receiver!!.username
                binding.undertext.text = receiver!!.bio

                getMessagesList()
            }
        })
        binding.sendButton.setOnClickListener {
            val message = binding.message.text
            if (message.isNotEmpty()) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTime(Date())
                val mSec: Int = calendar.get(Calendar.MILLISECOND)
                sendMessage(message.toString(), mSec.toString())
            }
        }
    }

    private fun sendMessage(message: String, messageTime: String) {
        var ref: DatabaseReference? = FirebaseDatabase.getInstance().getReference()


        if (chatDataRef == null) {
            val hash: HashMap<String, Any> = HashMap()
            hash.put("chat_name", "")
            hash.put("chat_image", "")
            hash.put("chat_description", "")
            hash.put("is_group", "false")

            val membersHash: HashMap<String, HashMap<String, Any>> = HashMap()

            val memberHash = HashMap<String, Any>()
            memberHash.put("isOwner", false)
            memberHash.put("isMember", true)
            memberHash.put("last_read_message_id", "")
            membersHash.put(sender!!.userId, memberHash)
            Log.d("qwewqe", membersHash.keys.toString())

            val memberHash2 = HashMap<String, Any>()
            memberHash2.put("isOwner", false)
            memberHash2.put("isMember", true)
            memberHash2.put("last_read_message_id", "")
            membersHash.put(receiver!!.userId, memberHash2)
            Log.d("qwewqe", membersHash.keys.toString())

            hash.put("members", membersHash)
            val chat = ref!!.child("Chats").push()
            hash.put("chat_id", chat.key!!)
            chatDataRef = chat
            chatDataRef!!.setValue(hash)

        }

        getMessagesList()

        /*val hash: HashMap<String, Any> = HashMap()
        hash.put("sender_id", sender!!.userId)
        hash.put("chat_id", chatDataRef!!.key.toString())
        hash.put("message", message)
        hash.put("message_time", messageTime)

        val messageRef =
            ref!!.child("Messages").child(chatDataRef!!.key.toString())
                .push()
        hash.put("message_id", messageRef.key!!)
        messageRef.setValue(hash)
        chatDataRef!!.child("last_message_id").setValue(messageRef.key!!)
        chatDataRef!!.child("last_message").setValue(message)
        chatDataRef!!.child("last_message_sender_id").setValue(sender!!.userId)*/

        /*if (senderChatDataRef == null) {
            val hash: HashMap<String, String> = HashMap()
            hash.put("receiver_id", receiver!!.userId)
            hash.put("receiver_image", receiver!!.profileImage)
            hash.put("receiver_name", receiver!!.username)

            var chat: DatabaseReference
            if (receiverChatDataRef != null) {
                chat = ref!!.child("Chats").child(sender!!.userId)
                    .child(receiverChatDataRef!!.key.toString())
            } else {
                chat = ref!!.child("Chats").child(sender!!.userId).push()
            }
            chat.setValue(hash)
            senderChatDataRef = chat
            senderChatDataRef!!.child("chat_id").setValue(senderChatDataRef!!.key)
        }

        if (receiverChatDataRef == null) {
            val hash: HashMap<String, String> = HashMap()
            hash.put("receiver_id", sender!!.userId)
            hash.put("receiver_image", sender!!.profileImage)
            hash.put("receiver_name", sender!!.username)


            val chat = ref!!.child("Chats").child(receiver!!.userId)
                .child(senderChatDataRef!!.key.toString())
            chat.setValue(hash)
            receiverChatDataRef = chat
            receiverChatDataRef!!.child("chat_id").setValue(receiverChatDataRef!!.key)

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        receiverChatDataRef!!.removeEventListener(this)
                        receiverChatDataRef = null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }

            receiverChatDataRef!!.addValueEventListener(eventListener)
        }

        val hash: HashMap<String, Any> = HashMap()
        hash.put("sender_id", sender!!.userId)
        hash.put("chat_id", senderChatDataRef!!.key.toString())
        hash.put("message", message)
        hash.put("message_time", messageTime)

        val messageSenderRef =
            ref!!.child("Messages").child(sender!!.userId).child(senderChatDataRef!!.key.toString())
                .push()
        hash.put("message_id", messageSenderRef.key!!)
        messageSenderRef.setValue(hash)

        val hash2: HashMap<String, String> = HashMap()
        hash2.put("sender_id", sender!!.userId)
        hash2.put("chat_id", receiverChatDataRef!!.key.toString())
        hash2.put("message", message)
        hash2.put("message_time", messageTime)

        val messageReceiverRef = ref.child("Messages").child(receiver!!.userId)
            .child(receiverChatDataRef!!.key.toString()).child(messageSenderRef.key!!)
        hash2.put("message_id", messageReceiverRef.key!!)
        messageReceiverRef.setValue(hash2)


        senderChatDataRef!!.child("last_message").setValue(message)
        senderChatDataRef!!.child("last_message_id").setValue(messageSenderRef.key)
        senderChatDataRef!!.child("last_read_message_id").setValue(messageSenderRef.key)
        senderChatDataRef!!.child("last_message_time").setValue(messageTime)
        senderChatDataRef!!.child("last_message_issender").setValue("true")

        receiverChatDataRef!!.child("last_message").setValue(message)
        receiverChatDataRef!!.child("last_message_id").setValue(messageReceiverRef.key)
        receiverChatDataRef!!.child("last_message_time").setValue(messageTime)
        receiverChatDataRef!!.child("last_message_issender").setValue("false")


        binding.message.setText("")

        getMessagesList()*/

    }

    var isActive = false


    val messagesValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            messageList.clear()
            for (datasnap: DataSnapshot in snapshot.children) {
                val chat = datasnap.getValue((Message::class.java))

                messageList.add(chat!!)
            }
            chatDataRef!!.child("members").child(sender!!.userId).child("last_read_message_id")
                .setValue(messageList.last().message_id)
            val userAdapter = MessageAdapter(this@ChatActivity, messageList)
            findViewById<RecyclerView>(R.id.messageRecyclerView).adapter = userAdapter
            binding.messageRecyclerView.scrollToPosition(userAdapter.itemCount - 1)
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        databaseRef!!.removeEventListener(messagesValueListener)
    }

    private fun getMessagesList() {
        if (isActive) return
        if (chatDataRef == null) return
        isActive = true
        databaseRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatDataRef!!.key.toString())
        databaseRef!!.addValueEventListener(messagesValueListener)

    }
}