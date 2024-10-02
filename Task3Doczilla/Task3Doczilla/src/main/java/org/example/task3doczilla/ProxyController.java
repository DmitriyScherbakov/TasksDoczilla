package org.example.task3doczilla;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String externalApiUrl = "https://todo.doczilla.pro";

    @GetMapping("/{path}")
    public ResponseEntity<String> proxyGet(@PathVariable String path, @RequestParam Map<String, String> queryParams) {
        String url = externalApiUrl + "/" + path;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, queryParams);
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/{path}")
    public ResponseEntity<String> proxyPost(@PathVariable String path, @RequestBody Map<String, Object> requestBody) {
        String url = externalApiUrl + "/" + path;
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
        return ResponseEntity.ok(response.getBody());
    }
}
