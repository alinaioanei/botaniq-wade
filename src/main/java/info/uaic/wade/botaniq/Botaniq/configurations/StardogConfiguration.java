package info.uaic.wade.botaniq.Botaniq.configurations;

import com.complexible.stardog.ext.spring.DataSourceFactoryBean;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by Aioanei Alin Ionut on 23.01.2018.
 */
@Configuration
public class StardogConfiguration {

    @Bean
    public DataSourceFactoryBean dataSourceFactoryBean(Environment env) {
        DataSourceFactoryBean dataSourceFactoryBean = new DataSourceFactoryBean();
        dataSourceFactoryBean.setUsername(env.getProperty("stardog.db.username"));
        dataSourceFactoryBean.setPassword(env.getProperty("stardog.db.password"));
        dataSourceFactoryBean.setUrl(env.getProperty("stardog.db.url"));
        dataSourceFactoryBean.setTo(env.getProperty("stardog.db.to"));
        return dataSourceFactoryBean;
    }

    @Bean
    public SnarlTemplate snarlTemplate(DataSourceFactoryBean dataSourceFactoryBean) throws Exception{
        SnarlTemplate snarlTeamplate = new SnarlTemplate();
        snarlTeamplate.setDataSource(dataSourceFactoryBean.getObject());
        return snarlTeamplate;
    }
}
