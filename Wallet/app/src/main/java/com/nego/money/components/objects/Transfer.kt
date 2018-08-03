package com.nego.money.components.objects

import java.util.ArrayList

import io.realm.RealmObject

/**
 * Created by tommaso on 26/07/17.
 */

class Transfer : RealmObject() {
    private val id: String? = null
    private val amount: Float = 0.toFloat()
    private val creation_date: Long = 0
    private val completion_date: Long = 0
    private val notes: String? = null
    private val direction: Boolean = false
    private val people = ArrayList<String>()

}
