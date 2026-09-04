import java.util.*

val mapaPlateiaA = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_PLATEIA_A) } }
val mapaPlateiaB = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_PLATEIA_B) } }
val mapaFrisas = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(NUM_FRISAS) } }
val mapaCamarotes = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(NUM_CAMAROTES) } }
val mapaBalcao = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_BALCAO) } }

val listaClientes = mutableListOf<Cliente>()
val listaIngressos = mutableListOf<Ingresso>()

fun main() {
    val scanner = Scanner(System.`in`)
    var opcao: Int
    do {
        println("Bem-vindo ao Teatro ABC")
        println("1. Comprar Ingresso")
        println("2. Estatísticas")
        println("3. Imprimir Ingresso por CPF")
        println("0. Sair")
        print("Escolha uma opção: ")
        opcao = scanner.nextInt()

        when (opcao) {
            1 -> {
                comprarIngresso(scanner)
            }

            2 -> {

            }

            3 -> {
                imprimirIngresso()
            }

            0 -> println("Encerrando o sistema")
            else -> println("Opção inválida!")
        }
    } while (opcao != 0)
}

fun comprarIngresso(scanner: Scanner) {
    println("Para qual peça deseja comprar o ingresso?")
    println("1. Wicked\n2. Rei Leão\n3. O Auto da Compadecida\n0. Voltar")
    val pecaEscolhida: Int = scanner.nextInt()
    if (pecaEscolhida == 0) return
    if (pecaEscolhida !in 1..NUM_PECAS) {
        println("Peça inválida!")
        return
    }
    println("Para qual horário deseja comprar o ingresso?")
    println("1. Manhã\n2. Tarde\n3. Noite\n0. Escolher outra peça")
    print("Escolha: ")
    val sessaoEscolhida = scanner.nextInt()
    if (sessaoEscolhida == 0) return
    if (sessaoEscolhida !in 1..NUM_SESSOES) {
        println("Sessão inválida!")
        return
    }

    val pecaIndex = pecaEscolhida - 1
    val secaoIndex = sessaoEscolhida - 1

    menuAreas(scanner, pecaIndex, secaoIndex)
}

fun menuAreas(scanner: Scanner, pecaIndex: Int, secaoIndex: Int) {
    println("\n--- SELEÇÃO DE AREA DO TEATRO (${NOMES_PECAS[pecaIndex]} - ${NOMES_SESSOES[secaoIndex]}) ---")
    val areas = Area.entries.toTypedArray()

    for (i in areas.indices) {
        val s = areas[i]
        println("${i + 1}. ${s.nomeExibicao} (R$ ${String.format("%.2f", s.precoBase)})")
    }
    println("0. Cancelar")
    print("Escolha o setor: ")
    val setorEscolhido = scanner.nextInt()
    if (setorEscolhido !in 1..areas.size) {
        println("Opção inválida!")
        return
    }
    val area = areas[setorEscolhido - 1]

    val mapa = when (area) {
        Area.PLATEIA_A -> mapaPlateiaA[pecaIndex][secaoIndex]
        Area.PLATEIA_B -> mapaPlateiaB[pecaIndex][secaoIndex]
        Area.FRISA -> mapaFrisas[pecaIndex][secaoIndex]
        Area.CAMAROTE -> mapaCamarotes[pecaIndex][secaoIndex]
        Area.BALCAO_NOBRE -> mapaBalcao[pecaIndex][secaoIndex]
    }

    selecionarAssento(scanner, pecaIndex, secaoIndex, area, mapa)
}

fun selecionarAssento(scanner: Scanner, pecaIndex: Int, sessaoIndex: Int, area: Area, mapa: BooleanArray) {
    println("\nMapa de Assentos - ${area.nomeExibicao} ([ ]Livre, [ X ]Ocupado):")
    for (i in mapa.indices) {
        val status = if (mapa[i]) " X " else " "
        print(String.format("%s%02d:[%s]  ", area.prefixo, i + 1, status))
        if ((i + 1) % 10 == 0) println()
    }
    println()
    print("Digite o número do assento desejado (1 a ${area.capacidade}): ")
    val numero = scanner.nextInt()
    val assentoIdx = numero - 1

    if (assentoIdx !in mapa.indices) {
        println("Assento inválido.")
        return
    }

    if (mapa[assentoIdx]) {
        println("Assento já ocupado!")
        return
    }

    finalizarCheckout(scanner, pecaIndex, sessaoIndex, area, "${area.prefixo}$numero", mapa, assentoIdx)
}

fun finalizarCheckout(
    scanner: Scanner,
    pecaIndex: Int,
    sessaoIndex: Int,
    area: Area,
    codigoAssento: String,
    mapa: BooleanArray,
    assentoIdx: Int
) {
    scanner.nextLine()
    println("\n--- FINALIZAR COMPRA ---")
    print("Digite o CPF do titular: ")
    val cpf = scanner.nextLine().trim()

    if (!validarCPF(cpf.toLong())) {
        println("CPF inválido!")
        return
    }

    var clienteExistente = listaClientes.find { it.cpf == cpf }

    if (clienteExistente == null) {
        println("Cadastro não encontrado para este CPF.")
        print("Deseja realizar o cadastro completo? (1. Sim / 2. Não): ")
        val opcFidelidade = scanner.nextInt()
        scanner.nextLine()

        val novoCliente = Cliente(cpf = cpf)
        if (opcFidelidade == 1) {
            novoCliente.fidelidade = true
            print("Nome completo: ")
            novoCliente.nome = scanner.nextLine()
            print("Telefone: ")
            novoCliente.telefone = scanner.nextLine()
            print("Data de Nascimento (dd/mm/aaaa): ")
            novoCliente.dataNascimento = scanner.nextLine()
            print("Cidade: ")
            novoCliente.cidade = scanner.nextLine()
            print("Estado: ")
            novoCliente.estado = scanner.nextLine()
        }
        listaClientes.add(novoCliente)
        clienteExistente = novoCliente
    }
    println("\nForma de pagamento:")
    println("1. Cartão de Crédito\n2. Cartão de Débito\n3. Boleto\n4. PIX")
    print("Escolha a opção: ")
    val formaPagto = when (scanner.nextInt()) {
        1 -> "Crédito"
        2 -> "Débito"
        3 -> "Boleto"
        4 -> "PIX"
        else -> "Outro"
    }

    mapa[assentoIdx] = true

    val ingresso = Ingresso(
        pecaIndex = pecaIndex,
        sessaoIndex = sessaoIndex,
        area = area,
        codigoAssento = codigoAssento,
        preco = area.precoBase,
        cpfCliente = cpf,
        formaPagamento = formaPagto
    )
    listaIngressos.add(ingresso)

    println("\nCompra realizada com sucesso. Assento: $codigoAssento (${area.nomeExibicao})!")
}

fun imprimirIngresso() {

}