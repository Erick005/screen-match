package br.com.alura.screanmatch.model;

public enum CategoriaEnum {

    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String categoriaOmdb;
    private String categoriaPortugues;

    CategoriaEnum(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static CategoriaEnum fromString(String text) {
        for (CategoriaEnum categoriaEnum : CategoriaEnum.values()) {
            if (categoriaEnum.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoriaEnum;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static CategoriaEnum fromPortugues(String text) {
        for (CategoriaEnum categoriaEnum : CategoriaEnum.values()) {
            if (categoriaEnum.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoriaEnum;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
