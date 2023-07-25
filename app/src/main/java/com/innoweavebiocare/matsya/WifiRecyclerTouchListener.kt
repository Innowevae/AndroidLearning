///////////////////////////////////////////////////
//                                               //
// Copyright 2022-2023                           //
// Notice: Property of Innoweave Biocare         //
// Any part of this code cannot be copied or     //
// redistributed without prior consent of        //
// Innoweave                                     //
//                                               //
///////////////////////////////////////////////////

///////////////////////////////////////////////////
// File Name: WifiRecyclerTouchListener.kt
// File Description: Recycler touch listener class
// for wifiScanActivity class it handles accessing
// item of recyclerView.
// Author: Anshul Malviya
// Date: May 3, 2023
///////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class WifiRecyclerTouchListener(
    context: Context?,
    recyclerView: RecyclerView,
    private val clickListener: ClickListener?) :
    RecyclerView.OnItemTouchListener {
    private val gestureDetector: GestureDetector
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child: View? = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child))
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    interface ClickListener {
        fun onClick(view: View?, position: Int)
        fun onLongClick(view: View?, position: Int)
    }

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child: View? = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                }
            }
        })
    }
}