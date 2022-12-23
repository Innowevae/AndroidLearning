package com.example.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var lastNumeric: Boolean = false
    var lastDot: Boolean = false

    private var tvInput:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View){
        tvInput?.append((view as Button).text)
        lastNumeric = true
    }

    fun onClear(view: View){
        tvInput?.text = ""
        lastNumeric = false
        lastDot = false
    }
    fun onDecimalPoint(view: View) {


        if (lastNumeric && !lastDot) {
            tvInput?.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onOperator(view: View) {
        tvInput?.text?.let {
            if (lastNumeric && !isOperatorAdded(it.toString())) {
                tvInput?.append((view as Button).text)
                lastNumeric = false
                lastDot = false
            }
        }

    }


    fun onEqual(view: View) {

        if (lastNumeric) {

            var tvValue = tvInput?.text.toString()
            var prefix = ""
            try {


                if (tvValue.startsWith("-")) {
                    prefix = "-"
                    tvValue = tvValue.substring(1);
                }

                when {
                    tvValue.contains("/") -> {
                        val splitedValue = tvValue.split("/")

                        var one = splitedValue[0]
                        val two = splitedValue[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }

                        tvInput?.text = removeZeroAfterDot((one.toDouble() / two.toDouble()).toString())
                    }
                    tvValue.contains("*") -> {

                        val splitedValue = tvValue.split("*")

                        var one = splitedValue[0]
                        val two = splitedValue[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }


                        tvInput?.text = removeZeroAfterDot((one.toDouble() * two.toDouble()).toString())
                    }
                    tvValue.contains("-") -> {


                        val splitedValue = tvValue.split("-")

                        var one = splitedValue[0]
                        val two = splitedValue[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }


                        tvInput?.text = removeZeroAfterDot((one.toDouble() - two.toDouble()).toString())
                    }
                    tvValue.contains("+") -> {

                        val splitedValue = tvValue.split("+")

                        var one = splitedValue[0]
                        val two = splitedValue[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }

                        tvInput?.text = removeZeroAfterDot((one.toDouble() + two.toDouble()).toString())
                    }
                }
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeZeroAfterDot(result: String): String {

        var value = result

        if (result.contains(".0")) {
            value = result.substring(0, result.length - 2)
        }

        return value
    }


    private fun isOperatorAdded(value: String): Boolean {



        return if (value.startsWith("-")) {
            false
        } else {
            (value.contains("/")
                    || value.contains("*")
                    || value.contains("-")
                    || value.contains("+"))
        }
    }
}