package com.example.myapp.chat

class Message {
    var message: String? = null
    var senderId: String? = null
    var date: String? = null
    var time: String? = null

    constructor(){
    }


    constructor(message: String?, senderId: String?, date: String?, time: String?){
        this.message = message
        this.senderId = senderId
        this.date = date
        this.time = time
    }


}