package com.pemrogramanmobile.todo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AdapterCard? = null
    private lateinit var rvParent: RecyclerView
    private lateinit var swPlayout: SwipeRefreshLayout
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnSearch: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        swPlayout = findViewById(R.id.swpRef)
        swPlayout.setOnRefreshListener(this)
        getAllTodos()
    }

    fun displayView(){
        rvParent = findViewById(R.id.rv_parent)
        btnAdd = findViewById(R.id.btn_fload_add)
        btnSearch = findViewById(R.id.btn_fload_search)

        val layoutManger = LinearLayoutManager(this)
        layoutManger.orientation = LinearLayoutManager.HORIZONTAL

        rvParent.adapter = adapter
        rvParent.layoutManager = layoutManger
    }

    fun getAllTodos() {
        ApiConfig.instanceRetrofit.getAllTodo().enqueue(object : Callback<Todo>{
            override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                if (response.isSuccessful){
                    Log.d("Get All Todos", response.body()?.data.toString())
                    adapter = AdapterCard(this@MainActivity, response.body()?.data.orEmpty())

                    displayView()
                    postTodo()
                    searchTodo()

                    swPlayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<Todo>, t: Throwable) {
                Log.d("TAG", "Gagal Get data Todo")
            }

        })
    }

    fun postTodo(){
        btnAdd.setOnClickListener {
            val mDialog =
                LayoutInflater.from(this).inflate(R.layout.dialog_newtodo, null)

            val edtDialog = mDialog.findViewById<EditText>(R.id.edt_simpan_todo)
            val btnSimpan = mDialog.findViewById<Button>(R.id.btn_simpantodo)

            val mBuild = AlertDialog.Builder(this)
                .setView(mDialog)
                .setTitle("Create New Todo")

            val mAlertDialog = mBuild.show()

            btnSimpan.setOnClickListener {
                val name = edtDialog.text.toString()

                ApiConfig.instanceRetrofit.postTodo(name).enqueue(object : Callback<Todo>{
                    override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                        if (response.isSuccessful){
                            Toast.makeText(this@MainActivity, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                            getAllTodos()
                        } else{
                            Toast.makeText(this@MainActivity, "Data Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Todo>, t: Throwable) {
                        getAllTodos()
                    }

                })
                mAlertDialog.dismiss()
            }

        }
    }

    fun searchTodo() {
        btnSearch.setOnClickListener {
            val mDialog =
                LayoutInflater.from(this).inflate(R.layout.dialog_searchtodo, null)

            val edtDialog = mDialog.findViewById<EditText>(R.id.edt_search_todo)
            val btnCari = mDialog.findViewById<Button>(R.id.btn_searchtodo)

            val mBuild = AlertDialog.Builder(this)
                .setView(mDialog)
                .setTitle("Search Todo")

            val mAlertDialog = mBuild.show()
            btnCari.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, SearchActivity::class.java)
                        .putExtra("keywords", edtDialog.text.toString())
                )
            }
        }

    }

    override fun onRefresh() {
        getAllTodos()
    }
}