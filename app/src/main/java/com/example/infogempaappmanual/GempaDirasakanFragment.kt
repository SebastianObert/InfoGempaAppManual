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

import com.example.infogempaappmanual.Gempa
import com.example.infogempaappmanual.GempaAdapter
import com.example.infogempaappmanual.R

class GempaDirasakanFragment : Fragment() {

    private val gempaDirasakanList = ArrayList<Gempa>()
    private lateinit var gempaAdapter: GempaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gempa_dirasakan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_gempa)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        gempaAdapter = GempaAdapter(gempaDirasakanList)
        recyclerView.adapter = gempaAdapter

        fetchData(progressBar)
    }

    private fun fetchData(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        val url = "https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    gempaDirasakanList.clear()
                    val gempaArray = response.getJSONObject("Infogempa").getJSONArray("gempa")

                    for (i in 0 until gempaArray.length()) {
                        val gempaObject = gempaArray.getJSONObject(i)

                        // Menggunakan .optString agar lebih aman
                        val dirasakan = gempaObject.optString("Dirasakan", "tidak dirasakan")

// Filter yang benar: Cek jika nilai BUKAN "tidak dirasakan".
// Ini akan mencakup semua nilai seperti "II MMI", "III-IV MMI", dll.
                        if (dirasakan != "tidak dirasakan") {
                            gempaDirasakanList.add(
                                Gempa(
                                    tanggal = gempaObject.optString("Tanggal", "-"),
                                    jam = gempaObject.optString("Jam", "-"),
                                    magnitudo = gempaObject.optString("Magnitude", "-"),
                                    kedalaman = gempaObject.optString("Kedalaman", "-"),
                                    wilayah = gempaObject.optString("Wilayah", "-"),
                                    dirasakan = dirasakan
                                )
                            )
                        }
                    }

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