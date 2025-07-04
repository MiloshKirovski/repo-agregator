package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.service.impl.GitLabServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitLabServiceImplTest {

    private GitLabServiceImpl gitLabService;
    private RestTemplate restTemplate;

    @TestConfiguration
    static class MockConfig {
        @Bean
        RestTemplate restTemplate() {
            return mock(RestTemplate.class);
        }
    }

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        gitLabService = new GitLabServiceImpl(restTemplate);
    }

    @Test
    @DisplayName("Невалидно URL")
    void testNullRepoUrl() {
        // 1, 2
        String result = gitLabService.fetchReadmeContent(null);
        assertEquals("Invalid GitLab repository URL.", result);
    }

    @Test
    @DisplayName("repoUrl со parts < 3")
    void testInvalidFormatRepoUrl() {
        // 1, 3, 4
        String result = gitLabService.fetchReadmeContent("https://gitlab.com");
        assertEquals("Invalid GitLab repository URL format.", result);
    }

    @Test
    @DisplayName("Валидно URL, HTTP 200, без headers")
    void testContentKeyMissing() {
        // 1, 3, 5, 7, 9, 11, 13, 14
        Map<String, Object> body = new HashMap<>();
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project");
        assertEquals("README content not found in API response.", result);
    }

    @Test
    @DisplayName("Невалидна Base64 содржина")
    void testInvalidBase64Content() {
        // 1, 3, 5, 7, 9, 11, 13, 15, 8
        Map<String, Object> body = new HashMap<>();
        body.put("content", "%%%invalid_base64%%%");
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project");
        assertEquals("Error decoding README content from GitLab.", result);
    }

    @Test
    @DisplayName("Валидно Base64")
    void testValidContent() {
        // 1, 3, 5, 7, 9, 11, 13, 15, 16, 10
        String base64Content = java.util.Base64.getEncoder().encodeToString("Пример README".getBytes());
        Map<String, Object> body = new HashMap<>();
        body.put("content", base64Content);

        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project");
        assertEquals("Пример README", result);
    }

    @Test
    @DisplayName("HTTP != 200")
    void testHttpFailure() {
        // 1, 3, 5, 7, 9, 11, 12
        ResponseEntity<Map> response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project");
        assertEquals("Failed to fetch README: HTTP 404 NOT_FOUND", result);
    }

    @Test
    @DisplayName("repoUrl со суфикс .git")
    void testRepoUrlWithGitSuffix() {
        // 1, 3, 5, 6, 7, 9, 11, 13, 15, 16, 10
        String base64Content = java.util.Base64.getEncoder().encodeToString("README од .git репозиториум".getBytes());
        Map<String, Object> body = new HashMap<>();
        body.put("content", base64Content);
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project.git");
        assertEquals("README од .git репозиториум", result);
    }

    @Test
    @DisplayName(".git URL, грешка при декодирање")
    void testGitSuffixDecodeError() {
        // 1, 3, 5, 6, 7, 8
        Map<String, Object> body = new HashMap<>();
        body.put("content", "%%%invalid_base64%%%");
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(response);

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project.git");
        assertEquals("Error decoding README content from GitLab.", result);
    }

    @Test
    @DisplayName("restTemplate.exchange фрла исклучок")
    void testGitSuffixExchangeException() {
        // 1, 3, 5, 6, 7, 9, 10
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Падна конекцијата!"));

        String result = gitLabService.fetchReadmeContent("https://gitlab.com/group/project.git");
        assertEquals("README not found or error fetching README from GitLab.", result);
    }

}
