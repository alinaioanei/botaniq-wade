package info.uaic.wade.botaniq.Botaniq;

import com.complexible.stardog.ext.spring.SnarlTemplate;
import info.uaic.wade.botaniq.Botaniq.services.SparqlUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BotaniqApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotaniqApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(SparqlUtil sparqlUtil){
		return (args) ->{
			String get = sparqlUtil.getRequestToStardog("select * where {?a ?b ?c}", null);
			String post = sparqlUtil.postRequestToStardog("select * where {?a ?b ?c}", null);
			System.out.println(get);
			System.out.println("---------");
			System.out.println(post);
		};
	}
}
