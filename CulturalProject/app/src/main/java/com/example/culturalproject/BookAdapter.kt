package com.example.culturalproject

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.firebase.database.FirebaseDatabase

class BookAdapter(context: Context, val layoutResId: Int, val booksList: List<Book>)
    :ArrayAdapter<Book>(context, layoutResId, booksList)
{
    val TAG = "BOOK_ADAPTER"
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context);
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textTitle = view.findViewById<TextView>(R.id.textName);
        val textAuthor = view.findViewById<TextView>(R.id.textAuthor)
        val textRead = view.findViewById<TextView>(R.id.textRead)
        val cardView = view.findViewById<CardView>(R.id.onebookCard)


        var book = booksList[position]
        textTitle.text=book.title;
        textAuthor.text=book.author;
        textRead.text=book.read;

        cardView.setOnClickListener {
            ShowUpdateDialog(book)
        }


        return view;

    }

    fun ShowUpdateDialog(book:Book)
    {
        val builder = AlertDialog.Builder(context);
        builder.setTitle("Zaktualizuj książkę")
        val inflater=LayoutInflater.from(context);
        val view =inflater.inflate(R.layout.update_book, null)
        val updNameText = view.findViewById<EditText>(R.id.updName);
        val updAuthorText = view.findViewById<EditText>(R.id.updAutor);
        val updReadText = view.findViewById<EditText>(R.id.updRead);

        updNameText.setText(book.title)
        updAuthorText.setText(book.author)
        updReadText.setText(book.read)

        builder.setView(view);
        builder.setPositiveButton("Aktualizu"){
            p0, p1 ->
            val database = FirebaseDatabase.getInstance().getReference("books")

            val name = updNameText.text.toString().trim()
            if(name.isEmpty()){
                updNameText.error="Podaj tytuł!"
                updNameText.requestFocus()
                return@setPositiveButton
            }

            val author = updAuthorText.text.toString().trim()
            if(author.isEmpty()){
                updAuthorText.error="Podaj autora!"
                updAuthorText.requestFocus()
                return@setPositiveButton
            }

            val read = updReadText.text.toString().trim()
            if(read.isEmpty()){
                updReadText.error="Przeczytane czy nie?"
                updReadText.requestFocus()
                return@setPositiveButton
            }

            val book = Book(book.id, name, author,  read.uppercase())
            database.child(book.id).setValue(book)
            Toast.makeText(context, "Zaktulizowano książkę", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Usuń ksiązkę"){
                p0, p1 ->
            val database = FirebaseDatabase.getInstance().getReference("books")
            database.child(book.id).removeValue()
            Toast.makeText(context, "Usunięto książkę", Toast.LENGTH_SHORT).show()
        }

        val alert =builder.create()
        alert.show()
    }


}