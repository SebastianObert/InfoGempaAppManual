package com.example.infogempaappmanual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// === TAMBAHKAN ATAU PASTIKAN BARIS DI BAWAH INI ADA ===
import com.example.infogempaappmanual.R
class GempaAdapter(private val gempaList: List<Gempa>) : RecyclerView.Adapter<GempaAdapter.GempaViewHolder>() {

    // 1. ViewHolder: Kelas ini 'memegang' referensi ke setiap view di dalam item_gempa.xml.
    // Tujuannya agar tidak perlu memanggil findViewById berulang kali, sehingga lebih efisien.
    class GempaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWilayah: TextView = itemView.findViewById(R.id.tv_wilayah)
        val tvWaktu: TextView = itemView.findViewById(R.id.tv_waktu)
        val tvMagnitudo: TextView = itemView.findViewById(R.id.tv_magnitudo)
        val tvKedalaman: TextView = itemView.findViewById(R.id.tv_kedalaman)
    }

    // 2. onCreateViewHolder: Dipanggil saat RecyclerView perlu membuat ViewHolder baru.
    // Ini terjadi ketika item baru muncul di layar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GempaViewHolder {
        // Mengubah layout item_gempa.xml menjadi sebuah object View.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gempa, parent, false)
        return GempaViewHolder(view)
    }

    // 3. onBindViewHolder: Dipanggil untuk menghubungkan data dari list (objek Gempa)
    // ke dalam view yang ada di ViewHolder.
    override fun onBindViewHolder(holder: GempaViewHolder, position: Int) {
        // Mengambil satu objek Gempa dari list berdasarkan posisinya.
        val gempa = gempaList[position]

        // Mengisi setiap TextView di dalam ViewHolder dengan data dari objek Gempa.
        holder.tvWilayah.text = gempa.wilayah
        holder.tvWaktu.text = "${gempa.tanggal} | ${gempa.jam}"
        holder.tvMagnitudo.text = "Magnitudo: ${gempa.magnitudo}"
        holder.tvKedalaman.text = "Kedalaman: ${gempa.kedalaman}"
    }

    // 4. getItemCount: Memberitahu RecyclerView berapa total item yang ada di dalam list.
    override fun getItemCount(): Int {
        return gempaList.size
    }
}