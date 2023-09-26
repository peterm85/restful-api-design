package org.example.restful.utils;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

public class RestfulAPIResponse {

  @SuppressWarnings("rawtypes")
  public static Builder status(HttpStatus status) {
    return new Builder<>(status);
  }

  public static class Builder<T extends RepresentationModel<T>> {
    private HttpStatus status;
    private CacheControl cacheControl;
    private Instant lastModified;
    private URI location;
    private Function<T, Map<RequestMethod, WebMvcLinkBuilder>> hateoasMap;
    private List<RequestMethod> includedLinks;

    public Builder(HttpStatus status) {
      this.status = status;
    }

    public Builder<T> cacheControl(CacheControl cacheControl) {
      this.cacheControl = cacheControl;
      return this;
    }

    public Builder<T> lastModified(Instant lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    public Builder<T> location(URI location) {
      this.location = location;
      return this;
    }

    public Builder<T> hateoas(
        Function<T, Map<RequestMethod, WebMvcLinkBuilder>> hateoasMap,
        List<RequestMethod> includedLinks) {
      this.hateoasMap = hateoasMap;
      this.includedLinks = includedLinks;
      return this;
    }

    public ResponseEntity<T> body(T body) {

      if (hateoasMap != null) {
        applyHATEOAS(body, hateoasMap, includedLinks);
      }

      BodyBuilder builder = ResponseEntity.status(status);
      if (cacheControl != null) builder = builder.cacheControl(cacheControl);
      if (lastModified != null) builder = builder.lastModified(lastModified);
      if (location != null) builder = builder.location(location);
      return builder.body(body);
    }

    public ResponseEntity<List<T>> body(List<T> body) {

      if (hateoasMap != null) {
        applyHATEOAS(body, hateoasMap, includedLinks);
      }

      BodyBuilder builder = ResponseEntity.status(status);
      if (cacheControl != null) builder = builder.cacheControl(cacheControl);
      if (lastModified != null) builder = builder.lastModified(lastModified);
      if (location != null) builder = builder.location(location);
      return builder.body(body);
    }

    public ResponseEntity<List<T>> build() {
      BodyBuilder builder = ResponseEntity.status(status);
      if (cacheControl != null) builder = builder.cacheControl(cacheControl);
      if (lastModified != null) builder = builder.lastModified(lastModified);
      if (location != null) builder = builder.location(location);
      return builder.build();
    }

    private void applyHATEOAS(
        T response,
        Function<T, Map<RequestMethod, WebMvcLinkBuilder>> hateoasMap,
        List<RequestMethod> includedLinks) {

      includedLinks.stream()
          .forEach(
              method ->
                  response.add(hateoasMap.apply(response).get(method).withRel(method.name())));
    }

    private void applyHATEOAS(
        List<T> responses,
        Function<T, Map<RequestMethod, WebMvcLinkBuilder>> hateoasMap,
        List<RequestMethod> includedLinks) {

      responses.stream().forEach(r -> applyHATEOAS(r, hateoasMap, includedLinks));
    }
  }
}
