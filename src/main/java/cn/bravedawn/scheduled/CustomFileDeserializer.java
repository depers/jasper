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
public class CustomFileDeserializer extends StdDeserializer<GithubContent> {

    /**
     * 自定义反序列化器
     */


    public CustomFileDeserializer() {
        this(null);
    }


    public CustomFileDeserializer(final Class<?> vc) {
        super(vc);
    }

    public GithubContent deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        List<GithubContent> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootNode = codec.readTree(parser);
        try {
            String type = rootNode.path("type").asText();
            String path = rootNode.path("path").asText();
            String name = rootNode.path("name").asText();
            String content = rootNode.path("content").asText();
            return new GithubContent(name, path, type, content);
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return null;
    }






}
