package com.learnonline.online

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type;


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = 123
    }

    lateinit var modeOfpayment: String
    lateinit var modeOfLearning: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Write a message to the database
        val pref = applicationContext.getSharedPreferences(
            "MyPref", 0
        ) // 0 - for private mode

        val user = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val myRef = database.getReference(STUDENTS)

        val gson = Gson()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            courseLevel.setPopupBackgroundResource(R.color.colorDarkLight);
            level.setPopupBackgroundResource(R.color.colorDarkLight);
            grade.setPopupBackgroundResource(R.color.colorDarkLight);
            course.setPopupBackgroundResource(R.color.colorDarkLight);
        }

        try {


            val type: Type =
                object : TypeToken<List<String?>?>() {}.type

            val courseLv = pref.getString(LEVEL, "")
            val list: List<String> = gson.fromJson(courseLv, type)
            val aa = ArrayAdapter(this, R.layout.spinner_item, list)
            level!!.adapter = aa


            val courses = pref.getString(COURSES, "")
            val list2: List<String> = gson.fromJson(courses, type)
            val aaa = ArrayAdapter(this, R.layout.spinner_item, list2)
            course!!.adapter = aaa

            val grade1 = pref.getString(GRADE, "")
            val list3: List<String> = gson.fromJson(grade1, type)
            val aaa3 = ArrayAdapter(this, R.layout.spinner_item, list3)
            grade!!.adapter = aaa3

            val leveL = pref.getString(COURSELEVEL, "")
            val list4: List<String> = gson.fromJson(leveL, type)
            val aaa4 = ArrayAdapter(this, R.layout.spinner_item, list4)
            courseLevel!!.adapter = aaa4

//            val leveL = pref.getString(LEVEL, "")
//            val list4: List<String> = gson.fromJson(leveL, type)
//            val aaa4 = ArrayAdapter(this, R.layout.spinner_item,  list4)
//            course!!.adapter = aaa4

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("LoadPost", ":onCancelled", databaseError.toException())
                // ...
            }
        }
        myRef.child(PROFILES).child(user!!.uid).addListenerForSingleValueEvent(profileListener)

        submit.setOnClickListener {

            if (fName.text.toString().trim().isEmpty()) {
                fName.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (lName.text.toString().trim().isEmpty()) {
                lName.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (nId.text.toString().trim().isEmpty()) {
                nId.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (nId.text.toString().trim().isEmpty()) {
                nId.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (email.text.toString().trim().isEmpty()) {
                email.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (phone.text.toString().trim().isEmpty()) {
                phone.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (level.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, "Choose academic level", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (grade.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, "Choose Grade", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (course.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, "Choose course to apply", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            if (courseLevel.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, "Choose course level", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (origin.text.toString().trim().isEmpty()) {
                origin.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (residence.text.toString().trim().isEmpty()) {
                residence.error = "Cannot be empty"
                return@setOnClickListener
            }
            //Mode of Fees Payment
            var id: Int = radioGroup.checkedRadioButtonId
            if (id != -1) { // If any radio button checked from radio group
                // Get the instance of radio button using id
                val radio: RadioButton = findViewById(id)
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
            var id2: Int = radioGroup2.checkedRadioButtonId
            if (id2 != -1) {
                val radio: RadioButton = findViewById(id2)
                modeOfLearning = radio.text.toString()
            } else {
                // If no radio button checked in this radio group
                Toast.makeText(
                    applicationContext, "Choose Mode of learning",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val pg = ProgressDialog(this@MainActivity)
            pg.setMessage("Registering please wait...")
            pg.setCanceledOnTouchOutside(false)
            pg.show()
            InternetCheck(object : InternetCheck.Consumer {
                override fun accept(internet: Boolean?) {

                    if (!(internet!!)) {
                        Toast.makeText(
                            this@MainActivity,
                            "No internet connection",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        pg.dismiss()
                        return
                    }
                }
            })

            myRef.child(PROFILES).child(user!!.uid)
                .setValue(
                    Profile(
                        fName.text.toString().trim(),
                        lName.text.toString().trim(),
                        nId.text.toString().trim(),
                        email.text.toString().trim(),
                        phone.text.toString().trim(),
                        origin.text.toString().trim(),
                        residence.text.toString().trim(),
                        level.selectedItem.toString()

                    )
                ).addOnSuccessListener {

                    myRef.child(STUDENTSCOURSES).child(user!!.uid)
                        .push().setValue(
                            Post(
                                grade.selectedItem.toString(),
                                course.selectedItem.toString(),
                                modeOfpayment,
                                modeOfLearning,
                                courseLevel.selectedItem.toString()
                            )
                        ).addOnSuccessListener {
                            pg.dismiss()
                            Toast.makeText(
                                this@MainActivity,
                                "Registration Successful",
                                Toast.LENGTH_LONG
                            ).show()

//                            val editor = pref.edit()
//                            editor.putString("UserNId", nId.text.toString().trim());
//                            editor.apply()

                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            pg.dismiss()
                            Toast.makeText(
                                this@MainActivity,
                                "Unable to Register, try again later",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                }
                .addOnFailureListener {
                    pg.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "Unable to Register, try again later",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
        }


        //Spinner level
        level.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
//                Toast.makeText(
//                    this@MainActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT
//                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //Spinner grade
        grade.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
//                Toast.makeText(
//                    this@MainActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT
//                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //Spinner course
        course.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
//                Toast.makeText(
//                    this@MainActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT
//                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //Spinner courseLevel
        courseLevel.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
//                Toast.makeText(
//                    this@MainActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT
//                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
}
