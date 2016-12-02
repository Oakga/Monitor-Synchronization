import java.awt.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

//This class is responsible for creation of threads and executions
public class ThreadPool {

	private ArrayList<Passenger> passengerThreads = new ArrayList<Passenger>();
	private ArrayList<Car> carThreads = new ArrayList<Car>();
	private AtomicInteger noOfPassenger;
	
	//to create threads and to run them
	public ThreadPool(int tickets,int noOfPassengers, int noOfCars,Vector waitingPassengers,Vector waitingCars) {
		
		noOfPassenger=new AtomicInteger(noOfPassengers);
		
		//Threads Creations
		for (int i = 0; i < noOfPassengers; i++) {
			passengerThreads.add(new Passenger(i,tickets,waitingPassengers,waitingCars,noOfPassenger));
		}

		for (int i = 0; i < noOfCars; i++) {
			carThreads.add(new Car(i,waitingPassengers,waitingCars,noOfPassenger));
		}

		//Threads executions
		
		sop("Starting all Passenger Threads");
		
		for (Passenger thread : passengerThreads) {
			thread.start();
		}
		
		sop("Starting all car Threads");

		for (Car thread : carThreads) {
			thread.start();
		}
	}
	

	//to make sure all threads finish execution
	public void join() {
		try {
			for (Passenger thread : passengerThreads) {
				thread.join();
			}

			sop("All Passenger threads has finish execution");

			for (Car thread : carThreads) {
				thread.join();
			}
			sop("All car threads has finish execution");

			sop("All threads has finish execution");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sop(String text) {
		System.out.println(text);
	}
}
