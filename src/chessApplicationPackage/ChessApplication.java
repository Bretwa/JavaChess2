/*
ChessApplication is the only no reusable class, it interacts with the ChessRuler and the chessBoard
in order to have a well functioning of the entire application
*/

package chessApplicationPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import chessBoardPackage.ChessBoardMouseListener;
import chessBoardPackage.ChessBoardWithCoordinates;
import informationPanelPackage.InformationPanel;

public class ChessApplication extends JFrame implements ActionListener
{
	private static final int infinite=1000000000;
	private static final int maximumValueForCounting=1000000;
	private static final int numberOfCountings=5;
	private int maximumGameDescriptionLength=80;
	private int indexMoves=-1;
	private static final int white=1;
	private static final int black=-white;
	private static final int whiteIsPat=2*white;
	private static final int blackIsPat=2*black;
	private static final int noCurrentGame=0;
	public static int maximumDepth=5;
	public static int defaultDepth=3;
	private static final long serialVersionUID=1L;
	ChessBoardWithCoordinates chessBoardWithCoordinates; // the chessboard, the main component of the game
	private InformationPanel informationPanel; // the information panel, used to display information
	private static final int numberOfSquarePerLine=8;
	private ChessRuler chessRuler;
	private static ChessBoardMouseListener chessBoardMouseListener;
	public boolean whitesAtBottom=true;
	public static Point oldSelectedSquare;
	private String piecesMatrix[][]; // used for piece representation
	
	// the spaces between component
	private static final int spaceAtTopOfTheChessboard=10;
	private static final int spaceAtBottomOfTheChessboard=10;
	private static final int spaceAtLeftOfTheChessboard=10;
	private static final int spaceAtRightOfTheChessboard=10;
	private static final int spaceAtRightOfTheInformationPanel=10;
	
	// we use the box layout, the vertical box is the main box, we put the horizontal box into it
	private Box mainHorizontalBox;
	private Box mainVerticalBox;
	
	// for the menu bar 
	JMenuBar menuBar;
	private static final String game="Game";
	private static final String quit="Quit";
	private static final String newGame="New game";
	private static final String saveGame="Save game";
	private static final String loadGame="Load game";
	public static String computerLevel="Computer level";
	public static String computerPlaysBlack="Computer plays black";
	public static String computerPlaysWhite="Computer plays white";
	private static final String moves="Moves";
	private static final String remakeMove="Remake move";
	private static final String undoMove="Unmake move";
	private static final String display="Display";
	private static final String turnChessboard="Turn chessboard";
	private static final String standardNotation="Standard notation";
	private static final String explicitNotation="Explicit notation";
	private JMenuItem itemComputerPlaysBlack;
	private JMenuItem itemComputerPlaysWhite;
	public static ArrayList<JRadioButtonMenuItem> arrayListBlackLevel;
	public static ArrayList<JRadioButtonMenuItem> arrayListWhiteLevel;
	public static String blackPlayerLevel="Black player level";
	public static String whitePlayerLevel="White player level";
	
	public void invertSquare(Point squareToReverse)
	{
		squareToReverse.x=Math.abs(squareToReverse.x-numberOfSquarePerLine+1);
		squareToReverse.y=Math.abs(squareToReverse.y-numberOfSquarePerLine+1);
	}
	
	public void invertMultipleSquares(ArrayList<Point> arrayListSquaresToReverse)
	{
		Iterator<Point> PointIterator=arrayListSquaresToReverse.iterator();
		while(PointIterator.hasNext())
			invertSquare(PointIterator.next());
	}
	
