package com.chillarcards.privatepractice.di.module


object ConfigBuild {

    // Field from build type: debug
    val DEBUG: Boolean = java.lang.Boolean.parseBoolean("true")

    // Field from build type: debug
    const val AUTH_KEY = "lynACGsaX4HoZ8QvSjy0"

    // Field from build type: debug
    const val AUTH_SECRET = "wjQtgB2jMWbhQdCIZmqX"

    //Need to change MainActivity  page for installation priority immediate or flexi
    // Field from build type: debug
//    const val BASE_URL = "http://35.154.16.95/api/v1/" //AWS
//    const val BASE_URL = "http://139.59.76.214:8081/api/v1/"   //DIGITALOCEAN
    const val BASE_URL = "http://booking.chillarpayments.com:8081/api/v1/"

}
