package com.tree.imagewithcrop;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonCamera;
    Button buttonGallery;
    ImageView imageView;
    RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCamera = findViewById(R.id.button_camera);
        buttonGallery = findViewById(R.id.button_gallery);
        imageView = findViewById(R.id.iv);

        buttonGallery.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_camera:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                RxImagePicker.with(this).requestImage(Sources.CAMERA).subscribe(new Consumer<Uri>() {
                                    @Override
                                    public void accept(@NonNull Uri uri) throws Exception {
                                        CropImage.activity(uri)
                                                .start(MainActivity.this);
                                    }
                                });
                            } else {
                                // At least one permission is denied
                            }
                        });


                break;

            case R.id.button_gallery:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                RxImagePicker.with(this).requestImage(Sources.GALLERY).subscribe(new Consumer<Uri>() {
                                    @Override
                                    public void accept(@NonNull Uri uri) throws Exception {
                                        CropImage.activity(uri)
                                                .start(MainActivity.this);
                                    }
                                });
                            } else {
                                // At least one permission is denied
                            }
                        });


                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
