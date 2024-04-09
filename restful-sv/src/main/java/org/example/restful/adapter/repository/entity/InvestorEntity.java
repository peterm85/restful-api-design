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
    name = "investor",
    uniqueConstraints = {
      @UniqueConstraint(
          name = InvestorEntity.UNIQUE_ID_NUMBER_CONSTRAINT,
          columnNames = {"id_number"})
    })
public class InvestorEntity {

  public static final String UNIQUE_ID_NUMBER_CONSTRAINT = "UNIQUEIDNUMBERCONSTRAINT";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "id_number", nullable = false)
  private String idNumber;

  private String name;

  private Integer age;

  private String country;
}
