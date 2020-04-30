package com.learnonline.online

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learnonline.online.utils.Contants
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val user = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val myRef = database.getReference(Contants.STUDENTS)
//        val pgb = ProgressDialog(this@SplashActivity)
//        pgb.setMessage("Loading please wait...")
//        pgb.setCanceledOnTouchOutside(false)
//        pgb.show()

        if (user == null) {
            Timer("SettingUp", false).schedule(500) {
                //                pgb.dismiss()
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        } else {

            val profileListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    if (dataSnapshot.exists()) {
//                        pgb.dismiss()
                        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
//                        pgb.dismiss()
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.e("LoadPost", ":onCancelled", databaseError.toException())
                    // ...
                }
            }

            myRef.child(Contants.PROFILES).child(user!!.uid)
                .addListenerForSingleValueEvent(profileListener)

        }
    }
}
