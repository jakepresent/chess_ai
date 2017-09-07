import java.applet.Applet;

import javax.swing.ImageIcon;

public class Queen extends Piece {

	public Queen(boolean isWhite) {
		super(isWhite, 9, "Q", new ImageIcon(Chess.class.getResource("WhiteQueen.png")),
				Applet.newAudioClip(Chess.class.getResource("Queen.wav")));
		if (!isWhite)
			this.setImage(new ImageIcon(Chess.class.getResource("BlackQueen.png")));
	}

}
