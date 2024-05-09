package org.example.restful.adapter.repository.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "operation")
public class OperationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Instant creationDateTime;

  @OneToOne
  @JoinColumn(name = "investor_id", referencedColumnName = "id")
  private InvestorEntity investor;

  @OneToOne
  @JoinColumn(name = "stock_id", referencedColumnName = "id")
  private StockEntity stock;

  private Integer amount;

  private Double limitedPrize;

  private OrderTypeEntity orderTypeEntity;

  private OperationTypeEntity operationTypeEntity;
}
