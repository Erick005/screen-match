package br.com.alura.screanmatch.principal;

import br.com.alura.screanmatch.model.DadosSerie;
import br.com.alura.screanmatch.model.DadosTemporada;
import br.com.alura.screanmatch.model.Episodio;
import br.com.alura.screanmatch.service.ConsumoApi;
import br.com.alura.screanmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Secundaria {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6f83d88e";
    private ConverteDados conversor = new ConverteDados();

    public void exibMenu() {
        System.out.println("Coloque o nome da sua serie favorita");

        var lerDados = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + lerDados.replace(" ", "+") + API_KEY);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println("\n" + dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + lerDados.replace(" ", "+") +"&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

//        System.out.println("\nMelhores episodios!");
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toUnmodifiableList());
//
//        dadosEpisodios.stream()
//                .filter(a -> !a.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        System.out.println("\nDados de cada episodio");
        List<Episodio> episodio = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodio.forEach(System.out::println);

//        System.out.println("Escreva o seu episodio");
//        var trechoDoTitulo = leitura.nextLine();
//        Optional<Episodio> optionalEpisodio = episodio.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoDoTitulo.toLowerCase()))
//                .findFirst();
//
//        if (optionalEpisodio.isPresent()) {
//            System.out.println("Temporada: " + optionalEpisodio.get().getTemporada());
//        } else {
//            System.out.println("Temporada: '" + trechoDoTitulo + "' não foi encontrada.");
//        }

//        System.out.println("A partir de qual ano você quer?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate data = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter fomartador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodio.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(data))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                        "Titulo: " + e.getTitulo() +
//                        "Data de lancamento: " + e.getDataLancamento().format(fomartador)
//                ));

        Map<Integer, Double> avaliacaoPorTemporada = episodio.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodio.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
