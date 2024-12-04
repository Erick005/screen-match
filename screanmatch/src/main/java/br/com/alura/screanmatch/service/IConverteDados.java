package br.com.alura.screanmatch.service;

public interface IConverteDados {
    <T> T obterDados (String json, Class<T> classe);
}
