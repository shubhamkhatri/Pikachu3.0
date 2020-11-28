/*
 *  Copyright (c) 2020 Pikachu(shubham khatri). All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.example.android.pikachu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import com.itextpdf.text.Document;

import java.text.SimpleDateFormat;


public class OcrCreateActivity extends AppCompatActivity {

    private EditText mResltEt;
    private ImageView mPreviewIv;
    private Button mSaveBtn;
    private String category;

    public static final int CAMERA_REQUEST_CODE = 200;
    public static final int STORAGE_REQUEST_CODE = 400;
    private static final int STORAGE_CODE = 600;
    public static final int IMAGE_PICK_GALLERY_CODE = 1000;
    public static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_create);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click camera button to Scan ->");

        mResltEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        mSaveBtn = findViewById(R.id.saveBtn);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we need to handle runtime permission for devices with marshmallow and above
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    //system OS >= Marshmallow(6.0), check if permission is enabled or not
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permission was not granted, request it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);
                    } else {
                        //permission already granted, call save pdf method
                        if (mResltEt.getText().toString().trim().isEmpty()) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OcrCreateActivity.this);

                            builder.setMessage("There is no text to save in PDF file, Do you still want to create file?").setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialogInterface, int i) {
                                            savePdf();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                        //system OS < Marshmallow, call save pdf method
                        else
                            savePdf();
                    }
                } else {
                    if (mResltEt.getText().toString().trim().isEmpty()) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OcrCreateActivity.this);

                        builder.setMessage("There is no text to save in PDF file, Do you still want to create file?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        savePdf();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                    //system OS < Marshmallow, call save pdf method
                    else
                        savePdf();
                }
            }
        });

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ocr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addImage) {
            showImageImportDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showImageImportDialog() {
        String[] items = {" Camera ", " Gallery "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {

                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                }

                if (which == 1) {
                    if (!checkStoragePermission()) {

                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    public boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from popup, call savepdf method
                    savePdf();
                } else {
                    //permission was denied from popup, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mPreviewIv.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    mResltEt.setText(sb.toString().trim());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setCategory() {
        EditText textView = (EditText) findViewById(R.id.resultEt);

        if (textView.getText().toString().contains("Liver function tests") ||
                textView.getText().toString().contains("Liver") ||
                textView.getText().toString().contains("LFT") ||
                textView.getText().toString().contains("LIVER")
                || textView.getText().toString().contains("liver")) {
            category = "Liver Test";
        } else if (textView.getText().toString().contains("RFT")
                || textView.getText().toString().contains("Renal Function Test") ||
                textView.getText().toString().contains("KIDNEY") ||
                textView.getText().toString().contains("kidney") ||
                textView.getText().toString().contains("rft")
        ) {
            category = "Kidney Test";
        } else if (textView.getText().toString().contains("Blood Sugar")
                || textView.getText().toString().contains("Sugar") ||
                textView.getText().toString().contains("SUGAR") ||
                textView.getText().toString().contains("sugar") ||
                textView.getText().toString().contains("Diabetes")
        ) {
            category = "Sugar Test";
        } else if (textView.getText().toString().contains("thyroid stimulating hormone test")
                || textView.getText().toString().contains("THYROID STIMULATING HORMONE TEST") ||
                textView.getText().toString().contains("Thyroid Stimulating Hormone Test") ||
                textView.getText().toString().contains("TSH") ||
                textView.getText().toString().contains("T3") ||
                textView.getText().toString().contains("triiodothyronine") ||
                textView.getText().toString().contains("Triiodothyronine") ||
                textView.getText().toString().contains("thyroxine") ||
                textView.getText().toString().contains("Thyroxine") ||
                textView.getText().toString().contains("Thyroid") ||
                textView.getText().toString().contains("thyroid")
        ) {
            category = "Thyroid Test";
        } else if (textView.getText().toString().contains("billirubene")
                || textView.getText().toString().contains("Billirubene") ||
                textView.getText().toString().contains("BILLIRUBENE") ||
                textView.getText().toString().contains("JAUNDICE") ||
                textView.getText().toString().contains("Jaundice") ||
                textView.getText().toString().contains("jaundice")
        ) {
            category = "Jaundice Test";
        } else if (textView.getText().toString().contains("Hiv positive")
                || textView.getText().toString().contains("Hiv Positive") ||
                textView.getText().toString().contains("HIV Positive") ||
                textView.getText().toString().contains("HIV positive") ||
                textView.getText().toString().contains("HIV POSITIVE") ||
                textView.getText().toString().contains("HIV+") ||
                textView.getText().toString().contains("Hiv+") ||
                textView.getText().toString().contains("hiv+") ||
                textView.getText().toString().contains("HIV") ||
                textView.getText().toString().contains("Hiv")
        ) {
            category = "HIV Test";
        } else category = "Common Test";
    }


    private void savePdf() {
        //create object of Document class
        setCategory();
        Document mDoc = new Document();
        //pdf file name
        String mFileName = new SimpleDateFormat("yyyyMMdd_HHmm",
                Locale.getDefault()).format(System.currentTimeMillis());
        mFileName = mFileName.concat(category);
        //pdf file path
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "PikachuDocument" + "/" + mFileName + ".pdf";

        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            //open the document for writing
            mDoc.open();

            Rectangle rect = new Rectangle(36, 108);
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorder(2);
            rect.setBorderColor(BaseColor.BLACK);
            rect.setBorderWidth(3);
            rect.setUseVariableBorders(true);
            mDoc.add(rect);
            //get text from EditText i.e. mTextEt
            String mText = mResltEt.getText().toString().trim();

            //add author of the document (optional)
            mDoc.addAuthor("shubham");

            //add paragraph to the document
            mDoc.add(new Paragraph(mText));

            //close the document
            mDoc.close();
            //show message that file is saved, it will show file name and file path too
            Toast.makeText(this, mFileName + ".pdf\nis saved to\n" + mFilePath, Toast.LENGTH_SHORT).show();
            Intent activity = new Intent(OcrCreateActivity.this, TabLayoutActivity.class);
            activity.putExtra("fragment id", 0);
            startActivity(activity);
            finish();
        } catch (Exception e) {
            //if any thing goes wrong causing exception, get and show exception message
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
