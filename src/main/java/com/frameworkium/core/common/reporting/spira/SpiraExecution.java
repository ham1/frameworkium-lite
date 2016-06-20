package com.frameworkium.core.common.reporting.spira;

import com.frameworkium.core.common.properties.Property;
import io.restassured.RestAssured;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static io.restassured.RestAssured.given;

public class SpiraExecution {

    private final static Logger logger = LogManager.getLogger();

    private final static String spiraURI =
            Property.SPIRA_URL.getValue() + Config.spiraRestURI;
    private final String username = Config.spiraUsername;
    private final String apiKey = Config.spiraApiKey;

    public void recordTestResult(
            String issue, int resultId, String comment, ITestResult result) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("ExecutionStatusId", 0);
            obj.put("StartDate", "\\/Date(" + result.getStartMillis() + ")\\/");
            obj.put("TestCaseId", issue.replaceAll("[^0-9]", ""));
            obj.put("TestRunTypeId", 0);
            obj.put("TestRunFormatId", 0);
            obj.put("ExecutionStatusId", resultId);
            obj.put("RunnerName", "Frameworkium");
            obj.put("RunnerTestName", result.getMethod().getMethodName());
            obj.put("RunnerStackTrace", comment);

            if (Property.RESULT_VERSION.isSpecified()) {
                obj.put("ReleaseId", getReleaseId(Property.RESULT_VERSION.getValue()));
            }
            //"Name":null,
            //"BuildId":null,
            //"RunnerMessage":null,
        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }

        //JSONObject auto-escapes the backslash, so we need to unescape it!
        String json = obj.toString().replace("\\\\", "\\");

        // TODO: use RestAssured
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(spiraURI + "/test-runs/record?" +
                "username=" + username + "&" +
                "api-key=" + apiKey.replace("{", "%7B").replace("}", "%7D"));
        StringEntity jsonBody;
        try {
            jsonBody = new StringEntity(json);
            request.addHeader("content-type", "application/json");
            request.setEntity(jsonBody);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e);
        }

        try {
            httpClient.execute(request);
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    public String getReleaseId(String releaseName) {
        RestAssured.baseURI = spiraURI;
        String path = String.format(
                "find {it.FullName == '%s'}.ReleaseId", releaseName);

        return given().contentType("application/json")
                .param("username", username)
                .param("api-key", apiKey)
                .when()
                .get("/releases")
                .thenReturn().jsonPath()
                .getString(path);
    }

}
