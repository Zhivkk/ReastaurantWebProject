package app.APIMessage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mailClient", url = "http://localhost:8081/api/email")
public interface MailClient {
    @PostMapping
    void sendMail(@RequestBody MailRequest mailRequest);
}
