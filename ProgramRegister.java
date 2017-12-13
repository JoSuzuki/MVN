
class ProgramRegister{
  private int programNumber;
  private int ci;
  private int accumulator;
  private int[] memoryBlock;
  private int[] discBlock;

  public ProgramRegister(int programNumber, int ci, int accumulator, int[] memoryBlock,int[] discBlock){
    this.programNumber = programNumber;
    this.ci = ci;
    this.accumulator = accumulator;
    this.memoryBlock = memoryBlock;
    this.discBlock = discBlock;
    //this.discBlock = discBlock;
  }

  public int getCi(){
    return this.ci;
  }
  public int getAccumulator(){
    return this.accumulator;
  }

  public void setCi(int ci){
    this.ci = ci;
  }
  public void setAccumulator(int accumulator){
    this.accumulator = accumulator;
  }


  public int getProgramNumber(){
    return this.programNumber;
  }

  public int getIndexOfMemoryBlock(int value){
    for (int i = 0; i < this.memoryBlock.length; i++){
      if (this.memoryBlock[i] == value){
        return i;
      }
    }  
    return -1;
  }


  public int[] getMemoryBlock(){
    return this.memoryBlock;
  }
  public int[] getDiscBlock(){
    return this.discBlock;
  }

  public void setMemoryBlock(int logicalBlock, int memoryBlock){
    this.memoryBlock[logicalBlock] = memoryBlock;
  }
  public void setDiscBlock(int logicalBlock, int discBlock){
    this.discBlock[logicalBlock] = discBlock;
  }

}
