package com.berkaykbl.poppy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.adapter.ChatAdapter
import com.berkaykbl.poppy.adapter.GroupChatAdapter
import com.berkaykbl.poppy.adapter.UserAdapter
import com.berkaykbl.poppy.databinding.ActivityUsersBinding
import com.berkaykbl.poppy.model.Chat
import com.berkaykbl.poppy.model.GroupChat
import com.berkaykbl.poppy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GroupChatsActivity : Fragment() {
    val chatList = ArrayList<GroupChat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chats, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<RecyclerView>(R.id.recyclerView).layoutManager =
            LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
        getChatsList()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        //val currentUser = Utils().getCurrentUser()
        //FirebaseDatabase.getInstance().getReference("GroupChats").orderByChild("members/"+currentUser!!.userId+"/isMember").equalTo(true).removeEventListener(valueEvent)
    }

    val valueEvent = object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            chatList.clear()
            for (datasnap: DataSnapshot in snapshot.children) {
                val c = (GroupChat::class.java)
                val chat = datasnap.getValue(c)
                chatList.add(chat!!)
            }
            adap()
        }
    }

    fun getChatsList() {
        val currentUser = Utils().getCurrentUser()
        /*var databaseRef: Query =
            FirebaseDatabase.getInstance().getReference("GroupChats")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (datasnap: DataSnapshot in snapshot.children) {
                    val c = (GroupChat::class.java)
                    val chat = datasnap.getValue(c)
                    chatList.add(chat!!)
                }
                adap()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })*/

        FirebaseDatabase.getInstance().getReference("Chats").orderByChild("members/"+currentUser!!.userId+"/isMember").equalTo(true).addValueEventListener(valueEvent)

    }


    fun adap() {

        val userAdapter = GroupChatAdapter(requireContext(), chatList)
        requireView().findViewById<RecyclerView>(R.id.recyclerView).adapter = userAdapter
    }
}