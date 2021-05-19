package me.setiana.sona.menote.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.setiana.sona.menote.room.entity.Note

@Dao
interface NoteDao {
    @Query("Select * from note_table")
    fun getNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)
}