package com.sbeve.dictionary.activities

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbeve.dictionary.retrofit_files.RetrofitInitialization
import com.sbeve.dictionary.retrofit_files.Word
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    //make an instance of the retrofit api each time the viewmodel is instantiated (somewhat unncessary right now but will be useful later)
    private val accessApiObject = RetrofitInitialization("hi").accessApiObject

    //store the response output by enqueue call, empty if the viewmodel has just been instantiated
    val outputResponse = MutableLiveData<Response<List<Word>>>()

    //make a call to the server
    fun enqueueCall(query: String) {
        //retrieve a new call object to make a request to the server
        getRetrofitCall(query).enqueue(object : Callback<List<Word>> {
            override fun onResponse(call: Call<List<Word>>, response: Response<List<Word>>) {
                if (!response.isSuccessful) {
                    Log.e("dictionary_api_access", "failed with error: ${response.code()}")
                    //set outputResponse to null since the search didn't hit a match. updateUI() will show an error message.
                    outputResponse.value = null
                    return
                }
                //update outputResponse with a new value
                outputResponse.value = response
            }

            override fun onFailure(call: Call<List<Word>>, t: Throwable) {
                Log.e("dictionary_api_access", "connection the server could not be established")
            }
        })
    }

    //returns a new call object to use for a new request to the server
    private fun getRetrofitCall(query: String) = accessApiObject.getDefinitions(query)

    //hides the keyboard
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
