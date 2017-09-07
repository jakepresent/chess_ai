import java.util.ArrayList;

public class Board {
	
	Space[][] board;
	
	final boolean WHITE = true;
	final boolean BLACK = false;
	
	boolean whiteCheck = false;
	boolean blackCheck = false;
	boolean playerTurn = WHITE;
	int whiteScore = 0;
	int blackScore = 0;
	boolean whiteWins = false;
	boolean blackWins = false;
	boolean stalemate = false;
	boolean draw = false;
	boolean threefold = false;
	boolean soundOn = false;
	
	private RowCol wKingPos;
	private RowCol bKingPos;
	private ArrayList<RowCol> last6WhiteMoves;
	private ArrayList<RowCol> last6BlackMoves;
	AIController whiteAI;
	AIController blackAI;
	
	RowCol lastSelected = null;
	
	public Board() {
		board = new Space[8][8];
		setBoard();
		
		whiteAI = new AIController(1, false, WHITE, this);
		blackAI = new AIController(1, false, BLACK, this);
		
		last6WhiteMoves = new ArrayList<RowCol>();
		last6BlackMoves = new ArrayList<RowCol>();
	}
	
	public void setBoard() {
		
		for (int c = 0; c < 8; c++)
			board[1][c] = new Pawn(false); //sets up the black pawns
		
		for (int r = 2; r<=5; r++)
			for (int c = 0; c<=7; c++)
				board[r][c] = new EmptySpace(); //sets up the blank spaces
		
		for (int c = 0; c < 8; c++)
			board[6][c] = new Pawn(true); //sets up the white pawns
		
		board[0][0] = new Rook(false);
		board[0][7] = new Rook(false);
		board[7][0] = new Rook(true);
		board[7][7] = new Rook(true);
		board[0][1] = new Knight(false);
		board[0][6] = new Knight(false);
		board[7][1] = new Knight(true);
		board[7][6] = new Knight(true);
		board[0][2] = new Bishop(false);
		board[0][5] = new Bishop(false);
		board[7][2] = new Bishop(true);
		board[7][5] = new Bishop(true);
		board[0][3] = new Queen(false);
		board[7][3] = new Queen(true);
		board[0][4] = new King(false);
		board[7][4] = new King(true);
		
		wKingPos = new RowCol(7,4);
		bKingPos = new RowCol(0,4);
	}
	
	public void selectPiece(RowCol space) {
		
		int row = space.getRow();
		int col = space.getCol();
		
		if (lastSelected !=null)
			((Piece)board[lastSelected.getRow()][lastSelected.getCol()]).setSelected(false);
		
		lastSelected = null;
		
		for (int r = 0; r<8; r++)
			for (int c = 0; c<8; c++)
				board[r][c].setHighlighted(false);
		
		if(row>=0 && row <=7 && col >=0 && col <=7 && board[row][col] instanceof Piece) {
			
			ArrayList<RowCol> possibles = possibleMoves(space);
			
			if(possibles.size() >0 && ((Piece) board[row][col]).isWhite() == playerTurn) {
			
				((Piece) board[row][col]).setSelected(true);
				lastSelected = space;
			
				for (RowCol pos : possibles) {
					board[pos.getRow()][pos.getCol()].setHighlighted(true);
				}
			}
		}
	}
	
