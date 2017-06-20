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


public class Linker {

public static void main(String[] args) {
  try {
    Scanner userInput = new Scanner(System.in);
    ArrayList<ExternalMap> externalTable = new ArrayList<ExternalMap>();
    ArrayList<EntryMap> entryTable = new ArrayList<EntryMap>();
    ArrayList<String> files = new ArrayList<String>();
    int selection = 0;
    int org = 0;
    String file;
    String absoluteFile = ".\\absolute.txt";
    String root = ".\\";
    boolean main = false;
    String relocableFile;
    Scanner scan;
    String holeFile = "";
    String[] split;
    String line;
    String moduleName = "";
    String externalName = "";
    String entryName = "";
    int totalProgramSize = 0;
    int size;
    int index = 0;
    int checksum = 0;
    int internalAddress;
    int realAddress;


    while(selection == 0){
      System.out.println("0. Add new file");
      System.out.println("1. End selection");
      System.out.println("Type the number you want:");
      selection = userInput.nextInt();
      if(selection == 0){
        System.out.println("Type the name of the file you want to link: ");
        file = userInput.next();
        files.add(root + file);
      }
    }
    System.out.println("Where do you want your program to start? (decimal)");
    org = userInput.nextInt();

    //Starts first step
    for(int j = 0; j < files.size() ; j ++){
      relocableFile = files.get(j);
      scan = new Scanner(new FileInputStream(new File(relocableFile)));
      holeFile = "";
      moduleName = "";
      externalName = "";
      entryName = "";
      index = 0;
      checksum = 0;
      //Makes the hole file into one big string
      while(scan.hasNextLine()){
        line = scan.nextLine();
        holeFile = holeFile + line + " ";
      }
      split = holeFile.split("\\s+");
      int[] splitFile = new int[split.length];
      for (int i = 0; i < split.length ; i++){
        splitFile[i] = Integer.valueOf(split[i]);
      }

      while (index < splitFile.length){
        checksum = 0;
        size = splitFile[index];
        checksum = checksum + size;

        if(splitFile[index+1] == 1){   // reads name block
          checksum = checksum + splitFile[index+1];
          if(splitFile[index+2] == 0){
            if(main == false){
              main = true;
            } else {
              System.out.println("Error: main was already defined");
            }
          }
          checksum = checksum + splitFile[index+2];
          moduleName = moduleName + (char)splitFile[index+3] + (char)splitFile[index+4];
          checksum = checksum + splitFile[index+3] + splitFile[index+4];

        } else if (splitFile[index+1] == 2){ // reads entry block
          checksum = checksum + splitFile[index+1];
          entryName = "";
          entryName = entryName + (char)splitFile[index+2] + (char)splitFile[index+3];
          checksum = checksum + splitFile[index+2] + splitFile[index+3];
          realAddress = splitFile[index+4]*256 + splitFile[index+5];
          checksum = checksum + splitFile[index+4] + splitFile[index+5];
          if(isInEntryTable(entryName, entryTable) == false){
            entryTable.add(new EntryMap(entryName, realAddress + totalProgramSize));
          } else {
            System.out.println("This entry point ("+entryName+") has already been defined");
          }
        } else if (splitFile[index+1] == 3){ // reads external block
          checksum = checksum + splitFile[index+1];
          externalName = "";
          externalName = externalName + (char)splitFile[index+2] + (char)splitFile[index+3];
          checksum = checksum + splitFile[index+2] + splitFile[index+3];
          internalAddress = splitFile[index+4]*256 + splitFile[index+5];
          checksum = checksum + splitFile[index+4] + splitFile[index+5];
          if(isInExternalTable(moduleName, externalName, externalTable) == false){
            externalTable.add(new ExternalMap(moduleName, externalName, internalAddress));
          } else {
            System.out.println("This external ("+externalName+","+moduleName+") has already been defined in this module");
          }
        } else if (splitFile[index+1] == 4){ // reads data block
          checksum = checksum + splitFile[index+1];

          for (int i = 2; i < size ; i = i + 3){
            if(splitFile[index+i] == 0){
              totalProgramSize = totalProgramSize + 2;
            } else if(splitFile[index+i] == 1){
              totalProgramSize = totalProgramSize + 2;
            } else if(splitFile[index+i] == 2){
              totalProgramSize = totalProgramSize + 2;
            } else if(splitFile[index+i] == 3){
              totalProgramSize = totalProgramSize + 1;
            } else {
              System.out.println("This is not a type of address (absolute, relocable, external or define byte)");
            }
            checksum = checksum + splitFile[index + i] + splitFile[index + i + 1] + splitFile[index + i + 2];
          }
        }
        // checks if checksum is ok
        if(checksum%256 != splitFile[index+size]){
          System.out.println("Error: Checksum for \""+moduleName+"\" "+splitFile[index+1]+" does not match");
          System.out.println("Expected: "+splitFile[index+size]);
          System.out.println("Calculated: "+checksum%256);
        }
        index = index + size + 1;
      }
    }

    for (int i = 0; i < externalTable.size() ; i++){
      if(isInEntryTable(externalTable.get(i).getExternalName(), entryTable) == false){
        System.out.println("The external "+externalTable.get(i).getExternalName()+" from "+externalTable.get(i).getProgramName()+" is not defined in the entryTable");
      }
    }

    PrintWriter writer = new PrintWriter(new File(absoluteFile));
    int checksumAbsolute = 0;
    int numberPrint;
    int counter = 0;
    int originalOrg = org;
    int newOrg = org;


    writer.printf("%02X %02X \n", org/256, org%256);
    writer.printf("%02X %02X \n", totalProgramSize/256, totalProgramSize%256);

    for(int j = 0; j < files.size() ; j ++){
      relocableFile = files.get(j);
      scan = new Scanner(new FileInputStream(new File(relocableFile)));
      holeFile = "";
      moduleName = "";
      externalName = "";
      entryName = "";
      index = 0;
      org = newOrg;
      //Makes the hole file into one big string
      while(scan.hasNextLine()){
        line = scan.nextLine();
        holeFile = holeFile + line + " ";
      }
      split = holeFile.split("\\s+");
      int[] splitFile = new int[split.length];
      for (int i = 0; i < split.length ; i++){
        splitFile[i] = Integer.valueOf(split[i]);
      }

      while (index < splitFile.length){
        size = splitFile[index];

        if(splitFile[index+1] == 1){   // reads name block
          moduleName = moduleName + (char)splitFile[3] + (char)splitFile[4];
        } else if (splitFile[index+1] == 2){ // reads entry block
        } else if (splitFile[index+1] == 3){ // reads external block
        } else if (splitFile[index+1] == 4){ // reads data block
          for (int i = 2; i < size ; i = i + 3){
            if(splitFile[index+i] == 0){
              numberPrint = splitFile[index+i+1]*256 + splitFile[index+i+2];
              writer.printf("%02X ", numberPrint/256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint/256, writer);
              counter = reset(counter);
              writer.printf("%02X \n", numberPrint%256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint%256, writer);
              counter = reset(counter);
              newOrg = newOrg + 2;
            } else if(splitFile[index+i] == 1){
              numberPrint = splitFile[index+i+1]*256 + splitFile[index+i+2] + org;
              writer.printf("%02X ", numberPrint/256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint/256, writer);
              counter = reset(counter);
              writer.printf("%02X \n", numberPrint%256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint%256, writer);
              counter = reset(counter);
              newOrg = newOrg + 2;
            } else if(splitFile[index+i] == 2){
              numberPrint = (splitFile[index+i+1]/16)*4096 + getExternalAddress(moduleName, splitFile[index+i+1]*256 + splitFile[index+i+2] - (splitFile[index+i+1]/16)*4096, externalTable, entryTable) + originalOrg;
              writer.printf("%02X ", numberPrint/256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint/256, writer);
              counter = reset(counter);
              writer.printf("%02X \n", numberPrint%256);
              counter = counter + 1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint%256, writer);
              counter = reset(counter);
              newOrg = newOrg + 2;
            } else if(splitFile[index+i] == 3){
              numberPrint = splitFile[index+i+1];
              writer.printf("%02X \n", numberPrint);
              counter = counter +1;
              checksumAbsolute = addAndPrint(counter, checksumAbsolute, numberPrint, writer);
              counter = reset(counter);
              newOrg = newOrg+1;
            }
          }
        }
        index = index + size + 1;
      }
    }

    writer.printf("%02X \n", checksumAbsolute%256);
    writer.close();


  }catch(IOException e) {
    System.out.println("rip");
  }

}


