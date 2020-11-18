//3:40 PM 1/24/2005 u 3:47 PM 1/24/2005 u 3:54 PM 1/24/2005 u 4:30 PM 1/24/2005

import java.io.*;

public class Name implements Serializable
{
   private String white, black;
   private long serial;
   public Name( long s )
   {
      white = "";
      black = "";
      serial = s;
   }
   public void setNameU( String nm, int c )
   {
      if( c == C.WHITE )
      {
         white = nm;
      }
      else
      {
         black = nm;
      }
   }
   public String getNameU( int c )
   {
      if( c == C.WHITE )
      {
         return new String( white );
      }
      else
      {
         return new String( black );
      }
   }
   public long getSerialU()
   {
      return serial;
   }
}