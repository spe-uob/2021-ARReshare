package com.example.ar_reshare;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ar_reshare.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Product> products = createDummyProducts();
        populateMap(mMap, products);

        LatLng mvb = new LatLng(51.456070226943865, -2.602992299931959);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mvb));
    }

    private List<Product> createDummyProducts () {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Fancy Cup", "John", 51.45120306024447, -2.5869936269149303));
        products.add(new Product("Magic Pen", "Artur", 51.45599668866024, -2.6030781306216135));
        products.add(new Product("Pink Umbrella", "Lingtao", 51.45416805430673, -2.591828561043675));
        products.add(new Product("Apple Pencil", "Hellin", 51.45864853294286, -2.5853638594577193));
        products.add(new Product("Meat", "Ziqian", 51.45692540090406, -2.6081114869801714));
        products.add(new Product("Pink Headphones", "Arafat", 51.459040571152514, -2.6022736036387366));
        return products;
    }

    private void populateMap(GoogleMap mMap, List<Product> products) {
        for (Product product : products) {
            LatLng coordinates = new LatLng(product.lat, product.lng);
            mMap.addMarker(new MarkerOptions().position(coordinates).title(product.name).snippet("by " + product.contributor));
        }
    }

    private class Product {
        public String name;
        public String contributor;
        public double lat;
        public double lng;

        Product(String name, String contributor, double lat, double lng) {
            this.name = name;
            this.contributor = contributor;
            this.lat = lat;
            this.lng = lng;
        }
    }
}