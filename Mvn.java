

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.ArrayList;


class Mvn{

  private int[] memory = new int[65536];
  private int[] disc = new int[131072];
  private int memoryBlockAllocation[] = new int[16]; // says which program is in each block, by index
  private ArrayList<ProgramRegister> programTable = new ArrayList<ProgramRegister>();
  private int missingLogicalBlock;
  private int currentProgram;
  private int executionCounter;
  private int executionLimit;
  private int lastMemoryBlock;
  private int ci;
  private int acceptInterruption;
  private int instructionRegister;
  private int accumulator;
  private int indirect;
  private int index = 0; //used for reading the .txt
	private String[] getTxtSplit; //could be inside the get method, but it would be inefficient
  private Scanner scaninput = new Scanner(System.in);
  public InputDevice inputDevice = new InputDevice();
  public OutputDevice outputDevice = new OutputDevice();


  public Mvn(){
    for(int i = 0; i<65536; i++){
        this.memory[i] = 255;
    }
    for(int i = 0; i < 16; i++){
      this.memoryBlockAllocation[i] = -1;
    }
    this.memoryBlockAllocation[0] = 0;
    this.memoryBlockAllocation[5] = 1;
    try{
      Scanner scan = new Scanner(new FileInputStream(new File(".\\get.txt")));
      String totalTxt = "";
      while(scan.hasNextLine()){
        String line = scan.nextLine();
        totalTxt = totalTxt + line.split("#", 2)[0] + " ";
      }
      this.getTxtSplit = totalTxt.split("\\s+");
    } catch(IOException e) {
      System.out.println("Read get.txt error");
    }
    int[] program0memory = {0,-1,-1};
    int[] program0disc = {4,8,10};
    this.programTable.add(new ProgramRegister(0, 0, 0, program0memory,program0disc));
    int[] program1memory = {-1};
    int[] program1disc = {5};
    this.programTable.add(new ProgramRegister(1, 20480, 0, program1memory,program1disc));


    this.memory[20480] = 0;
    this.memory[20481] = 0;

    this.memory[0] = 176;
    this.memory[1] = 0;
    this.memory[2] = 0;
    this.memory[3] = 4;
    this.memory[4] = 16;
    this.memory[5] = 0;
    this.disc[32768] = 176;
    this.disc[32769] = 0;
    this.disc[32770] = 0;
    this.disc[32771] = 4;
    this.disc[32772] = 32;
    this.disc[32773] = 6;
    this.disc[40966] = 192;
    this.disc[40967] = 80;




    this.instructionRegister = 0;
    this.ci = 0;
    this.executionLimit = 10;
    this.accumulator = 0;
    this.currentProgram = 0;
    this.indirect = 0;
    this.acceptInterruption = 1;
    this.executionCounter = 0;
    this.lastMemoryBlock = 0;
  }

  public void setExecutionLimit(int i){
    this.executionLimit = i;
  }

  public int getMemory(int i){
    return this.memory[i];
  }

  public int getCi(){
    return this.ci;
  }

  public int getAccumulator(){
    return this.accumulator;
  }

  public int getInstructionRegister(){
    return this.instructionRegister;
  }

  public void setMemory(int i, int data){
    this.memory[i] = data;
  }

  public void setDisc(int i, int data){
    this.disc[i] = data;
  }

  public void setCi(int data){
    this.ci = data;
  }

  public void setAccumulator(int data){
    this.accumulator = data;
  }

  public void setInstructionRegister(int data){
    this.instructionRegister = data;
  }

  public int getProgramTableSize(){
    return this.programTable.size();
  }

  public ProgramRegister getProgramRegister(int program){
    for (int i = 0; i < this.programTable.size(); i++){
      if (this.programTable.get(i).getProgramNumber() == program){
        return this.programTable.get(i);
      }
    }
    System.out.printf("There is no program %d", program);
    return this.programTable.get(-1);
  }

  public int getMemoryLocation(int program, int logicalBlock){
    return getProgramRegister(program).getMemoryBlock()[logicalBlock];
  }

