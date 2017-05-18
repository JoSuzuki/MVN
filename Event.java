class Event{
  private int kind;
  private int task;
  private int time; // Not implemented
  protected Event next;

  public Event(int kind, int task, int time){
    this.kind = kind;
    this.task = task;
    this.time = time;
    this.next = null;
  }

  public int getKind(){
    return kind;
  }
  public int getTask(){
    return task;
  }
  public int getTime(){
    return time;
  }

  public void setKind(int kind){
    this.kind = kind;
  }
  public void setTask(int task){
    this.task = task;
  }
  public void setTime(int time){
    this.time = time;
  }

}
