package dev.ashish.springai.service;


import dev.ashish.springai.model.Product;
import dev.ashish.springai.model.Question;

public interface OpenAIService {
    Product getAnswers(Question question);

}
