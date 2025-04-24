package com.secureguard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secureguard.app.R
import com.secureguard.app.databinding.FragmentHomeBinding
import com.secureguard.app.utils.PreferencesManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        
        // Configurar status das funcionalidades
        updateStatusCards()
    }
    
    private fun updateStatusCards() {
        // Atualizar cards de status com base nas configurações atuais
        binding.cardLocationTracking.apply {
            val isEnabled = preferencesManager.isLocationTrackingEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
        
        binding.cardWrongPasswordPhoto.apply {
            val isEnabled = preferencesManager.isWrongPasswordPhotoEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
        
        binding.cardSimChangeDetection.apply {
            val isEnabled = preferencesManager.isSimChangeDetectionEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
        
        binding.cardRemoteLock.apply {
            val isEnabled = preferencesManager.isRemoteLockEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
        
        binding.cardSoundAlert.apply {
            val isEnabled = preferencesManager.isSoundAlertEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
        
        binding.cardCallControl.apply {
            val isEnabled = preferencesManager.isCallControlEnabled()
            statusIcon.setImageResource(if (isEnabled) R.drawable.ic_check else R.drawable.ic_close)
            statusText.text = if (isEnabled) "Ativo" else "Inativo"
            statusText.setTextColor(resources.getColor(if (isEnabled) R.color.green else R.color.red, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
