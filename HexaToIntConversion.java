import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class HexaToIntConversion {

	 public static void main(String[] args) {
		 try {
			 /*String getTxt;
			 String[] getTxtSplit;
       getTxt = new String(Files.readAllBytes(Paths.get(".\\get.txt")));
       getTxtSplit = getTxt.split("\\s+|#");
			 for(int i=0; i < getTxtSplit.length ;i++ ){
				 System.out.println(getTxtSplit[i]);
			 }*/
			 String hexa = ".\\absolute.txt";
			 String binary = ".\\absoluteb.txt";
			 Scanner scan = new Scanner(new FileInputStream(new File(hexa)));
			 String totaltxt = "";
			 while(scan.hasNextLine()){
				 String line = scan.nextLine();
				 while(line.charAt(0) == '#'){
					 line = scan.nextLine();
				 }
				 totaltxt = totaltxt + line.split("#", 2)[0] + " ";
			 }
			 PrintWriter writer = new PrintWriter(new File(binary));
			 writer.print("");
			 writer.close();
			 String[] splitTxt = totaltxt.split("\\s+");
			 int decimal;
			 for (int i = 0; i < splitTxt.length; i++){
				 decimal =	hexCharToDecimal(splitTxt[i].charAt(0))*16;
				 decimal = decimal + hexCharToDecimal(splitTxt[i].charAt(1));
				 if (splitTxt[i].length() > 2){
					 Files.write(Paths.get(binary), (String.valueOf(decimal)+" ").getBytes(), StandardOpenOption.APPEND);
					 decimal =	hexCharToDecimal(splitTxt[i].charAt(2))*16;
					 decimal = decimal + hexCharToDecimal(splitTxt[i].charAt(3));
					 Files.write(Paths.get(binary), (String.valueOf(decimal)+"\n").getBytes(), StandardOpenOption.APPEND);
				 } else {
					 Files.write(Paths.get(binary), (String.valueOf(decimal)+"\n").getBytes(), StandardOpenOption.APPEND);
				 }
	       //System.out.printf("%d",this.getAccumulator());
			 }
		 }catch(IOException e){
		 	System.out.println("rip");
	 	 }

	}

	public static int hexCharToDecimal(char hex){
		switch(hex){
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'A':
				return 10;
			case 'B':
				return 11;
			case 'C':
				return 12;
			case 'D':
				return 13;
			case 'E':
				return 14;
			case 'F':
				return 15;
			default:
				System.out.println("Oops, this is not a hexadecimal char");
				return 0;
		}
 }

}
