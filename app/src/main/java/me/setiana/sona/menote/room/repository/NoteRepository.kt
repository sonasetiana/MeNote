package me.setiana.sona.menote.room.repository

import android.app.Application
import android.view.Display
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.setiana.sona.menote.room.AppDatabase
import me.setiana.sona.menote.room.dao.NoteDao
import me.setiana.sona.menote.room.entity.Note

class NoteRepository(application: Application) {
    private val noteDao : NoteDao?
    private var notes : LiveData<List<Note>>? = null

    init {
        val db = AppDatabase.getInstance(application.applicationContext)
        noteDao = db?.noteDao()
        notes = noteDao?.getNotes()
    }

    fun getNotes() : LiveData<List<Note>>? = notes

    fun insertNote(note: Note) = runBlocking {
        this.launch(Dispatchers.IO) {
            noteDao?.insertNote(note)
        }
    }

    fun deleteNote(note: Note) = runBlocking {
        this.launch(Dispatchers.IO){
            noteDao?.deleteNote(note)
        }
    }

    fun updateNote(note: Note) = runBlocking {
        this.launch(Dispatchers.IO) {
            noteDao?.updateNote(note)
        }
    }

}