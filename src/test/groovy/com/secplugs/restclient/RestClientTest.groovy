package com.secplugs.restclient

import groovy.json.JsonSlurper
import okhttp3.Response
import spock.lang.Specification

class RestClientTest extends Specification {
    static final EICAR_SHA256 = "275a021bbfb6489e54d471899f7db9d1663fc695ec2fe2a2c4538aabf651fd0f"
    def "Test that getPresignedUrl returns the url and other properties"() {
        when:
        Map response = RestClient.getPresignedUrl(EICAR_SHA256)

        then:
        response != null
        response.containsKey("upload_post")
    }

    def "Test that uploadFile uploads the file to an S3"() {
        when:
        Map response = RestClient.getPresignedUrl(EICAR_SHA256)
        boolean uploadRes = RestClient.uploadFile("/tmp/eicar.com", EICAR_SHA256, response.get("upload_post") as Map)

        then:
        uploadRes
    }

    def "Test that uploading a file and invoking quickScan triggers the scan and returns the response"() {
        when:
        def response = RestClient.getPresignedUrl(EICAR_SHA256)
        boolean uploadRes = RestClient.uploadFile("/tmp/eicar.com", EICAR_SHA256, response.get("upload_post") as Map)
        byte[] scanRes = RestClient.quickScan(EICAR_SHA256)

        then:
        scanRes != null
        Map jsonRes = new JsonSlurper().parse(scanRes)
        jsonRes.containsKey("score")
        jsonRes.containsKey("json_report")
    }
}
