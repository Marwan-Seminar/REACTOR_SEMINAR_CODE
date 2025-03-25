package seminar_reactor.exercises.rc_3_data.rc_3_5_subscriber_backpressure.solution;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/*
 * Programmiere einen Subscriber, der einen lang laufenden Hintergrund-Job ausführt.
 * 
 * Dabei sollen zwei Ziele erfüllt werden
 * 
 * 1. Non-Obstruction: Der Subscriber soll den Publisher nicht behindern, also onNext() muss sofort zurueckkehren
 * 2. Back-Pressure: Der Subscriber soll immer nur eine begrenzte Anzahl an neuen Items beauftragen
 * 
 * Bemerkeungen zur Loesung
 * 
 * Gehe von der Klasse SlowSubscriberNaive aus. Diese fordert unendlich viele Items von der 
 * Source an. 
 * 
 * Man sieht im Shell-Output, dass die Source immer weitere Items produziert, die der Subscriber nicht verarbeiten kann.
 * 
 * Baue dort Back-Pressure ein. 
 * 
 * Eine einfache Loesung wäre, in jedem onNext() Call Subscription.request(1) aufzurufen. 
 * Das ist aber inefizient, weil dann immer nur einzelne Items angefordert werden.
 * 
 * Finde eine Loesung, bei der Du einen Batch von Items anforderst, nachdem eine bestimmte
 * Anzahl von onNext()-Calls abgearbeitet worden ist.
 * 
 */
public class SubscriberBackpressure_Solution {

	public static void main(String[] args) {
	
		SubscriberBackpressure_Solution instance = new SubscriberBackpressure_Solution();
		

		// Der Subscriber mit Back-Pressure
		//instance.testBPSubscriber();
		
		// Der Subscriber ohne Back-Pressure
		instance.testNaiveSubscriber();
		
		// Das Programm am Leben erhalten
		//Helper.sleep(30000);
	}

	private void testNaiveSubscriber() {
		
		Flux<Integer> generator = Flux.generate(() -> new AtomicInteger(), (state, emitter) -> {
			
			int value = state.incrementAndGet();
			
			// Verlangsamt den Generator, um den Output lesbarer zu machen
			Helper.sleep(10);
			System.out.println("generated: " + value);
			
			emitter.next(value);
			
			return state;
		} );
		
		
		
		// Subscribe
		generator
		//.log()
		//.publishOn(Schedulers.parallel())
		.subscribe(new SlowSubscriberNaive());
		
	}

	private void testBPSubscriber() {
		
		Flux<Integer> generator = Flux.generate(() -> new AtomicInteger(), (state, emitter) -> {
			
			int value = state.incrementAndGet();
			
			System.out.println("generated: " + value);
			
			emitter.next(value);
			
			return state;
		} );
		
		
		// Subscribe
		generator
		//.log()
		//.publishOn(Schedulers.parallel())
		.subscribe(new SlowSubscriberBackPressure());
		
		
		
		// Entkoppeln des Publishers vom Subscriber mit generator.publishOn(Schedulers.parallel())
		// ist nicht nötig, da unser Subscriber schnell aus onNext() zurückkehrt.
		// Führt aber zu prefetch von 256 Items
		
		
	}
}


/////////////////// RICHTIG: SlowSubscriberBP erzeugt Backpressure ///////

class SlowSubscriberBackPressure extends BaseSubscriber<Integer>{
	
	// Die Verbindung zum Publisher
	Subscription subscription;
	
	// Zähler für die bereits erledigten Aufgaben
	AtomicInteger tasksFulfilled = new AtomicInteger(0);
	
	// So viele Daten werden von der Source pro Batch angefordert
	static final int BATCH_SIZE = 16;
	
	// Thread Pool für lang laufende Aufgaben
	ExecutorService threadPool = Executors.newFixedThreadPool(8);

	
	@Override
	protected void hookOnSubscribe(Subscription subscription) {
		this.subscription = subscription;
		
		request(BATCH_SIZE);
	}
	
	/*
	 * Lang laufende Aufgaben muessen in einen Hintergrund-Thread ausgelagert werden
	 */
	@Override
	protected void hookOnNext(Integer value) {
	
		System.out.println("onNext: " + value);
		
		// Aufgabe laeuft eine Sekunde im Hintergrund
		threadPool.execute(() -> {
			System.out.println("Running Background-Task: " + value + " " + Thread.currentThread());
			
			// Eine CPU intensive Aufgabe ausfuehren
			Helper.cpuIntensiveCall(1000);
			
			// Die erledigten Aufgaben zaehlen
			tasksFulfilled.incrementAndGet();
			
			// synchronized, damit nicht mehrere Threads gleichzeitig neue Items requesten
			synchronized(tasksFulfilled) {
				
				if(tasksFulfilled.get() == BATCH_SIZE) {
					System.out.println("Ordering new Batch");
					// Backpressure: Nur eine geringe Menge von Daten anfordern
					subscription.request(BATCH_SIZE);
					
					// Den internen Zaehler zuruesetzten
					tasksFulfilled.set(0);
					
				}
			}
		});
		
		
		
		
		
	}
	
}


///////////// FALSCH: SlowSubscriberNaive triggert unendlichen Demand /////////////
	
	
class SlowSubscriberNaive extends BaseSubscriber<Integer>{
	
	// Die Verbindung zum Publisher
	Subscription subscription;
	
	
	// Thread Pool für lang laufende Aufgaben
	ExecutorService threadPool = Executors.newFixedThreadPool(8);

		
	
	@Override
	protected void hookOnSubscribe(Subscription subscription) {
		this.subscription = subscription;
		
		super.requestUnbounded();
	}
	
	/*
	 * Lang laufende Aufgaben muessen in einen Hintergrund-Thread ausgelagert werden
	 */
	@Override
	protected void hookOnNext(Integer value) {
	
		threadPool.submit(()->{
			// lauft eine Sekunde im "Hintergrund"
			Helper.cpuIntensiveCall(1000);
			
			System.out.println("Running Background-Task: " + value + " " + Thread.currentThread());
			
			
		});
	}
	
}
	



//////////////////// HELPER //////////////

class Helper{
	
	static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Runs approximately one second on the CPU
	static void cpuIntensiveCall() {
		long start = System.currentTimeMillis();
		
		long dummy = 2;
		for(long counter = 0; counter <= Integer.MAX_VALUE; counter ++) {
				dummy = (dummy + dummy);
				if (dummy > Integer.MAX_VALUE) {
					dummy = dummy % Integer.MAX_VALUE;
					
				}
				
		}
		
		long runtime = System.currentTimeMillis() - start;
		
		System.out.println("cpuIntensiveCall: runtime " + runtime);
	}
	
	
	
	/*
	 * This method runs runtimeInMillis Milliseconds on a CPU without blocking, then
	 * it returns.
	 * 
	 * CAVEAT: but N parallel calls all will return after runtimeInMillis milliseconds!!!
	 * 
	 */
	static void cpuIntensiveCall(long runtimeInMillis) {
		long start = System.currentTimeMillis();
		long dummy = 3;
		while (true) {
			dummy = (dummy + dummy);
			if (dummy > Integer.MAX_VALUE) {
				dummy = dummy % Integer.MAX_VALUE;
				if (System.currentTimeMillis() - start > runtimeInMillis) {
					return;
				}
			}
		}
	}
}