import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.url.shortener.Vyson.VysonApplication;
import com.url.shortener.Vyson.controllers.UrlController;
import com.url.shortener.Vyson.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UrlController.class)
@ContextConfiguration(classes = VysonApplication.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Correct annotation here
    private UrlShortenerService urlShortenerService;

    @Test
    public void testShortenUrl() throws Exception {
        // Arrange
        String longUrl = "https://facebook2.com";
        String expectedShortCode = "abc123";

        when(urlShortenerService.GenerateShortCode(longUrl)).thenReturn(expectedShortCode);

        // Act & Assert
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"" + longUrl + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRedirectUrl() throws Exception {
        // Arrange
        String code = "5";
        String expectedRedirectUrl = "https://facebook.com";

        when(urlShortenerService.getLongUrl(code)).thenReturn(expectedRedirectUrl);

        // Act & Assert
        mockMvc.perform(get("/redirect")
                        .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", expectedRedirectUrl));
    }
}
