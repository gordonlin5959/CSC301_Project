package com.team09.theproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UploadScheduleActivity extends AppCompatActivity {

    public static final int ACTIVITY_CHOOSE_FILE = 1;
    private static List<List<String>> courses = new ArrayList<>();
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_schedule);
        Button uploadButton = findViewById(R.id.uploadButton);

        Button addButton = findViewById(R.id.ManuelCreate);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open file manager
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            }
        });
        queue = Volley.newRequestQueue(UploadScheduleActivity.this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //still need to create a new friend request and send it to server


                //Builds a new alertDialog to let user input a message along with their friend request.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadScheduleActivity.this);

                LinearLayout layout = new LinearLayout(UploadScheduleActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText et = new EditText(UploadScheduleActivity.this);
                final EditText et2 = new EditText(UploadScheduleActivity.this);
                et.setHint("Class Name");
                et2.setHint("Lecture Section");
                layout.addView(et);
                layout.addView(et2);
                alertDialogBuilder.setView(layout);


                // set dialog message
                alertDialogBuilder.setTitle("Add a new class");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String url ="https://csc301-group09.herokuapp.com/api/schedules/";
                        JSONObject body = new JSONObject();

                        try{
                            body.put("user_id", Manager.getCurrentID());
                            body.put("class_name", et.getText().toString());
                            body.put("lecture_section", et2.getText().toString());

                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response){


                                    }

                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub

                                    }
                                });


                        queue.add(jsObjRequest);
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    assert uri != null;
                    String filePath = uri.getPath();
                    Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                    // Set courses
                    try {
                        String line;
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("SUMMARY")) {
                                List<String> course = new ArrayList<>();
                                String summary = line.split(":")[1];
                                String[] summarysplit;
                                if (summary.contains(" LEC")) {
                                    summarysplit = summary.split(" LE");
                                } else if (summary.contains(" TUT")) {
                                    summarysplit = summary.split(" TU");
                                } else {
                                    summarysplit = summary.split(" PR");
                                }
                                course.add(summarysplit[0]);
                                course.add(summarysplit[1]);
                                String semester = bufferedReader.readLine().split(":")[1].substring(4, 6);
                                course.add(semester);
                                courses.add(course);
                            }
                        }

                        String url = "https://csc301-group09.herokuapp.com/api/schedules/";
                        for (List<String> course:courses) {
                            JSONObject schedule = new JSONObject();
                            schedule.put("user_id", Manager.getCurrentID());
                            schedule.put("class_name", course.get(0));
                            schedule.put("lecture_section", course.get(1));

                            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                                    schedule, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.getStackTrace();
                                }
                            });
                            queue.add(jsObjRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(UploadScheduleActivity.this, "Schedule uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UploadScheduleActivity.this, ProfileActivity.class));
                    finish();
                }
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(UploadScheduleActivity.this, MainActivity.class));
        finish();

    }
}
