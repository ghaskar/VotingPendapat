package com.example.votinganon

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ResultsActivity : AppCompatActivity() {
    private lateinit var tvSummary: TextView
    private lateinit var tvReasons: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        tvSummary = findViewById(R.id.tvSummary)
        tvReasons = findViewById(R.id.tvReasons)

        db.collection("voting").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { it.data }
                val setuju = list.count { it["pilihan"] == "Setuju" }
                val tidak = list.count { it["pilihan"] == "Tidak Setuju" }

                tvSummary.text = "Setuju: $setuju\nTidak Setuju: $tidak"

                val alasanText = list.sortedBy { it["timestamp"] as Long }.mapIndexed { index, it ->
                    "${index + 1}. (${it["pilihan"]}) ${it["alasan"]}"
                }.joinToString("\n\n")
                tvReasons.text = alasanText
            }
        }
    }
}
