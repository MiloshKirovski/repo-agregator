package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.service.GitLabService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
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

            String cleanedUrl = repoUrl.replaceFirst("https://", "").replaceFirst("http://", "");
            String[] parts = cleanedUrl.split("/");

            if (parts.length < 3) {
                return "Invalid GitLab repository URL format.";
            }

            String domain = parts[0];
            String namespace = parts[1];
            String project = parts[2];

            if (project.endsWith(".git")) {
                project = project.substring(0, project.length() - 4);
            }

            String apiBase = "https://" + domain + "/api/v4";

            String encodedProjectPath = namespace + "%2F" + project;

            String completeUrl = apiBase + "/projects/" + encodedProjectPath + "/repository/files/README.md?ref=HEAD";
            URI uri = URI.create(completeUrl); // throws IllegalArgumentException

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "RepoAggregator-App");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class); // throws Exception

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.containsKey("content")) {
                    String base64Content = (String) body.get("content");

                    base64Content = base64Content.replaceAll("\\s", "");

                    byte[] decodedBytes = Base64.getDecoder().decode(base64Content); // throws IllegalArgumentException
                    String content = new String(decodedBytes, StandardCharsets.UTF_8); // throws Exception

                    return content;
                } else {
                    return "README content not found in API response.";
                }
            } else {
                return "Failed to fetch README: HTTP " + response.getStatusCode();
            }

        } catch (IllegalArgumentException e) {
            return "Error decoding README content from GitLab.";
        } catch (Exception e) {
            e.printStackTrace();
            return "README not found or error fetching README from GitLab.";
        }
    }
}