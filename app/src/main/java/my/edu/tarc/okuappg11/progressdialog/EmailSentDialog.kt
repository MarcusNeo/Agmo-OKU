package my.edu.tarc.okuappg11.progressdialog

import android.app.AlertDialog
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.RegisterFragment

class EmailSentDialog(val fragment: RegisterFragment) {


    private lateinit var isDialog:AlertDialog
    fun startLoading(){
        //Set View
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.emailsent_dialog, null)
        //Set Dialog
        val builder = AlertDialog.Builder(fragment.context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
        isDialog.setCanceledOnTouchOutside(true)
        isDialog.window?.setLayout(1200,1200) //Controlling width and height.
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}