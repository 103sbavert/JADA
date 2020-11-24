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

    enum class State {
        CallFailed,
        NoMatch
    }

    //store whether the last shown content on the screen was an error message or results from the
    //server to prevent the livedata observer from updating the UI when it should not
    enum class ShownBeforeConfigurationChange {
        Result,
        Error
    }

    var shownBeforeConfigurationChange: ShownBeforeConfigurationChange? = null

    //make an instance of the retrofit api each time the viewmodel is instantiated (somewhat
    //unnecessary right now but will be useful later)
    private val accessApiObject = RetrofitInitialization("hi").accessApiObject

    //tells the reason behind a result not being found (network fail or no match?)
    val failType = MutableLiveData<State>()

    //store the response output by enqueue call, empty if the viewmodel has just been instantiated
    val outputResponse = MutableLiveData<Response<List<Word>>>()

    //make a call to the server
    fun fetchWordInformation(query: String) {
        //retrieve a new call object to make a request to the server
        getRetrofitCall(query).enqueue(object : Callback<List<Word>> {
            override fun onResponse(call: Call<List<Word>>, response: Response<List<Word>>) {
                if (!response.isSuccessful) {
                    Log.e("dictionary_api_access", "failed with error: ${response.code()}")
                    shownBeforeConfigurationChange = ShownBeforeConfigurationChange.Error
                    //set failType to NoMatch since the search didn't hit a match. showError() will
                    // show the relevant error message.
                    failType.value = State.NoMatch
                    return
                }
                shownBeforeConfigurationChange = ShownBeforeConfigurationChange.Result
                //update outputResponse with a new value
                outputResponse.value = response
            }

            override fun onFailure(call: Call<List<Word>>, t: Throwable) {
                Log.e("dictionary_api_access", "connection the server could not be established")
                shownBeforeConfigurationChange = ShownBeforeConfigurationChange.Error
                //set failType to CallFailed since the server couldn't be reached. showError() will
                // show the relevant error message.
                failType.value = State.CallFailed
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

        //If no view currently has focus, create a new one, just so we can grab a window token from
        // it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
