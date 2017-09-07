import java.util.Scanner;

import javax.swing.JFrame;

public class Chess {

	public static void main(String[] args) {
		
		graphicsGame();
		
		//textGame();
		
	}
	
	public static void textGame() {
		
		Board board = new Board();
		Scanner scan = new Scanner(System.in);
		int row;
		int col;
		
		while (!board.gameOver()) {  //game loop
			
			System.out.print(board);
			System.out.println("White's Score: " + board.whiteScore);
			System.out.println("Black's Score: " + board.blackScore);
			if (board.playerTurn)
				System.out.println("White's turn");
			else
				System.out.println("Black's turn");
			System.out.println("Enter the row of the piece you want to move (1-8)");
			row = scan.nextInt();
			System.out.println("Enter the column of the piece you want to move (1-8)");
			col = scan.nextInt();
			board.selectPiece(new RowCol(row-1,col-1));
			while(board.lastSelected == null) {
				System.out.println("Not a valid choice. Try again.");
				System.out.println("Enter the row of the piece you want to move");
				row = scan.nextInt();
				System.out.println("Enter the column of the piece you want to move");
				col = scan.nextInt();
				board.selectPiece(new RowCol(row-1,col-1));
			}
			System.out.println(board);
			System.out.println("Enter the row of the selected piece's destination");
			row = scan.nextInt();
			System.out.println("Enter the column of the selected piece's destination");
			col = scan.nextInt();
			board.movePiece(new RowCol(row-1, col-1));
		}
		
		scan.close();
	}

	public static void graphicsGame() {
		JFrame frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ChessPanel panel = new ChessPanel();
		
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
		
	}
}
