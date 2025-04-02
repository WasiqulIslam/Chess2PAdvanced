//2:23 AM 1/24/2005 u 11:02 AM 1/24/2005 u 1:20 PM 1/24/2005 u 1:44 PM 1/24/2005
//u 1:43 PM 1/25/2005 u 1:45 PM 1/25/2005 u 9:02 PM 1/25/2005
//first completion( v 1.0 ) at 12:14 AM 1/26/200
//bs 12:32 AM 1/26/2005 u 1:23 AM 1/26/2005
//v1.1 10:20 AM 1/26/2005 u 10:28 AM 1/26/2005 u 10:51 AM 1/26/2005 u 11:03 AM 1/26/2005
//u 3:43 PM 1/28/2005
//v1.2 gameSaveSpaceMinimized by changing ChessInstance class 5:54 PM 1/28/2005
//u 6:04 PM 1/28/2005
//v1.3 2:23 PM 2/10/2006
//u 3:15 PM 2/10/2006
//v1.4 autoload 10:29 PM 2/11/2006
//u 10:30 PM 2/11/2006
//v2.0 move indicator line added 1:23 AM 2/12/2006
//u 1:24 AM 2/12/2006
//u 8:58 PM 2/12/2006
//u 11:50 PM 2/13/2006
//9:00 PM 6/1/2008


import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;


public class Chess2P extends JWindow implements MouseListener, ActionListener, java.io.Serializable
{
   private BufferedImage bi;
   private ChessInstance currentInstance;
   private Drawer drawer;
   private Rectangle previousBounds;
   private JPopupMenu popup;
   private JMenuItem menus[] = new JMenuItem[ 16 ];
   private boolean isStarted = false, firstTime = true, isSelected = false, step = false;
   private int selectedX, selectedY;
   private MoveSequence moveSequence;
   private Name name;
   private javax.swing.Timer replayer;
   private boolean autosaveMessage = false;

