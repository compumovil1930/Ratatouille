package edu.javeriana.ratatouille_chef_app.core

import android.location.Location



fun distanceTo(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val loc1 = Location("")
    loc1.latitude = lat1
    loc1.longitude = lon1

    val loc2 = Location("")
    loc2.latitude = lat2
    loc2.longitude = lon2

    return loc1.distanceTo(loc2)
}