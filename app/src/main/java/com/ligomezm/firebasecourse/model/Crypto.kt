package com.ligomezm.firebasecourse.model

class Crypto(
    var name: String = "",
    var imageUrl: String = "",
    var available: Int = 0) {

    fun getDocumentId(): String {
        return name.lowercase()
    }
}