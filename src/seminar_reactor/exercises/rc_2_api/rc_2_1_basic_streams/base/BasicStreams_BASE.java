package seminar_reactor.exercises.rc_2_api.rc_2_1_basic_streams.base;


import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;

/*
 * a)	Stream mit wenigen Stufen:
 * 		Baue einen einfachen Stream aus: range(), map(), subscribe().
 *  	Zeige druch Shell Ausgaben, was die einzelnen Stufen tun.
 * 		
 * 		Hinweise
 * 		map( i -> {
 *   		System.out.println(“mapping i ” + 1);
 *      	return i+1;
 *   	});
 * 		
 * 		.doOnNext(i -> {...})
 *
 *
 * b) 	In welchen Threads laufen die Stream-Stufen? Laufen sie parallel oder nacheinander
 *
 * 		Hinweis: .map( value -> {System.out.println( Thread.currentThread(); return value;}); 
 * 
 * c) 	Summe: Baue einen Stream aus den Stufen range(), filter(), map(), subscribe(), 
 * 		wobei der Subscriber die Quadrate der geraden Zahlen von 0-10 aufsummiert. 
 * 
 * 
 * Lernziel: Stream API benutzen können und mit Shell-Ausgaben debuggen.
 * 
 * 
 */
public class BasicStreams_BASE {
	
	public static void main(String[] args) {
	
		System.out.println("BasicStreams_SOLUTION");
		
		
		BasicStreams_BASE instance = new BasicStreams_BASE();
		
		instance.a_fewStages();
		
		instance.b_whichThreads();
		
		instance.c_filterMapSum();
		
	}

	/*
	 *  a)	Stream mit wenigen Stufen:
	 * 		Baue einen einfachen Stream aus: range(), map(), subscribe().
	 *  	Zeige druch Shell Ausgaben, was die einzelnen Stufen tun.
	 * 		
	 * 		Hinweise
	 * 		map( i -> {
	 *   		System.out.println(“mapping i ” + 1);
	 *      	return i+1;
	 *   	});
	 * 		
	 * 		.doOnNext(i -> {...})
	 */
	private void a_fewStages() {
		

		System.out.println("\n a_fewStages \n");
		
		// TODO Factory: erzeugt Daten und emittiert sie: // = Flux.range(1, 10);
		Flux<Integer> source = null;
		
		source
			// doOnNext dient nur dem Debuggen, 
			// schreibt Items auf die Shell, die die Source emittiert
			.doOnNext(i -> {
				System.out.println("Source emited: " + i);
			
			});
		
		// TODO
		// map(i -> {return i;})
		// bildetItems auf neue Items ab 
		// und gibt einen Stream mit den neuen Items zurueck 
		
		
		// TODO
		// subscribe( i -> {// do something with i})
		// empfaengt Items am Ende des Streams
		// .subscribe(null); 
			
		
	}

	/*
	 * b) In welchen Threads laufen die Stream-Stufen? Laufen sie parallel oder
	 * nacheinander?
	 *
	 * Hinweis: .map( value -> 
	 * 	{
	 *		System.out.println( Thread.currentThread();
	 * 		return value;
	 * 	});
	 */
	private void b_whichThreads() {
		
		System.out.println("\n b_whichThreads \n");
		
		// Factory: erzeugt Daten und emittiert sie
		Flux<Integer> source = Flux.range(1, 10);
		
		source
		.map( i -> {
			int mapResult = i * 2;
			// TODO: in welchem Thread lauft diese Stufe?
			System.out.println("map() Stage maps " + i + " to " + mapResult );
			return mapResult;
		})
		
		.subscribe(i -> {
			// TODO: in welchem Thread lauft dieser Subscriber
			System.out.println("Subscriber got: " + i);
		}); 
		
	}

	
	/* 
	 * c) 	Summe: Baue einen Stream aus den Stufen range(), filter(), map(), subscribe(), 
	 * 		wobei der Subscriber die Quadrate der geraden Zahlen von 0-10 aufsummiert. 
	 */
	private void c_filterMapSum() {
		
		System.out.println("\nc_filterMapSum\n");
		
		// Hierin summiert der Subscriber
		final AtomicInteger sum = new AtomicInteger();
		
		// Factory: erzeugt Daten und emittiert sie
		Flux<Integer> source = Flux.range(1, 10);
		
		source
		
		// TODO Gerade Zahlen herausfiltern mit filter()
		.filter(null)
		
		// TODO Quadrate berechnen mit map()
		.<Integer>map(null)
		
		// Subscriber summiert, z.B. in einer final Variable außerhalb des Streams, siehe oben.
		.subscribe((Integer i) -> {
			// TODO aufsummieren: sum.addAndGet(i);
		}); 
		
	}
}
