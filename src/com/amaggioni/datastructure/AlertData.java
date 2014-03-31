package com.amaggioni.datastructure;

import com.ib.client.Contract;

/**
 *
 * @author magang
 */
public class AlertData
{

  public String status;
  public int requestId;
  public double checkPrice;
  public Contract contract;
  public boolean alertstatus;

  public AlertData(String status, int requestId,  double checkPrice, Contract contract)
  {
    this.status = status;
    this.requestId = requestId;
    this.checkPrice = checkPrice;
    this.contract = contract;
    this.alertstatus = false;

  }
}
