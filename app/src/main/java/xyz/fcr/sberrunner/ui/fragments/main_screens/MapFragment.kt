package xyz.fcr.sberrunner.ui.fragments.main_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.fcr.sberrunner.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

//    private val callback = OnMapReadyCallback { googleMap ->
//        val city = CitySaver().getFromSharedPref(requireContext())
//        val cityLatLng = LatLng(city.lat, city.lon)
//
//        googleMap.addMarker(MarkerOptions().position(cityLatLng).title("Marker in ${city.name}"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cityLatLng))
//        googleMap.setMinZoomPreference(9F)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val city = CitySaver().getFromSharedPref(requireContext())
//
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
//
//        binding.mapCityName.text = city.name
//
//        binding.backButton.setOnClickListener {
//            activity?.supportFragmentManager?.popBackStack()
//        }
//    }
}