	public void movePiece(RowCol endPos) {
		int startRow = lastSelected.getRow();
		int startCol = lastSelected.getCol();
		int endRow = endPos.getRow();
		int endCol = endPos.getCol();
		Piece startPiece = (Piece)board[startRow][startCol];
		Space endSpace = board[endRow][endCol];
		
		//System.out.println(startPiece + " at " + startRow + " x " + startCol + " moves to " + endSpace + " at " + endPos);
		
		//play sound
		if (soundOn)
			startPiece.playSound();
		
		//threefold repetition stuff
		
		if (playerTurn == WHITE) {
			last6WhiteMoves.add(0, endPos);
			if (last6WhiteMoves.size() == 7)
				last6WhiteMoves.remove(6);
		}
		else {
			last6BlackMoves.add(0, endPos);
			if (last6BlackMoves.size() == 7)
				last6BlackMoves.remove(6);
		}
		
		//move pieces stuff
				
		if(startPiece.isSelected() && endSpace.isHighlighted()) {
			if (startPiece.isWhite())
				whiteScore += endSpace.getValue();
			else
				blackScore += endSpace.getValue();
			
			if(startPiece instanceof Pawn) {
				if (endSpace instanceof EmptySpace) {
					if(startPiece.isWhite()) {
						if (board[endRow+1][endCol] instanceof Pawn && ((Pawn)board[endRow+1][endCol]).isEnPassantAble()) {
							board[endRow+1][endCol] = new EmptySpace(); //en passant stuff
							whiteScore++;
						}
					}
					else {
						if(board[endRow-1][endCol] instanceof Pawn && ((Pawn)board[endRow-1][endCol]).isEnPassantAble()) {
							board[endRow-1][endCol] = new EmptySpace(); //en passant stuff
							blackScore++;
						}
					}
				}
				if(startPiece.isWhite()) {
					if(endRow == 0)
						startPiece = new Queen(true);
				}
				else {
					if(endRow == 7)
						startPiece = new Queen(false);
				}
		}
			if(startPiece instanceof King) {
				
				if (((King)startPiece).isWhite())
					wKingPos = endPos;
				else
					bKingPos = endPos;
				
				if(((King)startPiece).getHasMoved() == false) {
					((King)startPiece).move();
					
					if (endCol == 6) {
						board[startRow][5] = board[startRow][7];
						board[startRow][7] = new EmptySpace();
						((Rook)board[startRow][5]).move();
						if (soundOn)
							((Rook)board[startRow][5]).playSound();
					}
					if (endCol == 2) {
						board[startRow][3] = board[startRow][0];
						board[startRow][0] = new EmptySpace();
						((Rook)board[startRow][3]).move();
						if (soundOn)
							((Rook)board[startRow][3]).playSound();
					}
				}
			}
			
			if(startPiece instanceof Rook) {
				if(((Rook)startPiece).getHasMoved() == false) {
					((Rook)startPiece).move();
				}
			}
			
			board[endRow][endCol] = startPiece;
			board[startRow][startCol] = new EmptySpace();
			((Piece)board[endRow][endCol]).setSelected(false);

			if(board[endRow][endCol] instanceof Pawn && Math.abs(startRow-endRow)==2)
				((Pawn)board[endRow][endCol]).setEnPassantAble(true);
			
			switchPlayers();
		}
		
		if(board[startRow][startCol] instanceof Piece)
			((Piece)board[startRow][startCol]).setSelected(false);
		
		lastSelected = null;
		
		for (int r = 0; r<8; r++)
			for (int c = 0; c<8; c++)
				board[r][c].setHighlighted(false);
		
		if(isSquareAttacked(wKingPos, WHITE))
			whiteCheck = true;
		else
			whiteCheck = false;
		if(isSquareAttacked(bKingPos, BLACK))
			blackCheck = true;
		else
			blackCheck = false;
		
		if (whiteCheck && checkmate(WHITE))
			blackWins = true;
		else if (blackCheck && checkmate(BLACK))
			whiteWins = true;
		else if (getStalemate())
			stalemate = true;
	}
	
	public boolean isTakeable(RowCol origin, RowCol pos) {
		
		if(pos.getCol()<=7 && pos.getCol()>=0 && pos.getRow()<=7 && pos.getRow()>=0) {
		
		Space space = board[pos.getRow()][pos.getCol()];
		Piece taker = (Piece)board[origin.getRow()][origin.getCol()];
		if(space instanceof Piece && taker.isWhite() == ((Piece) space).isWhite()) //if the space is a piece of the same color
			return false;
		return true;
		}
		
		return false;
	}
	