public static int addAndPrint(int counter, int checksumAbsolute, int numberAdd, PrintWriter writer){
  checksumAbsolute = checksumAbsolute + numberAdd;
  if(counter == 127){
    writer.printf("\n%02X \n", checksumAbsolute%256);
    checksumAbsolute = 0;
  }
  return checksumAbsolute;
}

public static int reset(int counter){
  if(counter == 127){
    return 0;
  }
  return counter;
}


public static boolean isInEntryTable(String entryName, ArrayList<EntryMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(entryName.equals(table.get(i).getEntryName())) {
      return true;
    }
  }
  return false;
}

public static boolean isInExternalTable(String moduleName, String externalName, ArrayList<ExternalMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(moduleName.equals(table.get(i).getProgramName()) && externalName.equals(table.get(i).getExternalName())) {
      return true;
    }
  }
  return false;
}


public static int getExternalAddress(String moduleName, int internalAddress, ArrayList<ExternalMap> externalTable, ArrayList<EntryMap> entryTable){
  for(int i = 0; i < externalTable.size(); i++){
    if(externalTable.get(i).getInternalAddress() == internalAddress && externalTable.get(i).getProgramName().equals(moduleName)){
      for(int j = 0; j < entryTable.size(); j++){
        if(entryTable.get(j).getEntryName().equals(externalTable.get(i).getExternalName())){
          return entryTable.get(j).getRealAddress();
        }
      }
    }
  }
  return -4097;
}

public static int whereInTable(String symbol, ArrayList<ExternalMap> table){
  for(int i = 0; i < table.size(); i++) {
    if(symbol.equals(table.get(i).getExternalName())) {
      return i;
    }
  }
  return -1;
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
