package com.proofd.server;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proofd.knowledgebase.UniversityCateringPlan;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReceiveController {

    public static final String owner = "MestonIndustries";
    public static final String commodity = "DeliveryItem";
    public static final String apiEndpoint = "http://localhost:3000/api/Delivery";

    @PostMapping(value = "/transaction")
    public ResponseEntity addToBlockchain(@RequestBody String rawPayload) throws IOException {
   //public void addToBlockchain(@RequestBody String rawPayload) throws IOException {
        JsonObject req = new JsonObject();
        JsonObject payload = JsonParser.parseString(rawPayload).getAsJsonObject();
        System.out.println(payload);
        
        UniversityCateringPlan plan = new UniversityCateringPlan (payload);
        Model m = plan.getModel();
        StringWriter sw = new StringWriter();
       m.write (sw,"TTL");
       // m.write (System.out,"TTL");
    
    
        String trace = sw.toString();
        
       
        req.addProperty("complianceReport", trace);
        req.addProperty("status", payload.get("status").getAsString());
        req.addProperty("newOwner", owner);
        req.addProperty("commodity", commodity);

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(System.getProperty("APP_MASTER_USERNAME"),
                        System.getProperty("APP_MASTER_PASSWORD"))
        );

        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();
        HttpPost httpPost = new HttpPost(apiEndpoint);
        HttpEntity stringEntity = new StringEntity(req.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        if(response.getStatusLine().getStatusCode() != HttpStatus.OK.value()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
        return ResponseEntity.ok(responseBody);
      
        
    }

    @PostMapping(value = "/save")
    @ResponseStatus(HttpStatus.OK)
    public void saveData(@RequestBody String payload) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try (FileOutputStream out =
                     new FileOutputStream(String.format("/var/log/proofdit/%s", timestamp).replace(" ", "_"))) {
            out.write(payload.getBytes());
        } catch (IOException ex) {
            System.err.println("ERROR WRITING: " + ex.getMessage());
            throw ex;
        }
    }
}
