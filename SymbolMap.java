
class SymbolMap{
  private String name;
  private int value;
  private boolean defined;

  public SymbolMap(String name, int value, boolean defined){
    this.name = name;
    this.value = value;
    this.defined = defined;
  }

  public String getName(){
    return this.name;
  }
  public int getValue(){
    return this.value;
  }
  public boolean getDefined(){
    return this.defined;
  }

  public void setName(String name){
    this.name = name;
  }
  public void setValue(int value){
    this.value = value;
  }
  public void setDefined(boolean defined){
    this.defined = defined;
  }

}
