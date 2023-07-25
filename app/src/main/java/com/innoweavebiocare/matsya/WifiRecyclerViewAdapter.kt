////////////////////////////////////////////////////
//                                                //
// Copyright 2022-2023                            //
// Notice: Property of Innoweave Biocare          //
// Any part of this code cannot be copied or      //
// redistributed without prior consent of         //
// Innoweave                                      //
//                                                //
////////////////////////////////////////////////////

////////////////////////////////////////////////////
// File Name: WifiRecyclerViewAdapter.kt
// File Description: Adapter class for recycler view
// of Wifi Scan Activity that handle touch recycler item
// related function
// Author: Anshul Malviya
// Date: May 3, 2023
////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.net.wifi.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_wifi_scan_activity.view.*
import kotlin.math.log


class WifiRecyclerViewAdapter(
    private val arrayList: List<ScanResult>
) :
    RecyclerView.Adapter<WifiRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_wifi_scan_activity, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ssid = arrayList[position].SSID
        var isDuplicate = false

        for (i in 0 until position) {
            if (ssid == arrayList[i].SSID) {
                isDuplicate = true
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = ViewGroup.LayoutParams(0,0)
                break
            }
        }

        if (!isDuplicate) {
            holder.networkNameTv.text = ssid
        }

        val empty = ""
        if(arrayList[position].SSID == empty){
            val newSSID = "Hidden Network"
            holder.networkNameTv.text = newSSID
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val networkNameTv: TextView = view.networkNameTv
    }
}