	// a click has been done on the board, it has to be analyzed to know what happened
	public void onMousePressedOnTheChessBoard(MouseEvent mouseEvent) throws InterruptedException
	{
		// no need to do anything if there is no game
		if(chessRuler.getCurrentTurn()==noCurrentGame)
			return;
		
		// we get the right point for the chessboard with coordinates
		Point pointThatFitWithTheChessBoardWithCoordinates=new Point(mouseEvent.getPoint().x-spaceAtLeftOfTheChessboard-getInsets().left,mouseEvent.getPoint().y-spaceAtTopOfTheChessboard-menuBar.getHeight()-getInsets().top);
		Point newSeletectedSquare=chessBoardWithCoordinates.getCorrespondingSquare(pointThatFitWithTheChessBoardWithCoordinates);
		if(newSeletectedSquare==null)
			return;
		
		// we check if the current square is a piece the play can move 
		if(chessBoardWithCoordinates.giveMeThePieceColorOnThisSquare(newSeletectedSquare)==chessRuler.getCurrentTurn())
		{
			// we paint the old square, if it has been selected, it is repaint 
			if(oldSelectedSquare.x>=0)
			{
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
				if(whitesAtBottom==false)
					invertMultipleSquares(arrayListPossibleMoves);
				chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves); // repaint target if a new piece has been selected
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				chessBoardWithCoordinates.drawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			}
			
			// the old square is the same that the new, we have to unselect it and possible moves
			if(oldSelectedSquare.x==newSeletectedSquare.x&&oldSelectedSquare.y==newSeletectedSquare.y)
			{
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
				if(whitesAtBottom==false)
					invertMultipleSquares(arrayListPossibleMoves);
				oldSelectedSquare=new Point(-1,-1);
				return;
			}
			chessBoardWithCoordinates.drawASquare(newSeletectedSquare,Color.green,mainVerticalBox.getGraphics());
			oldSelectedSquare=new Point(newSeletectedSquare.x,newSeletectedSquare.y);
			
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			
			ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves,Color.blue);
		}
		else
		{
			// maybe a square target has been selected
			if(oldSelectedSquare.x>=0)
			{
				// we invert the old and new square in order to make the good move
				if(whitesAtBottom==false)
				{
					invertSquare(oldSelectedSquare);
					invertSquare(newSeletectedSquare);
				}
				
				if(chessRuler.IsThisMovePossible(oldSelectedSquare,newSeletectedSquare)==true)
				{
					// we erase the old possible moves            
					ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
					if(whitesAtBottom==false)
						invertMultipleSquares(arrayListPossibleMoves);
					chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
					
					// we make the move and update move description
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					if(indexMoves!=-1)
						chessRuler.SetCounterOfMoves(indexMoves);
					informationPanel.DeleteHistoricUntil(indexMoves);
					indexMoves=-1;
					informationPanel.UndrawLines();
					ArrayList<Boolean> arrayIsSpecial=new ArrayList<Boolean>();
					ArrayList<Point> arrayConcernedSquares=chessRuler.doThisMoveAndGetDescription(oldSelectedSquare,newSeletectedSquare,arrayMoveDescription,arrayIsSpecial);
					
					// we paint the  concerned squares
					transformBitSetsIntoReadableMatrix();
					for(int counterConcernedSquares=0;counterConcernedSquares<arrayConcernedSquares.size();counterConcernedSquares++)
					{
						if(whitesAtBottom==false)
							invertSquare(arrayConcernedSquares.get(counterConcernedSquares)); //we invert all to prepare to the entire transformation
						chessBoardWithCoordinates.drawASquare(arrayConcernedSquares.get(counterConcernedSquares),mainVerticalBox.getGraphics());
					}
					oldSelectedSquare=new Point(-1,-1);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial.get(0));
					
					// check if the game is over
					if(finishTheGameIfItsTheCase()==true)
						return;
					
					// play computer if necessary
					if((chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)||(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true))
						playComputer();
					
					// check if the game is over
					finishTheGameIfItsTheCase();
				}
				else
				{
					if(whitesAtBottom==false)
					{
						invertSquare(oldSelectedSquare);
						invertSquare(newSeletectedSquare);
					}
				}
			}
		}
	}
	
	private boolean finishTheGameIfItsTheCase()
	{
		int winner=chessRuler.ifGameHasEndedGiveMeTheWinner();
		if(winner!=0)
		{
			switch(winner)
			{
			case blackIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				javax.swing.JOptionPane.showMessageDialog(null,"Black player is pat ! Game is drawn.");
				break;
			case whiteIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				javax.swing.JOptionPane.showMessageDialog(null,"White player is pat ! Game is drawn.");
				break;
			case white:
				informationPanel.SetPlayerTurn("White is the winner");
				javax.swing.JOptionPane.showMessageDialog(null,"White player wins !");
				break;
			case black:
				informationPanel.SetPlayerTurn("Black is the winner");
				javax.swing.JOptionPane.showMessageDialog(null,"Black player wins !");
				break;
			default:
				;
			}
			chessRuler.endTheGame();
			return true;
		}
		return false;
	}
	
	public void playComputer() throws InterruptedException
	{
		ArrayList<Point> listPointSource=new ArrayList<Point>();
		ArrayList<Point> listPointDestination=new ArrayList<Point>();
		ArrayList<String> listMoveDescription=new ArrayList<String>();
		ArrayList<Boolean> arrayIsSpecial=new ArrayList<Boolean>();
		if(chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
			chessRuler.playComputerWithMultiThread(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial);
		if(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
			chessRuler.playComputerWithMultiThread(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial);
		goToNextTurn(listMoveDescription,arrayIsSpecial.get(0));
		transformBitSetsIntoReadableMatrix();
		if(whitesAtBottom==false)
		{
			invertSquare(listPointSource.get(0));
			invertSquare(listPointDestination.get(0));
		}
		chessBoardWithCoordinates.drawASquare(listPointSource.get(0),mainVerticalBox.getGraphics());
		chessBoardWithCoordinates.drawASquare(listPointDestination.get(0),mainVerticalBox.getGraphics());
	}
	
	// get the white computer level into the menu
	public int getWhiteLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorWhiteLevel=arrayListWhiteLevel.iterator();
		while(iteratorWhiteLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorWhiteLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting white level, no level selected");
		return -1;
	}
	
	// get the black computer level into the menu
	public int getBlackLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorBlackLevel=arrayListBlackLevel.iterator();
		while(iteratorBlackLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorBlackLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting black level, no level selected");
		return -1;
	}
	
	public void goToNextTurn(ArrayList<String> arrayListMovesDescriptions,Boolean isSpecialMove)
	{
		chessRuler.ChangePlayerTurn();
		if(chessRuler.getCurrentTurn()==white)
			informationPanel.SetPlayerTurn("White turn");
		if(chessRuler.getCurrentTurn()==black)
			informationPanel.SetPlayerTurn("Black turn");
		informationPanel.addNewMoveDescription(arrayListMovesDescriptions.get(0),arrayListMovesDescriptions.get(1),isSpecialMove);
	}
	
	// at the creation of the application we put all the box on the main frame : chessboard and information panel
	public ChessApplication()
	{
		oldSelectedSquare=new Point(-1,-1);
		
		// first of all we create the menu bar and add items on it
		menuBar=new JMenuBar();
		JMenu menuGame=new JMenu(game);
		JMenuItem itemNewGame=new JMenuItem(newGame);
		menuGame.add(itemNewGame);
		itemNewGame.addActionListener(this);
		JMenuItem itemSaveGame=new JMenuItem(saveGame);
		menuGame.add(itemSaveGame);
		itemSaveGame.addActionListener(this);
		JMenuItem itemLoadGame=new JMenuItem(loadGame);
		menuGame.add(itemLoadGame);
		itemLoadGame.addActionListener(this);
		menuGame.addSeparator();
		itemComputerPlaysBlack=new JCheckBoxMenuItem(computerPlaysBlack);
		itemComputerPlaysBlack.addActionListener(this);
		itemComputerPlaysBlack.setSelected(true); // computer play blacks by default
		menuGame.add(itemComputerPlaysBlack);
		itemComputerPlaysWhite=new JCheckBoxMenuItem(computerPlaysWhite);
		itemComputerPlaysWhite.addActionListener(this);
		menuGame.add(itemComputerPlaysWhite);
		menuGame.addSeparator();
		JMenuItem itemQuit=new JMenuItem(quit);
		menuGame.add(itemQuit);
		itemQuit.addActionListener(this);
		menuBar.add(menuGame);
		JMenu menuMoves=new JMenu(moves);
		JMenuItem itemRemakeMove=new JMenuItem(remakeMove);
		itemRemakeMove.setAccelerator(KeyStroke.getKeyStroke('r'));
		menuMoves.add(itemRemakeMove);
		itemRemakeMove.addActionListener(this);
		JMenuItem itemUnmakeMove=new JMenuItem(undoMove);
		itemUnmakeMove.setAccelerator(KeyStroke.getKeyStroke('u'));
		menuMoves.add(itemUnmakeMove);
		itemUnmakeMove.addActionListener(this);
		menuBar.add(menuMoves);
		JMenu menuDisplay=new JMenu(display);
		ButtonGroup group=new ButtonGroup();
		JRadioButtonMenuItem itemStandardNotation=new JRadioButtonMenuItem(standardNotation);
		menuDisplay.add(itemStandardNotation);
		group.add(itemStandardNotation);
		itemStandardNotation.addActionListener(this);
		JRadioButtonMenuItem itemExplicitNotation=new JRadioButtonMenuItem(explicitNotation);
		itemExplicitNotation.setSelected(true);
		menuDisplay.add(itemExplicitNotation);
		group.add(itemExplicitNotation);
		itemExplicitNotation.addActionListener(this);
		menuDisplay.addSeparator();
		JMenuItem itemSwitchSides=new JMenuItem(turnChessboard);
		itemSwitchSides.setAccelerator(KeyStroke.getKeyStroke('t'));
		menuDisplay.add(itemSwitchSides);
		itemSwitchSides.addActionListener(this);
		menuBar.add(menuDisplay);
		
		// now a create the menu for the computer level
		JMenu menuComputerConfiguration=new JMenu(computerLevel);
		
		// add black computer levels
		ButtonGroup groupBlack=new ButtonGroup();
		arrayListBlackLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String blackPlayerLevelCounter=blackPlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemBlackPlayerLevel=new JRadioButtonMenuItem(blackPlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemBlackPlayerLevel.setSelected(true);
			arrayListBlackLevel.add(menuItemBlackPlayerLevel);
			menuItemBlackPlayerLevel.addActionListener(this);
			groupBlack.add(menuItemBlackPlayerLevel);
			menuComputerConfiguration.add(menuItemBlackPlayerLevel);
		}
		
		// put a separator between black level and white level
		menuComputerConfiguration.addSeparator();
		
		// add white computer levels
		ButtonGroup groupWhite=new ButtonGroup();
		arrayListWhiteLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String whitePlayerLevelCounter=whitePlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemWhitePlayerLevel=new JRadioButtonMenuItem(whitePlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemWhitePlayerLevel.setSelected(true);
			arrayListWhiteLevel.add(menuItemWhitePlayerLevel);
			menuItemWhitePlayerLevel.addActionListener(this);
			groupWhite.add(menuItemWhitePlayerLevel);
			menuComputerConfiguration.add(menuItemWhitePlayerLevel);
		}
		menuComputerConfiguration.addActionListener(this);
		menuBar.add(menuComputerConfiguration);
		setJMenuBar(menuBar);
		
		// ChessRuler, who knows the chess functioning and the mouse listener of the frame
		chessRuler=new ChessRuler();
		chessBoardMouseListener=new ChessBoardMouseListener(this);
		addMouseListener(chessBoardMouseListener);
		
		// we create the two main box
		mainHorizontalBox=Box.createHorizontalBox();
		mainVerticalBox=Box.createVerticalBox();
		piecesMatrix=new String[numberOfSquarePerLine][numberOfSquarePerLine];
		
		for(int CounterVertical=0;CounterVertical<numberOfSquarePerLine;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<numberOfSquarePerLine;CounterHorizontal++)
				piecesMatrix[CounterVertical][CounterHorizontal]=new String("");
		setVisible(true);
		// we put the chessboard at the right place
		chessBoardWithCoordinates=new ChessBoardWithCoordinates(getGraphics(),piecesMatrix);
		Dimension chessBoardDimension=chessBoardWithCoordinates.getDimension();
		setVisible(false);
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtLeftOfTheChessboard,chessBoardDimension.height)));
		mainHorizontalBox.add(chessBoardWithCoordinates);
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheChessboard,chessBoardDimension.height)));
		
		// we add the information panel
		informationPanel=new InformationPanel(chessBoardDimension.height,getGraphics());
		mainHorizontalBox.add(informationPanel);
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheInformationPanel,chessBoardDimension.height)));
		
		// we manage the vertical box
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtTopOfTheChessboard)));
		mainVerticalBox.add(mainHorizontalBox);
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtBottomOfTheChessboard)));
		getContentPane().add(mainVerticalBox);
		
		// now we have to calculated the right dimension of the main frame
		setResizable(false);
		pack();
		Dimension dimensionThatFit=new Dimension();
		dimensionThatFit.height=chessBoardDimension.height+menuBar.getHeight()+getInsets().top+getInsets().bottom+spaceAtTopOfTheChessboard+spaceAtBottomOfTheChessboard;
		dimensionThatFit.width=chessBoardDimension.width+getInsets().left+getInsets().right+spaceAtLeftOfTheChessboard+spaceAtRightOfTheChessboard+spaceAtRightOfTheInformationPanel+informationPanel.getWidth();
		setSize(dimensionThatFit);
		setLocationRelativeTo(getParent());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("JavaChess 2");
		informationPanel.SetPlayerTurn("White turn");
		setVisible(true);
	}
	
	// we transform each of the matrix used by the ChessRuler
	public void transformBitSetsIntoReadableMatrix()
	{
		// first of all we erase all the matrix in order to recreate a clean one
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
				piecesMatrix[counterVertical][counterHorizontal]=new String("");
			
		// now we have to fill the string matrix according to the chessboard orientation
		if(whitesAtBottom==true)
		{
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteRooks.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wr");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteKnights.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wk");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteBishops.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wb");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whitePawns.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wp");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteQueen.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wq");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteKing.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("wK");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackRooks.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("br");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackKnights.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("bk");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackBishops.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("bb");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackPawns.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("bp");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackQueen.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("bq");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackKing.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[resultFoundPiece/numberOfSquarePerLine][resultFoundPiece%numberOfSquarePerLine]=new String("bK");
		}
		else
		{
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteRooks.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wr");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteKnights.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wk");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteBishops.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wb");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whitePawns.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wp");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteQueen.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wq");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.whiteKing.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("wK");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackRooks.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("br");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackKnights.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("bk");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackBishops.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("bb");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackPawns.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("bp");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackQueen.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("bq");
			for(int resultFoundPiece=-1;(resultFoundPiece=chessRuler.blackKing.nextSetBit(resultFoundPiece+1))!=-1;)
				piecesMatrix[Math.abs(resultFoundPiece/numberOfSquarePerLine-numberOfSquarePerLine+1)][Math.abs(resultFoundPiece%numberOfSquarePerLine-numberOfSquarePerLine+1)]=new String("bK");
		}
	}
	
	@Override
	public void paint(Graphics graphics)
	{
		// here we have to update all the components of the main window, the selected square if necessary
		menuBar.paint(menuBar.getGraphics());
		transformBitSetsIntoReadableMatrix();
		mainVerticalBox.paint(mainVerticalBox.getGraphics());
		
		if(oldSelectedSquare.x>=0)
		{
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawASquare(oldSelectedSquare,Color.green,mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves,Color.blue);
		}
	}
	
	// useful when an action has been done and selection doesn't have anymore sense
	public void unselectSquareIfSelected()
	{
		if(oldSelectedSquare.x>=0)
		{
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			ArrayList<Point> arrayListPossibleMoves=chessRuler.GetListOfPossibleMovesForAPieceWithCheckChecking(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
			oldSelectedSquare=new Point(-1,1);
		}
	}
	
	public String getNextWord(String stringParameter,int wordCounterParameter)
	{
		int indexBeginingWord=0;
		int wordCounter=0;
		for(int charCounter=1;charCounter<stringParameter.length();charCounter++)
		{
			if((stringParameter.charAt(charCounter)!=' '&&stringParameter.charAt(charCounter)!='\n')&&stringParameter.charAt(charCounter-1)==' '||stringParameter.charAt(charCounter-1)=='\n')
				indexBeginingWord=charCounter;
			
			if((stringParameter.charAt(charCounter)==' '||stringParameter.charAt(charCounter)=='\n')&&stringParameter.charAt(charCounter-1)!=' '&&stringParameter.charAt(charCounter-1)!='\n')
			{
				if(wordCounter==wordCounterParameter)
					return stringParameter.substring(indexBeginingWord,charCounter);
				wordCounter++;
			}
		}
		if(wordCounter==wordCounterParameter)
			return stringParameter.substring(indexBeginingWord,stringParameter.length());
		return "";
	}
	
	private void playComputerVsComputerGame() throws InterruptedException
	{
		paint(getGraphics());
		long beginingTimeForComputerVsComputerGame=System.currentTimeMillis();
		for(int counterMoves=0;counterMoves<200;counterMoves++)
		{
			ArrayList<Point> listPointSource=new ArrayList<Point>();
			ArrayList<Point> listPointDestination=new ArrayList<Point>();
			ArrayList<String> listMoveDescription=new ArrayList<String>();
			ArrayList<Boolean> arrayIsSpecial=new ArrayList<Boolean>();
			if(chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
				chessRuler.playComputerWithMultiThread(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial);
			if(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
				chessRuler.playComputerWithMultiThread(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial);
			transformBitSetsIntoReadableMatrix();
			if(whitesAtBottom==false)
			{
				invertSquare(listPointSource.get(0));
				invertSquare(listPointDestination.get(0));
			}
			chessBoardWithCoordinates.drawASquare(listPointSource.get(0),mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawASquare(listPointDestination.get(0),mainVerticalBox.getGraphics());
			goToNextTurn(listMoveDescription,arrayIsSpecial.get(0));
			if(finishTheGameIfItsTheCase()==true)
				break;
		}
		long endTimeForComputerVsComputerGame=System.currentTimeMillis();
		long totalTimeForComputerVsComputerGame=endTimeForComputerVsComputerGame-beginingTimeForComputerVsComputerGame;
		System.out.println("Game has ended diff : "+totalTimeForComputerVsComputerGame+" total evaluations : "+chessRuler.totalCounterEvaluation);
		System.out.println("total : "+chessRuler.totalCounterEvaluation/(totalTimeForComputerVsComputerGame/(float)1000));
	}
	
	// look if an event has occurred on the menu's items
	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		if(actionEvent.getActionCommand().equals(standardNotation))
		{
			informationPanel.SetToStandardNotation();
			unselectSquareIfSelected();
		}
		
		if(actionEvent.getActionCommand().equals(explicitNotation))
		{
			informationPanel.SetToExplicitNotation(indexMoves);
			unselectSquareIfSelected();
		}
		
		// we remake the move
		if(actionEvent.getActionCommand().equals(remakeMove))
		{
			if(indexMoves==informationPanel.GiveNumberOfMoves()||indexMoves==-1)
				return; // there is no remake to do so we leave
			indexMoves++;
			informationPanel.DrawLine(indexMoves);
			String sourceStringSquare=informationPanel.GetSourceSquare(indexMoves-1);
			if(sourceStringSquare==null) // castling management
			{
				ArrayList<Point> arrayConcernedSquares=chessRuler.MakeCastling(informationPanel.GetStringAt(indexMoves-1));
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
				{
					if(whitesAtBottom==false)
						invertSquare(arrayConcernedSquares.get(counterSquares));
					chessBoardWithCoordinates.drawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
				}
			}
			else
			{
				Point sourceSquare=chessRuler.GetCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.GetDestinationSquare(indexMoves-1);
				Point destinationSquare=chessRuler.GetCorrespondingSquare(destinationStringSquare);
				ArrayList<String> arrayMoveDescription=new ArrayList<String>();
				ArrayList<Boolean> arrayIsSpecial=new ArrayList<Boolean>();
				chessRuler.doThisMoveAndGetDescription(sourceSquare,destinationSquare,arrayMoveDescription,arrayIsSpecial);
				chessRuler.SetCounterOfMoves(indexMoves);
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				if(whitesAtBottom==false)
				{
					invertSquare(destinationSquare);
					invertSquare(sourceSquare);
				}
				chessBoardWithCoordinates.drawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.drawASquare(destinationSquare,mainVerticalBox.getGraphics());
			}
			unselectSquareIfSelected();
			chessRuler.ChangePlayerTurn();
			
			if(chessRuler.getCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.getCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			
			finishTheGameIfItsTheCase(); // check if the game is over
		}
		
		// we undo the move 
		if(actionEvent.getActionCommand().equals(undoMove))
		{
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			if(chessRuler.getCurrentTurn()==noCurrentGame)
				chessRuler.setToLastTurnBeforeCheckAndMate(informationPanel.IsPairNumberOfMoves());
			unselectSquareIfSelected();
			if(indexMoves==0)
				return; // we have undo everything so we leave
			if(indexMoves==-1)
				indexMoves=informationPanel.GiveNumberOfMoves(); // this is the beginning of a new cycle
			indexMoves--;
			informationPanel.DrawLine(indexMoves);
			chessRuler.ChangePlayerTurn();
			if(chessRuler.getCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.getCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			String sourceStringSquare=informationPanel.GetSourceSquare(indexMoves);
			if(sourceStringSquare==null)
			{
				ArrayList<Point> arrayConcernedSquares=chessRuler.UnmakeCastling(informationPanel.GetStringAt(indexMoves)); // castling management
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
				{
					if(whitesAtBottom==false)
						invertSquare(arrayConcernedSquares.get(counterSquares));
					chessBoardWithCoordinates.drawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
				}
			}
			else
			{
				Point sourceSquare=chessRuler.GetCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.GetDestinationSquare(indexMoves);
				Point destinationSquare=chessRuler.GetCorrespondingSquare(destinationStringSquare);
				String pieceTypeEventuallyDeleted=informationPanel.GetPieceTypeEventuallyDeleted(indexMoves);
				chessRuler.UnmakeMove(sourceSquare,destinationSquare,pieceTypeEventuallyDeleted,informationPanel.isThisMoveSpecial(indexMoves));
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				if(whitesAtBottom==false)
				{
					invertSquare(destinationSquare);
					invertSquare(sourceSquare);
				}
				chessBoardWithCoordinates.drawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.drawASquare(destinationSquare,mainVerticalBox.getGraphics());
			}
		}
		
		// we leave the application
		if(actionEvent.getActionCommand().equals(quit))
		{
			System.exit(0);
		}
		
		// we save the game into a local file
		if(actionEvent.getActionCommand().equals(saveGame))
		{
			// we have to build the file name, before all we retrieve the current date
			Calendar currentCalendar=Calendar.getInstance();
			long beginingTime=currentCalendar.getTimeInMillis();
			Date currentDate=new Date(beginingTime);
			
			// now we retrieve the beginning date
			DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd kk-mm-ss");
			Date beginningDate=chessRuler.GetBeginningDate();
			String stringBeginningDate=dateFormat.format(beginningDate);
			GregorianCalendar difference=new GregorianCalendar();
			
			// we calculate the difference 
			difference.setTimeInMillis(currentDate.getTime()-beginningDate.getTime());
			int year=difference.get(Calendar.YEAR)-1970;
			int month=difference.get(Calendar.MONTH);
			int day=difference.get(Calendar.DAY_OF_MONTH)-1;
			int hour=difference.get(Calendar.HOUR_OF_DAY)-1;
			int minute=difference.get(Calendar.MINUTE);
			int seconds=difference.get(Calendar.SECOND);
			
			// now we have all the items to create the entire file name
			String fileName="c:\\"+stringBeginningDate+" - ";
			if(year>0)
				fileName+=year+"-";
			if(month>0||year>0)
			{
				if(month<10)
					fileName+="0";
				fileName+=month+"-";
			}
			if(day>0||month>0||year>0)
			{
				if(day<10)
					fileName+="0";
				fileName+=day+" ";
			}
			if(hour>0||day>0||month>0||year>0)
			{
				if(hour<10)
					fileName+="0";
				fileName+=hour+"-";
			}
			if(minute<10)
				fileName+="0";
			fileName+=""+minute+"-";
			if(seconds<10)
				fileName+="0";
			fileName+=seconds+" - "+chessRuler.GetCounterOfMoves()+".pgn"; // here we have the full file name
			
			// we put the good look and feel 
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
			Locale.setDefault(java.util.Locale.ENGLISH);
			
			//we create the file chooser
			final JFileChooser fileChooser=new JFileChooser();
			fileChooser.setLocale(Locale.ENGLISH);
			fileChooser.updateUI();
			fileChooser.setSelectedFile(new File(fileName));
			fileChooser.setDialogTitle("Select directory to save current game");
			int returnOpenDialog=fileChooser.showOpenDialog(this);
			if(returnOpenDialog!=0)
				return;
			File file=fileChooser.getSelectedFile();
			String fileNameSelected=file.getPath();
			if(fileNameSelected==null)
				return;
			
			// we have to get all the standard moves descriptions and concatenate all of it
			ArrayList<String> listMovesDescription=informationPanel.GetStandardArrayMovesDescription();
			String concatenationMovesDescription="";
			int currentLineLength=0;
			for(int counterMoves=0;counterMoves<listMovesDescription.size();counterMoves++)
			{
				currentLineLength+=listMovesDescription.get(counterMoves).length();
				if(currentLineLength>maximumGameDescriptionLength)
				{
					concatenationMovesDescription+="\n";
					currentLineLength=listMovesDescription.get(counterMoves).length();
				}
				concatenationMovesDescription+=listMovesDescription.get(counterMoves);
				if(counterMoves<listMovesDescription.size()-1)
				{
					concatenationMovesDescription+=" ";
					currentLineLength++;
				}
			}
			
			// now we can write everything on the file
			try
			{
				FileWriter fileWriter=new FileWriter(fileNameSelected,true);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				bufferedWriter.write("[Event \"Local game\"]\n");
				bufferedWriter.write("[Site \"?\"]\n");
				bufferedWriter.write("[Date \""+new SimpleDateFormat("yyyy").format(beginningDate)+"."+new SimpleDateFormat("MM").format(beginningDate)+"."+new SimpleDateFormat("dd").format(beginningDate)+"\"]\n");
				bufferedWriter.write("[Round \""+chessRuler.GetCounterOfMoves()+"\"]\n");
				bufferedWriter.write("[White \"?\"]\n");
				bufferedWriter.write("[Black \"?\"]\n");
				bufferedWriter.write("[Result \"*\"]\n");
				bufferedWriter.write("[ProcessorIdentifier \""+System.getenv("PROCESSOR_IDENTIFIER")+"\"]\n");
				bufferedWriter.write("[ProcessorArchitectureType \""+System.getenv("PROCESSOR_ARCHITECTURE")+"\"]\n");
				bufferedWriter.write("[ProcessorArchitecture64/32Bits \""+System.getenv("PROCESSOR_ARCHITEW6432")+"\"]\n");
				bufferedWriter.write("[NumberOfProcessors \""+System.getenv("NUMBER_OF_PROCESSORS")+"\"]\n");
				
				// we do some performance tests
				long sumOfTimeDifferences=0;
				int result=0;
				for(int counterOfCountings=0;counterOfCountings<numberOfCountings;counterOfCountings++)
				{
					long beginingTimeForCounting=System.currentTimeMillis();
					result=0;
					for(int counter=0;counter<maximumValueForCounting;counter++)
						result=(int)Math.random();
					long endTimeForCounting=System.currentTimeMillis();
					long differenceTimeForCounting=endTimeForCounting-beginingTimeForCounting;
					sumOfTimeDifferences+=differenceTimeForCounting;
					bufferedWriter.write("[MillisecondsToReach1MillionRandomValues"+(counterOfCountings+1)+" \""+differenceTimeForCounting+"\"]\n");
					if(result>infinite) // it's never reached just to avoid optimization
						System.out.println("Computation result : "+result);
				}
				bufferedWriter.write("[MillisecondsToReach1MillionRandomValuesAverage \""+sumOfTimeDifferences/numberOfCountings+"\"]\n\n");
				
				// the game itself
				bufferedWriter.write(concatenationMovesDescription);
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
		}
		
		if(actionEvent.getActionCommand().equals(computerPlaysBlack))
		{
			if(itemComputerPlaysBlack.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.DeleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.UndrawLines();
				if(chessRuler.getCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysBlack.setSelected(false);
					return;
				}
				if(itemComputerPlaysWhite.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.getCurrentTurn()==black)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		if(actionEvent.getActionCommand().equals(computerPlaysWhite))
		{
			if(itemComputerPlaysWhite.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.DeleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.UndrawLines();
				if(chessRuler.getCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysWhite.setSelected(false);
					return;
				}
				if(itemComputerPlaysBlack.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.getCurrentTurn()==white)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		// we load a game
		if(actionEvent.getActionCommand().equals(loadGame))
		{
			// we open the file dialog box to select the right file
			String fileName="";
			FileDialog fileDialog=new FileDialog(this);
			fileDialog.setTitle("Select directory to save current game");
			fileDialog.setMode(FileDialog.LOAD);
			fileDialog.setVisible(true);
			fileName=fileDialog.getDirectory()+fileDialog.getFile();
			if(fileName==""||fileName==null||fileDialog.getFile()==null)
				return;
			
			// we open the file chosen and read its entire content
			File file=new File(fileName);
			StringBuilder stringBuilder=null;
			Charset charset=Charset.defaultCharset();
			Reader reader=null;
			try
			{
				reader=new InputStreamReader(new FileInputStream(file),charset);
			}
			catch(FileNotFoundException fileNotFoundException)
			{
				fileNotFoundException.printStackTrace();
			}
			stringBuilder=new StringBuilder((int)file.length());
			char[] arrayChar=new char[(int)file.length()];
			int sizeRead=0;
			try
			{
				sizeRead=reader.read(arrayChar);
			}
			catch(IOException inputOutputException)
			{
				inputOutputException.printStackTrace();
			}
			stringBuilder.append(arrayChar,0,sizeRead);
			
			// we reset the entire current game
			unselectSquareIfSelected();
			chessRuler.InitializeNewGame();
			informationPanel.UndrawLines();
			indexMoves=-1;
			informationPanel.ClearList();
			informationPanel.SetPlayerTurn("White turn");
			informationPanel.UndrawLines();
			
			// now we have to analyze the content of the file
			String movesDescription=new String(arrayChar); // we get the entire content of the file into a string
			movesDescription=movesDescription.substring(movesDescription.indexOf("\n1."));
			movesDescription=movesDescription.replaceAll("\\+","");
			movesDescription=movesDescription.replaceAll("x",""); // the fact piece are eaten doesn't bring any useful information
			movesDescription=movesDescription.replaceAll("\n"," "); // carriage return are more problem than other thing, we replace by space to because it has same meaning
			informationPanel.SetToExplicitNotation(indexMoves);
			
			// we look at all the words in the string
			for(int wordCounter=0;;wordCounter++)
			{
				String currentWord=getNextWord(movesDescription,wordCounter);
				if(currentWord.equals(""))
					break;
				if(currentWord.indexOf(".")==-1)
				{
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					ArrayList<Boolean> arrayIsSpecial=new ArrayList<Boolean>();
					chessRuler.doThisMoveAndGetDescription(currentWord,arrayMoveDescription,arrayIsSpecial);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial.get(0));
				}
			}
			
			// loaded of the file is done, we can now update graphics
			paint(getGraphics());
		}
		
		// we initialize a new game
		if(actionEvent.getActionCommand().equals(newGame))
		{
			indexMoves=-1;
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			chessRuler.InitializeNewGame();
			transformBitSetsIntoReadableMatrix();
			informationPanel.ClearList();
			informationPanel.SetPlayerTurn("White turn");
			paint(getGraphics());
		}
		
		// we turn the chessboard of 180 degrees
		if(actionEvent.getActionCommand().equals(turnChessboard))
		{
			oldSelectedSquare=new Point(-1,-1); // we unselect the last piece, which is quite natural
			whitesAtBottom=!whitesAtBottom;
			chessBoardWithCoordinates.turn180Degrees();
			paint(getGraphics());
		}
	}
}
