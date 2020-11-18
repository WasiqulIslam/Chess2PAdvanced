//11:07 PM 1/23/2005 u 12:46 AM 1/24/2005 u 12:57 AM 1/24/2005 u 2:11 AM 1/24/2005 u 2:15 AM 1/24/2005 u 11:55 AM 1/24/2005 u 4:01 PM 1/24/2005
//u 1:38 PM 1/25/2005 u 1:42 PM 1/25/2005 u 2:07 PM 1/25/2005 methodAdded 8:42 PM 1/25/2005 u 9:34 PM 1/25/2005 u 10:54 AM 1/26/2005
//Note: this chess instance was previously written in a manner which was comsuming mush space
//when game was saved. So it is going to be shortened in the next version. 12:57 PM 1/28/2005
//u 3:43 PM 1/28/2005 bs 4:06 PM 1/28/2005

import java.io.*;
import java.util.*;
import javax.swing.*;

public class ChessInstance implements Serializable
{
   private BitSet bits = new BitSet( 4 * 96 );   //=384 . Data is stored for 8*8 = 64 board positions and 4*8 = 32 positions for killed pieces each position requires 13 values of which 12 for 12 different chess pieces and the other for indication of no piece in that location. 4 bits are used for each positions and total 4 * 96 bits are requited. 4 bit can represent 16 number of which 13 are needed and remaining 3 are wastage of space.
   private int fromX, fromY, toX, toY;
   public ChessInstance()
   {
      resetU();
   }
   public ChessInstance( ChessInstance ci )
   {
      for( int i = 0; i <= 7; i++ )
      {
         for( int j =0; j <= 7; j++ )
         {
            setPieceU( i, j, true, ci.getPieceU( i, j, true ) );
         }
      }
      for( int i = 0; i <= 7; i++ )
      {
         for( int j =0; j <= 3; j++ )
         {
            setPieceU( i, j, false, ci.getPieceU( i, j, false ) );
         }
      }
      fromX = ci.fromX();
      fromY = ci.fromY();
      toX = ci.toX();
      toY = ci.toY();
   }
   public void arrangeForNewGameU()
   {
      resetU();
      setPieceU( 0, 0, true, C.WR );
      setPieceU( 1, 0, true, C.WN );
      setPieceU( 2, 0, true, C.WB );
      setPieceU( 3, 0, true, C.WQ );
      setPieceU( 4, 0, true, C.WK );
      setPieceU( 5, 0, true, C.WB );
      setPieceU( 6, 0, true, C.WN );
      setPieceU( 7, 0, true, C.WR );
      setPieceU( 0, 7, true, C.BR );
      setPieceU( 1, 7, true, C.BN );
      setPieceU( 2, 7, true, C.BB );
      setPieceU( 3, 7, true, C.BQ );
      setPieceU( 4, 7, true, C.BK );
      setPieceU( 5, 7, true, C.BB );
      setPieceU( 6, 7, true, C.BN );
      setPieceU( 7, 7, true, C.BR );
      for( int i = 0; i <= 7; i++ )
      {
         setPieceU( i, 1, true, C.WP );
         setPieceU( i, 6, true, C.BP );
      }
   }
   private void resetU()
   {
      /*BitSet tmp = (BitSet) bits.clone();
      bits.xor( tmp );*/
      for( int i = 0; i < bits.length(); i++ )
         bits.clear( i );
      resetMoveIndicator();
   }
   public void removeU( int x, int y )
   {
      setPieceU( x, y, true, C.NONE );
   }
   public void killU( int x, int y )
   {
      if( getPieceU( x, y, true ) == C.NONE )
         return;
      int t = getPieceU( x, y, true );
      lbl: for( int i = 0; i <= 7; i++ )
      {
         for( int j = 0; j <= 3; j++ )
         {
            if( getPieceU( i, j, false ) == C.NONE )
            {
               setPieceU( i, j, false, t );
               break lbl;
            }
         }
      }
      setPieceU( x, y, true, C.NONE );
   }
   public boolean isEmptyU( int x, int y )
   {
      if( getPieceU( x, y, true ) == C.NONE )
         return true;
      else
         return false;
   }
   public int getTeamU( int x, int y, boolean bk )
   {
      int a;
      if( bk )
      {
         a = getPieceU( x, y, true );
      }
      else
      {
         a = getPieceU( x, y, false );
      }
      //System.out.println( "ChessInstance getTeamU a " + a + "x " + x + "y " + y );
      if( a == C.WP || a == C.WR || a == C.WN || a == C.WB || a == C.WQ || a == C.WK )
      {
         //System.out.println( "Chess Instance getTeamU returning  " + C.WHITE + " for " + x + " " + y + " " + bk );
         return C.WHITE;
      }
      else if( a == C.BP || a == C.BR || a == C.BN || a == C.BB || a == C.BQ || a == C.BK )
      {
         //System.out.println( "Chess Instance getTeamU returning " + C.BLACK + " for " + x + " " + y + " " + bk );
         return C.BLACK;
       }
      else
      {
         //System.out.println( "Chess Instance getTeamU returning " + C.NONE + " for " + x + " " + y + " " + bk );
         return C.NONE;
      }
   }
   public boolean moveU( int x1, int y1, int x2, int y2 )  //if true returned success indicated
   {
      //System.out.println( "Chess Instance moveU x1 y1 x2 y2 : " + x1 + " " + y1 + " " + x2 + " " + y2 );
      if( x1 == x2 && y1 == y2 )
         return false;
      if( ( x1 < 0 || x1 > 7 ) ||( y1 < 0 || y1 > 7 ) ||( x2 < 0 || x2 > 7 ) ||( y2 < 0 || y2 > 7 ) )
         return false;
      if( ( getTeamU( x1, y1, true ) != getTeamU( x2, y2, true ) ) && !( getTeamU( x1, y1, true ) == C.NONE && getTeamU( x2, y2, true ) == C.NONE ) && getTeamU( x1, y1, true ) != C.NONE )
      {
         //System.out.println( "ChessInstance CURRENT SEARCH" );
         killU( x2, y2 );
         int t = getPieceU( x1, y1, true );
         removeU( x1, y1 );
         setPieceU( x2, y2, true, t );
         setMoveIndicator( x1, y1, x2, y2 );
         return true;
      }
      else
      {
         return false;
      }
   }
   public int getPieceU( int x, int y, boolean indic )
   {
      boolean a[] = new boolean[ 4 ];
      int ind = 0;
      ind += ( y * 8 * 4 );
      ind += ( x * 4 );
      if( !indic )
      {
         ind += ( 4 * 64 );
      }
      for( int i = 0; i <= 3; i++ )
      {
         a[ i ] = bits.get( ind + i );
      }
      int rtn = 0;
      for( int i = 0; i <= 3; i++ )
      {
         int t = 1;
         for( int j = i; j <= 2; j++  )
            t *= 2;
         if( a[ i ] == true )
         {
            rtn += t;
         }
      }
      return rtn;
   }
   public void printBoardU()
   {
      for( int i = 0; i <= 7; i++ )
      {
         for( int j = 0; j <= 7; j++ )
         {
            System.out.print( "" + getPieceU( i, j, true ) );
         }
         System.out.println();
      }
      System.out.println();
      System.out.println();
      for( int i = 0; i <= 7; i++ )
      {
         for( int j = 0; j <= 3; j++ )
         {
            System.out.print( "" + getPieceU( i, j, false ) );
         }
         System.out.println();
      }
      System.out.println();
   }
   public void setPieceU( int x, int y, boolean indic, int piece )
   {
      if( x < 0 || x > 7 || y < 0 || y > 7 )
         return;
      boolean a[] = new boolean[ 4 ];
      a[ 3 ] = ( ( ( piece % 2 ) == 0 ) ? false : true );
      piece /= 2;
      a[ 2 ] = ( ( ( piece % 2 ) == 0 ) ? false : true );
      piece /= 2;
      a[ 1 ] = ( ( ( piece % 2 ) == 0 ) ? false : true );
      piece /= 2;
      a[ 0 ] = ( ( ( piece % 2 ) == 0 ) ? false : true );
      int ind = 0;
      ind += ( y * 8 * 4 );
      ind += ( x * 4 );
      if( !indic )
      {
         ind += 4 * 64;
      }
      for( int i = 0; i <= 3; i++ )
      {
         if( a[ i ] == true )
         {
            bits.set( ind + i );
         }
         else
         {
            bits.clear( ind + i );
         }
      }
   }
   public void setMoveIndicator(  int a, int b, int c, int d)
   {
      fromX = a;
      fromY = b;
      toX = c;
      toY = d;
   }
   public void resetMoveIndicator()
   {
      fromX = -1;
      fromY = -1;
      toX = -1;
      toY = -1;
   }
   public void d( String msg )   //for debugging
   {
     System.out.println( msg );
   }
   public int  fromX()
   {
      return fromX;
   }
   public int  fromY()
   {
      return fromY;
   }
   public int  toX()
   {
      return toX;
   }
   public int  toY()
   {
      return toY;
   }
}