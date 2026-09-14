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
        println("\n=== Bem vindo ao Teatro ABC ===")
        println("1. Comprar Ingresso")
        println("2. Estatísticas")
        println("3. Imprimir Ingresso por CPF")
        println("0. Sair")
        opcao = lerInteiroEmIntervalo(scanner, "Escolha uma opção: ", 0, 3)

        when (opcao) {
            1 -> comprarIngresso(scanner)
            2 -> estatisticas()
            3 -> imprimirIngresso(scanner)
            0 -> println("Encerrando o sistema")
        }
    } while (opcao != 0)
}

fun comprarIngresso(scanner: Scanner) {
    println("\nPara qual peça deseja comprar o ingresso?")
    for (i in NOMES_PECAS.indices) {
        println("${i + 1}. ${NOMES_PECAS[i]}")
    }
    println("0. Voltar")
    val pecaEscolhida = lerInteiroEmIntervalo(scanner, "Escolha: ", 0, NUM_PECAS)
    if (pecaEscolhida == 0) return

    println("\nPara qual horário deseja comprar o ingresso?")
    for (i in NOMES_SESSOES.indices) {
        println("${i + 1}. ${NOMES_SESSOES[i]}")
    }
    println("0. Escolher outra peça")
    val sessaoEscolhida = lerInteiroEmIntervalo(scanner, "Escolha: ", 0, NUM_SESSOES)
    if (sessaoEscolhida == 0) return

    val pecaIndex = pecaEscolhida - 1
    val sessaoIndex = sessaoEscolhida - 1

    menuAreas(scanner, pecaIndex, sessaoIndex)
}

fun menuAreas(scanner: Scanner, pecaIndex: Int, sessaoIndex: Int) {
    println("\n--- SELEÇÃO DE ÁREA DO TEATRO (${NOMES_PECAS[pecaIndex]} - ${NOMES_SESSOES[sessaoIndex]}) ---")
    val areas = Area.entries.toTypedArray()

    for (i in areas.indices) {
        val a = areas[i]
        println("${i + 1}. ${a.nomeExibicao} (R$ ${"%.2f".format(a.precoBase)})")
    }
    println("0. Cancelar")
    val setorEscolhido = lerInteiroEmIntervalo(scanner, "Escolha o setor: ", 0, areas.size)
    if (setorEscolhido == 0) return

    val area = areas[setorEscolhido - 1]

    val mapa = when (area) {
        Area.PLATEIA_A -> mapaPlateiaA[pecaIndex][sessaoIndex]
        Area.PLATEIA_B -> mapaPlateiaB[pecaIndex][sessaoIndex]
        Area.FRISA -> mapaFrisas[pecaIndex][sessaoIndex]
        Area.CAMAROTE -> mapaCamarotes[pecaIndex][sessaoIndex]
        Area.BALCAO_NOBRE -> mapaBalcao[pecaIndex][sessaoIndex]
    }

    selecionarAssento(scanner, pecaIndex, sessaoIndex, area, mapa)
}

