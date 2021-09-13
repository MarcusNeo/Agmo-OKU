package my.edu.tarc.okuappg11.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.okuappg11.activities.Bookmark
import my.edu.tarc.okuappg11.activities.BookmarkAdapter
import my.edu.tarc.okuappg11.activities.BookmarkArrayList
import my.edu.tarc.okuappg11.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var eventLocation:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private var userID: String? = null
    private var eventID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileFragment.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        userID = fAuth.currentUser!!.uid

        binding.btnBookmarks.setOnClickListener {

            val hashmapBookmark = hashMapOf(
                "eventUID" to eventId,
                "eventName" to eventName
            )

            fStore.collection("users").document(userID!!).collection("bookmarks")
                .document(eventId!!)
                .set(hashmapBookmark)
                .addOnSuccessListener {
                    val intent = Intent(this@ProfileFragment, Bookmark::class.java)
                    startActivity(intent)
                }.addOnFailureListener {

                }

        }
    }

    private fun checkUserType(){
        if (user == "Normal User"){
            btnMyPostedEvents.visibility = View.VISIBLE
            btnMyVolunteers.visibility = View.VISIBLE
            btnMyEventHistory.visibility = View.INVISIBLE
            btnAllUpcomingEvents.visibility = View.INVISIBLE

            binding.tvUserType.text = "Normal User"
        }else{
            btnMyPostedEvents.visibility = View.INVISIBLE
            btnMyVolunteers.visibility = View.INVISIBLE
            btnMyEventHistory.visibility = View.VISIBLE
            btnAllUpcomingEvents.visibility = View.VISIBLE

            binding.tvUserType.text = "OKU"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkUserType()
    }
}