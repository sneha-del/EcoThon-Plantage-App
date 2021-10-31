package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Camera extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    //public static final int GALLERY_REQUEST_CODE = 105;
    TextView result;
    ImageView imageView;
    Button camera_btn, gallery_btn;
    String currentPhotoPath;
    String text, mUserId;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    DocumentReference documentReference;
    Query query;
    File f;
    Uri contentUri;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.displayImageView);
        camera_btn = findViewById(R.id.cameraBtn);
        gallery_btn = findViewById(R.id.galleryBtn);
        result = findViewById(R.id.infoOfImage);

        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        documentReference = FirebaseFirestore.getInstance().collection("Users").document(mUserId);
        query = documentReference.collection("Images");

        camera_btn.setOnClickListener(v -> {
            askCameraPermissions();
            //Toast.makeText(Camera.this, "camera button is clicked", Toast.LENGTH_SHORT).show();
        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Camera.this)
                        .setTitle("Confirmation...")
                        .setMessage("Are you sure\nImage will be uploaded")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                Progress_bar();
                                uploadImageToFirebase(f.getName(), contentUri);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.check)
                        .show();
            }
        });
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Detection Failed", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Camera.this, "com.example.paid_app.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                f = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(f));
                //imageView.getI;
                FirebaseVisionImage image;
                try {
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(),Uri.fromFile(f));
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                            .getOnDeviceImageLabeler();

                    labeler.processImage(image)
                            .addOnSuccessListener(labels -> {
                                // Task completed successfully
                                // ...
                                for (FirebaseVisionImageLabel label: labels) {
                                    text = label.getText();
                                    //String entityId = label.getEntityId();
                                    //float confidence = label.getConfidence();
                                    result.append(
                                            text+ "\n");
                                    if(result.getText().toString().contains("Plant")){
                                        Toast.makeText(Camera.this, "Verified ", Toast.LENGTH_LONG).show();
                                        Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        contentUri = Uri.fromFile(f);
                                        mediaScanIntent.setData(contentUri);
                                        this.sendBroadcast(mediaScanIntent);
                                        //Progress_bar();
                                        //uploadImageToFirebase(f.getName(), contentUri);
                                    }
                                    else {
                                        Toast.makeText(this, "Please Click Another Image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Task failed with an exception
                                // ...
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                        SharedPreferences sp = getSharedPreferences("Coins",MODE_PRIVATE);
                        String PlantName = sp.getString("Plant_name","Tulsi");
                        String pID = documentReference.getId();
                        Map<String,Object> map = new HashMap<>();
                        map.put("Plant-name",PlantName);
                        map.put("ImageUrL",uri.toString());
                        map.put("Id",pID);
                        map.put("Timestamp",(System.currentTimeMillis()/1000));
                        long Timestamp = (System.currentTimeMillis()/1000);
                        String ImageUrl = uri.toString();
                        documentReference.collection("Images").document().set(new ImageModel(pID,PlantName,ImageUrl,Timestamp)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Camera.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Camera.this, Bravo.class));
                                }else{
                                    Toast.makeText(Camera.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Camera.this, e+"", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //Toast.makeText(Camera.this, "Image Is Uploaded.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Camera.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Progress_bar(){
        progressDialog = new ProgressDialog(Camera.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}