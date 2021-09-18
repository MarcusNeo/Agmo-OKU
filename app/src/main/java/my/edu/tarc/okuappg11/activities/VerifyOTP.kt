package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R

class VerifyOTP : AppCompatActivity() {
    //lateinit var auth: FirebaseAuth
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var startDate:String? = null
    private var startTime:String? = null
    private var userID: String? = null
    private var eventID: String? = null
    private var eventName:String? = null
    private var participantUID:String? =null
    private var participantName:String? = null
    private var participantEmail:String? = null
    private var participantPhoneNum:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        eventName = intent.getStringExtra("EventName")
        startDate = intent.getStringExtra("StartDate")
        startTime = intent.getStringExtra("StartTime")
        participantName = intent.getStringExtra("ParticipantName")
        participantEmail = intent.getStringExtra("ParticipantEmail")
        participantPhoneNum = intent.getStringExtra("ParticipantPhoneNumber")
        val storedVerificationId=intent.getStringExtra("storedVerificationId")
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid


//        Reference
        val verify=findViewById<Button>(R.id.verifyBtn)
        val otpGiven=findViewById<EditText>(R.id.id_otp)


        verify.setOnClickListener{
            var otp=otpGiven.text.toString().trim()
            if(!otp.isEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    registerEvent(eventID, eventName, startDate, startTime)
                    registerParticipant(eventID, participantName, participantEmail)

                    startActivity(Intent(applicationContext, AllUpcomingEvents::class.java))
                    finish()
// ...
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun registerParticipant(eventId: String?, participantName: String?, participantEmail: String?) {
        val hashmapParticipants = hashMapOf(
            "userUID" to userID,
            "participantName" to participantName.toString(),
            "participantEmail" to participantEmail.toString(),
            "participantPhone" to participantPhoneNum.toString()
        )

        fStore.collection("events").document(eventId!!).collection("participants")
            .document(userID!!)
            .set(hashmapParticipants)
            .addOnSuccessListener {
                Log.d("check", "CHECKADD")

                /*val intent =
                    Intent(this@JoinEvent, VerifyPhone::class.java)
                startActivity(intent)
                finish()*/
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
            }
    }

    private fun registerEvent(eventId: String?, eventName: String?, startDate: String?, startTime: String?){
        val hashmapUpcomingEvents = hashMapOf(
            "eventUID" to eventId.toString(),
            "eventName" to eventName.toString(),
            "startDate" to startDate.toString(),
            "startTime" to startTime.toString()
        )

        fStore.collection("users").document(userID!!).collection("upcomingEvents")
            .document(eventId!!)
            .set(hashmapUpcomingEvents)
            .addOnSuccessListener {
                Log.d("check", "CHECKADD")
                //val intent = Intent(this@EventDetailsActivity, AllUpcomingEvents::class.java)
                //startActivity(intent)
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //val intent = Intent(this@VerifyOTP, VerifyPhone::class.java)
        //startActivity(intent)
    }
}