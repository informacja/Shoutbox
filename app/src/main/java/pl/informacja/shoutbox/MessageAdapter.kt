package pl.informacja.shoutbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.informacja.shoutbox.model.Message
import com.informacja.shoutbox.R
import kotlinx.android.synthetic.main.message_row.view.*

class MainAdapter(val shoutboxContentList: ArrayList<Message>):  RecyclerView.Adapter<CustomViewHolder>() {

    val peopleWhoSentMessage = listOf<String>("Adam","Maciek","Tomek","Romek","Bob","Damian")

    override fun getItemCount(): Int {
        return shoutboxContentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val callForRow = layoutInflater.inflate(R.layout.message_row,parent,false)
        return CustomViewHolder(callForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val shoutboxContent = shoutboxContentList.get(position)
        val shoutboxContent = shoutboxContentList.get(shoutboxContentList.count()-position-1)
        holder?.itemView?.name_textView_row?.text = shoutboxContent.login
        holder?.itemView?.message_textView_row?.text = shoutboxContent.content
        holder?.itemView?.date_textView_row?.text = shoutboxContent.date
        holder?.itemView?.id_textView_row?.text = shoutboxContent.id
    }

    fun currentId(position: Int): String? {
        return shoutboxContentList[shoutboxContentList.count()-position-1].id
    }

    fun currentName(position: Int): String? {
        return shoutboxContentList[shoutboxContentList.count()-position-1].login
    }

    //for swipeToDelete
    fun removeAt(position: Int) {
        shoutboxContentList.removeAt(position)
        notifyItemRemoved(position)
    }

}

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v) {

}