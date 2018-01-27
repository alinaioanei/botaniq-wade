package info.uaic.wade.botaniq.Botaniq.services;

import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import org.apache.jena.query.*;
import org.openrdf.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Aioanei Alin Ionut on 24.01.2018.
 */
@Service
public class SparqlUtil {

    @Autowired
    private SnarlTemplate snarlTemplate;

    private String findAlldbpediaQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\t\t\t PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "\t\t\t PREFIX dct: <http://purl.org/dc/terms/> \n" +
            "\t\t\t PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
            "\t\t\t PREFIX dbr: <http://dbpedia.org/resource/> \n" +
            "\t\t\t PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "\t\t\t PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "select ?id ?info ?class ?division ?family ?order ?comment ?link ?image ?plant  where  {\n" +
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

    private String findOneDbpedia = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\t\t\t PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "\t\t\t PREFIX dct: <http://purl.org/dc/terms/> \n" +
            "\t\t\t PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
            "\t\t\t PREFIX dbr: <http://dbpedia.org/resource/> \n" +
            "\t\t\t PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "\t\t\t PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "select ?id ?info ?class ?division ?family ?order ?comment ?link ?image   where  {\n" +
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
            "                         filter(lang(?comment)=\"en\") } LIMIT 1";

    private String stardogPrefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\" +\n" +
            "            \"\\t\\t\\t PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dct: <http://purl.org/dc/terms/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbc: <http://dbpedia.org/resource/Category:>\\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbr: <http://dbpedia.org/resource/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX dbo: <http://dbpedia.org/ontology/> \\n\" +\n" +
            "            \"\\t\\t\\t PREFIX foaf: <http://xmlns.com/foaf/0.1/>\\n\" +";

    private String stardogURL = "http://localhost:5820/botaniq/query";

    public void loadDataFromDbpedia(){
        ParameterizedSparqlString ps = new ParameterizedSparqlString(findAlldbpediaQuery);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", ps.asQuery());
        Map<Integer, DbpediaWrapper> map = new HashMap<>();
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            int id = qs.get("id").asLiteral().getInt();
            if (!map.containsKey(id)) {
                String[] linkSplitted = qs.get("link").toString().split("/");
                String name = linkSplitted[linkSplitted.length - 1];
                DbpediaWrapper dw = new DbpediaWrapper(id, qs.get("info").toString(), qs.get("class").toString(),
                        qs.get("division").toString(), qs.get("family").toString(), qs.get("order").toString(), qs.get("comment").toString(),
                        qs.get("link").toString(), qs.get("image").toString(), name, qs.get("plant").toString());
                map.put(id, dw);
            }
        }
        for (Map.Entry<Integer, DbpediaWrapper> entry: map.entrySet()){
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:abstract", entry.getValue().getInfo());
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:wikiPageID", entry.getValue().getLink());
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:family", entry.getValue().getFamily());
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:order", entry.getValue().getOrder());
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:class", entry.getValue().getClasss());
            snarlTemplate.add(entry.getValue().getPlant(), "dbo:thumbnail", entry.getValue().getImage());
            snarlTemplate.add(entry.getValue().getPlant(), "rdfs:comment", entry.getValue().getComment());
        }
        qe.close();

    }

    public void queryDbpedia(){
        ParameterizedSparqlString ps = new ParameterizedSparqlString(findAlldbpediaQuery);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", ps.asQuery());
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            System.out.println("id =" + qs.get("id") + ",abstract =" + qs.get("info"));
        }
        qe.close();
    }

    public DbpediaWrapper findOneFromDbpedia(String plant) {
        DbpediaWrapper dw = null;
        String plantLink = "<http://dbpedia.org/resource/" + plant + ">";
        String query = findOneDbpedia.replace("?plant", plantLink);
        ParameterizedSparqlString ps = new ParameterizedSparqlString(query);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", ps.asQuery());
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            int id = qs.get("id").asLiteral().getInt();
            String comment = qs.get("comment").toString();
            comment = comment.substring(0, comment.length() - 3);
            String[] classSplitted = qs.get("class").toString().split("/");
            String classs = classSplitted[classSplitted.length - 1];
            String[] divisionplitted = qs.get("division").toString().split("/");
            String division = divisionplitted[divisionplitted.length - 1];
            String[] familySplitted = qs.get("family").toString().split("/");
            String family = familySplitted[familySplitted.length - 1];
            String[] orderSplitted = qs.get("order").toString().split("/");
            String order = orderSplitted[orderSplitted.length - 1];
            String[] linkSplitted = qs.get("link").toString().split("/");
            String name = linkSplitted[linkSplitted.length - 1];
            dw = new DbpediaWrapper(id, qs.get("info").toString(), classs,
                    division, family, order, comment,
                    qs.get("link").toString(), qs.get("image").toString(), name, plantLink);
        }
        qe.close();
        return dw;
    }

    public List<DbpediaWrapper> fetchDbpediaData(){
        return getDbpediaDataAsList(wrapDbpediaQuery());
    }

    private Map<Integer, DbpediaWrapper> wrapDbpediaQuery(){
        ParameterizedSparqlString ps = new ParameterizedSparqlString(findAlldbpediaQuery);
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
                String[] divisionplitted = qs.get("division").toString().split("/");
                String division = divisionplitted[divisionplitted.length - 1];
                String[] familySplitted = qs.get("family").toString().split("/");
                String family = familySplitted[familySplitted.length - 1];
                String[] orderSplitted = qs.get("order").toString().split("/");
                String order = orderSplitted[orderSplitted.length - 1];
                String[] linkSplitted = qs.get("link").toString().split("/");
                String name = linkSplitted[linkSplitted.length - 1];
                DbpediaWrapper dw = new DbpediaWrapper(id, qs.get("info").toString(), classs,
                        division, family, order, comment,
                        qs.get("link").toString(), qs.get("image").toString(), name, qs.get("plant").toString());
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



    public String getRequestToStardog(String query, String responseType){
        String response = null;
        if (responseType == null){
            responseType = "application/sparql-results+json";
        }
        try {
            String authentification = "admin:admin";
            String encodedAuth = Base64.getEncoder().encodeToString(authentification.getBytes());
            URL url = new URL(stardogURL + "?query=" + query.replaceAll("\\s+",""));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", responseType);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;

            while ((output = br.readLine()) != null) {
               response += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return response;
    }

    public String postRequestToStardog(String query, String responseType){
        String response = null;
        if (responseType == null){
            responseType = "application/sparql-results+json";
        }
        try {
            String urlReq = stardogURL;
            String authentification = "admin:admin";
            String encodedAuth = Base64.getEncoder().encodeToString(authentification.getBytes());
            String input = "query=" + query;
            URL url = new URL(urlReq);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", responseType);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                response += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return response;
    }

}
