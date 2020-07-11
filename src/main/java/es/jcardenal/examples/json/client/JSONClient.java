package es.jcardenal.examples.json.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.jcardenal.examples.json.client.dtos.SimpleProductDTO;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.compiler.JsonPathCompiler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JSONClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    public static void main(String[] args) throws IOException {
        String url = args[0];
        long startTime = System.currentTimeMillis();
        System.out.println("Reading from "+url);
        URL sourceURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)sourceURL.openConnection();
        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Server returned response code " + responseCode + ". Download failed.");
            System.exit(0);
        }

        Iterator<Object> iterator = JsonSurferJackson.INSTANCE.iterator(connection.getInputStream(), JsonPathCompiler.compile("$.*"));
        Stream<Object> tokenStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        tokenStream.map(json -> JSONClient.mapToDTO(json, false)).map(JSONClient::mapToXML).forEach(System.out::print);

        connection.disconnect();

        long endTime = System.currentTimeMillis();
        System.out.println("Total time(ms) = "+(endTime-startTime));
    }

    private static SimpleProductDTO mapToDTO( Object token, boolean print) {
        SimpleProductDTO simpleProductDTO = null;
        try {
            simpleProductDTO = objectMapper.readValue(token.toString(), SimpleProductDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (print) {
            assert simpleProductDTO != null;
            System.out.println("Product-> "+simpleProductDTO.toString());
        }
        return simpleProductDTO;
    }

    private static String mapToXML(SimpleProductDTO dto) {
        try {
            return xmlMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "<!-- An Error happened-->";
    }
}
