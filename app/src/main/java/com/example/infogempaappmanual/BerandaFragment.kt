package com.example.infogempaappmanual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class BerandaFragment : Fragment() {

    // List untuk menyimpan data gempa yang akan ditampilkan
    private val gempaList = ArrayList<Gempa>()
    // Deklarasi adapter
    private lateinit var gempaAdapter: GempaAdapter

    // Metode ini hanya untuk membuat tampilan (layout) dari file XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    // Metode ini dipanggil setelah tampilan selesai dibuat. Di sinilah kita menaruh logika.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi Views dari layout
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_gempa)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)

        // 2. Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context) // Mengatur agar list tampil vertikal
        gempaAdapter = GempaAdapter(gempaList) // Membuat instance adapter dengan list kosong
        recyclerView.adapter = gempaAdapter // Menghubungkan adapter ke RecyclerView

        // 3. Panggil metode untuk mengambil data dari internet
        fetchData(progressBar)
    }

    private fun fetchData(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        val url = "https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    // Hapus data lama dari list
                    gempaList.clear()

                    // === PERBAIKAN UTAMA ADA DI SINI ===
                    // Ambil "gempa" sebagai JSONObject, bukan JSONArray
                    val gempaObject = response.getJSONObject("Infogempa").getJSONObject("gempa")

                    // Tidak perlu loop, langsung tambahkan satu objek gempa ke list
                    gempaList.add(
                        Gempa(
                            tanggal = gempaObject.getString("Tanggal"),
                            jam = gempaObject.getString("Jam"),
                            magnitudo = gempaObject.getString("Magnitude"),
                            kedalaman = gempaObject.getString("Kedalaman"),
                            wilayah = gempaObject.getString("Wilayah"),
                            dirasakan = gempaObject.getString("Dirasakan")
                        )
                    )
                    // ===================================

                    gempaAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } catch (e: JSONException) {
                    progressBar.visibility = View.GONE
                    // Tampilkan pesan error yang lebih detail untuk debugging
                    Toast.makeText(context, "Gagal mem-parsing data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }
}