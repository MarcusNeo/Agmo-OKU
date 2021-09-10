package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_event.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAddEventBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.progressdialog.SignInDialog
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private val dialogAddEvent = AddEventDialog(this)

    private var firestoreCheck:Boolean = false
    private var storageCheck:Boolean = false
    private var mSelectedImageFileUri:Uri? = null
    private var eventId:String? = null
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private var eventDuration:String? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var eventOrganizer:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        getDisplayName()



        binding.textFieldEventOrganizerName.editText!!.isEnabled = false
        binding.textFieldDateStart.editText!!.isEnabled = false
        binding.textFieldTime.editText!!.isEnabled= false



        binding.btnSelectDate.setOnClickListener{
            if(!binding.rbSingleDay.isChecked && !binding.rbMultiday.isChecked){
                Toast.makeText(this, R.string.select_duration_event, Toast.LENGTH_LONG).show()

            }else {
                if (binding.rbSingleDay.isChecked) {
                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build()

                    datePicker.show(supportFragmentManager, "tag")


                    datePicker.addOnPositiveButtonClickListener {
                        eventDuration="1"
                        binding.textFieldDateStart.editText?.setText(datePicker.headerText)
                    }
                }



                if (binding.rbMultiday.isChecked) {
                    val today: Long = MaterialDatePicker.todayInUtcMilliseconds();

                    val todayPair = Pair(today, today)


                    val dateRangePicker =
                        MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select dates")
                            .build()

                    dateRangePicker.show(supportFragmentManager, "tag")
                    dateRangePicker.addOnPositiveButtonClickListener {
                        eventDuration = "2"
                        binding.textFieldDateStart.editText?.setText(dateRangePicker.headerText)
                    }
                }
            }
        }

        binding.btnSelectTime.setOnClickListener{
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select Appointment time")
                    .build()

            picker.show(supportFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, picker.hour)
                cal.set(Calendar.MINUTE, picker.minute)
                val formattedDate = SimpleDateFormat("HH:mm").format(cal.time)
                binding.textFieldTime.editText?.setText(formattedDate)
            }

        }

        binding.ivEventThumbnail.setOnClickListener{
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ){
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)



            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnSubmit.setOnClickListener{
            if (validateEventDetails()) {
                eventName = binding.textFieldEventName.editText!!.text.toString()
                eventDescription = binding.textFieldEventDescription.editText!!.text.toString()
                startDate = binding.textFieldDateStart.editText!!.text.toString()
                startTime = binding.textFieldTime.editText!!.text.toString()

                val hashMapEvents = hashMapOf(
                    "eventName" to eventName,
                    "eventDescription" to eventDescription,
                    "startDate" to startDate,
                    "startTime" to startTime,
                    "eventDuration" to eventDuration,
                    "eventOrganizerName" to eventOrganizer
                )

                dialogAddEvent.startLoading()

                val ref:DocumentReference = fStore.collection("events").document()

                ref.set(hashMapEvents)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Added Document")
                        firestoreCheck=true
                    }
                    .addOnFailureListener {
                        Log.w(ContentValues.TAG, "Error adding document ${it.suppressedExceptions}")
                    }

                eventId = ref.id
                Log.d("check", eventId.toString())
                val sRef:StorageReference = FirebaseStorage.getInstance().reference.child(
                    Constants.USER_PROFILE_IMAGE + eventId + "."
                            + Constants.getFileExtension(
                                this,
                                mSelectedImageFileUri
                            )
                )

                sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                    Log.e("Firebase Image", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL",uri.toString())
                            storageCheck=true
                        }
                        .addOnFailureListener{exception ->
                            Log.e("ERROR", exception.message.toString())
                        }
                }

                val handler = Handler()
                handler.postDelayed(object: Runnable{
                    override fun run() {
                        val intent = Intent(this@AddEventActivity, EventDetailsActivity::class.java)
                        intent.putExtra("EventUID","${eventId.toString()}")
                        startActivity(intent)
                        dialogAddEvent.isDismiss()

                    }
                }, 6000)

            }else{
                Toast.makeText(this, R.string.event_validation_error, Toast.LENGTH_LONG).show()

            }


        }



        eventName = binding.textFieldEventName.editText.toString()
        eventDescription = binding.textFieldEventDescription.editText.toString()




    }

    private fun getDisplayName() {
        val docRef = fStore.collection("users").document(fAuth.currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    eventOrganizer = document.getString("name")
                    binding.textFieldEventOrganizerName.editText!!.setText(eventOrganizer)

                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            //granted
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this,"Storage permission granted", Toast.LENGTH_LONG).show()
                Constants.showImageChooser(this)


            }else{
                //display another toast if permission not granted
                Toast.makeText(this,R.string.storage_permission_denied, Toast.LENGTH_LONG).show()

            }
        }

    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data !=null){
                    try{
                        //uri of image
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivEventThumbnail)
                    }catch(e: Exception){
                        Toast.makeText(this, R.string.image_selection_fail, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }



    }

    private fun validateEventDetails(): Boolean {
        if (binding.textFieldEventName.editText!!.text.isEmpty() ||
            binding.textFieldEventDescription.editText!!.text.isEmpty() ||
            binding.textFieldDateStart.editText!!.text.isEmpty() ||
            binding.textFieldTime.editText!!.text.isEmpty()){
            return false
        }else{
            return true
        }
    }



}