	public ArrayList<RowCol> possibleMoves(RowCol space){
		
		Piece piece;
		ArrayList<RowCol> result;
		
		if(getSpace(space) instanceof Piece)
			piece = (Piece) getSpace(space);
		else
			return null;
		
		switch (piece.getName()) {
		case "P": result = pawnMoves(space);
		break;
		case "R": result = rookMoves(space);
		break;
		case "B": result = bishopMoves(space);
		break;
		case "N": result = knightMoves(space);
		break;
		case "Q": result = queenMoves(space);
		break;
		case "K": result = kingMoves(space);
		break;
		default: result = null;
		break;
		}
		
		result = checkMoves(result, space);
		
		return result;
	}
	
	public ArrayList<RowCol> pawnMoves(RowCol pawnPos){
		
		int row = pawnPos.getRow();
		int col = pawnPos.getCol();
		
		Pawn pawn = (Pawn)board[row][col];
		
		ArrayList<RowCol> moves = new ArrayList<RowCol>();
		
		if(pawn.isWhite()) {
			
			if (row -1 >=0 && board[row-1][col] instanceof EmptySpace)
				moves.add(new RowCol(row-1, col));
			
			if (pawnPos.getRow() == 6) {
				if (board[row-2][col] instanceof EmptySpace && board[row-1][col] instanceof EmptySpace)
					moves.add(new RowCol(row-2, col));
			}
			if(col-1>=0 && row-1>=0)
				if (board[row-1][col-1] instanceof Piece && !((Piece)board[row-1][col-1]).isWhite())
					moves.add(new RowCol(row-1, col-1));
			if(col+1<=7 && row-1>=0)
				if (board[row-1][col+1] instanceof Piece && !((Piece)board[row-1][col+1]).isWhite())
					moves.add(new RowCol(row-1, col+1));
			
			if(col-1>=0)
				if(board[row][col-1] instanceof Pawn && ((Pawn)board[row][col-1]).isEnPassantAble())
					moves.add(new RowCol(row-1, col-1));
			if(col+1<=7)
				if(board[row][col+1] instanceof Pawn && ((Pawn)board[row][col+1]).isEnPassantAble())
					moves.add(new RowCol(row-1, col+1));
		}
		else {
			if (row+1 <=7 && board[row+1][col] instanceof EmptySpace)
				moves.add(new RowCol(row+1, col));
			
			if (pawnPos.getRow() == 1) {
				if (board[row+2][col] instanceof EmptySpace && board[row+1][col] instanceof EmptySpace)
					moves.add(new RowCol(row+2, col));
			}
			
			if (col-1>=0 && row+1<=7)
				if (board[row+1][col-1] instanceof Piece && ((Piece)board[row+1][col-1]).isWhite())
					moves.add(new RowCol(row+1, col-1));
			if(col+1<=7 && row+1<=7)
				if (board[row+1][col+1] instanceof Piece && ((Piece)board[row+1][col+1]).isWhite())
					moves.add(new RowCol(row+1, col+1));
			if (col-1>=0)
				if(board[row][col-1] instanceof Pawn && ((Pawn)board[row][col-1]).isEnPassantAble())
					moves.add(new RowCol(row+1, col-1));
			if(col+1<=7)
				if(board[row][col+1] instanceof Pawn && ((Pawn)board[row][col+1]).isEnPassantAble())
					moves.add(new RowCol(row+1, col+1));
		}
		
		return moves;
	}
	
	public ArrayList<RowCol> rookMoves(RowCol rookPos){
		int row = rookPos.getRow();
		int col = rookPos.getCol();
		
		ArrayList<RowCol> moves = new ArrayList<RowCol>();
		
		for (int r = row-1; isTakeable(rookPos, new RowCol(r, col)); r--) { //checks up
			moves.add(new RowCol(r,col));
			if (board[r][col] instanceof Piece) {
				r=10;
			}
		}
		for (int r = row+1; isTakeable(rookPos, new RowCol(r,col)); r++) { //checks down
			moves.add(new RowCol(r,col));
			if (board[r][col] instanceof Piece) {
				r=10;
			}
		}
		for (int c = col-1; isTakeable(rookPos, new RowCol(row,c)); c--) { //checks left
			moves.add(new RowCol(row,c));
			if (board[row][c] instanceof Piece) {
				c=10;
			}
		}
		for (int c = col+1; isTakeable(rookPos, new RowCol(row,c)); c++) { //checks right
			moves.add(new RowCol(row,c));
			if (board[row][c] instanceof Piece) {
				c=10;
			}
		}
			
		return moves;
	}
	
