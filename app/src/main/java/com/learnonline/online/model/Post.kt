package com.learnonline.online.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    var grade: String? = "",
    var course: String? = "",
    var paymentmode: String? = "",
    var learningmode: String? = "",
    var courselevel: String? = ""

)