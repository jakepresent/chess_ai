import java.util.ArrayList;

public class AIController {
	private int level; //1-3
	private boolean type; //false for player, true for AI
	private Board board;
	private boolean isWhite;
	
	public AIController(int level, boolean type, boolean isWhite, Board board) {
		this.level = level;
		this.type = type;
		this.isWhite = isWhite;
		this.board = board;
	}
	
	public void move() {
		
		if (level == 1)
			levelOneMove();
		else if (level == 2)
			levelTwoMove();
		else if (level == 3)
			levelThreeMove();
	}
	
	public void levelOneMove() {
		
		RowCol pos;
		ArrayList<RowCol> validPieces = new ArrayList<RowCol>();
		ArrayList<RowCol> posList;
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				pos = new RowCol(r,c);
				if(board.getSpace(pos) instanceof Piece && ((Piece)board.getSpace(pos)).isWhite() == isWhite &&
						board.possibleMoves(pos).size() > 0) {
					validPieces.add(pos); 
				}
			}
		}
		
		if (validPieces.size()>0) {
			pos = validPieces.get((int)(Math.random()*validPieces.size()));
			
			board.selectPiece(pos);
			posList = board.possibleMoves(pos);
			
			RowCol move = posList.get((int)(Math.random()*posList.size()));
			if (board.lastSelected == pos)
				board.movePiece(move);
		}
	}
	
	public void levelTwoMove() {
		ArrayList<RowCol> validPieces = new ArrayList<RowCol>();
		ArrayList<RowCol> posList;
		RowCol pos;
		RowCol pieceToMove = null;
		RowCol placeToMove = null;
		int highestValue = 0;
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				pos = new RowCol(r,c);
				if(board.getSpace(pos) instanceof Piece && ((Piece)board.getSpace(pos)).isWhite() == isWhite &&
						board.possibleMoves(pos).size() > 0) {
					validPieces.add(pos); 
				}
			}
		}
		
		if (validPieces.size()>0) {
			
			for (RowCol piece : validPieces) {
				posList = board.possibleMoves(piece);
				for (RowCol move : posList) {
					if (board.getSpace(move).getValue() > highestValue) {
						highestValue = board.getSpace(move).getValue();
						placeToMove = move;
						pieceToMove = piece;
					}
				}
			}
			
			if (pieceToMove == null) {
				levelOneMove();
			}
			else {
				board.selectPiece(pieceToMove);
				board.movePiece(placeToMove);
			}
		}
		
	}
	
	public void levelThreeMove() {
		ArrayList<RowCol> validPieces = new ArrayList<RowCol>();
		ArrayList<RowCol> threatenedPieces = new ArrayList<RowCol>();
		ArrayList<RowCol> posList;
		ArrayList<RowCol> attackedList;
		RowCol pos;
		RowCol pieceToMove = null;
		RowCol placeToMove = null;
		Space temp;
		int highestValue = 0;
		boolean isTake = false;
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				
				pos = new RowCol(r,c);
				
				if(board.getSpace(pos) instanceof Piece && ((Piece)board.getSpace(pos)).isWhite() == isWhite &&
						board.possibleMoves(pos).size() > 0) {
					validPieces.add(pos);
					if(board.isSquareAttacked(pos, isWhite) && board.getSpace(pos) instanceof King == false)
						threatenedPieces.add(pos);
				}
			}
		}
		
		if (validPieces.size()>0) {
			for (RowCol piece : validPieces) { //takes a piece if it can
				
				posList = board.possibleMoves(piece);
				
				for (RowCol move : posList) {
					if (board.getSpace(move).getValue() >= highestValue 
							&& board.getSpace(move).getValue() > 0
							&& (board.getSpace(move).getValue() >= board.getSpace(piece).getValue()
							|| board.shouldTake(piece, move))) {
						highestValue = board.getSpace(move).getValue();
						isTake = true;
						placeToMove = move;
						pieceToMove = piece;
					}
					
					if (board.getSpace(piece) instanceof King == false && board.shouldTake(piece, move)) {
						
						temp = board.getSpace(move);
						board.setSpace(move, board.getSpace(piece));
						
						attackedList = board.possibleMoves(move); //threatens pieces if taking isn't applicable
						
						if (attackedList != null) {
							for (RowCol attack : attackedList) {
								
								if (board.getSpace(attack).getValue() > highestValue && 
										(board.getSpace(attack).getValue() > board.getSpace(move).getValue() ||
										!board.isSquareAttacked(attack, isWhite))) {
									
									highestValue = board.getSpace(attack).getValue();
									isTake = false;
									placeToMove = move;
									pieceToMove = piece;
									
								}
							}
						}
						board.setSpace(move, temp);
					}
				}
			}
			
			if(threatenedPieces.size() > 0) {
				
				if (isTake == false)
					highestValue = 0;
				
				for (RowCol piece : threatenedPieces) {
					if (board.getSpace(piece).getValue() > highestValue) {
						
						posList = board.possibleMoves(piece);
						
						for (RowCol move : posList) {
							if (board.isSquareAttacked(move, isWhite) == false &&
									board.getSpace(move).getValue() >= highestValue) {
								highestValue = board.getSpace(piece).getValue();
								placeToMove = move;
								pieceToMove = piece;
							}
						}
					}
				}
			}
			
			if (pieceToMove == null) {
				levelOneMove();
			}
			else {
				board.selectPiece(pieceToMove);
				board.movePiece(placeToMove);
			}
		}
	}
	
	public boolean getType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
}
