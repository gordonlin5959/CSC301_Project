package com.team09.theproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response;


public class SignUpActivity extends AppCompatActivity {

    private static String firstName, lastName, email, password;
    private EditText signUpFirstName, signUpLastName, signUpEmail, signUpPassword,
            signUpConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.signUpFirstName = findViewById(R.id.signUpFirstName);
        this.signUpLastName = findViewById(R.id.signUpLastName);
        this.signUpEmail = findViewById(R.id.signUpEmail);
        this.signUpPassword = findViewById(R.id.signUpPassword);
        this.signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        if (LoginActivity.email != null) {
            signUpEmail.setText(LoginActivity.email);
        } if (LoginActivity.password != null) {
            signUpPassword.setText(LoginActivity.password);
        }
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstName = signUpFirstName.getText().toString();
                if (firstName.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter your first name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                lastName = signUpLastName.getText().toString();
                if (lastName.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter your last name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                email = signUpEmail.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(email.contains("@") && email.substring(email.indexOf("@")).contains("."))) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                password = signUpPassword.getText().toString();
                if (password.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter a password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(password.equals(signUpConfirmPassword.getText().toString()))) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                String url = "https://csc301-group09.herokuapp.com/api/users/";
                JSONObject user = new JSONObject();

                try {
                    user.put("email", email);
                    user.put("name", firstName + "/ /" + lastName);
                    user.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        user, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Manager.setCurrentID((int) response.get("id"));
                            if (Manager.getCurrentID() == -1) {
                                Toast.makeText(SignUpActivity.this,
                                        "The email address you have entered " +
                                                "is already associated with another account",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Your account has been successfully created",
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, UploadScheduleActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getStackTrace();
                    }
                });
                queue.add(jsObjRequest);
            }
        });
    }
}
