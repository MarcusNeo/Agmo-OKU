package my.edu.tarc.okuappg11.activities

import android.net.Uri
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.utils.GlideLoader

class BookmarkAdapter(private val bookmarkList: ArrayList<BookmarkArrayList>) :
    RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
   // private var _binding: Fragment? = null
   // private val binding get() = _binding!!
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_bookmark, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookmarkAdapter.ViewHolder, position: Int) {
        val bookmarkArrayListItem: BookmarkArrayList = bookmarkList[position]
        holder.eventName.text = bookmarkArrayListItem.eventName.toString()
        holder.eventDate.text = bookmarkArrayListItem.startDate
        holder.eventTime.text = bookmarkArrayListItem.startTime
        holder.eventLocation.text = bookmarkArrayListItem.location.toString()
        GlideLoader(holder.ivBookmark.context).loadUserPicture(Uri.parse(bookmarkArrayListItem.eventThumbnailURL),holder.ivBookmark)

        //val url: Uri? = Uri.parse(bookmarkArrayListItem.eventThumbnailURL)
        //this.context?.let { Glide.with(it).load(url).into(holder.eventThumbnailURL)}

    }


    override fun getItemCount(): Int {
        return bookmarkList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.showTopicName)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescription)
        val eventTime: TextView = itemView.findViewById(R.id.showTime)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocation)
        val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)

    }
}