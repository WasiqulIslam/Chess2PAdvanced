//12:45 AM 1/24/2005 u 4:29 PM 1/24/2005 bs 12:10 PM 1/25/2005 bs 12:14 PM 1/25/2005 bs 12:38 PM 1/25/2005
//u 2:01 PM 1/25/2005 u 11:59 PM 1/25/2005 u 10:52 AM 1/26/2005

import java.io.*;
import java.util.*;

public class MoveSequence implements Serializable
{
   private long serial;
   private Vector v;
   private int currentLength;
   public MoveSequence( long s )
   {
      serial = s;
      v = new Vector();
      currentLength = v.size();
   }
   public int getLengthU()
   {
      return v.size();
   }
   public long getSerialU()
   {
      return serial;
   }
   public void addInstanceU( ChessInstance c )
   {
      //System.out.println( "size: " + v.size() + " currentLength " + currentLength );
      if( currentLength == v.size() )
      {
         v.addElement( (Object) new ChessInstance( c ) );
         //System.out.println( "MoveSequence Element added size: " + v.size() + " currentLength " + currentLength );
         currentLength = v.size();
      }
      else
      {
         for( int i = v.size() - 1; i >= currentLength; i-- )
         {
            v.removeElementAt( i );
         }
         v.addElement( (Object) new ChessInstance( c ) );
         currentLength = v.size();
      }
   }
   public void prevU()
   {
      if( currentLength >=2 )
         currentLength--;
   }
   public void nextU()
   {
      if( currentLength < v.size()  )
         currentLength++;
   }
   public void firstU()
   {
      if( v.size() != 0 )
         currentLength = 1;
   }
   public void lastU()
   {
      currentLength = v.size();
   }
   public ChessInstance getCurrentInstanceU()
   {
      if( v.size() == 0 )
      {
         //System.out.println( "move sequence gcu returning null" );
         return null;
      }
      else
      {
         return ( new ChessInstance( ( ChessInstance )( v.elementAt( currentLength - 1 ) ) ) );
      }
   }
   public boolean isLastU()
   {
      if( currentLength == v.size() )
         return true;
      else
         return false;
   }
}

