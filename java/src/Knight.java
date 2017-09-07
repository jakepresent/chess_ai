import java.applet.Applet;

import javax.swing.ImageIcon;

public class Knight extends Piece {
	
	public Knight(boolean isWhite) {
		super(isWhite, 3, "N", new ImageIcon(Chess.class.getResource("WhiteKnight.png")),
				Applet.newAudioClip(Chess.class.getResource("Knight.wav")));
		if (!isWhite)
			this.setImage(new ImageIcon(Chess.class.getResource("BlackKnight.png")));
		
	}
}
