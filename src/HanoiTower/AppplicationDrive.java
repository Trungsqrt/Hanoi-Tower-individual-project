package HanoiTower;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppplicationDrive {

	// Main method
		public static void main(String[] args) {
			Hanoi_TowerGUI action = new Hanoi_TowerGUI();
			
			action.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}});
		}

}
