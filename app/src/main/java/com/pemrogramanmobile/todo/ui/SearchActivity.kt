package com.pemrogramanmobile.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pemrogramanmobile.todo.R
import com.pemrogramanmobile.todo.adapter.AdapterCard
import com.pemrogramanmobile.todo.model.Todo
import com.pemrogramanmobile.todo.network.ApiConfig
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AdapterCard? = null
    private lateinit var rvParentSearch: RecyclerView
    private lateinit var swPlayout: SwipeRefreshLayout
    private lateinit var btnSearch: FloatingActionButton
    private var keywords: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        keywords =  intent.getStringExtra("keywords").toString()

        val edtDialog: TextView = findViewById(R.id.tVKeywords)
        edtDialog.text = keywords

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        swPlayout = findViewById(R.id.swpRef)
        swPlayout.setOnRefreshListener(this)

        getSearchTodos(keywords)
    }

    fun displayView(){
        rvParentSearch = findViewById(R.id.rv_parent_search)
        btnSearch = findViewById(R.id.btn_fload_search_activity)

        val layoutManger = LinearLayoutManager(this@SearchActivity)
        layoutManger.orientation = LinearLayoutManager.HORIZONTAL

        rvParentSearch.adapter = adapter
        rvParentSearch.layoutManager = layoutManger
    }

    private fun getSearchTodos(keywords: String?) {
        ApiConfig.instanceRetrofit.getSearchTodo(keywords)
            .enqueue(object: Callback<Todo> {
                override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                if (response.isSuccessful){
                    Log.d("Data getSearch", response.body().toString())

                    adapter = AdapterCard(this@SearchActivity, response.body()?.data.orEmpty())
                    displayView()
                    searchTodo()

                    swPlayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<Todo>, t: Throwable) {
                Log.d("Gagal GetSearch data Todo", t.toString() )
            }

        })
    }
    fun searchTodo() {
        btnSearch.setOnClickListener {
            val mDialog =
                LayoutInflater.from(this@SearchActivity).inflate(R.layout.dialog_searchtodo, null)

            val edtDialog = mDialog.findViewById<EditText>(R.id.edt_search_todo)
            val btnCari = mDialog.findViewById<Button>(R.id.btn_searchtodo)

            val mBuild = AlertDialog.Builder(this@SearchActivity)
                .setView(mDialog)
                .setTitle("Search Todo")

            val mAlertDialog = mBuild.show()

            btnCari.setOnClickListener {
                keywords = edtDialog.text.toString()
                getSearchTodos(keywords)
            }
            mAlertDialog.dismiss()
        }
    }

    override fun onRefresh() {
        getSearchTodos(keywords)
    }
}