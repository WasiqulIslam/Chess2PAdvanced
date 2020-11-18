//12:47 AM 1/24/2005 u 2:23 AM 1/24/2005 u 11:04 AM 1/24/2005 bs 11:11 AM 1/24/2005 bs 11:28 AM 1/24/2005 bs 11:30 AM 1/24/2005 bs 12:05 PM 1/24/2005 bs 2:59 PM 1/24/2005
//u 11:43 PM 1/24/2005 u 11:44 PM 1/24/2005 bs 1:00 PM 1/25/2005 board color changed 9:57 AM 1/26/2005 shapeOfChessPieceModified 10:19 AM 1/26/2005 u 10:51 AM 1/26/2005
//u 5:53 PM 1/28/2005
//u 8:57 PM 2/12/2006
//9:00 PM 6/1/2008

//Class that draws the board from an ChessInstance object

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class Drawer implements java.io.Serializable, ImageObserver
{
   Chess2P c2p;
   Graphics2D g2d;

   private Image[] allImages = null;

   public Drawer( Chess2P a, BufferedImage b )
   {

      c2p = a;
      g2d = b.createGraphics();
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

      //load required images firstly
      String[] imageNames = { "", "WP", "WR", "WN", "WB", "WQ", "WK", //White pieces
         "BP", "BR", "BN", "BB", "BQ", "BK",  //Black pieces
         "WBoard", "BBoard"  //Boards
         };
      allImages = new Image[15];
      for( int i = 1; i <= 14; i++ )
      {
         allImages[i] = new ImageIcon( "images/" + imageNames[i] + ".png" ).getImage();
      }

   }

   public void drawU( ChessInstance ci )
   {

      //clear screen
      g2d.setColor( C.getBackgroundColor() );
      g2d.fillRect( 0, 0, 800, 600 );
      if( ci == null )
      {
         //System.out.println( "Drawer drawU found null argument - returning" );
         return;
      }

      //declaration
      int x1, y1, x2, y2;

      //draw the board
      for( int x = 0; x <= 7; x++ )
      {
         for( int y = 0; y <= 7; y++ )
         {
            x1 = 0;
            y1 = 360;
            x2 = 440;
            y2 = 360;
            int a = x * 45, b = ( y + 1 ) * 45;
            x1 += a;
            y1 -= b;
            x2 += a;
            y2 -= b;
            if( ( ( ( x + y ) % 2 ) == 0 ) )  //black square
            {
              Image image1 = getBoardImage( false );
              g2d.drawImage( image1, x1 , y1, this );
              g2d.drawImage( image1, x2 , y2, this );
            }
            else  //white square
            {
              Image image1 = getBoardImage( true );
              g2d.drawImage( image1, x1 , y1, this );
              g2d.drawImage( image1, x2 , y2, this );
            }
         }
      }

      //draw the live pieces
      for( int x = 0; x <= 7; x++ )
      {
         for( int y = 0; y <= 7; y++ )
         {
            int t = ci.getPieceU( x, y, true );
            int c = ci.getTeamU( x, y, true );
            if( t < 1 || t > 12 )
               continue;
            if( c == C.WHITE )
            {
               g2d.setColor( Color.white );
            }
            else
            {
               g2d.setColor( Color.black );
            }
            x1 = 0;
            y1 = 360;
            x2 = 440; 
            y2 = 360;
            int a1 = x * 45, b1 = ( y + 1) * 45, a2 = ( 7 - x ) * 45, b2 = ( 7 - y + 1) * 45;
            x1 += a1;
            y1 -= b1;
            x2 += a2;
            y2 -= b2;
            AffineTransform at1 = AffineTransform.getTranslateInstance( (double) x1, (double) y1 );
            AffineTransform at2 = AffineTransform.getTranslateInstance( (double) x2, (double) y2 );
            Image image1 = getPieceImage( t );
            Image image2 = getPieceImage( t );
            g2d.drawImage( image1, at1, this );
            g2d.drawImage( image2, at2, this );
         }
      }

      //draw the dead pieces
      for( int x = 0; x <= 7; x++ )
      {
         for( int y = 0; y <= 3; y++ )
         {
            int t = ci.getPieceU( x, y, false );
            int c = ci.getTeamU( x, y, false );
            if( t < 1 || t > 12 )
               continue;
            if( c == C.WHITE )
            {
               g2d.setColor( Color.white );
            }
            else
            {
               g2d.setColor( Color.black );
            }
            x1 = 0;
            y1 = 600;
            int a = x * 45, b = ( y + 1 ) * 45;
            x1 += a;
            y1 -= b;
            AffineTransform at1 = AffineTransform.getTranslateInstance( (double) x1, (double) y1 );
            Image image1 = getPieceImage( t );
            g2d.drawImage( image1, at1, this );
         }
      }

      //draw a hint line
      g2d.setStroke( new BasicStroke( 4.0f ) );
      g2d.setColor( new Color( 157, 79, 183 ) );
      x1 = 0 + 20;
      y1 = 360 - 20;
      x2 = 800 - 20;
      y2 = 0 + 20;
      if( ci.fromX() != -1 && ci.fromY() != -1  && ci.toX() != -1  && ci.toY() != -1   )
      {
         g2d.drawLine( x1 + ( ci.fromX() * 45 ), y1 - ( ci.fromY() * 45 ), x1 + ( ci.toX() * 45 ) + 10 , y1 - ( ci.toY() * 45 )  );
         g2d.drawLine( x2 - ( ci.fromX() * 45 ), y2 + ( ci.fromY() * 45 ), x2 - ( ci.toX() * 45 ) -10 , y2 + ( ci.toY() * 45 )  );
      }

      //c2p.refreshU();

   }

   private Image getPieceImage( int piece )
   {

      if( piece < 1 || piece > 12 )
      {
         return null;
      }

      return allImages[ piece ];

   }

   private Image getBoardImage( boolean isWhite )
   {


      if( isWhite )
      {
         return allImages[ C.WHITE ];
      }
      else
      {
         return allImages[ C.BLACK ];
      }


   }

   //as this class is an image observer
   public boolean imageUpdate(Image img,
                    int infoflags,
                    int x,
                    int y,
                    int width,
                    int height)
   {
      //do nothing
      return true;
   }

   public void d( String msg )   //for debugging
   {
     System.out.println( msg );
   }
}