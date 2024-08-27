package com.chillarcards.bookmenow.utills

import com.chillarcards.bookmenow.data.model.EntityDetail
import com.google.gson.annotations.SerializedName

data class CommonDBaseModel  (

    @SerializedName("MastIDs"   ) var mastIDs   : String?    = null,
    @SerializedName("ItmName"   ) var itmName   : String? = null,
    @SerializedName("Mobile" ) var mobile : String? = null,
    @SerializedName("ValueStr1" ) var valueStr1 : String? = null,
    @SerializedName("ValueStr2" ) var valueStr2 : String? = null,
    @SerializedName("Position" ) var positionVal : Int? = null,
)
fun EntityDetail.toSpinnerItmBaseModel() :CommonDBaseModel {
    return CommonDBaseModel(
        mastIDs = this.entityId.toString(),
        itmName = this.entityName
    )
}

