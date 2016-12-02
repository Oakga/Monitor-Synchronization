//Main Program, This program link car threads and passengers threads together
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

public class House {
	private static Vector waitingPassengers;
	private static Vector waitingCars;

	public static void main(String[] args) {

		// This program take in a argument and run according to that number of
		// times
		int limit = Integer.parseInt(args[0]);
		int counter = 0;

		while (counter < limit) {
			sop("The program will now run for its " + counter + " times");

			// created two vectors that will be visible to waiting Passengers
			// and waiting Cars
			waitingPassengers = new Vector();
			waitingCars = new Vector();

			// Example: 14 passengers run for 4 times into haunted house with 3
			// cars
			ThreadPool pool = new ThreadPool(4, 14, 3, waitingPassengers,
					waitingCars);
			pool.join();
			counter++;

		}
		sop("Program end");
	}

	private static void sop(String text) {
		System.out.println(text);
	}
}