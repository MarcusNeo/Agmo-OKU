package my.edu.tarc.okuappg11.models

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_item_search.view.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity
import my.edu.tarc.okuappg11.activities.QuitEventActivity
import my.edu.tarc.okuappg11.utils.GlideLoader

class SearchListAdapter(var searchList: ArrayList<SearchModel>): RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var eventID: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.SearchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search, parent, false)
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.SearchListViewHolder, position: Int) {
        val searchListItem: SearchModel = searchList[position]
        holder.name.text = searchListItem.eventName
        holder.id.text = searchListItem.eventID
        holder.eventDate.text = searchListItem.startDate
        holder.eventTime.text = searchListItem.startTime
        holder.eventLocation.text = searchListItem.eventLocation
        GlideLoader(holder.ivSearch.context).loadUserPicture(Uri.parse(searchListItem.eventThumbnailURL),holder.ivSearch)

        val eventIDNumber: String = searchListItem.eventID
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        eventID = holder.id.text.toString()
        userID = fAuth.currentUser!!.uid

        holder.itemView.setOnClickListener(){
            //Toast.makeText(holder.itemView.context,"You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
//            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
//            intent.putExtra("EventUID", eventIDNumber)
//            holder.itemView.context.startActivity(intent)

            val jRef = fStore.collection("users").document(userID!!).collection("upcoming events").document(eventIDNumber)
            jRef.get()
                .addOnSuccessListener { document ->
                    if (document.getString("eventUID") == eventIDNumber) {

                        val intent =
                            Intent(holder.itemView.context, QuitEventActivity::class.java)
                        intent.putExtra("EventUID", "${eventIDNumber}")

                        holder.itemView.context.startActivity(intent)
                    }else{
                        val intent =
                            Intent(holder.itemView.context, EventDetailsActivity::class.java)
                        intent.putExtra("EventUID", "${eventIDNumber}")

                        holder.itemView.context.startActivity(intent)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }
    }

    class SearchListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        fun bind(searchModel: SearchModel){
//            itemView.tvNameSearch.text = searchModel.eventName
//        }
        val name : TextView = itemView.findViewById(R.id.showTopicNameSearch)
        val id : TextView = itemView.findViewById(R.id.tvIDSearch)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescriptionSearch)
        val eventTime: TextView = itemView.findViewById(R.id.showTimeSearch)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocationSearch)
        val ivSearch: ImageView = itemView.findViewById(R.id.ivSearch)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
