package me.setiana.sona.menote

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginStart
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import me.setiana.sona.menote.databinding.ActivityMainBinding
import me.setiana.sona.menote.room.entity.Note

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var noteAdapter : NoteAdapter

    private lateinit var noteViewModel : NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteAdapter = NoteAdapter(this@MainActivity){ note, _ ->
            showAlertMenu(note)
        }
        binding.rvNote.adapter = noteAdapter
        noteViewModel.initilize(application)
        noteViewModel.getNotes()?.observe(this, { notes ->
            noteAdapter.set(notes)
        })

        Log.d("ANDROID_ID", getDeviceId())
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addMenu -> showAlertDialogAdd()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialogAdd() {

        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.dialog_add_note, null)
        val til = layout.findViewById<TextInputLayout>(R.id.til_editext)
        val editText = layout.findViewById<TextInputEditText>(R.id.et_note)

        val alert = MaterialAlertDialogBuilder(this)
                .setTitle("New Note")
                .setView(layout)
                .create()//AlertDialog.Builder(this)



        val btnPositive = layout.findViewById<MaterialButton>(R.id.btn_positive)
        val btnNegative = layout.findViewById<MaterialButton>(R.id.btn_negative)

        btnNegative?.setOnClickListener {
            alert.dismiss()
        }

        btnPositive?.setOnClickListener {
            val txt = editText?.text?.toString()?.trim()
            til?.apply {
                isErrorEnabled = false
                error = null
            }
            if(txt.isNullOrEmpty()) {
                til?.apply {
                    isErrorEnabled = true
                    error = "Note can't be empty String."
                }
                return@setOnClickListener
            }
            noteViewModel.setNote(Note(note = txt))
            alert.dismiss()
        }

        alert.show()
    }

    private fun showAlertMenu(note: Note) {
        val items = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(this)
        builder.setItems(items) { dialog, which ->
            // the user clicked on colors[which]
            when (which) {
                0 -> {
                    showAlertDialogEdit(note)
                }
                1 -> {
                    noteViewModel.deleteNote(note)
                }
            }
        }
        builder.show()
    }

    private fun showAlertDialogEdit(note: Note) {
        val alert = AlertDialog.Builder(this)

        val editText = EditText(applicationContext)
        editText.setText(note.note)

        alert.setTitle("Edit Note")
        alert.setView(editText)

        alert.setPositiveButton("Update") { dialog, _ ->
            val txt = editText.text.toString()
            if(txt.isEmpty()) {
                toast("Note can't be empty String.")
                return@setPositiveButton
            }
            note.note = txt
            noteViewModel.editNote(note)
            dialog.dismiss()
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}