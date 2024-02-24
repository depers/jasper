package cn.bravedawn.scheduled.serializer;

import cn.bravedawn.scheduled.dto.GiteeContent;
import cn.bravedawn.scheduled.dto.GithubContent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jackson-demo
 * @description:
 * @date : Created in 2023/2/1 17:18
 */
public class GiteeCustomFileDeserializer extends StdDeserializer<GiteeContent> {

    /**
     * 自定义反序列化器
     */


    public GiteeCustomFileDeserializer() {
        this(null);
    }


    public GiteeCustomFileDeserializer(final Class<?> vc) {
        super(vc);
    }

    public GiteeContent deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        List<GiteeContent> list = new ArrayList<>();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootNode = codec.readTree(parser);
        try {
            String encoding = rootNode.path("encoding").asText();
            String content = rootNode.path("content").asText();
            String sha = rootNode.path("sha").asText();
            int size = rootNode.path("size").asInt();
            String url = rootNode.path("url").asText();
            return new GiteeContent(sha, content, size, url, encoding);
        } catch (final Exception e) {
            System.err.println("101_parse_exeption: unknown json.");
        }
        return null;
    }






}
