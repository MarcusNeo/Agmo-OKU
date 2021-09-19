package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewStub
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAdminPendingEventsBinding
import my.edu.tarc.okuappg11.databinding.ActivityEventsRecordBinding
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList
import my.edu.tarc.okuappg11.recyclerview.EventsAdapter
import my.edu.tarc.okuappg11.recyclerview.PendingEventCardArrayList
import my.edu.tarc.okuappg11.recyclerview.PendingEventsAdapter

class AdminPendingEvents : AppCompatActivity() {
    private lateinit var binding: ActivityAdminPendingEventsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var pendingEventsRecordList: ArrayList<PendingEventCardArrayList>
    private lateinit var eventsRecordAdapter: PendingEventsAdapter

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var viewStubPendingEvents: ViewStub

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPendingEventsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewStubPendingEvents = binding.viewStubPending
        viewStubPendingEvents.visibility = View.GONE

        supportActionBar?.title = "Pending Events"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))


        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        supportActionBar?.title = "Pending Event"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = binding.rvAdminPendingEvents
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE

        pendingEventsRecordList = arrayListOf()
        eventsRecordAdapter = PendingEventsAdapter(pendingEventsRecordList)

        recyclerView.adapter = eventsRecordAdapter

        //var adapter = VolunteerRequestAdapter(volunteerRequestArrayList)
        //recyclerView.adapter = adapter

        getData()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                if(pendingEventsRecordList.isEmpty()){
                    viewStubPendingEvents.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    viewStubPendingEvents.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }, 1000)

    }

    private fun getData(){


//        val collectionReference = fStore.collection("events")
//            .document("S7hf0mgrmj0cEFHJxL3h")
//            .collection("requestVolunteer")
//
//        collectionReference.addSnapshotListener{ snapshot, e->
//            Toast.makeText(this, "It Runs here", Toast.LENGTH_SHORT).show()
//            if(e!=null){
//                Toast.makeText(this, "$e", Toast.LENGTH_LONG).show()
//                return@addSnapshotListener
//            }
//            if(snapshot != null){
//                Toast.makeText(this, "It Runs here2", Toast.LENGTH_SHORT).show()
//                volunteerRequestArrayList = arrayListOf()
//                val document = snapshot.documents
//                document.forEach{
//                    val volunteerDetails = it.toObject(VolunteerRequestArrayList::class.java)
//                    if(volunteerDetails != null){
//                        volunteerDetails.vid = it.id
//                        volunteerRequestArrayList.add(volunteerDetails)
//                    }
//                }
//                volunteerRequestAdapter.notifyDataSetChanged()
//                Toast.makeText(this, "It Runs here3", Toast.LENGTH_SHORT).show()
//            }
//        }

        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    pendingEventsRecordList.clear()
                    value?.forEach{
                        val eventDetails = it.toObject(PendingEventCardArrayList::class.java)
                        if(eventDetails != null){
                            if(it.getString("status").toString() == "pending"){
                                Log.d("Check",eventDetails.eventTitle)
                                eventDetails.eventId = it.id
                                eventDetails.eventTitle = it.getString("eventName").toString()
                                eventDetails.imageUri = it.getString("eventThumbnailURL").toString()
                                eventDetails.eventTime = it.getString("startTime").toString()
                                eventDetails.eventLocation = it.getString("eventLocation").toString()
                                eventDetails.eventDate = it.getString("startDate").toString()
                                eventDetails.eventStatus = it.getString("status").toString()
                                pendingEventsRecordList.add(eventDetails)
                            }
                           pendingEventsRecordList.removeAll{
                               it.status == "accepted"
                               it.status == "rejected"
                           }
                        }
                    }
//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            val volunteerDetails = dc.document.toObject( VolunteerRequestArrayList::class.java)
//                            volunteerDetails.vid = value.documents.toString()
//                            volunteerRequestArrayList.add(dc.document.toObject( VolunteerRequestArrayList::class.java))
//                        }
//                    }
                    eventsRecordAdapter.notifyDataSetChanged()
                }
            })


        if(pendingEventsRecordList.isEmpty()){
            Log.d("try again","Array list is empty")
        } else {
            Log.d("Got array","Array list is not empty")
        }

    }

    override fun onResume() {
        super.onResume()
        if(pendingEventsRecordList.isEmpty()){
            viewStubPendingEvents.visibility = View.VISIBLE
        } else {
            viewStubPendingEvents.visibility = View.GONE
        }
    }

}
