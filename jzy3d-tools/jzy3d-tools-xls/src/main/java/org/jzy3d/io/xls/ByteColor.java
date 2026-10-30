package org.jzy3d.io.xls;

public class ByteColor {
  public static final ByteColor BLACK = new ByteColor((byte) 0x00, (byte) 0x00, (byte) 0x00);
  public static final ByteColor WHITE = new ByteColor((byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
  public static final ByteColor GREEN = new ByteColor((byte) 0x00, (byte) 0xFF, (byte) 0x00);
  public static final ByteColor RED = new ByteColor((byte) 0xFF, (byte) 0x00, (byte) 0x00);
  public static final ByteColor BLUE = new ByteColor((byte) 0x00, (byte) 0x00, (byte) 0xFF);
  public static final ByteColor ORANGE = new ByteColor((byte) 0xFF, (byte) 0xA5, (byte) 0x00);

  public ByteColor(byte r, byte g, byte b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public byte r;
  public byte g;
  public byte b;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + b;
    result = prime * result + g;
    result = prime * result + r;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ByteColor other = (ByteColor) obj;
    if (b != other.b)
      return false;
    if (g != other.g)
      return false;
    if (r != other.r)
      return false;
    return true;
  }
}
