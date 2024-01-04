package br.com.haroldo.TabelaFipe.principal;

import br.com.haroldo.TabelaFipe.model.Dados;
import br.com.haroldo.TabelaFipe.model.Modelos;
import br.com.haroldo.TabelaFipe.model.Veiculo;
import br.com.haroldo.TabelaFipe.service.ConsumirApi;
import br.com.haroldo.TabelaFipe.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private Dados dados;
    private Modelos modelos;
    private ConsumirApi consumirApi = new ConsumirApi();
    private ConverterDados converterDados = new ConverterDados();
    private final String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";

    public void exibirMenu() {
        var menu = """
                Escolha o tipo de veiculo para consultar:
                Carros
                Motos
                Caminhoes
                """;

        System.out.println(menu);
        var tipoVeiculo = leitura.nextLine();
        String endereco;

        if (tipoVeiculo.toLowerCase().contains("carr")) {
            endereco = ENDERECO + "carros/marcas";
        } else if (tipoVeiculo.toLowerCase().contains("mot")) {
            endereco = ENDERECO + "motos/marcas";
        } else {
            endereco = ENDERECO + "caminhoes/marcas";
        }
        var json = consumirApi.obterDados(endereco);
        System.out.println(json);

        var marcas = converterDados.obterlista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta:");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumirApi.obterDados(endereco);
        var modeloLista = converterDados.obterDados(json, Modelos.class);

        System.out.println("\n Modelos dessa marca: ");
        modeloLista.modelos().stream().
                sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\n Digite o nome do carro a ser buscado.");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase()
                        .contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\n Modelos filtrados.");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o codigo do modelo para busca os valores.");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/" + "anos";
        json = consumirApi.obterDados(endereco);
        List<Dados> anos = converterDados.obterlista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumirApi.obterDados(enderecoAnos);
            Veiculo veiculo = converterDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n Todos os veiculos filtrados por ano: ");
        veiculos.forEach(System.out::println);
    }
}
