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
    ArrayList<SymbolMap> mnemonicTable = new ArrayList<SymbolMap>();
    ArrayList<SymbolMap> symbolicTable = new ArrayList<SymbolMap>();
    ArrayList<SymbolMap> externalTable = new ArrayList<SymbolMap>();
    ArrayList<String> entryTable = new ArrayList<String>();
    String symbolicFile = ".\\symbolic.txt";
    String absoluteFile = ".\\absolute.txt";
    Scanner scan = new Scanner(new FileInputStream(new File(symbolicFile)));
    String[] splitLine;
    int ci = 0;
    int ext = 0;
    int hasLabel = 0;                      //0 if it does not 1 if it has
    String totaltxt = "";
		int index;
    //Starts first step
    while(scan.hasNextLine()) {
      //read line
      String line = scan.nextLine();
      if(!line.trim().equals("")) {            // if not empty
        if(line.trim().charAt(0) != '#') {    //if not commentary
          line = line.split("#", 2)[0];       // cuts commentary out
          if(line.startsWith(" ")) {         //if it starts with an empty space has no label
            splitLine = line.split("\\s+");
            hasLabel = 0;
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
            ci = Integer.valueOf(splitLine[hasLabel+1]);
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
            ci++;
          } else if (isInTable(splitLine[hasLabel], mnemonicTable)) {
            if(!splitLine[hasLabel+1].startsWith("\\") && !isInTable(splitLine[hasLabel+1], symbolicTable)){
                symbolicTable.add(new SymbolMap(splitLine[hasLabel+1], -1, false));
            }
            ci = ci + 2;
          } else {
            System.out.println("This ("+splitLine[0]+") does not classify as a mnemonic or an pseudoinstruction");
          }
        }
      }
    }


    PrintWriter writer = new PrintWriter(new File(absoluteFile));
    writer.print("");
    writer.close();
    String[] splitTxt = totaltxt.split("\\s+");
    int decimal;
    for (int i = 0; i < splitTxt.length; i++) {
      decimal =      hexCharToDecimal(splitTxt[i].charAt(0))*16;
      decimal = decimal + hexCharToDecimal(splitTxt[i].charAt(1));
      if (splitTxt[i].length() > 2) {
        Files.write(Paths.get(absoluteFile), (String.valueOf(decimal)+" ").getBytes(), StandardOpenOption.APPEND);
        decimal =      hexCharToDecimal(splitTxt[i].charAt(2))*16;
        decimal = decimal + hexCharToDecimal(splitTxt[i].charAt(3));
        Files.write(Paths.get(absoluteFile), (String.valueOf(decimal)+"\n").getBytes(), StandardOpenOption.APPEND);
      } else {
        Files.write(Paths.get(absoluteFile), (String.valueOf(decimal)+"\n").getBytes(), StandardOpenOption.APPEND);
      }
      //System.out.printf("%d",this.getAccumulator());
    }
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
