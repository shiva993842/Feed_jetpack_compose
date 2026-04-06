package com.example.feed.Repository

import com.example.feed.Model.OtpModel
import com.example.feed.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AuthRepository {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val EMAILJS_SERVICE_ID  = "service_c19lb2q"
    private val EMAILJS_TEMPLATE_ID = "template_n2zx7pj"
    private val EMAILJS_PUBLIC_KEY  = "5rch85W2OY4PLePpz"

    // ── Safe Firestore document ID ────────────
    private fun emailToDocId(email: String): String {
        return email
            .lowercase()
            .replace("@", "_at_")
            .replace(".", "_")
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    private fun generateOtp(): String {
        return (100000..999999).random().toString()
    }

    // ── Send OTP Email via EmailJS ────────────
    private suspend fun sendOtpEmail(
        email : String,
        otp   : String,
        name  : String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("service_id",  EMAILJS_SERVICE_ID)
                put("template_id", EMAILJS_TEMPLATE_ID)
                put("user_id",     EMAILJS_PUBLIC_KEY)
                put("template_params", JSONObject().apply {
                    put("to_email", email)
                    put("to_name",  name)
                    put("passcode", otp)
                    put("time",     "10 minutes")
                })
            }

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("https://api.emailjs.com/api/v1.0/email/send")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("origin", "http://localhost")
                .build()

            val response = client.newCall(request).execute()
            android.util.Log.d("EmailJS",
                "Code: ${response.code} Body: ${response.body?.string()}")
            response.isSuccessful

        } catch (e: Exception) {
            android.util.Log.e("EmailJS", "Error: ${e.message}")
            false
        }
    }

    // ── Save OTP + Send Email ─────────────────
    suspend fun sendOtp(
        email : String,
        name  : String
    ): Result<Unit> {
        return try {
            val otp       = generateOtp()
            val safeDocId = emailToDocId(email)
            val expiresAt = System.currentTimeMillis() + (10L * 60 * 1000)

            val otpModel = OtpModel(
                email     = email,
                code      = otp,
                expiresAt = expiresAt
            )

            firestore
                .collection("otp_codes")
                .document(safeDocId)
                .set(otpModel)
                .await()

            val sent = sendOtpEmail(email, otp, name)
            if (sent) Result.success(Unit)
            else Result.failure(Exception("Failed to send OTP email!"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Check Login Credentials + Send OTP ───
    suspend fun checkLoginCredentials(
        email    : String,
        password : String
    ): Result<Unit> {
        return try {
            // Step 1: Verify password is correct
            auth.signInWithEmailAndPassword(email, password).await()

            // Step 2: Sign out — will sign in after OTP
            auth.signOut()

            // Step 3: Generate + save OTP
            val otp       = generateOtp()
            val safeDocId = emailToDocId(email)
            val expiresAt = System.currentTimeMillis() + (10L * 60 * 1000)

            val otpModel = OtpModel(
                email     = email,
                code      = otp,
                expiresAt = expiresAt
            )

            firestore
                .collection("otp_codes")
                .document(safeDocId)
                .set(otpModel)
                .await()

            // Step 4: Send OTP email
            val sent = sendOtpEmail(email, otp, email)
            if (sent) Result.success(Unit)
            else Result.failure(Exception("Failed to send OTP!"))

        } catch (e: Exception) {
            Result.failure(Exception("Invalid email or password!"))
        }
    }

    // ── Verify Login OTP ──────────────────────
    suspend fun verifyLoginOtp(
        email      : String,
        enteredOtp : String,
        password   : String
    ): Result<Unit> {
        return try {
            val safeDocId = emailToDocId(email)

            val doc = firestore
                .collection("otp_codes")
                .document(safeDocId)
                .get()
                .await()

            val otpModel = doc.toObject(OtpModel::class.java)
                ?: return Result.failure(Exception("OTP not found! Please resend."))

            if (System.currentTimeMillis() > otpModel.expiresAt)
                return Result.failure(Exception("OTP expired! Please resend."))

            if (otpModel.code != enteredOtp)
                return Result.failure(Exception("Wrong OTP! Please try again."))

            // Sign in properly after OTP verified
            auth.signInWithEmailAndPassword(email, password).await()

            // Delete OTP
            firestore
                .collection("otp_codes")
                .document(safeDocId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Verify Register OTP ───────────────────
    suspend fun verifyOtp(
        email     : String,
        enteredOtp: String,
        fullName  : String,
        username  : String,
        phone     : String,
        password  : String
    ): Result<Unit> {
        return try {
            val safeDocId = emailToDocId(email)

            val doc = firestore
                .collection("otp_codes")
                .document(safeDocId)
                .get()
                .await()

            val otpModel = doc.toObject(OtpModel::class.java)
                ?: return Result.failure(Exception("OTP not found! Please resend."))

            if (System.currentTimeMillis() > otpModel.expiresAt)
                return Result.failure(Exception("OTP expired! Please resend."))

            if (otpModel.code != enteredOtp)
                return Result.failure(Exception("Wrong OTP! Please try again."))

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user!!

            val userModel = UserModel(
                uid       = user.uid,
                fullName  = fullName,
                username  = username,
                email     = email,
                phone     = phone,
                createdAt = System.currentTimeMillis()
            )

            firestore
                .collection("users")
                .document(user.uid)
                .set(userModel)
                .await()

            firestore
                .collection("otp_codes")
                .document(safeDocId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}