   public Chess2P()
   {
      popup = new JPopupMenu();
      String names[] = { "Exit", "Start new Game", "Undo", "Redo", "Change a piece of chess", "Save Full Game from start", "Save Current Board Instance", "Load Game", "Load Board Instance","Start replay", "Pause and Stop replay", "Next", "Previous", "First", "Last", "About" };
      for( int i = 0; i <= 15; i++ )
      {
         menus[ i ] = new JMenuItem( names[ i ] );
         popup.add( menus[ i ] );
         menus[ i ].addActionListener( this );
         if( i == 6 || i == 0 || i == 14 )
            popup.addSeparator();
      }
      addMouseListener( this );
      bi = new BufferedImage( 800, 600, BufferedImage.TYPE_INT_RGB );

      GraphicsEnvironment ge =  GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gd[] = ge.getScreenDevices();
      if( gd[ 0 ] != null )
      {
         GraphicsConfiguration gc = gd[ 0 ].getDefaultConfiguration();
         if( gc != null )
         {
            previousBounds = gc.getBounds();
            if( !( previousBounds.width == 800 && previousBounds.height == 600 ) )
            {
               JOptionPane.showMessageDialog( this, "Set resolution to 800X600 pixels.", "Recommended", JOptionPane.INFORMATION_MESSAGE );
            }
         }
      }

      /*currentInstance = new ChessInstance();

      currentInstance.arrangeForNewGameU();
      for( int i = 0; i <= 7; i ++ )
      {
         currentInstance.killU( i, 0 );
         currentInstance.killU( i, 1 );
         currentInstance.killU( i, 6 );
         currentInstance.killU( i, 7 );
      }*/

      drawer = new Drawer( this, bi );

      disableAllButtonsU();
      menus[ 0 ].setEnabled( true );
      menus[ 1 ].setEnabled( true );
      menus[ 7 ].setEnabled( true );
      menus[ 8 ].setEnabled( true );
      menus[ 15 ].setEnabled( true );

      setSize( 800, 600 );
      setVisible( true );
      requestFocus();

      load2U();

   }
   public void paint( Graphics g )   //tag
   {

      //print( "painting" );
      //create a background for the buffered image(bi)
      if( firstTime)
      {
         Graphics biGraphics = bi.getGraphics();
         biGraphics.setColor( C.getBackgroundColor() );
         biGraphics.fillRect( 0, 0, 800, 600 );
      }

      //draw the buffered image
      g.drawImage( bi, 0, 0, this );

      if( firstTime )
      {
         g.setColor( C.getTextColor() );
         g.setFont( new Font( "monospaced", Font.BOLD, 20 ) );
         g.drawString( "Chess two player:", 100, 100 );
         g.drawString( "Right click for menu", 100, 140 );

      }
      if( !isStarted )
      {
         //print( "returning from paint" );
         return;
      }
      if( isSelected )
      {
         //print( "drawing selection" );
         drawSelection( g );
      }
      //print( "painting ends" );

   }
   public void update( Graphics g )
   {
      paint( g );
   }
   public void exitU()
   {
      if( !isStarted )
         System.exit( 0 );
      int result = JOptionPane.showConfirmDialog( this, "There is a possibility that\nyou will lose your unsaved game.\n\nDo you really want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
      if( result == JOptionPane.YES_OPTION )
         System.exit( 0 );
   }

   public void mousePressed(MouseEvent e){}
   public void mouseClicked(MouseEvent e){}
   public void mouseReleased(MouseEvent event)
   {
      //print( "mouse released" );
      if( event.isPopupTrigger() )
      {
         if( firstTime )
         {
            firstTime = false;
            repaint();
         }
         popup.show( this, 400, 100 );
      }
      else
      {
         //print( "left click working" );
         checkAndSelectU( event );
      }
   }
   public void mouseEntered(MouseEvent e){}
   public void mouseExited(MouseEvent e){}

   private void print( String msg )
   {
      System.out.println( msg );
   }
   public void actionPerformed( ActionEvent event )
   {
      //print( "action performed" );
      if( event.getSource() == menus[ 0 ] )  //Exit
      {
         exitU();
      }
      else if( event.getSource() == menus[ 1 ] )   //Start
      {
         //print( "start button" );
         if( isStarted && !confirmU( "Want to start a new Game?\n(Unsaved game will be lost)" ) )
         {
            return;
         }
         long seed = System.currentTimeMillis();
         String wp = JOptionPane.showInputDialog( this, "Enter white player's name." );
         String bp = JOptionPane.showInputDialog( this, "Enter black player's name." );
         if( wp == null || wp.trim().equals( "" ) )
         {
            wp = "Aggressor";
         }
         if( bp ==null || bp.trim().equals( "" ) || ( bp.trim() ).equals( wp.trim() ) )
         {
            bp = "Defender";
         }
         moveSequence = new MoveSequence( seed );
         name = new Name( seed );
         name.setNameU( wp, C.WHITE );
         name.setNameU( bp, C.BLACK );
         currentInstance = new ChessInstance();
         currentInstance.arrangeForNewGameU();
         moveSequence.addInstanceU( currentInstance );
         save2U();
         isStarted = true;
         refreshU();
         //print( "inside start button" );
         //print( "end start button" );
         enableAllButtonsU();
         disableReplayButtonsU();
      }
      else if( event.getSource() == menus[ 2 ] )   //Undo
      {
         if( !isStarted )
            return;
         moveSequence.prevU();
         refreshU();
      }
      else if( event.getSource() == menus[ 3 ] )   //Redo
      {
         if( !isStarted )
            return;
         moveSequence.nextU();
         refreshU();
      }
      else if( event.getSource() == menus[ 4 ] )   //Replace
      {
         if( !isStarted )
            return;
         if( !isSelected )
         {
            messageU( "Before changing a chess piece\nyou have to select a chess piece first." );
            return;
         }
         else
         {
            if( currentInstance.isEmptyU( selectedX, selectedY ) )
            {
               messageU( "Before changing a chess piece\nyou have to select a chess piece first." );
               return;
            }
            String nm[] = { "Do nothing", "White Pawn", "White Rook", "White Knight", "White Bishop", "White Queen", "White King", "Black Pawn", "Black Rook", "Black Knight", "Black Bishop", "Black Queen", "Black King" };
            JComboBox cb = new JComboBox( nm );
            JOptionPane.showMessageDialog( this, cb, "Choose a piece of chess from the list below.", JOptionPane.INFORMATION_MESSAGE );
            int p;
            if( ( p = cb.getSelectedIndex() ) != C.NONE )
            {
               currentInstance.setPieceU( selectedX, selectedY, true, p );
               currentInstance.resetMoveIndicator();
               moveSequence.addInstanceU( currentInstance );
               disableReplayButtonsU();
               refreshU();
               save2U();
            }
         }
      }
      else if( event.getSource() == menus[ 5 ] )   //Save Game
      {
         if( !isStarted )
            return;
         saveU( true );
      }
      else if( event.getSource() == menus[ 6 ] )   //Save Instance
      {
         if( !isStarted )
            return;
         saveU( false );
      }
      else if( event.getSource() == menus[ 7 ] )   //Load game
      {
         loadU( true );
      }
      else if( event.getSource() == menus[ 8 ] )   //Load instance
      {
         loadU( false );
      }
      else if( event.getSource() == menus[ 9 ] )   //Start Replay
      {
         String s = JOptionPane.showInputDialog( this, "Enter a replay interval.\n( valid: 1 - 20 second )" );
         int i = 5;
         try
         {
            i = Integer.parseInt( s );
         }
         catch( Exception e )
         {
            //no operation needed
         }
         if( i < 1 || i > 20 )
            i = 5;
         messageU( "Interval: " + i + " second(s)" );
         disableAllButtonsU();
         menus[ 10 ].setEnabled( true );
         moveSequence.firstU();
         refreshU();
         replayer = new javax.swing.Timer( ( i * 1000 ), this );
         replayer.start();
      }
      else if( event.getSource() == menus[ 10 ] )   //pause and stop replay
      {
         replayer.stop();
         replayer = null;
         enableAllButtonsU();
         disablePlayButtonsU();
         messageU( "Replay stopped" );
      }
      else if( event.getSource() == menus[ 11 ] )   //next
      {
         moveSequence.nextU();
         refreshU();
      }
      else if( event.getSource() == menus[ 12 ] )   //prev
      {
         moveSequence.prevU();
         refreshU();
      }
      else if( event.getSource() == menus[ 13 ] )   //first
      {
         moveSequence.firstU();
         refreshU();
      }
      else if( event.getSource() == menus[ 14 ] )   //last
      {
         moveSequence.lastU();
         refreshU();
      }
      else if( event.getSource() == menus[ 15 ] )  //About
      {
         JOptionPane.showMessageDialog( this, "Chess 2 Player(Advanced) v2.1( 11:50 PM 2/13/2006 ).\nSave game file format compatibility v2.0 .\nProgrammed by Wasiqul Islam, Chittagong, Bangladesh.\nE-mail address: islam.wasiqul@gmail.com\nNo help is attached here.", "About", JOptionPane.INFORMATION_MESSAGE );
      }
      else if( event.getSource() == replayer )   //pause and stop replay
      {
         moveSequence.nextU();
         refreshU();
         if( moveSequence.isLastU() )
         {
            replayer.stop();
            replayer = null;
            enableAllButtonsU();
            disablePlayButtonsU();
            messageU( "Replay stopped" );
         }
      }
   }
   private void checkAndSelectU( MouseEvent event )  //tag
   {
      int x = event.getX(), y = event.getY();
      if( ( ( x >= 0 && x <= 360 ) && ( y >= 0 && y <= 360 ) ) || ( ( x >= 440 && x <= 800 ) && ( y >= 0 && y <= 360 ) ) )
      {
         if( step == false )
         {
            step = true;
            isSelected = true;
            selectedX = calculateXU( x, y );
            selectedY = calculateYU( x, y );
         }
         else  //step == true
         {
            step = false;
            isSelected = false;
            int xx = calculateXU( x, y );
            int yy = calculateYU( x, y );
            currentInstance = moveSequence.getCurrentInstanceU();
            if( currentInstance.moveU( selectedX, selectedY, xx, yy ) )
            {
               //print( "Chess2P currentInstance.moveU returned true " );
               moveSequence.addInstanceU( currentInstance );
               disableReplayButtonsU();
               save2U();
            }
            //else
               //print( "Chess2P currentInstance.moveU returned false " );
         }
         refreshU();
      }
   }
   private int calculateXU( int x, int y )
   {
      int x2, x1;
      if( ( x >= 0 && x <= 360 ) && ( y >= 0 && y <= 360 ) )
      {
         x2 = 0;
         //print( "debug: x2 0" );
         for( x1 = 0; x1 <= 7; x1++ )
         {
            if( x >= ( x2 + ( x1 * 45 ) ) && x <= ( x2 + ( ( x1 + 1 ) * 45 ) ) )
               break;
         }
      }
      else
      {
         x2 = 800;
         //print( "debug: x2 800" );
         for( x1 = 0; x1 <= 7; x1++ )
         {
            if( x <= ( x2 - ( x1 * 45 ) ) && x > ( x2 - ( ( x1 + 1 ) * 45 ) ) )
               break;
         }
      }
 
      //print( "x1: " + x1 );
      return x1;
   }
   private int calculateYU( int x, int y )
   {
      int y2, y1;
      if( ( x >= 0 && x <= 360 ) && ( y >= 0 && y <= 360 ) )
      {
         y2 = 360;
         for( y1 = 0; y1 <= 7; y1++ )
         {
            if( y < ( y2 - ( y1  * 45  ) )  && y >= ( y2 - ( ( y1 + 1 ) * 45 ) ) )
               break;
         }
      }
      else
      {
         y2 = 0;
         for( y1 = 0; y1 <= 7; y1++ )
         {
            if( y >= ( y2 + ( y1  * 45  ) )  && y < ( y2 + ( ( y1 + 1 ) * 45 ) ) )
               break;
         }
      }

      //print( "y1: " + y1 );
      return y1;
   }
   public void drawSelection( Graphics g )
   {
      int x1 = 0, y1 = 360, x2 = 800, y2 = 0;
      x1 += selectedX * 45;
      y1 -= ( selectedY + 1 ) * 45;
      x2 -= ( selectedX + 1 ) * 45;
      y2 += selectedY  * 45;
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g2d.setStroke( ( Stroke )( new BasicStroke( ( float ) 3.0 ) ) );
      g2d.setColor( new Color( 0, 70, 0 ) );
      g2d.drawRect( x1, y1, 45, 45 );
      g2d.drawRect( x2, y2, 45, 45 );
   }
   public void refreshU()
   {
      //print( "inside refreshU" );
      currentInstance = moveSequence.getCurrentInstanceU();
      drawer.drawU( currentInstance );
      //currentInstance.printBoardU();
      Graphics g = bi.getGraphics();
      g.setColor( C.getTextColor() );
      g.setFont( new Font( "monospaced", Font.BOLD, 20 ) );
      g.drawString( "White:   " + name.getNameU( C.WHITE ), 500, 400 );
      g.drawString( "Black:   " + name.getNameU( C.BLACK ), 500, 425 );
      repaint();
   }
   private boolean confirmU( String msg )
   {
      int result = JOptionPane.showConfirmDialog( this, msg, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
      if( result == JOptionPane.YES_OPTION )
         return true;
      else
         return false;
   }
   private void messageU( String msg )
   {
      JOptionPane.showMessageDialog( this, msg );
   }
   private void save2U()
   {
      try
      {
         if( !autosaveMessage )
         {
            File file = new File( "lastAutosavedChessGame.chs" );
            if( file.exists() && !file.canWrite() )
            {
               //confirmU( "File exists, Overwrite?\nfilename: " + file.getName() );
               file.delete();
               file.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
            out.writeObject( ( Object )name );
            out.writeObject( (Object) moveSequence );
            //messageU( "Game saved as " + file.getName() + "." )
            out.close();
         }
      }
      catch( Exception e )
      {
         e.printStackTrace();
         if( !autosaveMessage )   //Message should show only once
         {
            messageU( "Failed to autosave." );
            autosaveMessage = true;
         }
      }
   }
   private void saveU( boolean indic )
   {
      try
      {
         JFileChooser fc = new JFileChooser();
         fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
         int result = fc.showSaveDialog( this );
         if( result == JFileChooser.APPROVE_OPTION )
         {
            File file = fc.getSelectedFile();

            //check that, this file has appropriate extension( .chs (chess) or .csi (chess instance) )
            String fileName = file.getAbsolutePath();
            String fileExtension = "";
            if( indic )
            {
               fileExtension = ".chs";
            }
            else
            {
               fileExtension = ".csi";
            }
            int index = fileName.toLowerCase().lastIndexOf( fileExtension );
            if( index == -1 || 
               ( index != ( fileName.length() - fileExtension.length() ) ) 
            )
            {
               fileName += fileExtension;
            }
            file = new File( fileName );

            //checking
            if( file.exists() )
            {
               if( !confirmU( "File exists, Overwrite?\nfilename: " + file.getName() ) )
               {
                  return;
               }
            }

            //write to file
            ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
            if( indic )
            {
               out.writeObject( ( Object )name );
               out.writeObject( (Object) moveSequence );
               messageU( "Game saved as [" + file.getName() + "]." );
            }
             else
            {
               out.writeObject( ( Object )name );
               out.writeObject( (Object) currentInstance );
               messageU( "Board instance saved as [" + file.getName() + "]." );
            }
            out.close();

         }
      }
      catch( Exception e )
      {
         e.printStackTrace();
         messageU( "Either failed to complete the operation\nor error may occured while operation was going on.\n"
         + e.toString() );
      }
   }
   private void disablePlayButtonsU()
   {
      menus[ 2 ].setEnabled( false );
      menus[ 3 ].setEnabled( false );
      for( int i = 9; i <= 14; i++ )
      {
         if( i == 10 )
            menus[ i ].setEnabled( false );
         else
            menus[ i ].setEnabled( true );
      }
   }
   private void disableReplayButtonsU()
   {
      menus[ 2 ].setEnabled( true );
      menus[ 3 ].setEnabled( true );
      for( int i = 9; i <= 14; i++ )
      {
         menus[ i ].setEnabled( false );
      }
   }
   private void enableAllButtonsU()
   {
      for( int i = 0; i < menus.length; i++ )
      {
         menus[ i ].setEnabled( true );
      }
   }
   private void disableAllButtonsU()
   {
      for( int i = 0; i < menus.length; i++ )
      {
         menus[ i ].setEnabled( false );
      }
   }
   private void loadU( boolean indic )
   {
      if( isStarted )
      {
         if( !confirmU( "Load Confirm?\n(Unsaved game will be lost)" ) )
            return;
      }
      try
      {
         JFileChooser fc = new JFileChooser();
         fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
         int result = fc.showOpenDialog( this );
         if( result == JFileChooser.APPROVE_OPTION )
         {
            File file = fc.getSelectedFile();
            if( !file.exists() )
            {
               messageU( "File named " + file.getName() + " does not exists." );
            }
            ObjectInputStream in = new ObjectInputStream( new FileInputStream( file ) );
            if( indic )
            {
               Name a = ( Name ) in.readObject();
               MoveSequence b = ( MoveSequence ) in.readObject();
               java.util.Date date = null;
               if( a.getSerialU() != b.getSerialU() )
                  messageU( "Serial does not match" );
               else
               {
                  date = new java.util.Date( a.getSerialU() );
               }  
               name = null;
               moveSequence = null;
               name = a;
               moveSequence = b;
               moveSequence.firstU();
               refreshU();
               enableAllButtonsU();
               disablePlayButtonsU();
               isStarted = true;
               messageU( "Game loaded.\nPlay date entry: " + date.toString() );
            }
             else
            {
               Name a = ( Name ) in.readObject();
               ChessInstance c = ( ChessInstance ) in.readObject();
               MoveSequence b = new MoveSequence( a.getSerialU() );
               java.util.Date date = new java.util.Date( a.getSerialU() );
               b.addInstanceU( c );
               save2U();
               name = null;
               moveSequence = null;
               name = a;
               moveSequence = b;
               refreshU();
               enableAllButtonsU();
               disablePlayButtonsU();
               isStarted = true;
               messageU( "Game instance loaded.\nPlay date entry: " + date.toString() );
            }
            in.close();
         }
      }
      catch( Exception e )
      {
         e.printStackTrace();
         messageU( "Invalid file\nor failed to complete the operation\nor error may occured while operation was going on." );
      }
   }

   private void load2U()
   {
      try
      {
         File file = new File( "lastAutosavedChessGame.chs" );
         if( !file.exists() )
         {
             return;
         }
         ObjectInputStream in = new ObjectInputStream( new FileInputStream( file ) );
         Name a = ( Name ) in.readObject();
         MoveSequence b = ( MoveSequence ) in.readObject();
         java.util.Date date = null;
         if( a.getSerialU() != b.getSerialU() )
            messageU( "Serial does not match." );
         else
         {
            date = new java.util.Date( a.getSerialU() );
         }  
         name = null;
         moveSequence = null;
         name = a;
         moveSequence = b;
         moveSequence.lastU();
         refreshU();
         enableAllButtonsU();
         disableReplayButtonsU();
         isStarted = true;
         //messageU( "Game autoloaded.\nPlay date: " + date.toString() );
         in.close();
         firstTime = false;
         refreshU();
      }
      catch( Exception e )
      {
         e.printStackTrace();
         messageU( "Autoload failed." );
      }
   }

   public void d( String msg )   //for debugging
   {
     System.out.println( msg );
   }

   public static void main( String args[] )
   {
      new Chess2P();
   }
}