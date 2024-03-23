package com.berkaykbl.poppy.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.adapter.UserAdapter
import com.berkaykbl.poppy.adapter.UserSelectAdapter
import com.berkaykbl.poppy.databinding.ActivityCreateNewGroupBinding
import com.berkaykbl.poppy.manager.GroupMessageManger
import com.berkaykbl.poppy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val selectedUsers = HashMap<String, User>()
private var userList = ArrayList<User>()
private var hasGroup = false
private var chatDataRef: DatabaseReference? = null

class CreateNewGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewGroupBinding

    fun addSelectedUser(userId: String, user: User) {
        if (!selectedUsers.containsKey(userId)) {
            selectedUsers.put(userId, user)
        }
    }

    fun removeSelectedUser(userId: String) {
        if (selectedUsers.containsKey(userId)) {
            selectedUsers.remove(userId)
        }
    }

    fun setUserList(userL: ArrayList<User>, chatDataR: DatabaseReference, isGroup: Boolean = false) {
        userList = userL
        chatDataRef = chatDataR
        hasGroup = isGroup
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedUsers.clear()
        chatDataRef = null
        hasGroup = false
        userList.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewGroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.userSelectRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val currentUser = Utils().getCurrentUser()
        if (!hasGroup) {
            var databaseRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Users")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (datasnap: DataSnapshot in snapshot.children) {
                        val user = datasnap.getValue((User::class.java))

                        if (!user!!.userId.equals(currentUser!!.userId)) {
                            userList.add(user)
                        }
                    }
                    val userAdapter = UserSelectAdapter(this@CreateNewGroupActivity, userList)
                    view!!.findViewById<RecyclerView>(R.id.userSelectRecyclerView).adapter =
                        userAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            var ref: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
            binding.createGroup.setOnClickListener {
                val hash: HashMap<String, Any> = HashMap()
                hash.put("chat_name", currentUser!!.username + "'s Group")
                hash.put("chat_image", "")
                hash.put("chat_description", "")
                hash.put("is_group", "true")

                val membersHash: HashMap<String, HashMap<String, Any>> = HashMap()

                val memberHash = HashMap<String, Any>()
                memberHash.put("isOwner", true)
                memberHash.put("isMember", true)
                memberHash.put("last_read_message_id", "")
                membersHash.put(currentUser.userId, memberHash)
                val defaultMessages = ArrayList<String>()
                defaultMessages.add(currentUser.username + " created the '" + currentUser.username + "'s Group' group")

                selectedUsers.forEach {
                    val memberHash = HashMap<String, Any>()
                    memberHash.put("isOwner", false)
                    memberHash.put("isMember", true)
                    membersHash.put(it.key, memberHash)
                    val us = it.value
                    defaultMessages.add(currentUser.username + ", added " + us.username)
                }

                hash.put("members", membersHash)
                var chat = ref!!.child("Chats").push()
                hash.put("chat_id", chat.key!!)
                chat.setValue(hash)


                var i = 0
                defaultMessages.forEach {
                    /*var messageHash = HashMap<String, String>()
                    messageHash.put("chat_id", chat.key!!)
                    messageHash.put("message", it)
                    messageHash.put("sender_id", "system")
                    val chatMessage = ref.child("GroupMessages").child(chat.key!!).push()
                    messageHash.put("message_id", chatMessage.key!!)
                    chatMessage.setValue(messageHash)
                    i++
                    if (i == defaultMessages.size) {
                        chat.child("last_message_id").setValue(chatMessage.key!!)
                        chat.child("last_message").setValue(it)
                        chat.child("last_message_sender_id").setValue("system")
                    }*/
                    GroupMessageManger().sendMessage(chatDataRef, chat.key!!, it, "", "system")
                }

            }

        } else {
            Log.d("asdsad", "qwq")
            val userAdapter = UserSelectAdapter(this@CreateNewGroupActivity, userList)
            view!!.findViewById<RecyclerView>(R.id.userSelectRecyclerView).adapter = userAdapter

            binding.createGroup.setOnClickListener {
                val ch = chatDataRef!!.child("members")
                selectedUsers.forEach {
                    val memberHash = HashMap<String, Any>()
                    memberHash.put("isOwner", false)
                    memberHash.put("isMember", true)
                    /*GroupChatActivity().sendMessage(
                        chatDataRef!!.key!!,
                        currentUser!!.username + ", added " + it.value.username,
                        "",
                        true
                    )*/
                    ch.child(it.key).setValue(memberHash)
                }
                finish()

            }
        }


    }
}