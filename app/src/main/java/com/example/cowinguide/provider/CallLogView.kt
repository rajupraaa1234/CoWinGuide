package com.example.cowinguide.provider

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cowinguide.R

class CallLogView (private val userList : ArrayList<LogDetails>, val clickListener: (LogDetails, Int) -> Unit): RecyclerView.Adapter<CallLogView.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mylayout,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: LogDetails =  userList[position]
        holder.dbNumber.text = user.phone
        holder.type.text = user.type
        holder.timestamp.text = user.timeStamp.toString()
        holder.location.text = user.location

        holder?.cardContainer?.setOnClickListener { clickListener(user, position) }
    }

    override fun getItemCount(): Int {
        Log.i("tag", userList.size.toString())
        return userList.size
    }

    public class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dbNumber: TextView = itemView.findViewById<TextView>(R.id.tvNumber)
        val type: TextView = itemView.findViewById<TextView>(R.id.tvType)
        val timestamp = itemView.findViewById<TextView>(R.id.tvDate)
        val location = itemView.findViewById<TextView>(R.id.tvLocation)

        val cardContainer: CardView = itemView.findViewById(R.id.cardView)


    }

}