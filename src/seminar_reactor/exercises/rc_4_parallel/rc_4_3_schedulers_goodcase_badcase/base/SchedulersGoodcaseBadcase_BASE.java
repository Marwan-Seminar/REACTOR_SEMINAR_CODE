package seminar_reactor.exercises.rc_4_parallel.rc_4_3_schedulers_goodcase_badcase.base;

import java.util.concurrent.CountDownLatch;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * 
 * Zeige das Verhalten unterschiedlicher Scheduler fuer unterschiedliche Use-Cases.
 * 
 *  
 * 1. 	CPU-bound Use-Case: 
 * 		Verbessere die Laufzeit der einzelnen Jobs in den Clients eines Streams
 * 		durch die Wahl eines geeigneten Schedulers
 * 
 * 2. 	IO-bound Use-Case
 * 		Verbessere die Gesamtlaufzeit eines Streams durch die Auswahl eines geeigneten Schedulers
 *  
 */
public class SchedulersGoodcaseBadcase_BASE {

	public static void main(String[] args) throws InterruptedException {
		SchedulersGoodcaseBadcase_BASE instance = new SchedulersGoodcaseBadcase_BASE();
		
		// Fall 1 CPU-bound
		instance.cpuBoundCase();
		
		// Fall 2  IO-bound 
		//instance.ioBoundCase();
		
		sleep(10000);
	}

	/*
	 * 	1a CPU-bound Use-Case: Gutfall parallel()
	 * 		8 clients laufen parallel CPU-intensiv und lasten alle CPUs aus.
	 * 		Sobald die ersten 8 fertig sind, werden die anderen 8 gestartet. 
	 * 		Die ersten 8 sind dadruch frueher fertig als im Schlechtfall.
	 * 		
 	 *
	 *  1b CPU-bound Use-Case Schlechtfall elastic()
	 * 		Alle 16 Clients laufen gleichzeitig los und konkurrieren um die 8 CPUs 
	 *  	Die CPU-Intensiven Tasks behindern sich gegenseitig, weil Scheduler.elastic() 
	 *  	alle gleichzeitig startet.
	 * 		Die Summe der Laufzeit ist wie bei Schedulers.parallel() aber jede einzelne Task 
	 * 		läuft länger.
	 * 
	 * 		Ein weiteres Problem dieses Falles ist, dass der Speicherverbauch durch unnötig
	 * 	 	viele Threads ansteigt.
	 * */
	private void cpuBoundCase() throws InterruptedException {
		
		// So viele Clients werden an dem Stream subskribiert.
		final int NR_OF_JOBS = 16;
		
		// Hier wird geprueft, ob NR_OF_JOBS ausreichend ist, erhoehe diese ggf. 
		int hardwareParallelism = Runtime.getRuntime().availableProcessors();
		System.out.println("Nubmer of CPU-Cores: " + hardwareParallelism);
		if(NR_OF_JOBS < hardwareParallelism * 2) {
			System.err.println("Increase Parameter NR_OF_JOBS to " + hardwareParallelism * 2 );
		}
		
		// Fuer die Zeitmessung
		final CountDownLatch latch = new CountDownLatch(NR_OF_JOBS);
		long startTime = System.currentTimeMillis();
		
		// Der Stream
		Flux<Integer> source = 
				Flux.range(1, 1)
				// TODO: Finde einen geeigneten Scheduler
				.subscribeOn(Schedulers.boundedElastic()); // BAD CASE 1b
		
		for(int clientIdx = 0; clientIdx < NR_OF_JOBS; clientIdx ++) {
			
			final int idx = clientIdx;
			
			source.subscribe(i-> {
				
				cpuIntensiveCall();
				
				System.out.println("cpuIntensiveCall: " + idx  + " end-time " + (System.currentTimeMillis() - startTime));
				
				latch.countDown();
			});
		}
		
		// await() kehrt erst dann zurueck, wenn alle Clients fertig sind.
		latch.await();
		System.out.println("Overall Time: " + (System.currentTimeMillis() - startTime));
		
	}
	
	/*
	
	
	/*
	 * Fall 2 IO-bound Use-Case
	 */
	private void ioBoundCase() {
		
		// So viele Clients werden an dem Stream subskribiert.
		final int NR_OF_JOBS = 16;
		
		// Hier wird geprueft, ob NR_OF_JOBS ausreichend ist, erhoehe diese ggf. 
		int hardwareParallelism = Runtime.getRuntime().availableProcessors();
		System.out.println("Nubmer of CPU-Cores: " + hardwareParallelism);
		if(NR_OF_JOBS < hardwareParallelism * 2) {
			System.err.println("Increase Parameter NR_OF_JOBS to " + hardwareParallelism * 2 );
		}
		
		Flux<Integer> source = 
				Flux.range(1, 1)
				// TODO finde einen geeigneten Scheduler
				.subscribeOn(Schedulers.parallel()); 
				
		long start = System.currentTimeMillis();
		
		for(int clientIdx = 0; clientIdx < NR_OF_JOBS; clientIdx ++) {
			final int idx = clientIdx;
			source.subscribe(i-> {	
				// Diese Subscriber schlafen eine Sekunde lang
				System.out.println("Task " + idx + " started at " + (System.currentTimeMillis() -start));
				sleep(1000);
				System.out.println("Task " + idx + " completed after: " +(System.currentTimeMillis() -start));
			});
		}
	}
	
	
	
	////////////////////////////HELPER /////////////////////////////////////

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
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
	
}
