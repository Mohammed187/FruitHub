package com.example.fruithub.repository

import com.example.fruithub.model.Basket
import com.example.fruithub.model.Item
import com.example.fruithub.model.Order
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.collections.HashMap

class FirestoreRepository {

    private val user = Firebase.auth.currentUser
    private val firestoreDB = Firebase.firestore

    private val storageReference = Firebase.storage.reference

    /***
     * User Methods
     */

    fun getAuth(): FirebaseAuth {
        return Firebase.auth
    }

    fun logout() {
        return Firebase.auth.signOut()
    }

    fun loginWithCredentials(email: String, password: String): Task<AuthResult> {
        return getAuth().signInWithEmailAndPassword(email, password)
    }

    fun addUserRegisterDetails(name: String, email: String): Task<Void>? {

        val profileChangeRequest = userProfileChangeRequest {
            displayName = name
        }.also {
            val userData: HashMap<String, Any?> = hashMapOf(
                "name" to name,
                "phone" to "",
                "image" to "https://firebasestorage.googleapis.com/v0/b/fruit-hub-83d58.appspot.com/o/uploads%2Fuser.png?alt=media&token=5bcf6f7e-809d-4a6e-9ff5-c83c42425aea",
                "mail" to email
            )

            firestoreDB.collection("users").document(user?.uid!!).set(userData)
        }

        return user?.updateProfile(profileChangeRequest)
    }

    fun updateUserDetails(
        profileChangeRequest: UserProfileChangeRequest,
        phone: String
    ): Task<Void>? {
        return user?.updateProfile(profileChangeRequest).also {
            val userData: HashMap<String, Any?> = hashMapOf(
                "name" to profileChangeRequest.displayName,
                "phone" to phone,
                "mail" to user?.email
            )
            firestoreDB.collection("users").document(user?.uid!!).update(userData)
        }
    }

    fun updateUserMail(mail: String): Task<Void>? {
        return user?.updateEmail(mail)
    }

    fun getData(): DocumentReference {
        return firestoreDB.collection("users").document(this.user?.uid!!)
    }

    fun uploadImage(): StorageReference {
        return storageReference.child("uploads/" + user?.uid.toString())
    }

    fun addUploadRecordToDb(uri: String): Task<Void> {
        val data: HashMap<String, Any> = hashMapOf(
            "image" to uri
        )

        return firestoreDB.collection("users").document(user?.uid!!).update(data)
    }


    /***
     * Menu Item Methods
     */
    fun getItems(): CollectionReference {
        return firestoreDB.collection("items")
    }

    fun getItemsByCategory(category: String): Query {
        return firestoreDB.collection("items")
            .whereEqualTo("category", category)
    }

    fun getItemById(id: String): DocumentReference {
        return firestoreDB.collection("items").document(id)
    }

    fun deleteItem(item: Item): Task<Void> {
        val documentReference = firestoreDB.collection("items").document(item.id.toString())
        return documentReference.delete()
    }

    /***
     * Basket methods
     */
    fun addItemFromMainToBasket(item: Item): Task<Void> {
        val basketRef = firestoreDB.collection("users")
            .document(user?.uid!!)
            .collection("basket").document(item.id.toString())

        val basket = Basket(
            item.id,
            item,
            1,
            item.price
        )
        return basketRef.set(basket)
    }

    fun addItemToBasket(item: Basket): Task<Void> {
        val basketReference = firestoreDB.collection("users")
            .document(user?.uid!!)
            .collection("basket").document(item.id.toString())
        return basketReference.set(item)
    }

    fun getBasketItems(): CollectionReference {
        return firestoreDB.collection("users")
            .document(user?.uid!!)
            .collection("basket")
    }

    fun getBasketTotal(): CollectionReference {
        return firestoreDB.collection("users")
            .document(user?.uid!!)
            .collection("basket")
    }

    fun deleteBasket(): CollectionReference {
        return firestoreDB.collection("users")
            .document(user?.uid!!).collection("basket")
    }

    fun deleteBasketItem(id: String): DocumentReference {
        return firestoreDB.collection("users").document(user?.uid!!).collection("basket")
            .document(id)
    }

    /***
     * Order Methods
     */
    fun placeNewOrder(order: Order): Task<Void> {
        val orderRef = firestoreDB.collection("users").document(user?.uid!!)
            .collection("orders").document(order.id.toString())
        return orderRef.set(order)
    }

    fun getUserOrders(): CollectionReference {
        return firestoreDB.collection("users")
            .document(user?.uid!!)
            .collection("orders")
    }

}