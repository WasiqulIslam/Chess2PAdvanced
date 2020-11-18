//11:14 PM 1/23/2005 u 12:46 AM 1/24/2005 bs 6:34 PM 1/25/2005
//9:00 PM 6/1/2008
//Class C or Constant

import java.awt.*;

public class C implements java.io.Serializable
{

   public static final int NONE = 0, WP /*WhitePawn*/ = 1, WR = 2, WN = 3, WB = 4, WQ = 5, WK = 6, BP = 7, BR = 8, BN = 9, BB = 10, BQ = 11, BK = 12;
   public static final int WHITE = 13, BLACK = 14;

   public static Color getBackgroundColor()
   {
      return new Color( 132, 3, 19 ); //change background color from here
   }

   public static Color getTextColor()
   {
      return new Color( 255, 203, 19 ); //change text color from here
   }

}