package com.example.votinganon

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {
    private lateinit var etPhone: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvAdminStatus: TextView
    private lateinit var btnToggle: Button

    private val db = FirebaseFirestore.getInstance()
    private var loggedIn = false
    private var votingOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        etPhone = findViewById(R.id.etPhone)
        btnLogin = findViewById(R.id.btnLogin)
        tvAdminStatus = findViewById(R.id.tvAdminStatus)
        btnToggle = findViewById(R.id.btnToggle)

        btnLogin.setOnClickListener { attemptLogin() }
        btnToggle.setOnClickListener { toggleVoting() }

        listenStatus()
    }

    private fun attemptLogin() {
        val input = etPhone.text.toString().trim()
        if (input.isEmpty()) {
            etPhone.error = "Masukkan nomor admin atau pasundan:pasundan"
            return
        }
        loggedIn = input == Constants.ADMIN_PIN ||
                   Constants.ADMINS.contains(input) ||
                   input == "${Constants.ADMIN_USER}:${Constants.ADMIN_PASS}"
        updateUi()
        Toast.makeText(this, if (loggedIn) "Login sukses" else "Kredensial salah", Toast.LENGTH_SHORT).show()
    }

    private fun toggleVoting() {
        if (!loggedIn) return
        val newStatus = !votingOpen
        db.collection("settings").document("status").set(mapOf("open" to newStatus))
        Toast.makeText(this, "Voting ${if (newStatus) "dibuka" else "ditutup"}", Toast.LENGTH_SHORT).show()
    }

    private fun listenStatus() {
        db.collection("settings").document("status").addSnapshotListener { doc, _ ->
            if (doc != null && doc.exists()) {
                votingOpen = doc.getBoolean("open") ?: false
                updateUi()
            }
        }
    }

    private fun updateUi() {
        tvAdminStatus.text = "Status voting: ${if (votingOpen) "Terbuka" else "Tertutup"} | Admin: $loggedIn"
        btnToggle.isEnabled = loggedIn
    }
}
