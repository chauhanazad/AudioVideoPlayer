package com.example.audiovideoplayerdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovideoplayerdemo.R
import com.example.audiovideoplayerdemo.model.Music

class MusicAdapter(private val context: Context, val musicList: ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter.MusicHolder>(){

    var onClick: ((Int)->Unit)? = null
    inner class MusicHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val title: TextView = itemView.findViewById(R.id.name)
        private val container: ConstraintLayout = itemView.findViewById(R.id.container)
        init {
            container.setOnClickListener {
                onClick?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_music,parent,false)
        return MusicHolder(view)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.title.text = musicList[position].title
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}