package com.example.userportal.controller;

import com.example.userportal.entity.User;
import com.example.userportal.repository.UserRepository;
import com.example.userportal.service.OTPService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired private OTPService otpService;
    @Autowired private UserRepository userRepo;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> data) {
        String identifier = data.get("email") != null ? data.get("email") : data.get("mobile");
        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email or Mobile is required"));
        }
        String otp = otpService.generateOTP(identifier);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully", "otp", otp));
    }

    /*
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> data) {
        String identifier = data.get("email") != null ? data.get("email") : data.get("mobile");
        String otp = data.get("otp");
        if (identifier == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing data"));
        }
        if (otpService.verifyOTP(identifier, otp)) {
            otpService.clearOTP(identifier);
            return ResponseEntity.ok(Map.of("message", "OTP Verified"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP"));
        }
    }
    */

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();
        boolean hasMobile = user.getMobile() != null && !user.getMobile().isBlank();

        if ((hasEmail && hasMobile) || (!hasEmail && !hasMobile)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Provide only email or mobile, not both."));
        }

        user.setIsVerified(true); // auto-verify
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }

    // ✅ ✅ ✅ LOGIN API ADDED
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data, HttpSession session) {
        String username = data.get("username");
        String password = data.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and Password required"));
        }

        Optional<User> userOpt = userRepo.findByEmailOrMobileAndPassword(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loggedInUser", username); // save in session
            String fullName = user.getFirstName() + " " + user.getLastName();
            return ResponseEntity.ok(Map.of("fullName", fullName));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    // ✅ Dashboard ke liye full name fetch karne wali API
    @GetMapping("/user-profile")
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        }

        Optional<User> userOpt = userRepo.findByEmailOrMobile(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String fullName = user.getFirstName() + " " + user.getLastName();
            return ResponseEntity.ok(Map.of("fullName", fullName));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }
}
