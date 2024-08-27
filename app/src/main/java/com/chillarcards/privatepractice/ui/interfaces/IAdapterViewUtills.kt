package com.chillarcards.privatepractice.ui.interfaces

import com.chillarcards.privatepractice.utills.CommonDBaseModel

interface IAdapterViewUtills {

    fun getAdapterPosition(Position: Int, ValueArray: ArrayList<CommonDBaseModel>, Mode: String?)
}