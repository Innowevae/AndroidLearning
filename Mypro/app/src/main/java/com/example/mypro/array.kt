package com.example.mypro

fun main() {
    val num: IntArray = intArrayOf(1, 2, 3)
    val num1 = intArrayOf(5,6,7)
    print(num.contentToString())
    for(i in num1) {
        print(i)
    }
    num[1]=5
    num1[0]=8

    val days= arrayOf("mon","tues","wed")

    //lists

    val list1= ListOf("jan","feb","wed")
    print(list1.size)
    val list2 =list1.toMutableList()
    val list3= ListOf("mar","apr","may")
    list2.addAll(list3)
    list2.add("june")
    print(list2)

    //maps

    val fruits= setOf("mango","apple","mellon")

    val daysweek=mapOf(1 to "mon",2 to "tues",3 to "wed")
    pint(daysweek[3])

    data class fruit(val name:String, val Id:Int)

    val friutss= mapOf("fav" to fruit("apple",1),2 to fruit("orange",2),"ok" to fruit("kiwi",3))

    // lambda

    val sum={a:Int, b:Int -> print(a+b)}
}