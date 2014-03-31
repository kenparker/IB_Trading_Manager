package com.amaggioni.datastructure;

/**
 *
 * @author maggioni
 */
public class UserReqNr
{

  private int userReqNr;

  public UserReqNr(int userReqNr)
  {

    this.userReqNr = userReqNr;
  }

  /**
   * @return the userReqNr
   */
  public int getUserReqNr()
  {
    return userReqNr;
  }

  /**
   * @param userReqNr the userReqNr to set
   */
  public void setUserReqNr(int userReqNr)
  {
    this.userReqNr = userReqNr;
  }

  @Override
  public int hashCode()
  {

    return this.userReqNr;

  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (this == obj)
    {
      return true;
    }

    if (this.userReqNr == ((UserReqNr) obj).userReqNr)
    {
      return true;
    }

    return false;
  }
}
