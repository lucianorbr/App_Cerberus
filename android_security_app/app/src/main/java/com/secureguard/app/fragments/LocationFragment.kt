package com.secureguard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.secureguard.app.R
import com.secureguard.app.databinding.FragmentLocationBinding
import com.secureguard.app.utils.PreferencesManager
import com.secureguard.app.utils.ServerApi

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var serverApi: ServerApi
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        serverApi = ServerApi()
        
        // Inicializar mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Configurar botão de atualização
        binding.btnRefresh.setOnClickListener {
            loadLocationHistory()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configurar mapa
        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }
        
        // Carregar histórico de localização
        loadLocationHistory()
    }
    
    private fun loadLocationHistory() {
        // Aqui seria implementada a lógica para carregar o histórico de localização do servidor
        // Por enquanto, vamos apenas mostrar uma localização de exemplo
        
        val deviceId = preferencesManager.getDeviceId()
        
        // Exemplo de localização (São Paulo)
        val location = LatLng(-23.550520, -46.633308)
        
        // Adicionar marcador
        googleMap?.clear()
        googleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("Última localização")
        )
        
        // Mover câmera para a localização
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        
        // Exemplo de histórico de localização (seria carregado do servidor)
        val locationHistory = listOf(
            LatLng(-23.550520, -46.633308),
            LatLng(-23.551000, -46.634000),
            LatLng(-23.552000, -46.635000),
            LatLng(-23.553000, -46.636000)
        )
        
        // Desenhar linha conectando os pontos
        val polylineOptions = PolylineOptions()
            .addAll(locationHistory)
            .width(5f)
            .color(resources.getColor(R.color.colorPrimary, null))
        
        googleMap?.addPolyline(polylineOptions)
        
        // Atualizar timestamp
        binding.textLastUpdate.text = "Última atualização: ${java.util.Date()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
