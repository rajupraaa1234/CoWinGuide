package com.example.cowinguide.provider

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cowinguide.R

class ProviderService : AppCompatActivity() , AdapterView.OnItemClickListener {

    lateinit var recyclerView : RecyclerView
    var TAG = ProviderService::class.simpleName
    public lateinit var myAdapter : CallLogView
    var userArrayList: ArrayList<LogDetails> = arrayListOf()


    var cols = listOf<String>(
        CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
        CallLog.Calls.DURATION, CallLog.Calls.DATE, CallLog.Calls.GEOCODED_LOCATION).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {

        myAdapter = CallLogView(userArrayList){ itemDto: LogDetails, position: Int ->
            val intent = Intent(this, PushtoDB::class.java)
            intent.putExtra("Username", "Blah")
            startActivity(intent)
            Log.e("MyActivity", "Clicked on item  ${itemDto.phone} at position $position")
            /*Snackbar.make(View, "Clicked on item  ${itemDto.phone} at position $position",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()*/
        }
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(TAG,e.toString())
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_provider_service)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1){ Manifest.permission.READ_CALL_LOG},101)
        }
        else
            displayLog()



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 101 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            displayLog()
    }

    private fun displayLog() {

        var from = listOf<String>(
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.GEOCODED_LOCATION)

        var recieve = intArrayOf(
            R.id.dbNumber,
            R.id.duration,
            R.id.tvType,
            R.id.tvDate,
            R.id.tvLocation
        )

        var rs = contentResolver.query(
            CallLog.Calls.CONTENT_URI, cols, null,
            null, "${CallLog.Calls.LAST_MODIFIED} DESC")

        var number : Int? = rs?.getColumnIndex(CallLog.Calls.NUMBER)
        var type : Int? = rs?.getColumnIndex(CallLog.Calls.TYPE)
        var timestamp : Int? = rs?.getColumnIndex(CallLog.Calls.DATE)


        while(rs?.moveToNext()!!)
        {
            var type = " "
            when (rs.getString(3).toInt()){
                1 -> type = "Incoming"
                2 -> type = "Outgoing"
                3 -> type = "Missed Call"
                5 -> type = "Rejected Call"
            }

            userArrayList.add(LogDetails(rs.getString(1),type,rs.getString(4)))
            myAdapter.notifyDataSetChanged()

        }

        /*var adapter = SimpleCursorAdapter(applicationContext, R.layout.mylayout, rs,
            from.toTypedArray(), recieve, 0)*/
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,
            false)


        userArrayList = arrayListOf()


        recyclerView.adapter = myAdapter



    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

        var dataCursor = adapterView?.getItemAtPosition(position) as Cursor
        var colIndex = dataCursor.getColumnIndex(CallLog.Calls.NUMBER)
        var phoneno =dataCursor.getString(colIndex)
        Log.i("providerservice-phno= ",phoneno)
    }
}