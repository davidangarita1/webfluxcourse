package com.dangwebs.webflux.reactor;

import com.dangwebs.webflux.reactor.models.Comentarios;
import com.dangwebs.webflux.reactor.models.Usuario;
import com.dangwebs.webflux.reactor.models.UsuarioComentarios;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class WebfluxcourseApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WebfluxcourseApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebfluxcourseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // ejemploItarable();
        // ejemploFlatMap();
        // ejemploToString();
        // ejemploCollectList();
        // ejemploUsuarioComentarioFlatMap();
        // ejemploUsuarioComentarioZipWith();
        // ejemploUsuarioComentarioZipWithForma2();
        // ejemploZipWithRangos();
        // ejemploInterval();
        // ejemploDelayElements();
        // ejemploIntervalInfinito();
        // ejemploIntervalDesdeCreate();
        // ejemploContraPresion(); // Manipular el Backpresure
        ejemploContraPresion2();
    }

    public void ejemploItarable() throws Exception {

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Andres Guzman");
        usuariosList.add("Pedro Fulano");
        usuariosList.add("Maria Fulana");
        usuariosList.add("Diego Sultano");
        usuariosList.add("Juan Mengano");
        usuariosList.add("Bruce Lee");
        usuariosList.add("Bruce Willis");

        Flux<String> nombres = Flux.fromIterable(usuariosList); // Flux.just("Andres Guzman", "Pedro Fulano", "Maria Fulana", "Diego Sultano", "Juan Mengano", "Bruce Lee", "Bruce Willis");

        Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
                .filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Los nombres no pueden ser vacíos");
                    }
                    System.out.println(usuario.getNombre() + " " + usuario.getApellido());
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        usuarios.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado la ejecución del observable con éxito!");
                    }
                }
        );
    }

    public void ejemploFlatMap() throws Exception {

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Andres Guzman");
        usuariosList.add("Pedro Fulano");
        usuariosList.add("Maria Fulana");
        usuariosList.add("Diego Sultano");
        usuariosList.add("Juan Mengano");
        usuariosList.add("Bruce Lee");
        usuariosList.add("Bruce Willis");

        Flux.fromIterable(usuariosList)
                .map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
                .flatMap(usuario -> {
                    if (usuario.getNombre().equalsIgnoreCase("bruce")) {
                        return Mono.just(usuario);
                    }
                    return Mono.empty();
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                })
                .subscribe(u -> log.info(u.toString()));
    }

    public void ejemploToString() throws Exception {

        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Andres", "Guzman"));
        usuariosList.add(new Usuario("Pedro", "Fulano"));
        usuariosList.add(new Usuario("Maria", "Fulana"));
        usuariosList.add(new Usuario("Diego", "Sultano"));
        usuariosList.add(new Usuario("Juan", "Mengano"));
        usuariosList.add(new Usuario("Bruce", "Lee"));
        usuariosList.add(new Usuario("Bruce", "Willis"));

        Flux.fromIterable(usuariosList)
                .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                .flatMap(nombre -> {
                    if (nombre.contains("bruce".toUpperCase())) {
                        return Mono.just(nombre);
                    }
                    return Mono.empty();
                })
                .map(nombre -> nombre.toUpperCase())
                .subscribe(u -> log.info(u.toString()));
    }

    public void ejemploCollectList() throws Exception {

        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Andres", "Guzman"));
        usuariosList.add(new Usuario("Pedro", "Fulano"));
        usuariosList.add(new Usuario("Maria", "Fulana"));
        usuariosList.add(new Usuario("Diego", "Sultano"));
        usuariosList.add(new Usuario("Juan", "Mengano"));
        usuariosList.add(new Usuario("Bruce", "Lee"));
        usuariosList.add(new Usuario("Bruce", "Willis"));

        Flux.fromIterable(usuariosList)
                .collectList()
                .subscribe(lista -> {
                    lista.forEach(item -> log.info(item.toString()));
                });
    }

    public void ejemploUsuarioComentarioFlatMap() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

        Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola pepe, qué tal!");
            comentarios.addComentarios("Mañana voy a la playa!");
            comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
                .flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)));

        usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentarioZipWith() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

        Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola pepe, qué tal!");
            comentarios.addComentarios("Mañana voy a la playa!");
            comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
                .zipWith(comentariosUsuarioMono, (usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));

        usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentarioZipWithForma2() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

        Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola pepe, qué tal!");
            comentarios.addComentarios("Mañana voy a la playa!");
            comentarios.addComentarios("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
                .zipWith(comentariosUsuarioMono)
                .map(tuple -> {
                    Usuario u = tuple.getT1();
                    Comentarios c = tuple.getT2();
                    return new UsuarioComentarios(u, c);
                });

        usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploZipWithRangos() {
        Flux<Integer> rangos = Flux.range(0, 4);

        Flux.just(1, 2, 3, 4)
                .map(i -> (i * 2))
                .zipWith(rangos, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
                .subscribe(texto -> log.info(texto));
    }

    public void ejemploInterval() {
        Flux<Integer> rango = Flux.range(1, 12);
        Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

        rango.zipWith(retraso, (ra, re) -> ra)
                .doOnNext(i -> log.info(i.toString()))
                .blockLast();
        //.subscribe();
    }

    public void ejemploDelayElements() {
        Flux<Integer> rango = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.info(i.toString()));

        rango.subscribe();
    }

    public void ejemploIntervalInfinito() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(latch::countDown)
                .flatMap(i -> {
                    if (i >= 5) {
                        return Flux.error(new InterruptedException("Solo hasta 5"));
                    }
                    return Flux.just(i);
                })
                .map(i -> "Hola " + i)
                .retry(2) // Si ocurre un error vuelve a iniciar el flujo
                .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

        latch.await();
    }

    public void ejemploIntervalDesdeCreate() {
        Flux.create(emitter -> {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                        private Integer contador = 0;

                        @Override
                        public void run() {
                            emitter.next(++contador);
                            if (contador == 10) {
                                timer.cancel();
                                emitter.complete();
                            }

                            if (contador == 5) {
                                timer.cancel();
                                emitter.error(new InterruptedException("Error, se ha detenido el flux en 5!"));
                            }
                        }
                    }, 1000, 1000);
                })
                .subscribe(next -> log.info(next.toString()),
                        error -> log.error(error.getMessage()),
                        () -> log.info("Hemos terminado"));
    }

    public void ejemploContraPresion() {
        Flux.range(1, 10)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    private Integer limite = 5;
                    private Integer consumido = 0;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(limite);

                    }

                    @Override
                    public void onNext(Integer i) {
                        log.info(i.toString());
                        consumido++;
                        if (consumido == limite) {
                            consumido = 0;
                            s.request(limite);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void ejemploContraPresion2() {
        Flux.range(1, 10)
                .log()
                .limitRate(2)
                .subscribe();
    }
}
