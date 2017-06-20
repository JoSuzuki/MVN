//lul

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Assembler {

public static void main(String[] args) {
  try {
    ArrayList<SymbolMap> mnemonicTable = startMnemonicTable();
    ArrayList<SymbolMap> symbolicTable = new ArrayList<SymbolMap>();
    ArrayList<SymbolMap> externalTable = new ArrayList<SymbolMap>();
    ArrayList<String> entryTable = new ArrayList<String>();
    String symbolicFile;
    String relocableFile;
    int numberPrint ;
    String[] splitLine;
    int ci = 0;
    int dataBlockSize = 0;
    int ext = 0;
    int hasLabel = 0;                      //0 if it does not 1 if it has
    String totaltxt = "";
    String name = "";
		int index;
    Scanner userInput = new Scanner(System.in);
    String selection = "";
    String file;



    System.out.println("Type the name of the file you want to assemble:");
    file = userInput.next();
    symbolicFile = ".\\" + file;
    relocableFile = ".\\relocable_"+file;
    Scanner scan = new Scanner(new FileInputStream(new File(symbolicFile)));


    //Starts first step
    while(scan.hasNextLine()) {
      //read line
      String line = scan.nextLine();
      if(!line.trim().equals("")) {            // if not empty
        if(line.trim().charAt(0) != '#') {    //if not commentary
          line = line.split("#", 2)[0];       // cuts commentary out
          if(line.startsWith(" ")) {         //if it starts with an empty space has no label
            splitLine = line.split("\\s+");
            hasLabel = 1;
          } else {                                        //has a label
            splitLine = line.split("\\s++");
            hasLabel = 1;
            if(!isInTable(splitLine[0], symbolicTable)) {
              symbolicTable.add(new SymbolMap(splitLine[0], ci, true));
            } else if (!isDefinedInTable(splitLine[0], symbolicTable)) {
							index = whereInTable(splitLine[0], symbolicTable);
							symbolicTable.get(index).setValue(ci);
							symbolicTable.get(index).setDefined(true);
            } else {
							System.out.println("This symbol"+splitLine[0]+"was already defined");
						}
          }
          if(splitLine[hasLabel].startsWith("ORG")) {
            ci = hexStringToDecimal(splitLine[hasLabel+1]);
          } else if (splitLine[hasLabel].startsWith("NAME")){
            name = splitLine[hasLabel+1];
          } else if (splitLine[hasLabel].startsWith("EXT")) {
            if(!isInTable(splitLine[hasLabel+1], externalTable)) {
              externalTable.add(new SymbolMap(splitLine[hasLabel+1],ext,true));
              ext++;
            } else {
              System.out.println("This external ("+splitLine[hasLabel+1]+") is already defined");
            }
          } else if (splitLine[hasLabel].startsWith("ENT")) {
            if(!isInTable(splitLine[hasLabel+1], symbolicTable)) {
              entryTable.add(splitLine[hasLabel+1]);
              symbolicTable.add(new SymbolMap(splitLine[hasLabel+1], -1, false));
            } else{
              System.out.println("This entry point ("+splitLine[hasLabel+1]+") was already declared");
            }
          } else if (splitLine[hasLabel].startsWith("DB")) {
            dataBlockSize = dataBlockSize + 3;
            ci++;
          } else if (isInTable(splitLine[hasLabel], mnemonicTable)) {
            if(!splitLine[hasLabel+1].startsWith("/") && !isInTable(splitLine[hasLabel+1], symbolicTable) && !isInTable(splitLine[hasLabel+1], externalTable)){
                symbolicTable.add(new SymbolMap(splitLine[hasLabel+1], -1, false));
            }
            dataBlockSize = dataBlockSize + 3;
            ci = ci + 2;
          } else {

            System.out.println("This ("+splitLine[hasLabel]+") does not classify as a mnemonic or an pseudoinstruction");
          }
        }
      }
    }
    scan.close();
    scan = new Scanner(new FileInputStream(new File(symbolicFile)));

    for (int i = 0; i < symbolicTable.size(); i++){
      if(symbolicTable.get(i).getDefined() == false){
        System.out.println(symbolicTable.get(i).getName() + " has not been defined");
      }
    }

    PrintWriter writer = new PrintWriter(new File(relocableFile));
    int checksum = 0;

    //writes name block
    if(!name.equals("")){
      writer.println("5"); // block size
      checksum = checksum + 5;
      writer.println("1"); // "name" block
      checksum = checksum + 1;
      if(name.equals("main")){
        writer.println("0"); // main program
      } else {
        writer.println("1"); // subroutine program
        checksum = checksum + 1;
      }
      writer.println(Integer.valueOf(name.charAt(0))); // first char
      checksum = checksum + name.charAt(0);
      writer.println(Integer.valueOf(name.charAt(name.length()-1))); // last char
      checksum = checksum + name.charAt(name.length()-1);
      writer.println(checksum % 256);
    }
    //writes entry blocks if it has entry blocks
    checksum = 0;
    for(int i = 0; i < entryTable.size(); i++){
      checksum = 0;
      String entry = entryTable.get(i);
      int entryIndex = whereInTable(entry, symbolicTable);
      writer.println("6"); // block size
      checksum = checksum + 6;
      writer.println("2"); // "entrypoint" block
      checksum = checksum + 2;
      writer.println(Integer.valueOf(entry.charAt(0))); // first char of the entry point name
      checksum = checksum + entry.charAt(0);
      writer.println(Integer.valueOf(entry.charAt(entry.length()-1))); // last char of the entry point name
      checksum = checksum + entry.charAt(entry.length()-1);
      writer.println(symbolicTable.get(entryIndex).getValue()/256); //most significative byte of the address
      checksum = checksum + symbolicTable.get(entryIndex).getValue()/256;
      writer.println(symbolicTable.get(entryIndex).getValue()%256); //least significative byte of the address
      checksum = checksum + symbolicTable.get(entryIndex).getValue()%256;
      writer.println(checksum % 256);
    }
    //writes external block if it has them
    for(int i = 0; i < externalTable.size(); i++){
      String external = externalTable.get(i).getName();
      int externalValue = externalTable.get(i).getValue();
      checksum = 0;
      writer.println("6"); // block size
      checksum = checksum + 6;
      writer.println("3"); // "entrypoint" block
      checksum = checksum + 3;
      writer.println(Integer.valueOf(external.charAt(0))); // first char of the external name
      checksum = checksum + external.charAt(0);
      writer.println(Integer.valueOf(external.charAt(external.length()-1))); // last char of the external name
      checksum = checksum + external.charAt(external.length()-1);
      writer.println(externalValue/256); //most significative byte of the corresponding external
      checksum = checksum + externalValue/256;
      writer.println(externalValue%256); //least significative byte of the corresponding external
      checksum = checksum + externalValue%256;
      writer.println(checksum % 256);
    }

    checksum = 0;
    //writes data block
    writer.println(dataBlockSize+2); // size of the data block
    checksum = checksum + dataBlockSize+2;
    writer.println("4"); // "data" block
    checksum = checksum + 4;
    while(scan.hasNextLine()) {
      //read line
      String line = scan.nextLine();
      if(!line.trim().equals("")) {            // if not empty
        if(line.trim().charAt(0) != '#') {    //if not commentary
          line = line.split("#", 2)[0];       // cuts commentary out
          if(line.startsWith(" ") || line.startsWith("  ")) {         //if it starts with an empty space has no label
            splitLine = line.split("\\s+");
            hasLabel = 1;
          } else {                                        //has a label
            splitLine = line.split("\\s++");
            hasLabel = 1;
          }
          if(splitLine[hasLabel].startsWith("ORG")) {
          } else if (splitLine[hasLabel].startsWith("NAME")){
          } else if (splitLine[hasLabel].startsWith("EXT")) {
          } else if (splitLine[hasLabel].startsWith("ENT")) {
          } else if (splitLine[hasLabel].startsWith("DB")) {
            writer.print("03 ");
            checksum = checksum + 3;
            writer.print(hexStringToDecimal(splitLine[hasLabel+1]));
            writer.print(" 00 \n");
            checksum = checksum + Integer.valueOf(hexStringToDecimal(splitLine[hasLabel+1]));
          } else if (isInTable(splitLine[hasLabel], mnemonicTable)) {
            numberPrint = mnemonicTable.get(whereInTable(splitLine[hasLabel], mnemonicTable)).getValue() * 4096;
            if(splitLine[hasLabel+1].startsWith("/")){
              numberPrint = numberPrint + hexStringToDecimal(splitLine[hasLabel+1].substring(1));
              writer.print("00 ");
            } else if (isInTable(splitLine[hasLabel+1], symbolicTable)){
              numberPrint = numberPrint + symbolicTable.get(whereInTable(splitLine[hasLabel+1], symbolicTable)).getValue();
              writer.print("01 ");
              checksum = checksum + 1;
            } else if (isInTable(splitLine[hasLabel+1], externalTable)){
              numberPrint = numberPrint + externalTable.get(whereInTable(splitLine[hasLabel+1], externalTable)).getValue();
              writer.print("02 ");
              checksum = checksum + 2;
            } else {
              System.out.println("Something went wrong in the datablock write step");
            }
            writer.print(numberPrint/256);
            writer.print(" ");
            writer.print(numberPrint%256);
            writer.print(" \n");
            checksum = checksum + numberPrint/256 + numberPrint%256;
          } else {
            System.out.println("This ("+splitLine[hasLabel]+") does not classify as a mnemonic or an pseudoinstruction write step");
          }
        }
      }
    }
    writer.print(checksum%256);



    writer.close();


  }catch(IOException e) {
    System.out.println("rip");
  }

}





public static boolean isInTable(String symbol, ArrayList<SymbolMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(symbol.equals(table.get(i).getName())) {
      return true;
    }
  }
  return false;
}

