

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

class Mvn{

  private int memory[] = new int[65536];
  private int disc[] = new int[131072];
  private int currentBlock;
  private int currentProgram;
  private int ci;
  private int instructionRegister;
  private int accumulator;
  private int indirect;
  private int index = 0; //used for reading the .txt
	private String[] getTxtSplit; //could be inside the get method, but it would be inefficient
  private Scanner scaninput = new Scanner(System.in);

  public Mvn(){
    for(int i = 0; i<65536; i++){
        this.memory[i] = 255;
    }


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
    this.instructionRegister = 0;
    this.ci = 0;
    this.accumulator = 0;
    this.currentBlock = 0;
    this.currentProgram = 0;
    this.indirect = 0;
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

  public void setCi(int data){
    this.ci = data;
  }

  public void setAccumulator(int data){
    this.accumulator = data;
  }

  public void setInstructionRegister(int data){
    this.instructionRegister = data;
  }


  public void executeInstruction(){
    int operator ;
    int operationCode;
    int nextCi = 2;
    if (this.indirect == 0){
      this.setInstructionRegister((this.getMemory(this.getCi())*256)+this.getMemory(this.getCi()+1)); //fetch
      operationCode = this.getInstructionRegister()/4096; //decode
      operator = this.getInstructionRegister()%4096; //normal operator
      nextCi = 2;
    } else {
      this.setInstructionRegister((this.getMemory(this.getCi())*256)+this.getMemory(this.getCi()+1)); //fetch
      operationCode = this.getInstructionRegister()/4096; //decode
      operator = this.getMemory((this.getCi()+2)*256)+this.getMemory(this.getCi()+3); //next instruction is operator
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
        break;
      case 12: //halt machine
        this.setCi(operator);
        break;
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
          this.setAccumulator(scaninput.nextInt());
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
          System.out.printf("%d",this.getAccumulator());
        }
        //System.out.printf("%d",this.getAccumulator());
      } catch(IOException e){
        System.out.println("Write put.txt error");
      }
        this.setCi(this.getCi()+nextCi);
        break;
      case 15: //operating system call
        this.setCi(this.getCi()+nextCi);
        break;
    }
  }
}
