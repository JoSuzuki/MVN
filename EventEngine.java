import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

class EventEngine{

  private EventList eventList;
  private boolean debug = false;
  private Mvn mvn = new Mvn();
  private int breakpoint = 4096;
  public EventEngine(){
    this.eventList = new EventList();
  }
  public void eventReaction(Event event){
    int kind = event.getKind();
    switch (kind){
      case 0: //execute next instruction
        this.mvn.executeInstruction();
        if (this.mvn.getInstructionRegister()/4096 != 12 && this.mvn.getInstructionRegister()/4096 != 15 && this.mvn.getCi() != breakpoint){
          if(this.debug){
            this.addEvent(1,0,0);
          }
          this.addEvent(0,0,0);
        }
        break;
      case 1: //display MVN state
        this.printMvnState();
        break;
      case 2: //display MVN memory
        this.printMvnMemory();
        break;
      case 3: //set a breakpoint
        this.setBreakpoint();
        break;
      case 4: //toogle trace
        this.toogleTrace();
        break;
      case 5: //set ci
        this.setCi();
        break;
      case 6: //edit memory
        this.editMemory();
        break;
      case 7: //load memory
        this.loadMemory();
    }
  }

  public void setCi(){
      Scanner scan = new Scanner(System.in);
      System.out.println("Current ci(decimal): "+this.mvn.getCi());
      System.out.println("Set ci(decimal): ");
      this.mvn.setCi(scan.nextInt());
  }

  public void toogleTrace(){
      this.setDebug(!this.getDebug());
      System.out.println("Trace: " + debug);
  }

  public void setBreakpoint(){
    Scanner scan = new Scanner(System.in);
    System.out.println("Set breakpoint: ");
    breakpoint = scan.nextInt();
  }

  public void loadMemory(){
    Scanner scan = new Scanner(System.in);
    System.out.println("Where do you want to start?(decimal)");
    int index = scan.nextInt();
    try{
      scan = new Scanner(new FileInputStream(new File(".\\memory.txt")));
      String memoryTxt = "";
      while(scan.hasNextLine()){
        String line = scan.nextLine();
        memoryTxt = memoryTxt + line.split("#", 2)[0] + " ";
      }
      String[] memorySplitTxt = memoryTxt.split("\\s+");
    for (int i = index; i < memorySplitTxt.length + index; i++ ){
        this.mvn.setMemory(i,Integer.valueOf(memorySplitTxt[i-index]));
      }
    } catch (IOException e){
      System.out.println("Read memory.txt error");
    }
  }

  public void editMemory(){

    Scanner scan = new Scanner(System.in);
    int editAddress;
    int edit = 1;
    while (edit == 1){
      System.out.println("Current memory: ");
      this.printMvnMemory();
      System.out.println("\nWhich memory position would you like to change? (decimal) ");
      editAddress = scan.nextInt();
      System.out.println("Current content(hexa):");
      printHexa(this.mvn.getMemory(editAddress),2);
      System.out.println("\nWhat do you want to put? (decimal)");
      this.mvn.setMemory(editAddress,scan.nextInt()%256);
      System.out.println("Updated content(hexa):");
      printHexa(this.mvn.getMemory(editAddress),2);
      System.out.println("\nDo you want to keep editing?\n0.No\n1.Yes");
      edit = scan.nextInt();
    }
  }

  public void addEvent(int kind,int task, int time){
    Event event = new Event(kind,task,time);
    this.eventList.addEnd(event);
  }

  public boolean getDebug(){
    return this.debug;
  }
  public void setDebug(boolean debug){
    this.debug = debug;
  }

  public void run(){
    while (!this.eventList.isEmpty()){
        this.eventReaction(this.eventList.start);
        this.eventList.next();
    }
  }

  public void printMvnMemory(){
    System.out.printf("    ");
    for (int j = 0; j < 16;j++){
      System.out.printf(" ");
      printHexa(j,1);
      System.out.printf(" ");
    }
    for (int i = 0; i < 256; i++){
      System.out.printf("\n");
      printHexa(i*16,3);
      System.out.printf(" ");
      for (int j = 0; j < 16;j++){
        System.out.printf(" ");
        printHexa(this.mvn.getMemory(i*16+j),2);
      }
    }
  }

  public void printHexa(int numberToPrint,int n){
    /*Receives an int and prints
      it in n hexa numbers, ignoring if they are bigger*/
    char print[] = new char[n];
    int aux = 1;
    if (numberToPrint < 0){
      for (int i = 0; i<n ;i++){
        aux = aux*16;
      }
      numberToPrint += aux;
    }
    for (int i = 0; i < n;i ++){
      aux = 1;
      for(int j=((n-1)-i);j>0;j--)
        aux = aux*16; //4096,256,16,1
      print[i] = intToHex((numberToPrint/(aux))%16);
    }
    for (int i=0; i < n; i++){
      System.out.printf("%c",print[i]);
    }
  }


  public void printMvnState(){

    Scanner scan = new Scanner(System.in);
    System.out.printf("\nAccumulator: ");
    printHexa(this.mvn.getAccumulator(),4);
    System.out.printf(" (decimal:%d)",this.mvn.getAccumulator());
    System.out.printf("\nCi: ");
    printHexa(this.mvn.getCi(),4);
    System.out.printf("\nInstruction register: ");
    printHexa(this.mvn.getInstructionRegister(),8);
    System.out.printf("\nNext Instruction: ");
    printHexa(this.mvn.getMemory(this.mvn.getCi()),2);
    printHexa(this.mvn.getMemory(this.mvn.getCi()+1),2);
    System.out.println("\nPress ENTER to continue");
    scan.nextLine();
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

  public int hexCharToDecimal(char hex){
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