	public ArrayList<RowCol> bishopMoves(RowCol bishopPos){
		int row = bishopPos.getRow();
		int col = bishopPos.getCol();
		
		ArrayList<RowCol> moves = new ArrayList<RowCol>();
		
		for (int i = 1; isTakeable(bishopPos, new RowCol(row-i,col-i)); i++) { //checks up-left
			moves.add(new RowCol(row-i, col-i));
			if (board[row-i][col-i] instanceof Piece) {
				i=10;
			}
		}
		
		for (int i = 1; isTakeable(bishopPos, new RowCol(row+i,col-i)); i++) { //checks down-left
			moves.add(new RowCol(row+i, col-i));
			if (board[row+i][col-i] instanceof Piece) {
				i=10;
			}
		}
		
		for (int i = 1; isTakeable(bishopPos, new RowCol(row-i,col+i)); i++) { //checks up-right
			moves.add(new RowCol(row-i, col+i));
			if (board[row-i][col+i] instanceof Piece)
				i=10;
		}
		
		for (int i = 1; isTakeable(bishopPos, new RowCol(row+i,col+i)); i++) { //checks down-right
			moves.add(new RowCol(row+i, col+i));
			if (board[row+i][col+i] instanceof Piece)
				i=10;
		}
			
		return moves;
	}
	
	public ArrayList<RowCol> knightMoves(RowCol knightPos){
		
		int row = knightPos.getRow();
		int col = knightPos.getCol();
		
		ArrayList<RowCol> moves = new ArrayList<RowCol>();
		
		if (isTakeable(knightPos, new RowCol(row-2, col-1)))
			moves.add(new RowCol(row-2, col-1));
		if (isTakeable(knightPos, new RowCol(row-1, col-2)))
			moves.add(new RowCol(row-1, col-2));		
		if (isTakeable(knightPos, new RowCol(row-1, col+2)))
			moves.add(new RowCol(row-1, col+2));		
		if (isTakeable(knightPos, new RowCol(row+1, col-2)))
			moves.add(new RowCol(row+1, col-2));
		if (isTakeable(knightPos, new RowCol(row+1, col+2)))//  checks each of the knight's available moves
			moves.add(new RowCol(row+1, col+2));		
		if (isTakeable(knightPos, new RowCol(row+2, col-1)))
			moves.add(new RowCol(row+2, col-1));		
		if (isTakeable(knightPos, new RowCol(row+2, col+1)))
			moves.add(new RowCol(row+2, col+1));
		if (isTakeable(knightPos, new RowCol(row-2, col+1)))
			moves.add(new RowCol(row-2, col+1));		
		
		return moves;
	}
	
	public ArrayList<RowCol> queenMoves(RowCol queenPos){
		
		ArrayList<RowCol> moves = bishopMoves(queenPos);
		ArrayList<RowCol> dMoves = rookMoves(queenPos);
		for (RowCol item : dMoves)
			moves.add(item);
		
		
		return moves;
		
	}
	
