package com.moutamid.trip4petadmin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.trip4petadmin.adapters.CommentsAdapter;
import com.moutamid.trip4petadmin.adapters.SliderAdapter;
import com.moutamid.trip4petadmin.databinding.ActivityDetailBinding;
import com.moutamid.trip4petadmin.model.CommentModel;
import com.moutamid.trip4petadmin.model.FilterModel;
import com.moutamid.trip4petadmin.model.LocationsModel;
import com.moutamid.trip4petadmin.utilis.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    LocationsModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.contacts.setOnClickListener(v -> {
            if (model.contact.isEmpty()) {
                Toast.makeText(this, getString(R.string.contact_not_found), Toast.LENGTH_SHORT).show();
            } else {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + model.contact));
                startActivity(dialIntent);
            }
        });

        binding.directions.setOnClickListener(v -> {
            String destinationLatitude = String.valueOf(model.latitude);
            String destinationLongitude = String.valueOf(model.longitude);
            Uri mapUri = Uri.parse("http://maps.google.com/maps?daddr=" + destinationLatitude + "," + destinationLongitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            // mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        binding.more.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_place)
                    .setMessage(R.string.are_you_sure_you_want_to_delete_this_place)
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.PLACE).child(model.userID).child(model.id).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(this, getString(R.string.place_deleted), Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .show();
        });

    }

    private void updateRecycler() {
        if (model.comments != null) {
            float rating = 0;
            for (CommentModel commentModel : model.comments) {
                rating += commentModel.rating;
            }
            float total = rating / model.comments.size();
            String rate = String.format(Locale.getDefault(), "%.2f", total) + " (" + model.comments.size() + ")";
            if (model.comments.size() > 1) binding.rating.setText(rate);
            else binding.rating.setText(model.comments.get(0).rating + " (1)");

            binding.noComments.setVisibility(View.GONE);
            binding.commentsRC.setVisibility(View.VISIBLE);
        }
        binding.commentsRC.setLayoutManager(new LinearLayoutManager(this));
        binding.commentsRC.setHasFixedSize(false);
        CommentsAdapter adapter = new CommentsAdapter(this, model.comments);
        binding.commentsRC.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        model = (LocationsModel) Stash.getObject(Constants.MODEL, LocationsModel.class);
        if (model.images != null) {
            if (!model.images.isEmpty()) {
                binding.imageSlider.setSliderAdapter(new SliderAdapter(this, model.images));
            } else {
                binding.imageSlider.setSliderAdapter(new SliderAdapter(this, new ArrayList<>(Arrays.asList(Constants.DUMMY_IMAGE))));
            }
        } else {
            binding.imageSlider.setSliderAdapter(new SliderAdapter(this, new ArrayList<>(Arrays.asList(Constants.DUMMY_IMAGE))));
        }

        if (model.activities != null) {
            for (FilterModel s : model.activities) {
                LayoutInflater inflater = getLayoutInflater();
                View customEditTextLayout = inflater.inflate(R.layout.icon, null);
                ImageView image = customEditTextLayout.findViewById(R.id.image);
                image.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                image.setImageResource(s.icon);
                binding.activitiesIcon.addView(customEditTextLayout);
            }
        } else {
            binding.activities.setVisibility(View.GONE);
        }

        int activitySize = model.activities == null ? 0 : model.activities.size();
        int servicesSize = model.services == null ? 0 : model.services.size();

        binding.totalActivity.setText(activitySize + " " + getString(R.string.activities));
        binding.totalServices.setText(servicesSize + " " + getString(R.string.services));
        binding.name.setText(model.name);
        binding.typeOfPlace.setText(model.typeOfPlace);
        binding.desc.setText(model.description);
        int size = model.comments != null ? model.comments.size() : 0;
        binding.rating.setText(model.rating + " (" + size + ")");
        String cord = model.latitude + ", " + model.longitude + " (lat, long)";
        binding.coordinates.setText(cord);
        String address = model.name + ", " + model.city + ", " + model.country;
        binding.location.setText(address);
        binding.servicesIcon.removeAllViews();
        if (model.services != null) {
            for (FilterModel s : model.services) {
                Log.d(TAG, "onResume: " + s.name);
                try {
                    LayoutInflater inflater = getLayoutInflater();
                    View customEditTextLayout = inflater.inflate(R.layout.icon, null);
                    ImageView image = customEditTextLayout.findViewById(R.id.image);
                    CardView card = customEditTextLayout.findViewById(R.id.card);
                    card.setCardBackgroundColor(getResources().getColor(R.color.green_card));
                    image.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    image.setImageResource(s.icon);
                    binding.servicesIcon.addView(customEditTextLayout);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "onResume: " + e.getLocalizedMessage());
                }
            }
        }
        updateRecycler();
    }

    private static final String TAG = "DetailActivity";

}