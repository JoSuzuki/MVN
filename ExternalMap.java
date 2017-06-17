
class ExternalMap{
  private String programName;
  private String externalName;
  private int internalAddress;
  private int realAddress;
  private boolean defined;

  public ExternalMap(String programName, String externalName, int internalAddress, int realAddress, boolean defined){
    this.programName = programName;
    this.externalName = externalName;
    this.internalAddress = internalAddress;
    this.realAddress = realAddress;
    this.defined = defined;
  }

  public String getProgramName(){
    return this.programName;
  }
  public String getExternalName(){
    return this.externalName;
  }
  public int getInternalAddress(){
    return this.internalAddress;
  }
  public int getRealAddress(){
    return this.realAddress;
  }
  public boolean getDefined(){
    return this.defined;
  }

  public void setProgramName(String programName){
    this.programName = programName;
  }
  public void setExternalName(String externalName){
    this.externalName = externalName;
  }
  public void setInternalAddress(int internalAddress){
    this.internalAddress = internalAddress;
  }
  public void setRealAddress(int realAddress){
    this.realAddress = realAddress;
  }
  public void setDefined(boolean defined){
    this.defined = defined;
  }

}
