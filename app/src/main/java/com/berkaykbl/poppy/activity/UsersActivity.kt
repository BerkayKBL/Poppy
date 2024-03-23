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
import com.berkaykbl.poppy.adapter.UserAdapter
import com.berkaykbl.poppy.databinding.ActivityUsersBinding
import com.berkaykbl.poppy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : Fragment() {
    val userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_users, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
        getUsersList()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun getUsersList() {
        var firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseRef:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (datasnap: DataSnapshot in snapshot.children) {
                    val user = datasnap.getValue((User::class.java))

                    if (!user!!.userId.equals(firebase.uid) ) {
                        userList.add(user)
                    }
                }
                val userAdapter = UserAdapter(requireContext(), userList)
                view!!.findViewById<RecyclerView>(R.id.recyclerView).adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}