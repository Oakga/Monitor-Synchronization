import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Passenger extends Thread {
	private int pid, weight, currentTickets, maxTickets;
	private Vector<Ticket> waitingPassengers;
	private Vector<Object> waitingCars;
	private static AtomicInteger noOfPassenger;
	private boolean endFlag;
	public static long time = System.currentTimeMillis();

	public Passenger(int pid, int tickets, Vector waitingPassengers,
			Vector waitingCars, AtomicInteger noOfPassenger) {
		this.pid = pid;
		this.weight = initRandom(70, 200);
		this.currentTickets = tickets;
		this.maxTickets = tickets + 1;
		this.waitingPassengers = waitingPassengers;
		this.waitingCars = waitingCars;
		this.noOfPassenger = noOfPassenger;
		this.endFlag = false;
		sop("with weight " + weight + " is created");
	}// Constructor

	/*
	 * run until the tickets run out. Utilize decide to exit to see if it is
	 * holding the last ticket. If it is , it decrement number of passenger
	 * (noOfPassenger) in the system.
	 */
	public void run() {
		while (currentTickets != 0) {
			waitForRide();
			tour();
			decideToExit();
			currentTickets--;
		}
		end();
	}

	/*
	 * The following are four essential methods
	 */

	/*
	 * When arrive to the platform, it notify the waiting car if any. it create
	 * a ticket and wait on ticket object. This object is visible to the cars to
	 * process the weight to see if it can take the passenger on.
	 */
	private synchronized void waitForRide() {

		sop("is starting to wait for ride at the platform");

		/*
		 * creating Paper object to wait and notifying the car
		 */
		Ticket paper = new Ticket(pid, weight);
		synchronized (paper) {
			waitingPassengers.addElement(paper);
			synchronized (waitingCars) {
				if (!waitingCars.isEmpty()) {
					synchronized (waitingCars.elementAt(0)) {
						sop("is waiting for ride and signal the car");
						waitingCars.elementAt(0).notify();
					}
					waitingCars.removeElementAt(0);

				}
			}

			/*
			 * wait until the car drop the passenger off in the haunted house
			 */
			while (true) {
				try {
					sop("is waiting for ride");
					paper.wait();
					sop("is woke up");
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}

		}
	}

	/*
	 * Tour for random amount of time
	 */
	private void tour() {
		sop("is touring the haunted House");
		int time = initRandom(0, 500);
		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * If it is holding its last remaining ticket, it decrement the
	 * noOfPassenger for car threads to end eventually and set the thread
	 * endFlag to true.
	 */
	private void decideToExit() {
		if (currentTickets == 1) {
			noOfPassenger.decrementAndGet();
			endFlag = true;
			sop("ended due to its last ticket being :" + currentTickets);
		}
	}

	/*
	 * If the passenger exit the system, it wake up all the waiting cars. This
	 * is essential if it is the last passenger in the system.
	 */
	private synchronized void end() {
		sop("is exiting and notifying all waiting Cars");
		if (waitingCars.size() > 0) {
			synchronized (waitingCars) {
				while (!waitingCars.isEmpty()) {
					synchronized (waitingCars.elementAt(0)) {
						waitingCars.elementAt(0).notify();
						waitingCars.removeElementAt(0);
					}
				}
			}
			sop("exited");

		}
	}

	/*
	 * To intilaze random tour time
	 */
	private int initRandom(int min, int max) {

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/*
	 * Print
	 */
	private void sop(String text) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] "
				+ getName() + " |" + "Child " + pid + " | "+text);
	}

}
