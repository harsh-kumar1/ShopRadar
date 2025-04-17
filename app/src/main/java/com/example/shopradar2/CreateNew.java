package com.example.shopradar2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class CreateNew extends AppCompatActivity {

    // UI Components
    private RadioGroup roleRadioGroup;
    private TextInputEditText fullNameEditText, emailEditText, phoneEditText,phoneOtpEditText,emailOtpEditText,
            passwordEditText, confirmPasswordEditText, shopNameEditText, licenseNumberEditText;
    private Button registerButton;
    private LinearLayout userFields, vendorFields, deliveryFields;
    private ProgressBar progressBar;
    private CheckBox termsCheckBox;

    private boolean isEmailVerified = false;
    private boolean isPhoneVerified = false;
    private FirebaseAuth mAuth;
    private String role;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuth = FirebaseAuth.getInstance();
        // Initialize views
        initializeViews();
        updateRegisterButtonState();



        // Role selection listener
        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            userFields.setVisibility(View.GONE);
            vendorFields.setVisibility(View.GONE);
            deliveryFields.setVisibility(View.GONE);

            if (checkedId == R.id.userRadio) {
                userFields.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.vendorRadio) {
                vendorFields.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.deliveryRadio) {
                deliveryFields.setVisibility(View.VISIBLE);
            }



        });
        // OTP buttons
//        findViewById(R.id.emailOtpButton).setOnClickListener(v -> {
//            String email = emailEditText.getText().toString().trim();
//            if (email.isEmpty()) {
//                Toast.makeText(this, "Please enter email first", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            // Simulate OTP sent
//            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "TempPassword123!")
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            if (user != null) {
//                                user.sendEmailVerification().addOnCompleteListener(emailTask -> {
//                                    if (emailTask.isSuccessful()) {
//                                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
//                                        findViewById(R.id.emailOtpLayout).setVisibility(View.VISIBLE);
//                                    }
//                                });
//                            }
//                        }
//                    });
//        });
//        findViewById(R.id.verifyEmailOtpButton).setOnClickListener(v -> {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            user.reload().addOnCompleteListener(task -> {
//                if (user.isEmailVerified()) {
//                    isEmailVerified = true;
//                    Toast.makeText(this, "Email Verified", Toast.LENGTH_SHORT).show();
//                    updateRegisterButtonState();
//                } else {
//                    Toast.makeText(this, "Email not verified yet", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//
//        findViewById(R.id.phoneOtpButton).setOnClickListener(v -> {
//            String phone = phoneEditText.getText().toString().trim();
//            if (phone.isEmpty()) {
//                Toast.makeText(this, "Please enter phone first", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
//                    .setPhoneNumber("+91" + phone)
//                    .setTimeout(60L, TimeUnit.SECONDS)
//                    .setActivity(this)
//                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                        @Override
//                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                            isPhoneVerified = true;
//                            updateRegisterButtonState();
//                            Toast.makeText(CreateNew.this, "Phone auto verified", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onVerificationFailed(@NonNull FirebaseException e) {
//                            Toast.makeText(CreateNew.this, "Phone verification failed", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCodeSent(@NonNull String verificationId,
//                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                            mVerificationId = verificationId;
//                            findViewById(R.id.phoneOtpLayout).setVisibility(View.VISIBLE);
//                            Toast.makeText(CreateNew.this, "OTP Sent", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .build();
//            PhoneAuthProvider.verifyPhoneNumber(options);
//            // Simulate OTP sent
//
//        });
//        findViewById(R.id.verifyPhoneOtpButton).setOnClickListener(v -> {
//            String otp = phoneOtpEditText.getText().toString().trim();
//            if (TextUtils.isEmpty(otp)) {
//                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
//            mAuth.signInWithCredential(credential)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            isPhoneVerified = true;
//                            Toast.makeText(this, "Phone Verified", Toast.LENGTH_SHORT).show();
//                            updateRegisterButtonState();
//                        } else {
//                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });


        // Terms checkbox
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(validateInputs()){
//                termsCheckBox.setChecked(false);
//            }
            updateRegisterButtonState();
        });

        // Register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void initializeViews() {
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);


        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        shopNameEditText = findViewById(R.id.shopNameEditText);
        licenseNumberEditText = findViewById(R.id.licenseNumberEditText);
        registerButton = findViewById(R.id.registerButton);
        userFields = findViewById(R.id.userFields);
        vendorFields = findViewById(R.id.vendorFields);
        deliveryFields = findViewById(R.id.deliveryFields);
        progressBar = findViewById(R.id.progressBar);
        termsCheckBox = findViewById(R.id.termsCheckBox);
    }

    private void updateRegisterButtonState() {
        boolean canRegister = termsCheckBox.isChecked() && validateInputs();
        registerButton.setEnabled(canRegister);
    }

    private void registerUser() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
                        int selectedId= roleRadioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedRadioButton = findViewById(selectedId);
                            String selectedRole = selectedRadioButton.getText().toString();

                            if (selectedRole.equals("User")) {
                                startActivity(new Intent(this, Login.class));
                                finish();
                            } else if (selectedRole.equals("Vendor")) {
                                Intent intent = new Intent(this, VendorShopDetail.class);
                                intent.putExtra("role","Vendor");
                                startActivity(intent);
                                finish();

                            } else if (selectedRole.equals("Delivery Agent")) {
                                // Logic for delivery
                            }
                        }

                    } else {
                        Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add role-specific data
//        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
//        if (selectedId == R.id.vendorRadio) {
//            map.put("role", "vendor");
//            map.put("shopName", shopNameEditText.getText().toString().trim());
//        } else if (selectedId == R.id.deliveryRadio) {
//            map.put("role", "delivery");
//            map.put("licenseNumber", licenseNumberEditText.getText().toString().trim());
//        } else {
//            map.put("role", "user");
//        }


    }

    private boolean validateInputs() {
        // Common validations
        if (fullNameEditText.getText().toString().trim().isEmpty()) {
            fullNameEditText.setError("Full Name required");

            return false;
        }

        if (emailEditText.getText().toString().trim().isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (phoneEditText.getText().toString().trim().isEmpty()) {
            phoneEditText.setError("Phone is required");
            return false;
        }

        if (passwordEditText.getText().toString().trim().isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }

        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }

        // Role-specific validations
        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.vendorRadio && shopNameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter shop name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedId == R.id.deliveryRadio && licenseNumberEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter license number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
