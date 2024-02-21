package cn.bravedawn.scheduled.serializer;

import cn.bravedawn.scheduled.dto.GithubContent;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 16:11
 */
public class GiteeCustomDeserializer extends StdDeserializer<List<GithubContent>> {

    public GiteeCustomDeserializer() {
        this(null);
    }


    public GiteeCustomDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public List<GithubContent> deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        List<GithubContent> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootArray = codec.readTree(parser);
        try {
            for (JsonNode node : rootArray) {
                String type = node.path("type").asText();
                String path = node.path("path").asText();
                String name = node.path("path").asText();
                String url = node.path("url").asText();
                list.add(new GithubContent(name, path, type, url));
            }
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return list;
    }
}
