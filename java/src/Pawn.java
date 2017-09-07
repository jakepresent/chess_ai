
import java.applet.Applet;

import javax.swing.ImageIcon;

public class Pawn extends Piece {

	private boolean enPassantAble;
	
	public Pawn(boolean isWhite) {
		super(isWhite, 1, "P", new ImageIcon(Chess.class.getResource("WhitePawn.png")),
				Applet.newAudioClip(Chess.class.getResource("Pawn.wav")));
		if (!isWhite)
			this.setImage(new ImageIcon(Chess.class.getResource("BlackPawn.png")));
		enPassantAble = false;
	}

	public boolean isEnPassantAble() {
		return enPassantAble;
	}

	public void setEnPassantAble(boolean enPassantAble) {
		this.enPassantAble = enPassantAble;
	}
}
