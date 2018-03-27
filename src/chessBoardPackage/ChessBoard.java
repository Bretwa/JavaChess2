package chessBoardPackage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ChessBoard extends JComponent
{
	private int white=1;
	private int black=-white;
	
	private static final long serialVersionUID=1L;
	private int squareSize=80;
	private int numberOfSquarePerLine=8;
	String piecesMatrix[][];
	private int rectangleSelectionWidth=4;
	
	// the images for each kind of piece
	public BufferedImage whiteRookImage;
	public BufferedImage whiteKnightImage;
	public BufferedImage whiteBishopImage;
	public BufferedImage whiteQueenImage;
	public BufferedImage whiteKingImage;
	public BufferedImage whitePawnImage;
	public BufferedImage blackRookImage;
	public BufferedImage blackKnightImage;
	public BufferedImage blackBishopImage;
	public BufferedImage blackQueenImage;
	public BufferedImage blackKingImage;
	public BufferedImage blackPawnImage;
	
	public String whiteKnightImageFile="knight_80_filter_white.png";
	public String blackKnightImageFile="knight_80_filter_black.png";
	public String whiteBishopImageFile="bishop_80_filter_white.png";
	public String blackBishopImageFile="bishop_80_filter_black.png";
	public String whiteRookImageFile="rook_80_filter_white.png";
	public String blackRookImageFile="rook_80_filter_black.png";
	public String whiteQueenImageFile="queen_80_filter_white.png";
	public String blackQueenImageFile="queen_80_filter_black.png";
	public String whiteKingImageFile="king_80_filter_white.png";
	public String blackKingImageFile="king_80_filter_black.png";
	public String whitePawnImageFile="pawn_80_filter_white.png";
	public String blackPawnImageFile="pawn_80_filter_black.png";
	
	// draw a square according to a specific color
	public void drawRectableToASquare(Point pointParameter,Color colorParameter,Graphics graphics)
	{
		int HorizontalSquareSelected=pointParameter.x;
		int VerticalSquareSelected=pointParameter.y;
		graphics.setColor(colorParameter);
		for(int RectangleWidth=0;RectangleWidth<rectangleSelectionWidth;RectangleWidth++)
		{
			// left
			graphics.drawLine(HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize+squareSize-1);
			
			// top
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+RectangleWidth,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+RectangleWidth);
			
			// right
			graphics.drawLine(HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize+squareSize-1);
			
			// bottom
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1);
		}
	}
	
	// retrieve a point in ChessBoard coordinates with pixels coordinates as input parameter
	public Point getCorrespondingSquare(Point pointCoordinates)
	{
		int horizontalSquareSelected=pointCoordinates.x/squareSize;
		int verticalSquareSelected=pointCoordinates.y/squareSize;
		if(horizontalSquareSelected<0||horizontalSquareSelected>=numberOfSquarePerLine||verticalSquareSelected<0||verticalSquareSelected>=numberOfSquarePerLine)
			return null;
		return new Point(horizontalSquareSelected,verticalSquareSelected);
	}
	
	public int giveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("w"))
			return white;
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("b"))
			return black;
		return 0;
	}
	
	// constructor : read all the image
	public ChessBoard(String piecesMatrixParameter[][])
	{
		piecesMatrix=piecesMatrixParameter;
		setPreferredSize(getDimension());
		try
		{
			whiteRookImage=ImageIO.read(getClass().getResourceAsStream(whiteRookImageFile));
			whiteKnightImage=ImageIO.read(getClass().getResourceAsStream(whiteKnightImageFile));
			whiteBishopImage=ImageIO.read(getClass().getResourceAsStream(whiteBishopImageFile));
			whiteQueenImage=ImageIO.read(getClass().getResourceAsStream(whiteQueenImageFile));
			whiteKingImage=ImageIO.read(getClass().getResourceAsStream(whiteKingImageFile));
			whitePawnImage=ImageIO.read(getClass().getResourceAsStream(whitePawnImageFile));
			blackRookImage=ImageIO.read(getClass().getResourceAsStream(blackRookImageFile));
			blackKnightImage=ImageIO.read(getClass().getResourceAsStream(blackKnightImageFile));
			blackBishopImage=ImageIO.read(getClass().getResourceAsStream(blackBishopImageFile));
			blackQueenImage=ImageIO.read(getClass().getResourceAsStream(blackQueenImageFile));
			blackKingImage=ImageIO.read(getClass().getResourceAsStream(blackKingImageFile));
			blackPawnImage=ImageIO.read(getClass().getResourceAsStream(blackPawnImageFile));
		}
		catch(IOException imageException)
		{
			imageException.printStackTrace();
		}
	}
	
	public Dimension getDimension()
	{
		return new Dimension(numberOfSquarePerLine*squareSize,numberOfSquarePerLine*squareSize);
	}
	
	// allow to put an image under another one considering a transparent color 
	public BufferedImage makeColorTransparent(BufferedImage imageParameter,Color color)
	{
		BufferedImage resultImage=new BufferedImage(imageParameter.getWidth(),imageParameter.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics=resultImage.createGraphics();
		graphics.setComposite(AlphaComposite.Src);
		graphics.drawImage(imageParameter,null,0,0);
		graphics.dispose();
		for(int counterVertical=0;counterVertical<resultImage.getHeight();counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<resultImage.getWidth();counterHorizontal++)
				if(resultImage.getRGB(counterHorizontal,counterVertical)==color.getRGB())
					resultImage.setRGB(counterHorizontal,counterVertical,0x8F1C1C);
		return resultImage;
	}
	
	public void drawASquare(Point insertionPoint,Graphics graphics)
	{
		// first of all we repaint the square itself
		if(insertionPoint.y%2==0)
		{
			if(insertionPoint.x%2==0)
				graphics.setColor(Color.white);
			else
				graphics.setColor(Color.black);
		}
		else
		{
			if(insertionPoint.x%2==0)
				graphics.setColor(Color.black);
			else
				graphics.setColor(Color.white);
		}
		graphics.fillRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
		graphics.drawRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wr")==true)
			graphics.drawImage(makeColorTransparent(whiteRookImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wk")==true)
			graphics.drawImage(makeColorTransparent(whiteKnightImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wb")==true)
			graphics.drawImage(makeColorTransparent(whiteBishopImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wq")==true)
			graphics.drawImage(makeColorTransparent(whiteQueenImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wK")==true)
			graphics.drawImage(makeColorTransparent(whiteKingImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("wp")==true)
			graphics.drawImage(makeColorTransparent(whitePawnImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("br")==true)
			graphics.drawImage(makeColorTransparent(blackRookImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("bk")==true)
			graphics.drawImage(makeColorTransparent(blackKnightImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("bb")==true)
			graphics.drawImage(makeColorTransparent(blackBishopImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("bq")==true)
			graphics.drawImage(makeColorTransparent(blackQueenImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("bK")==true)
			graphics.drawImage(makeColorTransparent(blackKingImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
		if(piecesMatrix[insertionPoint.y][insertionPoint.x].equals("bp")==true)
			graphics.drawImage(makeColorTransparent(blackPawnImage,Color.white),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
	}
	
	// draw several squares in blue given in a list 
	public void drawSeveralSquares(ArrayList<Point> possibleMoves)
	{
		Iterator<Point> PointIterator=possibleMoves.iterator();
		while(PointIterator.hasNext())
		{
			Point currentPoint=PointIterator.next();
			drawASquare(currentPoint,getGraphics());
		}
	}
	
	// draw several squares in blue given in a list 
	public void drawSeveralSquares(ArrayList<Point> possibleMoves,Color colorParameter)
	{
		Iterator<Point> PointIterator=possibleMoves.iterator();
		while(PointIterator.hasNext())
		{
			Point currentPoint=PointIterator.next();
			drawRectableToASquare(currentPoint,colorParameter,getGraphics());
		}
	}
	
	// repaint the whole chess board, each square actually
	@Override
	public void paintComponent(Graphics graphics)
	{
		for(int CounterVertical=0;CounterVertical<numberOfSquarePerLine;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<numberOfSquarePerLine;CounterHorizontal++)
				drawASquare(new Point(CounterHorizontal,CounterVertical),graphics);
	}
}