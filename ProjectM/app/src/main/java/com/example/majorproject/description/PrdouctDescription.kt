package com.example.majorproject.description

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.CartActivity
import com.example.majorproject.R
import com.example.majorproject.RingSizeCalculator
import com.example.majorproject.TryOn
import com.example.majorproject.adapters.ProductImageAdapter
import com.example.majorproject.adapters.ProductSpecificationAdapter
import com.example.majorproject.adapters.SizeItemAdapter
import com.example.majorproject.adapters.ThumbnailAdapter
import com.example.majorproject.adapters.WeightItemAdapter
import com.example.majorproject.admin.AddProducts
import com.example.majorproject.contactus.BookAppoinment
import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.ProductSpecification
import com.example.majorproject.databinding.ActivityProductDescriptionBinding
import com.example.majorproject.navigation.StoreLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProductDescription : AppCompatActivity() {

    private lateinit var binding: ActivityProductDescriptionBinding
    private val firestore = FirebaseFirestore.getInstance()
    var calculatedRingSize: String? = null
    private val RING_SIZE_CALCULATOR_REQUEST_CODE = 1
    private var isPriceBreakExpanded = false
    private var isProductDescExpanded = false
    private var isSizeWeightExpanded = false
    private var isStylingExpanded = false
    private lateinit var adapter: ProductSpecificationAdapter
    private lateinit var product: Product
    private var proRatings:Int=0
    private var productExtra: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)







        product = intent.getSerializableExtra("product") as? Product
            ?: run {
                Log.e("ProductDescription", "Received null product")
                Toast.makeText(this, "Product data is unavailable.", Toast.LENGTH_SHORT).show()
                return // Exit early, but don't finish the activity
            }
        binding.tryOn.setOnClickListener {

            val intent = Intent(this@ProductDescription, TryOn::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        findViewById<Button>(R.id.add_to_cart).setOnClickListener {
            addToCart()
        }


        Log.d("ProductDescription", "Received product: ${product.name}")

        fetchProductData(product)
        setCurrentlySeeingProduct(product.name)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("ProductDescription", "Activity initialized")

        val productReference = FirebaseFirestore.getInstance()
            .collection("ratings")
            .document(binding.productName.text.toString())

        Log.d("Firestore", "Fetching document for product: ${binding.productName.text.toString()}")

        productReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Log the entire document's fields
                Log.d("Firestore", "Document data: ${document.data}")  // Logs all fields of the document

                // If you want to log each field individually:
                document.data?.forEach { (key, value) ->
                    Log.d("Firestore", "Field: $key, Value: $value")
                }

                // Fetch the average rating as before
                val averageRating = document.get("averageRating") as? Number ?: 0
                Log.d("Firestore", "Fetched average rating: $averageRating")

                val averageRatingAsFloat = averageRating.toFloat()  // Convert it to Float if needed
                Log.d("Firestore", "Converted average rating to Float: $averageRatingAsFloat")

                // Set the rating on the RatingBar
                binding.avgRatingBar.rating = averageRatingAsFloat

                // Make the RatingBar read-only (non-interactive)
                binding.avgRatingBar.setIsIndicator(true)  // This will disable user interaction
                Log.d("Firestore", "RatingBar updated to: ${binding.avgRatingBar.rating}")
            } else {
                Log.w("Firestore", "Document does not exist.")
                binding.avgRatingBar.rating = 0f
                binding.avgRatingBar.setIsIndicator(true)  // Make sure the RatingBar is read-only
            }
        }.addOnFailureListener { exception ->
            // If there is an error fetching the document, log it and set the RatingBar to default
            Log.e("Firestore", "Error getting document", exception)
            binding.avgRatingBar.rating = 0f  // Default to 0 if failed to fetch
            binding.avgRatingBar.setIsIndicator(true)  // Make sure the RatingBar is read-only
        }



        setupOnClickListeners()
    }

    private fun addToCart() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userDocRef = firestore.collection("users").document(userEmail)
        val cartItemDocRef = userDocRef.collection("cart").document(product.name)

        // Check if the product already exists in the cart
        cartItemDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Product already exists in the cart
                    Toast.makeText(this, "Product is already in the cart", Toast.LENGTH_SHORT).show()
                } else {
                    // Product doesn't exist, add it to the cart
                    val cartItem = hashMapOf(
                        "image" to product.images["0"],
                        "productName" to product.name,
                        "price" to product.price,
                        "quantity" to 1  // Default quantity is set to 1
                    )

                    cartItemDocRef.set(cartItem, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, CartActivity::class.java))
                            Log.d("ProductDescription", "Successfully added product to cart")
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to add to cart: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ProductDescription", "Error adding product to cart: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to check cart: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("ProductDescription", "Error checking cart: ${e.message}", e)
            }
    }


    private fun setProductSpecificationRecyclerView(product: Product) {

        Log.d("ProductDescription", "setProductSpecificationRecyclerView()")
        Log.d("ProductDescription", "Product data: ${product.productSpecification}")


        val specList = mutableListOf<ProductSpecification>()

        // Safely add product specifications, checking for nulls
        product.productSpecification["Brand"]?.let {
            specList.add(
                ProductSpecification(
                    "Brand",
                    it
                )
            )
        }
        product.productSpecification["collection"]?.let {
            specList.add(
                ProductSpecification(
                    "Collection",
                    it
                )
            )
        }
        product.productSpecification["country-of-origin"]?.let {
            specList.add(
                ProductSpecification(
                    "Country of Origin",
                    it
                )
            )
        }
        product.productSpecification["design-type"]?.let {
            specList.add(
                ProductSpecification(
                    "Design Type",
                    it
                )
            )
        }
        product.productSpecification["diamond-carat"]?.let {
            specList.add(
                ProductSpecification(
                    "Diamond Carat",
                    it
                )
            )
        }
        product.productSpecification["diamond-clarity"]?.let {
            specList.add(
                ProductSpecification(
                    "Diamond Clarity",
                    it
                )
            )
        }
        product.productSpecification["diamond-settings"]?.let {
            specList.add(
                ProductSpecification(
                    "Diamond Settings",
                    it
                )
            )
        }
        product.productSpecification["diamond-weight"]?.let {
            specList.add(
                ProductSpecification(
                    "Diamond Weight",
                    it
                )
            )
        }
        product.productSpecification["gender"]?.let {
            specList.add(
                ProductSpecification(
                    "Gender",
                    it
                )
            )
        }
        product.productSpecification["item-type"]?.let {
            specList.add(
                ProductSpecification(
                    "Item Type",
                    it
                )
            )
        }
        product.productSpecification["jewellery-type"]?.let {
            specList.add(
                ProductSpecification(
                    "Jewellery Type",
                    it
                )
            )
        }
        product.productSpecification["karatage"]?.let {
            specList.add(
                ProductSpecification(
                    "Karatage",
                    it
                )
            )
        }
        product.productSpecification["material-color"]?.let {
            specList.add(
                ProductSpecification(
                    "Material Color",
                    it
                )
            )
        }
        product.productSpecification["metal"]?.let {
            specList.add(
                ProductSpecification(
                    "Metal",
                    it
                )
            )
        }
        product.productSpecification["product-type"]?.let {
            specList.add(
                ProductSpecification(
                    "Product Type",
                    it
                )
            )
        }

        if (specList.isEmpty()) {
            Log.e("ProductDescription", "Specification list is empty!")
            return
        }

        Log.d("ProductDescription", "setProductSpecificationRecyclerView() adapter setup")
        adapter = ProductSpecificationAdapter(specList)
        binding.productSpecificationRV.layoutManager = LinearLayoutManager(this)
        Log.d("ProductDescription", "setProductSpecificationRecyclerView() recycler view setup")
        binding.productSpecificationRV.adapter = adapter
        Log.d(
            "ProductDescription",
            "setProductSpecificationRecyclerView() adapter added to recycler view"
        )
    }


    private fun setupOnClickListeners() {
//        binding.downArrowStyling.setOnClickListener {
//            toggleExpandableLayout(
//                isStylingExpanded,
//                binding.stylingExtandableLayout,
//                binding.downArrowStyling,
//                binding.stylingView
//            )
//            isStylingExpanded = !isStylingExpanded
//        }

        binding.downArrowProductDes.setOnClickListener {
            try {
                Log.d("ProductDescription", "Product Description")
                toggleExpandableLayout(
                    isProductDescExpanded,
                    binding.productDesExtandableLayout,
                    binding.downArrowProductDes,
                    binding.productDesSubtitle
                )
                Log.d("ProductDescription", "Product Description toggleExpandableLayout called")
                isProductDescExpanded = !isProductDescExpanded
            } catch (e: Exception) {
                Log.e("ProductDescription", "Error: ${e.message}", e)

            }
        }

        binding.downArrowPriceBreak.setOnClickListener {
            toggleExpandableLayout(
                isPriceBreakExpanded,
                binding.priceBreakExtandableLayout,
                binding.downArrowPriceBreak,
                binding.priceBreakSubtitle
            )
            isPriceBreakExpanded = !isPriceBreakExpanded
        }

        binding.sizeAndWeightLayout.setOnClickListener {
            toggleExpandableLayout(
                isSizeWeightExpanded,
                binding.sizeAndWeightExtandableLayout,
                binding.downArrowSizeWeight,
                binding.sizeInMmAndGrOssWeightInGms
            )
            isSizeWeightExpanded = !isSizeWeightExpanded
        }

        binding.appointmentBookingButton.setOnClickListener {
           startActivity(Intent(this@ProductDescription,BookAppoinment::class.java))
        }

        binding.storeLocationLayout.setOnClickListener {
            startActivity(Intent(this, StoreLocation::class.java))
        }

        binding.talkToExpertButton.setOnClickListener {
            val phoneNumber = "+919082953372"  // Replace with your actual WhatsApp number
            val message =
                "Hello, I'm interested in your jewelry collection. Could you please provide more details on your products?"

            val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            val whatsappInstalled =
                isAppInstalled("com.whatsapp") || isAppInstalled("com.whatsapp.w4b")

            if (whatsappInstalled) {
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.e("WhatsApp Intent", "Error opening WhatsApp: Activity not found", e)
                    Toast.makeText(
                        this,
                        "Error: WhatsApp app is not installed or could not be opened.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sizeCalculatorBtn.setOnClickListener {
            val intent = Intent(this, RingSizeCalculator::class.java)
            startActivityForResult(intent, RING_SIZE_CALCULATOR_REQUEST_CODE)
        }


        binding.postButton.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()

            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

            if (userEmail.isNotEmpty()) {

                val productReference = FirebaseFirestore.getInstance()
                    .collection("ratings")
                    .document(binding.productName.text.toString())

                productReference.get().addOnSuccessListener { document ->
                    val existingRatingsMap = document.get("ratings") as? Map<String, Int> ?: hashMapOf()

                    val updatedRatingsMap = existingRatingsMap.toMutableMap()

                        updatedRatingsMap[userEmail] = rating
                    productReference.update(
                        "ratings", updatedRatingsMap
                    ).addOnSuccessListener {
                        calculateAverageRating(updatedRatingsMap)

                        Toast.makeText(this, "Rating posted successfully!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {

                        val ratingMap= hashMapOf(
                            userEmail to rating
                        )
                        val productMap= hashMapOf(
                            "averageRating" to 0,
                            "rating" to ratingMap
                        )
                        productReference.set(productMap).addOnSuccessListener {
                            Toast.makeText(this, "Rating posted successfully!", Toast.LENGTH_SHORT).show()
                        }

                        calculateAverageRating(ratingMap)
                    }
                }.addOnFailureListener {
                    val ratingMap= hashMapOf(
                        userEmail to rating
                    )
                    val productMap= hashMapOf(
                        "averageRating" to 0,
                        "rating" to ratingMap
                    )
                    productReference.set(productMap)

                    calculateAverageRating(ratingMap)

                }
            } else {

                Toast.makeText(this, "Please log in to post a rating", Toast.LENGTH_SHORT).show()
            }


    }

        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private fun calculateAverageRating(ratingsMap: Map<String, Int>) {
        val totalRatings = ratingsMap.values.sum()  // Sum all ratings
        val ratingCount = ratingsMap.size  // Number of ratings

        // Calculate the average rating
        val averageRating = if (ratingCount > 0) {
            totalRatings.toDouble() / ratingCount
        } else {
            0.0
        }

        // Store the average rating in a variable (e.g., proRatings)
        val proRatings = averageRating.toFloat()

        // Optionally, update the UI or store the average rating somewhere
        Log.d("Average Rating", "The average rating is: $proRatings")
        // You can also save the average rating back to Firestore if needed
        // Example: update the document with the average rating
        updateProductWithAverageRating(proRatings)
    }

    // Optionally, update Firestore with the calculated average rating
    private fun updateProductWithAverageRating(averageRating: Float) {
        val productReference = FirebaseFirestore.getInstance()
            .collection("ratings")
            .document(binding.productName.text.toString())

        productReference.update(
            "averageRating", averageRating  // Store the average rating in the "averageRating" field
        ).addOnSuccessListener {
            binding.avgRatingBar.rating=averageRating
            Log.d("Average Rating", "Product average rating updated successfully")
        }.addOnFailureListener { exception ->

        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            // Log the error message if the app is not found
            Log.e("App Install Check", "App not found: $packageName", e)

            // Show the actual error message in the Toast
            Toast.makeText(this, "Error: App not found for package: $packageName", Toast.LENGTH_SHORT).show()

            false
        } catch (e: Exception) {
            // Catch any other unexpected exception
            Log.e("App Install Check", "Unexpected error while checking for app: $packageName", e)

            // Show the unexpected error message in the Toast
            Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()

            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RING_SIZE_CALCULATOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            calculatedRingSize = null
            calculatedRingSize = data?.getStringExtra("ringSize")
            displayRingSize(calculatedRingSize)

            Log.d("ringSize", "Ring: $calculatedRingSize")
        }
    }

    private fun fetchProductData(product: Product) {
        Log.d("ProductDescription", "Fetching product data")
        fetchBasicDescription(product)
        fetchPriceBreaking(product)
        fetchStyling(product)
        fetchStockFromFirebase(product)
        setProductSpecificationRecyclerView(product)
        fetchSizeandWeight(product)
        setUpViewPager(product)

    }
    private fun setUpViewPager(product: Product) {
        val imageUrls = product.images.values.toList()
       

        if (imageUrls.isNotEmpty()) {
            Log.d("ProductDescription", "Image URLs: $imageUrls")
            val imageAdapter = ProductImageAdapter(imageUrls)
            val viewPager: ViewPager2 = binding.viewPager
            viewPager.adapter = imageAdapter  // No need for layoutManager with ViewPager2

            val dotsIndicator: DotsIndicator = binding.dotsIndicator
            dotsIndicator.setViewPager2(viewPager)

            setUpRecyclerView(viewPager, imageUrls)

            Log.d("ProductDescription", "ViewPager adapter set with images")
        } else {
            Log.e("ProductDescription", "No images available for this product")
        }
    }

    private fun setUpRecyclerView(viewPager: ViewPager2, imageUrls: List<String>) {
        val thumbnailRecyclerView = binding.recyclerView
        thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val thumbnailAdapter = ThumbnailAdapter(imageUrls)
        thumbnailRecyclerView.adapter = thumbnailAdapter

        if (imageUrls.isNotEmpty()) {
            thumbnailAdapter.setOnItemClickListener { position ->
                viewPager.currentItem = position
            }
        } else {
            Log.e("ProductDescription", "No image URLs available for the adapter.")
        }
    }

    private fun fetchStyling(product: Product) {
        binding.stylingDescription.text=product.styling["style"].toString()
        binding.stylingDescription.text=product.styling["des"].toString()
    }

    private fun fetchPriceBreaking(product: Product) {

        binding.productDiamondPrice.text = product.priceBreaking["Diamond"]
        binding.productMakingCharges.text = product.priceBreaking["Making Charges"]
        binding.productMetalPrice.text = product.priceBreaking["Metal"]
        binding.priceTaxes.text = product.priceBreaking["Taxes"]
        binding.priceTotal.text = product.priceBreaking["Total"]
    }

    private fun fetchBasicDescription(product: Product) {
        binding.productName.text=product.name.toString()
        binding.productPrice.text=product.price.toString()

    }

    private fun fetchSizeandWeight(product: Product) {
        try {
            val sizeMap =product.size
            if (sizeMap != null && sizeMap.isNotEmpty()) {
                val sizeAdapter = SizeItemAdapter(sizeMap)
                binding.sizeRecycleView.adapter = sizeAdapter
                binding.sizeRecycleView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                Log.d("ProductDescription", "Size list populated")
            } else {
                Log.d("ProductDescription", "Size list is null or empty")
            }

            val weightMap = product.grossWeight
            if (weightMap.isNotEmpty()) {
                val weightAdapter = WeightItemAdapter(weightMap)
                binding.weightRecycleView.adapter = weightAdapter
                binding.weightRecycleView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                Log.d("Firestore", "Weight list populated")
            } else {
                Log.d("Firestore", "Weight list is null or empty")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error processing document data: ${e.message}")
        }
    }

    private fun displayRingSize(calculatedRingSize: String?) {
        if (calculatedRingSize != null) {
            Log.d("ringSize", "Size: $calculatedRingSize")

            binding.calculatedSizeLayout.visibility = View.VISIBLE

            firestore.collection("Products")
                .document("Rings")
                .collection("Women")
                .document("Daily-Wear")
                .get()
                .addOnSuccessListener { document ->
                    val availableSizes = document.get("size") as? List<String>
                    if (availableSizes != null && availableSizes.contains(calculatedRingSize)) {
                        binding.calculatedSizeDes.text= "Size available: $calculatedRingSize"
                        Log.d("ringSize", "Size available: $calculatedRingSize")
                    } else {
                        Log.d("ringSize", "Size not available: $calculatedRingSize")
                    }
                }
        }
    }

    private fun fetchStockFromFirebase(product: Product) {
        val stock = product.stock.toInt()
        Log.d("Firestore", "Stock fetched: ${stock}")
        if (stock <= 3) {
            binding.stockAvailableLayout.visibility = View.VISIBLE
            binding.stockAvailableTxt.text = "Only ${stock} left in stock"
        } else {
            binding.stockAvailableLayout.visibility = View.GONE
        }
    }

    private fun toggleExpandableLayout(
        isExpanded: Boolean,
        expandableLayout: View,
        arrow: View,
        titleView: View
    ) {
        if (isExpanded) {
            Log.d("ProductDescription", "Product Description toggleExpandableLayout called arrow expanded ")
            expandableLayout.visibility = View.GONE
            arrow.rotation = 0f
            titleView.visibility=View.VISIBLE
        } else {
            Log.d("ProductDescription", "Product Description toggleExpandableLayout called arrow expanded ")
            expandableLayout.visibility = View.VISIBLE
            arrow.rotation = 180f
            titleView.visibility=View.GONE
        }
        Log.d("ProductDescription", "toggleExpandableLayout finished")
    }
    private fun setCurrentlySeeingProduct(productName: String) {
        val userDocRef = firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.email!!) // replace "YourUserID" with the actual user ID or retrieve it dynamically

        val updateData = mapOf("currentlySeeing" to productName)
        userDocRef.update(updateData)
            .addOnSuccessListener {
                Log.d("ProductDescription", "Successfully updated currentlySeeing with product: $productName")
            }
            .addOnFailureListener { e ->
                Log.e("ProductDescription", "Error updating currentlySeeing field: ${e.message}", e)
            }
    }
}
