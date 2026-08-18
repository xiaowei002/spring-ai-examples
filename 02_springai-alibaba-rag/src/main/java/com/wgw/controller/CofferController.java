package com.wgw.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coffee")
@Slf4j
public class CofferController {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    public CofferController(VectorStore vectorStore, ChatClient.Builder builder) {
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .topK(3)
                .similarityThreshold(0.5)
                .vectorStore(vectorStore)
                .build();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(documentRetriever)
                .build();

        this.vectorStore = vectorStore;

        this.chatClient = builder
                .defaultAdvisors(retrievalAugmentationAdvisor)
                .build();
    }

    @RequestMapping("/import")
    public String importData() {
        try {
            //读取classpath下的QA.csv文件
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            //使用apache Commons csv 解析csv文件
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()             //第一行作为标题
                    .setSkipHeaderRecord(true) // 跳过标题行
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                //获取问题和回答字段
                String question = record.get("问题");
                String answer = record.get("回答");
                // 将问题和回答组合成文档
                String content = "问题：" + question + "\n回答：" + answer;

                //创建Document 对象
                Document document = new Document(content);
                //添加到文档列表
                documents.add(document);
            }
            //关闭解析器
            csvParser.close();
            //将文档存入向量数据库
            vectorStore.add(documents);
            return "成功导入" + documents.size() + " 条数据到向量数据库";
        } catch (IOException e) {
            log.error("导入失败{}", e.getMessage());
            return "导入失败：" + e.getMessage();
        }
    }

    @GetMapping("/ask-rag")
    public String askRag(@RequestParam(name = "query") String query) {
        return chatClient
                .prompt()
                .system("你是三更咖啡馆的服务员，你需要回答用户的问题")
                .user(query)
                .call()
                .content();
    }
}
