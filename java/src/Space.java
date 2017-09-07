
public abstract class Space {
	
	private boolean highlighted;
	private int value;
	private String name;
	
	public Space(int value, String name) {
		highlighted = false;
		this.value = value;
		this.name = name;
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		if (highlighted)
			name += "x";
		else if (name.charAt(name.length()-1)=='x')
			name = name.substring(0, name.length()-1);
	}
	
	public String getName() {
		return name;
	}
	
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return name;
	}
}
