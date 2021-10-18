package br.com.aula;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.aula.exception.*;

public class Banco {

    private List<Conta> contas = new ArrayList<Conta>();

    public Banco() {
    }

    public Banco(List<Conta> contas) {
        this.contas = contas;
    }


    public void cadastrarConta(Conta conta) throws ContaJaExistenteException, ContaNumeroInvalidoException {
        Pattern pattern = Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(Integer.toString(conta.getNumeroConta()));
        boolean matchFound = matcher.find();

        if (!matchFound) {
            throw new ContaNumeroInvalidoException();
        }

        for (Conta c : contas) {
            boolean isNomeClienteIgual = c.getCliente().getNome().equals(conta.getCliente().getNome());
            boolean isNumeroContaIgual = c.getNumeroConta() == conta.getNumeroConta();


            if (isNomeClienteIgual || isNumeroContaIgual) {
                throw new ContaJaExistenteException();
            }
        }

        this.contas.add(conta);
    }

    public void efetuarTransferencia(int numeroContaOrigem, int numeroContaDestino, int valor)
            throws ContaNaoExistenteException, ContaSemSaldoException, ValorNegativoException {

        Conta contaOrigem = this.obterContaPorNumero(numeroContaOrigem);
        Conta contaDestino = this.obterContaPorNumero(numeroContaDestino);

        boolean isContaOrigemExistente = contaOrigem != null;
        boolean isContaDestinoExistente = contaDestino != null;

        if (isContaOrigemExistente && isContaDestinoExistente) {

            boolean isContaOrigemPoupança = contaOrigem.getTipoConta().equals(TipoConta.POUPANCA);
            boolean isSaldoContaOrigemNegativo = contaOrigem.getSaldo() - valor < 0;

            if (isContaOrigemPoupança && isSaldoContaOrigemNegativo) {
                throw new ContaSemSaldoException();
            }

            if (valor < 0) {
                throw new ValorNegativoException();
            }

            contaOrigem.debitar(valor);
            contaDestino.creditar(valor);

        } else {
            throw new ContaNaoExistenteException();
        }
    }

    public Conta obterContaPorNumero(int numeroConta) {

        for (Conta c : contas) {
            if (c.getNumeroConta() == numeroConta) {
                return c;
            }
        }

        return null;
    }

    public List<Conta> obterContas() {
        return this.contas;
    }
}
