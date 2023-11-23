package com.api.templateconverter.dto;

import lombok.Data;

@Data
public class DadosArquivo {
    private String tipoComprovante;
    private String titulo;
    private String dataHora;
    private String numeroControle;
    private String valor;
    private String numeroBancoAgencia;
    private String canalPagamento;
    private String dataPagamento;
    private String codigoBarras;
    private String descricao;
    private String ufFavorecida;
    private String codigoReceita;
    private String cpfCnpj;
    private String diDsi;
    private String aiim;
    private String referencia;
    private String identificador;
    private String valorPagamento;
    private String autenticacao;
    private String codAutenticacao;


    private String razaoSocial;
    private String beneficiario;
    private String cpfCnpjBeneficiario;
    private String instituicao;
    private String beneficiarioFinal;
    private String cpfCnpjBeneficiarioFinal;

    private String bancoDestinatario;
    private String dataDebito;
    private String dataVencimento;
    private String valorInicial;
    private String desconto;
    private String abatimento;
    private String bonificacao;
    private String multa;
    private String juros;
    private String protocolo;

    private String nomePagador;
    private String cpfCnpjPagador;

    private String nomePagadorRegistradoBoleto;
    private String cpfCnpjPagadorRegistradoBoleto;

}
