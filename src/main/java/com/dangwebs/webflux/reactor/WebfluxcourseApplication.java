package com.dangwebs.webflux.reactor;

import com.dangwebs.webflux.reactor.models.Comentarios;
import com.dangwebs.webflux.reactor.models.Usuario;
import com.dangwebs.webflux.reactor.models.UsuarioComentarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

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
        ejemploZipWithRangos();
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
        Flux<Integer> rangos = Flux.range(0,4);

        Flux.just(1,2,3,4)
                .map(i -> (i*2))
                .zipWith(rangos, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
                .subscribe(texto -> log.info(texto));
    }
}
