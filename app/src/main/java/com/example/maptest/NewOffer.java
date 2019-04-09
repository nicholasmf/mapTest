package com.example.maptest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOffer extends AppCompatActivity {

    private LatLng selectedLocation;

    private EditText mFormName;
    private EditText mFormPrice;
    private ImageView mImageView;
    private Button mImageButton;
    private ProgressBar mProgressBar;

    private FireStore mDb;

    private String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFormName = findViewById(R.id.offer_name);
        mFormPrice = findViewById(R.id.offer_price);
        mImageView = findViewById(R.id.offer_image_preview);
        mImageButton = findViewById(R.id.offer_image);
        mProgressBar = findViewById(R.id.progress_bar);

        mDb = new FireStore();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFormName.getText().toString().matches("") || mFormPrice.getText().toString().matches("")) {
                    Snackbar.make(view, "Missing info", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    uploadFile(new FileTaskCb() {
                        @Override
                        public void fileUploaded(Uri downloadUrl) {
                            String name = mFormName.getText().toString();
                            float price = Float.parseFloat(mFormPrice.getText().toString());
                            String type = getTypeRadioValue();
                            ArrayList<String> images = new ArrayList<>();
                            images.add(downloadUrl.toString());

                            Building offer = new Building(name, selectedLocation, null, price, type, images);
                            mDb.insertOffer(offer);

                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        selectedLocation = getIntent().getParcelableExtra("selectedLocation");
    }

    private String getTypeRadioValue() {
        if (((RadioButton) findViewById(R.id.offer_type_rent)).isChecked()) {
            return "rent";
        } else if (((RadioButton) findViewById(R.id.offer_type_sell)).isChecked()) {
            return "sell";
        } else {
            return null;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        ArrayList allIntents = new ArrayList();
        allIntents.add(takePictureIntent);
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.getStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.maptest.fileProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            galleryAddPic();

            setPic();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + '_';
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                "jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        int targetW = Math.max(mImageView.getWidth(), 200);
        int targetH = Math.max(mImageView.getHeight(), 200);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/ targetH);
        scaleFactor = Math.min(scaleFactor, 300);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private void uploadFile(final FileTaskCb cb) {
        File f = new File(currentPhotoPath);
        Uri file = Uri.fromFile(f);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference picRef = storageRef.child("tests/pics/" + file.getLastPathSegment());

        UploadTask uploadTask = picRef.putFile(file);
        uploadTask.addOnFailureListener(uploadFileFailureTask())
                    .addOnProgressListener(uploadFileOnProgressTask())
                    .addOnSuccessListener(uploadFileSuccessTask())
                    .continueWithTask(getDownloadUri(picRef))
                    .addOnCompleteListener(onDownloadUriComplete(cb));
    }

    private OnFailureListener uploadFileFailureTask() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getStackTrace();
            }
        };
    }

    private OnProgressListener<UploadTask.TaskSnapshot> uploadFileOnProgressTask() {
        return new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                mProgressBar.setProgress((int) progress);
            }
        };
    }

    private OnSuccessListener<UploadTask.TaskSnapshot> uploadFileSuccessTask() {
        return new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("upload", taskSnapshot.getMetadata().toString());
            }
        };
    }

    private Continuation<UploadTask.TaskSnapshot, Task<Uri>> getDownloadUri(final StorageReference ref) {
        return new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        };
    }

    private OnCompleteListener<Uri> onDownloadUriComplete(final FileTaskCb cb) {
        return new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    cb.fileUploaded(downloadUri);
                }
            }
        };
    }
}
