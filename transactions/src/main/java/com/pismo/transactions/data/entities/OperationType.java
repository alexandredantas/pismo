package com.pismo.transactions.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "operationtypes", schema = "transactions")
public class OperationType {

  @Id
  private Integer id;

  @Column(name = "description")
  @NotNull
  private String description;

  @Column(name = "charge_order")
  private int chargeOrder;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getChargeOrder() {
    return chargeOrder;
  }

  public void setChargeOrder(int chargeOrder) {
    this.chargeOrder = chargeOrder;
  }
}
