package com.example.simpleapicalling

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsyncTask().execute()
    }

    private inner class CallAPILoginAsyncTask : AsyncTask<Any, Void, String>() {
        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null

            try {
                /** This URL is to be replaced if needed t access a different JSON object */
                val url = URL("https://run.mocky.io/v2/5e3939193200006700ddf815")

                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true // do we get data
                connection.doOutput = true // do we send data

                val httpResult: Int = connection.responseCode

                /** check if the returned result from the server is OK (200) */
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    /**
                     * while there is line to read append the newly read lines into the created
                     * string variable
                     */
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        /** try to close the input stream */
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                } else {
                    /** If nothing is returned from the server than append the server message to the string variable */
                    result = connection.responseMessage
                }
            } catch (e: SocketTimeoutException) {
                result = "Connection Time Out"
            } catch (e: Exception) {
                result = "ERROR: " + e.message
            } finally {
                /** If there is an established connection by the application than terminate it */
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)
            }

            /**
             * This piece of code is to parse the received JSON object and display it on the Log screen
             * created jsonObject is to access to the JSON that was returned from the URL (result)
             */
            val jsonObject = JSONObject(result)

            val userId = jsonObject.optInt("user_id")
            val name = jsonObject.optString("name")
            val message = jsonObject.optString("message")

            Log.i("User id ", userId.toString())
            Log.i("Name ", name)
            Log.i("Message", message)

            /**
             * To access the inner level of the JSON object that was returned apply the same process to the profileDetailsObject
             * Access to the data list that is embedded in the profile details
             * print all the related information via a for loop
             */
            val profileDetailsObject = jsonObject.optJSONObject("profile_details")
            val profileIsCompleted = profileDetailsObject?.optBoolean("is_profile_completed")

            Log.i("Is Profile Completed", profileIsCompleted.toString())

            val dataListArray = jsonObject.optJSONArray("data_list")
            Log.i("Data List Size", dataListArray?.length().toString())

            for (item in 0 until dataListArray!!.length()) {
                Log.i("Value $item", dataListArray[item].toString())

                val dataItemObject: JSONObject = dataListArray[item] as JSONObject // Store each read data from the JSON object
                val itemId = dataItemObject.optInt("id")
                val value = dataItemObject.optString("value")
                Log.i("Item ID", itemId.toString())
                Log.i("Item Value", value)


            }

            cancelProgressDialog()
        }

        /** This function is to set the dialog for dialog box */
        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)

            customProgressDialog.show()
        }

        /** This function is to dismiss the dialog box */
        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }
    }
}