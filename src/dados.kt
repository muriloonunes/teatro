data class Cliente(
    var cpf: String = "",
    var nome: String = "",
    var telefone: String = "",
    var dataNascimento: String = "",
    var rua: String = "",
    var numero: String = "",
    var bairro: String = "",
    var cidade: String = "",
    var estado: String = "",
    var cep: String = "",
    var fidelidade: Boolean = false
)

data class Ingresso(
    var pecaIndex: Int = 0,
    var sessaoIndex: Int = 0,
    var area: Area = Area.PLATEIA_A,
    var codigoAssento: String = "",
    var preco: Double = 0.0,
    var cpfCliente: String = "",
    var formaPagamento: String = ""
)

enum class Area(
    val nomeExibicao: String,
    val prefixo: String,
    val precoBase: Double,
    val capacidade: Int
) {
    PLATEIA_A("Plateia A", "A", 120.0, 25),
    PLATEIA_B("Plateia B", "B", 80.0, 100),
    FRISA("Frisa", "F", 250.0, 6),
    CAMAROTE("Camarote", "C", 400.0, 5),
    BALCAO_NOBRE("Balcão Nobre", "N", 60.0, 50)
}

val NOMES_PECAS = arrayOf("Wicked", "O Rei Leão", "O Auto da Compadecida")
val NOMES_SESSOES = arrayOf("Manhã", "Tarde", "Noite")

const val NUM_PECAS = 3
const val NUM_SESSOES = 3

const val ASSENTOS_PLATEIA_A = 25
const val ASSENTOS_PLATEIA_B = 100
const val NUM_FRISAS = 6
const val NUM_CAMAROTES = 5
const val ASSENTOS_BALCAO = 50

const val PRECO_PLATEIA_A = 120.0
const val PRECO_PLATEIA_B = 80.0
const val PRECO_FRISA = 250.0
const val PRECO_CAMAROTE = 400.0
const val PRECO_BALCAO = 60.0