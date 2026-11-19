import java.lang.reflect.*;

public class ArvoreBinariaDeBusca <X extends Comparable<X>> implements Cloneable
{
    private class No
    {
        private No esq;
        private X  info;
        private No dir;

        public No (No e, X i, No d)
        {
            this.esq  = e;
            this.info = i;
            this.dir  = d;
        }

        public No (X i)
        {
            this.esq  = null;
            this.info = i;
            this.dir  = null;
        }

        public No getEsq ()
        {
            return this.esq;
        }

        public X getInfo ()
        {
            return this.info;
        }

        public No getDir ()
        {
            return this.dir;
        }

        public void setEsq (No e)
        {
            this.esq = e;
        }
        
        public void setInfo (X i)
        {
            this.info = i;
        }

        public void setDir (No d)
        {
            this.dir = d;
        }
    } //fim da classe No

    private No raiz;

    public ArvoreBinariaDeBusca ()
    {
        this.raiz = null;
    }
    
    private X meuCloneDeX (X x)
    {
        X ret=null;

        try
        {
            Class<?> classe         = x.getClass();
            Class<?>[] tipoDosParms = null;
            Method metodo           = classe.getMethod("clone",tipoDosParms);
            Object[] parms          = null;
            ret                     = (X)metodo.invoke(x,parms);
        }
        catch(NoSuchMethodException erro)
        {}
        catch(IllegalAccessException erro)
        {}
        catch(InvocationTargetException erro)
        {}

        return ret;
    }
    
    public void guardeUmItem (X i) throws Exception
    {
        if (i==null) throw new Exception ("Informacao ausente");
        
        if (this.raiz==null)
        {
            if (i instanceof Cloneable) i=this.meuCloneDeX(i);
            this.raiz = new No (i);
            return;
        }
        
        No atual=this.raiz;
        for(;;) // forever
        {
            int comparacao=i.compareTo(atual.getInfo());
            
            if (comparacao==0) throw new Exception ("Elemento repetido");
            
            if (comparacao<0)
            {
                if (atual.getEsq()==null)
                {
                    if (i instanceof Cloneable) i=this.meuCloneDeX(i);
                    atual.setEsq (new No (i));
                    return;
                }
                else
                    atual=atual.getEsq();
            }
            else // comparacao>0
            {
                if (atual.getDir()==null)
                {
                    if (i instanceof Cloneable) i=this.meuCloneDeX(i);
                    atual.setDir (new No (i));
                    return;
                }
                else
                    atual=atual.getDir();
            }
        }
    }
    
    public boolean temOItem (X i) throws Exception
    {
        if (i==null) throw new Exception ("Informacao ausente");
        
        if (this.raiz==null) return false;
        
        No atual=this.raiz;
        while (atual!=null)
        {
            int comparacao=i.compareTo(atual.getInfo());
            if (comparacao==0) return true;
            if (comparacao<0)
                atual=atual.getEsq();
            else // comparacao>0
                atual=atual.getDir();
        }
        return false;
    }

    // procura o item e devolve a informacao guardada na arvore
    // (clonada, se for Cloneable, assim como o guardeUmItem clona na entrada)
    public X getOItem (X i) throws Exception
    {
        if (i==null) throw new Exception ("Informacao ausente");
        
        No atual=this.raiz;
        while (atual!=null)
        {
            int comparacao=i.compareTo(atual.getInfo());
            if (comparacao==0)
            {
                if (atual.getInfo() instanceof Cloneable)
                    return this.meuCloneDeX(atual.getInfo());
                return atual.getInfo();
            }
            if (comparacao<0)
                atual=atual.getEsq();
            else // comparacao>0
                atual=atual.getDir();
        }
        throw new Exception ("Elemento inexistente");
    }
    
    private int getQtdDeNodos (No r)
    {
        if (r==null) return 0;
        
        return getQtdDeNodos(r.getEsq())+1+getQtdDeNodos(r.getDir());
    }
    
    public int getQtdDeNodos ()
    {
        return getQtdDeNodos (this.raiz);
    }
    
