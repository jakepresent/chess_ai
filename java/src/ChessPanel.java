import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class ChessPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JButton flipButton, restartButton;
	private JLabel titleLabel, nameLabel, player1Label, player2Label, infoLabel, whiteScoreLabel, blackScoreLabel;
	private JPanel toolContainer, tools, mainPanel, logicPanel, topPanel, bottomPanel;
	private JRadioButton whiteHuman, blackHuman, whiteAI, blackAI;
	private JRadioButton whiteAI1, whiteAI2, whiteAI3, blackAI1, blackAI2, blackAI3;
	private JCheckBox soundCheckBox;
	private ActionListener taskPerformer;
	private boolean isLooping;
	private final int DELAY = 20; //delay for AIvAI in milliseconds
	private BoardPanel boardPanel;
	
	public ChessPanel() {
		
		isLooping = false;
		
		boardPanel = new BoardPanel();
		boardPanel.addMouseListener (new ClickListener());
		
		toolContainer = new JPanel();
		toolContainer.setLayout(new BoxLayout(toolContainer, BoxLayout.Y_AXIS));
		toolContainer.setPreferredSize (new Dimension (200, 600));
		toolContainer.setBackground (Color.darkGray);
		toolContainer.setOpaque (true);
		
		tools = new JPanel();
		tools.setLayout(new BoxLayout(tools, BoxLayout.Y_AXIS));
	    tools.setPreferredSize (new Dimension (200, 600));
	    tools.setBackground (Color.darkGray);
	    tools.setOpaque (true);
	    
	    topPanel = new JPanel();
	    topPanel.setLayout(new GridLayout(5, 1));
	    topPanel.setPreferredSize(new Dimension (200, 100));
	    topPanel.setBackground(Color.darkGray);
	    topPanel.setOpaque(true);
		
		titleLabel = new JLabel ("Chess");
	    titleLabel.setForeground (Color.white);
	    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    titleLabel.setFont(new Font("Sans Serif", Font.PLAIN, 40));
	    
	    nameLabel = new JLabel ("by Jake Present");
	    nameLabel.setForeground (Color.white);
	    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    nameLabel.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    infoLabel = new JLabel ("White's turn");
	    infoLabel.setForeground (Color.white);
	    infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    infoLabel.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    whiteScoreLabel = new JLabel ("White's score: 0");
	    whiteScoreLabel.setForeground (Color.white);
	    whiteScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    whiteScoreLabel.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    blackScoreLabel = new JLabel ("Black's score: 0");
	    blackScoreLabel.setForeground (Color.white);
	    blackScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    blackScoreLabel.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    topPanel.add(titleLabel);
	    topPanel.add(nameLabel);
	    topPanel.add(infoLabel);
	    topPanel.add(whiteScoreLabel);
	    topPanel.add(blackScoreLabel);
	    
	    logicPanel = new JPanel();
	    logicPanel.setLayout(new GridLayout(6, 2));
	    logicPanel.setPreferredSize (new Dimension (200, 500));
	    logicPanel.setBackground (Color.darkGray);
	    logicPanel.setOpaque (true);
	    
	    player1Label = new JLabel ("White");
	    player1Label.setForeground (Color.white);
	    player1Label.setHorizontalAlignment(SwingConstants.CENTER);
	    player1Label.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    player2Label = new JLabel ("Black");
	    player2Label.setForeground (Color.white);
	    player2Label.setHorizontalAlignment(SwingConstants.CENTER);
	    player2Label.setFont(new Font("Sans Serif", Font.PLAIN, 15));
	    
	    whiteHuman = new JRadioButton("Human", true);
	    whiteHuman.setBackground(Color.darkGray);
	    whiteHuman.setForeground(Color.white);
	    whiteHuman.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    
	    blackHuman = new JRadioButton("Human", true);
	    blackHuman.setBackground(Color.darkGray);
	    blackHuman.setForeground(Color.white);
	    blackHuman.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    
	    whiteAI = new JRadioButton("Computer", false);
	    whiteAI.setBackground(Color.darkGray);
	    whiteAI.setForeground(Color.white);
	    whiteAI.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    
	    blackAI = new JRadioButton("Computer", false);
	    blackAI.setBackground(Color.darkGray);
	    blackAI.setForeground(Color.white);
	    blackAI.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    
	    whiteAI1 = new JRadioButton("Random AI", true);
	    whiteAI1.setBackground(Color.darkGray);
	    whiteAI1.setForeground(Color.white);
	    whiteAI1.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    whiteAI1.setEnabled(false);
	    
	    blackAI1 = new JRadioButton("Random AI", true);
	    blackAI1.setBackground(Color.darkGray);
	    blackAI1.setForeground(Color.white);
	    blackAI1.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    blackAI1.setEnabled(false);
	    
	    whiteAI2 = new JRadioButton("Stupid AI", true);
	    whiteAI2.setBackground(Color.darkGray);
	    whiteAI2.setForeground(Color.white);
	    whiteAI2.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    whiteAI2.setEnabled(false);
	    
	    blackAI2 = new JRadioButton("Stupid AI", true);
	    blackAI2.setBackground(Color.darkGray);
	    blackAI2.setForeground(Color.white);
	    blackAI2.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    blackAI2.setEnabled(false);
	    
	    whiteAI3 = new JRadioButton("Smart AI", true);
	    whiteAI3.setBackground(Color.darkGray);
	    whiteAI3.setForeground(Color.white);
	    whiteAI3.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    whiteAI3.setEnabled(false);
	    
	    blackAI3 = new JRadioButton("Smart AI", true);
	    blackAI3.setBackground(Color.darkGray);
	    blackAI3.setForeground(Color.white);
	    blackAI3.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    blackAI3.setEnabled(false);
	    
	    ButtonGroup whiteType = new ButtonGroup();
	    whiteType.add(whiteHuman);
	    whiteType.add(whiteAI);
	    
	    ButtonGroup blackType = new ButtonGroup();
	    blackType.add(blackHuman);
	    blackType.add(blackAI);
	    
	    ButtonGroup whiteAIGroup = new ButtonGroup();
	    whiteAIGroup.add(whiteAI1);
	    whiteAIGroup.add(whiteAI2);
	    whiteAIGroup.add(whiteAI3);
	    
	    ButtonGroup blackAIGroup = new ButtonGroup();
	    blackAIGroup.add(blackAI1);
	    blackAIGroup.add(blackAI2);
	    blackAIGroup.add(blackAI3);
	    
	    logicPanel.add(player1Label);
	    logicPanel.add(player2Label);
	    logicPanel.add(whiteHuman);
	    logicPanel.add(blackHuman);
	    logicPanel.add(whiteAI);
	    logicPanel.add(blackAI);
	    logicPanel.add(whiteAI1);
	    logicPanel.add(blackAI1);
	    logicPanel.add(whiteAI2);
	    logicPanel.add(blackAI2);
	    logicPanel.add(whiteAI3);
	    logicPanel.add(blackAI3);
	    
	    bottomPanel = new JPanel();
	    bottomPanel.setLayout(new GridLayout(3, 1));
	    bottomPanel.setPreferredSize(new Dimension (200, 200));
	    bottomPanel.setBackground(Color.darkGray);
	    bottomPanel.setOpaque(true);
	    
	    soundCheckBox = new JCheckBox("Sound");
	    soundCheckBox.setSelected(false);
	    soundCheckBox.addActionListener(this);
	    soundCheckBox.setBackground(Color.darkGray);
	    soundCheckBox.setForeground(Color.white);
	    soundCheckBox.setFont(new Font("Sans Serif", Font.PLAIN, 12));
	    
	    flipButton = new JButton("Flip board");
	    flipButton.setMargin (new Insets (0, 0, 0, 0));
	    flipButton.addActionListener (this);
	    flipButton.setHorizontalAlignment(SwingConstants.CENTER);
	    
	    restartButton = new JButton("Restart game");
	    restartButton.setMargin (new Insets (0, 0, 0, 0));
	    restartButton.addActionListener (this);
	    restartButton.setHorizontalAlignment(SwingConstants.CENTER);
	    
	    bottomPanel.add(soundCheckBox);
	    bottomPanel.add(flipButton);
	    bottomPanel.add(restartButton);
	    
	    AIListener listener = new AIListener();
	    whiteHuman.addActionListener(listener);
	    blackHuman.addActionListener(listener);
	    whiteAI.addActionListener(listener);
	    blackAI.addActionListener(listener);
	    whiteAI1.addActionListener(listener);
	    whiteAI2.addActionListener(listener);
	    whiteAI3.addActionListener(listener);
	    blackAI1.addActionListener(listener);
	    blackAI2.addActionListener(listener);
	    blackAI3.addActionListener(listener);
	    
	    toolContainer.add(Box.createRigidArea(new Dimension(0, 10)));
	    
	    tools.add(topPanel);
	    tools.add(Box.createRigidArea(new Dimension(0, 20)));
	    tools.add(logicPanel);
	    tools.add(Box.createRigidArea(new Dimension(0, 20)));
	    tools.add(bottomPanel);
	    
	    toolContainer.add(tools);
	    
	    
	    mainPanel = new JPanel();
	    mainPanel.add(boardPanel);
	    mainPanel.add(toolContainer);
	    mainPanel.setBackground(Color.black);
	    
	    
	    add(mainPanel);
	    
	    taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(whiteAI.isSelected() && blackHuman.isSelected())
					boardPanel.moveWhiteAI();
				else if (blackAI.isSelected() && whiteHuman.isSelected())
					boardPanel.moveBlackAI();
				
				if (isLooping == false || whiteAI.isSelected() == false || blackAI.isSelected() == false) {
					((Timer) e.getSource()).stop();
					isLooping = false;
				}
				else {
					if (boardPanel.getPlayerTurn() == true) {
						boardPanel.moveWhiteAI();
					}
					else {
						boardPanel.moveBlackAI();
					}
					
					if (boardPanel.getWinner() != -1) {
						((Timer) e.getSource()).stop();
						isLooping = false;
					}
				}
				repaint();
			}
		  };
	}
	
	public void paintComponent (Graphics page){
		
		super.paintComponent (page);
	      
	    whiteScoreLabel.setText("White's score: " + boardPanel.getWhiteScore());
	    blackScoreLabel.setText("Black's score: " + boardPanel.getBlackScore());
		
	    if (boardPanel.getWinner() == -1) {
			if (boardPanel.whiteCheck())
				infoLabel.setText("White is in check");
			else if (boardPanel.blackCheck())
				infoLabel.setText("Black is in check");
			else
				infoLabel.setText(boardPanel.getTurn() + "'s turn");
		}
		
		else if (boardPanel.getWinner() == 2) {
			infoLabel.setText("Stalemate");
			if(boardPanel.getThreefold() == true)
				infoLabel.setText("Threefold Repetition");
			else if (boardPanel.getDraw() == true)
				infoLabel.setText("Draw");
		}
		
		else if (boardPanel.getWinner() == 0) {
			infoLabel.setText("Black wins");
		}
		else if (boardPanel.getWinner() == 1) {
			infoLabel.setText("White wins");
		}
	      
	}
	
	
	public void actionPerformed(ActionEvent event) {
		
		if (event.getSource() == flipButton) {
			boardPanel.flip();
		}
		
		if (event.getSource() == restartButton) {
			boardPanel.restartGame();
			whiteScoreLabel.setText("White's score: 0");
			blackScoreLabel.setText("Black's score: 0");
			infoLabel.setText("White's Turn");
			whiteHuman.setSelected(true);
			blackHuman.setSelected(true);
			whiteAI1.setSelected(true);
			whiteAI1.setEnabled(false);
			whiteAI2.setEnabled(false);
			whiteAI3.setEnabled(false);
			blackAI1.setSelected(true);
			blackAI1.setEnabled(false);
			blackAI2.setEnabled(false);
			blackAI3.setEnabled(false);
			soundCheckBox.setSelected(false);
			isLooping = false;
		}
		
		if (event.getSource() == soundCheckBox) {
			boardPanel.setSoundCheckBox(soundCheckBox.isSelected());
		}
		
		repaint();
	}
	
	private class AIListener implements ActionListener{

		public void actionPerformed(ActionEvent event) {
			
			Object source = event.getSource();
			
			if (source == whiteHuman) {
				boardPanel.setWhiteAIType(false);
				whiteAI1.setEnabled(false);
				whiteAI2.setEnabled(false);
				whiteAI3.setEnabled(false);
				
				repaint();
			}
			
			else if (source == blackHuman) {
				boardPanel.setBlackAIType(false);
				blackAI1.setEnabled(false);
				blackAI2.setEnabled(false);
				blackAI3.setEnabled(false);
				repaint();
			}
			
			else if (source == whiteAI) {
				boardPanel.setWhiteAIType(true);
				whiteAI1.setEnabled(true);
				whiteAI2.setEnabled(true);
				whiteAI3.setEnabled(true);
				
				repaint();
			}
			
			else if (source == blackAI) {
				boardPanel.setBlackAIType(true);
				blackAI1.setEnabled(true);
				blackAI2.setEnabled(true);
				blackAI3.setEnabled(true);
				
				repaint();
			}
			
			else if (source == whiteAI1)
				boardPanel.setWhiteAILevel(1);
			else if (source == whiteAI2)
				boardPanel.setWhiteAILevel(2);
			else if (source == whiteAI3)
				boardPanel.setWhiteAILevel(3);
			else if (source == blackAI1)
				boardPanel.setBlackAILevel(1);
			else if (source == blackAI2)
				boardPanel.setBlackAILevel(2);
			else if (source == blackAI3)
				boardPanel.setBlackAILevel(3);
		}
		
	}
	
	private class ClickListener implements MouseListener{
		
		public void mousePressed(MouseEvent event) {
			
			if (boardPanel.getPlayerTurn() == true && whiteAI.isSelected() == true && blackAI.isSelected() == false) {
				boardPanel.moveWhiteAI();
			}

			if (boardPanel.getPlayerTurn() == false && blackAI.isSelected() == true && whiteAI.isSelected() == false) {
				boardPanel.moveBlackAI();
			}
			
			if (boardPanel.getWhiteAIType() == true && boardPanel.getBlackAIType() == true) {
				if (isLooping == false) {
					isLooping = true;
					new Timer (DELAY, taskPerformer).start();
				}
			}
			
			repaint();
		}
		
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		
	}
}