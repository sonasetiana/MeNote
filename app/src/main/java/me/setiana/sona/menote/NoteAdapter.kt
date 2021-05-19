package me.setiana.sona.menote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.setiana.sona.menote.databinding.ItemNoteBinding
import me.setiana.sona.menote.room.entity.Note

class NoteAdapter(private val context: Context?, private val callback : (Note, Int) -> Unit) :
RecyclerView.Adapter<NoteAdapter.Holder>(){

    private var items = listOf<Note>()

    fun set(items: List<Note>){
        this.items = items
        notifyDataSetChanged()
    }

    class Holder(private val binding : ItemNoteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(context: Context?, item: Note, callback: (Note, Int) -> Unit) = with(binding){
            txtNote.text = item.note
            cardNote.setOnClickListener { callback(item, layoutPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemNoteBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, items[position], callback)
    }

    override fun getItemCount(): Int = items.size
}