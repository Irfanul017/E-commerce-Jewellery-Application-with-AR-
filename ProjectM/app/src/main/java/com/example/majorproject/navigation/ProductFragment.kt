package com.example.majorproject.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductFragment : Fragment(),ItemAdapter.OnItemClickListener {

lateinit var recyclerView: RecyclerView
   private lateinit var itemList:MutableList<item>
lateinit var adapter:ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product, container, false)
     itemList= mutableListOf()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.most_gifted_recycler)




        recyclerView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

       adapter = ItemAdapter(requireContext(), itemList, this)
        recyclerView?.adapter = adapter


        fetchProductsByItemType("ring")
      view.findViewById<ImageView>(R.id.banner1).setOnClickListener{
          navigateToCategoryActivity("necklace")
      }
      view.findViewById<ImageView>(R.id.banner2).setOnClickListener{
          navigateToCategoryActivity("ring")
      }
        view.findViewById<ImageView>(R.id.ring_explore).setOnClickListener{
            navigateToCategoryActivity("ring")
        }
        view.findViewById<ImageView>(R.id.necklace_explore).setOnClickListener{
            navigateToCategoryActivity("necklace")

        }
        view.findViewById<ImageView>(R.id.bracelet_explore).setOnClickListener{
            navigateToCategoryActivity("bracelet")
        }

        return view
    }
    private fun fetchProductsByItemType(itemType: String) {
      FirebaseFirestore.getInstance().collection("Items")
            .whereEqualTo("productSpecification.item-type", itemType)
            .get()
            .addOnSuccessListener { documents ->
                parseAndDisplayProducts(documents.documents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching items: ${e.message}", Toast.LENGTH_SHORT).show()
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
        adapter.notifyDataSetChanged()
    }


    override fun onItemClick(clickedItem: item) {
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

