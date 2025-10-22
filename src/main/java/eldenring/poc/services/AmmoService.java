package eldenring.poc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import eldenring.poc.config.ApiConfig;
import eldenring.poc.dto.AmmoResponse;
import eldenring.poc.models.Ammo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class AmmoService {
    private static final String BASE = ApiConfig.getBaseUrl() + "/ammos";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Ammo> fetchAmmos(int limit, int page) {
        String url = String.format("%s?limit=%d&page=%d", BASE, limit, page);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            AmmoResponse resp = mapper.readValue(body, AmmoResponse.class);
            if (resp != null && resp.getData() != null) {
                return resp.getData();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
