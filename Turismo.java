import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Turismo implements Cloneable {
    private static final int INFINITO = Integer.MAX_VALUE / 2;
    private static final String[] AEROPORTOS = { "Miami", "Nova York", "Dallas", "Los Angeles", "Atlanta", "Chicago" };

    private Grafo<String, Integer> mapa;
    private int[] dias;
    private int[][] dist, proximo; 

    public Turismo(int tamanho) throws Exception {
        if (tamanho <= 0)
            throw new Exception("Tamanho inválido");
        this.mapa = new Grafo<String, Integer>(tamanho);
        this.dias = new int[tamanho];
    }

    public void adicionarCidade(String c) throws Exception {
        this.mapa.adicionarVertice(c);
        if (this.mapa.getQtd() > this.dias.length) {
            int[] novo = new int[2 * this.dias.length];
            for (int i = 0; i < this.dias.length; i++)
                novo[i] = this.dias[i];
            this.dias = novo;
        }
    }

    public void adicionarEstrada(String x, String y, int distancia) throws Exception {
        if (distancia <= 0)
            throw new Exception("Distância inválida");
        this.mapa.adicionarAresta(x, y, distancia);
    }

    public void setDias(String c, int d) throws Exception {
        if (d < 0)
            throw new Exception("Quantidade de dias inválida");
        this.dias[indice(c)] = d;
    }

    private int indice(String c) throws Exception {
        int local = this.mapa.procurarVertice(c);
        if (local == -1)
            throw new Exception("Cidade " + c + " não encontrada no mapa");
        return local;
    }

    private String nome(int i) throws Exception {
        return this.mapa.getVertice(i);
    }

    private void calcularDistancias() throws Exception {
        int n = this.mapa.getQtd();
        this.dist = new int[n][n];
        this.proximo = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                this.proximo[i][j] = j;
                if (i == j)
                    this.dist[i][j] = 0;
                else if (this.mapa.existeAresta(nome(i), nome(j)))
                    this.dist[i][j] = this.mapa.getAresta(nome(i), nome(j));
                else
                    this.dist[i][j] = INFINITO;
            }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (this.dist[i][k] + this.dist[k][j] < this.dist[i][j]) {
                        this.dist[i][j] = this.dist[i][k] + this.dist[k][j];
                        this.proximo[i][j] = this.proximo[i][k];
                    }
    }

    private int[] expandir(int[] rota, boolean volta) {
        int[] aux = new int[rota.length * this.mapa.getQtd() + 1];
        int tam = 0;
        aux[tam++] = rota[0];

        int fim = volta ? rota.length : rota.length - 1;
        for (int i = 0; i < fim; i++) {
            int a = rota[i], b = rota[(i + 1) % rota.length];
            while (a != b) {
                a = this.proximo[a][b];
                aux[tam++] = a;
            }
        }

        int[] ret = new int[tam];
        for (int i = 0; i < tam; i++)
            ret[i] = aux[i];
        return ret;
    }

    private String trechos(int[] caminho) throws Exception {
        String ret = "";
        int total = 0;
        for (int i = 0; i < caminho.length - 1; i++) {
            int peso = this.mapa.getAresta(nome(caminho[i]), nome(caminho[i + 1]));
            total += peso;
            ret += (i + 1) + ") " + nome(caminho[i]) + " -> " + nome(caminho[i + 1]) + ": " + peso * 10 + " km\n";
        }
        return ret + "Distância total: " + total * 10 + " km";
    }

    private int[] cidadesParaVisitar(String inicio, String[] escolhidas) throws Exception {
        boolean aeroporto = false;
        for (int i = 0; i < AEROPORTOS.length; i++)
            if (AEROPORTOS[i].equals(inicio))
                aeroporto = true;
        if (!aeroporto)
            throw new Exception(
                    "Só é possível desembarcar em Miami, Nova York, Dallas, Los Angeles, Atlanta ou Chicago");

        int n = this.mapa.getQtd();
        int ini = indice(inicio);
        boolean[] marcada = new boolean[n];
        marcada[ini] = true;
        int qtd = 1;

        for (int i = 0; i < (escolhidas == null ? n : escolhidas.length); i++) {
            int local = (escolhidas == null) ? i : indice(escolhidas[i]);
            if (!marcada[local]) {
                marcada[local] = true;
                qtd++;
            }
        }
        if (qtd < 2)
            throw new Exception("Escolha ao menos uma cidade além da inicial");

        int[] ret = new int[qtd];
        int pos = 0;
        ret[pos++] = ini;
        for (int i = 0; i < n; i++)
            if (marcada[i] && i != ini)
                ret[pos++] = i;

        for (int i = 1; i < qtd; i++)
            if (this.dist[ini][ret[i]] >= INFINITO)
                throw new Exception("Não existe estrada até " + nome(ret[i]));
        return ret;
    }

    private void inserir(int[] rota, int custo, int[][] melhores, int[] custos) {
        int[] e1 = expandir(rota, true);
        for (int i = 0; i < melhores.length && melhores[i] != null; i++) {
            int[] e2 = expandir(melhores[i], true);
            if (e1.length != e2.length)
                continue;
            boolean igual = true, inverso = true;
            for (int k = 0; k < e1.length; k++) {
                if (e1[k] != e2[k])
                    igual = false;
                if (e1[k] != e2[e2.length - 1 - k])
                    inverso = false;
            }
            if (igual || inverso)
                return;
        }

        int pos = 0;
        while (pos < custos.length && custos[pos] <= custo)
            pos++;
        if (pos == custos.length)
            return;

        for (int i = custos.length - 1; i > pos; i--) {
            custos[i] = custos[i - 1];
            melhores[i] = melhores[i - 1];
        }
        custos[pos] = custo;
        melhores[pos] = rota.clone();
    }

    private void permutar(int[] rota, boolean[] usado, int[] visitar, int pos, int parcial, int[][] melhores,
            int[] custos) {
        int n = visitar.length;
        if (parcial >= custos[custos.length - 1])
            return;
        if (pos == n) {
            inserir(rota, parcial + this.dist[rota[n - 1]][rota[0]], melhores, custos);
            return;
        }
        for (int i = 1; i < n; i++)
            if (!usado[i]) {
                usado[i] = true;
                rota[pos] = visitar[i];
                permutar(rota, usado, visitar, pos + 1, parcial + this.dist[rota[pos - 1]][visitar[i]], melhores,
                        custos);
                usado[i] = false;
            }
    }

   
    private void heuristica(int[] visitar, int[][] melhores, int[] custos) {
        int n = visitar.length;
        for (int s = 1; s < n; s++) {
            int[] rota = new int[n];
            boolean[] usado = new boolean[n];
            rota[0] = visitar[0];
            rota[1] = visitar[s];
            usado[0] = usado[s] = true;

            for (int pos = 2; pos < n; pos++) {
                int melhor = -1;
                for (int i = 1; i < n; i++)
                    if (!usado[i] && (melhor == -1
                            || this.dist[rota[pos - 1]][visitar[i]] < this.dist[rota[pos - 1]][visitar[melhor]]))
                        melhor = i;
                rota[pos] = visitar[melhor];
                usado[melhor] = true;
            }

            boolean melhorou = true;
            while (melhorou) {
                melhorou = false;
                for (int i = 1; i < n - 1; i++)
                    for (int j = i + 1; j < n; j++) {
                        int a = rota[i - 1], b = rota[i], c = rota[j], d = rota[(j + 1) % n];
                        if (this.dist[a][c] + this.dist[b][d] < this.dist[a][b] + this.dist[c][d]) {
                            for (int x = i, y = j; x < y; x++, y--) {
                                int aux = rota[x];
                                rota[x] = rota[y];
                                rota[y] = aux;
                            }
                            melhorou = true;
                        }
                    }
            }

            int custo = 0;
            for (int i = 0; i < n; i++)
                custo += this.dist[rota[i]][rota[(i + 1) % n]];
            inserir(rota, custo, melhores, custos);
        }
    }

    private int[][] melhoresRotas(String inicio, String[] escolhidas, int quantas) throws Exception {
        calcularDistancias();
        int[] visitar = cidadesParaVisitar(inicio, escolhidas);

        int[][] melhores = new int[quantas][];
        int[] custos = new int[quantas];
        for (int i = 0; i < quantas; i++)
            custos[i] = INFINITO;

        if (visitar.length <= 11) {
            int[] rota = new int[visitar.length];
            rota[0] = visitar[0];
            permutar(rota, new boolean[visitar.length], visitar, 1, 0, melhores, custos);
        } else
            heuristica(visitar, melhores, custos);

        return melhores;
    }

    public String percursos(String inicio, String[] escolhidas, int quantos) throws Exception {
        if (quantos <= 0)
            throw new Exception("Quantidade de percursos inválida");
        int[][] rotas = melhoresRotas(inicio, escolhidas, quantos);

        String ret = "";
        for (int i = 0; i < rotas.length && rotas[i] != null; i++)
            ret += (i + 1) + "º percurso:\n" + trechos(expandir(rotas[i], true)) + "\n\n";
        return ret;
    }

    public String menorCaminho(String origem, String destino) throws Exception {
        calcularDistancias();
        int o = indice(origem), d = indice(destino);
        if (o == d)
            throw new Exception("Origem e destino são a mesma cidade");
        if (this.dist[o][d] >= INFINITO)
            throw new Exception("Não existe caminho entre " + origem + " e " + destino);

        return trechos(expandir(new int[] { o, d }, false));
    }

    public String datas(String inicio, String[] escolhidas, LocalDate chegada) throws Exception {
        if (chegada == null)
            throw new Exception("Data de chegada ausente");
        int[] rota = melhoresRotas(inicio, escolhidas, 1)[0];
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String ret = "";
        for (int i = 0; i < rota.length; i++) {
            LocalDate saida = chegada.plusDays(this.dias[rota[i]]);
            ret += nome(rota[i]) + " - chegada: " + chegada.format(f) + ", saída: " + saida.format(f) + "\n";
            chegada = saida;
        }
        return ret + "Embarque de volta ao Brasil em " + nome(rota[0]) + ": " + chegada.format(f);
    }
        public String rotaComLimite(String inicio, int kmMaximo) throws Exception {
        if (kmMaximo <= 0)
            throw new Exception("Distância máxima inválida");
        calcularDistancias();

        int n = this.mapa.getQtd();
        int ini = indice(inicio);

        int[] rota = new int[n];          
        boolean[] naRota = new boolean[n];
        rota[0] = ini;
        naRota[ini] = true;
        int tam = 1, total = 0;

        // a cada passo coloca na rota a cidade que menos
        // aumenta a distância total, enquanto o total couber no limite
        for (;;) {
            int melhorCidade = -1, melhorPos = -1, menorAumento = INFINITO;

            for (int c = 0; c < n; c++) {
                if (naRota[c] || this.dist[ini][c] >= INFINITO)
                    continue;
                for (int p = 0; p < tam; p++) {
                    int a = rota[p], b = rota[(p + 1) % tam];
                    int aumento = this.dist[a][c] + this.dist[c][b] - this.dist[a][b];
                    if (aumento < menorAumento) {
                        menorAumento = aumento;
                        melhorCidade = c;
                        melhorPos = p;
                    }
                }
            }

            if (melhorCidade == -1 || total + menorAumento > kmMaximo)
                break;

            
            for (int i = tam; i > melhorPos + 1; i--)
                rota[i] = rota[i - 1];
            rota[melhorPos + 1] = melhorCidade;
            naRota[melhorCidade] = true;
            tam++;
            total += menorAumento;
        }

        if (tam == 1)
            throw new Exception("Nenhuma rota de ida e volta a partir de " + inicio + " cabe em " + kmMaximo + " km");

        int[] escolhida = new int[tam];
        for (int i = 0; i < tam; i++)
            escolhida[i] = rota[i];

        return "Rota de ida e volta a partir de " + inicio + " (limite de " + kmMaximo + " km):\n"
                + trechos(expandir(escolhida, true));
    }

    @Override
    public String toString() {
        return this.mapa.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        Turismo t = (Turismo) obj;

        if (!this.mapa.equals(t.mapa))
            return false;
        for (int i = 0; i < this.mapa.getQtd(); i++)
            if (this.dias[i] != t.dias[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 1;
        ret = ret * 2 + this.mapa.hashCode();
        for (int i = 0; i < this.mapa.getQtd(); i++)
            ret = ret * 2 + Integer.valueOf(this.dias[i]).hashCode();

        if (ret < 0)
            ret = -ret;
        return ret;
    }

    public Turismo(Turismo modelo) throws Exception {
        if (modelo == null)
            throw new Exception("Modelo ausente");

        this.mapa = new Grafo<String, Integer>(modelo.mapa);
        this.dias = new int[modelo.dias.length];
        for (int i = 0; i < modelo.dias.length; i++)
            this.dias[i] = modelo.dias[i];
    }

    @Override
    public Object clone() {
        Turismo ret = null;
        try {
            ret = new Turismo(this);
        } catch (Exception erro) {
        }
        return ret;
    }
}
