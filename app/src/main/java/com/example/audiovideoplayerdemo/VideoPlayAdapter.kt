package com.example.audiovideoplayerdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovideoplayerdemo.model.MediaData

class VideoPlayAdapter(val context: Context, private val mediaList: ArrayList<MediaData>) : RecyclerView.Adapter<VideoPlayAdapter.VideoPlayHolder>(){

    var onClick: ((Int)->Unit)? = null

    inner class VideoPlayHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val text : TextView = itemView.findViewById(R.id.media_item)
        init {
            text.setOnClickListener {
                onClick?.invoke(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPlayHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.folder_items,parent,false)
        return VideoPlayHolder(view)
    }

    override fun onBindViewHolder(holder: VideoPlayHolder, position: Int) {
        holder.text.text = mediaList[position].title
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}