public static boolean isDefinedInTable(String symbol, ArrayList<SymbolMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(symbol.equals(table.get(i).getName())) {
      return table.get(i).getDefined();
    }
  }
  return false;
}

public static int whereInTable(String symbol, ArrayList<SymbolMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(symbol.equals(table.get(i).getName())) {
      return i;
    }
  }
  return -1;
}

public static ArrayList<SymbolMap> startMnemonicTable(){
  ArrayList<SymbolMap> mnemonicTable = new ArrayList<SymbolMap>();
  mnemonicTable.add(new SymbolMap("JP", 0, true));
  mnemonicTable.add(new SymbolMap("JZ", 1, true));
  mnemonicTable.add(new SymbolMap("JN", 2, true));
  mnemonicTable.add(new SymbolMap("LV", 3, true));
  mnemonicTable.add(new SymbolMap("+", 4, true));
  mnemonicTable.add(new SymbolMap("-", 5, true));
  mnemonicTable.add(new SymbolMap("*", 6, true));
  mnemonicTable.add(new SymbolMap("/", 7, true));
  mnemonicTable.add(new SymbolMap("LD", 8, true));
  mnemonicTable.add(new SymbolMap("MM", 9, true));
  mnemonicTable.add(new SymbolMap("SC", 10, true));
  mnemonicTable.add(new SymbolMap("RS", 11, true));
  mnemonicTable.add(new SymbolMap("HM", 12, true));
  mnemonicTable.add(new SymbolMap("GD", 13, true));
  mnemonicTable.add(new SymbolMap("PD", 14, true));
  mnemonicTable.add(new SymbolMap("OS", 15, true));
  return mnemonicTable;
}

public char intToHex(int number){
  char hex;
  int digit;
  if (number >= 0 && number <= 9){
    hex = (char)(number+'0');
  } else if (number == 10) {
    hex = 'A';
  } else if (number == 11) {
    hex = 'B';
  } else if (number == 12){
      hex = 'C';
  } else if (number == 13){
    hex = 'D';
  } else if (number == 14){
    hex = 'E';
  } else if (number == 15){
    hex = 'F';
  } else if (number < 0) {
    System.out.println("This is a problem, no number should be negative");
    hex = 'F';
  } else {
    System.out.println("Oops, this number is higher than 15");
    hex = 'F';
  }
  return hex;
}

public static int hexStringToDecimal(String hex){
  int decimal = 0;
  for(int i = 0; i < hex.length(); i ++){
    decimal = decimal*16;
    decimal = decimal + hexCharToDecimal(hex.charAt(i));
  }
  return decimal;
}

public static int hexCharToDecimal(char hex){
  switch(hex) {
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
