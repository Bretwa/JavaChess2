/*
ChessRuler knows every rule of the chess game
*/

package chessApplicationPackage;

import java.awt.Point;
import java.sql.Date;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ChessRuler extends Thread
{
	private int numberOfBestMoves;
	private int arrayBestSource[];
	private int arrayBestDestination[];
	private int arrayArraysTypes[][];
	private int arrayArraysSources[][];
	private int arrayArraysDestination[][];
	private static final int deepestLevel=10;
	private static final int maximumPossibleMoves=200;
	private ArrayList<PiecesSituation> listPiecesSituation;
	private ArrayList<Integer> listPiecesSituationOccurrences;
	private static final int maximumOccurrenceForASituation=3;
	public int totalCounterEvaluation=0;
	private int evaluationsCounterForCurrentThread;
	private int depthForThreadComputing;
	private int arrayValuesForThreadComputingSecondLevel[];
	private int beginSourceDestinationForThreadComputing;
	private int endSourceDestinationForThreadComputing;
	int arrayPieceTypeForThreadComputingFirstLevel[];
	int arraySourceForThreadComputingFirstLevel[];
	int arrayDestinationForThreadComputingFirstLevel[];
	int arrayPieceTypeForThreadComputingSecondLevel[];
	int arraySourceForThreadComputingSecondLevel[];
	int arrayDestinationForThreadComputingSecondLevel[];
	private static final int white=1;
	private static final int whiteIsPat=2*white;
	private static final int black=-white;
	private static final int blackIsPat=2*black;
	private static final int noCurrentGame=0;
	private static final int numberOfSquarePerLine=8;
	private int currentTurn;
	private int counterMoveFinished;
	private Date gameBeginningDate;
	private static final int evaluationThatNeverHappened=1234567;
	private static final int infinite=1000000000;
	private static final int noPieceId=0;
	private static final int pawnId=1;
	private static final int knightId=2;
	private static final int bishopId=3;
	private static final int rookId=4;
	private static final int queenId=5;
	private static final int kingId=6;
	private static final int pawnValue=10;
	private static final int knightValue=30;
	private static final int bishopValue=32;
	private static final int rookValue=50;
	private static final int queenValue=90;
	private static final int kingValue=10000;
	
	// moves for each type of piece
	private static HashMap<BitSet,BitSet> HashMapKnightMoves;
	private static HashMap<BitSet,BitSet> HashMapKingMoves;
	private static HashMap<BitSet,BitSet> HashMapLinesMasks;
	private static HashMap<BitSet,BitSet> HashMapLinesPossibilities;
	private static HashMap<BitSet,BitSet> HashMapDiagonalsMasks;
	private static HashMap<BitSet,BitSet> HashMapDiagonalsPossibilities;
	private static BitSet[] arrayDiagonalsExtremes;
	private static HashMap<BitSet,BitSet> HashMapLinesAndDiagonalesForCheck;
	
	// all the bitsets for each type of piece
	public BitSet whiteRooks;
	public BitSet whiteKnights;
	public BitSet whiteBishops;
	public BitSet whiteQueen;
	public BitSet whiteKing;
	public BitSet whitePawns;
	public BitSet blackRooks;
	public BitSet blackKnights;
	public BitSet blackBishops;
	public BitSet blackQueen;
	public BitSet blackKing;
	public BitSet blackPawns;
	
	// for castlings
	public BitSet whiteKingCastlingMask;
	public BitSet whiteQueenCastlingMask;
	public BitSet blackKingCastlingMask;
	public BitSet blackQueenCastlingMask;
	public int beginBlackQueenCastling=1;
	public int endBlackQueenCastling=3;
	public int beginBlackKingCastling=5;
	public int endBlackKingCastling=6;
	public int beginWhiteQueenCastling=57;
	public int endWhiteQueenCastling=59;
	public int beginWhiteKingCastling=61;
	public int endWhiteKingCastling=62;
	public int blackKingQueenCastlingDestination=2;
	public int blackRookQueenCastlingDestination=3;
	public int blackKingKingCastlingDestination=6;
	public int blackRookKingCastlingDestination=5;
	public int whiteKingQueenCastlingDestination=58;
	public int whiteRookQueenCastlingDestination=59;
	public int whiteKingKingCastlingDestination=62;
	public int whiteRookKingCastlingDestination=61;
	public String kingSideCastlingStandard="0-0";
	public String kingSideCastlingExplicit="kingside castling";
	public String queenSideCastlingStandard="0-0-0";
	public String queenSideCastlingExplicit="queenside castling";
	public boolean isWhiteKingHasMoved;
	public boolean isBlackKingHasMoved;
	public boolean isBlackLeftRookHasMoved;
	public boolean isBlackRightRookHasMoved;
	public boolean isWhiteLeftRookHasMoved;
	public boolean isWhiteRightRookHasMoved;
	
	// pawn promotion
	public String promotionExplicit="promotion";
	public String promotionStandard="=D";
	private String checkDescription="check";
	
	// there are all the coordinates for a standard game with the white at bottom 
	private Point leftWhiteRookInitialPosition=new Point(0,7);
	private Point rightWhiteRookInitialPosition=new Point(7,7);
	private Point leftWhiteKnightInitialPosition=new Point(1,7);
	private Point rightWhiteKnightInitialPosition=new Point(6,7);
	private Point leftWhiteBishopInitialPosition=new Point(2,7);
	private Point rightWhiteBishopInitialPosition=new Point(5,7);
	private Point whiteQueenInitialPosition=new Point(3,7);
	private Point whiteKingInitialPosition=new Point(4,7);
	private Point firstLeftWhitePawnsInitialPosition=new Point(0,6);
	private Point leftBlackRookInitialPosition=new Point(0,0);
	private Point rightBlackRookInitialPosition=new Point(7,0);
	private Point leftBlackKnightInitialPosition=new Point(1,0);
	private Point rightBlackKnightInitialPosition=new Point(6,0);
	private Point leftBlackBishopInitialPosition=new Point(2,0);
	private Point rightBlackBishopInitialPosition=new Point(5,0);
	private Point blackQueenInitialPosition=new Point(3,0);
	private Point blackKingInitialPosition=new Point(4,0);
	private Point firstLeftBlackPawnsInitialPosition=new Point(0,1);
	
	// display a BitSet, useful for debugging
	public void displayBitSet(BitSet bitSet)
	{
		if(bitSet==null)
		{
			System.out.println("Error in displayBitSet bitSet is null");
			return;
		}
		System.out.println("------------------------------");
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			{
				if(bitSet.get(counterVertical*numberOfSquarePerLine+counterHorizontal)==true)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.print("\n");
		}
	}
	
	// in the constructor we put each of the pieces at the right place
	public ChessRuler()
	{
		// initiate castling bitboards
		blackQueenCastlingMask=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		for(int counterIndexCastling=beginBlackQueenCastling;counterIndexCastling<=endBlackQueenCastling;counterIndexCastling++)
			blackQueenCastlingMask.set(counterIndexCastling);
		blackKingCastlingMask=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		for(int counterIndexCastling=beginBlackKingCastling;counterIndexCastling<=endBlackKingCastling;counterIndexCastling++)
			blackKingCastlingMask.set(counterIndexCastling);
		whiteQueenCastlingMask=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		for(int counterIndexCastling=beginWhiteQueenCastling;counterIndexCastling<=endWhiteQueenCastling;counterIndexCastling++)
			whiteQueenCastlingMask.set(counterIndexCastling);
		whiteKingCastlingMask=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		for(int counterIndexCastling=beginWhiteKingCastling;counterIndexCastling<=endWhiteKingCastling;counterIndexCastling++)
			whiteKingCastlingMask.set(counterIndexCastling);
		
		// to save right position in recursive 
		arrayBestSource=new int[maximumPossibleMoves];
		arrayBestDestination=new int[maximumPossibleMoves];
		arrayArraysTypes=new int[deepestLevel][];
		arrayArraysSources=new int[deepestLevel][];
		arrayArraysDestination=new int[deepestLevel][];
		for(int counterDepth=0;counterDepth<deepestLevel;counterDepth++)
		{
			arrayArraysTypes[counterDepth]=new int[maximumPossibleMoves];
			arrayArraysSources[counterDepth]=new int[maximumPossibleMoves];
			arrayArraysDestination[counterDepth]=new int[maximumPossibleMoves];
		}
		HashMapKingMoves=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		HashMapKnightMoves=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		HashMapLinesMasks=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		HashMapLinesPossibilities=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		HashMapDiagonalsMasks=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		HashMapDiagonalsPossibilities=new HashMap<BitSet,BitSet>(numberOfSquarePerLine*numberOfSquarePerLine);
		totalCounterEvaluation=0;
		arrayDiagonalsExtremes=new BitSet[numberOfSquarePerLine*numberOfSquarePerLine];
		BitSet bitSetSource=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		BitSet bitSetDestinations=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		int counterBits;
		for(counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
		{
			bitSetDestinations.clear();
			int horizontalPosition=counterBits%numberOfSquarePerLine;
			int verticalPosition=counterBits/numberOfSquarePerLine;
			
			// we have to compute the top-left and bottom-left coordinates to set the four extremes
			int topLeftHorizontalInsertion=horizontalPosition;
			int topLeftVerticalInsertion=verticalPosition;
			for(;topLeftVerticalInsertion>0&&topLeftHorizontalInsertion>0;topLeftHorizontalInsertion--,topLeftVerticalInsertion--)
				;
			bitSetDestinations.set(topLeftVerticalInsertion*numberOfSquarePerLine+topLeftHorizontalInsertion);
			bitSetDestinations.set((numberOfSquarePerLine-topLeftHorizontalInsertion-1)*numberOfSquarePerLine+(numberOfSquarePerLine-topLeftVerticalInsertion-1));
			int bottomLeftHorizontalInsertion=horizontalPosition;
			int bottomLeftVerticalInsertion=verticalPosition;
			for(;bottomLeftHorizontalInsertion>0&&bottomLeftVerticalInsertion<numberOfSquarePerLine-1;bottomLeftVerticalInsertion++,bottomLeftHorizontalInsertion--)
				;
			bitSetDestinations.set(bottomLeftVerticalInsertion*numberOfSquarePerLine+bottomLeftHorizontalInsertion);
			bitSetDestinations.set(bottomLeftHorizontalInsertion*numberOfSquarePerLine+bottomLeftVerticalInsertion);
			arrayDiagonalsExtremes[counterBits]=(BitSet)bitSetDestinations.clone();
			
			// compute all the knight moves
			bitSetSource.clear();
			bitSetDestinations.clear();
			bitSetSource.set(counterBits);
			
			// first left-top and second bottom-right
			if(counterBits%numberOfSquarePerLine>1)
			{
				if(counterBits/numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits-numberOfSquarePerLine-2);
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits+numberOfSquarePerLine-2);
			}
			
			// second left-bottom and first top-right
			if(counterBits/numberOfSquarePerLine>1)
			{
				if(counterBits%numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits-2*numberOfSquarePerLine-1);
				if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits-2*numberOfSquarePerLine+1);
			}
			
			// second right-top and first bottom-right
			if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-2)
			{
				if(counterBits/numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits-numberOfSquarePerLine+2);
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits+numberOfSquarePerLine+2);
			}
			
			// first left-bottom and second bottom-right
			if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-2)
			{
				if(counterBits%numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits+2*numberOfSquarePerLine-1);
				if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits+2*numberOfSquarePerLine+1);
			}
			
			// we put it in the knight moves HashMap
			HashMapKnightMoves.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
			
			// we have to compute king moves now
			bitSetDestinations.clear();
			
			// top-middle
			if(counterBits/numberOfSquarePerLine>0)
				bitSetDestinations.set(counterBits-numberOfSquarePerLine);
			
			// bottom-middle
			if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
				bitSetDestinations.set(counterBits+numberOfSquarePerLine);
			
			// left : top, middle and bottom
			if(counterBits%numberOfSquarePerLine>0)
			{
				bitSetDestinations.set(counterBits-1);
				if(counterBits/numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits-numberOfSquarePerLine-1);
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits+numberOfSquarePerLine-1);
			}
			
			// right : top, middle and bottom
			if((counterBits+1)%numberOfSquarePerLine!=0)
			{
				bitSetDestinations.set(counterBits+1);
				if(counterBits/numberOfSquarePerLine>0)
					bitSetDestinations.set(counterBits-numberOfSquarePerLine+1);
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					bitSetDestinations.set(counterBits+numberOfSquarePerLine+1);
			}
			
			// we put it in the king moves HashMap
			HashMapKingMoves.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
			
			// compute the line moves, for rooks and queens
			bitSetDestinations.clear();
			for(int counterBitsForLines=0;counterBitsForLines<numberOfSquarePerLine-2;counterBitsForLines++)
			{
				bitSetDestinations.set(horizontalPosition+(counterBitsForLines+1)*numberOfSquarePerLine);
				bitSetDestinations.set(counterBitsForLines+verticalPosition*numberOfSquarePerLine+1);
			}
			HashMapLinesMasks.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
			
			// now we set moves possibilities, we compute all the possibilities for vertical lines, on 6 bits that is 64 values
			// no need to run 256 values because the pieces can always go to the extremes and 256*256*65 is too much 
			bitSetDestinations.clear();
			for(int counterVerticalPossibilities=0;counterVerticalPossibilities<Math.pow(2,6);counterVerticalPossibilities++)
			{
				// first of all we convert the vertical numeric value into a bitset
				BitSet bitSetVerticalPossibilites=new BitSet();
				int counterVerticalPossibilitiesTemporary=counterVerticalPossibilities;
				int indexVerticalPossibilites=0;
				while(counterVerticalPossibilitiesTemporary!=0)
				{
					if(counterVerticalPossibilitiesTemporary%2!=0)
						bitSetVerticalPossibilites.set(indexVerticalPossibilites);
					++indexVerticalPossibilites;
					counterVerticalPossibilitiesTemporary=counterVerticalPossibilitiesTemporary>>>1;
				}
				
				// we compute all the possibilities for horizontal lines
				for(int counterHorizontalPossibilities=0;counterHorizontalPossibilities<Math.pow(2,6);counterHorizontalPossibilities++)
				{
					// we convert the horizontal numeric value into a bitset
					BitSet bitSetHorizontalPossibilites=new BitSet();
					int counterHorizontalPossibilitiesTemporary=counterHorizontalPossibilities;
					int indexHorizontalPossibilites=0;
					while(counterHorizontalPossibilitiesTemporary!=0)
					{
						if(counterHorizontalPossibilitiesTemporary%2!=0)
							bitSetHorizontalPossibilites.set(indexHorizontalPossibilites);
						++indexHorizontalPossibilites;
						counterHorizontalPossibilitiesTemporary=counterHorizontalPossibilitiesTemporary>>>1;
					}
					
					// we configure the source as the sum of the horizontal and vertical moves
					bitSetSource.clear();
					for(int counterBitsForLines=0;counterBitsForLines<numberOfSquarePerLine-2;counterBitsForLines++)
					{
						if(bitSetHorizontalPossibilites.get(counterBitsForLines)==true)
							bitSetSource.set(verticalPosition*numberOfSquarePerLine+counterBitsForLines+1);
						if(bitSetVerticalPossibilites.get(counterBitsForLines)==true)
							bitSetSource.set(horizontalPosition+(counterBitsForLines+1)*numberOfSquarePerLine);
					}
					
					// we put all the extreme bits in order to know all the directions
					bitSetSource.set(verticalPosition*numberOfSquarePerLine);
					bitSetSource.set(verticalPosition*numberOfSquarePerLine+numberOfSquarePerLine-1);
					bitSetSource.set(horizontalPosition);
					bitSetSource.set(horizontalPosition+numberOfSquarePerLine*(numberOfSquarePerLine-1));
					
					// left moves
					bitSetDestinations.clear();
					for(int counterLeftMoves=1;counterLeftMoves<=horizontalPosition;counterLeftMoves++)
					{
						if(bitSetSource.get(verticalPosition*numberOfSquarePerLine+horizontalPosition-counterLeftMoves)==true)
						{
							bitSetDestinations.set(verticalPosition*numberOfSquarePerLine+horizontalPosition-counterLeftMoves);
							break;
						}
						else
							bitSetDestinations.set(verticalPosition*numberOfSquarePerLine+horizontalPosition-counterLeftMoves);
					}
					
					// top moves
					for(int counterTopMoves=1;counterTopMoves<=verticalPosition;counterTopMoves++)
					{
						if(bitSetSource.get((verticalPosition-counterTopMoves)*numberOfSquarePerLine+horizontalPosition)==true)
						{
							bitSetDestinations.set((verticalPosition-counterTopMoves)*numberOfSquarePerLine+horizontalPosition);
							break;
						}
						else
							bitSetDestinations.set((verticalPosition-counterTopMoves)*numberOfSquarePerLine+horizontalPosition);
					}
					
					// right moves
					for(int counterRightMoves=1;counterRightMoves<(numberOfSquarePerLine-horizontalPosition);counterRightMoves++)
					{
						if(bitSetSource.get(verticalPosition*numberOfSquarePerLine+horizontalPosition+counterRightMoves)==true)
						{
							bitSetDestinations.set(verticalPosition*numberOfSquarePerLine+horizontalPosition+counterRightMoves);
							break;
						}
						else
							bitSetDestinations.set(verticalPosition*numberOfSquarePerLine+horizontalPosition+counterRightMoves);
					}
					
					// bottom moves
					for(int counterBottomMoves=1;counterBottomMoves<(numberOfSquarePerLine-verticalPosition);counterBottomMoves++)
					{
						if(bitSetSource.get((verticalPosition+counterBottomMoves)*numberOfSquarePerLine+horizontalPosition)==true)
						{
							bitSetDestinations.set((verticalPosition+counterBottomMoves)*numberOfSquarePerLine+horizontalPosition);
							break;
						}
						else
							bitSetDestinations.set((verticalPosition+counterBottomMoves)*numberOfSquarePerLine+horizontalPosition);
					}
					
					// now we can push the result link into board status and moves destinations
					HashMapLinesPossibilities.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
				}
			}
			
			// now we compute bishops and queens moves
			bitSetSource.clear();
			bitSetDestinations.clear();
			bitSetSource.set(counterBits);
			
			// from top-left to bottom-right 
			int leftTopHorizontalInsertion=horizontalPosition;
			int leftTopVerticalInsertion=verticalPosition;
			for(;leftTopHorizontalInsertion>0&&leftTopVerticalInsertion>0;leftTopHorizontalInsertion--,leftTopVerticalInsertion--)
				;
			int counterVertical=leftTopVerticalInsertion;
			for(int counterHorizontal=leftTopHorizontalInsertion;counterHorizontal<numberOfSquarePerLine&&counterVertical<numberOfSquarePerLine;counterHorizontal++,counterVertical++)
				bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
			
			// from bottom-left to top-right
			int leftBottomHorizontalInsertion=horizontalPosition;
			int leftBottomVerticalInsertion=verticalPosition;
			for(;leftBottomHorizontalInsertion>0&&leftBottomVerticalInsertion<numberOfSquarePerLine-1;leftBottomHorizontalInsertion--,leftBottomVerticalInsertion++)
				;
			counterVertical=leftBottomVerticalInsertion;
			for(int counterHorizontal=leftBottomHorizontalInsertion;counterHorizontal<numberOfSquarePerLine&&counterVertical>=0;counterHorizontal++,counterVertical--)
				bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
			
			// we can now put into hashmap to make link between piece coordinate and mask to get pieces
			HashMapDiagonalsMasks.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
			
			// now we have to create the second hashmap with
			int numberOfBitsOnTheTopLeftDiagonal=Math.min(numberOfSquarePerLine-leftTopHorizontalInsertion,numberOfSquarePerLine-leftTopVerticalInsertion)-2;
			int numberOfPossibilitiesOnTopLeftDiagonal=1;
			if(numberOfBitsOnTheTopLeftDiagonal>=1)
				numberOfPossibilitiesOnTopLeftDiagonal=(int)Math.pow(2,numberOfBitsOnTheTopLeftDiagonal);
			for(int counterLeftTopDiagonalPossibilities=0;counterLeftTopDiagonalPossibilities<numberOfPossibilitiesOnTopLeftDiagonal;counterLeftTopDiagonalPossibilities++)
			{
				BitSet bitSetTopLeftSource=new BitSet();
				BitSet bitSetLeftTopDiagonalPossibilites=new BitSet();
				int counterLeftTopDiagonalPossibilitiesTemporary=counterLeftTopDiagonalPossibilities;
				int indexLeftTopDiagonalPossibilites=0;
				while(counterLeftTopDiagonalPossibilitiesTemporary!=0)
				{
					if(counterLeftTopDiagonalPossibilitiesTemporary%2!=0)
						bitSetLeftTopDiagonalPossibilites.set(indexLeftTopDiagonalPossibilites);
					++indexLeftTopDiagonalPossibilites;
					counterLeftTopDiagonalPossibilitiesTemporary=counterLeftTopDiagonalPossibilitiesTemporary>>>1;
				}
				
				// we set the source with the right possibilities
				int counterBitsMovesPossibilities=0;
				counterVertical=leftTopVerticalInsertion+1;
				int counterHorizontal=leftTopHorizontalInsertion+1;
				for(;counterHorizontal<numberOfSquarePerLine-1&&counterVertical<numberOfSquarePerLine-1;counterHorizontal++,counterVertical++,counterBitsMovesPossibilities++)
					if(bitSetLeftTopDiagonalPossibilites.get(counterBitsMovesPossibilities)==true)
						bitSetTopLeftSource.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
					
				// we put the extremes positions to the current diagonal
				bitSetTopLeftSource.set(leftTopVerticalInsertion*numberOfSquarePerLine+leftTopHorizontalInsertion);
				bitSetTopLeftSource.set((numberOfSquarePerLine-leftTopHorizontalInsertion-1)*numberOfSquarePerLine+(numberOfSquarePerLine-leftTopVerticalInsertion-1));
				
				// now for each of this top left diagonals we have to compute all the top bottom diagonals possibilities
				int numberOfBitsOnTheBottomLeftDiagonal=Math.min((numberOfSquarePerLine-1)-(leftBottomHorizontalInsertion),leftBottomVerticalInsertion)-1;
				int numberOfPossibilitiesOnBottomLeftDiagonal=1;
				if(numberOfBitsOnTheBottomLeftDiagonal>=1)
					numberOfPossibilitiesOnBottomLeftDiagonal=(int)Math.pow(2,numberOfBitsOnTheBottomLeftDiagonal);
				for(int counterLeftBottomDiagonalPossibilities=0;counterLeftBottomDiagonalPossibilities<numberOfPossibilitiesOnBottomLeftDiagonal;counterLeftBottomDiagonalPossibilities++)
				{
					bitSetSource.clear();
					BitSet bitSetBottomLeftSource=new BitSet();
					BitSet bitSetLeftBottomDiagonalPossibilites=new BitSet();
					int counterLeftBottomDiagonalPossibilitiesTemporary=counterLeftBottomDiagonalPossibilities;
					int indexLeftBottomDiagonalPossibilites=0;
					while(counterLeftBottomDiagonalPossibilitiesTemporary!=0)
					{
						if(counterLeftBottomDiagonalPossibilitiesTemporary%2!=0)
							bitSetLeftBottomDiagonalPossibilites.set(indexLeftBottomDiagonalPossibilites);
						++indexLeftBottomDiagonalPossibilites;
						counterLeftBottomDiagonalPossibilitiesTemporary=counterLeftBottomDiagonalPossibilitiesTemporary>>>1;
					}
					
					// we set the source with the right possibilities
					int counterBitsMovesBottomLeftPossibilities=0;
					counterVertical=leftBottomVerticalInsertion-1;
					counterHorizontal=leftBottomHorizontalInsertion+1;
					
					for(;counterHorizontal<numberOfSquarePerLine&&counterVertical>0;counterHorizontal++,counterVertical--,counterBitsMovesBottomLeftPossibilities++)
						if(bitSetLeftBottomDiagonalPossibilites.get(counterBitsMovesBottomLeftPossibilities)==true)
							bitSetBottomLeftSource.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
						
					// we put the extremes positions to the current diagonal
					bitSetBottomLeftSource.set((leftBottomVerticalInsertion)*numberOfSquarePerLine+leftBottomHorizontalInsertion);
					bitSetBottomLeftSource.set((leftBottomHorizontalInsertion)*numberOfSquarePerLine+leftBottomVerticalInsertion);
					
					// we configure the source as the sum of the horizontal and vertical moves
					bitSetSource.or(bitSetBottomLeftSource);
					bitSetSource.or(bitSetTopLeftSource);
					
					// delete the position corner to avoid ambiguous source coordinates
					if(counterBits==0||counterBits==numberOfSquarePerLine-1||counterBits==numberOfSquarePerLine*(numberOfSquarePerLine-1)||counterBits==numberOfSquarePerLine*numberOfSquarePerLine-1)
						bitSetSource.clear(counterBits);
					
					// we now put the moves into top-left direction
					counterVertical=verticalPosition-1;
					counterHorizontal=horizontalPosition-1;
					bitSetDestinations.clear();
					for(;counterVertical>=0&&counterHorizontal>=0;counterHorizontal--,counterVertical--)
					{
						if(bitSetSource.get(counterHorizontal+counterVertical*numberOfSquarePerLine)==true)
						{
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
							break;
						}
						else
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
					}
					
					// top-right
					counterVertical=verticalPosition-1;
					counterHorizontal=horizontalPosition+1;
					for(;counterVertical>=0&&counterHorizontal<numberOfSquarePerLine;counterHorizontal++,counterVertical--)
					{
						if(bitSetSource.get(counterHorizontal+counterVertical*numberOfSquarePerLine)==true)
						{
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
							break;
						}
						else
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
					}
					
					// bottom-right
					counterVertical=verticalPosition+1;
					counterHorizontal=horizontalPosition+1;
					for(;counterVertical<numberOfSquarePerLine&&counterHorizontal<numberOfSquarePerLine;counterHorizontal++,counterVertical++)
					{
						if(bitSetSource.get(counterHorizontal+counterVertical*numberOfSquarePerLine)==true)
						{
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
							break;
						}
						else
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
					}
					
					// bottom-left
					counterVertical=verticalPosition+1;
					counterHorizontal=horizontalPosition-1;
					for(;counterVertical<numberOfSquarePerLine&&counterHorizontal>=0;counterHorizontal--,counterVertical++)
					{
						if(bitSetSource.get(counterHorizontal+counterVertical*numberOfSquarePerLine)==true)
						{
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
							break;
						}
						else
							bitSetDestinations.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
					}
					
					// we can now filled the hashmap for all the diagonals possibilities
					HashMapDiagonalsPossibilities.put((BitSet)bitSetSource.clone(),(BitSet)bitSetDestinations.clone());
				}
			}
		}
		
		HashMapLinesAndDiagonalesForCheck=new HashMap<BitSet,BitSet>();
		for(int counterIndexKing=0;counterIndexKing<numberOfSquarePerLine*numberOfSquarePerLine;counterIndexKing++)
		{
			int kingVerticalPosition=counterIndexKing/numberOfSquarePerLine;
			int kingHorizontalPosition=counterIndexKing%numberOfSquarePerLine;
			for(int counterIndexSlidingPiece=0;counterIndexSlidingPiece<numberOfSquarePerLine*numberOfSquarePerLine;counterIndexSlidingPiece++)
			{
				int attackerVerticalPosition=counterIndexSlidingPiece/numberOfSquarePerLine;
				int attackerHorizontalPosition=counterIndexSlidingPiece%numberOfSquarePerLine;
				if(counterIndexSlidingPiece==counterIndexKing)
					continue;
				
				// we begin with lines movement
				BitSet bitSetIndexAttacker=new BitSet();
				bitSetIndexAttacker.set(counterIndexSlidingPiece);
				bitSetIndexAttacker.set(attackerVerticalPosition*numberOfSquarePerLine);
				bitSetIndexAttacker.set(attackerVerticalPosition*numberOfSquarePerLine+numberOfSquarePerLine-1);
				bitSetIndexAttacker.set(attackerHorizontalPosition);
				bitSetIndexAttacker.set(attackerHorizontalPosition+numberOfSquarePerLine*(numberOfSquarePerLine-1));
				BitSet bitSetAttackLine=(BitSet)HashMapLinesPossibilities.get(bitSetIndexAttacker).clone();
				BitSet bitSetIndexKing=new BitSet();
				bitSetIndexKing.set(counterIndexKing);
				BitSet bitSetAttackLineClone=(BitSet)bitSetAttackLine.clone();
				bitSetAttackLineClone.and(bitSetIndexKing);
				if(bitSetAttackLineClone.cardinality()!=0)
				{
					// vertical lines
					if(attackerVerticalPosition<kingVerticalPosition)
					{
						BitSet maskBitSet=new BitSet();
						for(int counterBitsToBeSet=(attackerVerticalPosition+1)*numberOfSquarePerLine;counterBitsToBeSet<counterIndexKing;counterBitsToBeSet++)
							maskBitSet.set(counterBitsToBeSet);
						bitSetAttackLine.and(maskBitSet);
						bitSetAttackLine.set(counterIndexSlidingPiece);
						bitSetAttackLine.set(counterIndexKing);
						BitSet kingAndAttacker=new BitSet();
						kingAndAttacker.set(counterIndexKing);
						kingAndAttacker.set(counterIndexSlidingPiece);
						HashMapLinesAndDiagonalesForCheck.put(kingAndAttacker,bitSetAttackLine);
					}
					
					// horizontal lines
					if(kingHorizontalPosition<attackerHorizontalPosition)
					{
						BitSet maskBitSet=new BitSet();
						for(int counterBitsToBeSet=counterIndexKing;counterBitsToBeSet<counterIndexSlidingPiece;counterBitsToBeSet++)
							maskBitSet.set(counterBitsToBeSet);
						bitSetAttackLine.and(maskBitSet);
						bitSetAttackLine.set(counterIndexSlidingPiece);
						bitSetAttackLine.set(counterIndexKing);
						BitSet kingAndAttacker=new BitSet();
						kingAndAttacker.set(counterIndexKing);
						kingAndAttacker.set(counterIndexSlidingPiece);
						HashMapLinesAndDiagonalesForCheck.put(kingAndAttacker,bitSetAttackLine);
					}
				}
				bitSetIndexAttacker.clear();
				bitSetIndexAttacker.or(arrayDiagonalsExtremes[counterIndexSlidingPiece]);
				if(counterIndexSlidingPiece==0||counterIndexSlidingPiece==numberOfSquarePerLine-1||counterIndexSlidingPiece==numberOfSquarePerLine*(numberOfSquarePerLine-1)||counterIndexSlidingPiece==numberOfSquarePerLine*numberOfSquarePerLine-1)
					bitSetIndexAttacker.clear(counterIndexSlidingPiece);
				bitSetAttackLine=(BitSet)HashMapDiagonalsPossibilities.get(bitSetIndexAttacker).clone();
				bitSetAttackLineClone=(BitSet)bitSetAttackLine.clone();
				bitSetAttackLineClone.and(bitSetIndexKing);
				
				if(bitSetAttackLineClone.cardinality()!=0)
				{
					// diagonal from a8 to h1, attacker at top
					if(attackerVerticalPosition<kingVerticalPosition&&kingHorizontalPosition>attackerHorizontalPosition)
					{
						BitSet maskBitSet=new BitSet();
						int counterHorizontal=attackerHorizontalPosition;
						for(int counterVertical=attackerVerticalPosition;counterVertical<kingVerticalPosition&&counterHorizontal<kingHorizontalPosition;counterVertical++,counterHorizontal++)
							maskBitSet.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
						bitSetAttackLine.and(maskBitSet);
						bitSetAttackLine.set(counterIndexSlidingPiece);
						bitSetAttackLine.set(counterIndexKing);
						BitSet kingAndAttacker=new BitSet();
						kingAndAttacker.set(counterIndexKing);
						kingAndAttacker.set(counterIndexSlidingPiece);
						HashMapLinesAndDiagonalesForCheck.put(kingAndAttacker,bitSetAttackLine);
					}
					
					// diagonal from a1 to h8, attacker at top
					if(attackerVerticalPosition<kingVerticalPosition&&kingHorizontalPosition<attackerHorizontalPosition)
					{
						BitSet maskBitSet=new BitSet();
						int counterHorizontal=kingHorizontalPosition;
						for(int counterVertical=kingVerticalPosition;counterVertical>attackerVerticalPosition&&counterHorizontal<attackerHorizontalPosition;counterVertical--,counterHorizontal++)
							maskBitSet.set(counterHorizontal+counterVertical*numberOfSquarePerLine);
						bitSetAttackLine.and(maskBitSet);
						bitSetAttackLine.set(counterIndexSlidingPiece);
						bitSetAttackLine.set(counterIndexKing);
						BitSet kingAndAttacker=new BitSet();
						kingAndAttacker.set(counterIndexKing);
						kingAndAttacker.set(counterIndexSlidingPiece);
						HashMapLinesAndDiagonalesForCheck.put(kingAndAttacker,bitSetAttackLine);
					}
				}
			}
		}
		InitializeNewGame();
	}
	
	public int getCurrentTurn()
	{
		return currentTurn;
	}
	
	// a or of all white pieces
	public BitSet GetAllWhitePieces()
	{
		BitSet allWhitePieces=new BitSet();
		allWhitePieces.or(whiteRooks);
		allWhitePieces.or(whiteKnights);
		allWhitePieces.or(whiteBishops);
		allWhitePieces.or(whiteQueen);
		allWhitePieces.or(whiteKing);
		allWhitePieces.or(whitePawns);
		return allWhitePieces;
	}
	
	// a or of all black pieces
	public BitSet GetAllBlackPieces()
	{
		BitSet allBlackPieces=new BitSet();
		allBlackPieces.or(blackRooks);
		allBlackPieces.or(blackKnights);
		allBlackPieces.or(blackBishops);
		allBlackPieces.or(blackQueen);
		allBlackPieces.or(blackKing);
		allBlackPieces.or(blackPawns);
		return allBlackPieces;
	}
	
	public int GiveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if(GetAllWhitePieces().get(pointCoordinates.x+pointCoordinates.y*numberOfSquarePerLine)==true)
			return white;
		if(GetAllBlackPieces().get(pointCoordinates.x+pointCoordinates.y*numberOfSquarePerLine)==true)
			return black;
		return 0;
	}
	
	// we set all the pieces with their right position
	public void InitializeNewGame()
	{
		listPiecesSituation=new ArrayList<PiecesSituation>();
		listPiecesSituationOccurrences=new ArrayList<Integer>();
		totalCounterEvaluation=0;
		counterMoveFinished=0;
		currentTurn=white;
		whiteRooks=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whiteKnights=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whiteBishops=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whiteQueen=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whiteKing=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whitePawns=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		whiteRooks.set(leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine);
		whiteRooks.set(rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine);
		whiteKnights.set(leftWhiteKnightInitialPosition.x+leftWhiteKnightInitialPosition.y*numberOfSquarePerLine);
		whiteKnights.set(rightWhiteKnightInitialPosition.x+rightWhiteKnightInitialPosition.y*numberOfSquarePerLine);
		whiteBishops.set(leftWhiteBishopInitialPosition.x+leftWhiteBishopInitialPosition.y*numberOfSquarePerLine);
		whiteBishops.set(rightWhiteBishopInitialPosition.x+rightWhiteBishopInitialPosition.y*numberOfSquarePerLine);
		whiteQueen.set(whiteQueenInitialPosition.x+whiteQueenInitialPosition.y*numberOfSquarePerLine);
		whiteKing.set(whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine);
		for(int counterPawns=0;counterPawns<numberOfSquarePerLine;counterPawns++)
			whitePawns.set(firstLeftWhitePawnsInitialPosition.x+counterPawns+firstLeftWhitePawnsInitialPosition.y*numberOfSquarePerLine);
		blackRooks=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackKnights=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackBishops=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackQueen=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackKing=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackPawns=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		blackRooks.set(leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine);
		blackRooks.set(rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine);
		blackKnights.set(leftBlackKnightInitialPosition.x+leftBlackKnightInitialPosition.y*numberOfSquarePerLine);
		blackKnights.set(rightBlackKnightInitialPosition.x+rightBlackKnightInitialPosition.y*numberOfSquarePerLine);
		blackBishops.set(leftBlackBishopInitialPosition.x+leftBlackBishopInitialPosition.y*numberOfSquarePerLine);
		blackBishops.set(rightBlackBishopInitialPosition.x+rightBlackBishopInitialPosition.y*numberOfSquarePerLine);
		blackQueen.set(blackQueenInitialPosition.x+blackQueenInitialPosition.y*numberOfSquarePerLine);
		blackKing.set(blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine);
		for(int counterPawns=0;counterPawns<numberOfSquarePerLine;counterPawns++)
			blackPawns.set(firstLeftBlackPawnsInitialPosition.x+counterPawns+firstLeftBlackPawnsInitialPosition.y*numberOfSquarePerLine);
		
		// for castling
		isWhiteKingHasMoved=false;
		isBlackKingHasMoved=false;
		isBlackLeftRookHasMoved=false;
		isBlackRightRookHasMoved=false;
		isWhiteLeftRookHasMoved=false;
		isWhiteRightRookHasMoved=false;
		
		// save the current situation
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueen,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueen,blackKing,blackPawns,blackRooks);
		listPiecesSituation.add(piecesSituation);
		listPiecesSituationOccurrences.add(1);
		
		// because it's a new game, we have to initialize begining date
		Calendar calendar=Calendar.getInstance();
		long beginningTime=calendar.getTimeInMillis();
		gameBeginningDate=new Date(beginningTime);
	}
	
	public Date GetBeginningDate()
	{
		return gameBeginningDate;
	}
	
	// transform a coordinate square into a string with algebraic notation 
	private String GetSquareInString(Point coordinates)
	{
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		String result="";
		result+=heightFirstLettersOfTheAlphabet[coordinates.x];
		result+=numberOfSquarePerLine-coordinates.y;
		return result;
	}
	
	private void DeleteBlackPieceAtThisSquare(Point squareCoordinates)
	{
		blackKnights.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		blackKing.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		blackPawns.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		blackRooks.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		blackBishops.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		blackQueen.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
	}
	
	private void DeleteWhitePieceAtThisSquare(Point squareCoordinates)
	{
		whiteKnights.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		whitePawns.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		whiteKing.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		whiteRooks.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		whiteBishops.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
		whiteQueen.clear(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine);
	}
	
	// get the name of a piece, useful to understand what happens
	private String GetNamePieceAtThisSquare(Point squareCoordinates)
	{
		if(whiteKing.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackKing.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "king";
		else if(whiteKnights.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackKnights.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "knight";
		else if(whitePawns.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackPawns.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "pawn";
		else if(whiteRooks.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackRooks.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "rook";
		else if(whiteBishops.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackBishops.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "bishop";
		else if(whiteQueen.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)||blackQueen.get(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))
			return "queen";
		return "";
	}
	
	// make the move without description and delete the piece if necessary
	public boolean doMove(Point sourceSquare,Point destinationSquare)
	{
		int sourceOffset=sourceSquare.x+sourceSquare.y*numberOfSquarePerLine;
		int destinationOffset=destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
		
		if(GiveMeThePieceColorOnThisSquare(sourceSquare)==black)
		{
			if(blackPawns.get(sourceOffset)==true)
			{
				blackPawns.clear(sourceOffset);
				if(destinationOffset>(numberOfSquarePerLine*(numberOfSquarePerLine-1)-1))
				{
					blackQueen.set(destinationOffset);
					if(GetAllWhitePieces().get(destinationOffset)==true)
						DeleteWhitePieceAtThisSquare(destinationSquare);
					return true;
				}
				blackPawns.set(destinationOffset);
			}
			else if(blackKnights.get(sourceOffset)==true)
			{
				blackKnights.clear(sourceOffset);
				blackKnights.set(destinationOffset);
			}
			else if(blackKing.get(sourceOffset)==true)
			{
				blackKing.clear(sourceOffset);
				blackKing.set(destinationOffset);
				if(isBlackKingHasMoved==false)
				{
					isBlackKingHasMoved=true;
					if(GetAllWhitePieces().get(destinationOffset)==true)
						DeleteWhitePieceAtThisSquare(destinationSquare);
					return true;
				}
			}
			else if(blackRooks.get(sourceOffset)==true)
			{
				blackRooks.clear(sourceOffset);
				blackRooks.set(destinationOffset);
				if(leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine==sourceOffset&&isBlackLeftRookHasMoved==false)
				{
					isBlackLeftRookHasMoved=true;
					if(GetAllWhitePieces().get(destinationOffset)==true)
						DeleteWhitePieceAtThisSquare(destinationSquare);
					return true;
				}
				if(rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine==sourceOffset&&isBlackRightRookHasMoved==false)
				{
					isBlackRightRookHasMoved=true;
					if(GetAllWhitePieces().get(destinationOffset)==true)
						DeleteWhitePieceAtThisSquare(destinationSquare);
					return true;
				}
			}
			else if(blackBishops.get(sourceOffset)==true)
			{
				blackBishops.clear(sourceOffset);
				blackBishops.set(destinationOffset);
			}
			else if(blackQueen.get(sourceOffset)==true)
			{
				blackQueen.clear(sourceOffset);
				blackQueen.set(destinationOffset);
			}
			if(GetAllWhitePieces().get(destinationOffset)==true)
				DeleteWhitePieceAtThisSquare(destinationSquare);
		}
		else if(GiveMeThePieceColorOnThisSquare(sourceSquare)==white)
		{
			if(whitePawns.get(sourceOffset)==true)
			{
				whitePawns.clear(sourceOffset);
				if(destinationOffset<numberOfSquarePerLine)
				{
					whiteQueen.set(destinationOffset);
					if(GetAllBlackPieces().get(destinationOffset)==true)
						DeleteBlackPieceAtThisSquare(destinationSquare);
					return true;
				}
				else
					whitePawns.set(destinationOffset);
			}
			else if(whiteKnights.get(sourceOffset)==true)
			{
				whiteKnights.clear(sourceOffset);
				whiteKnights.set(destinationOffset);
			}
			else if(whiteKing.get(sourceOffset)==true)
			{
				whiteKing.clear(sourceOffset);
				whiteKing.set(destinationOffset);
				if(isWhiteKingHasMoved==false)
				{
					isWhiteKingHasMoved=true;
					if(GetAllBlackPieces().get(destinationOffset)==true)
						DeleteBlackPieceAtThisSquare(destinationSquare);
					return true;
				}
			}
			else if(whiteRooks.get(sourceOffset)==true)
			{
				whiteRooks.clear(sourceOffset);
				whiteRooks.set(destinationOffset);
				if(leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine==sourceOffset&&isWhiteLeftRookHasMoved==false)
				{
					isWhiteLeftRookHasMoved=true;
					if(GetAllBlackPieces().get(destinationOffset)==true)
						DeleteBlackPieceAtThisSquare(destinationSquare);
					return true;
				}
				if(rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine==sourceOffset&&isWhiteRightRookHasMoved==false)
				{
					isWhiteRightRookHasMoved=true;
					if(GetAllBlackPieces().get(destinationOffset)==true)
						DeleteBlackPieceAtThisSquare(destinationSquare);
					return true;
				}
			}
			else if(whiteBishops.get(sourceOffset)==true)
			{
				whiteBishops.clear(sourceOffset);
				whiteBishops.set(destinationOffset);
			}
			else if(whiteQueen.get(sourceOffset)==true)
			{
				whiteQueen.clear(sourceOffset);
				whiteQueen.set(destinationOffset);
			}
			if(GetAllBlackPieces().get(destinationOffset)==true)
				DeleteBlackPieceAtThisSquare(destinationSquare);
		}
		return false;
	}
	
	// determine if the move is ambiguous or if a piece has been eaten and give the standard description of the move
	private String TransformExplicitMoveDescriptionIntoStandardMoveDescription(String explicitMoveDescription,boolean isItAmbiguousMove)
	{
		String standardMoveDescription=explicitMoveDescription;
		int indexDot=standardMoveDescription.indexOf(".");
		int indexPawn=explicitMoveDescription.indexOf("pawn");
		if(indexPawn>indexDot+3) // this is useful to know if its a pawn who moved or if it's a pawn eaten
			indexPawn=-1;
		standardMoveDescription=standardMoveDescription.replaceAll("pawn ","");
		standardMoveDescription=standardMoveDescription.replaceAll("knight ","N");
		standardMoveDescription=standardMoveDescription.replaceAll("queen ","Q");
		standardMoveDescription=standardMoveDescription.replaceAll("king ","K");
		standardMoveDescription=standardMoveDescription.replaceAll("bishop ","B");
		standardMoveDescription=standardMoveDescription.replaceAll("rook ","R");
		int indexHyphen=standardMoveDescription.indexOf("-");
		if(indexPawn==-1)
		{
			if(isItAmbiguousMove==false)
			{
				if(standardMoveDescription.indexOf(kingSideCastlingExplicit)!=-1) // castling management
					standardMoveDescription=kingSideCastlingStandard;
				else if(standardMoveDescription.indexOf(queenSideCastlingExplicit)!=-1)
					standardMoveDescription=queenSideCastlingStandard;
				else
				{
					standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+3)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				}
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+5)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
			}
		}
		else
		{
			if(isItAmbiguousMove==false)
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen+1,indexHyphen+3); // we do not set any letter for the pawn case
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen-2,indexHyphen-0)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
		}
		int indexEat=explicitMoveDescription.indexOf("captures ");
		if(indexEat!=-1)
		{
			if(indexPawn==-1)
				standardMoveDescription=standardMoveDescription.substring(0,1)+"x"+standardMoveDescription.substring(1,standardMoveDescription.length());
			else
				standardMoveDescription="x"+standardMoveDescription; // we are in pawn case			
		}
		int indexCheck=explicitMoveDescription.indexOf(checkDescription);
		if(indexCheck!=-1)
			standardMoveDescription+="+";
		return standardMoveDescription;
	}
	
	// check is multiple piece with the same color and same type can go to the same square
	public Boolean IsItAmbiguous(Point oldSelectedSquare,Point newSelectedSquare,BitSet currentBitSet)
	{
		if(currentBitSet.get(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine)==true)
			for(int counterPieces=0;counterPieces<numberOfSquarePerLine*numberOfSquarePerLine;counterPieces++)
				if(currentBitSet.get(counterPieces)==true&&counterPieces!=(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine))
				{
					ArrayList<Point> arrayListPossibleMoves=GetListOfPossibleMovesForAPieceWithCheckChecking(counterPieces);
					Iterator<Point> PointIterator=arrayListPossibleMoves.iterator();
					while(PointIterator.hasNext())
					{
						Point currentPoint=PointIterator.next();
						if(newSelectedSquare.x==currentPoint.x&&newSelectedSquare.y==currentPoint.y)
							return true;
					}
				}
		return false;
	}
	
	// we make the move, with a description, this is useful for 
	public ArrayList<Point> doThisMoveAndGetDescription(Point oldSelectedSquare,Point newSelectedSquare,ArrayList<String> arrayMoveDescription,ArrayList<Boolean> arrayIsSpecial)
	{
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		arrayConcernedSquares.add(oldSelectedSquare);
		arrayConcernedSquares.add(newSelectedSquare);
		
		// before all we have to know if several pieces can move to destination or only one
		boolean isItAmbiguousMove=false;
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
		{
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whitePawns);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteKnights);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteQueen);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteBishops);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteRooks);
		}
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
		{
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackPawns);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackKnights);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackQueen);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackBishops);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackRooks);
		}
		
		// we create explicit description of the move 
		counterMoveFinished++;
		String moveDescription="";
		moveDescription+=counterMoveFinished+". ";
		
		// we check if it's a castling
		if(GetPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingKingCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==whiteKingKingCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=kingSideCastlingExplicit;
			doMoveAccordingToColorAndType(white,rookId,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookKingCastlingDestination);
			arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
			arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
		}
		else if(GetPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingQueenCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==whiteKingQueenCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=queenSideCastlingExplicit;
			doMoveAccordingToColorAndType(white,rookId,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookQueenCastlingDestination);
			arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
			arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
		}
		else if(GetPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingKingCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==blackKingKingCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=kingSideCastlingExplicit;
			doMoveAccordingToColorAndType(black,rookId,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookKingCastlingDestination);
			arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
			arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
		}
		else if(GetPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingQueenCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==blackKingQueenCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=queenSideCastlingExplicit;
			doMoveAccordingToColorAndType(black,rookId,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookQueenCastlingDestination);
			arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
			arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
		}
		else
		{
			moveDescription+=GetNamePieceAtThisSquare(oldSelectedSquare)+" "+GetSquareInString(oldSelectedSquare)+"-"+GetSquareInString(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
				if(GetAllBlackPieces().get(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==true)
					moveDescription+=" captures "+GetNamePieceAtThisSquare(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
				if(GetAllWhitePieces().get(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==true)
					moveDescription+=" captures "+GetNamePieceAtThisSquare(newSelectedSquare);
			if((newSelectedSquare.y==0&&whitePawns.get(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine)==true)||(newSelectedSquare.y==numberOfSquarePerLine-1&&blackPawns.get(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine)==true))
				moveDescription+=" "+promotionExplicit;
		}
		arrayIsSpecial.add(doMove(oldSelectedSquare,newSelectedSquare));
		
		// we look if the opponent's king is under check 
		BitSet allWhitePieces=GetAllWhitePieces();
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allPieces=(BitSet)allWhitePieces.clone();
		allPieces.or(allBlackPieces);
		ChangePlayerTurn();
		if(currentTurn==black)
		{
			int indexKing=blackKing.nextSetBit(0);
			if(isKingOnCheck(indexKing,allBlackPieces,allWhitePieces,allPieces)==true)
				moveDescription+=" - "+checkDescription;
		}
		else
		{
			int indexKing=whiteKing.nextSetBit(0);
			if(isKingOnCheck(indexKing,allBlackPieces,allWhitePieces,allPieces)==true)
				moveDescription+=" - "+checkDescription;
		}
		ChangePlayerTurn();
		
		// we add the two moves to the description
		arrayMoveDescription.add(moveDescription);
		arrayMoveDescription.add(TransformExplicitMoveDescriptionIntoStandardMoveDescription(moveDescription,isItAmbiguousMove));
		
		// we save position into the array and set the good number of occurrence
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueen,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueen,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)+1);
				return arrayConcernedSquares;
			}
		listPiecesSituation.add(piecesSituation);
		listPiecesSituationOccurrences.add(1);
		return arrayConcernedSquares;
	}
	
	// calculate a point coordinate with the string coordinates given in parameter
	Point GetCorrespondingSquare(String squareCoordinates)
	{
		Point pointCoordinate=new Point(-1,-1);
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		for(int counterLetter=0;counterLetter<heightFirstLettersOfTheAlphabet.length;counterLetter++)
			if(squareCoordinates.charAt(0)==heightFirstLettersOfTheAlphabet[counterLetter])
				pointCoordinate.x=counterLetter;
		pointCoordinate.y=numberOfSquarePerLine-Integer.decode(squareCoordinates.substring(1,2));
		return pointCoordinate;
	}
	
	public void UnmakeMove(Point sourceSquare,Point destinationSquare,String pieceDeleted,Boolean isSpecial)
	{
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueen,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueen,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)-1);
				if(listPiecesSituationOccurrences.get(counterSituations)==0)
				{
					listPiecesSituationOccurrences.remove(counterSituations);
					listPiecesSituation.remove(counterSituations);
				}
				break;
			}
		UnmakeMoveForWithoutRefreshRehearsalHistoric(sourceSquare,destinationSquare,pieceDeleted,isSpecial);
	}
	
	public ArrayList<Point> UnmakeCastling(String castlingDescription)
	{
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.indexOf(kingSideCastlingExplicit)!=-1||castlingDescription.indexOf(kingSideCastlingStandard)!=-1) // king side
			{
				doMove(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine),rightWhiteRookInitialPosition);
				doMove(new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine),whiteKingInitialPosition);
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
			else
			{
				doMove(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine),leftWhiteRookInitialPosition);
				doMove(new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine),whiteKingInitialPosition);
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
		}
		else
		{
			if(castlingDescription.indexOf(kingSideCastlingExplicit)!=-1||castlingDescription.indexOf(kingSideCastlingStandard)!=-1)
			{
				doMove(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine),rightBlackRookInitialPosition);
				doMove(new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine),blackKingInitialPosition);
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
			else
			{
				doMove(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine),leftBlackRookInitialPosition);
				doMove(new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine),blackKingInitialPosition);
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
		}
		return arrayConcernedSquares;
	}
	
	public ArrayList<Point> MakeCastling(String castlingDescription)
	{
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.indexOf(kingSideCastlingExplicit)!=-1||castlingDescription.indexOf(kingSideCastlingStandard)!=-1)
			{
				doMove(rightWhiteRookInitialPosition,new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
				doMove(whiteKingInitialPosition,new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
			else
			{
				doMove(leftWhiteRookInitialPosition,new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
				doMove(whiteKingInitialPosition,new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
		}
		else
		{
			if(castlingDescription.indexOf(kingSideCastlingExplicit)!=-1||castlingDescription.indexOf(kingSideCastlingStandard)!=-1)
			{
				doMove(rightBlackRookInitialPosition,new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
				doMove(blackKingInitialPosition,new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
			else
			{
				doMove(leftBlackRookInitialPosition,new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
				doMove(blackKingInitialPosition,new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
		}
		return arrayConcernedSquares;
	}
	
	// we unmake a move and restore the piece which has eventually been deleted
	public void UnmakeMoveForWithoutRefreshRehearsalHistoric(Point sourceSquare,Point destinationSquare,String pieceDeleted,Boolean isMoveSpecial)
	{
		doMove(destinationSquare,sourceSquare);
		int currentColor=GetThePieceColorAtThisIndex(sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
		int pieceId=GetPieceTypeAtThisIndexAndWithThisColor(currentColor,sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
		if(isMoveSpecial==true)
		{
			switch(pieceId)
			{
			case queenId:
				if(currentColor==white)
				{
					whiteQueen.clear(sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
					whitePawns.set(sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
				}
				else if(currentColor==black)
				{
					blackQueen.clear(sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
					blackPawns.set(sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
				}
				break;
			case rookId:
				if(currentColor==white)
				{
					if(sourceSquare.x==rightWhiteRookInitialPosition.x&&sourceSquare.y==rightWhiteRookInitialPosition.y)
						isWhiteRightRookHasMoved=false;
					else if(sourceSquare.x==leftWhiteRookInitialPosition.x&&sourceSquare.y==leftWhiteRookInitialPosition.y)
						isWhiteLeftRookHasMoved=false;
				}
				else if(currentColor==black)
				{
					if(sourceSquare.x==rightBlackRookInitialPosition.x&&sourceSquare.y==rightBlackRookInitialPosition.y)
						isBlackRightRookHasMoved=false;
					else if(sourceSquare.x==leftBlackRookInitialPosition.x&&sourceSquare.y==leftBlackRookInitialPosition.y)
						isBlackLeftRookHasMoved=false;
				}
				break;
			case kingId:
				if(currentColor==white&&sourceSquare.x==whiteKingInitialPosition.x&&sourceSquare.y==whiteKingInitialPosition.y)
					isWhiteKingHasMoved=false;
				else if(currentColor==black&&sourceSquare.x==blackKingInitialPosition.x&&sourceSquare.y==blackKingInitialPosition.y)
					isBlackKingHasMoved=false;
			default:
				;
			}
		}
		
		if(pieceDeleted!="")
		{
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==black)
			{
				if(pieceDeleted.equals(new String("pawn"))==true)
					whitePawns.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("knight"))==true)
					whiteKnights.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("king"))==true)
					whiteKing.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("rook"))==true)
					whiteRooks.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("bishop"))==true)
					whiteBishops.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("queen"))==true)
					whiteQueen.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
			}
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==white)
			{
				if(pieceDeleted.equals(new String("pawn"))==true)
					blackPawns.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("knight"))==true)
					blackKnights.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("king"))==true)
					blackKing.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("rook"))==true)
					blackRooks.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("bishop"))==true)
					blackBishops.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
				else if(pieceDeleted.equals(new String("queen"))==true)
					blackQueen.set(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
			}
		}
	}
	
	public int GetThePieceColorAtThisIndex(int pieceIndex)
	{
		BitSet allWhitePieces=(BitSet)GetAllWhitePieces().clone();
		BitSet allBlackPieces=(BitSet)GetAllBlackPieces().clone();
		if(allWhitePieces.get(pieceIndex)==true)
			return white;
		if(allBlackPieces.get(pieceIndex)==true)
			return black;
		return noPieceId;
	}
	
	public int GetPieceTypeAtThisIndexAndWithThisColor(int pieceColor,int pieceIndex)
	{
		switch(pieceColor)
		{
		case white:
			return GetWhitePieceType(pieceIndex);
		case black:
			return GetBlackPieceType(pieceIndex);
		default:
			;
		}
		return noPieceId;
	}
	
	private void doMoveAccordingToColorAndType(int color,int pieceType,int indexSource,int indexDestination)
	{
		switch(color)
		{
		case white:
			switch(pieceType)
			{
			case pawnId:
				whitePawns.clear(indexSource);
				whitePawns.set(indexDestination);
				break;
			case rookId:
				whiteRooks.clear(indexSource);
				whiteRooks.set(indexDestination);
				break;
			case bishopId:
				whiteBishops.clear(indexSource);
				whiteBishops.set(indexDestination);
				break;
			case knightId:
				whiteKnights.clear(indexSource);
				whiteKnights.set(indexDestination);
				break;
			case queenId:
				whiteQueen.clear(indexSource);
				whiteQueen.set(indexDestination);
				break;
			case kingId:
				whiteKing.clear(indexSource);
				whiteKing.set(indexDestination);
				break;
			}
			DeleteBlackPieceAtThisIndex(indexDestination);
			break;
		case black:
			switch(pieceType)
			{
			case pawnId:
				blackPawns.clear(indexSource);
				blackPawns.set(indexDestination);
				break;
			case rookId:
				blackRooks.clear(indexSource);
				blackRooks.set(indexDestination);
				break;
			case bishopId:
				blackBishops.clear(indexSource);
				blackBishops.set(indexDestination);
				break;
			case knightId:
				blackKnights.clear(indexSource);
				blackKnights.set(indexDestination);
				break;
			case queenId:
				blackQueen.clear(indexSource);
				blackQueen.set(indexDestination);
				break;
			case kingId:
				blackKing.clear(indexSource);
				blackKing.set(indexDestination);
				break;
			}
			DeleteWhitePieceAtThisIndex(indexDestination);
		default:
			;
		}
	}
	
	// we investigate to know is three moves repetition occurs
	public boolean IsThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(int color,int pieceType,int indexSource,int indexDestination)
	{
		int typeOfEventualyDeletedPiece=GetPieceTypeAtThisIndexAndWithThisColor(-color,indexDestination);
		doMoveAccordingToColorAndType(color,pieceType,indexSource,indexDestination);
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueen,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueen,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true&&listPiecesSituationOccurrences.get(counterSituations)+1>=maximumOccurrenceForASituation)
			{
				doMoveAccordingToColorAndType(color,pieceType,indexDestination,indexSource);
				if(typeOfEventualyDeletedPiece!=noPieceId&&color==white)
					SetBlackPiece(typeOfEventualyDeletedPiece,indexDestination);
				if(typeOfEventualyDeletedPiece!=noPieceId&&color==black)
					SetWhitePiece(typeOfEventualyDeletedPiece,indexDestination);
				return true;
			}
		doMoveAccordingToColorAndType(color,pieceType,indexDestination,indexSource);
		if(typeOfEventualyDeletedPiece!=noPieceId&&color==white)
			SetBlackPiece(typeOfEventualyDeletedPiece,indexDestination);
		if(typeOfEventualyDeletedPiece!=noPieceId&&color==black)
			SetWhitePiece(typeOfEventualyDeletedPiece,indexDestination);
		return false;
	}
	
	// we get all the moves for a piece at a specific square, useful for human player
	public ArrayList<Point> GetListOfPossibleMovesForAPieceWithCheckChecking(int indexSource)
	{
		BitSet bitSetMovesResult=new BitSet();
		int currentIndex=indexSource;
		int currentColor=GetThePieceColorAtThisIndex(currentIndex);
		int currentPieceType=GetPieceTypeAtThisIndexAndWithThisColor(currentColor,currentIndex);
		BitSet blackPieces=GetAllBlackPieces();
		BitSet whitePieces=GetAllWhitePieces();
		BitSet allPieces=(BitSet)whitePieces.clone();
		allPieces.or((BitSet)blackPieces.clone());
		BitSet ownPieces=null;
		int kingIndex=0;
		if(currentColor==white)
		{
			kingIndex=whiteKing.nextSetBit(0);
			ownPieces=whitePieces;
		}
		else if(currentColor==black)
		{
			kingIndex=blackKing.nextSetBit(0);
			ownPieces=blackPieces;
		}
		BitSet bitSetPotentialMovesRequiredToAvoidCheck=getBitSetOfMovesRequiredToAvoidCheck(kingIndex,blackPieces,whitePieces,allPieces);
		if(bitSetPotentialMovesRequiredToAvoidCheck!=null) // the rare case where king is check by two pieces at the same time
			bitSetPotentialMovesRequiredToAvoidCheck.clear(kingIndex);
		
		// get the right method according to piece type
		switch(currentPieceType)
		{
		case pawnId:
			if(currentColor==white)
				bitSetMovesResult=getMovesForAWhitePawn(currentIndex,blackPieces,allPieces);
			else if(currentColor==black)
				bitSetMovesResult=getMovesForABlackPawn(currentIndex,whitePieces,allPieces);
			break;
		case kingId:
			bitSetMovesResult=getMovesForKingWithCheckChecking(currentIndex,ownPieces,allPieces);
			break;
		case queenId:
			bitSetMovesResult=getMovesForQueen(currentIndex,ownPieces,allPieces);
			break;
		case bishopId:
			bitSetMovesResult=getMovesForBishop(currentIndex,ownPieces,allPieces);
			break;
		case rookId:
			bitSetMovesResult=getMovesForRook(currentIndex,ownPieces,allPieces);
			break;
		case knightId:
			bitSetMovesResult=getMovesForKnight(currentIndex,ownPieces,allPieces);
			break;
		default:
			;
		}
		
		if(bitSetPotentialMovesRequiredToAvoidCheck==null&&currentPieceType!=kingId) // if king is in check by two others pieces, it is the only piece which can move
			bitSetMovesResult=new BitSet();
		else
		{
			// in case king is in chess, delete moves that let king in check
			if(currentPieceType!=kingId&&bitSetPotentialMovesRequiredToAvoidCheck!=null&&bitSetPotentialMovesRequiredToAvoidCheck.cardinality()!=0)
				bitSetMovesResult.and(bitSetPotentialMovesRequiredToAvoidCheck);
			
			// detect nailed moves and constraint the right pieces
			BitSet nailedPieces=getNailedPieces(blackPieces,whitePieces,allPieces);
			if(nailedPieces.cardinality()!=0&&nailedPieces.get(currentIndex)==true)
				bitSetMovesResult.and(nailedPieces);
		}
		
		// we put each move possible into an arrayList and return it
		ArrayList<Point> arrayListPoint=new ArrayList<Point>();
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
				if(bitSetMovesResult.get(counterVertical*numberOfSquarePerLine+counterHorizontal)==true)
					if(IsThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(currentColor,currentPieceType,currentIndex,counterVertical*numberOfSquarePerLine+counterHorizontal)==false)
						arrayListPoint.add(new Point(counterHorizontal,counterVertical));
					
		return arrayListPoint;
	}
	
	public void SetCounterOfMoves(int counterOfMovesParameter)
	{
		counterMoveFinished=counterOfMovesParameter;
	}
	
	public int GetCounterOfMoves()
	{
		return counterMoveFinished;
	}
	
	// change the player turn, because we use opposed values, we don't have to know what is the current turn, and the turn we have to switch on
	public void ChangePlayerTurn()
	{
		currentTurn=-currentTurn;
	}
	
	// used for human player
	public boolean IsThisMovePossible(Point sourceCoordinates,Point destinationCoordinates)
	{
		ArrayList<Point> arrayListPossibleMoves=GetListOfPossibleMovesForAPieceWithCheckChecking(sourceCoordinates.x+sourceCoordinates.y*numberOfSquarePerLine);
		Iterator<Point> PointIterator=arrayListPossibleMoves.iterator();
		while(PointIterator.hasNext())
		{
			Point currentPoint=PointIterator.next();
			if(destinationCoordinates.x==currentPoint.x&&destinationCoordinates.y==currentPoint.y)
				return true;
		}
		return false;
	}
	
	public void doThisMoveAndGetDescriptionWithMoveDescription(BitSet bitsetCurrentPieces,String moveDescription,ArrayList<String> arrayMoveDescription,ArrayList<Boolean> arrayIsSpecial)
	{
		// first of all we get destination
		String moveDescriptionWithoutPieceIdentifier=moveDescription.replaceAll("N","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("R","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("B","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("Q","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("K","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll(promotionStandard,"");
		String stringDestination=moveDescriptionWithoutPieceIdentifier.substring(moveDescriptionWithoutPieceIdentifier.length()-2,moveDescriptionWithoutPieceIdentifier.length());
		Point pointDestination=GetCorrespondingSquare(stringDestination);
		ArrayList<Point> arrayListPossibleMoves=new ArrayList<Point>();
		int numberOfPossiblePieces=0;
		Point sourceMove=null;
		ArrayList<Point> arrayListPossibleSourcePieces=new ArrayList<Point>();
		for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
		{
			arrayListPossibleMoves.clear();
			if(bitsetCurrentPieces.get(counterBits)==true)
			{
				sourceMove=new Point(counterBits%numberOfSquarePerLine,counterBits/numberOfSquarePerLine);
				arrayListPossibleMoves=GetListOfPossibleMovesForAPieceWithCheckChecking(counterBits);
				Point destinationOfCurrentMove=null;
				for(int counterMovesFirstLevel=0;counterMovesFirstLevel<arrayListPossibleMoves.size();counterMovesFirstLevel++)
				{
					destinationOfCurrentMove=arrayListPossibleMoves.get(counterMovesFirstLevel);
					if(destinationOfCurrentMove.x==pointDestination.x&&destinationOfCurrentMove.y==pointDestination.y)
					{
						numberOfPossiblePieces++;
						arrayListPossibleSourcePieces.add((Point)sourceMove.clone());
					}
				}
			}
		}
		
		// here multiple piece can go do the destination, we have to delete the wrong pieces
		if(numberOfPossiblePieces>1)
		{
			// we have to know what piece is concerned
			ArrayList<Point> ArrayListPossibleSourceWithColumnFilter=new ArrayList<Point>();
			for(int counterPossiblePieceForColumnFilter=0;counterPossiblePieceForColumnFilter<arrayListPossibleSourcePieces.size();counterPossiblePieceForColumnFilter++)
			{
				Point currentPieceSourceForColumnFilter=arrayListPossibleSourcePieces.get(counterPossiblePieceForColumnFilter);
				if(moveDescriptionWithoutPieceIdentifier.charAt(0)<'a'||moveDescriptionWithoutPieceIdentifier.charAt(0)>'h') // we do coherence check on the file content
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : ["+moveDescriptionWithoutPieceIdentifier.charAt(0)+"].\n"+"It should be between a and h.\n"+"Move description : "+moveDescription);
				}
				if(currentPieceSourceForColumnFilter.x==moveDescriptionWithoutPieceIdentifier.charAt(0)-'a')
					ArrayListPossibleSourceWithColumnFilter.add((Point)currentPieceSourceForColumnFilter.clone());
			}
			if(ArrayListPossibleSourceWithColumnFilter.size()>1) // column filter is not enough, we use line filter
			{
				ArrayList<Point> ArrayListPossibleSourceWithColumnAndLineFilter=new ArrayList<Point>();
				for(int counterPossiblePieceForLineFilter=0;counterPossiblePieceForLineFilter<ArrayListPossibleSourceWithColumnFilter.size();counterPossiblePieceForLineFilter++)
				{
					Point currentPieceSourceForColumnAndLineFilter=ArrayListPossibleSourceWithColumnFilter.get(counterPossiblePieceForLineFilter);
					if(moveDescriptionWithoutPieceIdentifier.charAt(1)<'1'||moveDescriptionWithoutPieceIdentifier.charAt(1)>'8') // we do coherence check on the file content for line filter
					{
						javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : "+moveDescriptionWithoutPieceIdentifier.charAt(1)+".\n"+"It should be between 1 and 8.\n"+"Move description : "+moveDescription);
					}
					if(numberOfSquarePerLine-currentPieceSourceForColumnAndLineFilter.y-1==moveDescriptionWithoutPieceIdentifier.charAt(1)-'1')
						ArrayListPossibleSourceWithColumnAndLineFilter.add((Point)currentPieceSourceForColumnAndLineFilter.clone()); // we have found the piece according to line filter
				}
				if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==0) // error case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()>1) // error case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, too many pieces found.\n"+"Move description : "+moveDescription);
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==1)
					doThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnAndLineFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial); // we have the right piece according to column and line filter
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==0)
			{
				javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify column of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==1)
				doThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial);
		}
		else if(numberOfPossiblePieces==0) // case which non piece can go to the destination
		{
			javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify piece at the begining move.\n"+"Move description : "+moveDescription);
		}
		else
			doThisMoveAndGetDescription(arrayListPossibleSourcePieces.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial); // we have directly the good piece, it's too easy
	}
	
	private final int Evaluate()
	{
		evaluationsCounterForCurrentThread++;
		return (whitePawns.cardinality()-blackPawns.cardinality())*pawnValue+(whiteKnights.cardinality()-blackKnights.cardinality())*knightValue+(whiteBishops.cardinality()-blackBishops.cardinality())*bishopValue+(whiteRooks.cardinality()-blackRooks.cardinality())*rookValue+(whiteQueen.cardinality()-blackQueen.cardinality())*queenValue;
	}
	
	public int GetBlackPieceType(int indexPiece)
	{
		if(blackPawns.get(indexPiece)==true)
			return pawnId;
		else if(blackKnights.get(indexPiece)==true)
			return knightId;
		else if(blackBishops.get(indexPiece)==true)
			return bishopId;
		else if(blackRooks.get(indexPiece)==true)
			return rookId;
		else if(blackQueen.get(indexPiece)==true)
			return queenId;
		else if(blackKing.get(indexPiece)==true)
			return kingId;
		return noPieceId;
	}
	
	public int GetWhitePieceType(int indexPiece)
	{
		if(whitePawns.get(indexPiece)==true)
			return pawnId;
		else if(whiteKnights.get(indexPiece)==true)
			return knightId;
		else if(whiteBishops.get(indexPiece)==true)
			return bishopId;
		else if(whiteRooks.get(indexPiece)==true)
			return rookId;
		else if(whiteQueen.get(indexPiece)==true)
			return queenId;
		else if(whiteKing.get(indexPiece)==true)
			return kingId;
		return noPieceId;
	}
	
	public void DeleteBlackPieceAtThisIndexWithThisType(int pieceIndex,int indexType)
	{
		switch(indexType)
		{
		case pawnId:
			blackPawns.clear(pieceIndex);
			break;
		case knightId:
			blackKnights.clear(pieceIndex);
			break;
		case rookId:
			blackRooks.clear(pieceIndex);
			break;
		case bishopId:
			blackBishops.clear(pieceIndex);
			break;
		case queenId:
			blackQueen.clear(pieceIndex);
			break;
		case kingId:
			blackKing.clear(pieceIndex);
			break;
		default:
			;
		}
	}
	
	public void DeleteWhitePieceAtThisIndexWithThisType(int pieceIndex,int indexType)
	{
		switch(indexType)
		{
		case pawnId:
			whitePawns.clear(pieceIndex);
			break;
		case knightId:
			whiteKnights.clear(pieceIndex);
			break;
		case rookId:
			whiteRooks.clear(pieceIndex);
			break;
		case bishopId:
			whiteBishops.clear(pieceIndex);
			break;
		case queenId:
			whiteQueen.clear(pieceIndex);
			break;
		case kingId:
			whiteKing.clear(pieceIndex);
			break;
		default:
			;
		}
	}
	
	public void DeleteBlackPieceAtThisIndex(int pieceIndex)
	{
		blackPawns.clear(pieceIndex);
		blackKnights.clear(pieceIndex);
		blackBishops.clear(pieceIndex);
		blackRooks.clear(pieceIndex);
		blackQueen.clear(pieceIndex);
		blackKing.clear(pieceIndex);
	}
	
	public void DeleteWhitePieceAtThisIndex(int pieceIndex)
	{
		whitePawns.clear(pieceIndex);
		whiteKnights.clear(pieceIndex);
		whiteBishops.clear(pieceIndex);
		whiteRooks.clear(pieceIndex);
		whiteQueen.clear(pieceIndex);
		whiteKing.clear(pieceIndex);
	}
	
	private void SetBlackPiece(int pieceType,int indexMove)
	{
		switch(pieceType)
		{
		case pawnId:
			blackPawns.set(indexMove);
			break;
		case knightId:
			blackKnights.set(indexMove);
			break;
		case bishopId:
			blackBishops.set(indexMove);
			break;
		case rookId:
			blackRooks.set(indexMove);
			break;
		case queenId:
			blackQueen.set(indexMove);
			break;
		case kingId:
			blackKing.set(indexMove);
			break;
		case noPieceId:
			System.out.println("Error in SetBlackPiece noPieceId found indexMove : "+indexMove);
			break;
		default:
			;
		}
	}
	
	private void SetWhitePiece(int pieceType,int indexMove)
	{
		switch(pieceType)
		{
		case pawnId:
			whitePawns.set(indexMove);
			break;
		case knightId:
			whiteKnights.set(indexMove);
			break;
		case bishopId:
			whiteBishops.set(indexMove);
			break;
		case rookId:
			whiteRooks.set(indexMove);
			break;
		case queenId:
			whiteQueen.set(indexMove);
			break;
		case kingId:
			whiteKing.set(indexMove);
			break;
		case noPieceId:
			System.out.println("Error in SetWhitePiece noPieceId found indexMove : "+indexMove);
			break;
		default:
			;
		}
	}
	
	private BitSet getMovesForAWhitePawn(int pieceIndex,BitSet blackPieces,BitSet allPieces)
	{
		BitSet bitSetMovesResult=new BitSet();
		if(pieceIndex>=numberOfSquarePerLine)
		{
			bitSetMovesResult.set(pieceIndex-numberOfSquarePerLine);
			if(pieceIndex>=numberOfSquarePerLine*(numberOfSquarePerLine-2)&&allPieces.get(pieceIndex-numberOfSquarePerLine)==false)
				bitSetMovesResult.set(pieceIndex-2*numberOfSquarePerLine);
			BitSet bitsToBeDeleted=(BitSet)bitSetMovesResult.clone();
			bitsToBeDeleted.and(allPieces);
			bitSetMovesResult.xor(bitsToBeDeleted);
			if(pieceIndex%numberOfSquarePerLine>0&&blackPieces.get(pieceIndex-1-numberOfSquarePerLine))
				bitSetMovesResult.set(pieceIndex-1-numberOfSquarePerLine);
			if(pieceIndex%numberOfSquarePerLine<numberOfSquarePerLine-1&&blackPieces.get(pieceIndex+1-numberOfSquarePerLine))
				bitSetMovesResult.set(pieceIndex+1-numberOfSquarePerLine);
		}
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForABlackPawn(int pieceIndex,BitSet whitePieces,BitSet allPieces)
	{
		BitSet bitSetMovesResult=new BitSet();
		if(pieceIndex<numberOfSquarePerLine*(numberOfSquarePerLine-1))
		{
			bitSetMovesResult.set(pieceIndex+numberOfSquarePerLine);
			if(pieceIndex<2*numberOfSquarePerLine&&allPieces.get(pieceIndex+numberOfSquarePerLine)==false)
				bitSetMovesResult.set(pieceIndex+2*numberOfSquarePerLine);
			BitSet bitsToBeDeleted=(BitSet)bitSetMovesResult.clone();
			bitsToBeDeleted.and(allPieces);
			bitSetMovesResult.xor(bitsToBeDeleted);
			if(pieceIndex%numberOfSquarePerLine>0&&whitePieces.get(pieceIndex-1+numberOfSquarePerLine))
				bitSetMovesResult.set(pieceIndex-1+numberOfSquarePerLine);
			if(pieceIndex%numberOfSquarePerLine<numberOfSquarePerLine-1&&whitePieces.get(pieceIndex+1+numberOfSquarePerLine))
				bitSetMovesResult.set(pieceIndex+1+numberOfSquarePerLine);
		}
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForRook(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		BitSet bitSetMovesResult=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		int horizontalPosition=pieceIndex%numberOfSquarePerLine;
		int verticalPosition=pieceIndex/numberOfSquarePerLine;
		
		// first of all we get the corresponding mask for the current position 
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		BitSet bitSetLinesMask=HashMapLinesMasks.get(bitSetPieceCoordinates);
		
		// we compute the current status board for the two lines and get the moves result 
		BitSet bitSetCurrentLinesPieces=(BitSet)allPieces.clone();
		bitSetCurrentLinesPieces.and(bitSetLinesMask);
		bitSetCurrentLinesPieces.set(verticalPosition*numberOfSquarePerLine);
		bitSetCurrentLinesPieces.set(verticalPosition*numberOfSquarePerLine+numberOfSquarePerLine-1);
		bitSetCurrentLinesPieces.set(horizontalPosition);
		bitSetCurrentLinesPieces.set(horizontalPosition+numberOfSquarePerLine*(numberOfSquarePerLine-1));
		bitSetMovesResult=(BitSet)HashMapLinesPossibilities.get(bitSetCurrentLinesPieces).clone();
		
		// we get the current pieces to eliminate moves that eat our own pieces			
		bitSetMovesResult.andNot(ownPieces);
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForBishop(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		// first of all we get the corresponding mask for the current position 
		BitSet bitSetMovesResult;
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		
		// we compute the current status board for the two diagonals and get the moves result 
		BitSet bitSetCurrentDiagonalsPieces=(BitSet)allPieces.clone();
		bitSetCurrentDiagonalsPieces.and(HashMapDiagonalsMasks.get(bitSetPieceCoordinates));
		bitSetCurrentDiagonalsPieces.or(arrayDiagonalsExtremes[pieceIndex]);
		if(pieceIndex==0||pieceIndex==numberOfSquarePerLine-1||pieceIndex==numberOfSquarePerLine*(numberOfSquarePerLine-1)||pieceIndex==numberOfSquarePerLine*numberOfSquarePerLine-1)
			bitSetCurrentDiagonalsPieces.clear(pieceIndex);
		bitSetMovesResult=(BitSet)HashMapDiagonalsPossibilities.get(bitSetCurrentDiagonalsPieces).clone();
		bitSetMovesResult.andNot(ownPieces);
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForKing(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		BitSet bitSetMovesResult=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetMovesResult=(BitSet)HashMapKingMoves.get(bitSetPieceCoordinates).clone();
		
		// we delete the moves that eat our own piece
		BitSet bitsToBeDeleted=(BitSet)bitSetMovesResult.clone();
		bitsToBeDeleted.and(ownPieces);
		bitSetMovesResult.xor(bitsToBeDeleted);
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForKingWithCheckChecking(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		BitSet bitSetMovesResult=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetMovesResult=(BitSet)HashMapKingMoves.get(bitSetPieceCoordinates).clone();
		
		// we delete the moves that eat our own piece
		BitSet bitsToBeDeleted=(BitSet)bitSetMovesResult.clone();
		bitsToBeDeleted.and(ownPieces);
		bitSetMovesResult.xor(bitsToBeDeleted);
		BitSet blackPieces=GetAllBlackPieces();
		BitSet whitePieces=GetAllWhitePieces();
		int currentPossibleMoves=-1;
		
		// we delete moves that put king in check
		while(true)
		{
			currentPossibleMoves=bitSetMovesResult.nextSetBit(currentPossibleMoves+1);
			if(currentPossibleMoves==-1)
				break;
			if(isThisEmptySquareAttacked(currentPossibleMoves,blackPieces,whitePieces,allPieces)==true)
				bitSetMovesResult.clear(currentPossibleMoves);
		}
		
		// castling management
		if(currentTurn==white)
		{
			if(whiteKing.get(whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine)==true&&whiteRooks.get(rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine)==true&&isWhiteKingHasMoved==false&&isWhiteRightRookHasMoved==false)
			{
				BitSet allPiecesForCastling=(BitSet)allPieces.clone();
				allPiecesForCastling.and(whiteKingCastlingMask);
				if(allPiecesForCastling.isEmpty())
				{
					int indexSquare=0;
					for(indexSquare=whiteKingCastlingMask.nextSetBit(indexSquare);indexSquare!=-1;indexSquare=whiteKingCastlingMask.nextSetBit(indexSquare+1))
						if(isThisEmptySquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
					if(indexSquare==-1&&isKingOnCheck(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						bitSetMovesResult.set(whiteKingKingCastlingDestination);
				}
			}
			if(whiteKing.get(whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine)==true&&whiteRooks.get(leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine)==true&&isWhiteKingHasMoved==false&&isWhiteLeftRookHasMoved==false)
			{
				BitSet allPiecesForCastling=(BitSet)allPieces.clone();
				allPiecesForCastling.and(whiteQueenCastlingMask);
				if(allPiecesForCastling.isEmpty())
				{
					int indexSquare=0;
					for(indexSquare=whiteQueenCastlingMask.nextSetBit(indexSquare);indexSquare!=-1;indexSquare=whiteQueenCastlingMask.nextSetBit(indexSquare+1))
						if(isThisEmptySquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
					if(indexSquare==-1&&isKingOnCheck(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						bitSetMovesResult.set(whiteKingQueenCastlingDestination);
				}
			}
		}
		else
		{
			if(blackKing.get(blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine)==true&&blackRooks.get(rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine)==true&&isBlackKingHasMoved==false&&isBlackRightRookHasMoved==false)
			{
				BitSet allPiecesForCastling=(BitSet)allPieces.clone();
				allPiecesForCastling.and(blackKingCastlingMask);
				if(allPiecesForCastling.isEmpty())
				{
					int indexSquare=0;
					for(indexSquare=blackKingCastlingMask.nextSetBit(indexSquare);indexSquare!=-1;indexSquare=blackKingCastlingMask.nextSetBit(indexSquare+1))
						if(isThisEmptySquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
					if(indexSquare==-1&&isKingOnCheck(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						bitSetMovesResult.set(blackKingKingCastlingDestination);
				}
			}
			if(blackKing.get(blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine)==true&&blackRooks.get(leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine)==true&&isBlackKingHasMoved==false&&isBlackLeftRookHasMoved==false)
			{
				BitSet allPiecesForCastling=(BitSet)allPieces.clone();
				allPiecesForCastling.and(blackQueenCastlingMask);
				if(allPiecesForCastling.isEmpty())
				{
					int indexSquare=0;
					for(indexSquare=blackQueenCastlingMask.nextSetBit(indexSquare);indexSquare!=-1;indexSquare=blackQueenCastlingMask.nextSetBit(indexSquare+1))
						if(isThisEmptySquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
					if(indexSquare==-1&&isKingOnCheck(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						bitSetMovesResult.set(blackKingQueenCastlingDestination);
				}
			}
		}
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForKnight(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		BitSet bitSetMovesResult=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetMovesResult=(BitSet)HashMapKnightMoves.get(bitSetPieceCoordinates).clone();
		
		// we delete the moves that eat our own piece
		BitSet bitsToBeDeleted=(BitSet)bitSetMovesResult.clone();
		bitsToBeDeleted.and(ownPieces);
		bitSetMovesResult.xor(bitsToBeDeleted);
		return bitSetMovesResult;
	}
	
	private BitSet getMovesForQueen(int pieceIndex,BitSet ownPieces,BitSet allPieces)
	{
		// first of all we get the corresponding lines mask for the current position 
		BitSet bitSetMovesResult=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		BitSet bitSetPieceCoordinates=new BitSet(numberOfSquarePerLine*numberOfSquarePerLine);
		bitSetPieceCoordinates.set(pieceIndex);
		int horizontalPosition=pieceIndex%numberOfSquarePerLine;
		int verticalPosition=pieceIndex/numberOfSquarePerLine;
		BitSet bitSetLinesMask=HashMapLinesMasks.get(bitSetPieceCoordinates);
		
		// we compute the current status board for the two lines and get the moves result 
		BitSet bitSetCurrentLinesPieces=(BitSet)allPieces.clone();
		bitSetCurrentLinesPieces.and(bitSetLinesMask);
		bitSetCurrentLinesPieces.set(verticalPosition*numberOfSquarePerLine);
		bitSetCurrentLinesPieces.set(verticalPosition*numberOfSquarePerLine+numberOfSquarePerLine-1);
		bitSetCurrentLinesPieces.set(horizontalPosition);
		bitSetCurrentLinesPieces.set(horizontalPosition+numberOfSquarePerLine*(numberOfSquarePerLine-1));
		BitSet bitSetLinesMovesResult=HashMapLinesPossibilities.get(bitSetCurrentLinesPieces);
		
		// we compute the current status board for the two diagonals and get the moves result 
		BitSet bitSetCurrentDiagonalsPieces=(BitSet)allPieces.clone();
		bitSetCurrentDiagonalsPieces.and(HashMapDiagonalsMasks.get(bitSetPieceCoordinates));
		bitSetCurrentDiagonalsPieces.or(arrayDiagonalsExtremes[pieceIndex]);
		
		// we clear our own position when we are at a corner of the chessboard in order to avoid ambiguous localization of the current piece
		if(pieceIndex==0||pieceIndex==numberOfSquarePerLine-1||pieceIndex==numberOfSquarePerLine*(numberOfSquarePerLine-1)||pieceIndex==numberOfSquarePerLine*numberOfSquarePerLine-1)
			bitSetCurrentDiagonalsPieces.clear(pieceIndex);
		
		// we have now the good offset to retrieve the moves bitset 
		BitSet bitSetDiagonalsMovesResult=HashMapDiagonalsPossibilities.get(bitSetCurrentDiagonalsPieces);
		
		// now we add the diagonals and lines moves re
		bitSetMovesResult.or(bitSetLinesMovesResult);
		bitSetMovesResult.or(bitSetDiagonalsMovesResult);
		
		// we get the current pieces to eliminates moves that eat our own pieces				
		bitSetMovesResult.andNot(ownPieces);
		return bitSetMovesResult;
	}
	
	private boolean doMoveFromTwoIndexBlack(int pieceType,int indexSource,int indexDestination,boolean isSpecial)
	{
		switch(pieceType)
		{
		case pawnId:
			blackPawns.clear(indexSource);
			if(isSpecial==true)
				blackQueen.clear(indexSource);
			if(indexDestination>numberOfSquarePerLine*(numberOfSquarePerLine-1)-1)
			{
				blackQueen.set(indexDestination);
				return true;
			}
			blackPawns.set(indexDestination);
			break;
		case queenId:
			blackQueen.clear(indexSource);
			blackQueen.set(indexDestination);
			break;
		case rookId:
			blackRooks.clear(indexSource);
			blackRooks.set(indexDestination);
			break;
		case bishopId:
			blackBishops.clear(indexSource);
			blackBishops.set(indexDestination);
			break;
		case knightId:
			blackKnights.clear(indexSource);
			blackKnights.set(indexDestination);
			break;
		case kingId:
			blackKing.clear(indexSource);
			blackKing.set(indexDestination);
			break;
		default:
		}
		return false;
	}
	
	private boolean doMoveFromTwoIndexWhite(int pieceType,int indexSource,int indexDestination,boolean isSpecial)
	{
		switch(pieceType)
		{
		case pawnId: // we put all special moves in paw section for faster process
			whitePawns.clear(indexSource);
			if(isSpecial==true)
				whiteQueen.clear(indexSource);
			if(indexDestination<numberOfSquarePerLine)
			{
				whiteQueen.set(indexDestination);
				return true;
			}
			whitePawns.set(indexDestination);
			break;
		case queenId:
			whiteQueen.clear(indexSource);
			whiteQueen.set(indexDestination);
			break;
		case rookId:
			whiteRooks.clear(indexSource);
			whiteRooks.set(indexDestination);
			break;
		case bishopId:
			whiteBishops.clear(indexSource);
			whiteBishops.set(indexDestination);
			break;
		case knightId:
			whiteKnights.clear(indexSource);
			whiteKnights.set(indexDestination);
			break;
		case kingId:
			whiteKing.clear(indexSource);
			whiteKing.set(indexDestination);
			break;
		default:
		}
		return false;
	}
	
	private int RecursiveMethodWithCheckChecking(int currentDepth,int maximumDepth,int alpha,int beta)
	{
		if(whiteKing.cardinality()==0)
			return -kingValue;
		if(blackKing.cardinality()==0)
			return kingValue;
		if(currentDepth==0)
			return Evaluate();
		int[] arrayPiecesTypes=(arrayArraysTypes[currentDepth]);
		int[] arraySource=(arrayArraysSources[currentDepth]);
		int[] arrayDestination=(arrayArraysDestination[currentDepth]);
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allWhitePieces=GetAllWhitePieces();
		int numberOfMoves=getMovesForCurrentPlayerWithDeletionAccordingToThreeRepetitionLaw(arrayPiecesTypes,arraySource,arrayDestination,allBlackPieces,allWhitePieces);
		int typeOfEventualyDeletedPiece=noPieceId;
		if(getCurrentTurn()==white)
		{
			int extremum=-infinite;
			for(int counterMoves=numberOfMoves-1;counterMoves>=0;counterMoves--)
			{
				int sourceIndex=arraySource[counterMoves];
				int destinationIndex=arrayDestination[counterMoves];
				int pieceType=arrayPiecesTypes[counterMoves];
				if(allBlackPieces.get(destinationIndex)==true)
				{
					typeOfEventualyDeletedPiece=GetBlackPieceType(destinationIndex);
					DeleteBlackPieceAtThisIndexWithThisType(destinationIndex,typeOfEventualyDeletedPiece);
				}
				boolean isSpecial=doMoveFromTwoIndexWhite(pieceType,sourceIndex,destinationIndex,false);
				currentTurn=-currentTurn;
				int resultRecursiveMethod=RecursiveMethodWithCheckChecking(currentDepth-1,maximumDepth,alpha,beta);
				currentTurn=-currentTurn;
				if(resultRecursiveMethod>=extremum)
				{
					if(currentDepth==maximumDepth)
					{
						if(resultRecursiveMethod>extremum)
							numberOfBestMoves=0;
						arrayBestSource[numberOfBestMoves]=sourceIndex;
						arrayBestDestination[numberOfBestMoves++]=destinationIndex;
					}
					extremum=resultRecursiveMethod;
				}
				doMoveFromTwoIndexWhite(pieceType,destinationIndex,sourceIndex,isSpecial);
				if(typeOfEventualyDeletedPiece!=noPieceId)
				{
					SetBlackPiece(typeOfEventualyDeletedPiece,destinationIndex);
					typeOfEventualyDeletedPiece=noPieceId;
				}
				if(beta<extremum) // very important strict comparison to get all potential interesting moves
					return extremum;
				alpha=Math.max(alpha,extremum);
			}
			return extremum;
		}
		else
		{
			int extremum=infinite;
			for(int counterMoves=numberOfMoves-1;counterMoves>=0;counterMoves--)
			{
				int sourceIndex=arraySource[counterMoves];
				int destinationIndex=arrayDestination[counterMoves];
				int pieceType=arrayPiecesTypes[counterMoves];
				if(allWhitePieces.get(destinationIndex)==true)
				{
					typeOfEventualyDeletedPiece=GetWhitePieceType(destinationIndex);
					DeleteWhitePieceAtThisIndexWithThisType(destinationIndex,typeOfEventualyDeletedPiece);
				}
				boolean isSpecial=doMoveFromTwoIndexBlack(pieceType,sourceIndex,destinationIndex,false);
				currentTurn=-currentTurn;
				int resultRecursiveMethod=RecursiveMethodWithCheckChecking(currentDepth-1,maximumDepth,alpha,beta);
				currentTurn=-currentTurn;
				if(resultRecursiveMethod<=extremum)
				{
					if(currentDepth==maximumDepth)
					{
						if(resultRecursiveMethod<extremum)
							numberOfBestMoves=0;
						arrayBestSource[numberOfBestMoves]=sourceIndex;
						arrayBestDestination[numberOfBestMoves++]=destinationIndex;
					}
					extremum=resultRecursiveMethod;
				}
				doMoveFromTwoIndexBlack(pieceType,destinationIndex,sourceIndex,isSpecial);
				if(typeOfEventualyDeletedPiece!=noPieceId)
				{
					SetWhitePiece(typeOfEventualyDeletedPiece,destinationIndex);
					typeOfEventualyDeletedPiece=noPieceId;
				}
				if(alpha>extremum)
					return extremum;
				beta=Math.min(beta,extremum);
			}
			return extremum;
		}
	}
	
	// the most important method, run the search tree
	private int RecursiveMethod(int currentDepth,int maximumDepth,int alpha,int beta)
	{
		if(whiteKing.cardinality()==0)
			return -kingValue;
		if(blackKing.cardinality()==0)
			return kingValue;
		if(currentDepth==0)
			return Evaluate();
		int[] arrayPiecesTypes=(arrayArraysTypes[currentDepth]);
		int[] arraySource=(arrayArraysSources[currentDepth]);
		int[] arrayDestination=(arrayArraysDestination[currentDepth]);
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allWhitePieces=GetAllWhitePieces();
		int numberOfMoves=getMovesForCurrentPlayerWithFreeMovesForKing(arrayPiecesTypes,arraySource,arrayDestination,allBlackPieces,allWhitePieces);
		int typeOfEventualyDeletedPiece=noPieceId;
		if(getCurrentTurn()==white)
		{
			int extremum=-infinite;
			for(int counterMoves=numberOfMoves-1;counterMoves>=0;counterMoves--)
			{
				int sourceIndex=arraySource[counterMoves];
				int destinationIndex=arrayDestination[counterMoves];
				int pieceType=arrayPiecesTypes[counterMoves];
				if(allBlackPieces.get(destinationIndex)==true)
				{
					typeOfEventualyDeletedPiece=GetBlackPieceType(destinationIndex);
					DeleteBlackPieceAtThisIndexWithThisType(destinationIndex,typeOfEventualyDeletedPiece);
				}
				boolean isSpecial=doMoveFromTwoIndexWhite(pieceType,sourceIndex,destinationIndex,false);
				currentTurn=-currentTurn;
				int resultRecursiveMethod=RecursiveMethod(currentDepth-1,maximumDepth,alpha,beta);
				currentTurn=-currentTurn;
				if(resultRecursiveMethod>=extremum)
				{
					if(currentDepth==maximumDepth)
					{
						if(resultRecursiveMethod>extremum)
							numberOfBestMoves=0;
						arrayBestSource[numberOfBestMoves]=sourceIndex;
						arrayBestDestination[numberOfBestMoves++]=destinationIndex;
					}
					extremum=resultRecursiveMethod;
				}
				doMoveFromTwoIndexWhite(pieceType,destinationIndex,sourceIndex,isSpecial);
				if(typeOfEventualyDeletedPiece!=noPieceId)
				{
					SetBlackPiece(typeOfEventualyDeletedPiece,destinationIndex);
					typeOfEventualyDeletedPiece=noPieceId;
				}
				if(beta<extremum) // very important strict comparison to get all potential interesting moves
					return extremum;
				alpha=Math.max(alpha,extremum);
			}
			return extremum;
		}
		else
		{
			int extremum=infinite;
			for(int counterMoves=numberOfMoves-1;counterMoves>=0;counterMoves--)
			{
				int sourceIndex=arraySource[counterMoves];
				int destinationIndex=arrayDestination[counterMoves];
				int pieceType=arrayPiecesTypes[counterMoves];
				if(allWhitePieces.get(destinationIndex)==true)
				{
					typeOfEventualyDeletedPiece=GetWhitePieceType(destinationIndex);
					DeleteWhitePieceAtThisIndexWithThisType(destinationIndex,typeOfEventualyDeletedPiece);
				}
				boolean isSpecial=doMoveFromTwoIndexBlack(pieceType,sourceIndex,destinationIndex,false);
				currentTurn=-currentTurn;
				int resultRecursiveMethod=RecursiveMethod(currentDepth-1,maximumDepth,alpha,beta);
				currentTurn=-currentTurn;
				if(resultRecursiveMethod<=extremum)
				{
					if(currentDepth==maximumDepth)
					{
						if(resultRecursiveMethod<extremum)
							numberOfBestMoves=0;
						arrayBestSource[numberOfBestMoves]=sourceIndex;
						arrayBestDestination[numberOfBestMoves++]=destinationIndex;
					}
					extremum=resultRecursiveMethod;
				}
				doMoveFromTwoIndexBlack(pieceType,destinationIndex,sourceIndex,isSpecial);
				if(typeOfEventualyDeletedPiece!=noPieceId)
				{
					SetWhitePiece(typeOfEventualyDeletedPiece,destinationIndex);
					typeOfEventualyDeletedPiece=noPieceId;
				}
				if(alpha>extremum)
					return extremum;
				beta=Math.min(beta,extremum);
			}
			return extremum;
		}
	}
	
	public void doMoveFromTwoBitSets(BitSet bitSetSource,BitSet bitSetDestination,BitSet allBlackPieces,BitSet allWhitePieces)
	{
		if(allBlackPieces.get(bitSetSource.nextSetBit(0))==true)
		{
			SetBlackPiece(GetBlackPieceType(bitSetSource.nextSetBit(0)),bitSetDestination.nextSetBit(0));
			DeleteBlackPieceAtThisIndex(bitSetSource.nextSetBit(0));
			DeleteWhitePieceAtThisIndex(bitSetDestination.nextSetBit(0));
		}
		else if(allWhitePieces.get(bitSetSource.nextSetBit(0))==true)
		{
			SetWhitePiece(GetWhitePieceType(bitSetSource.nextSetBit(0)),bitSetDestination.nextSetBit(0));
			DeleteWhitePieceAtThisIndex(bitSetSource.nextSetBit(0));
			DeleteBlackPieceAtThisIndex(bitSetDestination.nextSetBit(0));
		}
	}
	
	// we delete moves that are under the three moves repetition
	public int getMovesForCurrentPlayerWithDeletionAccordingToThreeRepetitionLaw(int[] arrayType,int[] arraySource,int[] arrayDestination,BitSet allBlackPieces,BitSet allWhitePieces)
	{
		int numberOfPossibleMoves=getMovesForCurrentPlayerWithoutFreeMovesForKing(arrayType,arraySource,arrayDestination,allBlackPieces,allWhitePieces);
		for(int counterPossibleMoves=numberOfPossibleMoves-1;counterPossibleMoves>=0;counterPossibleMoves--)
			if(IsThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(currentTurn,arrayType[counterPossibleMoves],arraySource[counterPossibleMoves],arrayDestination[counterPossibleMoves])==true)
			{
				numberOfPossibleMoves--;
				for(int counterPossibleMovesTemp=counterPossibleMoves;counterPossibleMovesTemp<numberOfPossibleMoves;counterPossibleMovesTemp++)
				{
					arrayType[counterPossibleMovesTemp]=arrayType[counterPossibleMovesTemp+1];
					arraySource[counterPossibleMovesTemp]=arraySource[counterPossibleMovesTemp+1];
					arrayDestination[counterPossibleMovesTemp]=arrayDestination[counterPossibleMovesTemp+1];
				}
			}
		return numberOfPossibleMoves;
	}
	
	public int getMovesForCurrentPlayerWithoutFreeMovesForKing(int arrayPiecesTypes[],int arrayIndexSources[],int arrayIndexDestinations[],BitSet allBlackPieces,BitSet allWhitePieces)
	{
		int counterTotalMoves=0;
		BitSet possiblesMoves;
		BitSet allPieces=(BitSet)allBlackPieces.clone();
		allPieces.or(allWhitePieces);
		int indexSource;
		counterTotalMoves+=getMovesForCurrentPlayerWithNailedPieces(arrayPiecesTypes,arrayIndexSources,arrayIndexDestinations,allBlackPieces,allWhitePieces);
		if(getCurrentTurn()==white)
		{
			indexSource=-1;
			for(int counterPieces=whiteKing.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteKing.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKingWithCheckChecking(whiteKing.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=kingId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
		}
		else
		{
			indexSource=-1;
			for(int counterPieces=blackKing.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackKing.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKingWithCheckChecking(blackKing.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=kingId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
		}
		return counterTotalMoves;
	}
	
	public int getMovesForCurrentPlayerWithFreeMovesForKing(int arrayPiecesTypes[],int arrayIndexSources[],int arrayIndexDestinations[],BitSet allBlackPieces,BitSet allWhitePieces)
	{
		int counterTotalMoves=0;
		BitSet possiblesMoves;
		BitSet allPieces=(BitSet)allBlackPieces.clone();
		allPieces.or(allWhitePieces);
		int indexSource;
		counterTotalMoves+=getMovesForCurrentPlayer(arrayPiecesTypes,arrayIndexSources,arrayIndexDestinations,allBlackPieces,allWhitePieces);
		if(getCurrentTurn()==white)
		{
			indexSource=-1;
			for(int counterPieces=whiteKing.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteKing.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKing(whiteKing.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=kingId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
		}
		else
		{
			indexSource=-1;
			for(int counterPieces=blackKing.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackKing.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKing(blackKing.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=kingId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
		}
		return counterTotalMoves;
	}
	
	public int getMovesForCurrentPlayerWithNailedPieces(int arrayPiecesTypes[],int arrayIndexSources[],int arrayIndexDestinations[],BitSet blackPieces,BitSet whitePieces)
	{
		int arrayPiecesTypesWithoutNailed[]=new int[maximumPossibleMoves];
		int arrayIndexSourcesWithoutNailed[]=new int[maximumPossibleMoves];
		int arrayIndexDestinationsWithoutNailed[]=new int[maximumPossibleMoves];
		int counterTotalMovesBeforeNailed=getMovesForCurrentPlayer(arrayPiecesTypesWithoutNailed,arrayIndexSourcesWithoutNailed,arrayIndexDestinationsWithoutNailed,blackPieces,whitePieces);
		int counterTotalMovesAfterNailed=0;
		for(int counterMovesBeforeNailed=0;counterMovesBeforeNailed<counterTotalMovesBeforeNailed;counterMovesBeforeNailed++)
		{
			if(counterMovesBeforeNailed==0||arrayIndexSourcesWithoutNailed[counterMovesBeforeNailed]!=arrayIndexSourcesWithoutNailed[counterMovesBeforeNailed-1])
			{
				ArrayList<Point> arrayPoint=GetListOfPossibleMovesForAPieceWithCheckChecking(arrayIndexSourcesWithoutNailed[counterMovesBeforeNailed]);
				for(int counterMovesAfterNailed=0;counterMovesAfterNailed<arrayPoint.size();counterMovesAfterNailed++)
				{
					arrayPiecesTypes[counterTotalMovesAfterNailed]=arrayPiecesTypesWithoutNailed[counterMovesBeforeNailed];
					arrayIndexSources[counterTotalMovesAfterNailed]=arrayIndexSourcesWithoutNailed[counterMovesBeforeNailed];
					arrayIndexDestinations[counterTotalMovesAfterNailed]=arrayPoint.get(counterMovesAfterNailed).x+arrayPoint.get(counterMovesAfterNailed).y*numberOfSquarePerLine;
					counterTotalMovesAfterNailed++;
				}
			}
		}
		return counterTotalMovesAfterNailed;
	}
	
	public int getMovesForCurrentPlayer(int arrayPiecesTypes[],int arrayIndexSources[],int arrayIndexDestinations[],BitSet allBlackPieces,BitSet allWhitePieces)
	{
		int counterTotalMoves=0;
		BitSet possiblesMoves;
		BitSet allPieces=(BitSet)allBlackPieces.clone();
		allPieces.or(allWhitePieces);
		int indexSource;
		if(getCurrentTurn()==white)
		{
			indexSource=-1;
			for(int counterPieces=whitePawns.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whitePawns.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForAWhitePawn(whitePawns.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=pawnId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteQueen.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteQueen.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForQueen(whiteQueen.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=queenId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteRooks.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteRooks.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForRook(whiteRooks.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=rookId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteBishops.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteBishops.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForBishop(whiteBishops.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=bishopId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteKnights.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=whiteKnights.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKnight(whiteKnights.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=knightId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			
		}
		else
		{
			indexSource=-1;
			for(int counterPieces=blackPawns.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackPawns.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForABlackPawn(blackPawns.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=pawnId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=blackQueen.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackQueen.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForQueen(blackQueen.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=queenId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=blackRooks.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackRooks.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForRook(blackRooks.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=rookId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=blackBishops.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackBishops.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForBishop(blackBishops.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=bishopId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
			indexSource=-1;
			for(int counterPieces=blackKnights.cardinality();counterPieces>0;counterPieces--)
			{
				indexSource=blackKnights.nextSetBit(indexSource+1);
				possiblesMoves=getMovesForKnight(blackKnights.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					arrayPiecesTypes[counterTotalMoves]=knightId;
					arrayIndexSources[counterTotalMoves]=indexSource;
					arrayIndexDestinations[counterTotalMoves++]=indexDestination;
				}
			}
		}
		return counterTotalMoves;
	}
	
	public void getMovesForCurrentPlayerWithFreeMovesForKing(ArrayList<BitSet> listSourceBitsets,ArrayList<BitSet> listDestinationBitsets)
	{
		BitSet possiblesMoves;
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allWhitePieces=GetAllWhitePieces();
		BitSet allPieces=(BitSet)allBlackPieces.clone();
		allPieces.or(allWhitePieces);
		BitSet destinationBitset=new BitSet();
		int indexSource=-1;
		if(getCurrentTurn()==white)
		{
			indexSource=-1;
			for(int counterPieces=whitePawns.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whitePawns.nextSetBit(indexSource+1);
				sourceBitset.set(whitePawns.nextSetBit(indexSource));
				possiblesMoves=getMovesForAWhitePawn(whitePawns.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteQueen.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whiteQueen.nextSetBit(indexSource+1);
				sourceBitset.set(whiteQueen.nextSetBit(indexSource));
				possiblesMoves=getMovesForQueen(whiteQueen.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteRooks.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whiteRooks.nextSetBit(indexSource+1);
				sourceBitset.set(whiteRooks.nextSetBit(indexSource));
				possiblesMoves=getMovesForRook(whiteRooks.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteBishops.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whiteBishops.nextSetBit(indexSource+1);
				sourceBitset.set(whiteBishops.nextSetBit(indexSource));
				possiblesMoves=getMovesForBishop(whiteBishops.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteKnights.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whiteKnights.nextSetBit(indexSource+1);
				sourceBitset.set(whiteKnights.nextSetBit(indexSource));
				possiblesMoves=getMovesForKnight(whiteKnights.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=whiteKing.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=whiteKing.nextSetBit(indexSource+1);
				sourceBitset.set(whiteKing.nextSetBit(indexSource));
				possiblesMoves=getMovesForKing(whiteKing.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
		}
		else
		{
			indexSource=-1;
			for(int counterPieces=blackPawns.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackPawns.nextSetBit(indexSource+1);
				sourceBitset.set(blackPawns.nextSetBit(indexSource));
				possiblesMoves=getMovesForABlackPawn(blackPawns.nextSetBit(indexSource),allWhitePieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=blackQueen.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackQueen.nextSetBit(indexSource+1);
				sourceBitset.set(blackQueen.nextSetBit(indexSource));
				possiblesMoves=getMovesForQueen(blackQueen.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=blackRooks.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackRooks.nextSetBit(indexSource+1);
				sourceBitset.set(blackRooks.nextSetBit(indexSource));
				possiblesMoves=getMovesForRook(blackRooks.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=blackBishops.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackBishops.nextSetBit(indexSource+1);
				sourceBitset.set(blackBishops.nextSetBit(indexSource));
				possiblesMoves=getMovesForBishop(blackBishops.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=blackKnights.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackKnights.nextSetBit(indexSource+1);
				sourceBitset.set(blackKnights.nextSetBit(indexSource));
				possiblesMoves=getMovesForKnight(blackKnights.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
			indexSource=-1;
			for(int counterPieces=blackKing.cardinality();counterPieces>0;counterPieces--)
			{
				BitSet sourceBitset=new BitSet();
				indexSource=blackKing.nextSetBit(indexSource+1);
				sourceBitset.set(blackKing.nextSetBit(indexSource));
				possiblesMoves=getMovesForKing(blackKing.nextSetBit(indexSource),allBlackPieces,allPieces);
				int indexDestination=-1;
				for(int counterMovesFirstLevel=possiblesMoves.cardinality();counterMovesFirstLevel>0;counterMovesFirstLevel--)
				{
					destinationBitset=new BitSet();
					indexDestination=possiblesMoves.nextSetBit(indexDestination+1);
					destinationBitset.set(indexDestination);
					listDestinationBitsets.add((BitSet)destinationBitset.clone());
					listSourceBitsets.add((BitSet)sourceBitset.clone());
				}
			}
		}
	}
	
	BitSet getAllPieces()
	{
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allWhitePieces=GetAllWhitePieces();
		BitSet allPieces=(BitSet)allBlackPieces.clone();
		allPieces.or(allWhitePieces);
		return allPieces;
	}
	
	private void deletePieceAtThisIndexWithThisType(int pieceIndex,int pieceType)
	{
		if(currentTurn==white)
			DeleteBlackPieceAtThisIndexWithThisType(pieceIndex,pieceType);
		else
			DeleteWhitePieceAtThisIndexWithThisType(pieceIndex,pieceType);
	}
	
	// the main computation function : each thread will compute its part
	@Override
	public void run()
	{
		// for each move we compute its part : two levels
		for(int counterMovesFirstLevel=beginSourceDestinationForThreadComputing;counterMovesFirstLevel<endSourceDestinationForThreadComputing;counterMovesFirstLevel++)
		{
			int typeOfEventualyDeletedPieceFirstLevel=GetPieceTypeAtThisIndexAndWithThisColor(-currentTurn,arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel]);
			boolean isSpecialFirstLevel=doMoveFromTwoIndex(arrayPieceTypeForThreadComputingFirstLevel[counterMovesFirstLevel],arraySourceForThreadComputingFirstLevel[counterMovesFirstLevel],arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel],false);
			if(typeOfEventualyDeletedPieceFirstLevel!=noPieceId)
				deletePieceAtThisIndexWithThisType(arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel],typeOfEventualyDeletedPieceFirstLevel);
			currentTurn=-currentTurn;
			int typeOfEventualyDeletedPieceSecondLevel=GetPieceTypeAtThisIndexAndWithThisColor(-currentTurn,arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel]);
			boolean isSpecialSecondLevel=doMoveFromTwoIndex(arrayPieceTypeForThreadComputingSecondLevel[counterMovesFirstLevel],arraySourceForThreadComputingSecondLevel[counterMovesFirstLevel],arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel],false);
			if(typeOfEventualyDeletedPieceSecondLevel!=noPieceId)
				deletePieceAtThisIndexWithThisType(arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel],typeOfEventualyDeletedPieceSecondLevel);
			int alpha=-infinite;
			int beta=-alpha;
			currentTurn=-currentTurn;
			int returnValue=RecursiveMethod(depthForThreadComputing,depthForThreadComputing,alpha,beta);
			currentTurn=-currentTurn;
			arrayValuesForThreadComputingSecondLevel[counterMovesFirstLevel]=returnValue;
			doMoveFromTwoIndex(arrayPieceTypeForThreadComputingSecondLevel[counterMovesFirstLevel],arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel],arraySourceForThreadComputingSecondLevel[counterMovesFirstLevel],isSpecialSecondLevel);
			if(currentTurn==white)
			{
				if(typeOfEventualyDeletedPieceSecondLevel!=noPieceId)
					SetBlackPiece(typeOfEventualyDeletedPieceSecondLevel,arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel]);
			}
			else
			{
				if(typeOfEventualyDeletedPieceSecondLevel!=noPieceId)
					SetWhitePiece(typeOfEventualyDeletedPieceSecondLevel,arrayDestinationForThreadComputingSecondLevel[counterMovesFirstLevel]);
			}
			currentTurn=-currentTurn;
			doMoveFromTwoIndex(arrayPieceTypeForThreadComputingFirstLevel[counterMovesFirstLevel],arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel],arraySourceForThreadComputingFirstLevel[counterMovesFirstLevel],isSpecialFirstLevel);
			if(currentTurn==white)
			{
				if(typeOfEventualyDeletedPieceFirstLevel!=noPieceId)
					SetBlackPiece(typeOfEventualyDeletedPieceFirstLevel,arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel]);
			}
			else
			{
				if(typeOfEventualyDeletedPieceFirstLevel!=noPieceId)
					SetWhitePiece(typeOfEventualyDeletedPieceFirstLevel,arrayDestinationForThreadComputingFirstLevel[counterMovesFirstLevel]);
			}
		}
	}
	
	public ChessRuler(int currentTurnParameter,int beginSourceDestinationParameter,int endSourceDestinationParameter,int[] arrayValuesParameter,int depthParameter,int arrayPieceTypeFirstLevelParameter[],int arraySourceFirstLevelParameter[],int arrayDestinationFirstLevelParameter[],int numberOfMoves,int arrayPieceTypeSecondLevelParameter[],int arraySourceSecondLevelParameter[],int arrayDestinationSecondLevelParameter[],BitSet whiteKnightsParameter,BitSet whiteBishopsParameter,BitSet whiteQueenParameter,BitSet whiteKingParameter,BitSet whitePawnsParameter,BitSet whiteRooksParameter,BitSet blackKnightsParameter,BitSet blackBishopsParameter,BitSet blackQueenParameter,BitSet blackKingParameter,BitSet blackPawnsParameter,BitSet blackRooksParameter)
	{
		arrayBestSource=new int[maximumPossibleMoves];
		arrayBestDestination=new int[maximumPossibleMoves];
		arrayArraysTypes=new int[deepestLevel][];
		arrayArraysSources=new int[deepestLevel][];
		arrayArraysDestination=new int[deepestLevel][];
		for(int counterDepth=0;counterDepth<deepestLevel;counterDepth++)
		{
			arrayArraysTypes[counterDepth]=new int[maximumPossibleMoves];
			arrayArraysSources[counterDepth]=new int[maximumPossibleMoves];
			arrayArraysDestination[counterDepth]=new int[maximumPossibleMoves];
		}
		evaluationsCounterForCurrentThread=0;
		whiteKnights=(BitSet)whiteKnightsParameter.clone();
		whiteBishops=(BitSet)whiteBishopsParameter.clone();
		whiteQueen=(BitSet)whiteQueenParameter.clone();
		whiteKing=(BitSet)whiteKingParameter.clone();
		whitePawns=(BitSet)whitePawnsParameter.clone();
		whiteRooks=(BitSet)whiteRooksParameter.clone();
		blackKnights=(BitSet)blackKnightsParameter.clone();
		blackBishops=(BitSet)blackBishopsParameter.clone();
		blackQueen=(BitSet)blackQueenParameter.clone();
		blackKing=(BitSet)blackKingParameter.clone();
		blackPawns=(BitSet)blackPawnsParameter.clone();
		blackRooks=(BitSet)blackRooksParameter.clone();
		beginSourceDestinationForThreadComputing=beginSourceDestinationParameter;
		endSourceDestinationForThreadComputing=endSourceDestinationParameter;
		arrayValuesForThreadComputingSecondLevel=arrayValuesParameter;
		currentTurn=currentTurnParameter;
		depthForThreadComputing=depthParameter;
		arrayPieceTypeForThreadComputingFirstLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		arraySourceForThreadComputingFirstLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		arrayDestinationForThreadComputingFirstLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		for(int counterSourceDestination=0;counterSourceDestination<numberOfMoves;counterSourceDestination++)
		{
			arrayPieceTypeForThreadComputingFirstLevel[counterSourceDestination]=arrayPieceTypeFirstLevelParameter[counterSourceDestination];
			arraySourceForThreadComputingFirstLevel[counterSourceDestination]=arraySourceFirstLevelParameter[counterSourceDestination];
			arrayDestinationForThreadComputingFirstLevel[counterSourceDestination]=arrayDestinationFirstLevelParameter[counterSourceDestination];
		}
		arrayPieceTypeForThreadComputingSecondLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		arraySourceForThreadComputingSecondLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		arrayDestinationForThreadComputingSecondLevel=new int[maximumPossibleMoves*maximumPossibleMoves];
		for(int counterSourceDestination=0;counterSourceDestination<numberOfMoves;counterSourceDestination++)
		{
			arrayPieceTypeForThreadComputingSecondLevel[counterSourceDestination]=arrayPieceTypeSecondLevelParameter[counterSourceDestination];
			arraySourceForThreadComputingSecondLevel[counterSourceDestination]=arraySourceSecondLevelParameter[counterSourceDestination];
			arrayDestinationForThreadComputingSecondLevel[counterSourceDestination]=arrayDestinationSecondLevelParameter[counterSourceDestination];
		}
	}
	
	private boolean doMoveFromTwoIndex(int pieceType,int sourceIndex,int destinationIndex,boolean isSpecial)
	{
		if(currentTurn==white)
			return doMoveFromTwoIndexWhite(pieceType,sourceIndex,destinationIndex,isSpecial);
		return doMoveFromTwoIndexBlack(pieceType,sourceIndex,destinationIndex,isSpecial);
	}
	
	private void playComputerAtLevelOne(ArrayList<String> listMoveDescription,ArrayList<Point> listPointSource,ArrayList<Point> listPointDestination,ArrayList<Boolean> arrayIsSpecial)
	{
		int beta=infinite;
		int alpha=-beta;
		RecursiveMethodWithCheckChecking(1,1,alpha,beta);
		listPointSource.add(new Point(arrayBestSource[0]%numberOfSquarePerLine,arrayBestSource[0]/numberOfSquarePerLine));
		listPointDestination.add(new Point(arrayBestDestination[0]%numberOfSquarePerLine,arrayBestDestination[0]/numberOfSquarePerLine));
		doThisMoveAndGetDescription(listPointSource.get(0),listPointDestination.get(0),listMoveDescription,arrayIsSpecial);
	}
	
	// play the computer with multithreading, manage of the computer play, calling the recursive method
	public void playComputerWithMultiThread(int maximumDepth,ArrayList<String> listMoveDescription,ArrayList<Point> listPointSource,ArrayList<Point> listPointDestination,ArrayList<Boolean> arrayIsSpecial) throws InterruptedException
	{
		int arrayTypeFirstLevel[]=new int[maximumPossibleMoves];
		int arraySourceFirstLevel[]=new int[maximumPossibleMoves];
		int arrayDestinationFirstLevel[]=new int[maximumPossibleMoves];
		int arrayTypeFirstLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		int arraySourceFirstLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		int arrayDestinationFirstLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		int arrayTypeSecondLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		int arraySourceSecondLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		int arrayDestinationSecondtLevelForCoreComputing[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		BitSet allBlackPieces=GetAllBlackPieces();
		BitSet allWhitePieces=GetAllWhitePieces();
		
		// special treatment for depth 1
		if(maximumDepth==1)
		{
			playComputerAtLevelOne(listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial);
			return;
		}
		int numberOfMovesFirstLevel=getMovesForCurrentPlayerWithDeletionAccordingToThreeRepetitionLaw(arrayTypeFirstLevel,arraySourceFirstLevel,arrayDestinationFirstLevel,allBlackPieces,allWhitePieces);
		
		// list all the moves at the second level
		int totalOffset=0;
		for(int counterMovesFirstLevel=numberOfMovesFirstLevel-1;counterMovesFirstLevel>=0;counterMovesFirstLevel--)
		{
			int arrayTypeSecondLevel[]=new int[maximumPossibleMoves];
			int arraySourceSecondLevel[]=new int[maximumPossibleMoves];
			int arrayDestinationSecondLevel[]=new int[maximumPossibleMoves];
			int typeOfEventualyDeletedPiece=GetPieceTypeAtThisIndexAndWithThisColor(-currentTurn,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			boolean isSpecialFirstLevel=doMoveFromTwoIndex(arrayTypeFirstLevel[counterMovesFirstLevel],arraySourceFirstLevel[counterMovesFirstLevel],arrayDestinationFirstLevel[counterMovesFirstLevel],false);
			if(typeOfEventualyDeletedPiece!=noPieceId)
				deletePieceAtThisIndexWithThisType(arrayDestinationFirstLevel[counterMovesFirstLevel],typeOfEventualyDeletedPiece);
			currentTurn=-currentTurn;
			allBlackPieces=GetAllBlackPieces();
			allWhitePieces=GetAllWhitePieces();
			int numberOfMovesSecondLevel=getMovesForCurrentPlayerWithFreeMovesForKing(arrayTypeSecondLevel,arraySourceSecondLevel,arrayDestinationSecondLevel,allBlackPieces,allWhitePieces);
			currentTurn=-currentTurn;
			for(int counterMovesSecondLevel=numberOfMovesSecondLevel-1;counterMovesSecondLevel>=0;counterMovesSecondLevel--,totalOffset++)
			{
				arrayTypeFirstLevelForCoreComputing[totalOffset]=arrayTypeFirstLevel[counterMovesFirstLevel];
				arraySourceFirstLevelForCoreComputing[totalOffset]=arraySourceFirstLevel[counterMovesFirstLevel];
				arrayDestinationFirstLevelForCoreComputing[totalOffset]=arrayDestinationFirstLevel[counterMovesFirstLevel];
				arrayTypeSecondLevelForCoreComputing[totalOffset]=arrayTypeSecondLevel[counterMovesSecondLevel];
				arraySourceSecondLevelForCoreComputing[totalOffset]=arraySourceSecondLevel[counterMovesSecondLevel];
				arrayDestinationSecondtLevelForCoreComputing[totalOffset]=arrayDestinationSecondLevel[counterMovesSecondLevel];
			}
			doMoveFromTwoIndex(arrayTypeFirstLevel[counterMovesFirstLevel],arrayDestinationFirstLevel[counterMovesFirstLevel],arraySourceFirstLevel[counterMovesFirstLevel],isSpecialFirstLevel);
			if(currentTurn==white)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetBlackPiece(typeOfEventualyDeletedPiece,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			}
			else if(currentTurn==black)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetWhitePiece(typeOfEventualyDeletedPiece,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			}
		}
		
		// make treatment for each thread
		int numberOfCores=Runtime.getRuntime().availableProcessors();
		if(numberOfMovesFirstLevel<numberOfCores)
			numberOfCores=numberOfMovesFirstLevel;
		ArrayList<ChessRuler> listChessRuler=new ArrayList<ChessRuler>();
		ArrayList<Thread> listThread=new ArrayList<Thread>();
		int remainPossibleMoves=totalOffset;
		int arrayValuesSecondLevel[]=new int[maximumPossibleMoves*maximumPossibleMoves];
		
		for(int counterSizeListValues=0;counterSizeListValues<maximumPossibleMoves*maximumPossibleMoves;counterSizeListValues++)
			arrayValuesSecondLevel[counterSizeListValues]=evaluationThatNeverHappened;
		
		// do treatment for each core	    
		for(int counterCore=numberOfCores;counterCore>0;counterCore--)
		{
			float currentMovesFloat=remainPossibleMoves/counterCore;
			int currentMoves=(int)currentMovesFloat;
			if(currentMovesFloat>0)
			{
				if(currentMoves==0)
					currentMoves=remainPossibleMoves;
			}
			else
				break;
			remainPossibleMoves=remainPossibleMoves-currentMoves;
			ChessRuler instanceChessRulesMan=new ChessRuler(currentTurn,remainPossibleMoves,remainPossibleMoves+currentMoves,arrayValuesSecondLevel,maximumDepth-2,arrayTypeFirstLevelForCoreComputing,arraySourceFirstLevelForCoreComputing,arrayDestinationFirstLevelForCoreComputing,totalOffset,arrayTypeSecondLevelForCoreComputing,arraySourceSecondLevelForCoreComputing,arrayDestinationSecondtLevelForCoreComputing,whiteKnights,whiteBishops,whiteQueen,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueen,blackKing,blackPawns,blackRooks);
			listChessRuler.add(instanceChessRulesMan);
			Thread thread=new Thread(instanceChessRulesMan);
			listThread.add(thread);
			thread.start();
		}
		
		// we wait all threads done their work and count the number of evaluations
		for(int counterThread=0;counterThread<numberOfCores;counterThread++)
		{
			listThread.get(counterThread).join();
			totalCounterEvaluation+=listChessRuler.get(counterThread).evaluationsCounterForCurrentThread;
		}
		
		// now we have to create values for the first level
		int listValuesFirstLevel[]=new int[numberOfMovesFirstLevel];
		for(int counterFirstLevel=0;counterFirstLevel<numberOfMovesFirstLevel;counterFirstLevel++)
			listValuesFirstLevel[counterFirstLevel]=evaluationThatNeverHappened;
		
		int indexForValuesInSecondLevel=0;
		for(int counterMovesFirstLevel=numberOfMovesFirstLevel-1;counterMovesFirstLevel>=0;counterMovesFirstLevel--)
		{
			int arrayPieceTypeSecondLevelTemp[]=new int[maximumPossibleMoves];
			int arraySourceSecondLevelTemp[]=new int[maximumPossibleMoves];
			int arrayDestinationSecondLevelTemp[]=new int[maximumPossibleMoves];
			int typeOfEventualyDeletedPiece=GetPieceTypeAtThisIndexAndWithThisColor(-currentTurn,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			boolean isSpecialFirstLevel=doMoveFromTwoIndex(arrayTypeFirstLevel[counterMovesFirstLevel],arraySourceFirstLevel[counterMovesFirstLevel],arrayDestinationFirstLevel[counterMovesFirstLevel],false);
			if(typeOfEventualyDeletedPiece!=noPieceId)
				deletePieceAtThisIndexWithThisType(arrayDestinationFirstLevel[counterMovesFirstLevel],typeOfEventualyDeletedPiece);
			currentTurn=-currentTurn;
			allBlackPieces=GetAllBlackPieces();
			allWhitePieces=GetAllWhitePieces();
			int numberOfMovesSecondLevel=getMovesForCurrentPlayerWithFreeMovesForKing(arrayPieceTypeSecondLevelTemp,arraySourceSecondLevelTemp,arrayDestinationSecondLevelTemp,allBlackPieces,allWhitePieces);
			
			// before all we get the best extreme at second level for the first level and fit the first value with it
			int bestExtreme=0;
			if(currentTurn==white)
			{
				bestExtreme=-infinite;
				for(int counterMovesSecondLevel=0;counterMovesSecondLevel<numberOfMovesSecondLevel;counterMovesSecondLevel++)
					if(arrayValuesSecondLevel[indexForValuesInSecondLevel+counterMovesSecondLevel]>bestExtreme)
						bestExtreme=arrayValuesSecondLevel[indexForValuesInSecondLevel+counterMovesSecondLevel];
				listValuesFirstLevel[counterMovesFirstLevel]=bestExtreme;
			}
			else if(currentTurn==black)
			{
				bestExtreme=infinite;
				for(int counterMovesSecondLevel=0;counterMovesSecondLevel<numberOfMovesSecondLevel;counterMovesSecondLevel++)
					if(arrayValuesSecondLevel[indexForValuesInSecondLevel+counterMovesSecondLevel]<bestExtreme)
						bestExtreme=arrayValuesSecondLevel[indexForValuesInSecondLevel+counterMovesSecondLevel];
				listValuesFirstLevel[counterMovesFirstLevel]=bestExtreme;
			}
			indexForValuesInSecondLevel+=numberOfMovesSecondLevel; //we shift the index for second level values
			currentTurn=-currentTurn;
			doMoveFromTwoIndex(arrayTypeFirstLevel[counterMovesFirstLevel],arrayDestinationFirstLevel[counterMovesFirstLevel],arraySourceFirstLevel[counterMovesFirstLevel],isSpecialFirstLevel);
			if(currentTurn==white)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetBlackPiece(typeOfEventualyDeletedPiece,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			}
			else if(currentTurn==black)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetWhitePiece(typeOfEventualyDeletedPiece,arrayDestinationFirstLevel[counterMovesFirstLevel]);
			}
		}
		
		// we get the best extreme value found
		int bestExtremeFound=infinite;
		if(currentTurn==white)
		{
			bestExtremeFound=-infinite;
			for(int counterBestValue=0;counterBestValue<numberOfMovesFirstLevel;counterBestValue++)
				if(listValuesFirstLevel[counterBestValue]>bestExtremeFound&&listValuesFirstLevel[counterBestValue]!=evaluationThatNeverHappened)
					bestExtremeFound=listValuesFirstLevel[counterBestValue];
		}
		else
		{
			for(int counterBestValue=0;counterBestValue<numberOfMovesFirstLevel;counterBestValue++)
				if(listValuesFirstLevel[counterBestValue]<bestExtremeFound&&listValuesFirstLevel[counterBestValue]!=evaluationThatNeverHappened)
					bestExtremeFound=listValuesFirstLevel[counterBestValue];
		}
		int arrayBestSourceOriginal[]=new int[maximumPossibleMoves];
		int arrayBestDestinationOriginal[]=new int[maximumPossibleMoves];
		int numberOfBestMovesOriginal=0;
		for(int counterBestValue=0;counterBestValue<numberOfMovesFirstLevel;counterBestValue++)
			if(bestExtremeFound==listValuesFirstLevel[counterBestValue])
			{
				arrayBestSourceOriginal[numberOfBestMovesOriginal]=arraySourceFirstLevel[counterBestValue];
				arrayBestDestinationOriginal[numberOfBestMovesOriginal++]=arrayDestinationFirstLevel[counterBestValue];
			}
		
		// now we have to delete move that will put in pat 
		int counterBestMoves=0;
		boolean arrayIndexToBeDeleted[]=new boolean[maximumPossibleMoves];
		for(int counter=0;counter<maximumPossibleMoves;counter++)
			arrayIndexToBeDeleted[counter]=false;
		int counterMovesDeleted=0;
		for(;counterBestMoves<numberOfBestMovesOriginal;counterBestMoves++)
		{
			int typeOfSourcePiece=GetPieceTypeAtThisIndexAndWithThisColor(currentTurn,arrayBestSourceOriginal[counterBestMoves]);
			int typeOfEventualyDeletedPiece=GetPieceTypeAtThisIndexAndWithThisColor(-currentTurn,arrayBestDestinationOriginal[counterBestMoves]);
			boolean isSpecialFirstLevel=doMoveFromTwoIndex(typeOfSourcePiece,arrayBestSourceOriginal[counterBestMoves],arrayBestDestinationOriginal[counterBestMoves],false);
			ChangePlayerTurn();
			int winner=ifGameHasEndedGiveMeTheWinner();
			ChangePlayerTurn();
			if(winner!=0)
			{
				switch(winner)
				{
				case whiteIsPat:
				case blackIsPat:
					if(Evaluate()*currentTurn>0)
					{
						counterMovesDeleted++;
						arrayIndexToBeDeleted[counterBestMoves]=true;
					}
				default:
					;
				}
			}
			doMoveFromTwoIndex(typeOfSourcePiece,arrayBestDestinationOriginal[counterBestMoves],arrayBestSourceOriginal[counterBestMoves],isSpecialFirstLevel);
			if(currentTurn==white)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetBlackPiece(typeOfEventualyDeletedPiece,arrayBestDestinationOriginal[counterBestMoves]);
			}
			else if(currentTurn==black)
			{
				if(typeOfEventualyDeletedPiece!=noPieceId)
					SetWhitePiece(typeOfEventualyDeletedPiece,arrayBestDestinationOriginal[counterBestMoves]);
			}
		}
		if(numberOfBestMovesOriginal!=counterMovesDeleted) // we delete only if there a least one issue, if not it's a deseperate situation
		{
			for(int counterMoves=0;counterMoves<maximumPossibleMoves;counterMoves++)
				if(arrayIndexToBeDeleted[counterMoves]==true)
					for(int counterBestMoveDeletion=counterMoves;counterBestMoveDeletion<maximumPossibleMoves-1;counterBestMoveDeletion++)
					{
						arrayIndexToBeDeleted[counterBestMoveDeletion]=arrayIndexToBeDeleted[counterBestMoveDeletion+1];
						arrayBestSourceOriginal[counterBestMoveDeletion]=arrayBestSourceOriginal[counterBestMoveDeletion+1];
						arrayBestDestinationOriginal[counterBestMoveDeletion]=arrayBestDestinationOriginal[counterBestMoveDeletion+1];
					}
			numberOfBestMovesOriginal-=counterMovesDeleted;
		}
		
		// we filter according to lower levels
		int numberOfBestMovesBeforeTreatment=numberOfBestMovesOriginal;
		for(int counterDepth=1;counterDepth<maximumDepth;counterDepth++)
		{
			if(numberOfBestMovesBeforeTreatment>1) // if there are multiple choices we have to refine the decision 
			{
				boolean arrayBestIndex[]=new boolean[maximumPossibleMoves];
				int arrayBestSourceFinal[]=new int[maximumPossibleMoves];
				int arrayBestDestinationFinal[]=new int[maximumPossibleMoves];
				for(int counterBestIndex=0;counterBestIndex<numberOfBestMovesBeforeTreatment;counterBestIndex++)
					arrayBestIndex[counterBestIndex]=false;
				int beta=infinite;
				int alpha=-beta;
				
				RecursiveMethod(counterDepth,counterDepth,alpha,beta);
				for(int counterMovesMaximumDepth=numberOfBestMovesBeforeTreatment-1;counterMovesMaximumDepth>=0;counterMovesMaximumDepth--)
					for(int counterMovesForLowerDepth=0;counterMovesForLowerDepth<numberOfBestMoves;counterMovesForLowerDepth++)
						if(arrayBestSourceOriginal[counterMovesMaximumDepth]==arrayBestSource[counterMovesForLowerDepth]&&arrayBestDestinationOriginal[counterMovesMaximumDepth]==arrayBestDestination[counterMovesForLowerDepth])
							arrayBestIndex[counterMovesMaximumDepth]=true;
				int counterGoodIndexesForTestingIfAtLeastOneMove=0;
				for(;counterGoodIndexesForTestingIfAtLeastOneMove<numberOfBestMovesBeforeTreatment;counterGoodIndexesForTestingIfAtLeastOneMove++)
					if(arrayBestIndex[counterGoodIndexesForTestingIfAtLeastOneMove]==true)
						break;
					
				if(counterGoodIndexesForTestingIfAtLeastOneMove<numberOfBestMovesBeforeTreatment)
				{
					int numberOfBestMovesTemp=0;
					for(int counterGoodIndexes=0;counterGoodIndexes<numberOfBestMovesBeforeTreatment;counterGoodIndexes++)
						if(arrayBestIndex[counterGoodIndexes]==true)
						{
							arrayBestSourceFinal[numberOfBestMovesTemp]=arrayBestSourceOriginal[counterGoodIndexes];
							arrayBestDestinationFinal[numberOfBestMovesTemp++]=arrayBestDestinationOriginal[counterGoodIndexes];
						}
					numberOfBestMovesBeforeTreatment=0;
					for(int counteFinalMoves=0;counteFinalMoves<numberOfBestMovesTemp;counteFinalMoves++)
					{
						arrayBestSourceOriginal[counteFinalMoves]=arrayBestSourceFinal[counteFinalMoves];
						arrayBestDestinationOriginal[counteFinalMoves]=arrayBestDestinationFinal[counteFinalMoves];
					}
					numberOfBestMovesBeforeTreatment=numberOfBestMovesTemp;
				}
			}
		}
		listPointSource.add(new Point(arrayBestSourceOriginal[0]%numberOfSquarePerLine,arrayBestSourceOriginal[0]/numberOfSquarePerLine));
		listPointDestination.add(new Point(arrayBestDestinationOriginal[0]%numberOfSquarePerLine,arrayBestDestinationOriginal[0]/numberOfSquarePerLine));
		doThisMoveAndGetDescription(listPointSource.get(0),listPointDestination.get(0),listMoveDescription,arrayIsSpecial);
	}
	
	// use for redo feature
	public void doThisMoveAndGetDescription(String moveDescription,ArrayList<String> arrayMoveDescription,ArrayList<Boolean> arrayIsSpecial)
	{
		if(getCurrentTurn()==white)
		{
			// now we have to find the source of the moves
			BitSet bitsetCurrentPieces=null;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				bitsetCurrentPieces=whiteKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				bitsetCurrentPieces=whiteRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				bitsetCurrentPieces=whiteBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				bitsetCurrentPieces=whiteQueen;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				bitsetCurrentPieces=whiteKing;
			if(bitsetCurrentPieces==null)
				bitsetCurrentPieces=whitePawns;
			if(moveDescription.indexOf(kingSideCastlingStandard)!=-1) //	castling management
			{
				MakeCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingStandard);
				arrayIsSpecial.add(true);
			}
			else
				doThisMoveAndGetDescriptionWithMoveDescription(bitsetCurrentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial);
		}
		else if(getCurrentTurn()==black)
		{
			// now we have to find the source of the moves
			BitSet bitsetCurrentPieces=null;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				bitsetCurrentPieces=blackKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				bitsetCurrentPieces=blackRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				bitsetCurrentPieces=blackBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				bitsetCurrentPieces=blackQueen;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				bitsetCurrentPieces=blackKing;
			if(bitsetCurrentPieces==null)
				bitsetCurrentPieces=blackPawns;
			if(moveDescription.indexOf(kingSideCastlingStandard)!=-1) //	castling management
			{
				MakeCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingStandard);
				arrayIsSpecial.add(true);
			}
			else
				doThisMoveAndGetDescriptionWithMoveDescription(bitsetCurrentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial);
		}
	}
	
	// get all the moves that can be done to avoid check
	public BitSet getBitSetOfMovesRequiredToAvoidCheck(int kingIndex,BitSet blackPieces,BitSet whitePieces,BitSet allPieces)
	{
		blackPieces=(BitSet)blackPieces.clone();
		whitePieces=(BitSet)whitePieces.clone();
		allPieces=(BitSet)allPieces.clone();
		BitSet bitSetSquaresToBeTargeted=new BitSet();
		switch(currentTurn)
		{
		case white:
			whitePieces.clear(kingIndex); // we delete the king if a slide piece go beyond it 
			allPieces.clear(kingIndex);
			whiteKnights.set(kingIndex);
			BitSet bitSetMoves=(BitSet)getMovesForKnight(kingIndex,whitePieces,allPieces).clone();
			bitSetMoves.clear(kingIndex);
			bitSetMoves.and(blackKnights);
			whiteKnights.clear(kingIndex);
			if(bitSetMoves.cardinality()!=0)
				bitSetSquaresToBeTargeted=bitSetMoves;
			whiteBishops.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForBishop(kingIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackBishops);
			whiteBishops.clear(kingIndex);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			whiteRooks.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForRook(kingIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackRooks);
			whiteRooks.clear(kingIndex);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			whiteQueen.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForQueen(kingIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackQueen);
			bitSetMoves.clear(kingIndex);
			whiteQueen.clear(kingIndex);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted.or(bitSetMoves);
				else
					return null;
			}
			whitePawns.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForAWhitePawn(kingIndex,blackPieces,allPieces).clone();
			whitePawns.clear(kingIndex);
			bitSetMoves.and(blackPawns);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			break;
		case black:
			blackPieces.clear(kingIndex);
			allPieces.clear(kingIndex);
			blackKnights.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForKnight(kingIndex,blackPieces,allPieces).clone();
			blackKnights.clear(kingIndex);
			bitSetMoves.and(whiteKnights);
			if(bitSetMoves.cardinality()!=0)
				bitSetSquaresToBeTargeted=bitSetMoves;
			blackBishops.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForBishop(kingIndex,blackPieces,allPieces).clone();
			blackBishops.clear(kingIndex);
			bitSetMoves.and(whiteBishops);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			blackRooks.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForRook(kingIndex,blackPieces,allPieces).clone();
			blackRooks.clear(kingIndex);
			bitSetMoves.and(whiteRooks);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			blackQueen.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForQueen(kingIndex,blackPieces,allPieces).clone();
			blackQueen.clear(kingIndex);
			bitSetMoves.clear(kingIndex);
			bitSetMoves.and(whiteQueen);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted.or(bitSetMoves);
				else
					return null;
			}
			blackPawns.set(kingIndex);
			bitSetMoves=(BitSet)getMovesForABlackPawn(kingIndex,whitePieces,allPieces).clone();
			blackPawns.clear(kingIndex);
			bitSetMoves.and(whitePawns);
			if(bitSetMoves.cardinality()!=0)
			{
				if(bitSetSquaresToBeTargeted.cardinality()==0)
					bitSetSquaresToBeTargeted=bitSetMoves;
				else
					return null;
			}
			break;
		default:
			;
		}
		if(bitSetSquaresToBeTargeted.cardinality()!=0)
		{
			bitSetSquaresToBeTargeted.set(kingIndex);
			BitSet bitSetSquaresToBeTargetedWithFullLine=null;
			try
			{
				bitSetSquaresToBeTargetedWithFullLine=(BitSet)HashMapLinesAndDiagonalesForCheck.get(bitSetSquaresToBeTargeted).clone();
			}
			catch(Exception exception)
			{
				bitSetSquaresToBeTargetedWithFullLine=null;
			}
			if(bitSetSquaresToBeTargetedWithFullLine==null)
				bitSetSquaresToBeTargetedWithFullLine=(BitSet)bitSetSquaresToBeTargeted.clone();
			bitSetSquaresToBeTargetedWithFullLine.clear(kingIndex);
			return bitSetSquaresToBeTargetedWithFullLine;
		}
		return bitSetSquaresToBeTargeted;
	}
	
	// used for king's moves, to check if other king doesn't attack this square, the method without check checking is used
	public boolean isThisEmptySquareAttacked(int squareIndex,BitSet blackPieces,BitSet whitePieces,BitSet allPieces)
	{
		allPieces=(BitSet)allPieces.clone();
		switch(currentTurn)
		{
		case white:
			int indexWhiteKing=whiteKing.nextSetBit(0);
			allPieces.clear(indexWhiteKing);
			whiteKnights.set(squareIndex);
			BitSet bitSetMoves=(BitSet)getMovesForKnight(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackKnights);
			whiteKnights.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteBishops.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForBishop(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackBishops);
			whiteBishops.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteRooks.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForRook(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackRooks);
			whiteRooks.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteQueen.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForQueen(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackQueen);
			whiteQueen.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteKing.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForKing(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackKing);
			whiteKing.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whitePawns.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForAWhitePawn(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(blackPawns);
			whitePawns.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			break;
		case black:
			int indexBlackKing=blackKing.nextSetBit(0);
			allPieces.clear(indexBlackKing);
			blackKnights.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForKnight(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteKnights);
			blackKnights.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackBishops.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForBishop(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteBishops);
			blackBishops.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackRooks.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForRook(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteRooks);
			blackRooks.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackQueen.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForQueen(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteQueen);
			blackQueen.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackKing.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForKing(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteKing);
			blackKing.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackPawns.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForABlackPawn(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(whitePawns);
			blackPawns.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			break;
		default:
			;
		}
		return false;
	}
	
	// check if king is targeted 
	public boolean isKingOnCheck(int squareIndex,BitSet blackPieces,BitSet whitePieces,BitSet allPieces)
	{
		switch(currentTurn)
		{
		case white:
			whiteKnights.set(squareIndex);
			BitSet bitSetMoves=(BitSet)getMovesForKnight(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackKnights);
			whiteKnights.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteBishops.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForBishop(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackBishops);
			whiteBishops.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteRooks.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForRook(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackRooks);
			whiteRooks.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whiteQueen.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForQueen(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackQueen);
			whiteQueen.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			bitSetMoves=(BitSet)getMovesForKing(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(blackKing);
			if(bitSetMoves.cardinality()!=0)
				return true;
			whitePawns.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForAWhitePawn(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(blackPawns);
			whitePawns.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			break;
		case black:
			blackKnights.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForKnight(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteKnights);
			blackKnights.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackBishops.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForBishop(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteBishops);
			blackBishops.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackRooks.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForRook(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteRooks);
			blackRooks.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackQueen.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForQueen(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteQueen);
			blackQueen.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			bitSetMoves=(BitSet)getMovesForKing(squareIndex,blackPieces,allPieces).clone();
			bitSetMoves.and(whiteKing);
			if(bitSetMoves.cardinality()!=0)
				return true;
			blackPawns.set(squareIndex);
			bitSetMoves=(BitSet)getMovesForABlackPawn(squareIndex,whitePieces,allPieces).clone();
			bitSetMoves.and(whitePawns);
			blackPawns.clear(squareIndex);
			if(bitSetMoves.cardinality()!=0)
				return true;
			break;
		default:
			;
		}
		return false;
	}
	
	// we compute move queen at the king square after we and with sliding pieces, if result not null, we check that there is only one piece of the concerned color, this will be a nailed piece
	public BitSet getNailedPieces(BitSet blackPieces,BitSet whitePieces,BitSet allPieces)
	{
		BitSet resultForNailedPieces=new BitSet();
		BitSet bitSetMoves=null;
		BitSet noPiecesAtAll=new BitSet();
		switch(currentTurn)
		{
		case black:
			int blackKingIndex=blackKing.nextSetBit(0);
			noPiecesAtAll=new BitSet();
			// queen case
			blackQueen.set(blackKingIndex);
			bitSetMoves=(BitSet)getMovesForQueen(blackKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int whiteQueenIndex=-1;
			blackQueen.clear(blackKingIndex);
			for(int counterQueens=0;counterQueens<whiteQueen.cardinality();counterQueens++)
			{
				whiteQueenIndex=whiteQueen.nextSetBit(whiteQueenIndex+1);
				if(bitSetMoves.get(whiteQueenIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet queenAndKingBitSet=new BitSet();
					queenAndKingBitSet.set(blackKingIndex);
					queenAndKingBitSet.set(whiteQueenIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(queenAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(blackKingIndex);
					bitSetMaskPotentialNailedPiece.clear(whiteQueenIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&blackPieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(queenAndKingBitSet).clone());
						resultForNailedPieces.clear(blackKingIndex);
					}
				}
			}
			// bishop case
			blackBishops.set(blackKingIndex);
			bitSetMoves=(BitSet)getMovesForBishop(blackKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int whiteBishopIndex=-1;
			blackBishops.clear(blackKingIndex);
			for(int counterBishops=0;counterBishops<whiteBishops.cardinality();counterBishops++)
			{
				whiteBishopIndex=whiteBishops.nextSetBit(whiteBishopIndex+1);
				if(bitSetMoves.get(whiteBishopIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet bishopAndKingBitSet=new BitSet();
					bishopAndKingBitSet.set(blackKingIndex);
					bishopAndKingBitSet.set(whiteBishopIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(bishopAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(blackKingIndex);
					bitSetMaskPotentialNailedPiece.clear(whiteBishopIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&blackPieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(bishopAndKingBitSet).clone());
						resultForNailedPieces.clear(blackKingIndex);
					}
				}
			}
			// rook case
			blackRooks.set(blackKingIndex);
			bitSetMoves=(BitSet)getMovesForRook(blackKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int whiteRookIndex=-1;
			blackRooks.clear(blackKingIndex);
			for(int counterRooks=0;counterRooks<whiteRooks.cardinality();counterRooks++)
			{
				whiteRookIndex=whiteRooks.nextSetBit(whiteRookIndex+1);
				if(bitSetMoves.get(whiteRookIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet rookAndKingBitSet=new BitSet();
					rookAndKingBitSet.set(blackKingIndex);
					rookAndKingBitSet.set(whiteRookIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(rookAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(blackKingIndex);
					bitSetMaskPotentialNailedPiece.clear(whiteRookIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&blackPieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(rookAndKingBitSet).clone());
						resultForNailedPieces.clear(blackKingIndex);
					}
				}
			}
			break;
		case white:
			int whiteKingIndex=whiteKing.nextSetBit(0);
			noPiecesAtAll=new BitSet();
			// queen case
			whiteQueen.set(whiteKingIndex);
			bitSetMoves=(BitSet)getMovesForQueen(whiteKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int blackQueenIndex=-1;
			whiteQueen.clear(whiteKingIndex);
			for(int counterQueens=0;counterQueens<blackQueen.cardinality();counterQueens++)
			{
				blackQueenIndex=blackQueen.nextSetBit(blackQueenIndex+1);
				if(bitSetMoves.get(blackQueenIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet queenAndKingBitSet=new BitSet();
					queenAndKingBitSet.set(blackQueenIndex);
					queenAndKingBitSet.set(whiteKingIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(queenAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(blackQueenIndex);
					bitSetMaskPotentialNailedPiece.clear(whiteKingIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&whitePieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(queenAndKingBitSet).clone());
						resultForNailedPieces.clear(whiteKingIndex);
					}
				}
			}
			// bishop case
			whiteBishops.set(whiteKingIndex);
			bitSetMoves=(BitSet)getMovesForBishop(whiteKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int blackBishopIndex=-1;
			whiteBishops.clear(whiteKingIndex);
			for(int counterBishops=0;counterBishops<blackBishops.cardinality();counterBishops++)
			{
				blackBishopIndex=blackBishops.nextSetBit(blackBishopIndex+1);
				if(bitSetMoves.get(blackBishopIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet bishopAndKingBitSet=new BitSet();
					bishopAndKingBitSet.set(whiteKingIndex);
					bishopAndKingBitSet.set(blackBishopIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(bishopAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(whiteKingIndex);
					bitSetMaskPotentialNailedPiece.clear(blackBishopIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&whitePieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(bishopAndKingBitSet).clone());
						resultForNailedPieces.clear(whiteKingIndex);
					}
				}
			}
			// rook case
			whiteRooks.set(whiteKingIndex);
			bitSetMoves=(BitSet)getMovesForRook(whiteKingIndex,noPiecesAtAll,noPiecesAtAll).clone();
			int blackRookIndex=-1;
			whiteRooks.clear(whiteKingIndex);
			for(int counterRooks=0;counterRooks<blackRooks.cardinality();counterRooks++)
			{
				blackRookIndex=blackRooks.nextSetBit(blackRookIndex+1);
				if(bitSetMoves.get(blackRookIndex)==true)
				{
					// here ewe have potentially a queen that can give a nailed piece
					BitSet rookAndKingBitSet=new BitSet();
					rookAndKingBitSet.set(whiteKingIndex);
					rookAndKingBitSet.set(blackRookIndex);
					BitSet bitSetMaskPotentialNailedPiece=(BitSet)HashMapLinesAndDiagonalesForCheck.get(rookAndKingBitSet).clone();
					bitSetMaskPotentialNailedPiece.clear(whiteKingIndex);
					bitSetMaskPotentialNailedPiece.clear(blackRookIndex);
					bitSetMaskPotentialNailedPiece.and(allPieces);
					BitSet bitSetConcernedPieces=(BitSet)bitSetMaskPotentialNailedPiece.clone();
					if(bitSetConcernedPieces.cardinality()==1&&whitePieces.get(bitSetConcernedPieces.nextSetBit(0))==true) // we have only on piece and it the good color
					{
						resultForNailedPieces.or((BitSet)HashMapLinesAndDiagonalesForCheck.get(rookAndKingBitSet).clone());
						resultForNailedPieces.clear(whiteKingIndex);
					}
				}
			}
			break;
		default:
			;
		}
		return resultForNailedPieces;
	}
	
	// check if at least one move of the current player is possible
	public int ifGameHasEndedGiveMeTheWinner()
	{
		BitSet blackAllPieces=GetAllBlackPieces();
		BitSet whiteAllPieces=GetAllWhitePieces();
		BitSet allPieces=(BitSet)blackAllPieces.clone();
		allPieces.or(whiteAllPieces);
		switch(currentTurn)
		{
		case white:
			int indexWhitePiece=-1;
			int totalMovesForWhitePlayer=0;
			for(;;)
			{
				indexWhitePiece=whiteAllPieces.nextSetBit(indexWhitePiece+1);
				if(indexWhitePiece==-1)
					break;
				ArrayList<Point> listPoint=GetListOfPossibleMovesForAPieceWithCheckChecking(indexWhitePiece);
				totalMovesForWhitePlayer+=listPoint.size();
			}
			if(totalMovesForWhitePlayer==0)
			{
				if(isKingOnCheck(whiteKing.nextSetBit(0),blackAllPieces,whiteAllPieces,allPieces)==false)
					return whiteIsPat;
				return black;
			}
			break;
		case black:
			int indexBlackPiece=-1;
			int totalMovesForBlackPlayer=0;
			for(;;)
			{
				indexBlackPiece=blackAllPieces.nextSetBit(indexBlackPiece+1);
				if(indexBlackPiece==-1)
					break;
				ArrayList<Point> listPoint=GetListOfPossibleMovesForAPieceWithCheckChecking(indexBlackPiece);
				totalMovesForBlackPlayer+=listPoint.size();
			}
			if(totalMovesForBlackPlayer==0)
			{
				if(isKingOnCheck(blackKing.nextSetBit(0),blackAllPieces,whiteAllPieces,allPieces)==false)
					return blackIsPat;
				return white;
			}
			break;
		}
		return 0;
	}
	
	public void endTheGame()
	{
		currentTurn=noCurrentGame;
	}
	
	public void setToLastTurnBeforeCheckAndMate(boolean isItPairMovement)
	{
		if(isItPairMovement==true)
			currentTurn=white;
		else
			currentTurn=black;
	}
}
