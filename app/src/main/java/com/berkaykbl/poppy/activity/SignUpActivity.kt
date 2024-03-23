package com.berkaykbl.poppy.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.buttonToLogin.setOnClickListener {

            Utils().changeActivity(this@SignUpActivity, LogInActivity::class.java, false)
        }

        binding.buttonSignUp.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val passwordAgain = binding.passwordAgain.text.toString()

            var anyEmpty = false

            if (username.isEmpty()) {
                anyEmpty = true
                binding.username.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.username.setBackgroundResource(R.drawable.edittext_bg)

            if (email.isEmpty()) {
                anyEmpty = true
                binding.email.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.email.setBackgroundResource(R.drawable.edittext_bg)

            if (password.isEmpty()) {
                anyEmpty = true
                binding.password.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.password.setBackgroundResource(R.drawable.edittext_bg)

            if (passwordAgain.isEmpty()) {
                anyEmpty = true
                binding.passwordAgain.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else binding.passwordAgain.setBackgroundResource(R.drawable.edittext_bg)


            if (password.isEmpty() || !password.equals(passwordAgain)) {
                anyEmpty = true
                binding.passwordAgain.setBackgroundResource(R.drawable.edittext_bg_wrong)
                binding.password.setBackgroundResource(R.drawable.edittext_bg_wrong)
            } else {
                binding.passwordAgain.setBackgroundResource(R.drawable.edittext_bg)
                binding.password.setBackgroundResource(R.drawable.edittext_bg)
            }

            if (!anyEmpty) {
                registerUser(username, email, password)
            }
        }


    }

    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId = user!!.uid

                    databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashmap: HashMap<String, String> = HashMap()
                    hashmap.put("userId", userId)
                    hashmap.put("username", username)
                    hashmap.put("profileImage", "")
                    hashmap.put("bio", "")

                    databaseRef.setValue(hashmap).addOnCompleteListener(this) {

                        if (it.isSuccessful) {

                            binding.username.setText("")
                            binding.email.setText("")
                            binding.password.setText("")
                            binding.passwordAgain.setText("")
                            Utils().changeActivity(this@SignUpActivity, MainActivity::class.java, false)
                        }
                    }
                } else {
                    it.exception!!.localizedMessage?.let { it1 ->
                        binding.errorMessage.text = it1
                        binding.errorMessage.layoutParams.height = LayoutParams.WRAP_CONTENT
                    }
                }
            }
    }

}