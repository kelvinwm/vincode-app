package com.learnonline.online

import android.app.Application
import android.graphics.Typeface
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.learnonline.online.utils.Contants.*


class ApplicationController : Application() {

    companion object {
        const val TAG = "ApplicationController"
        lateinit var typeface: Typeface
    }

    override fun onCreate() {
        super.onCreate()
//        typeface = Typeface.createFromAsset(assets, "fonts/myFont.ttf")
        val pref = applicationContext.getSharedPreferences(
            "MyPref",
            0
        ) // 0 - for private mode

        val gson = Gson()
        val editor = pref.edit()
        val user = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val myRef = database.getReference(STUDENTS)

        val courseLevel = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    val list = ArrayList<String>()
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue()
                        list.add(post.toString())
                    }

                    val json: String = gson.toJson(list)
                    editor.putString(COURSELEVEL, json);
                    editor.apply()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRef.child("CourseLevel").addValueEventListener(courseLevel)

        //COURSES
        val courses = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val list = ArrayList<String>()
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue()
                        list.add(post.toString())
                    }

                    val json: String = gson.toJson(list)
                    editor.putString(COURSES, json);
                    editor.apply()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRef.child("Courses").addValueEventListener(courses)

        //Grade
        val grade = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val list = ArrayList<String>()
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue()
                        list.add(post.toString())
                    }

                    val json: String = gson.toJson(list)
                    editor.putString(GRADE, json);
                    editor.apply()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRef.child("Grade").addValueEventListener(grade)

        //Level
        val level = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val list = ArrayList<String>()
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue()
                        list.add(post.toString())
                    }
                    val json: String = gson.toJson(list)
                    editor.putString(LEVEL, json);
                    editor.apply()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRef.child("Level").addValueEventListener(level)

    }

}