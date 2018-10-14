package com.team09.theproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    protected static String email, password;
    private EditText loginEmail, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.loginEmail = findViewById(R.id.loginEmail);
        this.loginPassword = findViewById(R.id.loginPassword);
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = loginEmail.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter your email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(email.contains("@") && email.substring(email.indexOf("@")).contains("."))) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                password = loginPassword.getText().toString();
                if (password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter a password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                String url ="https://csc301-group09.herokuapp.com/api/log-in/";
                JSONObject user = new JSONObject();

                try {
                    user.put("email", email);
                    user.put("password", password);
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        user, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Manager.setCurrentID((int) response.get("id"));
                            if (Manager.getCurrentID() == -1) {
                                Toast.makeText(LoginActivity.this,"Email or password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Logging in...",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } catch(JSONException e) {
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
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = loginEmail.getText().toString();
                password = loginPassword.getText().toString();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }
}
