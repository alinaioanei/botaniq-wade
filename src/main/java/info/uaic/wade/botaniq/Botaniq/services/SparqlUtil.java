package info.uaic.wade.botaniq.Botaniq.services;

import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import org.apache.jena.query.*;
import org.openrdf.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
            "\t\t\t                   ?plant foaf:isPrimaryTopicOf ?link\n" +
            "                         filter(lang(?info)=\"en\")\n" +
            "                         filter(lang(?comment)=\"en\") } LIMIT 2";

    private String stardogPrefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\" +\n" +
            "            \"\\t\\t\\t PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dct: <http://purl.org/dc/terms/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbc: <http://dbpedia.org/resource/Category:>\\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbr: <http://dbpedia.org/resource/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbo: <http://dbpedia.org/ontology/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX foaf: <http://xmlns.com/foaf/0.1/>\\n\" +";

    private String stardogURL = "http://localhost:5820/botaniq/query";

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

    public List<DbpediaWrapper> fetchDbpediaData(){
        return getDbpediaDataAsList(wrapDbpediaQuery());
    }

    private Map<Integer, DbpediaWrapper> wrapDbpediaQuery(){
        ParameterizedSparqlString ps = new ParameterizedSparqlString(dbpediaQuery);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", ps.asQuery());
        Map<Integer, DbpediaWrapper> map = new HashMap<>();
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            int id = qs.get("id").asLiteral().getInt();
            if (!map.containsKey(id)) {
                String comment = qs.get("comment").toString();
                comment = comment.substring(0, comment.length() - 3);
                String[] classSplitted = qs.get("class").toString().split("/");
                String classs = classSplitted[classSplitted.length - 1];
                String[] divisionplitted = qs.get("class").toString().split("/");
                String division = divisionplitted[divisionplitted.length - 1];
                String[] familySplitted = qs.get("class").toString().split("/");
                String family = familySplitted[familySplitted.length - 1];
                String[] orderSplitted = qs.get("class").toString().split("/");
                String order = orderSplitted[orderSplitted.length - 1];
                DbpediaWrapper dw = new DbpediaWrapper(id, qs.get("info").toString(), classs,
                        division, family, order, comment,
                        qs.get("link").toString(), qs.get("image").toString());
                map.put(id, dw);
            }
        }
        qe.close();
        return map;
    }

    private List<DbpediaWrapper> getDbpediaDataAsList(Map<Integer, DbpediaWrapper> map){
        List<DbpediaWrapper> list = new LinkedList<>();
        for(Map.Entry<Integer, DbpediaWrapper> entry : map.entrySet()){
            list.add(entry.getValue());
        }
        return list;
    }

    public void printAll(){
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

    public void stardogQuery(){
        String query = stardogPrefix + "query=select * where { ?a ?b ?c}";
        String url = stardogURL + query;

        String testUrl =  "http://localhost:5820/botaniq/query";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setAccept(Arrays.asList(MediaType.valueOf("application/sparql-results+json")));

        String authentification = "admin:admin";
        String encodedAuth = Base64.getEncoder().encodeToString(authentification.getBytes());
        Map<String, String> vars = new HashMap<>();
        vars.put("query", "select * where { ?a ?b ?c}");
        headers.add("Authorization","Basic " + encodedAuth);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(testUrl, HttpMethod.GET, entity, String.class, vars);
        System.out.println(response.getBody());
    }

}
