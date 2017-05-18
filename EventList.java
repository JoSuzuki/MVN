public class EventList{
  protected Event start, end;
  private int numberOfEvents;

  public EventList(){
    start = null;
    end = null;
    numberOfEvents = 0;
  }

  public boolean isEmpty(){
    if (start == null && end == null)
      return true;
    else
      return false;
  }

  public void addStart(Event event){
    numberOfEvents += 1;
    if (isEmpty())
      end = event;
    else
      event.next = start;
    start = event;
  }

  public void addEnd(Event event){
    numberOfEvents += 1;
    if (isEmpty())
      start = event;
    else
      end.next = event;
    end = event;
  }

  public int getNumberOfEvents(){
    return numberOfEvents;
  }

  public void next(){
    if (this.numberOfEvents == 0){
      System.out.println("This list ended");
    } else if (this.numberOfEvents == 1){
      start = null;
      end = null;
      numberOfEvents -= 1;
    } else {
      start = start.next;
      numberOfEvents -= 1;
    }
  }


}
