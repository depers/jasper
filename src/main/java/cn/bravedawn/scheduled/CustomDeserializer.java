package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.dto.GithubContent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jackson-demo
 * @description:
 * @date : Created in 2023/2/1 17:18
 */
public class CustomDeserializer extends StdDeserializer<List<GithubContent>> {

    /**
     * 自定义反序列化器
     */


    public CustomDeserializer() {
        this(null);
    }


    public CustomDeserializer(final Class<?> vc) {
        super(vc);
    }

    public List<GithubContent> deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {

        List<GithubContent> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootArray = codec.readTree(parser);
        try {
            for (JsonNode node : rootArray) {
                String type = node.path("type").asText();
                String path = node.path("path").asText();
                String name = node.path("name").asText();
                String url = node.path("url").asText();
                list.add(new GithubContent(name, path, type, url));
            }
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return list;
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("json/root_content.json");
        BufferedReader json = new BufferedReader(new FileReader(new File(resource.toURI())));
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new CustomDeserializer());
        mapper.registerModule(module);
        List list = mapper.readValue(json, List.class);
        System.out.println(list);
    }





}
