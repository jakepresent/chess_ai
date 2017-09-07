import java.applet.Applet;

import javax.swing.ImageIcon;

public class Rook extends Piece {
	
	private boolean hasMoved;

	public Rook(boolean isWhite) {
		super(isWhite, 5, "R", new ImageIcon(Chess.class.getResource("WhiteRook.png")),
				Applet.newAudioClip(Chess.class.getResource("Rook.wav")));
		if (!isWhite) {
			this.setImage(new ImageIcon(Chess.class.getResource("BlackRook.png")));
		}
		
		hasMoved = false;
	}

	public void move() {
		hasMoved = true;
	}
	
	public boolean getHasMoved() {
		return hasMoved;
	}
	
}
