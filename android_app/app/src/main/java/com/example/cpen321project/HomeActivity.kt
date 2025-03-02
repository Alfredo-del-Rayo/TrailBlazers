package com.example.cpen321project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val tkn = extras?.getString("tkn") ?: ""
        val email = extras?.getString("email") ?: ""

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.manageProfileButton).setOnClickListener() {
            val intent = Intent(this, ManageProfile::class.java)
            intent.putExtra("tkn", tkn)
            intent.putExtra("email", googleIdTokenCredential.id)
            startActivity(intent)
        }
        findViewById<Button>(R.id.signOutButton).setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            //Sign Out code
            startActivity(intent)
        }
    }
}