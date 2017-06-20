
class EntryMap{
  private String entryName;
  private int realAddress;
  private boolean defined;

  public EntryMap(String entryName, int realAddress){
    this.entryName = entryName;
    this.realAddress = realAddress;
  }

  public String getEntryName(){
    return this.entryName;
  }
  public int getRealAddress(){
    return this.realAddress;
  }

  public void setEntryName(String entryName){
    this.entryName = entryName;
  }
  public void setRealAddress(int realAddress){
    this.realAddress = realAddress;
  }


}
