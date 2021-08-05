package com.example.cowinguide.consumer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cowinguide.R
import com.google.firebase.firestore.*

class ConsumerService : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    public lateinit var myAdapter: MyAdapter
    lateinit var db: FirebaseFirestore
    lateinit var userArrayList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_service)

        recyclerView = findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)


        userArrayList = arrayListOf()

        myAdapter = MyAdapter(userArrayList)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("callLogs")
            .addSnapshotListener(object: EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) {
                        Log.e("firestore error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!! )
                    {
                        if(dc.type == DocumentChange.Type.ADDED)
                        {
                            userArrayList.add(dc.document.toObject(User::class.java))
                            Log.e(ConsumerService::class.simpleName,dc.document.toString())
                        }
                        myAdapter.notifyDataSetChanged()
                    }
                }

            })
    }
}