
class ExternalMap{
  private String programName;
  private String externalName;
  private int internalAddress;
  private boolean defined;

  public ExternalMap(String programName, String externalName, int internalAddress){
    this.programName = programName;
    this.externalName = externalName;
    this.internalAddress = internalAddress;
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

  public void setProgramName(String programName){
    this.programName = programName;
  }
  public void setExternalName(String externalName){
    this.externalName = externalName;
  }
  public void setInternalAddress(int internalAddress){
    this.internalAddress = internalAddress;
  }


}
