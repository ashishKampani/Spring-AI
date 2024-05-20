package dev.ashish.springai.controller;


import dev.ashish.springai.model.Product;
import dev.ashish.springai.model.Question;
import dev.ashish.springai.service.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class OpenaiController {
    private final OpenAIService openAIService;

    public OpenaiController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }


    @PostMapping("/ask")
    public Product askQuestion(@RequestBody Question question) {
        return openAIService.getAnswers(question);
    }

}