	public ArrayList<RowCol> kingMoves(RowCol kingPos){
		int row = kingPos.getRow();
		int col = kingPos.getCol();
		boolean isWhite = ((Piece)board[row][col]).isWhite();
		
		ArrayList<RowCol> moves = new ArrayList<RowCol>();
		
		
		if(isTakeable(kingPos, new RowCol(row-1,col-1)) && !isSquareAttacked(new RowCol(row-1,col-1), isWhite))
			moves.add(new RowCol(row-1,col-1));
		if(isTakeable(kingPos, new RowCol(row,col-1)) && !isSquareAttacked(new RowCol(row,col-1), isWhite))
			moves.add(new RowCol(row,col-1));
		if(isTakeable(kingPos, new RowCol(row+1,col-1)) && !isSquareAttacked(new RowCol(row+1,col-1), isWhite))
			moves.add(new RowCol(row+1,col-1));
		if(isTakeable(kingPos, new RowCol(row+1,col)) && !isSquareAttacked(new RowCol(row+1,col), isWhite))
			moves.add(new RowCol(row+1,col));
		if(isTakeable(kingPos, new RowCol(row+1,col+1)) && !isSquareAttacked(new RowCol(row+1,col+1), isWhite))
			moves.add(new RowCol(row+1,col+1));
		if(isTakeable(kingPos, new RowCol(row,col+1)) && !isSquareAttacked(new RowCol(row,col+1), isWhite))
			moves.add(new RowCol(row,col+1));
		if(isTakeable(kingPos, new RowCol(row-1,col+1)) && !isSquareAttacked(new RowCol(row-1,col+1), isWhite))
			moves.add(new RowCol(row-1,col+1));
		if(isTakeable(kingPos, new RowCol(row-1,col)) && !isSquareAttacked(new RowCol(row-1,col), isWhite))
			moves.add(new RowCol(row-1,col));
		
		//Castling
		if(((King)board[row][col]).getHasMoved() == false && !isSquareAttacked(kingPos, isWhite)) {
			
			if(board[row][col+1] instanceof EmptySpace && board[row][col+2] instanceof EmptySpace) {
				if(!isSquareAttacked(new RowCol(row,col+1), isWhite) && !isSquareAttacked(new RowCol(row,col+2), isWhite)) {
					if(board[row][col+3] instanceof Rook && ((Rook)board[row][col+3]).getHasMoved() == false) {
						moves.add(new RowCol(row,col+2));
					}
				}
			}
			if(board[row][col-1] instanceof EmptySpace && board[row][col-2] instanceof EmptySpace && 
					board[row][col-3] instanceof EmptySpace) {
				if(!isSquareAttacked(new RowCol(row,col-1), isWhite) && !isSquareAttacked(new RowCol(row,col-2), isWhite) &&
						!isSquareAttacked(new RowCol(row,col-3), isWhite)) {
					if(board[row][col-4] instanceof Rook && ((Rook)board[row][col-4]).getHasMoved() == false) {
						moves.add(new RowCol(row,col-2));
					}
				}
			}
		}
		
		
		return moves;
	}
	
