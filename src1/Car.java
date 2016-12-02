import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Car extends Thread {
	int cid, limit, cWeight;
	boolean goFlag, endFlag;
	private Vector<Ticket> waitingPassengers, ridingPassengers;
	private Vector<Object> waitingCars;
	private static AtomicInteger noOfPassenger;
	public static long time = System.currentTimeMillis();

	public Car(int cid, Vector waitingPassengers, Vector waitingCars,
			AtomicInteger noOfPassenger) {
		this.cid = cid;
		this.limit = 1000;
		this.cWeight = limit;
		this.goFlag = false;
		this.endFlag = false;
		this.waitingPassengers = waitingPassengers;
		this.waitingCars = waitingCars;
		this.ridingPassengers = new Vector();
		this.noOfPassenger = noOfPassenger;
		sop("with Weight limit " + cWeight + " is created");
	}// constructor

	/*
	 * Utilize two flags : end flag and go Flag end Flag is set to true if the
	 * thread should end go flag is set to true if the car thread should start
	 * wandering in the house
	 */
	public void run() {
		sop("start execution");

		while (noOfPassenger.get() > 0) {
			while (endFlag == false && goFlag == false) {
				load();
			}
			if (endFlag == false) {
				wander();
				unload();
			}
		}
	}

	/*
	 * The following are three essential methods
	 */

	/*
	 * Car will loads as following: check the waiting passenger queue if empty ,
	 * wait until passenger arrive. Otherwise, it check the weight of the
	 * passenger. The car only run if it is last passenger or cannot carry on
	 * the weight for the next passengers
	 */
	private synchronized void load() {
		sop("start loading");

		/*
		 * check the number of passenger in the system before checking the wait
		 * list if passengers are still in the system and wait list is empty,
		 * wait at the gate
		 */
		if (!endCheck()) {
			while (waitingPassengers.isEmpty()) {
				Object gate = new Object();
				sop("is waiting");
				synchronized (gate) {
					waitingCars.addElement(gate);
					while (true) {
						try {
							gate.wait();
							break;
						} catch (InterruptedException e) {
							continue;
						}
					}
					/*
					 * can be either woke up when passenger thread end or a new
					 * passenger arrive. Therefore, we must check if there is
					 * still passenger in the system after we woke up. If there
					 * is non, we break out of the waiting loop with end flag
					 * true. If there is but if other cars took the passengers
					 * we go back to waiting
					 */
					sop("woke up!");
					if (endCheck())
						break;
				}
			}
		}
		if (!endFlag) {

			if (waitingPassengers.size() > 0) {
				synchronized (waitingPassengers) {
					if (waitingPassengers.size() > 0) {
						synchronized (waitingPassengers.elementAt(0)) {
							Ticket paper = (Ticket) waitingPassengers
									.elementAt(0);
							if (okayWeight(paper.weight)) {

								// if okay with the weight, put the passenger on
								ridingPassengers.add(waitingPassengers
										.remove(0));

								// if this is the last waiting passenger, car
								// go.
								if (waitingPassengers.size() == 0) {
									goFlag = true;
								}

								// if not okay with the weight of the next
								// passenger, car go
							} else
								goFlag = true;
						}
					}
				}
			}
		}
	}

	/*
	 * Utilize list Vector to list the passengers pid on its ride
	 */
	private void wander() {
		sop("is wandering with passengers:" + listVector(ridingPassengers));
		int time = initRandom(0, 500);
		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Unload all the passengers on its car and notify the passengers to start
	 * touring the haunted house, reset the car as it has nobody on its now and
	 * go back to pick up more passengers
	 */
	private void unload() {
		sop("start unloading passengers pid: " + listVector(ridingPassengers));

		int count = ridingPassengers.size();
		for (int i = 0; i < count; i++) {
			synchronized (ridingPassengers.elementAt(0)) {
				ridingPassengers.elementAt(0).notify();
			}
			ridingPassengers.removeElementAt(0);
		}
		resetCar();
	}

	/*
	 * The following are helper methods
	 */
	private boolean endCheck() {
		if (noOfPassenger.get() == 0) {
			sop("decide to end with passengers in the system being"
					+ noOfPassenger.get());
			endFlag = true;
		}
		return endFlag;
	}

	private void resetCar() {
		sop("resetting");
		goFlag = false;
		cWeight = limit;
	}

	/*
	 * For printing the list of passengers on the car to check if load and
	 * unload method refer to the same passengers
	 */
	private String listVector(Vector<Ticket> list) {
		String sentence = "";
		for (Ticket a : list) {
			sentence += (a.pid + " ");
		}
		return sentence;
	}

	/*
	 * To see if the passenger weight is okay to put on the car
	 */
	private boolean okayWeight(int pWeight) {
		if (cWeight - pWeight > 0) {
			cWeight = cWeight - pWeight;
			return true;
		} else {
			return false;
		}
	}

	/*
	 * For wandering method to wander for random time
	 */
	private int initRandom(int min, int max) {

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/*
	 * To print
	 */
	private void sop(String text) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] "
				+ getName() + " |" + "Car " + cid + " | " + text);
	}

}
