package com.example.cowinguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class SpinnerActivity : AppCompatActivity() {


    lateinit var result: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)

        val option = findViewById<Spinner>(R.id.sp_option)
        result = findViewById(R.id.tv_result)

        val options = arrayOf("Service 1", "Service 2", "Service 3", "Service 4")

        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,options)
        option.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                result.text = options.get(position )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                result.text = "Please select one item"
            }

        }
    }
}