  public void loadMissingLogicalBlock(int program, int logicalBlock){
    int avaliableMemoryBlock;
    System.out.printf("Current program: %d \n", program);
    System.out.printf("Looking for logicalBlock: %d \n", logicalBlock);
    ProgramRegister register = getProgramRegister(program);
    avaliableMemoryBlock = unusedMemoryBlock();
    if (avaliableMemoryBlock == -1){
      System.out.println("No more free memory blocks");
      avaliableMemoryBlock = this.lastMemoryBlock;
      this.lastMemoryBlock++;
      System.out.printf("Replacing memory block %d \n", avaliableMemoryBlock);
      if (this.lastMemoryBlock > 15){
        this.lastMemoryBlock = 0;
      }
      ProgramRegister replaceRegister = getProgramRegister(this.memoryBlockAllocation[avaliableMemoryBlock]);
      System.out.printf("Copying position %d from memory to position %d of the disc \n", avaliableMemoryBlock*4096,replaceRegister.getDiscBlock()[replaceRegister.getIndexOfMemoryBlock(avaliableMemoryBlock)]);
      for (int i = 0; i < 4096; i++){
        this.disc[replaceRegister.getDiscBlock()[replaceRegister.getIndexOfMemoryBlock(avaliableMemoryBlock)]*4096 + i] = this.memory[avaliableMemoryBlock*4096 + i];
      }
      replaceRegister.setMemoryBlock(replaceRegister.getIndexOfMemoryBlock(avaliableMemoryBlock), -1);
    }

    System.out.printf("Using memory block %d \n", avaliableMemoryBlock);
    System.out.printf("Copying position %d from disc to position %d of the memory \n",register.getDiscBlock()[logicalBlock]*4096 , avaliableMemoryBlock*4096);
    for (int i = 0; i < 4096; i++){
      this.memory[avaliableMemoryBlock*4096+i] = this.disc[register.getDiscBlock()[logicalBlock]*4096+i];
    }
    register.setMemoryBlock(logicalBlock, avaliableMemoryBlock);
    this.memoryBlockAllocation[avaliableMemoryBlock] = program;
  }

  public int unusedMemoryBlock(){
    for (int i = 0; i < this.memoryBlockAllocation.length; i++){
      if(this.memoryBlockAllocation[i] == -1){
        return i;
      }
    }
    return -1;
  }

  public int getMissingLogicalBlock(){
    return this.missingLogicalBlock;
  }

  public int getCurrentProgram(){
    return this.currentProgram;
  }

  public int getAcceptInterruption(){
    return this.acceptInterruption;
  }

  public void setAcceptInterrution(int number){
    this.acceptInterruption = number;
  }

  public void switchContext(){
    ProgramRegister programRegist;
    int nextProgram;
    programRegist = getProgramRegister(this.currentProgram);
    System.out.printf("Registering ci %d for program %d \n", this.ci, this.currentProgram);
    programRegist.setCi(this.ci);
    programRegist.setAccumulator(this.accumulator);
    if (this.currentProgram == getProgramTableSize()-1){
      nextProgram = 0;
    } else {
      nextProgram = this.currentProgram + 1;
    }
    if (this.programTable.get(nextProgram).getMemoryBlock()[0] == -1){
      loadMissingLogicalBlock(nextProgram, 0);
      this.programTable.get(nextProgram).setCi(this.programTable.get(nextProgram).getMemoryBlock()[0]*4096);
    }
    this.ci = getProgramRegister(nextProgram).getCi();
    this.accumulator = getProgramRegister(nextProgram).getAccumulator();
    this.executionCounter = 0;
  }