    public void removaUmItem (X i) throws Exception
    {
        if (i==null) throw new Exception ("Informacao ausente");
        if (this.raiz == null) throw new Exception("Arvore vazia");

        No atual = this.raiz;
        No pai = null;
        boolean filhoEsquerdo = true;

        for(;;) // forever
        {
            int comparacao = i.compareTo(atual.getInfo());
            if (comparacao==0) break;
     
            pai = atual;
            if (comparacao<0)
            {
                atual=atual.getEsq();
                filhoEsquerdo=true;
            }
            else // comparacao>0
            {
                atual=atual.getDir();
                filhoEsquerdo=false;
            }
            
            if (atual==null) throw new Exception ("Remocao de algo inexistente");
        }

        // se a info for encontrada numa folha, deslique a folha da árvore,
        // fazendo o ponteiro que aponta para ela dentro do seu nó pai,
        // tornar-se null
        if (atual.getEsq()==null && atual.getDir()==null)
        {
            if (atual==this.raiz)
                this.raiz=null;
            else if (filhoEsquerdo)
                pai.setEsq(null);
            else
                pai.setDir(null);
        }
        // se info for encontrada num nó N, que não é folha, sendo que N
        // só tem filho à esquerda, e sendo N filho esquerdo de um certo
        // pai P, faça o ponteiro esquerdo de P, passar a apontar para
        // esse filho que ha na esquerda de N
        else if (atual.getDir()==null && filhoEsquerdo)
        {
            if (atual==this.raiz)
                this.raiz=atual.getEsq();
            else
                pai.setEsq(atual.getEsq());
        }
        // se info for encontrada num nó N, que não é folha, sendo que N
        // só tem filho à esquerda, e sendo N filho direito de um certo
        // pai P, faça o ponteiro direito de P, passar a apontar para
        // esse filho que ha na esquerda de N
        else if (atual.getDir()==null && !filhoEsquerdo)
        {
            if (atual==this.raiz)
                this.raiz=atual.getEsq();
            else
                pai.setDir(atual.getEsq());
        }
        // se info for encontrada num nó N, que não é folha, sendo que N
        // só tem filho à direita, e sendo N filho esquerdo de um certo
        // pai P, faça o ponteiro esquerdo de P, passar a apontar para
        // esse filho que ha na direita de N
        else if (atual.getEsq()==null && filhoEsquerdo)
        {
            if (atual==this.raiz)
                this.raiz=atual.getDir();
            else
                pai.setEsq(atual.getDir());
        }
        // se info for encontrada num nó N, que não é folha, sendo que N
        // só tem filho à direita, e sendo N filho direita de um certo
        // pai P, faça o ponteiro direito de P, passar a apontar para
        // esse filho que ha na direita de N
        else if (atual.getEsq()==null && !filhoEsquerdo)
        {
            if (atual==this.raiz)
                this.raiz=atual.getDir();
            else
                pai.setDir(atual.getDir());
        }
        // se info for encontrada num nó N, que não é folha e tem 2 filhos,
        // encontre a informação info que existe à extrema esquerda da
        // subarvore direita de N ou à extrema direita da subarvore esquerda
        // de N; remova o nó que contém info e substitua dentro do nó N,
        // a informação que ali se encontra por info
        else
        {
            No substituto = null;
            pai = atual;
            if (getQtdDeNodos(atual.getEsq())>getQtdDeNodos(atual.getDir()))
            {
                // maior da subarvore esquerda (ele nao tem filho a direita)
                substituto = atual.getEsq();
                while (substituto.getDir() != null)
                {
                    pai = substituto;
                    substituto = substituto.getDir();
                }
                // o filho esquerdo do substituto ocupa o lugar dele
                if (pai==atual)
                    pai.setEsq(substituto.getEsq());
                else
                    pai.setDir(substituto.getEsq());
            }
            else
            {
                // menor da subarvore direita (ele nao tem filho a esquerda)
                substituto = atual.getDir();
                while (substituto.getEsq() != null)
                {
                    pai = substituto;
                    substituto = substituto.getEsq();
                }
                // o filho direito do substituto ocupa o lugar dele
                if (pai==atual)
                    pai.setDir(substituto.getDir());
                else
                    pai.setEsq(substituto.getDir());
            }

            atual.setInfo(substituto.getInfo());
        }
    }

    // altura de arvore vazia = 0; de uma arvore so com a raiz = 1
    private int getAltura (No r)
    {
        if (r==null) return 0;

        int altEsq = this.getAltura(r.getEsq()),
            altDir = this.getAltura(r.getDir());

        return 1 + Math.max(altEsq, altDir);
    }
    
    public int getAltura ()
    {
        return this.getAltura(this.raiz);
    }
    
	private boolean isBalanceada (No r)
	{
        if (r==null) return true;

		int qtdEsq = this.getQtdDeNodos(r.getEsq()),
		    qtdDir = this.getQtdDeNodos(r.getDir());
		    
		if (qtdEsq>qtdDir+1 || qtdDir>qtdEsq+1) return false;
		
		/*
		Podemos trocar todos os comandos abaixo por:
		return isBalanceada(r.getEsq()) && isBalanceada(r.getDir());
		*/
		
		if (!this.isBalanceada(r.getEsq())) return false;
		if (!this.isBalanceada(r.getDir())) return false;
		
		return true;
	}
	
	public boolean isBalanceada ()
	{
		return this.isBalanceada(this.raiz);
	}
	
