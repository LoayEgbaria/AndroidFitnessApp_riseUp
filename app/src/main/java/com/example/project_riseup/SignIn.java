package com.example.project_riseup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class SignIn extends AppCompatActivity {
    Button signInButton,signUpButton;
    EditText passwordInput;
    EditText phoneInput;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UI elements
        signInButton = findViewById(R.id.sign_in_button);
        passwordInput = findViewById(R.id.password_input);
        phoneInput = findViewById(R.id.phone_input);
        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, Signup.class);
            startActivity(intent);
        });

        // Set up the Sign In button
        signInButton.setOnClickListener(v -> signInUser());
    }

    private void signInUser() {
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (validateInputs(phone, password)) {
            // Fetch user from the database using phone number
            userViewModel.getUserByPhone(phone).observe(this, user -> {
                if (user != null) {
                    Log.d("SignIn", "User found: " + user.getUserName());
                    if (user.getPassword().equals(password)) {
                        Toast.makeText(SignIn.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Save the user ID in SharedPreferences
                        long userId = user.getId();  // Get the user ID from the Room database
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("USER_ID", userId);
                        editor.apply();  // Commit the changes

                        // Navigate to HomePage and pass the user ID
                        navigateToHomePage(userId);
                    } else {
                        Toast.makeText(SignIn.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "User not found! Please sign up.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToHomePage(long userId) {
        // Navigate to HomePage
        Intent intent = new Intent(SignIn.this, HomePage.class);
        intent.putExtra("USER_ID", userId);  // Pass the user ID to HomePage
        startActivity(intent);
        finish();
    }


    // Validate phone number and password
    private boolean validateInputs(String phone, String password) {
        boolean isValid = true;

        if (phone.isEmpty()) {
            phoneInput.setError("Phone number cannot be empty");
            isValid = false;
        } else if (phone.length() != 10) {
            phoneInput.setError("Phone number must be 10 digits long");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters long");
            isValid = false;
        }

        return isValid;
    }
}
