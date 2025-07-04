package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.service.impl.GithubServiceImpl;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GithubServiceImplTest {

    private GithubService githubService;
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
        githubService = new GithubServiceImpl(restTemplate);
    }

    @Test
    @DisplayName("fetchReadmeContent со валиден GitHub URL - треба да ја врати содржината")
    void testFetchReadmeContentSuccess() {
        String repoUrl = "https://github.com/testuser/testrepo";
        String expectedApiUrl = "https://api.github.com/repos/testuser/testrepo/readme";
        String originalContent = "# Test README\nThis is a test README file.";
        String base64Content = Base64.getEncoder().encodeToString(originalContent.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("content", base64Content);

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        String result = githubService.fetchReadmeContent(repoUrl);

        assertNotNull(result);
        assertEquals(originalContent, result);
        verify(restTemplate).exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со .git extension - треба да го отстрани .git")
    void testFetchReadmeContentWithGitExtension() {
        String repoUrl = "https://github.com/testuser/testrepo.git";
        String expectedApiUrl = "https://api.github.com/repos/testuser/testrepo/readme";
        String originalContent = "# Test README";
        String base64Content = Base64.getEncoder().encodeToString(originalContent.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("content", base64Content);

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        String result = githubService.fetchReadmeContent(repoUrl);

        assertNotNull(result);
        assertEquals(originalContent, result);
        verify(restTemplate).exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со null URL - треба да врати error message")
    void testFetchReadmeContentNullUrl() {
        String result = githubService.fetchReadmeContent(null);

        assertEquals("Invalid GitHub repository URL.", result);
        verify(restTemplate, never()).exchange(any(), any(), any(), any(Class.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со non-GitHub URL - треба да врати error message")
    void testFetchReadmeContentNonGithubUrl() {
        String repoUrl = "https://gitlab.com/testuser/testrepo";

        String result = githubService.fetchReadmeContent(repoUrl);

        assertEquals("Invalid GitHub repository URL.", result);
        verify(restTemplate, never()).exchange(any(), any(), any(), any(Class.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со невалиден URL format - треба да врати error message")
    void testFetchReadmeContentInvalidUrlFormat() {
        String repoUrl = "https://github.com/onlyowner";

        String result = githubService.fetchReadmeContent(repoUrl);

        assertEquals("Invalid GitHub repository URL format.", result);
        verify(restTemplate, never()).exchange(any(), any(), any(), any(Class.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со празен response body - треба да врати error message")
    void testFetchReadmeContentEmptyResponseBody() {
        String repoUrl = "https://github.com/testuser/testrepo";
        String expectedApiUrl = "https://api.github.com/repos/testuser/testrepo/readme";

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        String result = githubService.fetchReadmeContent(repoUrl);

        assertEquals("README content not found in API response.", result);
        verify(restTemplate).exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со response body без content - треба да врати error message")
    void testFetchReadmeContentNoContentInResponse() {
        String repoUrl = "https://github.com/testuser/testrepo";
        String expectedApiUrl = "https://api.github.com/repos/testuser/testrepo/readme";

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("name", "README.md");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        String result = githubService.fetchReadmeContent(repoUrl);

        assertEquals("README content not found in API response.", result);
        verify(restTemplate).exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("fetchReadmeContent со невалиден Base64 content - треба да врати error message")
    void testFetchReadmeContentInvalidBase64() {
        String repoUrl = "https://github.com/testuser/testrepo";
        String expectedApiUrl = "https://api.github.com/repos/testuser/testrepo/readme";

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("content", "invalid-base64-content!");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        String result = githubService.fetchReadmeContent(repoUrl);

        assertEquals("Error decoding README content from GitHub.", result);
        verify(restTemplate).exchange(eq(expectedApiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }
}