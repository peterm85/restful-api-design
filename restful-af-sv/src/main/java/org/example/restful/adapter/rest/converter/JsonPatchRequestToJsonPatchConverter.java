package org.example.restful.adapter.rest.converter;

import org.openapitools.model.JsonPatchRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;

import java.io.IOException;

@Component
public class JsonPatchRequestToJsonPatchConverter
    implements Converter<JsonPatchRequest, JsonPatch> {

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public JsonPatch convert(JsonPatchRequest jsonPatch) {
    if (jsonPatch == null) {
      return null;
    } else {
      try {
        JsonNode node = objectMapper.valueToTree(jsonPatch.getOperations());
        return JsonPatch.fromJson(node);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException("Patch request cannot be processed");
      } catch (IOException e) {
        throw new RuntimeException("Patch request cannot be processed");
      }
    }
  }
}