	private void balanceieSe (No r) throws Exception
	{
        if (r==null) return;

		int qtdEsq = this.getQtdDeNodos(r.getEsq()),
		    qtdDir = this.getQtdDeNodos(r.getDir());
		    
		while (Math.abs(qtdEsq-qtdDir)>1)
			if (qtdEsq>qtdDir)
            {
				// giro para a direita, giro no sentido horário
				No pai   = r;
				No atual = r.getEsq();
				while (atual.getDir() != null)
				{
					pai   = atual;
					atual = atual.getDir();
				}
				X infoRaiz = r.getInfo();
				r.setInfo(atual.getInfo());
                if (pai==r)
                    pai.setEsq(atual.getEsq());
                else
				    pai.setDir(atual.getEsq());
				qtdEsq--;
				this.guardeUmItem(infoRaiz);
				qtdDir++;
			}
			else // qtdDir>qtdEsq
            {
				// giro para a esquerda, giro no sentido antihorário
				No pai   = r;
				No atual = r.getDir();
				while (atual.getEsq() != null)
				{
					pai   = atual;
					atual = atual.getEsq();
				}
				X infoRaiz = r.getInfo();
				r.setInfo(atual.getInfo());
                if (pai==r)
                    pai.setDir(atual.getDir());
                else
				    pai.setEsq(atual.getDir());
				qtdDir--;
				this.guardeUmItem(infoRaiz);
				qtdEsq++;
			}
			
		this.balanceieSe(r.getEsq());
		this.balanceieSe(r.getDir());		
	}

	public void balanceieSe () throws Exception
	{
		this.balanceieSe(this.raiz);
	}

    // os 4 métodos abaixo imaginam que a árvore não é
    // uma árvore binária DE BUSCA (espelhar quebra a
    // ordem de busca)
    
    private boolean isEspelho (No r1, No r2)
    {
        if (r1==null && r2==null) return true;
        if (r1==null || r2==null) return false;
        if (!r1.getInfo().equals(r2.getInfo())) return false;

        if (!this.isEspelho(r1.getEsq(), r2.getDir())) return false;
        if (!this.isEspelho(r1.getDir(), r2.getEsq())) return false;
        return true;
    }    
    
    public boolean isEspelho (ArvoreBinariaDeBusca<X> arv)
    {
        if (arv==null) return false;
        return this.isEspelho(this.raiz, arv.raiz);
    }
    
    private void espelheSe (No r)
    {
        if (r==null) return;

        No aux = r.getEsq();
        r.setEsq(r.getDir());
        r.setDir(aux);

        this.espelheSe(r.getEsq());
        this.espelheSe(r.getDir());
    }
    
    public void espelheSe ()
    {
        this.espelheSe(this.raiz);
    }

    // percurso em ordem: esquerda, raiz, direita
    private String toString (No r)
    {
        if (r==null) return "";

        String esq = this.toString(r.getEsq()),
               dir = this.toString(r.getDir());

        String ret = esq;
        if (!ret.equals("")) ret += ", ";
        ret += r.getInfo();
        if (!dir.equals("")) ret += ", " + dir;
        return ret;
    }

    @Override
    public String toString ()
    {
        return "{" + this.toString(this.raiz) + "}";
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj==this) return true;
        if (obj==null) return false;
        if (obj.getClass()!=this.getClass()) return false;
        ArvoreBinariaDeBusca<X> arv = (ArvoreBinariaDeBusca<X>)obj;
        return equals(this.raiz,arv.raiz);
    }
    
    private boolean equals (No raizArv1, No raizArv2)
    {
        if (raizArv1==null && raizArv2==null) return true;
        if (raizArv1==null && raizArv2!=null) return false;
        if (raizArv1!=null && raizArv2==null) return false;
        if (!raizArv1.getInfo().equals(raizArv2.getInfo())) return false;
        /*
        return equals(raizArv1.getEsq(),raizArv2.getEsq()) &&
               equals(raizArv1.getDir(),raizArv2.getDir());
        */
        if (!equals(raizArv1.getEsq(),raizArv2.getEsq())) return false;
        if (!equals(raizArv1.getDir(),raizArv2.getDir())) return false;
        return true;
    }

    private int hashCode (No r)
    {
        if (r==null) return 0;

        int ret = 1;
        ret = ret * 7 + r.getInfo().hashCode();
        ret = ret * 7 + this.hashCode(r.getEsq());
        ret = ret * 7 + this.hashCode(r.getDir());
        return ret;
    }

    @Override
    public int hashCode ()
    {
        int ret = this.hashCode(this.raiz);
        if (ret<0) ret = -ret;
        return ret;
    }

    private No copia (No r)
    {
        if (r==null) return null;

        X info = r.getInfo();
        if (info instanceof Cloneable) info = this.meuCloneDeX(info);

        return new No (this.copia(r.getEsq()), info, this.copia(r.getDir()));
    }

    public ArvoreBinariaDeBusca (ArvoreBinariaDeBusca<X> modelo) throws Exception
    {
        if (modelo==null) throw new Exception ("Modelo ausente");

        this.raiz = this.copia(modelo.raiz);
    }

    @Override
    public Object clone ()
    {
        ArvoreBinariaDeBusca<X> ret = null;

        try
        {
            ret = new ArvoreBinariaDeBusca<X>(this);
        }
        catch (Exception erro)
        {}

        return ret;
    }
}