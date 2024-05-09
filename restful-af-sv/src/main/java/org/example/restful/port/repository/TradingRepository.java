package org.example.restful.port.repository;

import org.example.restful.domain.Operation;

public interface TradingRepository {

  Operation purchase(Operation operation);
}
