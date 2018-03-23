package pny.com.pnyidgenrator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static final int CAMREA_ID = 1;

    private LinearLayout parentLayout;
    private de.hdodenhof.circleimageview.CircleImageView pp;
    private Spinner course;
    private EditText studentName;
    private EditText CNIC;
    private EditText batchNum;
    private EditText contactNo;
    private EditText vehicleNum;
    private EditText address;
    private EditText startDate;
    private EditText expiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.parentLayour);
        TextView save = findViewById(R.id.save);
        TextView studentId = findViewById(R.id.studentId);
        pp = findViewById(R.id.ppimage);
        studentName = findViewById(R.id.studentName);
        course = findViewById(R.id.courseName);
        CNIC = findViewById(R.id.CNIC);
        batchNum = findViewById(R.id.batchNum);
        contactNo = findViewById(R.id.contactNo);
        vehicleNum = findViewById(R.id.vehicleNum);
        address = findViewById(R.id.address);
        startDate = findViewById(R.id.startDate);
        expiryDate = findViewById(R.id.expiryDate);

        studentId.setText(getStudentId());
        setCourses();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMREA_ID);
                } else {
                    CropImage.startPickImageActivity(MainActivity.this);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateFields()) {
                    studentName.clearFocus();

                    final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Saving Data...",
                            true);
                    dialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            sendDatatoServer();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter all the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMREA_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                CropImage.startPickImageActivity(MainActivity.this);
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    private String getStudentId() {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(10000));
        return "PNY-" + id;
    }

    private boolean validateFields() {
        if (studentName.getText().toString().equals("") || CNIC.getText().toString().equals("") || batchNum.getText().toString().equals("") ||
                contactNo.getText().toString().equals("") || vehicleNum.getText().toString().equals("")
                || startDate.getText().toString().equals("") || expiryDate.getText().toString().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void setCourses() {

        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<>();
        categories.add("3D Animation");
        categories.add("Android App");
        categories.add("Photographer");
        categories.add("Web Designing");
        categories.add("Android Game ");
        categories.add("SMM");
        categories.add("Video Animation ");
        categories.add("Graphic Designing ");
        categories.add("SEO");
        categories.add("ASP. NET");
        categories.add("QuickBooks & Excel");
        categories.add("Adobe Premier & After Effects");
        categories.add("PMP");
        categories.add("PHP- Laravel");
        categories.add("CDMM");
        categories.add("Advanced Photoshop CC");
        categories.add("iOS App");
        categories.add("React Native");
        categories.add("AutoCad");
        categories.add("Ethical Hacking");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<
                >(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        course.setAdapter(dataAdapter);
    }

    private void saveData(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        FileOutputStream fileOutputStream = null;

        File path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/PNY");

        if (!path.exists()) {
            path.mkdir();
        }


        String uniqueID = UUID.randomUUID().toString();

        File file = new File(path, uniqueID + ".jpg");
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);

        try {
            assert fileOutputStream != null;
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            CropImage.activity(imageUri)
                    .start(this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                pp.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void sendDatatoServer() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveData(parentLayout);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put("name", studentName.getText().toString());
                params.put("cnic", CNIC.getText().toString());
                params.put("batch_num", batchNum.getText().toString());
                params.put("contact_num", contactNo.getText().toString());
                params.put("vehicle_num", vehicleNum.getText().toString());
                params.put("address", address.getText().toString());
                params.put("start_date", startDate.getText().toString());
                params.put("end_date", expiryDate.getText().toString());

                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
