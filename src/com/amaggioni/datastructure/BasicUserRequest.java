
package com.amaggioni.datastructure;

/**
 *
 * @author magang
 */
public abstract class BasicUserRequest
{

    private String userReqType;
    private String userReqNr;
    private String symbol;
    private int requestId;
    private boolean requeststatus;

    public BasicUserRequest()
    {
        this("", "", "", 0, false);
        //System.out.println("BasicUserRequest default start");
    }

    public BasicUserRequest(String userReqTyp, String userReqNr, String symbol, int requestId, boolean requeststatus)
    {
        //System.out.println("BasicUserRequest 2 start");
        this.requestId = requestId;
        this.userReqNr = userReqNr;
        this.symbol = symbol;
        this.userReqType = userReqTyp;
        this.requeststatus = requeststatus;
    }

    /**
     * @return the userReqType
     */
    public String getUserReqType()
    {
        return userReqType;
    }

    /**
     * @param userReqType the userReqType to set
     */
    public void setUserReqType(String userReqType)
    {
        this.userReqType = userReqType;
    }

    /**
     * @return the symbol
     */
    public String getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the requestId
     */
    public int getRequestId()
    {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(int requestId)
    {
        this.requestId = requestId;
    }

    /**
     * @return the requeststatus
     */
    public boolean isRequeststatus()
    {
        return requeststatus;
    }

    /**
     * @param requeststatus the requeststatus to set
     */
    public void setRequeststatus(boolean requeststatus)
    {
        this.requeststatus = requeststatus;
    }

  /**
   * @return the userReqNr
   */
  public String getUserReqNr()
  {
    return userReqNr;
  }

  /**
   * @param userReqNr the userReqNr to set
   */
  public void setUserReqNr(String userReqNr)
  {
    this.userReqNr = userReqNr;
  }
}
