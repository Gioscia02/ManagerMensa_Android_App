package com.example.managermensa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.managermensa.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        val accountButton = findViewById<FloatingActionButton>(R.id.account_button)

//        if(savedInstanceState == null){
//            supportFragmentManager.commit{
//                setReorderingAllowed(true)
//                add<HomeFragment>(R.id.fragmentContainerView)
//            }

            
        enableEdgeToEdge()


        accountButton.setOnClickListener(){


            val intent = Intent(this,AccountActivity::class.java)
            startActivity(intent)

        }


        }


//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }
        }


