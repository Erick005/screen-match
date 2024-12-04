package br.com.alura.screanmatch.principal;

import br.com.alura.screanmatch.model.*;
import br.com.alura.screanmatch.repository.SerieRepository;
import br.com.alura.screanmatch.service.ConsumoApi;
import br.com.alura.screanmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6f83d88e";
    private List<DadosSerie> listDadosSerie = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscar;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar novas séries
                    2 - Buscar episódios da serie
                    3 - Listar todas as series
                    4 - Buscar series por ator
                    5 - Buscar top 5 series
                    6 - Buscar Series por categoriaEnum
                    7 - Buscar Series por quantidade de temporada
                    8 - Buscar por trecho do nome de episodio
                    9 - Buscar Top Episodios Por Serie
                    10 - Buscar Serie Por Titulo
                    11 - Buscar Episodios Depois De Uma Data
                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    retornaListaDeSeries();
                    break;
                case 4:
                    buscarListaPorAtores();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    buscarSeriesPorQuantidadeTemporada();
                    break;
                case 8:
                    buscarEpisodioPorTrecho();
                    break;
                case 9:
                    topEpisodiosPorSerie();
                    break;
                case 10:
                    buscarSeriePorTitulo();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        System.out.println("Escolhe uma serie pelo titulo: ");
        var nomeTitulo = leitura.nextLine();

        serieBuscar = repository.findByTituloContainingIgnoreCase(nomeTitulo);

        if (serieBuscar.isPresent()) {

            var serieEncontrada = serieBuscar.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(e.numero(), e)))
                    .collect(Collectors.toUnmodifiableList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void retornaListaDeSeries(){
        series = repository.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.println("Escolha a serie pelo titulo: ");
        var nomeTitulo = leitura.nextLine();
        serieBuscar = repository.findByTituloContainingIgnoreCase(nomeTitulo);

        if (serieBuscar.isPresent()) {
            System.out.println("Dados da série: " + serieBuscar.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarListaPorAtores() {
        System.out.println("Escontre a serie pelo ator? ");
        var nomeAtor = leitura.nextLine();
        System.out.println("A partir de qual avaliação você quer? ");
        var valorAvaliacao = leitura.nextDouble();
        List<Serie> buscarPorAtor = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, valorAvaliacao);
        buscarPorAtor.forEach(i ->
                System.out.println("Nome do filme: " + i.getTitulo() + " avaliacao: " + i.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repository.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(i ->
                System.out.println("Nome do filme: " + i.getTitulo() + " avaliacao: " + i.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Escolha uma serie por categoriaEnum/genero");
        var recebeCategoria = leitura.nextLine();
        CategoriaEnum categoriaEnum = CategoriaEnum.fromPortugues(recebeCategoria);
        List<Serie> buscarSerieCategoria = repository.findByGenero(categoriaEnum);
        System.out.println("Series encontradas do genero: " + buscarSerieCategoria);
        buscarSerieCategoria.forEach(System.out::println);
    }

    private void buscarSeriesPorQuantidadeTemporada() {
        System.out.println("Escolha a serie pela quantidade de temporadas");
        var recebeQuantidadeTemporada = leitura.nextDouble();
        System.out.println("A partir de qual avaliação você quer? ");
        var valorAvaliacao = leitura.nextDouble();
        List<Serie> seriePorTemporadaAndAvaliacao = repository.seriePorTemporadaEAvaliacao(recebeQuantidadeTemporada, valorAvaliacao);
        seriePorTemporadaAndAvaliacao.forEach(i ->
                System.out.println("Nome do filme: " + i.getTitulo() + " Temporada: " + i.getTotalTemporadas() + " Avaliacao: " + i.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Escontre a serie pelo trecho do episodio? ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodios = repository.episodioPorTrecho(trechoEpisodio);
        episodios.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscar.isPresent()) {
            Serie serie = serieBuscar.get();
            List<Episodio> ep = repository.topEpisodiosPorSerie(serie);
            ep.forEach(e ->
                    System.out.printf("Série: %s Temporada: %s - Episódio: %s Titulo: %s Avaliação: %s \n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosDepoisDeUmaData(){
        buscarSeriePorTitulo();
        if(serieBuscar.isPresent()){
            Serie serie = serieBuscar.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repository.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}
