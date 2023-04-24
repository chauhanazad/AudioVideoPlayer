package com.example.audiovideoplayerdemo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.audiovideoplayerdemo.R
import com.example.audiovideoplayerdemo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var status = false
            it.forEach {
                status =
                    if (it.key == android.Manifest.permission.READ_EXTERNAL_STORAGE
                        || it.key == android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) {
                        it.value
                    } else {
                        false
                    }
            }
            if (status)
            {
                Toast.makeText(requireContext(),"Granted",Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions.launch(permissions)
        binding.audioPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_musicMediaFragment)
        }
        binding.videoPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_videoMediaFragment)
        }
    }
}