fun selecionarAssento(scanner: Scanner, pecaIndex: Int, sessaoIndex: Int, area: Area, mapa: BooleanArray) {
    println("\nMapa de Assentos - ${area.nomeExibicao} ([ ]Livre, [X]Ocupado):")
    for (i in mapa.indices) {
        val status = if (mapa[i]) "X" else " "
        print(String.format("%s%02d:[%s]  ", area.prefixo, i + 1, status))
        if ((i + 1) % 10 == 0) println()
    }
    println()

    if (mapa.all { it }) {
        println("Todos os assentos desta área estão ocupados para esta sessão.")
        return
    }

    val numero = lerInteiroEmIntervalo(
        scanner,
        "Digite o número do assento desejado (1 a ${area.capacidade}, 0 para cancelar): ",
        0,
        area.capacidade
    )
    if (numero == 0) return

    val assentoIdx = numero - 1

    if (mapa[assentoIdx]) {
        println("Assento já ocupado! Escolha outro.")
        selecionarAssento(scanner, pecaIndex, sessaoIndex, area, mapa)
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
    println("\n--- FINALIZAR COMPRA ---")
    val cpf = lerCPF(scanner) ?: run {
        println("Compra cancelada.")
        return
    }

    var clienteExistente = listaClientes.find { it.cpf == cpf }

    if (clienteExistente == null) {
        println("Cadastro não encontrado para este CPF.")
        val opcFidelidade = lerInteiroEmIntervalo(scanner, "Deseja realizar o cadastro completo? (1. Sim / 2. Não): ", 1, 2)

        val novoCliente = Cliente(cpf = cpf)
        if (opcFidelidade == 1) {
            novoCliente.fidelidade = true
            novoCliente.nome = lerTextoNaoVazio(scanner, "Nome completo: ")
            novoCliente.telefone = lerTextoNaoVazio(scanner, "Telefone: ")
            novoCliente.dataNascimento = lerDataNascimento(scanner)
            novoCliente.cidade = lerTexto(scanner, "Cidade: ")
            novoCliente.estado = lerTexto(scanner, "Estado: ")
        }
        listaClientes.add(novoCliente)
        clienteExistente = novoCliente
    }

    println("\nForma de pagamento:")
    println("1. Cartão de Crédito\n2. Cartão de Débito\n3. Boleto\n4. PIX")
    val opcaoPagamento = lerInteiroEmIntervalo(scanner, "Escolha a opção: ", 1, 4)
    val formaPagto = when (opcaoPagamento) {
        1 -> "Crédito"
        2 -> "Débito"
        3 -> "Boleto"
        else -> "PIX"
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

fun estatisticas() {
    if (listaIngressos.isEmpty()) {
        println("\nNenhuma venda registrada ainda.")
        return
    }

    val vendasPorPeca = IntArray(NUM_PECAS)
    val lucroPorPeca = DoubleArray(NUM_PECAS)
    val vendasPorSessao = IntArray(NUM_SESSOES)
    val lucroPorPecaSessao = Array(NUM_PECAS) { DoubleArray(NUM_SESSOES) }
    val vendasPorArea = mutableMapOf<Area, Int>()

    for (ingresso in listaIngressos) {
        vendasPorPeca[ingresso.pecaIndex]++
        lucroPorPeca[ingresso.pecaIndex] += ingresso.preco
        vendasPorSessao[ingresso.sessaoIndex]++
        lucroPorPecaSessao[ingresso.pecaIndex][ingresso.sessaoIndex] += ingresso.preco
        vendasPorArea[ingresso.area] = (vendasPorArea[ingresso.area] ?: 0) + 1
    }

    val pecaMaisVendida = indiceMaiorValor(vendasPorPeca)
    val pecaMenosVendida = indiceMenorValor(vendasPorPeca)
    val sessaoMaisOcupada = indiceMaiorValor(vendasPorSessao)
    val sessaoMenosOcupada = indiceMenorValor(vendasPorSessao)

    println("\n--- ESTATÍSTICAS DE VENDAS ---")
    println("Total de ingressos vendidos: ${listaIngressos.size}")
    println("Peça mais vendida: ${NOMES_PECAS[pecaMaisVendida]} (${vendasPorPeca[pecaMaisVendida]} ingressos)")
    println("Peça menos vendida: ${NOMES_PECAS[pecaMenosVendida]} (${vendasPorPeca[pecaMenosVendida]} ingressos)")
    println("Sessão mais ocupada: ${NOMES_SESSOES[sessaoMaisOcupada]}")
    println("Sessão menos ocupada: ${NOMES_SESSOES[sessaoMenosOcupada]}")

    println("\nDetalhamento por peça:")
    for (i in 0 until NUM_PECAS) {
        val lucroMedio = if (vendasPorPeca[i] != 0) lucroPorPeca[i] / vendasPorPeca[i] else 0.0
        val sessaoMaisLucrativa = indiceMaiorValor(lucroPorPecaSessao[i])
        val sessaoMenosLucrativa = indiceMenorValor(lucroPorPecaSessao[i])
        println("- ${NOMES_PECAS[i]}: ${vendasPorPeca[i]} vendidos, faturamento R$ ${"%.2f".format(lucroPorPeca[i])}, ticket médio R$ ${"%.2f".format(lucroMedio)}")
        println("  Sessão mais lucrativa: ${NOMES_SESSOES[sessaoMaisLucrativa]} | menos lucrativa: ${NOMES_SESSOES[sessaoMenosLucrativa]}")
    }

    println("\nVendas por área:")
    for (area in Area.entries) {
        println("- ${area.nomeExibicao}: ${vendasPorArea[area] ?: 0} ingressos")
    }
}

fun indiceMaiorValor(valores: IntArray): Int {
    var maior = 0
    for (i in 1 until valores.size) if (valores[i] > valores[maior]) maior = i
    return maior
}

fun indiceMenorValor(valores: IntArray): Int {
    var menor = 0
    for (i in 1 until valores.size) if (valores[i] < valores[menor]) menor = i
    return menor
}

fun indiceMaiorValor(valores: DoubleArray): Int {
    var maior = 0
    for (i in 1 until valores.size) if (valores[i] > valores[maior]) maior = i
    return maior
}

fun indiceMenorValor(valores: DoubleArray): Int {
    var menor = 0
    for (i in 1 until valores.size) if (valores[i] < valores[menor]) menor = i
    return menor
}
fun imprimirIngresso(scanner: Scanner) {
    println("\n--- IMPRIMIR INGRESSO ---")
    val cpf = lerCPF(scanner) ?: run {
        println("Operação cancelada.")
        return
    }

    val ingressosDoCliente = listaIngressos.filter { it.cpfCliente == cpf }
    if (ingressosDoCliente.isEmpty()) {
        println("Nenhum ingresso encontrado para o CPF informado.")
        return
    }

    val cliente = listaClientes.find { it.cpf == cpf }
    val nomeCliente = cliente?.nome?.takeIf { it.isNotBlank() }

    println(if (nomeCliente != null) "\nIngressos de $nomeCliente:" else "\nIngressos encontrados:")
    for (ingresso in ingressosDoCliente) {
        println("----------------------------------")
        println("Peça: ${NOMES_PECAS[ingresso.pecaIndex]}")
        println("Sessão: ${NOMES_SESSOES[ingresso.sessaoIndex]}")
        println("Área: ${ingresso.area.nomeExibicao}")
        println("Assento: ${ingresso.codigoAssento}")
        println("Preço: R$ ${"%.2f".format(ingresso.preco)}")
        println("Forma de pagamento: ${ingresso.formaPagamento}")
    }
    println("----------------------------------")
}