package sahu.ritvik.mycalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    
    var lastnumeric:Boolean=false
    var lastDot:Boolean=false
    
    private var tvInput: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        tvInput=findViewById(R.id.tvInput)
        
    }

    fun onDigit(view:View) {
        tvInput?.append((view as Button).text)
        lastnumeric=true
        lastDot=false
    }
    
    fun onClear(view:View){
        tvInput?.text=""
    }
    
    fun onDecimalPoint(view:View){
        if(lastnumeric && !lastDot){
            tvInput?.append(".")
            lastnumeric=false
            lastDot=true
        }
    }
    
    fun onOperator(view:View){
        tvInput?.text?.let {
            if(lastnumeric && !isOperatorAdded(it.toString())){
                tvInput?.append((view as Button).text)
                lastnumeric=false
                lastDot=true
            }
        }
    }

    fun onEqual(view:View){
        if(lastnumeric){
            var tvValue =tvInput?.text.toString()
            var prefix= ""
            try{

                if(tvValue.startsWith("-")){
                    prefix="-"
                    tvValue=tvValue.substring(1)
                }
                if(tvValue.contains("-")){
                    val splitvalue=tvValue.split("-")

                    var one=splitvalue[0]
                    var two=splitvalue[1]

                    if(prefix.isNotEmpty()){
                        one=prefix+one
                    }
                    tvInput?.text= (one.toDouble() - two.toDouble()).toString()
                }else if(tvValue.contains("-")) {
                    val splitvalue = tvValue.split("-")

                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if (prefix.isNotEmpty()) {
                        one = prefix + one
                    }
                    tvInput?.text = (one.toDouble() - two.toDouble()).toString()
                }else if(tvValue.contains("+")) {
                    val splitvalue = tvValue.split("+")

                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if (prefix.isNotEmpty()) {
                        one = prefix + one
                    }
                    tvInput?.text = (one.toDouble() + two.toDouble()).toString()
                }else if(tvValue.contains("*")) {
                    val splitvalue = tvValue.split("*")

                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if (prefix.isNotEmpty()) {
                        one = prefix + one
                    }
                    tvInput?.text = (one.toDouble() * two.toDouble()).toString()
                }else if(tvValue.contains("/")) {
                    val splitvalue = tvValue.split("/")

                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if (prefix.isNotEmpty()) {
                        one = prefix + one
                    }
                    tvInput?.text = (one.toDouble() / two.toDouble()).toString()
                }


            }catch(e:ArithmeticException){
                e.printStackTrace()

            }
        }
    }
    
    private fun isOperatorAdded(value:String):Boolean{
        return if(value.startsWith("-")){
            false
        }else{
            value.contains("/")
                    || value.contains("*")
                    || value.contains("+")
                    || value.contains("-")
        }
    }


}
/*
private fun String.contains(s: String): Boolean {

}

private fun CharSequence?.let(function: () -> Unit) {

}



private fun String.startsWith(s: String): Boolean {

}*/
