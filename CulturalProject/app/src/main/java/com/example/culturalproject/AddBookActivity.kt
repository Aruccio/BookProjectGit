package com.example.culturalproject

import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.culturalproject.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddBookActivity : AppCompatActivity() {
    lateinit var tTitle : EditText;
    lateinit var tAuthor:EditText;
    lateinit var ratingBar: RatingBar;
    lateinit var bAddBook : Button;
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var binding :com.example.culturalproject.databinding.ActivityAddBookBinding

    private companion object{
        private const val TAG = "ADDBOOKTAG"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init fb auth
        firebaseAuth=FirebaseAuth.getInstance()
        checkUser()

        tTitle = findViewById(R.id.tName);
        tAuthor = findViewById(R.id.tAuthor);
        bAddBook = findViewById<Button>(R.id.bAddBook);


        //init fb auth
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        //logout
        binding.bLogout.setOnClickListener {
            firebaseAuth.signOut()
            Log.d(AddBookActivity.TAG, "onCreate: klikniete.")
            checkUser()
        }

        binding.bAddBook.setOnClickListener {
            AddBook();
        }
    }


    private fun AddBook()
    {
        println("----------------------))))) JESTEM TUTAJ")
        val title = tTitle.text.toString().trim()
        val author = tAuthor.text.toString().trim()
        if(title.isEmpty())
        {
            tTitle.error = ("Please enter a name")
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("books")

        val bookId = ref.push().key!!
        val book = Book(bookId, title, author)

        ref.child(bookId).setValue(book).addOnCompleteListener{
            Toast.makeText(applicationContext, "Added a book", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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