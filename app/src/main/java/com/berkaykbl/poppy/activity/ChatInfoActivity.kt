package com.berkaykbl.poppy.activity

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.adapter.ChatMemberAdapter
import com.berkaykbl.poppy.databinding.ActivityChatInfoBinding
import com.berkaykbl.poppy.dialog.CallbackChatDetail
import com.berkaykbl.poppy.dialog.CallbackChatMember
import com.berkaykbl.poppy.dialog.ChangeChatDetail
import com.berkaykbl.poppy.manager.GroupMessageManger
import com.berkaykbl.poppy.model.GroupChat
import com.berkaykbl.poppy.model.User
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/*private var userList = ArrayList<User>()
private var isGroup = false
private var chatDataRef: DatabaseReference? = null*/

class ChatInfoActivity() : AppCompatActivity(), CallbackChatDetail, CallbackChatMember {
    /*fun setUserList(
        userL: ArrayList<User>,
        chatDataR: DatabaseReference,
        isGroup2: Boolean = false
    ) {
        userList = userL
        chatDataRef = chatDataR
        isGroup = isGroup2
    }*/

    private lateinit var binding: ActivityChatInfoBinding

    private var chatId: String? = null
    private var isGroup = false
    private var userList = ArrayList<User>()
    private var chat: GroupChat? = null
    private var chatname = ""
    private var chatdesc = ""
    private var chatimage = ""
    private var isAdmin = false
    private var chatDataRef: DatabaseReference? = null
    var members = HashMap<String, HashMap<String, Any>>()

    fun changeChatName(name: String) {
    }

    fun changeChatDesc(name: String) {
    }

    fun changeChatImage(url: String) {
    }

    fun changeChatMembers(chatMembers: HashMap<String, HashMap<String, Any>>) {

    }


    private val chatDataRefListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            chat = snapshot.getValue(GroupChat::class.java)

            if (chatname != chat!!.chat_name) {
                chatname = chat!!.chat_name
                binding.chatName.text = chatname
            }
            if (chatdesc != chat!!.chat_description) {
                chatdesc = chat!!.chat_description

                if (chat!!.chat_description != "") {
                    binding.chatDesc.text = chat!!.chat_description
                } else {
                    binding.chatDesc.setText(R.string.change_group_desc)
                }
            }
            if (chatimage != chat!!.chat_image) {
                chatimage = chat!!.chat_image
                Glide.with(this@ChatInfoActivity).load(chatimage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.chatImage)
            }

            if (members != chat!!.members) {
                members = chat!!.members
                val userRef = FirebaseDatabase.getInstance().getReference("Users")
                userList.clear()

                fun resumeCode() {
                    val currentAuth = Utils().getCurrentUser()

                    val currentUser = members.get(currentAuth!!.userId)
                    if (!currentUser.isNullOrEmpty()) {
                        isAdmin = currentUser.get("isOwner") == true
                    }


                    val memberEntries = members.entries.sortedWith(compareBy({
                        it.key != currentAuth.userId
                    }, {
                        it.value.get("isOwner") == false
                    }))


                    val result = ArrayList<String>()

                    for (entry in memberEntries) {
                        result.add(entry.key)
                    }


                    val rr = userList.sortedBy {
                        result.indexOf(it.userId)
                    }

                    userList = rr.toMutableList() as ArrayList<User>

                    val userAdapter = ChatMemberAdapter(
                        this@ChatInfoActivity,
                        members,
                        userList,
                        supportFragmentManager,
                        isAdmin,
                        currentAuth.userId
                    )
                    findViewById<RecyclerView>(R.id.chatMembersRecyclerView).adapter = userAdapter
                    binding.chatMembersRecyclerView.scrollToPosition(userAdapter.itemCount - 1)
                }

                for (key in members.keys.toList()) {
                    userRef.child(key)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                userList.add(snapshot.getValue(User::class.java)!!)

                                if (userList.size == members.size) resumeCode()

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }


            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        isGroup = intent.getBooleanExtra("isGroup", false)
        if (isGroup) {

            findViewById<RecyclerView>(R.id.chatMembersRecyclerView).layoutManager =
                LinearLayoutManager(this, LinearLayout.VERTICAL, false)

            chatId = intent.getStringExtra("chatId")

            chatDataRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId!!)
            chatDataRef!!.addValueEventListener(chatDataRefListener)


            binding.chatName.setOnClickListener {
                if (isAdmin) {
                    val dialog: DialogFragment = ChangeChatDetail("name")
                    dialog.show(supportFragmentManager, "")
                }
            }

            binding.chatDesc.setOnClickListener {
                if (isAdmin) {
                    val dialog: DialogFragment = ChangeChatDetail("desc", true)
                    dialog.show(supportFragmentManager, "")
                }
            }

            binding.addMember.setOnClickListener {
                if (isAdmin) {
                    val userList2 = ArrayList<User>()
                    var databaseRef: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference("Users")
                    databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userList2.clear()
                            for (datasnap: DataSnapshot in snapshot.children) {
                                val user = datasnap.getValue((User::class.java))

                                if (!members.containsKey(user!!.userId)) {
                                    userList2.add(user)
                                }
                            }
                            val r = CreateNewGroupActivity()
                            r.setUserList(userList2, chatDataRef!!, true)
                            Utils().changeActivity(this@ChatInfoActivity, r::class.java, true)
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }
            }
        }
    }

    override fun onChatNameChange(name: String, type: String) {
        if (type == "name") {
            chatDataRef!!.child("chat_name").setValue(name.replace("\n", ""))
            val currentUser = Utils().getCurrentUser()
            GroupMessageManger().sendMessage(
                chatDataRef,
                chatDataRef!!.key.toString(),
                currentUser!!.username + " changed group name as \"" + name + "\"",
                "",
                "system"
            )
        } else if (type == "desc") {
            chatDataRef!!.child("chat_description").setValue(name.replace("\n", ""))
            val currentUser = Utils().getCurrentUser()
            GroupMessageManger().sendMessage(
                chatDataRef,
                chatDataRef!!.key.toString(),
                currentUser!!.username + " changed group description as \"" + name + "\"",
                "",
                "system"
            )
        }
    }

    override fun onChatMemberChange(member_id: String, action: String) {
        if (action == "kick") {
            chatDataRef!!.child("members").child(member_id).removeValue()
            val currentUser = Utils().getCurrentUser()
            val kickedUserFilter = userList.filter { it.userId == member_id }
            val kickedUser = kickedUserFilter.first()
            GroupMessageManger().sendMessage(
                chatDataRef,
                chatDataRef!!.key.toString(),
                currentUser!!.username + ", kicked " + kickedUser.username,
                "",
                "system"
            )
        } else if (action == "make_admin") {
            chatDataRef!!.child("members").child(member_id).child("isOwner").setValue(true)
        } else if (action == "dismiss_admin") {
            chatDataRef!!.child("members").child(member_id).child("isOwner").setValue(false)
        }
    }

    /*fun sendMessage(
        chat_id: String = chat!!.chat_id,
        message: String,
        time: String,
        senderId: String = "system"
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
        chatDataRef!!.child("last_message_sender_id").setValue(sender!!.userId)

        binding.message.setText("")
    }*/
}