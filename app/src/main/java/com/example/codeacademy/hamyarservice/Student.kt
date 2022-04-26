package com.example.codeacademy.hamyarservice

import android.location.Location

/**
 * Created by Code Academy on 3/9/2018.
 */
class Student
{
    var name:String ?= null
    var desc:String ?= null
    var image:Int ?= null
    var location:Location ?= null


    constructor(image:Int, name:String,desc:String, lat:Double,lng:Double)
    {
        this.name = name
        this.desc = desc
        this.image = image
        this.location = Location("Student")
        this.location!!.latitude = lat
        this.location!!.longitude = lng

    }
}

