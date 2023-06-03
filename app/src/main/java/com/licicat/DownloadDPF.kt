package com.licicat

import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.licicat.Licitacio
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection

private val okHttpClient = OkHttpClient()

class DownloadDPF {

    fun download() {
        val url = "https://us-central1-apilicicat.cloudfunctions.net/generatePDF"
        //val body: RequestBody = RequestBody.create(JSON, refreshToken)
        val formBody = FormBody.Builder()
            .add("nom", "Jurassic Park")
            .add("hora", "00:00")
            .add("esdeveniment", "hola Replace 'API_URL' with the actual URL of your API endpoint")
            .build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure to create PDF")
            }

            override fun onResponse(call: Call, response: Response) {
                println("Success: " + response.isSuccessful)
                println("PDF made")
                println("Downloading...")
                val body = response.body()
                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()

                    val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    var fileName = "licitacio.pdf"
                    var destinationFile = File(downloadsFolder, fileName)
                    var counter = 1

                    while (destinationFile.exists()) {
                        fileName = "licitacio($counter).pdf"
                        destinationFile = File(downloadsFolder, fileName)
                        counter++
                    }

                    val destinationPath = downloadsFolder.absolutePath + "/$fileName"

                    val outputStream = FileOutputStream(destinationPath)
                    val bufferedOutputStream = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    inputStream?.use { input ->
                        bufferedOutputStream.use { output ->
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    response.body()?.close()
                    outputStream.close()
                    println("PDF downloaded successfully.")
                }
                else {
                    println("Failure to download PDF")
                }
            }
        })
    }
}

                /*
                val responseBody = response.body()
                val byteArray = responseBody?.bytes()
                byteArray?.let {
                    val file = File(requireContext().filesDir, "testapi.pdf")
                    //file.writeBytes(result.body()!!.bytes())
                    val outputStream = FileOutputStream(file)
                    outputStream.write(it)
                    outputStream.close()
                    println("Downloading PDF...")
                    println("PDF downloaded")
            }


        })


    }
}
*/
/*

        val url = "https://us-central1-apilicicat.cloudfunctions.net/generatePDF"

val requestBody = """
                    {
                        "qr": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAZoAAAGaAQAAAAAefbjOAAADDUlEQVR4nO2cS27bShBFT6UJeEgBbwFeCrUzI0vKDsilaAEGmkMDFO4bdHWTjkeJDEqhigODHx2oGyrU51bRJv74mH78OQMBBRRQQAEFFNAxIfOjw87lssPsBHaeO2CuHzjfZXkB7Q8NkqQMTCfQCGjkahrL8yRJ0mdov+UFtD80NwfA1c+GnGT2+mGA+427LS+gu0FSTmLIHkk0zh5Jvv2bAvo3oJI4TK8fBv2CnUlfFYt/bE8B/Q3US2v2wHDpAJLKJYCk5X7LC2hvaDIzLy6KU0hiuHTYW04CrqXUuNfyAtoPKtniGhgES40am3ufI8eD7ymgWyBKVTmsIaFfSjG6eUqvkm2WKnV88D0FdAvEqjNo7CU3hn7xxMHtwM+KKhEWcWSo+ohcswf6hWIRYyszytNqL2ERh4aaj/DfXGP5s6Cxr55hxIWK8BHHh2ogaE5hyOBRo2pVGklyAwmLODrUcsdlaxvKqXoLoDiP8BHPAVU7IHmnq1UdUqb5jeQpRFjE00C9y9b6ecKbXGeS7C1fzZ/O0Q1/Imi4vIjJOmDta7Rjsg6m11CxnwcyOwH0kr1J0rgxi+TJREk577K8gHZXsaF/N4aLocnSwqBrJ+YOwYeVy2n1Gg++p4BugZqKrSpOfc4xi1KZkzzHjMzy6FCrOVsjI9MiRNKqYrtMFRZxdGjT6fK+Rv5Nkqodjo1k8eB7CugWqM1OJhl0i2DpoM/A/CImA6YzMPzqsGHcd3kB7Q61ThfeyGh5hKtW+tIcDx9xaGg7AVEShxo/vK/RBic8foRFHBxCn49yc2MW+m1mIizi4FDJI6rUkBaG0bUHGy4G9O8dzP8tsE5lP/ieAvoGqLSxMuWdLnvL4PN17Sz6Gs8B1VpjPsHwC2B+kQGI+eQTt0Wu7HP1JQ++p4Bugbqvt67G9CqMfukoZWn/3om5ydgPvqeAvhPyOZnZX/ArrVBI8mbYnZcX0G5Qm5casr/EtTY7i21E7/M5oM10PrRelitU67t9dS43qs/DQxb/mSyggAIKKKCAAvpL6H8z7GIAtV7FHAAAAABJRU5ErkJggg==",
                        "data": "23 / 03 / 2023",
                        "esdeveniment": "hola Replace 'API_URL' with the actual URL of your API endpoint",
                        "foto": "https://media.istockphoto.com/id/1196829950/es/vector/mapa-de-catalu%C3%B1a.jpg?s=612x612&w=0&k=20&c=gpj1h9o-_o6Rm9rFmpo3kgf-_R2SkpLabThu78W2wUs=",
                        "hora": "00:00h",
                        "nom": "PROVA2!!!"
                    }
                """.trimIndent()
        val context = LocalContext.current

        val coroutineScope = rememberCoroutineScope()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val requestBody = """
                    {
                        "qr": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAZoAAAGaAQAAAAAefbjOAAADDUlEQVR4nO2cS27bShBFT6UJeEgBbwFeCrUzI0vKDsilaAEGmkMDFO4bdHWTjkeJDEqhigODHx2oGyrU51bRJv74mH78OQMBBRRQQAEFFNAxIfOjw87lssPsBHaeO2CuHzjfZXkB7Q8NkqQMTCfQCGjkahrL8yRJ0mdov+UFtD80NwfA1c+GnGT2+mGA+427LS+gu0FSTmLIHkk0zh5Jvv2bAvo3oJI4TK8fBv2CnUlfFYt/bE8B/Q3US2v2wHDpAJLKJYCk5X7LC2hvaDIzLy6KU0hiuHTYW04CrqXUuNfyAtoPKtniGhgES40am3ufI8eD7ymgWyBKVTmsIaFfSjG6eUqvkm2WKnV88D0FdAvEqjNo7CU3hn7xxMHtwM+KKhEWcWSo+ohcswf6hWIRYyszytNqL2ERh4aaj/DfXGP5s6Cxr55hxIWK8BHHh2ogaE5hyOBRo2pVGklyAwmLODrUcsdlaxvKqXoLoDiP8BHPAVU7IHmnq1UdUqb5jeQpRFjE00C9y9b6ecKbXGeS7C1fzZ/O0Q1/Imi4vIjJOmDta7Rjsg6m11CxnwcyOwH0kr1J0rgxi+TJREk577K8gHZXsaF/N4aLocnSwqBrJ+YOwYeVy2n1Gg++p4BugZqKrSpOfc4xi1KZkzzHjMzy6FCrOVsjI9MiRNKqYrtMFRZxdGjT6fK+Rv5Nkqodjo1k8eB7CugWqM1OJhl0i2DpoM/A/CImA6YzMPzqsGHcd3kB7Q61ThfeyGh5hKtW+tIcDx9xaGg7AVEShxo/vK/RBic8foRFHBxCn49yc2MW+m1mIizi4FDJI6rUkBaG0bUHGy4G9O8dzP8tsE5lP/ieAvoGqLSxMuWdLnvL4PN17Sz6Gs8B1VpjPsHwC2B+kQGI+eQTt0Wu7HP1JQ++p4Bugbqvt67G9CqMfukoZWn/3om5ydgPvqeAvhPyOZnZX/ArrVBI8mbYnZcX0G5Qm5casr/EtTY7i21E7/M5oM10PrRelitU67t9dS43qs/DQxb/mSyggAIKKKCAAvpL6H8z7GIAtV7FHAAAAABJRU5ErkJggg==",
                        "data": "23 / 03 / 2023",
                        "esdeveniment": "hola Replace 'API_URL' with the actual URL of your API endpoint",
                        "foto": "https://media.istockphoto.com/id/1196829950/es/vector/mapa-de-catalu%C3%B1a.jpg?s=612x612&w=0&k=20&c=gpj1h9o-_o6Rm9rFmpo3kgf-_R2SkpLabThu78W2wUs=",
                        "hora": "00:00h",
                        "nom": "PROVA2!!!"
                    }
                """.trimIndent()

                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody.toRequestBody())
                        .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body
                        val byteArray = responseBody?.bytes()

                        byteArray?.let {
                            val file = File(context.filesDir, "testapi.pdf")
                            val outputStream = FileOutputStream(file)
                            outputStream.write(it)
                            outputStream.close()

                            withContext(Dispatchers.Main) {
                                println("PDF file downloaded successfully.")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Error: ${response.message}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        println("Error: ${e.message}")
                    }
                }
            }
        }
    }
}

    }
}
*/