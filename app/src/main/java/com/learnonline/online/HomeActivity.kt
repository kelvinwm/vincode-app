package com.learnonline.online

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learnonline.online.model.Post
import com.learnonline.online.model.Profile
import com.learnonline.online.utils.Contants.*
import com.learnonline.online.utils.InternetCheck
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.add_course_layout.view.*
import java.lang.reflect.Type


class HomeActivity : AppCompatActivity() {
    val TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val pref = applicationContext.getSharedPreferences(
            "MyPref",
            0
        ) // 0 - for private mode
        val user = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val myRef = database.getReference(STUDENTS)
        val postReference =
            database.getReference(STUDENTS).child(PROFILES).child(user!!.uid)
        val pgb = ProgressDialog(this@HomeActivity)
        pgb.setMessage("Loading please wait...")
        pgb.setCanceledOnTouchOutside(false)
        pgb.show()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI

                fullName.text = dataSnapshot.child("fname").value.toString()
                unit.text = dataSnapshot.child("email").value.toString()
                residence.text = dataSnapshot.child("residence").value.toString()
                origin.text = dataSnapshot.child("origin").value.toString()
                eduLevel.text = dataSnapshot.child("level").value.toString()
                phone.text = dataSnapshot.child("phone").value.toString()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        postReference.addListenerForSingleValueEvent(postListener)


        val coursesReference =
            database.getReference(STUDENTS).child(STUDENTSCOURSES).child(user!!.uid)
        val coursesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    courses.text = dataSnapshot.childrenCount.toString()
                    var courses = ""
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        Log.e(TAG, "loadPost:" + post!!.paymentmode.toString())
                        courses =
                            "$courses+Course: ${post.course} \nLearning Mode: ${post.learningmode} \nLevel: ${post.courselevel} \n\n"
                    }
                    studentCourses.text = courses
                    pgb.dismiss()
                } else {
                    pgb.dismiss()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        coursesReference.addValueEventListener(coursesListener)


        addCourse.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_course_layout, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            val gson = Gson()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                mDialogView.courseLevel.setPopupBackgroundResource(R.color.colorDarkLight);
//                mDialogView.level.setPopupBackgroundResource(R.color.colorDarkLight);
//                mDialogView.grade.setPopupBackgroundResource(R.color.colorDarkLight);
//                mDialogView.course.setPopupBackgroundResource(R.color.colorDarkLight);
//            }
            try {

                val type: Type =
                    object : TypeToken<List<String?>?>() {}.type

                val courseLv = pref.getString(LEVEL, "")
                val list: List<String> = gson.fromJson(courseLv, type)
                val aa = ArrayAdapter(this, R.layout.spinner_item_text, list)
                mDialogView.level!!.adapter = aa


                val courses = pref.getString(COURSES, "")
                val list2: List<String> = gson.fromJson(courses, type)
                val aaa = ArrayAdapter(this, R.layout.spinner_item_text, list2)
                mDialogView.course!!.adapter = aaa

                val grade1 = pref.getString(GRADE, "")
                val list3: List<String> = gson.fromJson(grade1, type)
                val aaa3 = ArrayAdapter(this, R.layout.spinner_item_text, list3)
                mDialogView.grade!!.adapter = aaa3

                val leveL = pref.getString(COURSELEVEL, "")
                val list4: List<String> = gson.fromJson(leveL, type)
                val aaa4 = ArrayAdapter(this, R.layout.spinner_item_text, list4)
                mDialogView.courseLevel!!.adapter = aaa4

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

//            mDialogView.profileLayout.visibility = View.GONE
            mDialogView.submit.setOnClickListener {

                if (mDialogView.level.selectedItemPosition == 0) {
                    Toast.makeText(this@HomeActivity, "Choose academic level", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                if (mDialogView.grade.selectedItemPosition == 0) {
                    Toast.makeText(this@HomeActivity, "Choose Grade", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (mDialogView.course.selectedItemPosition == 0) {
                    Toast.makeText(this@HomeActivity, "Choose course to apply", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                if (mDialogView.courseLevel.selectedItemPosition == 0) {
                    Toast.makeText(this@HomeActivity, "Choose course level", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }

                var modeOfpayment = ""
                var modeOfLearning = ""
                //Mode of Fees Payment
                var id: Int = mDialogView.radioGroup.checkedRadioButtonId
                if (id != -1) { // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio: RadioButton =  mDialogView.findViewById(id)
                    modeOfpayment = radio.text.toString()
                } else {
                    // If no radio button checked in this radio group
                    Toast.makeText(
                        applicationContext, "Choose Mode of Fees Payment",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Mode of learning
                var id2: Int = mDialogView.radioGroup2.checkedRadioButtonId
                if (id2 != -1) {
                    val radio: RadioButton =  mDialogView.findViewById(id2)
                    modeOfLearning = radio.text.toString()
                } else {
                    // If no radio button checked in this radio group
                    Toast.makeText(
                        applicationContext, "Choose Mode of learning",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val pg = ProgressDialog(this@HomeActivity)
                pg.setMessage("Registering please wait...")
                pg.setCanceledOnTouchOutside(false)
                pg.show()
                InternetCheck(object : InternetCheck.Consumer {
                    override fun accept(internet: Boolean?) {

                        if (!(internet!!)) {
                            Toast.makeText(
                                this@HomeActivity,
                                "No internet connection",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            pg.dismiss()
                            return
                        }
                    }
                })

                myRef.child(STUDENTSCOURSES).child(user!!.uid)
                    .push().setValue(
                        Post(
                            mDialogView.grade.selectedItem.toString(),
                            mDialogView.course.selectedItem.toString(),
                            modeOfpayment,
                            modeOfLearning,
                            mDialogView.courseLevel.selectedItem.toString()
                        )
                    ).addOnSuccessListener {
                        pg.dismiss()
                        Toast.makeText(
                            this@HomeActivity,
                            "Registration Successful",
                            Toast.LENGTH_LONG
                        ).show()
                        mAlertDialog.dismiss()
                    }
                    .addOnFailureListener {
                        pg.dismiss()
                        Toast.makeText(
                            this@HomeActivity,
                            "Unable to Register, try again later",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                //cancel button click of custom layout
//            mDialogView.dialogCancelBtn.setOnClickListener {
//                //dismiss dialog
//                mAlertDialog.dismiss()
            }
        }

    }
}
