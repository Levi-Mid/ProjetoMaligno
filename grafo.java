public class Grafo<V, A> implements Cloneable {
    private V[] vertice;
    private A[][] aresta;
    private int qtd = 0;
    private final int tamanhoInicial;

    public Grafo(int tamanho) throws Exception {
        if (tamanho <= 0) throw new Exception("Tamanho inválido");
        this.vertice = (V[]) new Object[tamanho];
        this.aresta = (A[][]) new Object[tamanho][tamanho];
        this.tamanhoInicial = tamanho;
    }
    
    private void redimensione(int novoTamanho){
        V[] novoVertice = (V[]) new Object[novoTamanho];
        A[][] novaAresta = (A[][]) new Object[novoTamanho][novoTamanho];
        int pos = 0;
        
        for (int i = 0; i < this.vertice.length; i++) {
            if (this.vertice[i] != null) {
                novoVertice[pos] = this.vertice[i];
                
                int posJ = 0;
                for (int j = 0; j < this.vertice.length; j++) {
                    if (this.vertice[j] != null) {
                        novaAresta[pos][posJ] = this.aresta[i][j];
                        posJ++;
                    }
                }
                pos++;
            }
        }
        
        this.vertice = novoVertice;
        this.aresta = novaAresta;
    }
    
    public int getQtd() {
        return this.qtd;
    }
    
    public V getVertice(int posicao) throws Exception{
        if (qtd == 0) throw new Exception ("Grafo está vazio");
        if (posicao < 0 || posicao >= this.vertice.length || this.vertice[posicao] == null) throw new Exception ("informação invalida");
        if (this.vertice[posicao] instanceof Cloneable) return new Clonador<V>().clone(this.vertice[posicao]);
        return this.vertice[posicao];
    }
    
    public A getAresta(V x, V y) throws Exception {
        int local1 = procurarVertice(x);
        int local2 = procurarVertice(y);
        if (local1 == -1 || local2 == -1) throw new Exception("informação não encontrada no Grafo");
        if (local1 == local2) throw new Exception("Não é possivel existir aresta entre o mesmo vertice");
        if (this.aresta[local1][local2] == null) throw new Exception("Aresta inexistente");

        if (this.aresta[local1][local2] instanceof Cloneable) return new Clonador<A>().clone(this.aresta[local1][local2]);
        return this.aresta[local1][local2];
    }
    
    public int procurarVertice(V v)throws Exception{
        if (v == null) throw new Exception ("informação vazia");
        for(int i = 0; i < this.vertice.length; i++){
            if(vertice[i] != null && vertice[i].equals(v)) return i;       
        }
        return -1;
    }
    
    public void adicionarVertice(V v) throws Exception {
        if (procurarVertice(v) != -1) throw new Exception("Informação já está incluida");
        
        if (qtd == this.vertice.length)
            this.redimensione(2*this.vertice.length);
        
        int livre = 0;
        while (this.vertice[livre] != null) livre++;
        
        if (v instanceof Cloneable) this.vertice[livre] = new Clonador<V>().clone(v);
        else this.vertice[livre] = v;
        qtd++;
    }
    
    public void removerVertice(V v) throws Exception {
        int local = procurarVertice(v);
        if (local == -1) throw new Exception("informação não encontrada no Grafo");
        
        for (int i = 0; i < this.vertice.length; i++) {
            this.aresta[local][i] = null;
            this.aresta[i][local] = null;
        }
        this.vertice[local] = null;
        qtd--;
        
        if (this.vertice.length > this.tamanhoInicial && qtd <= this.vertice.length/4)
            this.redimensione(this.vertice.length/2);
    }
    
    public void adicionarAresta(V x, V y, A peso) throws Exception {
        if (peso == null) throw new Exception("Peso não pode ser vazio");
        int local1 = procurarVertice(x);
        int local2 = procurarVertice(y);
        if (local1 == -1 || local2 == -1) throw new Exception("informação não encontrada no Grafo");
        if (local1 == local2) throw new Exception("Não é possivel existir aresta entre o mesmo vertice");
    
        A p;
        if (peso instanceof Cloneable) p = new Clonador<A>().clone(peso);
        else p = peso;
        
        this.aresta[local1][local2] = p;
        this.aresta[local2][local1] = p;
    }
    
    public boolean existeAresta(V x, V y) throws Exception {
        int local1 = procurarVertice(x);
        int local2 = procurarVertice(y);
        if (local1 == -1 || local2 == -1) throw new Exception("informação não encontrada no Grafo");
        if (local1 == local2) throw new Exception("Não é possivel existir aresta entre o mesmo vertice");
    
        return this.aresta[local1][local2] != null;
    }
    
    public void removerAresta(V x, V y) throws Exception {
        int local1 = procurarVertice(x);
        int local2 = procurarVertice(y);
        if (local1 == -1 || local2 == -1) throw new Exception("informação não encontrada no Grafo");
        if (local1 == local2) throw new Exception("Não é possivel existir aresta entre o mesmo vertice");
        
        this.aresta[local1][local2] = null;
        this.aresta[local2][local1] = null;
    }
    
    public V[] vizinhos(V v) throws Exception {
        int local = procurarVertice(v);
        if (local == -1) throw new Exception("informação não encontrada no Grafo");

        int quantidade = 0;
        for (int j = 0; j < this.vertice.length; j++) {
            if (this.aresta[local][j] != null) quantidade++;
        }

        V[] vizinhos = (V[]) new Object[quantidade];
        int pos = 0;
        for (int j = 0; j < this.vertice.length; j++) {
            if (this.aresta[local][j] != null) {
                if (this.vertice[j] instanceof Cloneable) vizinhos[pos] = new Clonador<V>().clone(this.vertice[j]);
                else vizinhos[pos] = this.vertice[j];
                pos++;
            }
        }
        return vizinhos;
    }

    @Override
    public String toString() {
        String ret = "Vértices: {";
        boolean primeiro = true;
        for (int i = 0; i < this.vertice.length; i++) {
            if (this.vertice[i] != null) {
                if (!primeiro) ret += ", ";
                ret += this.vertice[i];
                primeiro = false;
            }
        }

        ret += "}\nArestas: {";
        primeiro = true;
        for (int i = 0; i < this.vertice.length; i++) {
            for (int j = i + 1; j < this.vertice.length; j++) {
                if (this.aresta[i][j] != null) {
                    if (!primeiro) ret += ", ";
                    ret += "(" + this.vertice[i] + " - " + this.vertice[j] + ": " + this.aresta[i][j] + ")";
                    primeiro = false;
                }
            }
        }
        ret += "}";
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Grafo<V, A> g = (Grafo<V, A>) obj;

        if (this.qtd != g.qtd) return false;
        if (this.vertice.length != g.vertice.length) return false;

        for (int i = 0; i < this.vertice.length; i++) {
            if (this.vertice[i] == null) {
                if (g.vertice[i] != null) return false;
            }
            else if (!this.vertice[i].equals(g.vertice[i])) return false;

            for (int j = 0; j < this.vertice.length; j++) {
                if (this.aresta[i][j] == null) {
                    if (g.aresta[i][j] != null) return false;
                }
                else if (!this.aresta[i][j].equals(g.aresta[i][j])) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 1;
        ret = ret * 2 + Integer.valueOf(this.qtd).hashCode();

        for (int i = 0; i < this.vertice.length; i++) {
            if (this.vertice[i] != null)
                ret = ret * 2 + this.vertice[i].hashCode();

            for (int j = 0; j < this.vertice.length; j++) {
                if (this.aresta[i][j] != null)
                    ret = ret * 2 + this.aresta[i][j].hashCode();
            }
        }

        if (ret < 0) ret = -ret;
        return ret;
    }

    public Grafo(Grafo<V, A> modelo) throws Exception {
        if (modelo == null) throw new Exception("Modelo ausente");

        this.qtd = modelo.qtd;
        this.tamanhoInicial = modelo.tamanhoInicial;
        this.vertice = (V[]) new Object[modelo.vertice.length];
        this.aresta = (A[][]) new Object[modelo.vertice.length][modelo.vertice.length];

        for (int i = 0; i < modelo.vertice.length; i++) {
            this.vertice[i] = modelo.vertice[i];
            for (int j = 0; j < modelo.vertice.length; j++)
                this.aresta[i][j] = modelo.aresta[i][j];
        }
    }

    @Override
    public Object clone() {
        Grafo<V, A> ret = null;
        try {
            ret = new Grafo<V, A>(this);
        } catch (Exception erro) {}
        return ret;
    }
}
