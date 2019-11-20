package edu.javeriana.ratatouille_chef_app.core

import android.location.Location
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


fun distanceTo(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val loc1 = Location("")
    loc1.latitude = lat1
    loc1.longitude = lon1

    val loc2 = Location("")
    loc2.latitude = lat2
    loc2.longitude = lon2

    return loc1.distanceTo(loc2)
}


interface HasId {
    var id : String
}

inline fun <reified T : HasId> DocumentSnapshot.toObjectWithId(): T {
    return this.toObject(T::class.java)!!.also {
        it.id = this.id
    }
}

inline fun <reified T : HasId> QuerySnapshot.toObjectsWithId(): List<T> {
    return this.documents.map {
        it.toObjectWithId<T>()
    }
}