package seminar_reactor.exercises.rc_3_data.rc_3_6_generator_backpressure.solution;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Nebenläufigkeit und Memory-Consumption
 * 
 * 1. Zeige, dass der Speicherverbrauch unkontrolliert wachsen kann, 
 * wenn Du asynchrone Stream-Stufen hast.
 * 
 * Repariere dies durch Back-Pressure.
 * 
 * In diesem Beispiel werden folgende Szenarien gezeigt
 * In einem Stream werden viele große Objekte verarbeitet.
 * Eine Streamstufe ist sehr langsam.
 * Der Generator und die langsame Stufe laufen asynchron zueinander.
 *
 * Szenario 1: Die Source ist mit create erzeugt. Sie ist nicht Backpressure-aware.
 * 	Ergebnis: Memory-Overflow
 * 
 * Szenario 2: Die Source ist mit generate erzeugt, sie ist Backpressure-aware
 * 	Ergebnis: Der Memory-Overflow wird verhindert. Grund: Der publishOn() Operator erzeugt 
 *  den Backpressure durch request(256)
 *  
 * Szenario 3: onBackpressureDrop()
 *   Ergebnis: Je nachdem, wo dies platziert ist, kann es den Backpressure erzwingen oder 
 *   wirkungslos bleiben. Laeuft es im Thread des Generators, so erzwingt es BP.
 * 		

 */
public class ConcurrencyMemoryBackpressure {

	final static int ARRAY_SIZE = 100_000;
	final static int STREAM_SIZE = 100_000;
	public static void main(String[] args) {
		ConcurrencyMemoryBackpressure instance = new ConcurrencyMemoryBackpressure();
		
		instance.concurrentStreamBigObjects();
		
		// die VM lebendig halten, damit der Stream im Hintergrund laufen kann
		sleep(100000);
		
	}

	void concurrentStreamBigObjects() {
	
		// Erzeugen vieler sehr großer Objekte
		
		// Mit range funktioniert dieses Beispiel nicht, weil es automatisch Backpressure-Aware ist.
		// Flux<Integer> sourceBigObjects = Flux.range(0, STREAM_SIZE);
		
		// Szenario 1: Diese Source ist nicht Backpressure-Aware! 
		Flux<int[]> sourceBigObjects = 
				Flux.create(emitter -> {
					int counter = 0;
					while(counter < STREAM_SIZE) {
						counter++;
						int[] bigArray = new int[ARRAY_SIZE];
						bigArray[0] = counter;
						emitter.next(bigArray);
						System.out.println("Generator emitted: " + counter);
						sleep(1);
					}
					emitter.complete();
				});
		
		
		// Scenario 2:  Diese Source ist Backprassure-Aware
		/*
		Flux<int[]> sourceBigObjects = Flux.generate(()-> new AtomicInteger(), (state, sink)  -> {
			
			int counter = state.incrementAndGet();
			int[] bigArray = new int[ARRAY_SIZE];
			bigArray[0] = counter;

			sink.next(bigArray);
			
			System.out.println("Generator emitted: " + counter);
			sleep(1);
			return state;
		});
		*/
		
		
		sourceBigObjects
		
		// Szenario 3 onBackpressureDrop()		
		// on backpressure drop behbt das Problem, aber nur, wenn es nicht vom Generator entkoppelt ist!
		//.onBackpressureDrop()
		
		// das hilft hingegen nicht
		//.onBackpressureBuffer()
		
		// Mache die Stream-Stufen asynchron zueinander
		.log()
		.publishOn(Schedulers.parallel()) // führt zu request(256). Die Queue hier drin buffert dann die eingehenden Items
		
		// on backpressure drop nützt nichts an dieser Stelle im Stream
		//.onBackpressureDrop()
		
		// Mache eine Stream-Stufe langsam
		.map(bigObject -> {
			sleep(1000);
			return bigObject;
		})
		
		.log()
		// Melde einen Subscriber als Lambda an: führt zu request(unbounded)
		.subscribe(bigObject -> {
			System.out.println("Subscriber received big object: " + bigObject[0]);
		});
	}
	
	/////////////////////// HELPER ////////////////////////////
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
