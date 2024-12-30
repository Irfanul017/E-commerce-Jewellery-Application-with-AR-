package com.example.majorproject.navigation

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.BannerAdapter
import com.example.majorproject.R
import com.example.majorproject.adapters.ItemAdapter
import com.example.majorproject.dataClass.Product
import com.example.majorproject.dataClass.item
import com.example.majorproject.description.ProductDescription
import com.google.firebase.firestore.FirebaseFirestore
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), ItemAdapter.OnItemClickListener {

    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: MutableList<item>
    private var product: Product? = null
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerImages = mutableListOf<String>()
    private lateinit var viewPager:ViewPager2
    private lateinit var progressBar:ProgressBar
    private lateinit var dots:DotsIndicator
    private lateinit var progressBarProducts:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        itemList = mutableListOf()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycleview)
        viewPager=view.findViewById(R.id.viewPager2)
        progressBar=view.findViewById(R.id.progressBar8)

        progressBarProducts=view.findViewById(R.id.progressBarItems)

        recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        view.findViewById<TextView>(R.id.seeAll).setOnClickListener{
        startActivity(Intent(context,Categories::class.java))
        }

        adapter = ItemAdapter(requireContext(), itemList, this)
        recyclerView?.adapter = adapter
         fetchBannerImages(view)

        fetchAllAuspiciousProducts()
        val categoryKids = view.findViewById<ImageView>(R.id.kids)
        val categoryMen = view.findViewById<ImageView>(R.id.men)
        val categoryWomen = view.findViewById<ImageView>(R.id.women)
        val categoryRing = view.findViewById<ImageView>(R.id.ring)
        val categoryBracelet = view.findViewById<ImageView>(R.id.bracelet)
        val categoryNecklace = view.findViewById<ImageView>(R.id.necklace)
// Add other categories similarly

        categoryKids.setOnClickListener {
            navigateToCategoryActivity("kids")
        }

        categoryMen.setOnClickListener {
            navigateToCategoryActivity("men")
        }

        categoryWomen.setOnClickListener {
            navigateToCategoryActivity("women")
        }
        categoryRing.setOnClickListener {
            navigateToCategoryActivity("ring")
        }
        categoryBracelet.setOnClickListener {
            navigateToCategoryActivity("bracelet")
        }
        categoryNecklace.setOnClickListener {
            navigateToCategoryActivity("necklace")
        }
        return view
    }
    private fun fetchBannerImages(view:View) {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        val bannerRef = db.collection("banners").document("bannerImage")

        bannerRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    bannerImages.clear()
                    document.getString("banner1")?.let { bannerImages.add(it) }
                    document.getString("banner2")?.let { bannerImages.add(it) }
                    // Set up adapter and ViewPager with loaded images
                    bannerAdapter = BannerAdapter(bannerImages)
                    viewPager.adapter = bannerAdapter
                    dots=view.findViewById(R.id.dotsBanner)
                    dots.attachTo(viewPager)
                    Log.d("fetchBannerImages", "Successfully loaded banner images")
                } else {
                    Log.e("fetchBannerImages", "No banner images found in document")
                    Toast.makeText(context, "No banners found", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("fetchBannerImages", "Error fetching banner images", e)
                Toast.makeText(context, "Error loading banners", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                progressBar.visibility = View.GONE
                Log.e("fetchBannerImages", "Banner image fetch canceled")
                Toast.makeText(context, "Banner loading canceled", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("fetchBannerImages", "Error: Task was unsuccessful")
                }
                progressBar.visibility = View.GONE
            }
    }


    private fun fetchAllAuspiciousProducts() {
        val db = FirebaseFirestore.getInstance()
        progressBarProducts.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Query items where the "item-type" in productSpecification equals "ring"
                val productTypeRef = db.collection("Items")
                    .whereEqualTo("productSpecification.item-type", "ring")

                // Fetch the filtered items
                val itemsSnapshot = productTypeRef.get().await()
                if (itemsSnapshot.isEmpty) {
                    Log.e("fetchAllProducts", "No items found with item-type: ring")
                    return@launch
                }

                for (itemDoc in itemsSnapshot.documents) {
                    if (itemDoc.exists()) {
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
                                "Making Charges" to (priceBreakingMap?.get("Making Charges")
                                    .toString() ?: ""),
                                "Metal" to (priceBreakingMap?.get("Metal").toString() ?: ""),
                                "Taxes" to (priceBreakingMap?.get("Taxes").toString() ?: ""),
                                "Total" to (itemDoc.getString("productPrice") ?: "")
                            ),
                            productSpecification = mapOf(
                                "Brand" to "Mahavir",
                                "collection" to (priceBreakingMap?.get("collection").toString() ?: ""),
                                "design-type" to (priceBreakingMap?.get("design-type").toString()
                                    ?: ""),
                                "diamond-clarity" to "22k",
                                "diamond-settings" to "121k",
                                "country-of-origin" to "india",
                                "diamond-carat" to (specificationMap?.get("diamond-carat").toString()
                                    ?: ""),
                                "diamond-weight" to (specificationMap?.get("diamond-weight")
                                    .toString() ?: ""),
                                "gender" to (specificationMap?.get("gender").toString() ?: ""),
                                "item-type" to (specificationMap?.get("item-type").toString() ?: ""),
                                "jewellery-type" to (specificationMap?.get("jewellery-type")
                                    .toString() ?: ""),
                                "karatage" to (specificationMap?.get("karatage").toString() ?: ""),
                                "material-color" to (specificationMap?.get("material-color")
                                    .toString() ?: ""),
                                "metal" to (specificationMap?.get("metal").toString() ?: "")
                            ),
                            size = mapOf("size" to "5mm"),
                            stock = itemDoc.getString("stockQuantity") ?: "",
                            styling = mapOf(
                                "style" to (stylingMap?.get("design-type").toString() ?: "Fancy")
                            )
                        )

                        // Create an item for the adapter
                        val item1 = product.images["0"]?.let {
                            Log.d("fetchAllProducts", "Product images: ${product.images}")
                            item(
                                image = it,
                                name = product.name,
                                price = product.price,
                                style = product.styling["style"] ?: "",
                                product = product
                            )
                        }

                        // Add the item to the list and notify the adapter
                        if (item1 != null) {
                            itemList.add(item1)
                        }

                        withContext(Dispatchers.Main) {
                            adapter.notifyDataSetChanged()
                            progressBarProducts.visibility = View.GONE
                        }
                        Log.d("fetchAllProducts", "Fetched item: ${product.name}")
                    } else {
                        Log.w("fetchAllProducts", "Document does not exist: ${itemDoc.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e("fetchAllProducts", "Error fetching products", e)
                withContext(Dispatchers.Main) {
                    progressBarProducts.visibility = View.GONE
                    Toast.makeText(context, "Error loading products", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(clickedItem: item) {
        // Access the Product directly from clickedItem
        val selectedProduct = clickedItem.product

        if (selectedProduct != null) {
            // Create the intent for ProductDescription activity
            val intent = Intent(requireContext(), ProductDescription::class.java)
            intent.putExtra("product", selectedProduct) // Pass the Product object
            Log.d("ProductDescription", "Product passed: ${selectedProduct.name}")
            startActivity(intent)
        } else {
            Log.d("ProductDescription", "Selected product not found")
        }
    }
    private fun navigateToCategoryActivity(category: String) {
        val intent = Intent(context, Categories::class.java)
        intent.putExtra("selected_category", category)
        startActivity(intent)
    }


}
