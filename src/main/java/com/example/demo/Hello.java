package com.example.demo;

import com.example.demo.model.Greeting;
import com.example.demo.model.Quote;
import com.example.demo.model.Todo;
import com.example.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;

//access azure key vault
import com.azure.core.util.polling.SyncPoller;
import com.azure.identity.DefaultAzureCredentialBuilder;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.DeletedSecret;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

@Component
@Slf4j
public class Hello implements Function<Mono<User>, Mono<Greeting>> {

    @Autowired
    RestTemplate restTemplate;

  /*   @Value("${secreto}")
    private String secreto; */

    public Mono<Greeting> apply(Mono<User> mono) {

        Todo hello = restTemplate.getForObject("https://jsonplaceholder.typicode.com/todos/1", Todo.class);

        log.info(hello.toString());

        String secreto="";
       // String keyVaultName = System.getenv("KEY_VAULT_NAME");
        String keyVaultUri = "https://akvlrojastest.vault.azure.net";

        SecretClient secretClient = new SecretClientBuilder()
            .vaultUrl(keyVaultUri)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();

        KeyVaultSecret retrievedSecret = secretClient.getSecret("secreto01");

        return mono.map(user -> new Greeting("Hello, " + user.getName() + "! Secreto: " + retrievedSecret.getValue()));
    }
}
