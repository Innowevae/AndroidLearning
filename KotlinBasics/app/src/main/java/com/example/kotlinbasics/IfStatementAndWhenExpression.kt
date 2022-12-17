package com.example.kotlinbasics

fun main(){
    // Control Flows
    // If Statements
    var age = 17
    if(age >= 21){
        print("now you may drink in the US")
    }
    // Else If Statement - only executed if the if statement is not true
    else if(age >= 18){
        print("now you may vote")
    }
    // Else If Statement - only executed if the foregoing else if statement is not true
    else if (age >= 16){
        print("you now may drive")
    }
    // else is only executed if all of the foregoing statements weren't true
    else{
        print("you're too young")
    }

    // Kotlin’s "when" expression is the replacement of the switch statement
    // from other languages like C, C++, and Java.
    // It is compact and more powerful than switch statements.
    var season = 3
    when(season) {
        1 -> println("Spring")
        2 -> println("Summer")
        3 -> println("Fall")
        4 -> println("Winter")
        else -> println("Invalid Season")
    }
    var month = 3
    when(month) {
        1,2,3 -> println("Spring")
        in 4..6 -> println("Summer")
        in 7..9 -> println("Fall")
        in 10..12 -> println("Winter")
        else -> println("Invalid Season")
    }

    // challenge - translate the if statement with the age to a when expression
    when(age){
        // with the !in it's the same as saying not in ...
        !in 0..20  -> print("now you may drink in the US")
        in 18..20  -> print("now you may vote")
        16,17 -> print("you now may drive")
        else -> print("you're too young")
    }

    var x : Any = 13.37
    when(x) {
        is Int -> println("$x is an Int")
        !is Double -> println("$x is not Double")
        is String -> println("$x is a String")
        else -> println("$x is none of the above")
    }
}