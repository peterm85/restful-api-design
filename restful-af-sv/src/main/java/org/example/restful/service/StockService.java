package org.example.restful.service;

import org.example.restful.domain.Stock;
import org.example.restful.exception.JsonPatchFormatException;
import org.example.restful.port.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.List;

@Service
public class StockService {

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private StockRepository stockRepository;

  public Stock getStockByIsin(final String isin) {

    return stockRepository.getByIsin(isin);
  }

  public List<Stock> getAllStocks() {

    return stockRepository.getAll();
  }

  public Stock createStock(final Stock stock) {

    return stockRepository.save(stock);
  }

  public Stock updateStock(final String isin, final JsonPatch patch) {

    final Stock stock = getStockByIsin(isin);
    try {
      final Stock stockPatched = applyPatchToStock(patch, stock);

      return stockRepository.save(stockPatched);
    } catch (JsonPatchException | JsonProcessingException e) {
      throw new JsonPatchFormatException();
    }
  }

  private Stock applyPatchToStock(JsonPatch patch, Stock targetStock)
      throws JsonPatchException, JsonProcessingException {

    JsonNode patched = patch.apply(objectMapper.convertValue(targetStock, JsonNode.class));
    return objectMapper.treeToValue(patched, Stock.class);
  }
}
