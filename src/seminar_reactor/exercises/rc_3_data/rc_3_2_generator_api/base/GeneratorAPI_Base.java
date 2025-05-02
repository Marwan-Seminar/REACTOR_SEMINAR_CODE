package seminar_reactor.exercises.rc_3_data.rc_3_2_generator_api.base;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SynchronousSink;

/*
 * 
 * Diese Uebung demonstriert die Generator API von Reactor
 * 
 * a) Baue einen Genarator mit generate() der Zufallszahlen emittiert
 * 
 * b) Baue einen Generator mit generate() der ein State Objekt benutzt und die Folge 1..10 emittiert
 * 
 * c) Baue einen Generator mit create() der die Folge 1..10 emittiert
 * 
 * d) Baue einen Generator mit create() asynchrone Events (Button-Clicks )in den Stream einspeist 
 * 
 */
public class GeneratorAPI_Base {
	
	public static void main(String[] args) {
		
		GeneratorAPI_Base instance = new GeneratorAPI_Base();
		
		instance.a_generateStateless();
		
		//instance.b_generateStateObject();
		
		//instance.c_createSynchronous();
		
		//instance.d_createAsynchronous();
	}

	private void a_generateStateless() {
		
		System.out.println("GeneratorAPI.a_genarateStateless()");
		
		/*
		 *  Hinweise:
		 *
		 *  Zufallszahl:  
		 *  	Random rand = new Random();
		 *  	rand.nextInt();
		 *  
		 *  Signatur Flux.generate():
		 *  
		 *  	generate(Consumer<SynchronousSink<T>> generator)
		 *  
		 *  	interface SynchronousSink{
		 *  		void next(T t);
		 *  		void complete();
		 * 		}
		 *  
		 */
		
		Random rand = new Random();
		
		// Abbruch-Bedingung 
		AtomicInteger exitCriteria = new AtomicInteger();
		
		Flux<Integer> source = Flux.generate( emitter -> {
			
			// TODO call emitter.next()
			
			// TODO call emitter.complete()
		});
		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
		
	}

	/*
	 * Flux.generate() hat eine Variante mit State Objekt.
	 * Dieses wird von Aufruf zu Aufruf des Generator-Lambdas weitergegeben
	 * um einen Zustand zu modellieren.
	 * 
	 * Hinweis: generate Signatur mit State Objekt
	 * 
	 * 		generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator)
	 * 		
	 * 		Als State Objekt eignet sich z.B.
	 * 		new AtomicInteger()
	 * 
	 */
	private void b_generateStateObject() {
		

		System.out.println("GeneratorAPI.b_genarateStateObject()");
		
		Flux<Integer> source = Flux.generate(
				()-> null /* TODO hier ein State Objekt mitgeben */,
				
				/* hier eine BiFunction mit den Argumenten (state, emitter) mitgeben */
				(state, emitter) -> {
					
					//Body der BiFunction 
					
					// TODO 1: emitter.next() aufrufen
					
					// TODO 2: emitter.complete() aufrufen 
					
					// TODO 3: state Objekt returnen
					return null;
				});
		
		// Subscriber
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
	}

	/*
	 * Flux.create() kann Daten synchron in den Stream emittieren,
	 * indem diese Daten innerhalb des Lambda-Aufrufes emittiert werden.
	 * 
	 * Hinweis:
	 * 	Flux.create() API
	 * 	
	 * 	create(Consumer<? super FluxSink<T>> emitter)
	 * 	
	 * 	interface FluxSink{
	 * 		next(T t);
	 * 		
	 * 		// ...
	 * 	}
	 * 
	 */
	private void c_createSynchronous() {
		
		System.out.println("GeneratorAPI.c_createSynchronous()");
		
		Flux<Integer> source = Flux.create( emitter -> {
			
			// TODO emitter.next() aufrufen
		
		});
		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
	}

	/*
	 * Flux.create() kann Daten asynchron in einen Stream senden,
	 * indem diese Daten ausserhalb des Lambda Aufrufes asynchron 
	 * erzeugt werden.
	 * 
	 * Innerhlab des Lamdas wird die Datenquelle an den Stream gekoppelt.
	 * Im hier vorliegenden Beispiel ist die Datenquelle ein JButton
	 * der ueber einen ActionListener mit dem Stream verbunden wird.
	 * 
	 * Hinweis: Einem Button einen ActionListener hinzufuegen:
	 * 
	 * 	button.addActionListener(new ActionListener() {
	 * 		@Override
	 * 		public void actionPerformed(ActionEvent e) {	
	 * 			// TODO Hier die Logik einbauen, um Daten in den 
	 * 			// Stream zu schreiben	
	 * 		}
	 * 	});
	 */
	private void d_createAsynchronous() {
		
		
		System.out.println("GeneratorAPI.d_createAsynchronous()");
		
		JButton button = ButtonFrame.startFrame();
		
		
		Flux<String> source = Flux.create( emitter -> {
		
			// TODO Hier den Button mittels einen ActionListeners mit dem 
			// emitter verbinden
			
		
		});

		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
	}

}

////////////////// HELPER CLASS FOR Swing BUTTON ///////////////////

class ButtonFrame
{
	static int state = 1;
	
	
    public static JButton startFrame()
    {
        JFrame frame = new JFrame();
        frame.setTitle("Button Stream Connection");
        JPanel panel = new JPanel();
 
        // JButton: An diesen wir ein Action-Handler angefuegt, der Daten in den Stream speist
        JButton button = new JButton("Send Data Into Stream");
 
        panel.add(button);        
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
       
        return button;
    }
}
