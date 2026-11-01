package com.goaltracker.seed;

import com.goaltracker.domain.enums.ReferenceType;

import java.util.List;

/** Static definition of the 3 default tracks/topics/references seeded for a brand-new user. */
final class SeedData {

    private SeedData() {
    }

    record ReferenceSeed(ReferenceType type, String title, String url) {
    }

    record TopicSeed(String title, String description, Integer estimatedMinutes, List<ReferenceSeed> references) {
    }

    record TrackSeed(String name, String description, String colorHex, List<TopicSeed> topics) {
    }

    static List<TrackSeed> defaultTracks() {
        return List.of(javaTrack(), systemDesignTrack(), aiTrack());
    }

    private static TrackSeed javaTrack() {
        return new TrackSeed("Java", "Core Java, the JVM, and the Spring ecosystem", "#F89820", List.of(
                topic("Java Fundamentals & Syntax", "Variables, control flow, arrays, and the type system.",
                        "DOC", "Oracle Java Tutorials - Language Basics", "https://docs.oracle.com/javase/tutorial/java/nutsandbolts/index.html"),
                topic("Object-Oriented Programming in Java", "Classes, inheritance, interfaces, polymorphism.",
                        "DOC", "Oracle Java Tutorials - Classes and Objects", "https://docs.oracle.com/javase/tutorial/java/javaOO/index.html"),
                topic("Collections Framework", "List, Set, Map, Queue implementations and when to use each.",
                        "DOC", "Oracle Java Tutorials - Collections", "https://docs.oracle.com/javase/tutorial/collections/index.html"),
                topic("Generics", "Type-safe collections and generic methods/classes.",
                        "DOC", "Oracle Java Tutorials - Generics", "https://docs.oracle.com/javase/tutorial/java/generics/index.html"),
                topic("Exception Handling", "Checked vs unchecked exceptions, try-with-resources.",
                        "DOC", "Oracle Java Tutorials - Exceptions", "https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html"),
                topic("Streams & Lambda Expressions", "Functional-style operations on collections (Java 8+).",
                        "DOC", "Oracle Java Tutorials - Lambda Expressions", "https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html"),
                topic("Concurrency & Multithreading", "Threads, executors, synchronization, java.util.concurrent.",
                        "DOC", "Oracle Java Tutorials - Concurrency", "https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html"),
                topic("JVM Internals & Memory Management", "Heap/stack, garbage collection, class loading.",
                        "DOC", "Oracle - HotSpot VM Garbage Collection Tuning Guide", "https://docs.oracle.com/en/java/javase/17/gctuning/introduction-garbage-collection-tuning.html"),
                topic("Java I/O & NIO", "Streams, readers/writers, channels and buffers.",
                        "DOC", "Oracle Java Tutorials - Basic I/O", "https://docs.oracle.com/javase/tutorial/essential/io/index.html"),
                topic("Spring Core & Dependency Injection", "Beans, the ApplicationContext, and IoC.",
                        "DOC", "Spring Framework Reference - Core", "https://docs.spring.io/spring-framework/reference/core.html"),
                topic("Spring Boot Essentials", "Auto-configuration, starters, and application properties.",
                        "DOC", "Spring Boot Reference Documentation", "https://docs.spring.io/spring-boot/documentation.html"),
                topic("Unit Testing with JUnit & Mockito", "Writing and mocking tests for Java services.",
                        "DOC", "JUnit 5 User Guide", "https://junit.org/junit5/docs/current/user-guide/")
        ));
    }

