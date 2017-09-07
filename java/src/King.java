import java.applet.Applet;

import javax.swing.ImageIcon;

public class King extends Piece {

	private boolean hasMoved;
	
	public King(boolean isWhite) {
		super(isWhite, 10, "K", new ImageIcon(Chess.class.getResource("WhiteKing.png")), 
				Applet.newAudioClip(Chess.class.getResource("King.wav")));
		if (!isWhite)
			this.setImage(new ImageIcon(Chess.class.getResource("BlackKing.png")));
		
		hasMoved = false;
	}
	
	public void move() {
		hasMoved = true;
	}
	
	public boolean getHasMoved() {
		return hasMoved;
	}

}
