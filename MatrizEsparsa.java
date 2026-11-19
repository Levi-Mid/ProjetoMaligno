public class MatrizEsparsa<X> implements Cloneable {

    // uma posição da linha que tem valor diferente do padrão
    private static class Coluna<X> implements Comparable<Coluna<X>>, Cloneable {
        private int num;
        private X valor;

        public Coluna(int num, X valor) {
            this.num = num;
            this.valor = valor;
        }

        public int getNum() {
            return this.num;
        }

        public X getValor() {
            return this.valor;
        }

        @Override
        public int compareTo(Coluna<X> outra) {
            if (this.num < outra.num) return -1;
            if (this.num > outra.num) return 1;
            return 0;
        }

        public Coluna(Coluna<X> modelo) throws Exception {
            if (modelo == null) throw new Exception("Modelo ausente");
            this.num = modelo.num;
            if (modelo.valor instanceof Cloneable) this.valor = new Clonador<X>().clone(modelo.valor);
            else this.valor = modelo.valor;
        }

        @Override
        public Object clone() {
            Coluna<X> ret = null;
            try {
                ret = new Coluna<X>(this);
            } catch (Exception erro) {}
            return ret;
        }
    }

    // uma linha da matriz - guarda numa árvore as colunas que têm valor
    private static class Linha<X> implements Comparable<Linha<X>>, Cloneable {
        private int num;
        private ArvoreBinariaDeBusca<Coluna<X>> colunas;
        private int[] cols;        // números das colunas guardadas em ordem 
        private int qtdCols = 0;

        public Linha(int num) {
            this.num = num;
            this.colunas = new ArvoreBinariaDeBusca<Coluna<X>>();
            this.cols = new int[4];
        }

        @Override
        public int compareTo(Linha<X> outra) {
            if (this.num < outra.num) return -1;
            if (this.num > outra.num) return 1;
            return 0;
        }

        public Linha(Linha<X> modelo) throws Exception {
            if (modelo == null) throw new Exception("Modelo ausente");
            this.num = modelo.num;
            this.colunas = (ArvoreBinariaDeBusca<Coluna<X>>) modelo.colunas.clone();
            this.qtdCols = modelo.qtdCols;
            this.cols = new int[modelo.cols.length];
            for (int i = 0; i < modelo.qtdCols; i++)
                this.cols[i] = modelo.cols[i];
        }

        @Override
        public Object clone() {
            Linha<X> ret = null;
            try {
                ret = new Linha<X>(this);
            } catch (Exception erro) {}
            return ret;
        }
    }

    private ArvoreBinariaDeBusca<Linha<X>> linhas;
    private int[] lins;            // números das linhas guardadas
    private int qtdLins = 0;
    private X valorPadrao;

    public MatrizEsparsa(X valorPadrao) {
        if (valorPadrao instanceof Cloneable) this.valorPadrao = new Clonador<X>().clone(valorPadrao);
        else this.valorPadrao = valorPadrao;
        this.linhas = new ArvoreBinariaDeBusca<Linha<X>>();
        this.lins = new int[4];
    }

    private boolean ehPadrao(X valor) {
        if (valor == null) return this.valorPadrao == null;
        return valor.equals(this.valorPadrao);
    }

    private X padrao() {
        if (this.valorPadrao instanceof Cloneable) return new Clonador<X>().clone(this.valorPadrao);
        return this.valorPadrao;
    }

    // coloca no vetor em ordem crescente - devolve o vetor
    private static int[] incluirEmOrdem(int[] vet, int qtd, int num) {
        if (qtd == vet.length) {
            int[] novo = new int[2 * vet.length];
            for (int i = 0; i < qtd; i++) novo[i] = vet[i];
            vet = novo;
        }
        int pos = qtd;
        while (pos > 0 && vet[pos - 1] > num) {
            vet[pos] = vet[pos - 1];
            pos--;
        }
        vet[pos] = num;
        return vet;
    }

    // tira num do vetor puxando o da frente uma posição para trás
    private static void excluirDoVetor(int[] vet, int qtd, int num) {
        int pos = 0;
        while (pos < qtd && vet[pos] != num) pos++;
        for (int i = pos; i < qtd - 1; i++) vet[i] = vet[i + 1];
    }

    private X valorNaLinha(Linha<X> linha, int col) throws Exception {
        return linha.colunas.getOItem(new Coluna<X>(col, null)).getValor();
    }

    public void setValor(int lin, int col, X valor) throws Exception {
        if (lin < 0 || col < 0) throw new Exception("Posição inválida");

        Linha<X> chaveLinha = new Linha<X>(lin);
        Coluna<X> chaveColuna = new Coluna<X>(col, null);

        // a linha ainda não existe
        if (!this.linhas.temOItem(chaveLinha)) {
            if (ehPadrao(valor)) return;   // valor padrão não precisa ser guardado
            chaveLinha.colunas.guardeUmItem(new Coluna<X>(col, valor));
            chaveLinha.cols = incluirEmOrdem(chaveLinha.cols, chaveLinha.qtdCols, col);
            chaveLinha.qtdCols++;
            this.linhas.guardeUmItem(chaveLinha);
            this.lins = incluirEmOrdem(this.lins, this.qtdLins, lin);
            this.qtdLins++;
            return;
        }

        // a arvore devolve uma COPIA da linha
        // tira a antiga da árvore e guarda a nova no lugar
        Linha<X> linha = this.linhas.getOItem(chaveLinha);
        this.linhas.removaUmItem(linha);

        boolean tinhaColuna = linha.colunas.temOItem(chaveColuna);
        if (tinhaColuna) linha.colunas.removaUmItem(chaveColuna);

        if (ehPadrao(valor)) {
            if (tinhaColuna) {
                excluirDoVetor(linha.cols, linha.qtdCols, col);
                linha.qtdCols--;
            }
        } else {
            linha.colunas.guardeUmItem(new Coluna<X>(col, valor));
            if (!tinhaColuna) {
                linha.cols = incluirEmOrdem(linha.cols, linha.qtdCols, col);
                linha.qtdCols++;
            }
        }

        // linha sem nenhuma coluna não precisa existir
        if (linha.qtdCols == 0) {
            excluirDoVetor(this.lins, this.qtdLins, lin);
            this.qtdLins--;
        } else this.linhas.guardeUmItem(linha);
    }

    public X getValor(int lin, int col) throws Exception {
        if (lin < 0 || col < 0) throw new Exception("Posição inválida");

        Linha<X> chaveLinha = new Linha<X>(lin);
        if (!this.linhas.temOItem(chaveLinha)) return padrao();

        Linha<X> linha = this.linhas.getOItem(chaveLinha);
        if (!linha.colunas.temOItem(new Coluna<X>(col, null))) return padrao();

        return valorNaLinha(linha, col);
    }

    // numeros das linhas que tem algum valor
    public int[] getLinhas() {
        int[] ret = new int[this.qtdLins];
        for (int i = 0; i < this.qtdLins; i++) ret[i] = this.lins[i];
        return ret;
    }

    // numeros das colunas com valor numa linha
    public int[] getColunas(int lin) throws Exception {
        if (lin < 0) throw new Exception("Linha inválida");

        Linha<X> chave = new Linha<X>(lin);
        if (!this.linhas.temOItem(chave)) return new int[0];

        Linha<X> linha = this.linhas.getOItem(chave);
        int[] ret = new int[linha.qtdCols];
        for (int i = 0; i < linha.qtdCols; i++) ret[i] = linha.cols[i];
        return ret;
    }

    @Override
    public String toString() {
        String ret = "L C V\n";
        try {
            for (int i = 0; i < this.qtdLins; i++) {
                Linha<X> linha = this.linhas.getOItem(new Linha<X>(this.lins[i]));
                for (int j = 0; j < linha.qtdCols; j++)
                    ret += linha.num + " " + linha.cols[j] + " " + valorNaLinha(linha, linha.cols[j]) + "\n";
            }
        } catch (Exception erro) {}
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        MatrizEsparsa<X> m = (MatrizEsparsa<X>) obj;

        if (this.valorPadrao == null) {
            if (m.valorPadrao != null) return false;
        }
        else if (!this.valorPadrao.equals(m.valorPadrao)) return false;

        if (this.qtdLins != m.qtdLins) return false;

        // os vetores estão em ordem então da para comparar posição a posição
        try {
            for (int i = 0; i < this.qtdLins; i++) {
                if (this.lins[i] != m.lins[i]) return false;

                Linha<X> l1 = this.linhas.getOItem(new Linha<X>(this.lins[i]));
                Linha<X> l2 = m.linhas.getOItem(new Linha<X>(m.lins[i]));
                if (l1.qtdCols != l2.qtdCols) return false;

                for (int j = 0; j < l1.qtdCols; j++) {
                    if (l1.cols[j] != l2.cols[j]) return false;
                    X v1 = valorNaLinha(l1, l1.cols[j]);
                    X v2 = valorNaLinha(l2, l2.cols[j]);
                    if (v1 == null) {
                        if (v2 != null) return false;
                    }
                    else if (!v1.equals(v2)) return false;
                }
            }
        } catch (Exception erro) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 1;
        if (this.valorPadrao != null) ret = ret * 7 + this.valorPadrao.hashCode();

        try {
            for (int i = 0; i < this.qtdLins; i++) {
                Linha<X> linha = this.linhas.getOItem(new Linha<X>(this.lins[i]));
                ret = ret * 7 + Integer.valueOf(this.lins[i]).hashCode();

                for (int j = 0; j < linha.qtdCols; j++) {
                    ret = ret * 7 + Integer.valueOf(linha.cols[j]).hashCode();
                    X valor = valorNaLinha(linha, linha.cols[j]);
                    if (valor != null) ret = ret * 7 + valor.hashCode();
                }
            }
        } catch (Exception erro) {}

        if (ret < 0) ret = -ret;
        return ret;
    }

    public MatrizEsparsa(MatrizEsparsa<X> modelo) throws Exception {
        if (modelo == null) throw new Exception("Modelo ausente");

        if (modelo.valorPadrao instanceof Cloneable) this.valorPadrao = new Clonador<X>().clone(modelo.valorPadrao);
        else this.valorPadrao = modelo.valorPadrao;

        this.linhas = (ArvoreBinariaDeBusca<Linha<X>>) modelo.linhas.clone();
        this.qtdLins = modelo.qtdLins;
        this.lins = new int[modelo.lins.length];
        for (int i = 0; i < modelo.qtdLins; i++)
            this.lins[i] = modelo.lins[i];
    }

    @Override
    public Object clone() {
        MatrizEsparsa<X> ret = null;
        try {
            ret = new MatrizEsparsa<X>(this);
        } catch (Exception erro) {}
        return ret;
    }
}