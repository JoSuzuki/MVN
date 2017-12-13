

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.ArrayList;


class OutputDevice{
  private int busy;
  private int ready;
  private int buffer;

  public OutputDevice(){
    this.busy =  0;
    this.ready = 0;
    this.buffer = 0;
  }

  public void setBusy(int number){
    this.busy = number;
  }

  public void setReady(int number){
    this.ready = number;
  }

  public void setBuffer(int number){
    this.buffer = number;
  }

  public int getBusy(){
    return this.busy;
  }

  public int getReady(){
    return this.ready;
  }

  public int getBuffer(){
    return this.buffer;
  }

  public void printBuffer(){
    System.out.printf("%d",this.buffer);
  }


}
