package com.example.practicaevaluable.Fragments_principal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.practicaevaluable.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class fragment_mapa : Fragment(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
    private var locationpermission=1000
    private var locationpermissionrequestcode=1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createFragment()

    }



    private fun createFragment() {
        val fragment=SupportMapFragment()

        //se conecta a la api de google y trae el mapa
        fragment.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }
    private fun createMarker() {
        val coordenada = LatLng(36.844200,-2.460541)
        val marker : MarkerOptions = MarkerOptions().position(coordenada).title("Mi Provincia")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenada, 18f),
            4000,
            null
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Habilita la ubicación del usuario en el mapa
            map.isMyLocationEnabled = true
        } else {
            // Solicita el permiso de ubicación
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationpermission)
        }

        createMarker()

    }




}