package com.example.majorproject.description

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.R
import com.example.majorproject.RingSizeCalculator
import com.example.majorproject.TryOn
import com.example.majorproject.adapters.ProductImageAdapter
import com.example.majorproject.adapters.ProductSpecificationAdapter
import com.example.majorproject.adapters.SizeItemAdapter
import com.example.majorproject.adapters.ThumbnailAdapter
import com.example.majorproject.adapters.WeightItemAdapter
import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.ProductSpecification
import com.example.majorproject.databinding.ActivityProductDescriptionBinding
import com.google.firebase.firestore.FirebaseFirestore
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
    private var productExtra:Product?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDescriptionBinding.inflate(layoutInflater)
       setContentView(binding.root)

        binding.tryOn.setOnClickListener{
            startActivity(Intent(this@ProductDescription,TryOn::class.java))
        }
        product = intent.getSerializableExtra("product") as? Product
            ?: run {
                Log.e("ProductDescription", "Received null product")
                Toast.makeText(this, "Product data is unavailable.", Toast.LENGTH_SHORT).show()
                return // Exit early, but don't finish the activity
            }

        Log.d("ProductDescription", "Received product: ${product.name}")

        fetchProductData(product)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("ProductDescription", "Activity initialized")


        setupOnClickListeners()
    }
    fun convertDrawableToBase64(context: Context, drawableResId: Int): String? {
        // Step 1: Convert Drawable to Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)

        // Step 2: Convert Bitmap to Base64 String
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // PNG format
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun setProductSpecificationRecyclerView(product: Product) {

        Log.d("ProductDescription", "setProductSpecificationRecyclerView()")
        Log.d("ProductDescription", "Product data: ${product.productSpecification}")


        val specList = mutableListOf<ProductSpecification>()

        // Safely add product specifications, checking for nulls
        product.productSpecification["Brand"]?.let { specList.add(ProductSpecification("Brand", it)) }
        product.productSpecification["collection"]?.let { specList.add(ProductSpecification("Collection", it)) }
        product.productSpecification["country-of-origin"]?.let { specList.add(ProductSpecification("Country of Origin", it)) }
        product.productSpecification["design-type"]?.let { specList.add(ProductSpecification("Design Type", it)) }
        product.productSpecification["diamond-carat"]?.let { specList.add(ProductSpecification("Diamond Carat", it)) }
        product.productSpecification["diamond-clarity"]?.let { specList.add(ProductSpecification("Diamond Clarity", it)) }
        product.productSpecification["diamond-settings"]?.let { specList.add(ProductSpecification("Diamond Settings", it)) }
        product.productSpecification["diamond-weight"]?.let { specList.add(ProductSpecification("Diamond Weight", it)) }
        product.productSpecification["gender"]?.let { specList.add(ProductSpecification("Gender", it)) }
        product.productSpecification["item-type"]?.let { specList.add(ProductSpecification("Item Type", it)) }
        product.productSpecification["jewellery-type"]?.let { specList.add(ProductSpecification("Jewellery Type", it)) }
        product.productSpecification["karatage"]?.let { specList.add(ProductSpecification("Karatage", it)) }
        product.productSpecification["material-color"]?.let { specList.add(ProductSpecification("Material Color", it)) }
        product.productSpecification["metal"]?.let { specList.add(ProductSpecification("Metal", it)) }
        product.productSpecification["product-type"]?.let { specList.add(ProductSpecification("Product Type", it)) }

        if (specList.isEmpty()) {
            Log.e("ProductDescription", "Specification list is empty!")
            return
        }

        Log.d("ProductDescription", "setProductSpecificationRecyclerView() adapter setup")
        adapter = ProductSpecificationAdapter(specList)
        binding.productSpecificationRV.layoutManager = LinearLayoutManager(this)
        Log.d("ProductDescription", "setProductSpecificationRecyclerView() recycler view setup")
        binding.productSpecificationRV.adapter = adapter
        Log.d("ProductDescription", "setProductSpecificationRecyclerView() adapter added to recycler view")
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
            }catch( e:Exception){
                Log.e("ProductDescription", "Error: ${e.message}", e)

            }            }

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

        binding.storeLocationButton.setOnClickListener {
            try {
                if (binding.storeLocationLayout != null && binding.outerScrollView != null) {
                    val originalColor = ContextCompat.getColor(this, R.color.white)
                    binding.storeLocationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

                    binding.outerScrollView.post {
                        val targetPosition = binding.storeLocationLayout.top
                        Log.d("ProductDescription", "Scrolling to position: $targetPosition")

                        if (targetPosition >= 0 && targetPosition <= binding.outerScrollView.height) {
                            binding.outerScrollView.smoothScrollTo(0, targetPosition)
                        } else {
                            Log.e("ProductDescription", "Invalid scroll position: $targetPosition")
                        }
                    }

                    lifecycleScope.launch {
                        delay(2000)
                        val animator = ObjectAnimator.ofArgb(
                            binding.storeLocationLayout,
                            "backgroundColor",
                            ContextCompat.getColor(this@ProductDescription, R.color.transparent),
                            originalColor
                        )
                        animator.duration = 1000
                        animator.start()
                    }
                } else {
                    Log.e("ProductDescription", "Null reference: storeLocationLayout or outerScrollView is null")
                }
            } catch (e: NullPointerException) {
                Log.e("ProductDescription", "NullPointerException in setStoreLocationButton: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductDescription", "Exception in setStoreLocationButton: ${e.message}")
            }
        }

        binding.sizeCalculatorBtn.setOnClickListener {
            val intent = Intent(this, RingSizeCalculator::class.java)
            startActivityForResult(intent, RING_SIZE_CALCULATOR_REQUEST_CODE)
        }

        binding.backButton.setOnClickListener {
            finish()
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
        val imageList = listOf( convertDrawableToBase64(this, R.drawable.necklace1),convertDrawableToBase64(this, R.drawable.necklace2),convertDrawableToBase64(this, R.drawable.necklace3),convertDrawableToBase64(this, R.drawable.necklace4),)

        if (imageUrls.isNotEmpty()) {
            Log.d("ProductDescription", "Image URLs: $imageUrls")
            val imageAdapter = ProductImageAdapter(imageList)
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
            if (weightMap != null && weightMap.isNotEmpty()) {
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

}
