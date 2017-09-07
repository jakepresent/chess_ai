import java.applet.AudioClip;

import javax.swing.ImageIcon;

public abstract class Piece extends Space {

	private boolean selected;
	private boolean color; //false if black, true if white
	private ImageIcon img;
	private AudioClip sound;
	
	public Piece(boolean isWhite, int value, String name, ImageIcon imageIcon, AudioClip sound) {
		super(value, name);
		color = isWhite;
		img = imageIcon;
		this.sound = sound;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isWhite() {
		return color;
	}
	
	public ImageIcon getImage() {
		return img;
	}
	
	protected void setImage(ImageIcon imageIcon) {
		this.img = imageIcon;
	}
	
	public void playSound() {
		sound.play();
	}
	
	public String toString() {
		if (color)
			return "w" + super.toString();
		else
			return "b" + super.toString();
	}
}
