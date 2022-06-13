package com.ligomezm.firebasecourse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.ligomezm.firebasecourse.adapter.CryptoAdapterListener
import com.ligomezm.firebasecourse.adapter.CryptosAdapter
import com.ligomezm.firebasecourse.model.Crypto
import com.ligomezm.firebasecourse.model.User
import com.ligomezm.firebasecourse.network.Callback
import com.ligomezm.firebasecourse.network.FirestoreService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception

class TraderActivity: AppCompatActivity(), CryptoAdapterListener {

    lateinit var firestoreService: FirestoreService
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)
    private var username: String? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras?.get(USERNAME_KEY).toString()
        usernameTextView.text = username

        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
        }

    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        TODO("Not yet implemented")
    }

    fun loadCryptos(){
        firestoreService.getCryptos(object: Callback<List<Crypto>>{
            override fun onSuccess(cryptoList: List<Crypto>?) {
                firestoreService.findUserById(username!!, object: Callback<User>{
                    override fun onSuccess(result: User?) {
                        user = result
                        if(user?.cryptoList == null){
                            val userCryptoList = mutableListOf<Crypto>()

                            for (crypto in cryptoList!!) {
                                val cryptoUser = Crypto()
                                cryptoUser.name = crypto.name
                                cryptoUser.available = crypto.available
                                cryptoUser.imageUrl = crypto.imageUrl
                                userCryptoList.add(cryptoUser)
                            }
                            user?.cryptoList = userCryptoList
                            firestoreService.updateUser(user!!, callback = null)
                        }
                        loadUserCryptos()
                    }

                    override fun onFailed(exception: Exception) {
                        showGeneralServerErrorMessage()
                    }

                })

                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptoList = cryptoList!!
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception) {
                Log.e("TraderActivity", "error loading criptos", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun loadUserCryptos() {
        runOnUiThread {
            if (user != null && user!!.cryptoList != null){
                infoPanel.removeAllViews()
                for (crypto in user!!.cryptoList!!){
                    addUserCryptoInfoRow(crypto)
                }
            }

        }
    }

    fun addUserCryptoInfoRow(crypto: Crypto) {
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text =
            getString(R.string.coin_info, crypto.name, crypto.available.toString())
       Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)
    }
}