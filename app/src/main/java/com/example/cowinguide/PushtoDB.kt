package com.example.cowinguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_pushto_db.*
import kotlinx.android.synthetic.main.activity_pushto_db.tvNumber
import kotlinx.android.synthetic.main.mylayout.*

class PushtoDB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pushto_db)

        submit.setOnClickListener(){
            val name = dbName.text.toString()
            val  number = tvNumber.text.toString()
            val type = dbType.text.toString()
            val date = dbDate.text.toString()
            val location = dbLocation.text.toString()
            saveFirestore(name, number, type, date, location)
        }
    }

    private fun saveFirestore(name: String, number: String, type: String, date: String, location: String) {

        val db = FirebaseFirestore.getInstance()
        val user: MutableMap<String, Any> = HashMap()
        user["name"] = name
        user["number"] = number
        user["type"] = type
        user["date"] = date
        user["location"] = location

        db.collection("callLogs")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this@PushtoDB, "record added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@PushtoDB, "record adding failed", Toast.LENGTH_SHORT).show()

            }


    }
}