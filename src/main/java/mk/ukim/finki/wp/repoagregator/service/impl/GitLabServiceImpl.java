package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.service.GitLabService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GitLabServiceImpl implements GitLabService {

    private final RestTemplate restTemplate;

    public GitLabServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String fetchReadmeContent(String repoUrl) {
        try {
            if (repoUrl == null || !repoUrl.contains("gitlab")) {
                return "Invalid GitLab repository URL.";
            }

            // Extract domain and path
            String cleanedUrl = repoUrl.replaceFirst("https://", "").replaceFirst("http://", "");
            String[] parts = cleanedUrl.split("/");

            if (parts.length < 3) {
                return "Invalid GitLab repository URL format.";
            }

            String domain = parts[0];  // e.g., gitlab.com or gitlab.finki.ukim.mk
            String namespace = parts[1];
            String project = parts[2];

            if (project.endsWith(".git")) {
                project = project.substring(0, project.length() - 4);
            }

            // Reconstruct the base GitLab API URL
            String apiBase = "https://" + domain + "/api/v4";

            // Build project path with manual encoding of just the slash
            String encodedProjectPath = namespace + "%2F" + project;

            // Build the complete URL as a string first, then convert to URI
            String completeUrl = apiBase + "/projects/" + encodedProjectPath + "/repository/files/README.md?ref=HEAD";
            URI uri = URI.create(completeUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "RepoAggregator-App");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.containsKey("content")) {
                    String base64Content = (String) body.get("content");

                    // Remove whitespace and newlines from base64 content
                    base64Content = base64Content.replaceAll("\\s", "");

                    byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                    String content = new String(decodedBytes, StandardCharsets.UTF_8);

                    return content;
                } else {
                    return "README content not found in API response.";
                }
            } else {
                return "Failed to fetch README: HTTP " + response.getStatusCode();
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Base64 decoding error: " + e.getMessage());
            return "Error decoding README content from GitLab.";
        } catch (Exception e) {
            System.err.println("Error fetching README from GitLab: " + e.getMessage());
            e.printStackTrace();
            return "README not found or error fetching README from GitLab.";
        }
    }
}