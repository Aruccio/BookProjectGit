package com.example.culturalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturalproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding :com.example.culturalproject.databinding.ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var database :DatabaseReference
    lateinit var bookslist : MutableList<Book>
    lateinit var listView:ListView

    private companion object{
        private const val TAG = "MAINWINDOWTAG"

    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("books")
        bookslist= mutableListOf()
        listView = findViewById(R.id.booksList)

        bookslist.clear()
        database.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                bookslist.clear()
                if(snapshot!!.exists())
                {
                    for(b in snapshot.children){
                        val book = b.getValue(Book::class.java)
                        bookslist.add(book!!)
                    }
                    val adapter =BookAdapter(this@MainActivity, R.layout.onebook, bookslist)
                    listView.adapter=adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //init fb auth
        firebaseAuth=FirebaseAuth.getInstance()
        checkUser()

        //logout
        binding.bLogout.setOnClickListener {
            firebaseAuth.signOut()
            Log.d(TAG, "onCreate: klikniete.")
            checkUser()
        }
        binding.bGoToAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }


    }


    private fun checkUser() {
        val firebaseUser=firebaseAuth.currentUser
        if(firebaseUser==null)
        {
            //user not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            //user logged in
            //get user info
            val email = firebaseUser.email
            binding.tEmail.text=email
        }
    }


}