	public boolean isSquareAttacked(RowCol pos, boolean isWhite) {
		
		int row = pos.getRow();
		int col = pos.getCol();
		
		//Attacked by:
		//Pawn
		
		if (isWhite) {
			if (row-1 >=0 && col-1 >=0 && board[row-1][col-1] instanceof Pawn && !((Pawn)board[row-1][col-1]).isWhite())
				return true;
			if (row-1 >=0 && col+1 <=7 && board[row-1][col+1] instanceof Pawn && !((Pawn)board[row-1][col+1]).isWhite())
				return true;
		}
		else {
			if (row+1 <=7 && col-1 >=0 && board[row+1][col-1] instanceof Pawn && ((Pawn)board[row+1][col-1]).isWhite())
				return true;
			if (row+1 <=7 && col+1 <=7 && board[row+1][col+1] instanceof Pawn && ((Pawn)board[row+1][col+1]).isWhite()) 
				return true;
		}
		
		//Bishop/Queen
		for (int i = 1; row-i >=0 && col-i >=0; i++) {
			if(board[row-i][col-i] instanceof Piece) {
				if(((Piece)board[row-i][col-i]).isWhite() != isWhite) {
					if(board[row-i][col-i] instanceof Bishop || board[row-i][col-i] instanceof Queen)
						return true;
				}
				i = 10;
			}
		}
		
		for (int i = 1; row+i <=7 && col-i >=0; i++) {
			if(board[row+i][col-i] instanceof Piece) {
				if(((Piece)board[row+i][col-i]).isWhite() != isWhite) {
					if(board[row+i][col-i] instanceof Bishop || board[row+i][col-i] instanceof Queen)
						return true;
				}
				i = 10;
			}
		}
		
		for (int i = 1; row-i >=0 && col+i <=7; i++) {
			if(board[row-i][col+i] instanceof Piece) {
				if(((Piece)board[row-i][col+i]).isWhite() != isWhite) {
					if(board[row-i][col+i] instanceof Bishop || board[row-i][col+i] instanceof Queen)
						return true;
				}
				i = 10;
			}
		}
		
		for (int i = 1; row+i <=7 && col+i <=7; i++) {
			if(board[row+i][col+i] instanceof Piece) {
				if(((Piece)board[row+i][col+i]).isWhite() != isWhite) {
					if(board[row+i][col+i] instanceof Bishop || board[row+i][col+i] instanceof Queen)
						return true;
				}
				i = 10;
			}
		}
		
		//Rook/Queen
		
		for (int r = row-1; r>=0; r--) {
			if (board[r][col] instanceof Piece) {
				if (((Piece)board[r][col]).isWhite() != isWhite) {
					if(board[r][col] instanceof Rook || board[r][col] instanceof Queen)
						return true;
				}
				r=-10;
			}
		}
		
		for (int r = row+1; r<=7; r++) {
			if (board[r][col] instanceof Piece) {
				if (((Piece)board[r][col]).isWhite() != isWhite) {
					if(board[r][col] instanceof Rook || board[r][col] instanceof Queen)
						return true;
				}
				r=10;
			}
		}
		
		for (int c = col-1; c>=0; c--) {
			if (board[row][c] instanceof Piece) {
				if (((Piece)board[row][c]).isWhite() != isWhite) {
					if(board[row][c] instanceof Rook || board[row][c] instanceof Queen)
						return true;
				}
				c=-10;
			}
		}
		for (int c = col+1; c<=7; c++) {
			if (board[row][c] instanceof Piece) {
				if (((Piece)board[row][c]).isWhite() != isWhite) {
					if(board[row][c] instanceof Rook || board[row][c] instanceof Queen)
						return true;
				}
				c=10;
			}
		}
		
		//Knight
		if(row-2>=0 && col-1 >=0 && board[row-2][col-1] instanceof Knight && ((Knight)board[row-2][col-1]).isWhite() != isWhite)
			return true;
		if(row-2>=0 && col+1 <=7 && board[row-2][col+1] instanceof Knight && ((Knight)board[row-2][col+1]).isWhite() != isWhite)
			return true;
		if(row-1>=0 && col-2 >=0 && board[row-1][col-2] instanceof Knight && ((Knight)board[row-1][col-2]).isWhite() != isWhite)
			return true;
		if(row-1>=0 && col+2 <=7 && board[row-1][col+2] instanceof Knight && ((Knight)board[row-1][col+2]).isWhite() != isWhite)
			return true;
		if(row+1<=7 && col-2 >=0 && board[row+1][col-2] instanceof Knight && ((Knight)board[row+1][col-2]).isWhite() != isWhite)
			return true;
		if(row+1<=7 && col+2 <=7 && board[row+1][col+2] instanceof Knight && ((Knight)board[row+1][col+2]).isWhite() != isWhite)
			return true;
		if(row+2<=7 && col-1 >=0 && board[row+2][col-1] instanceof Knight && ((Knight)board[row+2][col-1]).isWhite() != isWhite)
			return true;
		if(row+2<=7 && col+1 <=7 && board[row+2][col+1] instanceof Knight && ((Knight)board[row+2][col+1]).isWhite() != isWhite)
			return true;
		
		//King
		if (row-1>=0 && col-1>=0 && board[row-1][col-1] instanceof King && ((King)board[row-1][col-1]).isWhite() != isWhite)
			return true;
		if (row-1>=0 && board[row-1][col] instanceof King && ((King)board[row-1][col]).isWhite() != isWhite)
			return true;
		if (row-1>=0 && col+1<=7 && board[row-1][col+1] instanceof King && ((King)board[row-1][col+1]).isWhite() != isWhite)
			return true;
		if (col+1<=7 && board[row][col+1] instanceof King && ((King)board[row][col+1]).isWhite() != isWhite)
			return true;
		if (row+1<=7 && col+1<=7 && board[row+1][col+1] instanceof King && ((King)board[row+1][col+1]).isWhite() != isWhite)
			return true;
		if (row+1<=7 && board[row+1][col] instanceof King && ((King)board[row+1][col]).isWhite() != isWhite)
			return true;
		if (row+1<=7 && col-1>=0 && board[row+1][col-1] instanceof King && ((King)board[row+1][col-1]).isWhite() != isWhite)
			return true;
		if (col-1>=0 && board[row][col-1] instanceof King && ((King)board[row][col-1]).isWhite() != isWhite)
			return true;
		
		return false;
	}
	
