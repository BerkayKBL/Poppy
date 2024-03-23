package com.berkaykbl.poppy.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.Utils
import com.berkaykbl.poppy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewPager = binding.viewPager
        val adapter = PagerAdapter(this)
        viewPager.adapter = adapter

        binding.quitBTN.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Utils().changeActivity(this, LogInActivity::class.java, false, null)
        }

        binding.createGroupBTN.setOnClickListener {
            Utils().changeActivity(this, CreateNewGroupActivity::class.java, true, null)
        }

        val currentUser = Utils().getCurrentUser()
        binding.username.text = currentUser!!.username
    }

    class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            if (position == 2) {
                return UsersActivity()
            } else if (position == 1) {
                return UsersActivity()
            }
            return GroupChatsActivity()
        }

    }
}