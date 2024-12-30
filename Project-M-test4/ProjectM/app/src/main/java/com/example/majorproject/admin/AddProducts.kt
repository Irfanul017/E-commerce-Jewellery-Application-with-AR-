package com.example.majorproject.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.majorproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddProducts : AppCompatActivity() {

    private lateinit var productImageView1: ImageView
    private lateinit var productImageView2: ImageView
    private lateinit var productImageView3: ImageView
    private lateinit var productImageView4: ImageView
    private lateinit var btnSaveProduct: Button
    private lateinit var spinnerProductCategoryGen: Spinner
    private lateinit var spinnerProductCategory: Spinner

    private var selectedGender: String = "DefaultGender"
    private var selectedCategory: String = "DefaultCategory"
    private lateinit var storageReference: StorageReference

    // Store each selected image URI
    private val imageUris = arrayOfNulls<Uri>(4)
    private val imageUrls = arrayOfNulls<String>(4) // Store URLs to save in Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_products)

        spinnerProductCategory = findViewById(R.id.spinnerProductCategory)
        spinnerProductCategoryGen = findViewById(R.id.spinnerProductCategoryGen)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)

        productImageView1 = findViewById(R.id.img1)
        productImageView2 = findViewById(R.id.img2)
        productImageView3 = findViewById(R.id.img3)
        productImageView4 = findViewById(R.id.img4)

        setupPermissionAndListeners()

        btnSaveProduct.setOnClickListener {
            uploadAllImagesAndSaveDetails()
        }
    }

    private fun setupPermissionAndListeners() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }

        productImageView1.setOnClickListener { openGallery(0) }
        productImageView2.setOnClickListener { openGallery(1) }
        productImageView3.setOnClickListener { openGallery(2) }
        productImageView4.setOnClickListener { openGallery(3) }
    }

    private fun openGallery(imageIndex: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000 + imageIndex) // requestCode differentiates images
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val index = requestCode - 1000
            val uri = data.data
            imageUris[index] = uri

            // Set the image preview
            when (index) {
                0 -> productImageView1.setImageURI(uri)
                1 -> productImageView2.setImageURI(uri)
                2 -> productImageView3.setImageURI(uri)
                3 -> productImageView4.setImageURI(uri)
            }
        }
    }

    private fun uploadAllImagesAndSaveDetails() {
        updateStorageReference()

        // Upload each image one by one
        imageUris.forEachIndexed { index, uri ->
            uri?.let {
                uploadImage(uri, index) { imageUrl ->
                    imageUrls[index] = imageUrl

                    // When all URLs are uploaded, save product details
                    if (imageUrls.all { it != null }) {
                        saveProductDetails()
                    }
                }
            }
        }
    }

    private fun uploadImage(uri: Uri, index: Int, onSuccess: (String) -> Unit) {
        val fileName = "product_image_${System.currentTimeMillis()}_$index"
        val imageRef = storageReference.child(fileName)

        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStorageReference() {
        storageReference = FirebaseStorage.getInstance().reference
            .child("product/ring/$selectedGender/$selectedCategory")
    }

    private fun saveProductDetails() {
        val product = hashMapOf(
            "productName" to findViewById<EditText>(R.id.etProductName).text.toString(),
            "availableSizes" to findViewById<EditText>(R.id.etProductSizes).text.toString(),
            "stockQuantity" to findViewById<EditText>(R.id.etProductStock).text.toString(),
            "productPrice" to findViewById<EditText>(R.id.etProductPrice).text.toString(),
            // Add other fields here...

            // Add image URLs
            "imageUrls" to imageUrls.filterNotNull()
        )

        val productReference = FirebaseFirestore.getInstance()
            .collection("Products")
            .document("Rings")
            .collection(selectedGender)
            .document(selectedCategory)
            .collection("Items")

        productReference.add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.etProductName).text.clear()
        findViewById<ImageView>(R.id.img1).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img2).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img3).setImageResource(R.drawable.ic_placeholder_image)
        findViewById<ImageView>(R.id.img4).setImageResource(R.drawable.ic_placeholder_image)
        imageUris.fill(null)
        imageUrls.fill(null)
    }
}
