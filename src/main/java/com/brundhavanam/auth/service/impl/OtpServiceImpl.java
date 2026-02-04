package com.brundhavanam.auth.service.impl;


//Custom exception used to indicate client-side validation errors
import com.brundhavanam.common.exception.BadRequestException;

//Service interface for OTP operations
import com.brundhavanam.auth.service.OtpService;


//Marks this class as a Spring Service (business layer component)
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService{

 // OTP validity duration (in minutes)
 // Centralized constant to avoid magic numbers
 private static final int OTP_EXPIRY_MINUTES = 5;

 /*
  * In-memory OTP store
  * Key   : Mobile number
  * Value : OtpData (contains OTP value + expiry time)
  *
  * ConcurrentHashMap is used to ensure:
  * - Thread safety
  * - Non-blocking concurrent access
  * - High performance in multi-threaded environments
  *
  * Suitable for temporary OTP storage in early-stage / MVP systems
  */
 private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

 /*
  * Generates and sends OTP to the given mobile number
  * Current implementation prints OTP to console
  * (In production, this will be replaced with SMS/Email gateway)
  */
 @Override
 public void sendOtp(String mobile) {

     // Generate a random 6-digit OTP
     String otp = generateOtp();

     // Calculate OTP expiry time (current time + 5 minutes)
     LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

     // Store OTP and expiry against the mobile number
     // If OTP already exists, it will be replaced
     otpStore.put(mobile, new OtpData(otp, expiry));

     // TEMPORARY: Console output for testing
     // In real systems, integrate SMS/Email provider here
     System.out.println("OTP for " + mobile + " = " + otp);
 }

 @Override
 public boolean verifyOtp(String mobile, String otp) {

     // Step 1: Retrieve the OTP data associated with the given mobile number
     // If no entry exists, it means OTP was never generated or already expired/removed
     OtpData data = otpStore.get(mobile);

     // Step 2: Validate OTP existence
     // If data is null, OTP is either not generated or already expired
     if (data == null) {
         throw new BadRequestException("OTP expired or not generated");
     }

     // Step 3: Check OTP expiry time
     // If current time is after the stored expiry time, OTP is no longer valid
     if (LocalDateTime.now().isAfter(data.getExpiry())) {

         // Remove expired OTP from store to prevent memory leak
         otpStore.remove(mobile);

         // Inform client that OTP has expired
         throw new BadRequestException("OTP expired");
     }

     // Step 4: Validate OTP value
     // Compare user-provided OTP with stored OTP
     if (!data.getOtp().equals(otp)) {

         // OTP exists and is not expired, but value does not match
         throw new BadRequestException("Invalid OTP");
     }

     // Step 5: OTP is valid and verified successfully
     // Remove OTP to enforce one-time usage (prevents reuse attacks)
     otpStore.remove(mobile);

     // Step 6: Return success status
     return true;
 }


 /*
  * Generates a random 6-digit numeric OTP
  *
  * Logic:
  * - nextInt(900000) generates number between 0 and 899999
  * - Adding 100000 ensures minimum value is 100000
  * - Resulting OTP range: 100000 to 999999
  */
 private String generateOtp() {
     return String.valueOf(100000 + new Random().nextInt(900000));
 }
}




/*What Real Companies Do Next (Same Path You’re On)
* -------------------------------------------------

1. Start with ConcurrentHashMap

2. Validate business flow

3. Replace map with Redis

4. Replace println with SMS gateway

5. Add rate limiting

6. Add retry count

7. Add audit logs

Your architecture now supports all of this without refactor.
------------------------------------------------------------

Final Verdict (Clear)

👉 Go with the improved, industry-grade version
👉 Your original code is good for learning, not production
👉 This version is interview-safe and production-aligned

Next Logical Step

Implement UserServiceImpl.verifyOtpAndLogin()

Generate JWT after OTP success

If you want, I will write UserServiceImpl end-to-end next.

*/

