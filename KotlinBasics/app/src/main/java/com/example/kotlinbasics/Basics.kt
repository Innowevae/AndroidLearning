package com.example.kotlinbasics

fun main(){
    var myName = "Anshul" //string //mutable we can changed after creation and assign
    myName = "Ansh"
    var myAge = 21  //int
    print(myName+" "+myAge)

    val myNam = "Ans" //immutable variable
    println("hello  "+myNam)


    // single line comment
    /*multi line comment*/


    //integer Type: byte(8bit), short(16bit),
    //Int(32 bit), Long(64 bit)

    val myByte: Byte = 12
    val myShort: Short = 1425
    val myInt: Int = 12345678
    val myLong: Long = 39_33_44_56_88

    //Floating Point number Type: Float(32 bit), Double(64bit)
    val myFloat: Float = 13.3F
    val myDouble: Double = 3.3333333333

    //Booleans has two possible value true or false and represent logical value
    var isSunny = true
    isSunny = false

    //Character
    val letterChar = 'A'
    val digiChar = '1'

    //String
    val myStr = "hello"
    var firschar = myStr[0]
    var lastchar = myStr[myStr.length-1]
    print("first character "+firschar+" last character $lastchar" +lastchar)
    var myLength = myStr.length // first type

    print("first character $firschar last character ${myStr.length} ") //second type
}