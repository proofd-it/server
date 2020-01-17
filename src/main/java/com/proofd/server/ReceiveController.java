package com.proofd.server;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public String receiveData(@RequestBody Map<String, Object> rawPayload) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        FileOutputStream out = new FileOutputStream(String.format("/var/log/proofdit/%s", timestamp).replace(" ", "_"));
        String payload = rawPayload.toString();
        out.write(payload.getBytes());
        out.close();

        System.out.println(new Gson().toJson(payload));

        URL url = new URL("https://trustlens.abdn.ac.uk/doors/api/Delivery");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        String username = System.getProperty("APP_MASTER_USERNAME");
        String password = System.getProperty("APP_MASTER_PASSWORD");
        con.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username+":"+password).getBytes()));
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        catch(IOException e) {
            System.err.println(e);
            System.err.println(IOUtils.toString(con.getErrorStream(), "utf-8"));
        }

        return "success";
    }
}
