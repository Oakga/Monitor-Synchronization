/*This class is for creating an object that passenger threads will block on.
 * Cars threads will use these objects to identify which passenger thread it is 
 * carrying on the trip.
 */
public class Ticket {
	public int pid, weight;
	
	public Ticket(int pid, int weight) {
		this.pid = pid;
		this.weight = weight;
	}
	
	public void display(){
		sop("Paper pid: "+pid+" ,weight: "+weight);
	}
	
	private void sop(String text) {
		System.out.println(text);
	}
}
