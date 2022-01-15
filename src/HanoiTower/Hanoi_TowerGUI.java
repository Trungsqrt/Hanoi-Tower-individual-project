package HanoiTower;

/*
 *	Author: Mai Quang Trung - 25211208597 
 * 	Class: CMU CS 316 AIS
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Hanoi_TowerGUI extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private int NumOfDisk, delay=-1;
	private int stepCount = 0;
	private boolean signal;

	private ShapePanel drawPanel;
	private JPanel button_Panel;
	private JButton SolveBtn, PlayGameBtn;
	private JButton[] Button;
	private JLabel data;
	private Thread Progress_run;
	private RoundRectangle2D Disk;

	// Setting the GUI
	public Hanoi_TowerGUI() {
		// Setting Window
		super("Hanoi Tower");
		getContentPane().setBackground(new Color(148, 0, 211));
		getContentPane().setForeground(new Color(255, 255, 255));
		drawPanel = new ShapePanel(400, 300);

		// Setting Solve button
		SolveBtn = new JButton("Solve of game");
		SolveBtn.setForeground(Color.WHITE);
		SolveBtn.setBackground(new Color(123, 104, 238));
		
		// Setting PlayGame button
		PlayGameBtn = new JButton("Play Game");
		PlayGameBtn.setForeground(Color.WHITE);
		PlayGameBtn.setBackground(new Color(123, 104, 238));
		
		// Setting buttons
		ButtonHandler bhandler = new ButtonHandler();
		SolveBtn.addActionListener(bhandler);
		PlayGameBtn.addActionListener(bhandler);

		Playgame pgame = new Playgame();
		button_Panel = new JPanel();
		button_Panel.setLayout(new GridLayout(1, 3));

		//Create 3 handle button 
		Button = new JButton[3];
		for (int i = 0; i < 3; i++) {
			Button[i] = new JButton("Tower " + i);
			Button[i].setVisible(false);
			Button[i].setEnabled(false);
			Button[i].addActionListener(pgame);
			button_Panel.add(Button[i]);
		}

		data = new JLabel("Choose to Play or Solve", (int) CENTER_ALIGNMENT);
		data.setBackground(new Color(255, 255, 255));
		data.setForeground(new Color(204, 153, 255));

		Container c = getContentPane();
		c.add(drawPanel, BorderLayout.CENTER);
		c.add(SolveBtn, BorderLayout.EAST);
		c.add(PlayGameBtn, BorderLayout.WEST);
		c.add(button_Panel, BorderLayout.SOUTH);
		c.add(data, BorderLayout.NORTH);

		theTowers = new Tower[3];

		Progress_run = null;

		setSize(580, 420);
		setResizable(false);
		setVisible(true);
	}

	// Tower class
	private class Tower {
		// MAX_WIDTH is width of bottom disk (biggest disk)
		// HEIGHT is distance of stack disks (distance of disks in stack)
		public int MAX_WIDTH = 100, HEIGHT = 25, NumberOfObject_Disk, distance;
		
		private RoundRectangle2D[] disks;
		
		// Constructor
		public Tower(int numOfdisk, int id) {
			disks = new RoundRectangle2D[numOfdisk]; //generate disks
			distance = ( id * (MAX_WIDTH + 5) ) + 20; //distance of towers
		}

		// Put all disks to a tower
		public void fill() {
			int width = MAX_WIDTH;
			// Generate disks, 
			for (int i = 0; i < disks.length; i++) {
				int x = distance + (MAX_WIDTH - width) / 2;
				int y = 300 - ((i + 1) * HEIGHT);
				disks[i] = new RoundRectangle2D.Double(x,y,width,25,0,0);  
				width = width - 10;
			}
			NumberOfObject_Disk = disks.length;
		}

		// Take disk on top 
		public RoundRectangle2D remove() {
			RoundRectangle2D diskmove = disks[NumberOfObject_Disk - 1]; //disk on top
			NumberOfObject_Disk--; //remove disk on top
			return diskmove; 
		}
		
		// Put disk on top
		public void add(RoundRectangle2D diskmove) {
			int width = (int) diskmove.getWidth();
			int x = distance + (MAX_WIDTH - width) / 2; //center align
			int y = 300 - ((NumberOfObject_Disk+1 ) * HEIGHT);
			
			diskmove.setFrame(x, y, width, 25); //set position of disk when put
			
			disks[NumberOfObject_Disk++] = diskmove;
			stepCount++;
		}
		
		// Automation (solve)
		public void move(Tower Tower_Name) {
			RoundRectangle2D diskmove;
			
			//delay between movements
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println(e);
			}

			diskmove = remove();
			Tower_Name.add(diskmove);
			repaint();
		}

		// Delete all current disk on screen
		public boolean isEmpty() {
			return NumberOfObject_Disk == 0;
		}

		// Get width of disk on top
		public int topWidth() {
			return ((int) disks[NumberOfObject_Disk - 1].getWidth());
		}

		public void draw(Graphics2D graphic) {
			for (int i = 0; i < NumberOfObject_Disk; i++) {
				graphic.setColor(new Color(84,139,172)); // color of disk
				graphic.fill(disks[i]);
				graphic.setColor(Color.WHITE); // color of border
				graphic.draw(disks[i]);
			}
		}

	}

	// Setting button
	private class ButtonHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			// When click 'Solve' button
			if (e.getSource() == SolveBtn) { 
				// Enter number of disk
				do {
					String input = JOptionPane.showInputDialog(Hanoi_TowerGUI.this, "How many disks? (<= 10)");
					
					if(input == null || input.equals(""))
						return;
					else
						try {
							NumOfDisk = Integer.parseInt(input);
						} catch (Exception exp) {
							continue;
						}
					
				} while (NumOfDisk < 1 || NumOfDisk > 10);

				// Enter delay time for easy motion tracking , minimum 1000 (Human can see)
				do {
					String input = JOptionPane.showInputDialog(Hanoi_TowerGUI.this, "Delay between moves? (milliseconds)");
					
					if(input == null || input.equals(""))
						return;
					else
						try {
							delay = Integer.parseInt(input);
						} catch (Exception exp) {
							continue;
						}
				} while (delay < 0);
				
				PlayGameBtn.setEnabled(false); // Button Play is disable (be can't click)
				SolveBtn.setEnabled(false); // Button Solve is disable
				
				// Generator 3 Tower
				for (int i = 0; i < 3; i++) /* i=0 is A, i=1 is B and i=2 is C */
					theTowers[i] = new Tower(NumOfDisk, i);

				theTowers[0].fill(); // At the beginning the disks will be placed in column A

				// Start solving progress
				if (Progress_run == null || (!Progress_run.isAlive())) {
					Progress_run = new Thread(Hanoi_TowerGUI.this);
					Progress_run.start();
				}
				repaint();
				// End progress
			} 
			// When click 'Play' button 
			else if (e.getSource() == PlayGameBtn) {
				if (PlayGameBtn.getText() == "Play Game") {
					// Enter number of disk
					do {
						String input = JOptionPane.showInputDialog(Hanoi_TowerGUI.this, "How many disks? (<= 10)");
						
						if(input == null || input.equals(""))
							return;
						else
							try {
								NumOfDisk = Integer.parseInt(input);
							} catch (Exception exp) {
								continue;
							}
					} while (NumOfDisk < 1 || NumOfDisk > 10);
					
					SolveBtn.setEnabled(false); // Button solve is disable

					// Generator 3 column
					for (int i = 0; i < 3; i++)
						theTowers[i] = new Tower(NumOfDisk, i);

					theTowers[0].fill(); // Fill all in tower A when start.

					for (int i = 0; i < 3; i++) {
						Button[i].setVisible(true); // Show 3 button 
					}
					Button[0].setEnabled(true); // When start game just enable 3 button 3 

					signal = false;

					data.setText("Select Tower to -take 1 disk on top- Select other Tower to -put-");
					PlayGameBtn.setText("Stop Game"); // Click 'Stop game' button to stop play
				} 
				else if (PlayGameBtn.getText() == "Stop Game") { // When click stop game
					SolveBtn.setEnabled(true); // Solve button is enable
					for (int i = 0; i < 3; i++) {
						Button[i].setVisible(false);
						Button[i].setEnabled(false);
					}
					data.setText("Choose to Play or Solve");
					PlayGameBtn.setText("Play Game");
					stepCount = 0;
				}
			}
		}
	}

	// Play game event
	private class Playgame implements ActionListener {
		public void actionPerformed(ActionEvent action) {
			// For run until i == button number which action was received from user
			for (int i = 0; i < 3; i++) {
				/*
				 * Signal default is false -> always remove a disk when start game
				 * After that is true -> put a disk in other tower
				 * 
				 * */
				// When button was interfered
				if (action.getSource() == Button[i]) {
					if (!signal) {
						signal = true;
						Disk = theTowers[i].remove(); // Delete a disk at index i and transfer data of this disk to 'Disk'
						int w = (int) Disk.getWidth(); // Get width of this Disk and transfer it to w
									
						/*	If tower j is empty or width of this Disk(disk was removed) smaller than disk on top's width
						 * 		enable this button
						 * 	Else
						 * 		disable this button
						 * */
						for (int j = 0; j < 3; j++) {
							if ( theTowers[j].isEmpty() || w < theTowers[j].topWidth() ) // Establish laws about Hanoi Tower
								Button[j].setEnabled(true);
							else
								Button[j].setEnabled(false);
						}
						data.setText("Select Tower to put disk to");
					} 
					else {
						signal = false;
						theTowers[i].add(Disk);
						for (int j = 0; j < 3; j++) {
							if (!theTowers[j].isEmpty()) 
								Button[j].setEnabled(true);
							else
								Button[j].setEnabled(false);
						}
						data.setText("Select Tower to take disk from");
					}
					repaint();
					
					// Check id the game is finish
					if (theTowers[2].NumberOfObject_Disk == NumOfDisk)
					{
						JOptionPane.showConfirmDialog(null, "Congratulation to you for compeleted the game with "+stepCount+" steps!", "Congratulation", JOptionPane.DEFAULT_OPTION);
						for (int k = 0; k < 3; k++) {
							Button[k].setVisible(false);
							Button[k].setEnabled(false);
						}
						PlayGameBtn.setText("Play Game");
						SolveBtn.setEnabled(true);
						stepCount = 0;
					}
					break;
				}
			}
		}
	}

	private class ShapePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private int pRefWid, pRefHt;

		public ShapePanel(int pWidth, int pHeight) {
			pRefWid = pWidth;
			pRefHt = pHeight;
		}

		public Dimension getPreferredSize() {
			return new Dimension(pRefWid, pRefHt);
		}

		// Generate graphic disk
		public void paintComponent(Graphics graphic) {
			super.paintComponent(graphic);
			Graphics2D graphic2d = (Graphics2D) graphic;
			for (int i = 0; i < 3; i++)
				if (theTowers[i] != null)
					theTowers[i].draw(graphic2d);
		}
	}
		
	private Tower[] theTowers; // 3 array tower are 3 column A B C. Contains the discs.

	// Method to solve Hanoi tower move disk from A ---> C
	public void Solvepuzzle(int n, int A, int B, int C, String a, String b, String c) {
		if (n == 1) {
			theTowers[A].move(theTowers[C]);
			data.setText("Move a disk from tower  -"+a+"-  to tower  -"+c+"- ");
			return;
		}
		else {
			Solvepuzzle(n - 1, A, C, B, a, c, b);
			Solvepuzzle(1,     A, B, C, a, b, c);
			Solvepuzzle(n - 1, B, A, C, b, a, c);
		}
	}

	// When thread start running
	public void run() {
		String A = "A";
		String B = "B";
		String C = "C";
		Solvepuzzle(NumOfDisk, 0, 1, 2, A, B, C);
		PlayGameBtn.setEnabled(true);
		SolveBtn.setEnabled(true);
		data.setText("With "+NumOfDisk+" disks it will take "+stepCount+" steps to solve!");
		stepCount=0;
	}
}