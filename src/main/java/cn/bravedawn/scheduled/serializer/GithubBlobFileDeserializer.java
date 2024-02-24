package cn.bravedawn.scheduled.serializer;

import cn.bravedawn.scheduled.dto.GithubBlob;
import cn.bravedawn.scheduled.dto.GithubBlobContent;
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
public class GithubBlobFileDeserializer extends StdDeserializer<GithubBlobContent> {


    public GithubBlobFileDeserializer() {
        this(null);
    }


    public GithubBlobFileDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public GithubBlobContent deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        List<GithubBlob> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootNode = codec.readTree(parser);
        try {
            String encoding = rootNode.path("encoding").asText();
            String content = rootNode.path("content").asText();
            String sha = rootNode.path("sha").asText();
            int size = rootNode.path("size").asInt();
            return new GithubBlobContent(sha, size, content, encoding);
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return null;
    }
}
