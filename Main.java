import java.util.Scanner;


public class Main {
	 public static void main(String[] args) {
		 EventEngine eventEngine = new EventEngine();
		 Scanner scan = new Scanner(System.in);
		 int selection;
		 System.out.println("\nWelcome to the simulation of a MVN ");
		 while(true){
			 System.out.println("\n0. Start MVN execution ");
			 System.out.println("1. See MVN state ");
			 System.out.println("2. See MVN memory ");
			 System.out.println("3. Set a breakpoint ");
			 System.out.println("4. Toogle trace state");
			 System.out.println("5. Edit ci ");
			 System.out.println("6. Manually edit memory ");
			 System.out.println("7. Load memory file \n");
			 System.out.println("Type the number you want:");
			 selection = scan.nextInt();
			 eventEngine.addEvent(selection,0,0);
			 eventEngine.run();
		 }
	 }
}
