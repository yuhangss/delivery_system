package com.example.delivery.common.bean

class UserDTO constructor(name: String, id: String, location: String, faceurl: String, sex: String){
    var name = ""
    var id = ""
    var location = ""
    var faceurl = ""
    var sex = ""

    init {
        this.name = name
        this.id = id
        this.location = location
        this.faceurl = faceurl
        this.sex = sex
    }
}