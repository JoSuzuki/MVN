

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.ArrayList;


class InputDevice{
  private int busy;
  private int ready;
  private int buffer;

  public InputDevice(){
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

  public void setBuffer(){
    Scanner scaninput = new Scanner(System.in);
    this.buffer = scaninput.nextInt();
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


}
