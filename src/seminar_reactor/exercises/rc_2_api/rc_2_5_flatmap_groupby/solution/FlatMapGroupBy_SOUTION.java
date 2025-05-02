package seminar_reactor.exercises.rc_2_api.rc_2_5_flatmap_groupby.solution;

import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.scheduler.Schedulers;

/*
 * a) 	Zerlege die Zahlen 1..100 in vier Gruppen. 
 * 		Jede Gruppe enthaelt die Zahlen, die denselben Wert modulo 4 haben.
 * 		Zeige durch Shell-Output welche Zahl in welcher Gruppe ist.
 * 		Hinweis: groupBy()
 * 
 * b) 	Fuege die Gruppen aus a) so zusammen, dass alle Zahlen wieder
 * 		in einen gemeinsamen Stream gemerged werden.
 * 		Hinweis: flatMap()
 * 
 * c) 	Parallelisiere die Loesung b) so, dass die Elemente verschiedener
 * 		Gruppen parallel verarbeitet werden.
 * 		Zeige die Parallelitaet durch Shell Ausgaben
 * 		Hinweis: publishOn(Schedulers.parallel()) 
 * 
 */
public class FlatMapGroupBy_SOUTION {
	
	public static void main(String[] args) {
		
		FlatMapGroupBy_SOUTION instance = new FlatMapGroupBy_SOUTION();
		
		//instance.a_groupingGroupBy();
		
		//instance.b_mergingFlatMap();
		
		instance.c_parallelFlatMapGroupBy();
		
		sleep(3000);
	}

	private void a_groupingGroupBy() {
		
		// Emittiert Zahlen
		Flux<Integer> source = Flux.range(0, 100);
		
		// Gruppiert in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux = source.groupBy(x -> x%4);
		
		// Subsribiert an GroupedFlux
		groupedFlux.subscribe(group ->{
			
			// Inner Subscribe: Pro Gruppe
			group.subscribe( val -> {
				System.out.println(
				"Group: " + group.key() + " Element " + val);
			});
		});
		
	
		
		
	}
	
	

	private void b_mergingFlatMap() {
		
		// Emittiert Zahlen
		Flux<Integer> source =
				Flux.range(0, 100);
				
		// Gruppiert in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux =
				source.groupBy(x -> x%4);
				
		
		// Merging
		groupedFlux.flatMap( group -> {
			// Gruppe bearbeiten
			return 
				group.map( value -> {
					// Werte der Gruppe bearbeiten
					System.out.println(
							"Group: " + group.key() + " Element " + value);
					return value;
			});
		 
		})
		
		.subscribe( outValue -> {
			System.out.println(
					"Subscriber: " + outValue);
		});
		
	}

	private void c_parallelFlatMapGroupBy() {
		
		// Emittiert Zahlen
		Flux<Integer> source =
				Flux.range(0, 100);
				
		// Gruppiert in vier Gruppen
		Flux<GroupedFlux<Object, Integer>> groupedFlux =
				source.groupBy(x -> x%4);
				
		
		// Merging
		groupedFlux.flatMap( group -> {
			// Gruppe bearbeiten
			return 	
				group
				
				// PARALLEL EINSTIEGSPUNKT
				//.subscribeOn(Schedulers.parallel())
				.publishOn(Schedulers.parallel())
				
				.map( value -> {
				
					// Werte der Gruppe parallel bearbeiten
					System.out.println(
							"Value: " + value + " grp: " + group.key() + " " + Thread.currentThread().getName());
				return value;
			});			
		})
		
		.subscribe( outValue -> {
			System.out.println(
					"Subscriber: " + outValue);
		});
		
	}

	
/////////////////// HELPER ///////////////////////

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
