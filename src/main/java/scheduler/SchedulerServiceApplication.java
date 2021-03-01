package scheduler;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SchedulerServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(SchedulerServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
