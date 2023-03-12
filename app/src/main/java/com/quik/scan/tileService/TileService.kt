package com.quik.scan.tileService

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Tiles: TileService() {

    override fun onClick() {
        val user = Firebase.auth.currentUser
        val link = if (user != null){
            "https://quikscan.page.link/fromTile"
        } else {
            "https://quikscan.page.link/loginRedirect"
        }
        super.onClick()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(intent)
    }

}