	public ArrayList<RowCol> checkMoves(ArrayList<RowCol> input, RowCol startPos){
		
		//returns a list of moves that will protect from check or not result in check
		
		ArrayList<RowCol> output = new ArrayList<RowCol>();
		Space temp;
		Space newTemp;
		
		if ((whiteCheck || blackCheck) && getSpace(startPos) instanceof King) {
			
			temp = (King)getSpace(startPos);
			board[startPos.getRow()][startPos.getCol()] = new EmptySpace();
			
			for (RowCol pos : input) {
				
				if (whiteCheck) {
					if (!isSquareAttacked(pos, WHITE)) {
						output.add(pos);
					}
				}
				else {
					if (!isSquareAttacked(pos, BLACK))
						output.add(pos);
				}
			}
			
			board[startPos.getRow()][startPos.getCol()] = temp;
			
		}
		else {	
			if (whiteCheck && playerTurn == WHITE) {
				for (RowCol pos : input) {
					temp = getSpace(pos);
					board[pos.getRow()][pos.getCol()] = new Pawn(WHITE);
					if (!isSquareAttacked(wKingPos, WHITE)) {
						output.add(pos);
					}
					board[pos.getRow()][pos.getCol()] = temp;
				}
			}
			else if(blackCheck && playerTurn == BLACK) {
				for (RowCol pos : input) {
					temp = getSpace(pos);
					board[pos.getRow()][pos.getCol()] = new Pawn(BLACK);
					if (!isSquareAttacked(bKingPos, BLACK)) {
						output.add(pos);
					}
					board[pos.getRow()][pos.getCol()] = temp;
				}
			}
			
			else {
				
				temp = getSpace(startPos);
				board[startPos.getRow()][startPos.getCol()] = new EmptySpace();
				if(playerTurn == WHITE && isSquareAttacked(wKingPos, WHITE)) {
					
					for (RowCol pos : input) {
						newTemp = getSpace(pos);
						setSpace(pos, temp);
						if (isSquareAttacked(wKingPos, WHITE) == false)
							output.add(pos);
						setSpace(pos, newTemp);
					}
				}
				else if (playerTurn == BLACK && isSquareAttacked(bKingPos, BLACK)) {
					
					for (RowCol pos : input) {
						newTemp = getSpace(pos);
						setSpace(pos, new EmptySpace());
						if (isSquareAttacked(bKingPos, BLACK) == false)
							output.add(pos);
						setSpace(pos, newTemp);
					}
				}
				
				else
					output = input;
				
				board[startPos.getRow()][startPos.getCol()] = temp;
			}
		}
		
		return output;
	}
	
	public void switchPlayers() {
		playerTurn = !playerTurn;
		
		for (int r = 0; r<8; r++)
			for (int c = 0; c<8; c++)
				if(board[r][c] instanceof Pawn && ((Pawn)board[r][c]).isWhite() == playerTurn) //en passant stuff
					((Pawn)board[r][c]).setEnPassantAble(false);
		
	}
	
