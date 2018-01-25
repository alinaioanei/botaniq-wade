package info.uaic.wade.botaniq.Botaniq.services;

import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import org.apache.jena.query.*;
import org.openrdf.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Aioanei Alin Ionut on 24.01.2018.
 */
@Service
public class SparqlUtil {

    @Autowired
    private SnarlTemplate snarlTemplate;

    private String dbpediaQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\t\t\t PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "\t\t\t PREFIX dct: <http://purl.org/dc/terms/> \n" +
            "\t\t\t PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
            "\t\t\t PREFIX dbr: <http://dbpedia.org/resource/> \n" +
            "\t\t\t PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "\t\t\t PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "select ?id ?info ?class ?division ?family ?order ?comment ?link ?image  where  {\n" +
            "                         ?plant dbo:wikiPageID ?id. \n" +
            "                         ?plant dbo:kingdom dbr:Plant.\n" +
            "                         ?plant dbo:abstract ?info.\n" +
            "                         ?plant dbo:class ?class.\n" +
            "                         ?plant dbo:division ?division.\n" +
            "                         ?plant dbo:family ?family.\n" +
            "                         ?plant dbo:order ?order.\n" +
            "                         ?plant rdfs:comment ?comment.\n" +
            "                         ?plant dbo:thumbnail ?image.\n" +
            "\t\t\t             ?plant foaf:isPrimaryTopicOf ?link\n" +
            "                         filter(lang(?info)=\"en\")\n" +
            "                         filter(lang(?comment)=\"en\") } LIMIT 2";


    public void queryDbpedia(){
        ParameterizedSparqlString ps = new ParameterizedSparqlString(dbpediaQuery);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", ps.asQuery());
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            System.out.println("id =" + qs.get("id") + ",abstract =" + qs.get("info"));
        }
        qe.close();
    }

    public void print(){
        String query = " select * where { ?x ?y ?c }";

        snarlTemplate.query(query, new RowMapper<Object>() {

            @Override
            public Object mapRow(BindingSet bindingSet) {
                System.out.println(bindingSet.getValue("x").stringValue());
                System.out.println(bindingSet.getValue("y").stringValue());
                System.out.println(bindingSet.getValue("c").stringValue());
                return new Object();
            }
        });
    }

}
