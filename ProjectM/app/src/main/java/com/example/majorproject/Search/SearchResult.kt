package com.example.majorproject.Search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

class SearchResult : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: MutableList<item>
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        // Initialize Firestore and RecyclerView
        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemList = mutableListOf()
        itemAdapter = ItemAdapter(baseContext, itemList, this)
        recyclerView.adapter = itemAdapter

        // Get the query passed from the SearchActivity
        val query = intent.getStringExtra("SEARCH_QUERY") ?: ""
        findViewById<TextView>(R.id.textViewQuery).setText("Showing Results for ${query}")
        // Fetch data based on the query
        fetchProductsBasedOnQuery(query)
    }

    // Fetch products based on the query
    private fun fetchProductsBasedOnQuery(query: String) {
        val lowerCaseQuery = query.lowercase()

        // Start with the base query that checks for lowercase product name
        firestore.collection("Items")
            .get()
            .addOnSuccessListener { documents ->
                val filteredDocuments = documents.filter { doc ->
                    // Convert product name to lowercase for case-insensitive comparison
                    val productName = doc.getString("productName") ?: ""
                    productName.lowercase().contains(lowerCaseQuery)
                }

                if (filteredDocuments.isEmpty()) {
                    // If no name matches, fallback to checking for item type
                    fallbackToItemTypeQuery(query)
                } else {
                    parseAndDisplayProducts(filteredDocuments)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fallback method to fetch products by item type if no name matches
    private fun fallbackToItemTypeQuery(query: String) {
        when {
            query.contains("ring", ignoreCase = true) -> fetchProductsByItemType("ring")
            query.contains("bracelet", ignoreCase = true) -> fetchProductsByItemType("bracelet")
            query.contains("men", ignoreCase = true) -> fetchProductsByGender("men")
            query.contains("women", ignoreCase = true) -> fetchProductsByGender("women")
            query.contains("kids", ignoreCase = true) -> fetchProductsByGender("kids")
            else -> {
                // Handle case where no matching query is found
                Toast.makeText(this, "No products found for your search", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch products by item type
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

    // Fetch products by gender
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
                    Log.e("fetchAllProducts", "Error processing document: ${itemDoc.id}", e)
                }
            } else {
                Log.w("fetchAllProducts", "Document does not exist: ${itemDoc.id}")
            }
        }

        itemAdapter.notifyDataSetChanged()
    }

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
