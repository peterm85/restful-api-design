package org.example.restful.port.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(
    name = "stock",
    uniqueConstraints = {
      @UniqueConstraint(
          name = StockEntity.UNIQUE_ISIN_CONSTRAINT,
          columnNames = {"isin"})
    })
public class StockEntity {

  public static final String UNIQUE_ISIN_CONSTRAINT = "UNIQUEISINCONSTRAINT";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "isin", nullable = false)
  private String isin;

  private String corporationName;

  private String market;

  private String currency;
}
