import java.applet.Applet;

import javax.swing.ImageIcon;

public class Bishop extends Piece {

	public Bishop(boolean isWhite) {
		
		super(isWhite, 3, "B", new ImageIcon(Chess.class.getResource("WhiteBishop.png")), 
				Applet.newAudioClip(Chess.class.getResource("Bishop.wav")));
		if (!isWhite)
			this.setImage(new ImageIcon(Chess.class.getResource("BlackBishop.png")));
	}

}
