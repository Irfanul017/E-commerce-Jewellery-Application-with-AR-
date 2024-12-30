package com.example.majorproject.Search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.adapters.ItemAdapter

import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.item
import com.example.majorproject.description.ProductDescription
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity(),ItemAdapter.OnItemClickListener {

    private lateinit var searchView: SearchView
    private lateinit var popularSearchRecyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private val itemList = mutableListOf<item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        searchView = findViewById(R.id.search_view)
        popularSearchRecyclerView = findViewById(R.id.recycleview_popular_search)
        progressBar = findViewById(R.id.progressBarItems)

        // Set up RecyclerView
        popularSearchRecyclerView.layoutManager = GridLayoutManager(this,2)

        // Initialize the adapter
        itemAdapter = ItemAdapter(this, itemList, this
          )
        popularSearchRecyclerView.adapter = itemAdapter
 findViewById<ImageView>(R.id.back).setOnClickListener {
onBackPressed()
 }

        // Fetch popular search items
        fetchPopularSearchItems()

        // Set a listener for the search query submission
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // Send the query to SearchResultActivity
                    val intent = Intent(this@SearchActivity, SearchResult::class.java)
                    intent.putExtra("SEARCH_QUERY", query)  // Pass the search query
                    startActivity(intent)
                    return true
                } else {
                    Toast.makeText(this@SearchActivity, "Please enter a search query.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can implement real-time search here if necessary
                return false
            }
        })
    }

    // Function to fetch popular search items
    private fun fetchPopularSearchItems() {
        progressBar.visibility = View.VISIBLE

        firestore.collection("Items")
            // Adjust according to your schema
            .limit(10)  // Limit to top 10 popular items
            .get()
            .addOnSuccessListener { documents ->
                parseAndDisplayProducts(documents.documents)
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching popular search items: ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
    }

    // Function to parse and display products in RecyclerView
    private fun parseAndDisplayProducts(documents: List<DocumentSnapshot>) {
        itemList.clear()  // Clear the previous list before adding new items

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
                    Log.e("fetchPopularSearchItems", "Error processing document: ${itemDoc.id}", e)
                }
            } else {
                Log.w("fetchPopularSearchItems", "Document does not exist: ${itemDoc.id}")
            }
        }

        // Notify the adapter of the updated item list
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

