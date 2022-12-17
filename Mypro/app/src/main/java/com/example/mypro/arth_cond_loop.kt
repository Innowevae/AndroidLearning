package com.example.mypro

fun main(){
    var moduloResult = 14%5
    println( moduloResult)

    val isEqual = 5==3
    println("isEqual is " + isEqual)
    println("is5LowerEqual5 ${5 >= 5}")

    var num=8
    print("increment ${num++}")
    print("increment ${++num}")

    //conditional
    var voteage =18
    var driveage=16
    var myage=15

    if(myage>=voteage){
        print("u can vote")
    }
    else if(myage>=driveage){
        print("u can drive")
    }else{
        print(" \ntoo young")
    }

    //when statement
    var month=8
    when(month){
        in 3..6->print("M")
        in 7..10->print("A")
        in 11 downTo 2->print("W")
        else -> print("S")
    }

    //loops

    for(num in 1..9) {
        print("$num ")
    }

    for(num1 in 12 downTo 9){
        print("$num1")
    }
}