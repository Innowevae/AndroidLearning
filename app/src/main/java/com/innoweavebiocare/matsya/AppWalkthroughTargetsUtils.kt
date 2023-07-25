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
// File Name: AppWalkthroughTargetsUtils.kt
// File Description: this file contains the code for
// the target view properties.
// Author: Ritvik Sahu
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.graphics.Typeface
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget

fun AppWalkthroughTargetsUtils (view: View, title: String, description: String, targetRadius: Int): TapTarget
{
    return TapTarget.forView(view, title, description)
    .outerCircleColor(R.color.colorPrimary)
    .outerCircleAlpha(0.9f)
    .targetCircleColor(R.color.white)
    .titleTextSize(30)
    .titleTextColor(R.color.white)
    .descriptionTextSize(15)
    .descriptionTextColor(R.color.black)
    .textColor(R.color.white)
    .textTypeface(Typeface.SANS_SERIF)
    .dimColor(R.color.black)
    .drawShadow(true)
    .cancelable(false)
    .tintTarget(true)
    .transparentTarget(true)
    .targetRadius(targetRadius)
}
