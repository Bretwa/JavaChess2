package chessApplicationPackage;

import java.util.BitSet;

public class PiecesSituation
{
	// all the bitsets for each 
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
	private static final int numberOfSquarePerLine=8;
	
	public PiecesSituation(BitSet whiteKnightsParameter,BitSet whiteBishopsParameter,BitSet whiteQueenParameter,BitSet whiteKingParameter,BitSet whitePawnsParameter,BitSet whiteRooksParameter,BitSet blackKnightsParameter,BitSet blackBishopsParameter,BitSet blackQueenParameter,BitSet blackKingParameter,BitSet blackPawnsParameter,BitSet blackRooksParameter)
	{
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
	}
	
	private boolean compareBitSet(BitSet bitSet1,BitSet bitSet2)
	{
		if(bitSet1.cardinality()==bitSet2.cardinality())
		{
			BitSet bitSet1Copy=(BitSet)bitSet1.clone();
			bitSet1Copy.and(bitSet2);
			if(bitSet1Copy.cardinality()==bitSet2.cardinality())
				return true;
		}
		return false;
	}
	
	public void displayBitSet(BitSet bitSet)
	{
		if(bitSet==null)
		{
			System.out.println("Error in DisplayBitSet bitSet is null");
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
	
	public BitSet getAllPieces()
	{
		BitSet allPieces=new BitSet();
		allPieces.or(whiteRooks);
		allPieces.or(whiteKnights);
		allPieces.or(whiteBishops);
		allPieces.or(whiteQueen);
		allPieces.or(whiteKing);
		allPieces.or(whitePawns);
		allPieces.or(blackRooks);
		allPieces.or(blackKnights);
		allPieces.or(blackBishops);
		allPieces.or(blackQueen);
		allPieces.or(blackKing);
		allPieces.or(blackPawns);
		return allPieces;
	}
	
	public boolean equal(PiecesSituation piecesSituationParameter)
	{
		if(compareBitSet(whiteRooks,piecesSituationParameter.whiteRooks)&&compareBitSet(whiteKnights,piecesSituationParameter.whiteKnights)&&compareBitSet(whiteBishops,piecesSituationParameter.whiteBishops)&&compareBitSet(whiteQueen,piecesSituationParameter.whiteQueen)&&compareBitSet(whitePawns,piecesSituationParameter.whitePawns)&&compareBitSet(whiteRooks,piecesSituationParameter.whiteRooks)&&compareBitSet(whiteKing,piecesSituationParameter.whiteKing)&&compareBitSet(blackRooks,piecesSituationParameter.blackRooks)&&compareBitSet(blackKnights,piecesSituationParameter.blackKnights)&&compareBitSet(blackBishops,piecesSituationParameter.blackBishops)&&compareBitSet(blackQueen,piecesSituationParameter.blackQueen)&&compareBitSet(blackPawns,piecesSituationParameter.blackPawns)&&compareBitSet(blackRooks,piecesSituationParameter.blackRooks)&&compareBitSet(blackKing,piecesSituationParameter.blackKing)==true)
		{
			return true;
		}
		return false;
	}
}