  public int executeInstruction(){
    if (this.inputDevice.getBusy() == 1){
      return 0;
    }

    if (this.executionCounter == this.executionLimit) {
      System.out.println("A context switch interruption was issued");
      this.currentProgram = memoryBlockAllocation[this.getCi()/4096];
      return 11;
    }
    this.executionCounter ++;


    int operator ;
    int operationCode;
    int nextCi = 2;
    System.out.printf("\nInstruction execution started \n");
    System.out.printf("Ci: %d \n", this.getCi());
    System.out.printf("For this instruction indirect: %d \n", this.indirect);
    if (this.indirect == 0){
      this.setInstructionRegister((this.getMemory(this.getCi())*256)+this.getMemory(this.getCi()+1)); //fetch
      operationCode = this.getInstructionRegister()/4096; //decode
      operator = (this.getCi()/4096)*4096 + this.getInstructionRegister()%4096; //normal operator
      System.out.printf("Operation Code: %d \n", operationCode);
      System.out.printf("Operator: %d \n", operator);
      nextCi = 2;
    } else {
      this.setInstructionRegister((this.getMemory(this.getCi())*256)+this.getMemory(this.getCi()+1)); //fetch
      operationCode = this.getInstructionRegister()/4096; //decode
      operator = this.getMemory((this.getCi()/4096)*4096 + this.getInstructionRegister()%4096)*256+this.getMemory((this.getCi()/4096)*4096+this.getInstructionRegister()%4096+1); // operator functions as pointer
      System.out.printf("Operation Code: %d \n", operationCode);
      System.out.printf("Operator: %d \n", operator);
      if (operator/4096 != this.getCi()/4096){ //if it is on another block
        int memoryLocation;
        memoryLocation = getMemoryLocation(memoryBlockAllocation[this.getCi()/4096], operator/4096); // find where the logical block (operator/4096) is located in the memory
        if (memoryLocation == -1){ //did not find
          System.out.println("A memory interruption will be necessary");
          this.currentProgram = memoryBlockAllocation[this.getCi()/4096];
          this.missingLogicalBlock = operator/4096; //which block it was searching for
          this.executionCounter --;
          return 10; // send interruption
        }
        operator = memoryLocation*4096 + operator%4096; //relocates the logical block to the memory block
        System.out.printf("Memory location: %d \n", memoryLocation);
        System.out.printf("The operator was dinamically relocated to %d \n", operator);
      }
      nextCi = 4;
      this.indirect = 0;
    }


    switch (operationCode){ //execute
      case 0: //jump unconditional
        this.setCi(operator);
        break;
      case 1: //jump if zero
        if (this.getAccumulator() == 0){
          this.setCi(operator);
        } else {
          this.setCi(this.getCi()+nextCi);
        }
        break;
      case 2: //jump if negative
        if (this.getAccumulator() < 0)
          this.setCi(operator);
        else
          this.setCi(this.getCi()+nextCi);
        break;
      case 3: //load value
        this.setAccumulator(operator);
        this.setCi(this.getCi()+nextCi);
        break;
      case 4: //add
        this.setAccumulator(this.getAccumulator()+getMemory(operator));
        this.setCi(this.getCi()+nextCi);
        break;
      case 5: //subtract
        this.setAccumulator(this.getAccumulator()-getMemory(operator));
        this.setCi(this.getCi()+nextCi);
        break;
      case 6: //multiply
        this.setAccumulator(this.getAccumulator()*getMemory(operator));
        this.setCi(this.getCi()+nextCi);
        break;
      case 7: //divide
        this.setAccumulator(this.getAccumulator()/getMemory(operator));
        this.setCi(this.getCi()+nextCi);
        break;
      case 8: //load from memory
        this.setAccumulator(this.getMemory(operator));
        this.setCi(this.getCi()+nextCi);
        break;
      case 9: //move to memory
        this.setMemory(operator,this.getAccumulator()%256);
        this.setCi(this.getCi()+nextCi);
        break;
      case 10: //subroutine call
        this.setMemory(operator,(this.getCi()+2)/256);
        this.setMemory(operator+1,(this.getCi()+2)%256);
        this.setCi(operator+2);
        break;
      case 11: //indirect
        this.indirect = 1;
        this.setCi(operator+nextCi);
        break;
      case 12: //halt machine
        System.out.println("Halt machine");
        this.setCi(operator);
        return 1;
      case 13: //get data
        /*        Scanner scan = new Scanner(System.in);
        this.setAccumulator(scan.next());
        break; */
        if (operator == 0){
          this.setAccumulator(Integer.valueOf(this.getTxtSplit[index]));
          index++;
        } else if (operator == 1) {
          this.setAccumulator(Integer.valueOf(this.getTxtSplit[index].charAt(0)));
        } else if (operator == 2) {
          this.setAccumulator(Integer.valueOf(this.getTxtSplit[index].charAt(1)));
          index++;
        } else if (operator == 3){
          this.setAccumulator(Integer.valueOf(scaninput.next().charAt(0)));
        } else if (operator == 4){
          if (this.inputDevice.getBusy() != 1){
            this.inputDevice.setBusy(1);
            this.setCi(this.getCi()+nextCi);
            return 12;
          } else {
            return 0;
          }

        }
        this.setCi(this.getCi()+nextCi);

        break;
      case 14: //put data
      try{
        if (operator == 0){
          Files.write(Paths.get(".\\put.txt"), (String.valueOf(this.getAccumulator())+"\n").getBytes(), StandardOpenOption.APPEND);
        } else if (operator == 1) {
          Files.write(Paths.get(".\\put.txt"), ((char)this.getAccumulator()+"").getBytes(), StandardOpenOption.APPEND);
        } else if (operator == 2) {
          Files.write(Paths.get(".\\put.txt"), ((char)this.getAccumulator()+"\n").getBytes(), StandardOpenOption.APPEND);
        } else if (operator == 3){
          System.out.printf("%c",this.getAccumulator());
        } else if (operator == 4){

          if (this.outputDevice.getBusy() != 1){
            this.outputDevice.setBusy(1);
            this.outputDevice.setBuffer(this.getAccumulator());
            this.setCi(this.getCi()+nextCi);
            return 13;

          } else {
            return 0;
          }
        }
        this.setCi(this.getCi()+nextCi);

        //System.out.printf("%d",this.getAccumulator());
      } catch(IOException e){
        System.out.println("Write put.txt error");
      }
        break;
      case 15: //operating system call
        this.setCi(this.getCi()+nextCi);
        return 2;
    }
    return 0;
  }
}
