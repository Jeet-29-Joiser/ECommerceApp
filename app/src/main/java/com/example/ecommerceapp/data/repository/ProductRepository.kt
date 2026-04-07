package com.example.ecommerceapp.data.repository

import android.util.Log
import com.example.ecommerceapp.data.api.ProductApi
import com.example.ecommerceapp.data.db.ProductDao
import com.example.ecommerceapp.data.mapper.toDomain
import com.example.ecommerceapp.data.mapper.toEntity
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.ui.home.Category
import com.example.ecommerceapp.ui.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao
) {

    suspend fun getHomeProducts(): UiState<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProducts(limit = 30, skip = 0)
            val apiProducts = response.products

            if (apiProducts.isEmpty()) {
                val cachedProducts = dao.getAllProducts().map { it.toDomain() }
                return@withContext if (cachedProducts.isNotEmpty()) {
                    UiState.Success(cachedProducts, isFromCache = true)
                } else {
                    UiState.Empty
                }
            }

            val entities = apiProducts.map { it.toEntity() }

            dao.clearProducts()
            dao.insertProducts(entities)

            UiState.Success(entities.map { it.toDomain() })

        } catch (e: SocketTimeoutException) {
            Log.e("ProductRepository", "Timeout: ${e.message}", e)
            getCachedHomeProducts("Request timed out. Showing offline data if available.")

        } catch (e: IOException) {
            Log.e("ProductRepository", "No internet / IO: ${e.message}", e)
            getCachedHomeProducts("No internet connection. Showing offline data if available.")

        } catch (e: HttpException) {
            Log.e("ProductRepository", "HTTP ${e.code()}: ${e.message()}", e)
            val message = when (e.code()) {
                401 -> "Session expired. Please log in again."
                403 -> "You do not have permission to access this content."
                404 -> "Requested data not found."
                429 -> "Too many requests. Please try again later."
                in 500..599 -> "Server is unavailable right now."
                else -> "Unable to load products from server."
            }
            getCachedHomeProducts(message)

        } catch (e: Exception) {
            Log.e("ProductRepository", "Unexpected: ${e.message}", e)
            getCachedHomeProducts("Something went wrong. Showing offline data if available.")
        }
    }

    private suspend fun getCachedHomeProducts(fallbackMessage: String): UiState<List<Product>> {
        val cachedProducts = dao.getAllProducts().map { it.toDomain() }

        return when {
            cachedProducts.isNotEmpty() -> UiState.Success(cachedProducts, isFromCache = true)
            else -> UiState.Error(fallbackMessage)
        }
    }

    suspend fun getProductsByCategory(
        category: String,
        skip: Int = 0,
        limit: Int = 20
    ): UiState<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProductsByCategory(category, limit, skip)
            val apiProducts = response.products

            if (apiProducts.isEmpty()) {
                val cached = dao.getProductsByCategory(category).map { it.toDomain() }
                return@withContext if (cached.isNotEmpty()) {
                    UiState.Success(cached, isFromCache = true)
                } else {
                    UiState.Empty
                }
            }

            val entities = apiProducts.map { it.toEntity() }
            dao.insertProducts(entities)

            UiState.Success(entities.map { it.toDomain() })

        } catch (e: SocketTimeoutException) {
            Log.e("ProductRepository", "Category timeout: ${e.message}", e)
            getCachedCategoryProducts(category, "Request timed out. Showing offline data if available.")

        } catch (e: IOException) {
            Log.e("ProductRepository", "Category IO: ${e.message}", e)
            getCachedCategoryProducts(category, "No internet connection. Showing offline data if available.")

        } catch (e: HttpException) {
            Log.e("ProductRepository", "Category HTTP ${e.code()}: ${e.message()}", e)
            getCachedCategoryProducts(category, "Unable to load category products.")

        } catch (e: Exception) {
            Log.e("ProductRepository", "Category unexpected: ${e.message}", e)
            getCachedCategoryProducts(category, "Something went wrong.")
        }
    }

    private suspend fun getCachedCategoryProducts(
        category: String,
        fallbackMessage: String
    ): UiState<List<Product>> {
        val cached = dao.getProductsByCategory(category).map { it.toDomain() }

        return when {
            cached.isNotEmpty() -> UiState.Success(cached, isFromCache = true)
            else -> UiState.Error(fallbackMessage)
        }
    }

    suspend fun getProductDetail(productId: Int): UiState<Product> = withContext(Dispatchers.IO) {
        try {
            val product = api.getProductById(productId).toDomain()
            UiState.Success(product)

        } catch (e: SocketTimeoutException) {
            Log.e("ProductRepository", "Detail timeout: ${e.message}", e)
            UiState.Error("Request timed out.")

        } catch (e: IOException) {
            Log.e("ProductRepository", "Detail IO: ${e.message}", e)
            UiState.Error("No internet connection.")

        } catch (e: HttpException) {
            Log.e("ProductRepository", "Detail HTTP ${e.code()}: ${e.message()}", e)
            UiState.Error("Unable to load product details.")

        } catch (e: Exception) {
            Log.e("ProductRepository", "Detail unexpected: ${e.message}", e)
            UiState.Error("Something went wrong.")
        }
    }

    suspend fun getHomeCategoriesFromProducts(): List<Category> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProducts(limit = 0, skip = 0)
            val products = response.products

            val grouped = products.groupBy { it.category }

            val preferredOrder = listOf(
                "mens-shirts",
                "mens-shoes",
                "womens-dresses",
                "womens-shoes",
                "tops",
                "beauty",
                "fragrances",
                "furniture",
                "groceries",
                "smartphones",
                "laptops",
                "tablets",
                "mobile-accessories",
                "sports-accessories",
                "sunglasses",
                "mens-watches",
                "womens-watches",
                "womens-bags"
            )

            val categories = grouped.mapNotNull { (slug, productList) ->
                val firstProduct = productList.firstOrNull() ?: return@mapNotNull null

                Category(
                    title = formatCategoryTitle(slug),
                    apiSlug = slug,
                    imageUrl = firstProduct.thumbnail
                )
            }

            categories.sortedWith(
                compareBy<Category> {
                    preferredOrder.indexOf(it.apiSlug).let { index ->
                        if (index == -1) Int.MAX_VALUE else index
                    }
                }.thenBy { it.title }
            )

        } catch (e: Exception) {
            Log.e("ProductRepository", "Category list error: ${e.message}", e)
            emptyList()
        }
    }

    private fun formatCategoryTitle(slug: String): String {
        return when (slug.lowercase()) {
            "mens-shirts" -> "Men's Fashion"
            "mens-shoes" -> "Men's Footwear"
            "mens-watches" -> "Men's Watches"
            "womens-dresses" -> "Women's Fashion"
            "womens-shoes" -> "Women's Footwear"
            "womens-watches" -> "Women's Watches"
            "womens-bags" -> "Women's Bags"
            "tops" -> "Tops"
            "beauty" -> "Beauty"
            "fragrances" -> "Fragrances"
            "furniture" -> "Furniture"
            "groceries" -> "Groceries"
            "smartphones" -> "Smartphones"
            "laptops" -> "Laptops"
            "tablets" -> "Tablets"
            "mobile-accessories" -> "Accessories"
            "sports-accessories" -> "Sports"
            "sunglasses" -> "Sunglasses"
            else -> slug.split("-").joinToString(" ") {
                it.replaceFirstChar { ch -> ch.uppercase() }
            }
        }
    }
}