import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BoardPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private Board board;
	private boolean isFlipped;

	public BoardPanel() {
		
		board = new Board();
		
		isFlipped = false;
		
		setBackground(Color.black);
		setOpaque(true);
		setPreferredSize (new Dimension(600,600));
		addMouseListener (new SpaceListener());
	}
	
	public void drawBoard(Graphics page) {
		
		new ImageIcon(Chess.class.getResource("Board.png")).paintIcon(this, page, 0, 0);
		
		if (isFlipped) {
			
			for (int r = 0; r<8; r++) {
				for (int c = 0; c< 8; c++) {
				
					page.setColor(new Color(0,200,255,100));
				
					if(board.getSpace(new RowCol(r,c)).isHighlighted()) {
						page.fillRect((7-c)*75, (7-r)*75, 75, 75);
					}
				
					if (board.getSpace(new RowCol(r,c)) instanceof Piece) {	
					
						if(((Piece)board.getSpace(new RowCol(r,c))).isSelected()) {
							page.fillRect((7-c)*75, (7-r)*75, 75, 75);
						}
					
						((Piece)board.getSpace(new RowCol(r,c))).getImage().paintIcon(this, page, (7-c)*75, (7-r)*75);
					}
				}
			}
		}
		else {
		
			for (int r = 0; r<8; r++) {
				for (int c = 0; c< 8; c++) {
				
					page.setColor(new Color(0,200,255,100));
				
					if(board.getSpace(new RowCol(r,c)).isHighlighted()) {
						page.fillRect(c*75, r*75, 75, 75);
					}
				
					if (board.getSpace(new RowCol(r,c)) instanceof Piece) {	
					
						if(((Piece)board.getSpace(new RowCol(r,c))).isSelected()) {
							page.fillRect(c*75, r*75, 75, 75);
						}
					
						((Piece)board.getSpace(new RowCol(r,c))).getImage().paintIcon(this, page, c*75, r*75);
					}
				}
			}
		}
	}
	
	public void paintComponent (Graphics page){
		
	      super.paintComponent (page);
	      
	      drawBoard(page);
	      }
	
	private class SpaceListener implements MouseListener{
		public void mousePressed(MouseEvent event) {
			RowCol pos;
			if(isFlipped)
				pos = new RowCol((600-event.getY())/75, (600-event.getX())/75);
			else
				pos = new RowCol(event.getY()/75, event.getX()/75);
			
			if (!board.gameOver()) {
			
				if (board.getSpace(pos).isHighlighted() == false) {
					board.selectPiece(pos);
				}
				else {
					board.movePiece(pos);
				}
				
				repaint();
			}
			
		}

		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		
	}
	
	public void flip() {
		isFlipped = !isFlipped;
	}
	
	public void restartGame() {
		board = new Board();
	}
	
	public int getWhiteScore () {
		return board.whiteScore;
	}
	public int getBlackScore () {
		return board.blackScore;
	}
	
	public String getTurn() {
		if (board.playerTurn == true)
			return "White";
		else
			return "Black";
	}
	
	public boolean blackCheck () {
		return board.blackCheck;
	}
	public boolean whiteCheck () {
		return board.whiteCheck;
	}
	
	public int getWinner () {
		if (board.whiteWins)
			return 1;
		else if (board.blackWins)
			return 0;
		else if (board.stalemate == true)
			return 2;
		else
			return -1;
	}
	
	public boolean getPlayerTurn() {
		return board.playerTurn;
	}
	
	public boolean getThreefold() {
		return board.threefold;
	}
	
	public boolean getDraw() {
		return board.draw;
	}
	
	public void setWhiteAIType (boolean type) {
		board.whiteAI.setType(type);
	}
	
	public void setBlackAIType (boolean type) {
		board.blackAI.setType(type);
	}
	
	public void setWhiteAILevel (int level) {
		board.whiteAI.setLevel(level);
	}
	
	public void setBlackAILevel (int level) {
		board.blackAI.setLevel(level);
	}
	
	public boolean getWhiteAIType() {
		return board.whiteAI.getType();
	}
	
	public boolean getBlackAIType() {
		return board.blackAI.getType();
	}
	
	public void moveWhiteAI() {
		board.whiteAI.move();
	}
	
	public void moveBlackAI() {
		board.blackAI.move();
	}
	
	public void setSoundCheckBox(boolean b) {
		board.soundOn = b;
	}
}