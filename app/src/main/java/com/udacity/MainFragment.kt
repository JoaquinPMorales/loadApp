package com.udacity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.udacity.databinding.ContentMainBinding

class MainFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ContentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}