    private static TrackSeed systemDesignTrack() {
        return new TrackSeed("System Design", "Scalable, reliable distributed-systems fundamentals", "#2E86AB", List.of(
                topic("Scalability Fundamentals", "Vertical vs horizontal scaling, bottlenecks.",
                        "ARTICLE", "High Scalability - Blog Archives", "http://highscalability.com/all-time-favorites/"),
                topic("Load Balancing", "Algorithms and layer 4 vs layer 7 balancing.",
                        "ARTICLE", "NGINX - Load Balancing Guide", "https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/"),
                topic("Caching Strategies", "Cache-aside, write-through, and CDN caching.",
                        "ARTICLE", "AWS - Caching Overview", "https://aws.amazon.com/caching/"),
                topic("Database Sharding & Partitioning", "Horizontal partitioning strategies and trade-offs.",
                        "BOOK", "Designing Data-Intensive Applications - Ch.6 Partitioning", "https://dataintensive.net/"),
                topic("SQL vs NoSQL Databases", "Data models and when to choose each.",
                        "BOOK", "Designing Data-Intensive Applications - Ch.2-3", "https://dataintensive.net/"),
                topic("CAP Theorem & Consistency Models", "Trade-offs between consistency, availability, partition tolerance.",
                        "BOOK", "Designing Data-Intensive Applications - Ch.9 Consistency", "https://dataintensive.net/"),
                topic("Message Queues & Event-Driven Architecture", "Async processing with Kafka/RabbitMQ.",
                        "DOC", "Apache Kafka Documentation", "https://kafka.apache.org/documentation/"),
                topic("Microservices Architecture", "Service decomposition and inter-service communication.",
                        "ARTICLE", "Martin Fowler - Microservices", "https://martinfowler.com/articles/microservices.html"),
                topic("API Design (REST & GraphQL)", "Resource modeling, versioning, pagination.",
                        "ARTICLE", "RESTful API Design Guide", "https://restfulapi.net/"),
                topic("Rate Limiting & Throttling", "Token bucket, leaky bucket, and sliding window algorithms.",
                        "ARTICLE", "Cloudflare Learning - Rate Limiting", "https://www.cloudflare.com/learning/bots/what-is-rate-limiting/"),
                topic("Content Delivery Networks (CDN)", "Edge caching and global content distribution.",
                        "ARTICLE", "Cloudflare Learning - What is a CDN", "https://www.cloudflare.com/learning/cdn/what-is-a-cdn/"),
                topic("Designing for Fault Tolerance & Resilience", "Circuit breakers, retries, bulkheads.",
                        "BOOK", "Designing Data-Intensive Applications - Ch.8 Trouble with Distributed Systems", "https://dataintensive.net/")
        ));
    }

    private static TrackSeed aiTrack() {
        return new TrackSeed("AI", "Machine learning foundations through modern LLMs", "#6A4C93", List.of(
                topic("Linear Algebra & Calculus Foundations", "Vectors, matrices, derivatives for ML.",
                        "COURSE", "Khan Academy - Linear Algebra", "https://www.khanacademy.org/math/linear-algebra"),
                topic("Python for Data Science", "NumPy and Pandas for data manipulation.",
                        "DOC", "NumPy User Guide", "https://numpy.org/doc/stable/user/"),
                topic("Supervised Learning Fundamentals", "Regression, classification, model evaluation.",
                        "DOC", "scikit-learn Tutorials", "https://scikit-learn.org/stable/tutorial/index.html"),
                topic("Unsupervised Learning & Clustering", "K-means, hierarchical clustering, dimensionality reduction.",
                        "DOC", "scikit-learn - Clustering", "https://scikit-learn.org/stable/modules/clustering.html"),
                topic("Neural Networks Basics", "Perceptrons, backpropagation, activation functions.",
                        "COURSE", "DeepLearning.AI - Deep Learning Specialization", "https://www.deeplearning.ai/courses/deep-learning-specialization/"),
                topic("Deep Learning with PyTorch", "Tensors, autograd, and training loops.",
                        "DOC", "PyTorch Tutorials", "https://pytorch.org/tutorials/"),
                topic("Deep Learning with TensorFlow/Keras", "Building and training models with Keras.",
                        "DOC", "TensorFlow Tutorials", "https://www.tensorflow.org/tutorials"),
                topic("Convolutional Neural Networks (CNNs)", "Image recognition architectures.",
                        "COURSE", "Stanford CS231n - CNNs for Visual Recognition", "https://cs231n.github.io/"),
                topic("Recurrent Networks & Sequence Models", "RNNs, LSTMs, and sequence-to-sequence models.",
                        "COURSE", "DeepLearning.AI - Sequence Models", "https://www.deeplearning.ai/courses/deep-learning-specialization/"),
                topic("Transformers & Attention Mechanism", "The architecture behind modern LLMs.",
                        "ARTICLE", "Attention Is All You Need (arXiv)", "https://arxiv.org/abs/1706.03762"),
                topic("Large Language Models & Prompt Engineering", "Working with and prompting modern LLMs.",
                        "COURSE", "Hugging Face - NLP Course", "https://huggingface.co/learn/nlp-course"),
                topic("ML System Design & MLOps", "Productionizing models: serving, monitoring, retraining.",
                        "BOOK", "Designing Machine Learning Systems (Chip Huyen)", "https://www.oreilly.com/library/view/designing-machine-learning/9781098107956/")
        ));
    }

    private static TopicSeed topic(String title, String description, String refType, String refTitle, String refUrl) {
        return new TopicSeed(title, description, 90, List.of(
                new ReferenceSeed(ReferenceType.valueOf(refType), refTitle, refUrl)));
    }
}
