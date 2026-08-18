package com.wgw.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final VectorStore vectorStore;


    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 将数据转换为向量
     *
     * @param data
     * @return
     */
    @PostMapping("/importdData")
    public String importData(@RequestParam(name = "data") String data) {
        Document document = Document
                .builder()
                .text(data)
                .build();

        vectorStore.add(List.of(document));
        return "success";
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam(name = "query") String query) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(10)
                .similarityThreshold(0.8)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}
