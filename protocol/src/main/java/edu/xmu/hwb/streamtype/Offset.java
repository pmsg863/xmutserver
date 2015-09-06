package edu.xmu.hwb.streamtype;

public class Offset
{
  private int position = 0;

    public int getPosition()
  {
    return this.position;
  }

  public void setPosition(int cursor)
  {
    this.position = cursor;
  }

  public void forword(int step)
  {
    this.position += step;
  }
}