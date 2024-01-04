package br.com.haroldo.TabelaFipe.service;

import java.util.List;

public interface IConverterDados {

    <T> T obterDados(String json, Class<T> classe);

    <T> List<T> obterlista(String json, Class<T> classe);
}