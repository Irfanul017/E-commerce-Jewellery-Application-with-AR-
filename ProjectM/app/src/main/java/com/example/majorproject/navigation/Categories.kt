package com.example.majorproject.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.adapters.ItemAdapter
import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.item
import com.example.majorproject.description.ProductDescription
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class Categories : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: MutableList<item>
    private lateinit var itemAdapter: ItemAdapter

    // Category buttons
    private lateinit var kidsCategory: ImageView
    private lateinit var menCategory: ImageView
    private lateinit var womenCategory: ImageView
    private lateinit var necklaceCategory: ImageView
    private lateinit var ringCategory: ImageView
    private lateinit var braceletCategory: ImageView

    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Initialize Firestore and RecyclerView
        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recycleviewCategories)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemList = mutableListOf()
        itemAdapter = ItemAdapter(baseContext, itemList, this)
        recyclerView.adapter = itemAdapter

        // Initialize category buttons
        kidsCategory = findViewById(R.id.kidsCategory)
        menCategory = findViewById(R.id.menCategory)
        womenCategory = findViewById(R.id.womenCategory)
        necklaceCategory = findViewById(R.id.necklaceCategory)
        ringCategory = findViewById(R.id.ringCategory)
        braceletCategory = findViewById(R.id.braceletCategory)

        // Get the selected category from the Intent (if passed from HomeFragment)
        selectedCategory = intent.getStringExtra("selected_category")

        // Fetch products for the selected category if any
        if (selectedCategory != null) {
            fetchProductsByCategory(selectedCategory)
            highlightSelectedCategoryButton(selectedCategory)
        }

        // Set up click listeners for each category button
        setUpCategoryButtons()
    }

    // Fetch products based on the selected category
    private fun fetchProductsByCategory(category: String?) {
        when (category) {
            "kids" -> fetchProductsByGender("kids")
            "men" -> fetchProductsByGender("men")
            "women" -> fetchProductsByGender("women")
            "necklace" -> fetchProductsByItemType("necklace")
            "ring" -> fetchProductsByItemType("ring")
            "bracelet" -> fetchProductsByItemType("bracelet")
            else -> Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Highlight the selected category button
    private fun highlightSelectedCategoryButton(category: String?) {
        when (category) {
            "kids" -> setButtonSelected(kidsCategory)
            "men" -> setButtonSelected(menCategory)
            "women" -> setButtonSelected(womenCategory)
            "necklace" -> setButtonSelected(necklaceCategory)
            "ring" -> setButtonSelected(ringCategory)
            "bracelet" -> setButtonSelected(braceletCategory)
        }
    }

    // Set selected button background
    private fun setButtonSelected(selectedButton: ImageView) {
        val categoryButtons = listOf(kidsCategory, menCategory, womenCategory, necklaceCategory, ringCategory, braceletCategory)
        categoryButtons.forEach { button ->
            if (button == selectedButton) {
                button.setBackgroundResource(R.drawable.circular_categories_selected_bg)
            } else {
                button.setBackgroundResource(R.drawable.circular_categories_bg)
            }
        }
    }

    // Fetch products based on gender (kids, men, women)
    private fun fetchProductsByGender(gender: String) {
        firestore.collection("Items")
            .whereEqualTo("productSpecification.gender", gender)
            .get()
            .addOnSuccessListener { documents ->
                parseAndDisplayProducts(documents.documents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching items: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fetch products based on item type (necklace, ring, bracelet)
    private fun fetchProductsByItemType(itemType: String) {
        firestore.collection("Items")
            .whereEqualTo("productSpecification.item-type", itemType)
            .get()
            .addOnSuccessListener { documents ->
                parseAndDisplayProducts(documents.documents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching items: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Parse Firestore documents and update RecyclerView
    private fun parseAndDisplayProducts(documents: List<DocumentSnapshot>) {
        itemList.clear()
        for (itemDoc in documents) {
            if (itemDoc.exists()) {
                try {
                    val grossWeightMap = itemDoc.get("grossWeight") as? Map<*, *>
                    val imagesMap = itemDoc.get("images") as? Map<*, *>
                    val priceBreakingMap = itemDoc.get("priceBreaking") as? Map<*, *>
                    val specificationMap = itemDoc.get("productSpecification") as? Map<*, *>
                    val stylingMap = itemDoc.get("styling") as? Map<*, *>

                    // Construct the product object
                    val product = Product(
                        name = itemDoc.getString("productName") ?: "",
                        price = itemDoc.getString("productPrice") ?: "",
                        images = mapOf(
                            "0" to (imagesMap?.get("0").toString() ?: ""),
                            "1" to (imagesMap?.get("1").toString() ?: ""),
                            "2" to (imagesMap?.get("2").toString() ?: ""),
                            "3" to (imagesMap?.get("3").toString() ?: "")
                        ),
                        grossWeight = mapOf(
                            "diamond-weight" to (grossWeightMap?.get("diamond-weight") ?: "")
                        ),
                        priceBreaking = mapOf(
                            "Diamond" to (priceBreakingMap?.get("Diamond").toString() ?: ""),
                            "Making Charges" to (priceBreakingMap?.get("Making Charges")?.toString() ?: ""),
                            "Metal" to (priceBreakingMap?.get("Metal").toString() ?: ""),
                            "Taxes" to (priceBreakingMap?.get("Taxes").toString() ?: ""),
                            "Total" to (itemDoc.getString("productPrice") ?: "")
                        ),
                        productSpecification = mapOf(
                            "Brand" to "Mahavir",
                            "collection" to (specificationMap?.get("collection")?.toString() ?: ""),
                            "design-type" to (specificationMap?.get("design-type")?.toString() ?: ""),
                            "diamond-carat" to (specificationMap?.get("diamond-carat")?.toString() ?: ""),
                            "diamond-weight" to (specificationMap?.get("diamond-weight")?.toString() ?: ""),
                            "gender" to (specificationMap?.get("gender")?.toString() ?: ""),
                            "item-type" to (specificationMap?.get("item-type")?.toString() ?: ""),
                            "karatage" to (specificationMap?.get("karatage")?.toString() ?: ""),
                            "metal" to (specificationMap?.get("metal")?.toString() ?: "")
                        ),
                        size = mapOf("size" to "5mm"),
                        stock = itemDoc.getString("stockQuantity") ?: "",
                        styling = mapOf(
                            "style" to (stylingMap?.get("design-type")?.toString() ?: "Fancy")
                        )
                    )

                    // Create item for adapter
                    product.images["0"]?.let {
                        val item1 = item(
                            image = it,
                            name = product.name,
                            price = product.price,
                            style = product.styling["style"] ?: "",
                            product = product
                        )
                        itemList.add(item1)
                    }

                } catch (e: Exception) {
                    // Log error and continue with the next product
                    e.printStackTrace()
                }
            }
        }

        // Notify adapter that the data has been updated
        itemAdapter.notifyDataSetChanged()
    }

    // Set up category button click listeners
    private fun setUpCategoryButtons() {
        kidsCategory.setOnClickListener { onCategoryClick("kids") }
        menCategory.setOnClickListener { onCategoryClick("men") }
        womenCategory.setOnClickListener { onCategoryClick("women") }
        necklaceCategory.setOnClickListener { onCategoryClick("necklace") }
        ringCategory.setOnClickListener { onCategoryClick("ring") }
        braceletCategory.setOnClickListener { onCategoryClick("bracelet") }
    }

    // Handle category clicks and update the selected category
    private fun onCategoryClick(category: String) {
        // Update the category in intent
        selectedCategory = category
        fetchProductsByCategory(category)
        highlightSelectedCategoryButton(category)
    }

    // Handle product item click event
    override fun onItemClick(clickedItem: item) {
        val selectedProduct = clickedItem.product

        if (selectedProduct != null) {
            // Create the intent for ProductDescription activity
            val intent = Intent(baseContext, ProductDescription::class.java)
            intent.putExtra("product", selectedProduct) // Pass the Product object
            Log.d("ProductDescription", "Product passed: ${selectedProduct.name}")
            startActivity(intent)
        } else {
            Log.d("ProductDescription", "Selected product not found")
        }
    }
    }

