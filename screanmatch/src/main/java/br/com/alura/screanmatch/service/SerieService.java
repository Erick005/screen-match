package br.com.alura.screanmatch.service;

import br.com.alura.screanmatch.dto.EpisodioDTO;
import br.com.alura.screanmatch.dto.SerieDTO;
import br.com.alura.screanmatch.model.CategoriaEnum;
import br.com.alura.screanmatch.model.Serie;
import br.com.alura.screanmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> obterTodasAsSeries() {
        return converteDados(repository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repository.lancamentosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getImagemPoster(),
                    s.getGenero(), s.getAtores(), s.getSinopse());
        } else {
            return null;
        }
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getImagemPoster(),
                        s.getGenero(), s.getAtores(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<EpisodioDTO> obterTodasAsTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();

            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return repository.obterSeriesPorTemporadas(id, numero).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeriesPorCategoria(String numero) {
        CategoriaEnum categoriaEnum = CategoriaEnum.fromPortugues(numero);
        return converteDados(repository.findByGenero(categoriaEnum));
    }
}
