package com.example.infogempaappmanual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class BerandaFragment : Fragment() {

    private val gempaList = ArrayList<Gempa>()
    private lateinit var gempaAdapter: GempaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_gempa)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false // Penting untuk NestedScrollView
        gempaAdapter = GempaAdapter(gempaList)
        recyclerView.adapter = gempaAdapter

        fetchData(view)
    }

    private fun fetchData(view: View) {
        // Inisialisasi semua elemen UI dari Dashboard di sini
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val tvHighlightWilayah: TextView = view.findViewById(R.id.tv_highlight_wilayah)
        val tvHighlightMagnitudo: TextView = view.findViewById(R.id.tv_highlight_magnitudo)
        val tvHighlightWaktu: TextView = view.findViewById(R.id.tv_highlight_waktu)
        val tvStatsTotal: TextView = view.findViewById(R.id.tv_stats_total)
        val tvStatsTerbesar: TextView = view.findViewById(R.id.tv_stats_terbesar)

        progressBar.visibility = View.VISIBLE
        val url = "https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    gempaList.clear()
                    val gempaArray = response.getJSONObject("Infogempa").getJSONArray("gempa")

                    for (i in 0 until gempaArray.length()) {
                        val gempaObject = gempaArray.getJSONObject(i)
                        gempaList.add(
                            Gempa(
                                tanggal = gempaObject.optString("Tanggal", "-"),
                                jam = gempaObject.optString("Jam", "-"),
                                magnitudo = gempaObject.optString("Magnitude", "-"),
                                kedalaman = gempaObject.optString("Kedalaman", "-"),
                                wilayah = gempaObject.optString("Wilayah", "-"),
                                dirasakan = gempaObject.optString("Dirasakan", "-")
                            )
                        )
                    }

                    // MENGISI DATA KE DASHBOARD

                    val gempaTerbaru = gempaList.firstOrNull()
                    if (gempaTerbaru != null) {
                        tvHighlightWilayah.text = gempaTerbaru.wilayah
                        tvHighlightMagnitudo.text = "M ${gempaTerbaru.magnitudo}"
                        tvHighlightWaktu.text = "${gempaTerbaru.tanggal} | ${gempaTerbaru.jam}"
                    }

                    tvStatsTotal.text = gempaList.size.toString()
                    val magnitudoTerbesar = gempaList.maxByOrNull { it.magnitudo.toDoubleOrNull() ?: 0.0 }
                    tvStatsTerbesar.text = magnitudoTerbesar?.magnitudo ?: "-"

                    // Mengisi Daftar RecyclerView
                    gempaAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

                } catch (e: JSONException) {
                    progressBar.visibility = View.GONE
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