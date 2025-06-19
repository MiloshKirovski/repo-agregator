package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.service.GithubService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GithubServiceImpl implements GithubService {
    private final RestTemplate restTemplate;

    public GithubServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchReadmeContent(String repoUrl) {
        try {
            if (repoUrl == null || !repoUrl.contains("github.com")) {
                return "Invalid GitHub repository URL.";
            }

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) {
                return "Invalid GitHub repository URL format.";
            }

            String owner = parts[0];
            String repo = parts[1];

            if (repo.endsWith(".git")) {
                repo = repo.substring(0, repo.length() - 4);
            }

            String apiUrl = String.format("https://api.github.com/repos/%s/%s/readme", owner, repo);

            System.out.println("Fetching README from: " + apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "RepoAggregator-App");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("content")) {
                String base64Content = (String) body.get("content");

                String cleanedBase64 = base64Content.replaceAll("\\s+", "");

                byte[] decodedBytes = Base64.getDecoder().decode(cleanedBase64);
                return new String(decodedBytes, StandardCharsets.UTF_8);
            } else {
                return "README content not found in API response.";
            }

        } catch (IllegalArgumentException e) {
            return "Error decoding README content from GitHub.";
        } catch (Exception e) {
            e.printStackTrace();
            return "README not found or error fetching README.";
        }
    }
}