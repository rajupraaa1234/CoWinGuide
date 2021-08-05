package com.example.cowinguide.consumer

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cowinguide.R
import com.example.cowinguide.provider.LogDetails

class MyAdapter (private val userList : ArrayList<User>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mylayout2,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {

        val user: User =  userList[position]
        holder.name.text = user.name
        holder.phone.text = user.number
        holder.date.text = user.date
        holder.location.text = user.location
        holder.type.text = user.type

        /*holder?.cardContainer?.setOnClickListener { clickListener(user, position) }*/
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    public class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById<TextView>(R.id.pName)
        val phone: TextView = itemView.findViewById<TextView>(R.id.pNumber)

        val date: TextView = itemView.findViewById<TextView>(R.id.pDate)

        val location: TextView = itemView.findViewById<TextView>(R.id.pLocation)
        val type: TextView = itemView.findViewById<TextView>(R.id.pType)

        val cardContainer: CardView = itemView.findViewById(R.id.cardView)


    }
}