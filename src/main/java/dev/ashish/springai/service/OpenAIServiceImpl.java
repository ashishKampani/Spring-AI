package dev.ashish.springai.service;

import dev.ashish.springai.model.Product;
import dev.ashish.springai.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final Logger logger = LoggerFactory.getLogger(OpenAIServiceImpl.class);
    private final ChatModel chatModel;

    private final SimpleVectorStore vectorStore;

    public OpenAIServiceImpl(ChatModel chatModel, SimpleVectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    @Value("classpath:templates/get-product.st")
    private Resource getProduct;

    @Override
    public Product getAnswers(Question question) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest
                .query(question.question()).withTopK(4));
        List<String> contentList = documents.stream().map(Document::getContent).toList();

        var outputParser = new BeanOutputParser<>(Product.class);
        String format = outputParser.getFormat();

        //Generating templates that are used to structure questions before they are sent to the chat-based AI service
        logger.info("Generating prompt template...");
        PromptTemplate promptTemplate = new PromptTemplate(getProduct);

        //Representation of the formatted question that is suitable for the chat-based AI service to process
        logger.info("Creating prompt...");
        Prompt prompt = promptTemplate.create(Map.of("input", question.question(), "format", format, "documents",
                String.join("\n", contentList)));

        logger.info("Calling chat client with prompt...");
        Generation generation = chatModel.call(prompt).getResult();
        logger.info("Retrieving response content...");
        return outputParser.parse(generation.getOutput().getContent());
    }
}

