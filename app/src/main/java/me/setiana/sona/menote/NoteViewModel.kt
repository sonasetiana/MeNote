package me.setiana.sona.menote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import me.setiana.sona.menote.room.entity.Note
import me.setiana.sona.menote.room.repository.NoteRepository

class NoteViewModel : ViewModel() {

    private lateinit var repository: NoteRepository

    fun initilize(application: Application){
        repository = NoteRepository(application)
    }

    fun getNotes() = repository.getNotes()

    fun setNote(note: Note) = repository.insertNote(note)

    fun editNote(note: Note) = repository.updateNote(note)

    fun deleteNote(note: Note) = repository.deleteNote(note)

}