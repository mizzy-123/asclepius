package com.dicoding.asclepius.model

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.api.RetrofitClient
import com.dicoding.asclepius.api.response.NewsCancer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsCancerViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _newsCancer = MutableLiveData<NewsCancer>()
    val newsCancer: LiveData<NewsCancer> = _newsCancer

    fun newsCancer(con: Context){
        _isLoading.value = true

        RetrofitClient.instance.getNewsCancer().enqueue(object : Callback<NewsCancer> {
            override fun onResponse(call: Call<NewsCancer>, response: Response<NewsCancer>) {
                if (response.isSuccessful){
                    val responseNewsCancer = response.body()
                    _newsCancer.value = responseNewsCancer!!
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<NewsCancer>, t: Throwable) {
                Toast.makeText(con, "Opps something wrong", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }

        })
    }
}