package com.example.cpen321project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cpen321andriodapp.ApiService
import com.example.cpen321project.MainActivity.Companion
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ManageProfile : AppCompatActivity() {

    companion object {
        private const val TAG = "ManageProfile"
    }

    private lateinit var paceText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_profile)
        paceText = findViewById(R.id.editTextNumberDecimal)

        paceText.setText("1");


        val runDistanceArray = resources.getStringArray(R.array.RunDistance)
        val runTimeArray = resources.getStringArray(R.array.RunTime)
        var runDistance: String? = null
        var runTime: String? = null
        val extras = intent.extras
        val tkn = extras?.getString("tkn") ?: ""
        val email = extras?.getString("email") ?: ""
        Log.d(TAG, "tkn and email: $tkn and $email")

        val disSpinner = findViewById<Spinner>(R.id.RunDistance)
        if (disSpinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, runDistanceArray)
            disSpinner.adapter = adapter

            disSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Log.d(TAG, runDistanceArray[position])
                    runDistance = runDistanceArray[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do Nothing
                }
            }
        }
        val timeSpinner = findViewById<Spinner>(R.id.RunTime)
        if (timeSpinner != null) {
            //Note: Will be better to make helper function to reduce repetition
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, runTimeArray)
            timeSpinner.adapter = adapter

            timeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Log.d(TAG, runTimeArray[position])
                    runTime = runTimeArray[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do Nothing
                }
            }
        }

        findViewById<Button>(R.id.save_button).setOnClickListener() {
            val pace = paceText.text.toString().toIntOrNull()
            if(pace == null || pace == 0){
                Toast.makeText(this, "Please enter valid Pace value", Toast.LENGTH_SHORT).show()
            }
            else{
                val jsonString = """
                    {
                        "distance": "${runDistance ?: "0"}",
                        "time": "${runTime ?: "0"}",
                        "pace": $pace
                    }
                """
                val requestBody = RequestBody.create(
                    MediaType.parse("application/json"), jsonString
                )
                updateUser(tkn, email, requestBody)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun updateUser(token: String, email: String, requestBody: RequestBody){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.updateUser("Bearer $token", email, requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageProfile, "Changes Saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageProfile, "Unable to Update Profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ManageProfile, "Unable to Update Profile", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Request failed: ${t.message}")
            }
        })
    }
}