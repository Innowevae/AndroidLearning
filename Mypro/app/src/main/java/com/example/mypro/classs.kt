package com.example.mypro

fun main(){
    var obj1=parent("ritvik","sahu",22)
    obj1.myhobby()
    obj1.hobby="cricket"
    obj1.myhobby()

    //data

    //val user1= User(id:1,name:"Rit")
    //val name1=ser1.name
    //print(name1)
}

class parent(firstname:String,lastname:String){
    //member variable
    var age: Int? = null
    var hobby: String = "Watch Netflix"
    //var myFirstName = firstName
    init {
        print("person created with name $firstname $lastname")
    }
    constructor(firstName: String, lastName: String, age: Int):
            this(firstName, lastName){
                this.age=age
            }

      //member function
    fun myhobby(){
        print("\nmy hobby is  $hobby")
    }

    // data class
    //data class User(var id:Int,var name:String)
}
