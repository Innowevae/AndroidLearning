package com.example.mypro

fun main(){
    myfun()
    println(add(5,3))
    println(avg(2.0,6.0))

}

fun myfun(){
    print("myfun is called")
}

fun add(a:Int,b:Int):Int{
    return(a+b)
}

fun avg(a:Double,b:Double):Double{
    return((a+b)/2)
}