	public boolean checkmate(boolean isWhite) {
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				if (board[r][c] instanceof Piece && ((Piece)board[r][c]).isWhite() == isWhite &&
						possibleMoves(new RowCol(r,c)).size() > 0) {
					return false;
				}
			}
		}
		
		if ((isWhite && whiteCheck) || (!isWhite && blackCheck)) {
			return true;
		}
		else return false;
	}
	
	public boolean getStalemate() {
		
		if(stalemate)
			return true;
		
		else if (!whiteCheck && !blackCheck) {
			
			boolean result = true;
			int counter = 0;
			
			for (int r = 0; r<8; r++) {
				for (int c = 0; c<8; c++) {
					if (board[r][c] instanceof Piece && ((Piece)board[r][c]).isWhite() == playerTurn &&
							possibleMoves(new RowCol(r,c)).size() >0){
						result = false;
					}
					if (board[r][c] instanceof Piece)
						counter++;
				}
			}
			
			if (last6WhiteMoves.size() == 6 && last6BlackMoves.size() == 6) {
				
				if (last6WhiteMoves.get(0).equals(last6WhiteMoves.get(2)) &&
						last6WhiteMoves.get(0).equals(last6WhiteMoves.get(4))) {
					if (last6WhiteMoves.get(1).equals(last6WhiteMoves.get(3)) &&
							last6WhiteMoves.get(1).equals(last6WhiteMoves.get(5))) {
						if (last6BlackMoves.get(0).equals(last6BlackMoves.get(2)) &&
								last6BlackMoves.get(0).equals(last6BlackMoves.get(4))) {
							if (last6BlackMoves.get(1).equals(last6BlackMoves.get(3)) &&
									last6BlackMoves.get(1).equals(last6BlackMoves.get(5))) {
								threefold = true;
								return true;
							}
						}
					}
				}
			}
			
			if (counter <=2) {
				draw = true;
				return true;
			}
			else
				return result;
		}
		
		else
			return false;
	}
	
	public int timesAttacked(RowCol space) {
				
		int counter = 0;
		ArrayList<RowCol> attackList;
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				if (board[r][c] instanceof Piece && ((Piece)board[r][c]).isWhite() == playerTurn) {
					
					attackList = possibleMoves(new RowCol(r,c));
					
					for (RowCol attack : attackList) {
						if (attack.equals(space)) {
							counter++;
						}
					}
				}
			}
		}
		
		return counter;
	}
	
	public int timesDefended(RowCol space, RowCol attacker) {
		int counter = 0;
		ArrayList<RowCol> defendList;
		Space tempAttack = getSpace(attacker);
		setSpace(attacker, new EmptySpace());
		Space tempSpace = getSpace(space);
		setSpace(space, new Pawn(playerTurn));
		
		
		for (int r = 0; r<8; r++) {
			for (int c = 0; c<8; c++) {
				if (board[r][c] instanceof Piece) {
					
					defendList = possibleMoves(new RowCol(r,c));
					
					for (RowCol defense : defendList) {
						if (defense.equals(space)) {
							counter++;
						}
					}
				}
			}
		}
		
		if (getSpace(space) instanceof King)
			counter++;
		
		setSpace(space, tempSpace);
		setSpace(attacker, tempAttack);
		return counter;
	}
	
	public boolean shouldTake(RowCol piece, RowCol attack) {
		
		int attackCounter = timesAttacked(attack);
		int defendCounter = timesDefended(attack, piece);
		
		if (attackCounter > defendCounter) {
			return true;
		}
	
		return false;
		
	}
	
	public boolean gameOver() {
		if (whiteWins || blackWins || getStalemate())
			return true;
		
		return false;
	}
	
	public Space getSpace (RowCol pos) {
		return board[pos.getRow()][pos.getCol()];
	}
	
	public void setSpace (RowCol pos, Space space) {
		board[pos.getRow()][pos.getCol()] = space;
	}
	
	public String toString() {
		String output = "";
		
		for(int r = 0; r<board.length; r++) {
			for(int c = 0; c<board[0].length; c++) {
				output += board[r][c] + "\t";
			}
			output += "\n\n";
		}
		
		return output;
	}
}