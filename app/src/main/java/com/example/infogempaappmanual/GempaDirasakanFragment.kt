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

        recyclerView.isNestedScrollingEnabled = false

        recyclerView.layoutManager = LinearLayoutManager(context)
        gempaAdapter = GempaAdapter(gempaDirasakanList)
        recyclerView.adapter = gempaAdapter

        fetchData(view)
    }

    private fun fetchData(view: View) {
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val tvEmptyMessage: TextView = view.findViewById(R.id.tv_empty_message)

        tvEmptyMessage.text = "Tidak ada data gempa signifikan (M ≥ 5.0) saat ini."
        progressBar.visibility = View.VISIBLE
        tvEmptyMessage.visibility = View.GONE
        val url = "https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    gempaDirasakanList.clear()

                    val infogempaObject = response.optJSONObject("Infogempa")

                    if (infogempaObject != null) {
                        val gempaArray = infogempaObject.optJSONArray("gempa")

                        if (gempaArray != null) {
                            for (i in 0 until gempaArray.length()) {
                                val gempaObject = gempaArray.getJSONObject(i)

                                val magnitudo = gempaObject.optDouble("Magnitude", 0.0)

                                if (magnitudo >= 5.0) {
                                    gempaDirasakanList.add(
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
                            }
                        }
                    }

                    gempaAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

                    if (gempaDirasakanList.isEmpty()) {
                        tvEmptyMessage.visibility = View.VISIBLE
                    } else {
                        tvEmptyMessage.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Terjadi kesalahan parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }
}