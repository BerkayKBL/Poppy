package com.berkaykbl.poppy.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.databinding.ActivityLogInBinding
import com.berkaykbl.poppy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)



        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Utils().changeCurrentUser(snapshot.getValue(User::class.java))
                        /*val r = ChatInfoActivity()
                        val bundle = Bundle()
                        bundle.putString("chatId", "-NlUeWPhAOmJo7BQqwq6")
                        bundle.putBoolean("isGroup", true)
                        Utils().changeActivity(this@LogInActivity, r::class.java, false, bundle)*/
                        val bundle = Bundle()
                        bundle.putString("id", "-NlPU2n1K66IvkNI0yUB")
                        Utils().changeActivity(this@LogInActivity, MainActivity::class.java, false, bundle)
                    }
                }
            })
            return
        }
        val view = binding.root
        setContentView(view)

        binding.buttonToSignup.setOnClickListener {

            Utils().changeActivity(this@LogInActivity, UsersActivity::class.java, true)

        }


        binding.buttonLogIn.setOnClickListener {

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            var anyEmpty = false
            if (email.isEmpty()) {
                anyEmpty = true
                binding.email.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.email.setBackgroundResource(R.drawable.edittext_bg)

            if (password.isEmpty()) {
                anyEmpty = true
                binding.password.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.password.setBackgroundResource(R.drawable.edittext_bg)

            if (!anyEmpty) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.email.setText("")
                        binding.password.setText("")
                        Utils().changeActivity(this@LogInActivity, MainActivity::class.java, false)
                    } else {
                        it.exception!!.localizedMessage?.let { it1 ->
                            binding.errorMessage.text = it1
                            binding.errorMessage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                    }
                }
            }
        }
    }

}