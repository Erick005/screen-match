package br.com.alura.screanmatch.dto;

import br.com.alura.screanmatch.model.CategoriaEnum;

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalTemporadas,
                       Double avaliacao,
                       String imagemPoster,
                       CategoriaEnum genero,
                       String atores,
                       String sinopse) {
}
