package demo.springai.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VectorStoreConfig {
//    @Bean
//    public QdrantClient qdrantClient() {
//        return new QdrantClient(
//                QdrantGrpcClient.newBuilder(
//                                "5514efd4-3e19-4a9f-a151-5620f735cabd.us-east4-0.gcp.cloud.qdrant.io",
//                                6334,
//                                true
//                        )
//                        .withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3MiOiJtIn0.E2-lPOYq9gF70CVlWwHHhyP81bxL4VjU97rPy6zSiVQ")
//                        .build()
//        );
//    }
//
//    @Bean
//    public VectorStore vectorStore(QdrantClient qdrantClient,
//                                   EmbeddingModel embeddingModel) {
//        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
//                .collectionName("sgk_tin_kntt")
//                .initializeSchema(false)
//                .build();
//    }
}
