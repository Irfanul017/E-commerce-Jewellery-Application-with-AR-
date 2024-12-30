package com.example.majorproject.navigation

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.io.Serializable

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

        recyclerView?.layoutManager = GridLayoutManager(context, 2)

        adapter = ItemAdapter(requireContext(), itemList, this)
        recyclerView?.adapter = adapter
         fetchBannerImages(view)

        fetchAllAuspiciousProducts()

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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val auspiciousDocRef = db.collection("Products")
                    .document("Rings")
                    .collection("Women")
                    .document("Auspicious")

                for (i in 1..10) {
                    val subCollectionRef = auspiciousDocRef.collection("item$i")
                    val querySnapshot = subCollectionRef.get().await()

                    for (document in querySnapshot.documents) {
                        if (document.exists()) {
                            val fetchedProduct = document.toObject(Product::class.java)

                            if (fetchedProduct != null) {
                                // Switch to the main thread to update the UI
                                withContext(Dispatchers.Main) {
                                    progressBarProducts.visibility = View.GONE

                                    // Create a Product object from the document
                                    val product = Product(
                                        name = document.getString("name") ?: "",
                                        price = document.getString("price") ?: "",
                                        images = document.get("images") as? Map<String, String> ?: emptyMap(),
                                        grossWeight = document.get("gross-weight") as? Map<String, String> ?: emptyMap(),
                                        priceBreaking = document.get("price-breaking") as? Map<String, String> ?: emptyMap(),
                                        productSpecification = document.get("product-specification") as? Map<String, String> ?: emptyMap(),
                                        size = document.get("size") as? Map<String, String> ?: emptyMap(),
                                        stock = document.getString("stock") ?: "",
                                        styling = document.get("styling") as? Map<String, String> ?: emptyMap()
                                    )

                                    val item1 = item(
                                        image = fetchedProduct.images["0"] ?: "",
                                        name = fetchedProduct.name,
                                        price = fetchedProduct.price,
                                        style = fetchedProduct.styling["style"] ?: "",
                                        product = product // Pass the Product object here
                                    )
                                    itemList.add(item1)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("fetchAllAuspiciousProducts", "Error fetching products", e)
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



}
