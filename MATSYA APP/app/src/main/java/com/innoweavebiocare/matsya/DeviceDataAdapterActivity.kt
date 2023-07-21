/////////////////////////////////////////////////////
//                                                 //
// Copyright 2022-2023                             //
// Notice: Property of Innoweave Biocare           //
// Any part of this code cannot be copied or       //
// redistributed without prior consent of          //
// Innoweave                                       //
//                                                 //
/////////////////////////////////////////////////////

/////////////////////////////////////////////////////
// File Name: DeviceDataAdapter.kt
// File Description: adapter class for handle recycler
// view data
// Author: Anshul Malviya
// Date: April 10, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.content.res.ColorStateList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.recyclerview.widget.RecyclerView
import com.innoweavebiocare.matsya.recyclerviewDataModel.DeviceData


class DeviceDataAdapterActivity(val c: HomeActivity, private var pondList:ArrayList<DeviceData>):RecyclerView.Adapter<DeviceDataAdapterActivity.UserViewHolder>()
{
    inner class UserViewHolder(private val v:View,listner: onItemClickListner):RecyclerView.ViewHolder(v){
        var name: TextView = v.findViewById(R.id.etPondName)
        var progressBar: ProgressBar = v.findViewById(R.id.progress_bar)
        var progressDo: TextView = v.findViewById(R.id.progress_text_do)
        var tvTemp: TextView = v.findViewById(R.id.tvTemp)
        var tvTDS: TextView = v.findViewById(R.id.tvTDS)
        var tvPH: TextView = v.findViewById(R.id.tvPH)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        // recyclerview item click listener
        init{
            v.setOnClickListener { v: View ->
                val position:Int=adapterPosition
                listner.onItemClick(adapterPosition)
            }
        }
    }
    private lateinit var mListner : onItemClickListner
    interface onItemClickListner{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListner(listner:onItemClickListner){
        mListner=listner
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item_recyclerview_home_activity,parent,false)
        return UserViewHolder(v,mListner)


    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = pondList[position]

        if(newList.tds <= 0 || newList.tds > 100000){
            holder.tvTDS.text = "NA"
            holder.tvTDS.setTextColor(Color.parseColor("#FF0000"))
        }else{
            var tds =  newList.tds.toString()
            holder.tvTDS.text = "$tds ppm"
            holder.tvTDS.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.dO <= 0.0F || newList.dO > 100.0F){
            holder.progressDo.text = "NA"
            holder.progressDo.setTextColor(Color.parseColor("#FF0000"))
        }else{
            holder.progressDo.text = newList.dO.toString()
            holder.progressDo.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.pH <= 0.0F || newList.pH > 14.0F){
            holder.tvPH.text = "NA"
            holder.tvPH.setTextColor(Color.parseColor("#FF0000"))
        }else{
            holder.tvPH.text = newList.pH.toString()
            holder.tvPH.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.temp <= 0.0F || newList.temp > 100.0F){
            holder.tvTemp.text = "NA"
            holder.tvTemp.setTextColor(Color.parseColor("#FF0000"))
        }else{
            var temp = newList.temp.toString()
            holder.tvTemp.text = "$temp \u2103"
            holder.tvTemp.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.dateString == "NA"){
            holder.tvDate.setTextColor(Color.parseColor("#FF0000"))
        }else{
            holder.tvDate.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.timeString == "NA"){
            holder.tvTime.setTextColor(Color.parseColor("#FF0000"))
        }else{
            holder.tvTime.setTextColor(Color.parseColor("#000000"))
        }

        if(newList.dO == 0.0f){
            holder.progressBar.progressTintList= ColorStateList.valueOf(Color.parseColor("#FFDDDDDD"))
        }else{
            holder.progressBar.progress = newList.dO.toInt()
        }
        holder.name.text = newList.pondName
        holder.tvTime.text = newList.timeString
        holder.tvDate.text = newList.dateString
    }

    override fun getItemCount(): Int {
        return  pondList.size
    }
}

