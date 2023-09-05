import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.ResultSet
import java.util.UUID

class PessoaService(private val connection: Connection) {

    fun create(pessoa: Pessoa): UUID {
        val sql = "INSERT INTO pessoa (apelido, nome, nascimento, stack) VALUES (?, ?, ?, ?) RETURNING id"
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, pessoa.apelido)
        preparedStatement.setString(2, pessoa.nome)
        preparedStatement.setDate(3, java.sql.Date.valueOf(pessoa.nascimento))
        preparedStatement.setArray(4, connection.createArrayOf("text", pessoa.stack?.toTypedArray()))
        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) UUID.fromString(resultSet.getString("id")) else throw Exception("Failed to create Pessoa")
    }

    fun read(id: String): Pessoa? {
        val sql = "SELECT * FROM pessoa WHERE id = ?"
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setObject(1, id)
        val resultSet = preparedStatement.executeQuery()
        return resultSetToPessoa(resultSet)
    }

    fun search(term: String): List<Pessoa> {
        val sql = "SELECT * FROM pessoa WHERE apelido LIKE ? OR nome LIKE ?"
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, "%$term%")
        preparedStatement.setString(2, "%$term%")
        val resultSet = preparedStatement.executeQuery()
        val pessoas = mutableListOf<Pessoa>()
        while (resultSet.next()) {
            pessoas.add(resultSetToPessoa(resultSet)!!)
        }
        return pessoas
    }

    fun count(): Int {
        val sql = "SELECT COUNT(*) FROM pessoa"
        val preparedStatement = connection.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) resultSet.getInt(1) else 0
    }

    private fun resultSetToPessoa(resultSet: ResultSet): Pessoa? {
        return if (resultSet.next()) {
            Pessoa(
                id = resultSet.getString("id"),
                apelido = resultSet.getString("apelido"),
                nome = resultSet.getString("nome"),
                nascimento = resultSet.getDate("nascimento").toString(),
                stack = resultSet.getArray("stack")?.array as? List<String>
            )
        } else {
            null
        }
    }
}

@Serializable
data class Pessoa(
    val id: String,
    val apelido: String,
    val nome: String,
    val nascimento: String,
    val stack: List<String>?
)