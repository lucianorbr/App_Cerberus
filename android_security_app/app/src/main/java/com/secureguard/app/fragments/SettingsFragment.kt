package com.secureguard.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secureguard.app.activities.SettingsActivity
import com.secureguard.app.databinding.FragmentSettingsBinding
import com.secureguard.app.utils.PreferencesManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        
        // Configurar informações do dispositivo
        setupDeviceInfo()
        
        // Configurar botão de configurações
        binding.btnOpenSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Atualizar informações quando o fragmento se tornar visível
        setupDeviceInfo()
    }
    
    private fun setupDeviceInfo() {
        // Exibir ID do dispositivo
        binding.textDeviceId.text = "ID do Dispositivo: ${preferencesManager.getDeviceId()}"
        
        // Exibir e-mail de notificação
        val email = preferencesManager.getNotificationEmail()
        binding.textNotificationEmail.text = "E-mail para Notificações: ${email.ifEmpty { "Não configurado" }}"
        
        // Exibir status das funcionalidades
        val locationEnabled = if (preferencesManager.isLocationTrackingEnabled()) "Ativo" else "Inativo"
        val photoEnabled = if (preferencesManager.isWrongPasswordPhotoEnabled()) "Ativo" else "Inativo"
        val simEnabled = if (preferencesManager.isSimChangeDetectionEnabled()) "Ativo" else "Inativo"
        val lockEnabled = if (preferencesManager.isRemoteLockEnabled()) "Ativo" else "Inativo"
        val soundEnabled = if (preferencesManager.isSoundAlertEnabled()) "Ativo" else "Inativo"
        val callEnabled = if (preferencesManager.isCallControlEnabled()) "Ativo" else "Inativo"
        
        binding.textFeaturesStatus.text = """
            Rastreamento de Localização: $locationEnabled
            Foto em Senha Incorreta: $photoEnabled
            Detecção de Troca de SIM: $simEnabled
            Bloqueio Remoto: $lockEnabled
            Alerta Sonoro: $soundEnabled
            Controle de Ligações: $callEnabled
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
