package uet.ktmt.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }


//    @Bean
//    CommandLineRunner run(OrderServiceImpl orderService, ProductServiceImpl productService, HistoryClickRepository historyClickRepository) {
//        return args -> {
//            for (int i = 0; i < 100; i ++) {
//                productService.create(Product.builder()
//                        .name("Quần áo mùa thu")
//                        .productProperties()
//                );
//            }
//        };
//    }
}
