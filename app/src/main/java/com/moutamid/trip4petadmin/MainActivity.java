package com.moutamid.trip4petadmin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.moutamid.trip4petadmin.adapters.LocationAdapter;
import com.moutamid.trip4petadmin.databinding.ActivityMainBinding;
import com.moutamid.trip4petadmin.model.LocationsModel;
import com.moutamid.trip4petadmin.utilis.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<LocationsModel> places = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.placesRC.setLayoutManager(new LinearLayoutManager(this));
        binding.placesRC.setHasFixedSize(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        Constants.databaseReference().child(Constants.PLACE).get().addOnSuccessListener(dataSnapshot -> {
            Constants.dismissDialog();
            if (dataSnapshot.exists()) {
                places.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                        LocationsModel model = dataSnapshot2.getValue(LocationsModel.class);
                        places.add(model);
                    }
                }
                LocationAdapter adapter = new LocationAdapter(this, places);
                binding.placesRC.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}