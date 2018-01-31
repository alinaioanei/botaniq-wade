package info.uaic.wade.botaniq.Botaniq.services;

import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import info.uaic.wade.botaniq.Botaniq.model.Activity;
import info.uaic.wade.botaniq.Botaniq.model.CommentForm;
import info.uaic.wade.botaniq.Botaniq.model.StardogQuery;
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
import java.net.URI;
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
            "                         filter(lang(?comment)=\"en\") } LIMIT 50";

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
    private String botaniqComment = "http://www.wadebotaniq.com/botaniq#comment";
    private String botaniqHasRelationWith = "http://www.wadebotaniq.com/botaniq#hasRelationWith";
    private String botaniqPicture = "http://www.wadebotaniq.com/botaniq#picture";


    public void addComment(CommentForm commentForm) {
        String link = commentForm.getPlant();
        link = link.substring(1, link.length());
        link = link.substring(0, link.length() - 1);
        snarlTemplate.add(link, "http://www.wadebotaniq.com/botaniq#comment", commentForm.getComment());
    }

    public void addRelation(CommentForm commentForm) {
        String link = commentForm.getPlant();
        link = link.substring(1, link.length());
        link = link.substring(0, link.length() - 1);
        String relationWith = commentForm.getComment();
        relationWith = "http://dbpedia.org/resource/" + relationWith;
        snarlTemplate.add(URI.create(link), URI.create("http://www.wadebotaniq.com/botaniq#hasRelationWith"), URI.create(relationWith));
    }

    public void addImage(CommentForm commentForm) {
        String link = commentForm.getPlant();
        link = link.substring(1, link.length());
        link = link.substring(0, link.length() - 1);
        snarlTemplate.add(link, "http://www.wadebotaniq.com/botaniq#picture", commentForm.getComment());
    }

    public void loadDataFromDbpedia(){
        List<String> sections = fetchSectionNames();
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
            int random = (int)Math.random() * sections.size();
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/abstract", entry.getValue().getInfo());
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/wikiPageID", entry.getValue().getLink());
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/family", entry.getValue().getFamily());
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/order", entry.getValue().getOrder());
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/class", entry.getValue().getClasss());
            snarlTemplate.add(entry.getValue().getPlant(), "http://dbpedia.org/ontology/thumbnail", entry.getValue().getImage());
            snarlTemplate.add(entry.getValue().getPlant(), "https://www.w3.org/2000/01/rdf-schema#comment", entry.getValue().getComment());
            snarlTemplate.add(entry.getValue().getPlant(), "https://www.w3.org/2000/01/rdf-schema#label", entry.getValue().getName());
            snarlTemplate.add(entry.getValue().getPlant(), "http://www.wadebotaniq.com/botaniq#partOfSection", sections.get(random & sections.size()));
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
        dw.setUserComments(new ArrayList<>());
        dw.setUserImages(new ArrayList<>());
        dw.setUserRelation(new ArrayList<>());
        plant = "dbr:" + plant;
        String stardogQuery = " select * where { " + plant + " ?y ?c }";

        List<StardogQuery> list = snarlTemplate.query(stardogQuery, new RowMapper<StardogQuery>() {

            @Override
            public StardogQuery mapRow(BindingSet bindingSet) {

                return new StardogQuery(bindingSet.getValue("y").stringValue(), bindingSet.getValue("c").stringValue());
            }
        });
        for (StardogQuery sQ: list){
            if(sQ.getKey().equals(botaniqComment)){
                dw.getUserComments().add(sQ.getValue());
            } else if (sQ.getKey().equals(botaniqPicture)){
                dw.getUserImages().add(sQ.getValue());
            } else if(sQ.getKey().equals(botaniqHasRelationWith)){
                dw.getUserRelation().add(sQ.getValue());
            }
        }
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

    public List<String> fetchSectionNames(){
        List<String> list = new LinkedList<>();
        String query = "select  ?label where {?garden <http://www.wadebotaniq.com/botaniq#hasSections> ?section.\n" +
                "                      ?section rdfs:label ?label\n" +
                "                       }";
        list = snarlTemplate.query(query, new RowMapper<String>() {

            @Override
            public String mapRow(BindingSet bindingSet) {
                return bindingSet.getValue("label").stringValue();
            }
        });
        return list;
    }

    public Activity fetchSoilPreparationSteps(){
        List<CommentForm> list;
        String query = "select  ?label ?comment where {?step <http://www.wadebotaniq.com/botaniq#partOfActivity> <http://www.wadebotaniq.com/botaniq#FloweringSoilPreparetion>.\n" +
                "                \n" +
                "                 ?step rdfs:label ?label.\n" +
                "                 ?step rdfs:comment ?comment\n" +
                "                }";
        list = snarlTemplate.query(query, new RowMapper<CommentForm>() {
            @Override
            public CommentForm mapRow(BindingSet bindingSet) {
                return new CommentForm(bindingSet.getValue("label").stringValue(), bindingSet.getValue("comment").stringValue());
            }
        });
        Activity a = new Activity();
        a.setCommentForms(list);

        List<String> comments = snarlTemplate.query("select ?comment where { <http://www.wadebotaniq.com/botaniq#FloweringSoilPreparetion> <http://www.wadebotaniq.com/botaniq#comment> ?comment}", new RowMapper<String>() {

            @Override
            public String mapRow(BindingSet bindingSet) {

                return bindingSet.getValue("comment").stringValue();
            }
        });
        List<String> relation = snarlTemplate.query("select ?relation where { <http://www.wadebotaniq.com/botaniq#FloweringSoilPreparetion> <http://www.wadebotaniq.com/botaniq#picture> ?picture}", new RowMapper<String>() {

            @Override
            public String mapRow(BindingSet bindingSet) {

                return bindingSet.getValue("picture").stringValue();
            }
        });
        List<String> images = snarlTemplate.query("select ?picture where { <http://www.wadebotaniq.com/botaniq#FloweringSoilPreparetion> <http://www.wadebotaniq.com/botaniq#hasRelationWith> ?relation}", new RowMapper<String>() {

            @Override
            public String mapRow(BindingSet bindingSet) {

                return bindingSet.getValue("relation").stringValue();
            }
        });
        a.setUserRelation(relation);
        a.setUserComments(comments);
        a.setUserImages(images);
        return a;
    }

    public List<String> fetchPlantsName(String plant) {
        List<String> names = new LinkedList<>();
        return names;
    }



    public String getRequestToStardog(String query, String responseType){
        String response = "";
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
        String response = "";
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
