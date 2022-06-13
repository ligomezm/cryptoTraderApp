package com.ligomezm.firebasecourse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ligomezm.firebasecourse.model.User
import com.ligomezm.firebasecourse.network.Callback
import com.ligomezm.firebasecourse.network.FirestoreService
import com.ligomezm.firebasecourse.network.USERS_COLLECTION_NAME
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }

    fun onStartClicked(view: View) {
        view.isEnabled = false
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val username = username.text.toString()

                    firestoreService.findUserById(username, object : Callback<User>{
                        override fun onSuccess(result: User?) {
                            if (result == null){
                                val user = User()
                                user.username = username
                                saveUserAndStartMainActivity(user, view)
                            }else {
                                startMainActivity(username)
                            }
                        }
                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }
                    })

                }else {
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            }
    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(user, USERS_COLLECTION_NAME, user.username, object : Callback<Void>{
            override fun onSuccess(result: Void?) {
                startMainActivity(user.username)
            }

            override fun onFailed(exception: Exception) {
                showErrorMessage(view)
                Log.e(TAG, "error", exception)
                view.isEnabled = true
            }
        })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }
}