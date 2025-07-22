package com.example.pda.auth;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pda.R;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private RequestQueue requestQueue;

    private static final String BASE_URL = "https://votre-api.com/api/";
    private static final String LOGIN_ENDPOINT = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        requestQueue = Volley.newRequestQueue(this);


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (validateInputs(email, password)) {
                    attemptLogin(email, password);
                }
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("L'email est requis");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Le mot de passe est requis");
            return false;
        }

        return true;
    }

    private void attemptLogin(String email, String password) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + LOGIN_ENDPOINT,
                jsonBody,
                response -> {

                    try {
                        String token = response.getString("token");

                        Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erreur de réponse", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {

                    Toast.makeText(LoginActivity.this, "Échec de la connexion: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );


        requestQueue.add(jsonObjectRequest);
    }
}