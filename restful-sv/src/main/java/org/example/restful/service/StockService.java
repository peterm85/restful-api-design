package org.example.restful.service;

import java.util.List;

import org.example.restful.adapter.repository.converter.StockEntityToStockConverter;
import org.example.restful.adapter.repository.converter.StockToStockEntityConverter;
import org.example.restful.domain.Stock;
import org.example.restful.exception.JsonPatchFormatException;
import org.example.restful.exception.StockNotFoundException;
import org.example.restful.port.repository.StockRepository;
import org.example.restful.port.repository.entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@Service
public class StockService {

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private StockRepository stockRepository;

  @Autowired private StockEntityToStockConverter entityConverter;
  @Autowired private StockToStockEntityConverter domainConverter;

  public Stock getStockByIsin(final String isin) {

    return stockRepository
        .findByIsin(isin)
        .map(entityConverter::convert)
        .orElseThrow(StockNotFoundException::new);
  }

  public List<Stock> getAllStocks() {

    return entityConverter.convert(stockRepository.findAll());
  }

  public Stock createStock(final Stock stock) {

    try {
      final StockEntity newEntity = stockRepository.save(domainConverter.convert(stock));

      return entityConverter.convert(newEntity);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage().contains(StockEntity.UNIQUE_ISIN_CONSTRAINT)) {
        return getStockByIsin(stock.getIsin());
      } else {
        throw e;
      }
    }
  }

  public Stock modifyStock(final String isin, final JsonPatch patch) {

    final Stock stock = getStockByIsin(isin);
    try {
      final Stock stockPatched = applyPatchToStock(patch, stock);

      final StockEntity updatedStock = stockRepository.save(domainConverter.convert(stockPatched));

      return entityConverter.convert(updatedStock);
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
