package mk.ukim.finki.wp.repoagregator.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.not;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Login Page Tests")
    class LoginPageTests {

        @Test
        @DisplayName("Враќање на login страницата без параметри")
        void shouldReturnLoginPageWithoutParameters() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                    .andExpect(content().string(containsString("login")));
        }


        @ParameterizedTest(name = "GET /login?error={0}")
        @ValueSource(strings = { "true", "false" })
        void shouldReturnLoginPageWithErrorMessage(String value) throws Exception {
            boolean expectMessage = Boolean.parseBoolean(value);
            String errorMessage = "Неточно корисничко име или лозинка";

            mockMvc.perform(get("/login?error=" + value))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                    .andExpect(expectMessage
                            ? content().string(containsString(errorMessage))
                            : content().string(not(containsString(errorMessage))));
        }

        @ParameterizedTest(name = "GET /login?logout={0}")
        @ValueSource(strings = { "true", "false" })
        void shouldReturnLoginPageWithLogoutMessage(String value) throws Exception {
            boolean expectMessage = Boolean.parseBoolean(value);
            String logoutMessage = "Успешно се одјавивте";

            mockMvc.perform(get("/login?logout=" + value))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                    .andExpect(expectMessage
                            ? content().string(containsString(logoutMessage))
                            : content().string(not(containsString(logoutMessage))));
        }

        @Test
        @DisplayName("Проверка дали формата е на login страницата")
        void shouldContainFormOnLoginPage() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("<form")));
        }
    }
}
