package com.example.votinganon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var btnSetuju: Button
    private lateinit var btnTidak: Button
    private lateinit var etAlasan: EditText
    private lateinit var btnKirim: Button
    private lateinit var btnLihatHasil: Button

    private val db = FirebaseFirestore.getInstance()
    private var pilihan: String? = null
    private var votingTerbuka = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSetuju = findViewById(R.id.btnSetuju)
        btnTidak = findViewById(R.id.btnTidak)
        etAlasan = findViewById(R.id.etAlasan)
        btnKirim = findViewById(R.id.btnKirim)
        btnLihatHasil = findViewById(R.id.btnLihatHasil)

        btnSetuju.setOnClickListener { pilihan = "Setuju" }
        btnTidak.setOnClickListener { pilihan = "Tidak Setuju" }
        btnKirim.setOnClickListener { kirimVoting() }
        btnLihatHasil.setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }

        db.collection("settings").document("status").addSnapshotListener { doc, _ ->
            if (doc != null && doc.exists()) {
                votingTerbuka = doc.getBoolean("open") ?: false
            }
        }
    }

    private fun kirimVoting() {
        val alasan = etAlasan.text.toString().trim()
        if (!votingTerbuka) {
            Toast.makeText(this, "Voting sedang ditutup", Toast.LENGTH_SHORT).show()
            return
        }
        if (pilihan == null) {
            Toast.makeText(this, "Pilih setuju / tidak setuju", Toast.LENGTH_SHORT).show()
            return
        }
        if (alasan.isEmpty()) {
            Toast.makeText(this, "Tuliskan alasan", Toast.LENGTH_SHORT).show()
            return
        }
        val data = mapOf(
            "pilihan" to pilihan,
            "alasan" to alasan,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("voting").add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Voting terkirim", Toast.LENGTH_SHORT).show()
                etAlasan.setText("")
                pilihan = null
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengirim", Toast.LENGTH_SHORT).show()
            }
    }
}
