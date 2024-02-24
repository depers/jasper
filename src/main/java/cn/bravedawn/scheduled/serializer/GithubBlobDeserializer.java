package cn.bravedawn.scheduled.serializer;

import cn.bravedawn.scheduled.dto.GiteeContent;
import cn.bravedawn.scheduled.dto.GithubBlob;
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
 * @date : Created in 2024/2/24 17:07
 */
public class GithubBlobDeserializer extends StdDeserializer<List<GithubBlob>> {


    public GithubBlobDeserializer() {
        this(null);
    }


    public GithubBlobDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public List<GithubBlob> deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        List<GithubBlob> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootArray = codec.readTree(parser);
        try {
            for (JsonNode node : rootArray) {
                String type = node.path("type").asText();
                String path = node.path("path").asText();
                String name = node.path("name").asText();
                String url = node.path("git_url").asText();
                String sha = node.path("sha").asText();
                list.add(new GithubBlob(name, path, url, type, sha));
            }
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return list;
    }
}
