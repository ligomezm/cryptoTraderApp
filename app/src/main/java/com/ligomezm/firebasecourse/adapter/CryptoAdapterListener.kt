package com.ligomezm.firebasecourse.adapter

import com.ligomezm.firebasecourse.model.Crypto

interface CryptoAdapterListener {

    fun onBuyCryptoClicked(crypto: Crypto)
}