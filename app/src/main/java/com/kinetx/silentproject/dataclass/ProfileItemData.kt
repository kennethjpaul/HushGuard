package com.kinetx.silentproject.dataclass

data class ProfileItemData(
    val profileId : Long = -1L,

    var profileName : String = "",

    var profileIcon : Int = -1,

    var profileColor : Int = -1,

    var profileChecked : Boolean = false

)
