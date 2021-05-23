package com.example.fruithub.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.fruithub.model.Basket
import com.example.fruithub.model.Item
import com.example.fruithub.model.Order
import com.example.fruithub.model.User
import com.example.fruithub.repository.FirestoreRepository
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch

class FirestoreViewModel : ViewModel() {

    private var firestoreRepository = FirestoreRepository()

    private var savedItems: MutableLiveData<List<Item>> = MutableLiveData()
    private var catItems: MutableLiveData<List<Item>> = MutableLiveData()

    private var basketItems: MutableLiveData<List<Basket>> = MutableLiveData()
    private var basketTotal: MutableLiveData<Double> = MutableLiveData()

    var item: MutableLiveData<Item> = MutableLiveData()

    var orders: MutableLiveData<List<Order>> = MutableLiveData()

    var user: MutableLiveData<User> = MutableLiveData()

    // Login to home.
    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    // Check if the user login or not
    fun checkIfUserExists(): Boolean {
        val user = firestoreRepository.getAuth().currentUser

        return user != null
    }

    // User Login
    fun loginUser(email: String, password: String): LiveData<Boolean?> {
        viewModelScope.launch {
            firestoreRepository.loginWithCredentials(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        _navigateToHome.value = true
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        _navigateToHome.value = false
                    }
                }
        }
        return navigateToHome
    }

    // Register User
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            firestoreRepository.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "registerUser: Success")

                        val user = firestoreRepository.getAuth().currentUser
                        firestoreRepository.addUserRegisterDetails(name, user?.email!!)
                            ?.addOnSuccessListener {
                                Log.d(TAG, "addUserRegisterDetails: Success")
                            }
                            ?.addOnFailureListener { e ->
                                Log.w(TAG, "addUserRegisterDetails: Failed ", e)
                            }


                    } else {
                        Log.w(TAG, "registerUser: Failed", task.exception)
                    }
                }
        }
    }

    // Update User Details
    fun updateUserDetails(userName: String, phone: String) {
        viewModelScope.launch {
            val profileChange = userProfileChangeRequest {
                displayName = userName
            }

            firestoreRepository.updateUserDetails(profileChange, phone)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "updateUserDetails: Success")
                    } else {
                        Log.w(TAG, "updateUserDetails: Failed", task.exception)
                    }
                }
        }
    }

    // Update user register email
    fun updateUserMail(mail: String) {
        viewModelScope.launch {
            firestoreRepository.updateUserMail(mail)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "updateUserMail: Success")
                } else {
                    Log.w(TAG, "updateUserMail: Failed", task.exception)
                }
            }
        }
    }

    // Get user details
    fun getUserData(): LiveData<User> {
        viewModelScope.launch {
            firestoreRepository.getData().get().addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot == null) {
                    Log.w(TAG, "getUserData: Failed")
                    return@addOnSuccessListener
                }

                val mUser = documentSnapshot.toObject<User>()
                user.value = mUser
            }
        }
        return user
    }

    // Update user image to Firestore
    fun uploadUserImage(uri: Uri?) {
        viewModelScope.launch {
            firestoreRepository.uploadImage().putFile(uri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        firestoreRepository.addUploadRecordToDb(imageUrl)
                            .addOnCompleteListener {
                                Log.d(TAG, "addImageToDB: Success")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "uploadUserImage: Failed", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "uploadUserImage: Failed", e)
                }
        }
    }

    // User Logout

    fun userLogout() {
        viewModelScope.launch {
            firestoreRepository.logout()
        }
    }

    // save item to firebase
    fun addItemToBasketMain(item: Item) {
        viewModelScope.launch {
            firestoreRepository.addItemFromMainToBasket(item).addOnFailureListener {
                Log.e(TAG, " Failed to save item")
            }
        }
    }

    // save item to firebase
    fun addItemToBasketFirebase(item: Basket) {
        viewModelScope.launch {
            firestoreRepository.addItemToBasket(item).addOnFailureListener {
                Log.e(TAG, " Failed to save item")
            }
        }
    }

    // get realtime updates from firebase regarding saved Items
    fun getAllItems(): LiveData<List<Item>> {
        viewModelScope.launch {
            firestoreRepository.getItems()
                .addSnapshotListener(EventListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed.", error)
                        savedItems.value = null
                        return@EventListener
                    }

                    val allItemsList: MutableList<Item> = mutableListOf()
                    for (doc in value!!) {
                        val item = doc.toObject(Item::class.java)
                        allItemsList.add(item)
                    }
                    savedItems.value = allItemsList
                })
        }
        return savedItems
    }

    // get realtime updates from firebase regarding Items filtered by category
    fun getItemsByCat(category: String): LiveData<List<Item>> {
        viewModelScope.launch {
            firestoreRepository.getItemsByCategory(category)
                .addSnapshotListener(EventListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed.", error)
                        savedItems.value = null
                        return@EventListener
                    }

                    val itemsByCat: MutableList<Item> = mutableListOf()

                    for (doc in value!!) {
                        val item = doc.toObject(Item::class.java)
                        itemsByCat.add(item)
                    }
                    catItems.value = itemsByCat
                })
        }
        return catItems
    }

    // Get item details by Id from firebase
    fun getItemDetails(id: String): LiveData<Item> {
        viewModelScope.launch {
            firestoreRepository.getItemById(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val itemDetails = task.result?.toObject(Item::class.java)
                    item.value = itemDetails
                } else {
                    Log.e(TAG, "Failed to get item details")
                }
            }
        }
        return item
    }

    // Navigate to item Details.
    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail

    fun onMenuItemClicked(id: String) {
        _navigateToItemDetail.value = id
    }

    fun onItemDetailNavigated() {
        _navigateToItemDetail.value = null
    }

    // get basket items updates from firebase
    fun getBasket(): LiveData<List<Basket>> {
        viewModelScope.launch {
            firestoreRepository.getBasketItems()
                .addSnapshotListener(EventListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed.", error)
                        basketItems.value = null
                        return@EventListener
                    }

                    val basketList: MutableList<Basket> = mutableListOf()
                    for (doc in value!!) {
                        val item = doc.toObject(Basket::class.java)
                        basketList.add(item)
                    }
                    basketItems.value = basketList
                    Log.d(TAG, "getBasket: ${basketList.size}")
                })
        }
        return basketItems
    }

    // get basket items total price updates from firebase
    fun getBasketTotal(): LiveData<Double> {
        viewModelScope.launch {
            firestoreRepository.getBasketTotal()
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val result = ArrayList<Double>()
                    for (doc in value!!) {
                        doc.getDouble("total")?.let {
                            result.add(it)
                        }
                        basketTotal.value = result.sum()
                    }
                }
        }
        return basketTotal
    }

    // Clear basket
    private fun clearBasket() {
        viewModelScope.launch {
            firestoreRepository.deleteBasket().get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        doc.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "clearBasket: Success")
                            }
                            .addOnFailureListener {
                                Log.w(TAG, "clearBasket: Failed", it)
                            }
                    }
                }
        }
    }

    // Delete Item from basket
    fun deleteItemFromBasket(id: String?) {
        viewModelScope.launch {
            firestoreRepository.deleteBasketItem(id!!).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteItemFromBasket: id : $id Success")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "deleteItemFromBasket: Failed", e)
                }
        }
    }

    // place a new order
    fun placeOrder(order: Order) {
        firestoreRepository.placeNewOrder(order)
            .addOnSuccessListener {
                Log.d(TAG, "placeOrder: Success")
                clearBasket()
            }
            .addOnFailureListener {
                Log.w(TAG, "placeOrder: Failed")
            }
    }

    // Get all user orders from firebase
    fun getOrders(): LiveData<List<Order>> {
        viewModelScope.launch {
            firestoreRepository.getUserOrders()
                .addSnapshotListener(EventListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed.", error)
                        basketItems.value = null
                        return@EventListener
                    }

                    val ordersList: MutableList<Order> = mutableListOf()
                    for (doc in value!!) {
                        val order = doc.toObject(Order::class.java)
                        ordersList.add(order)
                    }
                    orders.value = ordersList
                })
        }
        return orders
    }

    // delete item from firebase
    fun deleteItem(item: Item) {
        firestoreRepository.deleteItem(item).addOnFailureListener {
            Log.e(TAG, "Failed to delete Item")
        }
    }

    companion object {
        const val TAG = "FIRESTORE_VIEW_MODEL"
    }
}

class FirestoreViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirestoreViewModel::class.java)) {
            return FirestoreViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}