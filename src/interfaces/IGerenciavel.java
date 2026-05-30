package interfaces;

import java.util.List;

/**
 * Interface genérica que define o contrato padrão de operações CRUD
 * para todas as entidades gerenciadas pelo sistema.
 *
 * @param <T> Tipo da entidade gerenciada
 */
public interface IGerenciavel<T> {

    /**
     * Salva (insere) um novo registro no banco de dados.
     * @param obj objeto a ser salvo
     * @return true se salvou com sucesso, false caso contrário
     */
    boolean salvar(T obj);

    /**
     * Atualiza um registro existente no banco de dados.
     * @param obj objeto com os dados atualizados
     * @return true se atualizou com sucesso, false caso contrário
     */
    boolean atualizar(T obj);

    /**
     * Remove um registro do banco de dados pelo seu ID.
     * @param id identificador do registro a ser removido
     * @return true se removeu com sucesso, false caso contrário
     */
    boolean deletar(int id);

    /**
     * Busca um registro pelo seu ID.
     * @param id identificador do registro
     * @return objeto encontrado ou null se não existir
     */
    T buscarPorId(int id);

    /**
     * Retorna todos os registros da entidade.
     * @return lista com todos os registros
     */
    List<T> listarTodos();
}
