package com.secplugs.restclient

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.net.URLEncoder

@CompileStatic
class RestClient {
    static final String baseUrl = "https://api.live.secplugs.com"
    static final String apiKey = "r2iKI4q7Lu91Nu5uPl3eW3BPmRo4XK1ZbhLWtOKd"

    static HashMap getPresignedUrl(String sha256, String userApiKey = "", String scanObjectType = "file") {
        def fileUploadUrl = baseUrl + "/security/file/upload?sha256=" + sha256
        if (scanObjectType == "email") {
            fileUploadUrl = baseUrl + "/security/email/upload?email_id=" + sha256
        }
        String myApiKey = apiKey;
        if (userApiKey.length()) {
            myApiKey = userApiKey;
        }
        def rawResponse = new URL(fileUploadUrl).getBytes(requestProperties: ['x-api-key': myApiKey])
        def jsonResponse = new JsonSlurper().parse(rawResponse) as Map
        def uploadPost = jsonResponse.get("upload_post") as Map
        HashMap<Object, Object> map = new HashMap<>()
        uploadPost.each {it ->
            map.put(it.key, it.value)
        }
        return map
    }
    
    static Boolean uploadFile(String filePath, String sha256, Map uploadUrlInfo) {
        def uploadUrl = uploadUrlInfo.get("url")
        Map payloadParts = uploadUrlInfo.get("fields")
        File file = new File(filePath)
        try {
            def reqBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
            payloadParts.each {it
                reqBodyBuilder.addFormDataPart(it.key.toString(), it.value.toString())
            }
            reqBodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/octetstream")))
            RequestBody requestBody = reqBodyBuilder.build()

            Request request = new Request.Builder()
                    .url(uploadUrl.toString())
                    .post(requestBody)
                    .build()
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute()
            return response.code() in [200, 204]

        } catch (Exception ignored) {

        }
    }

    static String quickScan(String sha256, String vendor = "hybrid_analysis", String userApiKey = "", String scanObjectType = "file", String scanContext = "") {
        String scanUrl = baseUrl + "/security/file/quickscan?"
        String encodedScanContext = URLEncoder.encode(scanContext, "UTF-8")
        Map params = ["sha256": sha256, "vendorcfg": vendor]
        if (scanObjectType == "email") {
            scanUrl = baseUrl + "/security/email/quickscan?"
            params = ["email_id": sha256, "vendorcfg": vendor]
        }
        if (encodedScanContext.length() > 0) {
            params.put("scancontext", encodedScanContext)
        }
        String query_string = params.each {it ->
            it.key + "=" + it.value
        }.collect().join("&")
        scanUrl += query_string
        String myApiKey = apiKey;
        if (userApiKey.length()) {
            myApiKey = userApiKey;
        }
        def rawResponse = new URL(scanUrl).getText(requestProperties: ['x-api-key': myApiKey])
        return rawResponse
    }

}
