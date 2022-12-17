package com.example.mypro

fun main(){
    var user1=user(1,"Rit")
    var name1=user1.name
    print(name1)
    var user2=user(2,"sahu")
    print("user2 detail $user2")
    var update=user1.copy(name="Ritvik")
    print(update)
    print(update.component1())
    print(update.component2())
}

data class user(var id:Int,var name:String)