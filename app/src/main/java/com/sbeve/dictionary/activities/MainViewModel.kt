package com.sbeve.dictionary.activities

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbeve.dictionary.retrofit_files.RetrofitInitialization
import com.sbeve.dictionary.retrofit_files.Word
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val accessApiObject = RetrofitInitialization("hi").accessApiObject

    val queriedWord = MutableLiveData<String>()
    var outputResponse = MutableLiveData<Response<List<Word>>>()

    fun enqueueCall(query: String) {
        getRetrofitCall(query).enqueue(object : Callback<List<Word>> {
            override fun onResponse(call: Call<List<Word>>, response: Response<List<Word>>) {
                if (!response.isSuccessful) {
                    Log.e("dictionary_api_access", "failed with error: ${response.code()}")
                    return
                }
                outputResponse.value = response
            }

            override fun onFailure(call: Call<List<Word>>, t: Throwable) {
                Log.e("dictionary_api_access", "connection the server could not be established")
            }
        })
    }

    private fun getRetrofitCall(query: String) = accessApiObject.getDefinitions(query)


}
